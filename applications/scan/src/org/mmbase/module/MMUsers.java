/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.builders.Properties;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @application SCAN
 * @author Arjan Houtman
 * @author Daniel Ockeloen
 * @version $Id: MMUsers.java,v 1.17 2008-12-22 18:52:37 michiel Exp $
 */
public class MMUsers extends ProcessorModule {

    private static Logger log = Logging.getLoggerInstance(MMUsers.class.getName());

    private PasswordGenerator pwgen = new PasswordGenerator ();
    private MMBase mmbase;
    private sessionsInterface sessions;
    private SendMailInterface sendMail;

    private MMObjectBuilder users; // Users
    private Properties properties;

    //Strings for composing an email
    private String emailFrom;
    private String emailReturnPath;
    private String emailSubject;
    private String emailBodyBegin;
    private String emailBodyLogin;
    private String emailBodyPasswd;
    private String emailBodyEnd;

    private String error (String msg) {
        log.error(msg);
        return "Error-> " + msg;
    }

    private String cleanupString(String str) { return str; }

    // Keys not cleared by clearSessionInfo. Memo should start and end with ;
    private static String excludedKeys = ";LID;FIRSTNAME;LASTNAME;EMAIL;";

    private void clearSessionInfo (scanpage sp) {
        if (sessions==null) sessions = (sessionsInterface)getModule("SESSION");

        String sid = sp.getSessionName();
        sessionInfo info = sessions.getSession (sp, sp.sname);
        String userid = sessions.getValue (info, "USERNUMBER");

        if (log.isDebugEnabled()) {
            log.debug("Clearing session-info of user " + userid);
        }
        if (userid!=null) {
            Map<String, MMObjectNode> has = getUserProperties (userid);
            if (has != null) {
                for (MMObjectNode node : has.values()) {
                    String key = node.getStringValue ("key");
                    if ((key != null) && (excludedKeys.indexOf(";"+key+";")<0)) {
                        if (log.isDebugEnabled()) {
                            log.debug ("Clearing key '" + key + "'");
                        }
                        sessions.setValue (info, key, "");
                    }
                }
            }
            sessions.forgetSession( userid );
        }
        deleteSessionSIDs( sid );
    }

/*
    Het creeren van een nieuwe login gaat momenteel nog door het opslaan van de nieuwe gegevens
    (LID, EMAIL, FIRSTNAME, LASTNAME) in sessie-variabelen en vervolgens $MOD-MMUSERS-CREATELOGIN-...
    aan te roepen. Dit gaat waarschijnlijk niet goed, omdat de 'nieuwe' sessie-variabelen tussen de
    sessie-variabelen van de 'oude' gebruiker komen te staan en deze dus niet meer uit elkaar te houden
    zijn. Nu kan je de nieuwe variabelen wel met een bepaald keywoord beginnen (NEWNAME, NEWEMAIL
    etc), maar ook dan kunnen er problemen ontstaan (je moet dit dan hard incoderen, wat waarschijnlijk
    niet gewenst is). Het creeren van de nieuwe login kan daarom het beste gebeuren via PRC-CMD-... in
    forms. Op deze manier krijg je de nieuwe gegevens binnen en kan je eerst de sessie-gegevens van
    de oude gebruiker
    verwijderen alvorens je de nieuwe gegevens invoert.
    */
    public String createLogin (String loginname, StringTokenizer tokens, scanpage sp)
    {
        String address = getRemainingTokens( tokens );
        if (address.equals(""))
            return error ("CREATELOGIN-" + loginname + " needs an e-mail address");

        // Cleanup name/adress
        loginname=cleanupString(loginname);
        address=cleanupString(address);

        String rtn="",res;

        // Check if email/account already exists
        res=doSearchUserNumber(loginname);
        if (res.length()==0) {
            res=doSearchUserNumber(address);
            if (res.length()==0) {

                // Now first delete any existing session-info, 'cause it is from the
                // 'old' user under this SID. This is tricky right now, because the 'new'
                // login info is also stored in the same session-info. Deleteing all the
                // session-info will delete the new info too.
                // WH: see clearSessionInfo for comment
                clearSessionInfo (sp);

                String password = pwgen.getPassword ();
                int userid = createNewUser (sp, loginname, password, address);

                if (userid < 0) log.error("Couldn't create a new user!");
                else sendConfirmationEMail (address, loginname, password);
                rtn = rtn+userid;
            } else {
                rtn="EMAIL";
                if (log.isDebugEnabled()) {
                    log.debug("Email address is already in use");
                }
            }
        } else {
            rtn="USER";
            if (log.isDebugEnabled()) {
                log.debug("Username is already in use");
            }
        }
        return rtn;
    }

    private int createNewUser (scanpage sp, String ln, String pw, String ad) {
        if (users == null) users = mmbase.getMMObject ("users");
        if (sessions==null) sessions = (sessionsInterface)getModule("SESSION");
        String sid = sp.getSessionName();
        // Create a new user-node...
        MMObjectNode usernode = users.getNewNode ("system");
        if (usernode == null) return -1;
        usernode.setValue ("description", "created for SID = " + sid);
        int id = users.insert ("system", usernode);
        if (log.isServiceEnabled()) log.service("Created new user " + id + ", SID=" + sid);

        // sessionInfo info = sessions.getSession (sp.req, sid);
        sessionInfo info = sessions.getSession (sp, sp.sname);
        sessions.setValue (info, "USERNUMBER", "" + id);
        sessions.setValue (info, "PASSWD", pw);
        sessions.setValue (info, "LID", ln);

        storeValue ("" + id, "SID", sid);
        storeValue ("" + id, "PASSWD", pw);
        storeValue ("" + id, "LID", ln);

        return id;
    }

    private boolean deleteSIDs(String usernumber) {
        if (properties == null) properties = (Properties)mmbase.getMMObject("properties");
        Enumeration e = properties.search("parent=E"+usernumber+" + key=='SID'");
        while (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode) e.nextElement();
            //properties.removeRelations(node);// Beetje overdreven, alleen in theorie kan ie rels hebbe
            properties.removeNode(node);
        }
        return true;
    }

    private boolean deleteSessionSIDs(String sessionname) {
        if (properties == null) properties = (Properties)mmbase.getMMObject("properties");
        Enumeration e = properties.search("key=='SID'+ value=='"+sessionname+"'");
        while (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            //properties.removeRelations(node);// Beetje overdreven, alleen in theorie kan ie rels hebbe
            properties.removeNode(node);
        }
        return true;
    }

    public String doSearchUserNumber(String value) {
        if (properties == null) properties = (Properties)mmbase.getMMObject("properties");
        Enumeration e = properties.search("key=='LID' + value=='"+value+"'");
        if (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode) e.nextElement();
            return node.getStringValue("parent");
        }

        e = properties.search("key=='EMAIL' + value=='"+value+"'");
        if (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode) e.nextElement();
            return node.getStringValue("parent");
        }
        return "";


    }

    private String getRemainingTokens( StringTokenizer tok )
    {
        if (!tok.hasMoreTokens())
            return "";
        return addRemainingTokens( tok.nextToken(), tok);
    }

    private String addRemainingTokens( String s, StringTokenizer tok )
    {
        while (tok.hasMoreTokens()) { s += "-"+tok.nextToken(); }
        return s;
    }


    public void init () {
        super.init ();
        mmbase = (MMBase)getModule ("MMBASEROOT");

    }

    /*
     * Store a property in the database.
     */
    private String storeValue (String userid, String key, String value) {
        if (users == null) users = mmbase.getMMObject("users");
        if (properties == null) properties = (Properties)mmbase.getMMObject("properties");

        MMObjectNode usernode = users.getNode (userid);

        if (usernode != null) {
            MMObjectNode pnode = usernode.getProperty (key);

            if (pnode == null) {
                pnode = properties.getNewNode ("system");
                pnode.setValue ("parent", Integer.parseInt (userid));
                pnode.setValue ("key", key);
                pnode.setValue ("value", value);
                pnode.setValue ("ptype", "string");
                properties.insert ("system", pnode);
                usernode.delPropertiesCache();
                if (log.isDebugEnabled()) {
                    log.debug ("storeValue() storing '" + value + "' in '" + key + "' for user "+userid);
                }

            }
            else
            {
                pnode.setValue ("value", value);
                pnode.setValue ("ptype", "string");
                pnode.commit();
                if (log.isDebugEnabled()) {
                    log.debug ("storeValue() overwriting '" + value + "' in '" + key + "' for user "+userid);
                }
            }
        }
        else {
            return error ("MOD-MMUSERS-" + userid + "-PUT has invalid user-id");
        }
        return "";
    }


    /**
     * <LIST GETPROPERTY PARENT=[value] FIELD=[values] PROCESSOR="MMUsers">
     * </LIST>
     */
    @Override public List<String> getList(PageInfo sp, StringTagger tagger, String value) {
        StringTokenizer tokens = new StringTokenizer (value, "-\n\r");
        String          tok    = tokens.nextToken ();
        Vector          res    = new Vector ();

        if (tok.equals ("GETPROPERTY") && tokens.hasMoreTokens ()) {
            tok = tokens.nextToken ();

            if (tok.equals ("PARENT") && tokens.hasMoreTokens ()) {
                String    userid = tokens.nextToken ();
                Map<String, MMObjectNode> props  = getUserProperties (userid);
                Vector    fields = tagger.Values ("FIELD");
                String    wanted = (String)fields.firstElement ();

                res = wanted.equals ("*")
                    ? getListAll (props, tagger)
                    : getListSelection (props, fields, tagger);
            }
            else {
                log.warn("MMUsers can't get property '" + tok + "'");
            }
        }
        else {
            log.warn("MMUsers can't list '" + value + "'");
        }

        return res;
    }

    private Vector getListAll (Map<String, MMObjectNode> props, StringTagger tagger) {
        Vector v = new Vector ();

        // Go through all properties...
        for (MMObjectNode n : props.values()) {
            String       key  = n.getStringValue ("key");
            String       type = n.getStringValue ("ptype");

            v.addElement (key);

            if (type.equals ("string")) v.addElement (n.getStringValue ("value"));
            // Place for more types...
            else v.addElement ("<NON-PRINTABLE>");
        }

        tagger.setValue ("ITEMS", "2"); // Both key and value

        return v;
    }

    private Vector getListSelection (Map<String, MMObjectNode> props, Vector fields, StringTagger tagger)
    {
        Vector v = new Vector ();

        // Go through all wanted-fields, and get the right property from the hashtable...
        for (Enumeration field = fields.elements (); field.hasMoreElements (); ) {
            String       f    = (String)field.nextElement ();
            MMObjectNode node = (MMObjectNode)props.get (f);

            if (node != null) {
                String type = node.getStringValue ("ptype");

                if (type.equals ("string")) v.addElement (node.getStringValue("value"));
                // Place for more types...
                else v.addElement ("<NON-PRINTABLE>");
            }
        }

        tagger.setValue ("ITEMS", "" + fields.size ());

        return v;
    }

    /**
     *	Handle a $MOD command
     */
    @Override public String replace (PageInfo sp, String command) {
        StringTokenizer tokens = new StringTokenizer (command, "-\n\r");

        if (tokens.hasMoreTokens ()) {
            String param1 = tokens.nextToken ();

            if (tokens.hasMoreTokens ()) {
                String param2 = tokens.nextToken ();
                if      (param2.equals ("GET")) return replaceGet(param1, tokens);
                else if (param2.equals ("PUT")) return replacePut(param1, tokens);
                else if (param1.equals ("SWITCH")) return replaceSwitch((scanpage) sp, param2);
                else if (param1.equals ("SEARCHUSERNUMBER"))
                    return doSearchUserNumber(addRemainingTokens(param2, tokens));
                else if (param1.equals ("CREATELOGIN")) return createLogin (param2, tokens, (scanpage) sp);

                // Everything else is an error:
                else return error ("MMUSERS-" + param1 + " has illegal parameter " + param2 + " following");
            }
            else return error ("MMUSERS-" + param1 + " missing parameter");
        }
        else return error ("MMUSERS missing parameter");
    }

    private void sendConfirmationEMail (String address, String loginname, String password)
    {

        // get email config and check it
        emailFrom = getInitParameter("from");
        if (emailFrom == null || emailFrom.equals(""))
            log.debug ("missing init param from");
        if (emailFrom.equals("youremail@yourcompany.nl"))
            log.debug ("from init parameter is still default, please change!!!!");
        emailReturnPath = getInitParameter("returnpath");
        if (emailReturnPath == null || emailReturnPath.equals(""))
            log.debug (" missing init param returnpath");
        if (emailReturnPath.equals("youremail@yourcompany.nl"))
            log.debug (" returnpath init parameter is still default, please change!!!!");
        emailSubject = getInitParameter("subject");
        if (emailSubject == null || emailSubject.equals(""))
            log.debug ("missing init param subject");
        emailBodyBegin = getInitParameter("bodybegin");
        if (emailBodyBegin == null || emailBodyBegin.equals(""))
            log.debug ("missing init param bodybegin");
        emailBodyLogin = getInitParameter("bodylogin");
        if (emailBodyLogin == null || emailBodyBegin.equals(""))
            log.debug ("missing init param bodylogin");
        emailBodyPasswd = getInitParameter("bodypasswd");
        if (emailBodyPasswd == null || emailBodyPasswd.equals(""))
            log.debug ("missing init param bodypasswd");
        emailBodyEnd = getInitParameter("bodyend");
        if (emailBodyEnd == null || emailBodyEnd.equals(""))
            log.debug ("missing init param bodyend");

        if (log.isServiceEnabled()) {
            log.service("Sending e-mail to " + address + " concerning login '" + loginname + "' and password '" + password + "'");
        }
        if (sendMail == null) sendMail = (SendMailInterface)getModule("sendmail");
        if (sendMail == null) {
            log.error("MMUsers ERROR CANNOT GET MODULE SendMail!");
            log.error("MMUsers could not send mail to " + address + " for login name " + loginname
                               +" with password "+ password);
            return;
        }

        Hashtable headers = new Hashtable( 3 );

        headers.put("From", emailFrom);
        headers.put("Return-path", emailReturnPath);
        headers.put("Subject", emailSubject);
        String body = emailBodyBegin + "\n\n" + emailBodyLogin + loginname + "\n" + emailBodyPasswd + password + "\n\n" + emailBodyEnd;
        sendMail.sendMail(emailFrom, address, body, headers);


        }

    // new version from daniel, now uses the getproperties in each node
    // and uses its cache (general mmobjectnode cache).

    private Map<String, MMObjectNode> getUserProperties (String userid) {
        // need a builder to obtain the usernode and its properties
        if (users == null) users = mmbase.getMMObject ("users");

        // obtain the correct node so we can get its hashtable of properties nodes
        MMObjectNode usernode = users.getNode(userid);

        if (usernode!=null) {
            Map<String, MMObjectNode> properties = usernode.getProperties();
            return properties;
        } else {
            log.warn("MMUsers -> getUserProperties not a valid user = " + userid);
            return null;
        }
    }

    private String replaceGet (String userid, StringTokenizer tokens) {
        String res = "";

        if (!tokens.hasMoreTokens())
            return error("MOD-MMUSERS-" + userid + "-GET missing KEY param");
        String key = tokens.nextToken();
        // obtain the users builder
        if (users==null) users = mmbase.getMMObject ("users");
        // obtain the user node
        MMObjectNode usernode=users.getNode(userid);
        if (usernode == null)
            return error("MOD-MMUSERS-" + userid + "-GET: usernode not found");

        usernode.getProperties();
        // we have the user node, get the properties node for this key
        MMObjectNode pnode=usernode.getProperty(key);
        if (pnode == null)
        {
            if (log.isDebugEnabled()) {
                log.debug( "MOD-MMUSERS-" + userid + "-GET-"+key+": key not found");
            }
            return "";
        }
        String type = pnode.getStringValue ("ptype");
        String value = pnode.getStringValue ("value");
        if (type.equals ("string")) res = value; else res = "<NON-PRINTABLE>";
        if (log.isDebugEnabled()) {
            log.debug( "" + userid + " " + res);
        }
        return res;
    }

    private String replacePut (String userid, StringTokenizer tokens) {
        if (!tokens.hasMoreTokens ())
            return error ("MOD-MMUSERS-" + userid + "PUT/ADD missing KEY");

        String key   = tokens.nextToken ();
        String value = getRemainingTokens( tokens );
        return storeValue (userid, key, value);
    }

    /*
        Zoals alles nu geimplementeerd is, moet er voorzichtig worden omgegaan
        met het verwijderen van sessie-info. Dit omdat bij het creeren van een
        nieuwe login de nieuwe gegevens in dezelfde sessie-info worden gezet.
        WH: Added excludedKeys in method clearSessionInfo. Assumptions	is that
        you will set them properly before creating or switching a login
    */
    private String replaceSwitch(scanpage sp, String newUserID) {
        if (sessions==null) sessions = (sessionsInterface)getModule("SESSION");

        // $MOD-MMUSERS-SWITCH-1211212 (number is user to switch too)
        String sid = sp.getSessionName();
        // sessionInfo info = sessions.getSession (sp.req, sid);
        sessionInfo info = sessions.getSession (sp,sp.sname);
        String oldUserID = sessions.getValue (info, "USERNUMBER");

        if (log.isDebugEnabled()) {
            log.debug("Switch Info from " + oldUserID +" to "+newUserID);
        }

        if (!newUserID.equals(oldUserID))
            clearSessionInfo( sp );

        // remove all the linked SID's from the new usernode
        deleteSIDs( newUserID );

        // link the current SID to this user
        storeValue( newUserID, "SID", sid );
        // oke now lets set the new values
        sessions.setValue(info,"USERNUMBER",newUserID);

        Map<String, MMObjectNode> has2=getUserProperties(newUserID);
        if (has2 == null) {
            log.warn("SWITCH: newuser " + newUserID + " has no properties!");
            return "";
        }
        for (MMObjectNode propNode : has2.values()) {
            sessions.setValueFromNode( info, propNode);
        }
        return "";
    }

    /**
     */
    @Override public String getModuleInfo () {
        return "Support routines for MMUsers, by Daniel Ockeloen & Arjan Houtman";
    }

    public void reload () {
        mmbase   = (MMBase)getModule ("MMBASEROOT");
    }

}

