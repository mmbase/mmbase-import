/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * CLEARSET-NAME : This command clears the session variable called NAME
 * ADDSET-NAME-VALUE : This command adds VALUE to the SESSION variable set called NAME, no duplicates are allowed
 * PUTSET-NAME-VALUE : This command adds VALUE to the SESSION variable set called NAME, duplicates are allowed
 * DELSET-NAME-VALUE : This command deletes VALUE form the SESSION variable set called NAME.
 * CONTAINSSET-NAME-VALUE : returns "YES" if the session variable NAME contains the VALUE, otherwise returns "NO"
 * SETSTRING-NAME : This command gives all values of the session variable NAME, comma separated.
 * SETCOUNT-NAME : This command gives the number of values contained by the session variable NAME.
 * AVGSET-NAME : This command returns the average of a set numbers.
 * CLEARSESSIONINFO : This command clears the SessionInfo.
 *
 * @application SCAN
 * @rename Sessions
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class sessions extends ProcessorModule implements sessionsInterface {

    private static Logger log = Logging.getLoggerInstance(sessions.class.getName());

    Hashtable<String, sessionInfo> sessions = new Hashtable<String, sessionInfo>();
    private MMBase mmbase;
    MMObjectBuilder props,users;

    /**
     * sessions Module constructor.
     */
    public sessions() {
    }

    public void init() {
        mmbase=MMBase.getMMBase();
    }

    public sessionInfo getSession(scanpage sp,String wanted) {
        if (log.isDebugEnabled()) {
            log.debug("getSession(): wanted=" + wanted);
        }
        if (wanted!=null) {
            sessionInfo session=sessions.get(wanted);
            if (session==null) {
                if (sp.req!=null) {
                    session=new sessionInfo(sp.req.getRemoteHost(),wanted);
                } else {
                    session=new sessionInfo("<unknown>",wanted);
                }
                sessions.put(wanted,session);
                // get all the propertie values of this node
                if (mmbase!=null) {
                    try {
                        loadProperties(session);
                    } catch(Exception r) {}
                }
            } else {
                session.setValue("COOKIETEST","YES");
            }
            return session;
        }
        return null;
    }

    public void forgetSession(String wanted) {
        if(wanted!=null) {
            if (sessions.containsKey(wanted)) {
                sessions.remove(wanted);
                log.info("forgetSession(" + wanted + "): Who? Don't know 'm .. sorry!");
            } else log.warn("forgetSession(" + wanted + "): This key not found in session!");
        } else log.error("forgetSession(" + wanted + "): wanted to forget a null!");
    }

    public String getValue(sessionInfo session,String wanted) {

        if (session==null) {
            log.error("getValue("+wanted+"): session is null!");
            return null;
        }

        if(wanted.indexOf("-xmlescape")!=-1) {
            wanted = wanted.substring(0,wanted.length()-10);
            String ret = session.getValue(wanted);
            return xmlEscape(ret);
        } else {
            return session.getValue(wanted);
        }
    }

    /**
     * Sets or changes a parameter in a sessionInfo.
     *
     * @param session  the sessionInfo wich has to contain
     *                 the parameter.
     * @param key      the name of the parameter to be set.
     * @param value    the value to wich the parameter should be set.
     */
    public String setValue(sessionInfo session,String key,String value) {
        if (session!=null) {
            return session.setValue(key,value);
        } else {
            log.error("setValue("+key+","+value+"): session is null!");
            return null;
        }
    }

    /**
     * Adds a number of Strings to a set.
     *
     * @param session  the sessionInfo containing the set.
     * @param key      the name of the set.
     * @param values   a Vector containing the
     *                 Strings to be added to the set.
     */
    public void addSetValues(sessionInfo session,String key,Vector<Object> values) {
        if (session!=null) {
            String str;
            for (Object object : values) {
                str=(String)object;
                session.addSetValue(key,str);
            }
        } else {
            log.error("addSetValues("+key+","+values+"): session is null!");
        }
    }

    /**
     * Adds a String to a set. If the String is
     * already contained by the set nothing happens.
     *
     * @param session  the sessionInfo containing the set.
     * @param key      the name of the set.
     * @param value    the String to be added to the set.
     */
    public void addSetValue(sessionInfo session,String key,String value) {
        if (session!=null) {
            session.addSetValue(key,value);
        } else {
            log.error("addSetValue("+key+","+value+"): session is null!");
        }
    }

    public void setValueFromNode( sessionInfo session, MMObjectNode node ) {
        String key = node.getStringValue("key");
        String ptype = node.getStringValue("ptype");
        String value = node.getStringValue("value");
        if (ptype.equals("string")) {
            setValue(session, key, value);
        } else if (ptype.equals("vector")) {
            StringTokenizer tok = new StringTokenizer(value,",\n\r");
            while (tok.hasMoreTokens()) {
                addSetValue( session, key, tok.nextToken());
            }
        } else {
            log.error("setValueFromNode(" + key + "," + ptype + "," +
                      value + "): ptype("+ptype+") neither 'string' nor 'vector'!");
        }
    }

    public void loadProperties(sessionInfo session) {
        // daniel, added a check to switch between old and new users
        // system. the new one uses a cookies builder so i use
        // that as a check.
        if (mmbase!=null) {
            MMObjectBuilder bul=mmbase.getMMObject("cookies");
            if (bul!=null) {
                loadNewProperties(session);
                return;
            }
        }
        try {
            String sid;
            if (session!=null) {
                sid=session.getCookie();
                if (mmbase!=null) {
                    props=mmbase.getMMObject("properties");
                    // MOET ANDERS
                    Enumeration res=props.search("WHERE "+mmbase.getStorageManagerFactory().getStorageIdentifier("key")+"='SID' AND "+mmbase.getStorageManagerFactory().getStorageIdentifier("value")+"='"+sid+"'");
                    if (log.isDebugEnabled()) {
                        log.debug("loadProperties(): got SID(" + sid + ")");
                    }
                    if (res.hasMoreElements()) {
                        MMObjectNode snode = (MMObjectNode)res.nextElement();
                        int id=snode.getIntValue("parent");
                        setValue(session,"USERNUMBER",""+id);
                        res=props.search("parent=="+id);
                        while (res.hasMoreElements()) {
                            setValueFromNode( session, (MMObjectNode)res.nextElement() );
                        }
                    }
                } else log.error("loadProperties(): mmbase is null!");
            } else log.error("loadProperties(): session is null!");
        } catch (Exception e) {
            //  log.error(Logging.stackTrace(e));
        }
    }

    public void loadNewProperties(sessionInfo session) {
        // new system uses cookies/users builder to find
        // the correct SID
        try {
            String sid;
            if (session!=null) {
                if (mmbase!=null) {
                    sid=session.getCookie();
                    Users users=(Users)mmbase.getMMObject("users");
                    int id=users.getNumber(sid);
                    if (id==-1) {
                        Cookies cookies=(Cookies)mmbase.getMMObject("cookies");
                        id=cookies.getNumber(sid);
                    }
                    if (id!=-1) {
                        props=mmbase.getMMObject("properties");
                        System.out.println("NEW SID="+id);
                        Enumeration res=props.search("parent=="+id);
                        while (res.hasMoreElements()) {
                            setValueFromNode( session, (MMObjectNode)res.nextElement() );
                        }
                    }
                } else log.error("loadProperties(): mmbase is null!");
            } else log.error("loadProperties(): session is null!");
        } catch (Exception e) {
            //  log.error(Logging.stackTrace(e));
        }
    }

    public String saveValue(sessionInfo session,String key) {
        // daniel, added a check to switch between old and new users
        // system. the new one uses a cookies builder so i use
        // that as a check.
        if (mmbase!=null) {
            MMObjectBuilder bul=mmbase.getMMObject("cookies");
            if (bul!=null) {
                return saveValueNew(session,key);
            }
        }
        if (mmbase==null) {
            log.error("saveValue(" + session + "," + key + "): mmbase is null!");
            return null;
        }
        if (session!=null) {
            if( key != null ) {
                int id=-1;
                String sid=session.getCookie();
                MMObjectNode node=session.getNode();

                if (node==null) {
                    // node not found
                    // --------------
                    props=mmbase.getMMObject("properties");
                    users=mmbase.getMMObject("users");
                    // does the sid have any properties?
                    if (props==null || users==null) {
                        log.warn("Can't Save: One of the needed builders is not loaded either users or properties");
                    } else {
                        Enumeration res=props.search("key=='SID'+value=='"+sid+"'");
                        if (res.hasMoreElements()) {
                            // yes, is it a user?
                            // ------------------
                            // get the parent for this ID value
                            MMObjectNode snode = (MMObjectNode)res.nextElement();
                            id=snode.getIntValue("parent");
                            node=users.getNode(id);
                            if( node != null ) {
                                session.setNode(node);
                            } else {
                                log.warn("saveValue(" + key + "): node("+id+") for user("+sid+") not found in usersdb, maybe long-time-no-see and forgotten?!");
                            }
                        } else {
                            // ----------------------------------------------------------------------
                            // Server has given a cookie, but *now* we create a new user & properties
                            // ----------------------------------------------------------------------
                            if (log.isServiceEnabled()) {
                                log.service("saveValue(" + key + "): This is a new user(" + sid + "), making database entry..");
                            }

                            node = users.getNewNode ("system");
                            if( node != null ) {
                                node.setValue ("description","created for SID = "+sid);
                                id = users.insert ("system", node);
                                node.setValue("number",id);

                                // hier
                                // ----
                                MMObjectNode snode = props.getNewNode ("system");
                                snode.setValue ("parent",id);
                                snode.setValue ("ptype","string");
                                snode.setValue ("key","SID");
                                snode.setValue ("value",sid);
                                props.insert("system", snode);
                                session.setNode(node);
                            } else {
                                log.error("saveValue(" + session + "," + key + "): No node(" + id + ") could be created for this user(" + sid + ")!");
                            }
                        }
                    }
                } else {
                    id=node.getIntValue("number");
                }
                // set value in the users node and save in database
                // ------------------------------------------------
                if (node!=null) {
                    MMObjectNode pnode=node.getProperty(key);
                    if (pnode!=null) {
                        String value=session.getSetString(key);
                        if (value==null) {
                            value=session.getValue(key);
                        }
                        pnode.setValue("value",value);
                        pnode.commit();
                    } else {
                        MMObjectNode snode = props.getNewNode ("system");
                        String value=session.getSetString(key);
                        if (value==null) {
                            value=session.getValue(key);
                            if( value==null )
                            {
                                log.warn("saveValue("+key+"): value("+value+") is null!");
                                log.warn(" - values(" + session.values +")" );
                                log.warn(" - setvalues(" + session.setvalues +")" );
                            }
                            snode.setValue ("ptype","string");
                        } else {
                            snode.setValue ("ptype","vector");
                        }
                        snode.setValue ("parent",id);
                        snode.setValue ("key",key);
                        snode.setValue ("value",value);
                        int id2=props.insert("system", snode);
                        snode.setValue("number",id2);
                        node.putProperty(snode);
                    }

                    return null;
                }
                else
                {
                    log.error("saveValue("+session+","+key+"): no node("+node+") found for user("+sid+")!");
                    return null;
                }
            } else {
                log.error("saveValue("+session+","+key+"): key is null!");
                return null;
            }
        } else {
            log.error("saveValue("+session+","+key+"): session is null!");
            return null;
        }
    }

    public String saveValueNew(sessionInfo session,String key) {
        if (mmbase==null) {
            log.error("saveValue("+session+","+key+"): mmbase is null!");
            return null;
        }
        if (session!=null) {
            if( key != null ) {
                int id=-1;
                String sid=session.getCookie();
                MMObjectNode node=session.getNode();

                if (node==null) {
                    // node not found
                    // --------------
                    props=mmbase.getMMObject("properties");
                    Users users=(Users)mmbase.getMMObject("users");
                    // does the sid have any properties?
                    if (props==null || users==null) {
                        log.warn("Can't Save: One of the needed builders is not loaded either users or properties");
                    } else {
                        id=users.getNumber(sid);
                        // get use the new way !
                        if (id!=-1) {
                            node=users.getNode(id);
                            if( node != null ) {
                                session.setNode(node);
                            } else {
                                log.warn("saveValue("+key+"): node("+id+") for user("+sid+") not found in usersdb, maybe long-time-no-see and forgotten?!");
                            }
                        } else {
                            // ----------------------------------------------------------------------
                            // Server has given a cookie, but *now* we create a new user & properties
                            // ----------------------------------------------------------------------
                            // Daniel: for now removed what should we do ??
                        }
                    }
                } else {
                    id=node.getIntValue("number");
                }
                // set value in the users node and save in database
                // ------------------------------------------------
                if (node!=null) {
                    MMObjectNode pnode=node.getProperty(key);
                    if (pnode!=null) {
                        String value=session.getSetString(key);
                        if (value==null) {
                            value=session.getValue(key);
                        }
                        pnode.setValue("value",value);
                        pnode.commit();
                    } else {
                        MMObjectNode snode = props.getNewNode ("system");
                        String value=session.getSetString(key);
                        if (value==null) {
                            value=session.getValue(key);
                            if( value==null )
                            {
                                log.warn("saveValue("+key+"): value("+value+") is null!");
                                log.warn(" - values(" + session.values +")" );
                                log.warn(" - setvalues(" + session.setvalues +")" );
                            }
                            snode.setValue ("ptype","string");
                        } else {
                            snode.setValue ("ptype","vector");
                        }
                        snode.setValue ("parent",id);
                        snode.setValue ("key",key);
                        snode.setValue ("value",value);
                        int id2=props.insert("system", snode);
                        snode.setValue("number",id2);
                        node.putProperty(snode);
                    }
                    return null;
                } else {
                    log.error("saveValue("+session+","+key+"): no node("+node+") found for user("+sid+")!");
                    return null;
                }
            } else {
                log.error("saveValue("+session+","+key+"): key is null!");
                return null;
            }
        } else {
            log.error("saveValue("+session+","+key+"): session is null!");
            return null;
        }
    }

    public Vector getList(scanpage sp, StringTagger tagger, String cmd) throws ParseException {
        String val;
        sessionInfo tmps;

        if (cmd.charAt(0)=='"') cmd=cmd.substring(1,cmd.length()-1);
        if (cmd.equals("sessions")) {
            Vector results = new Vector();
            for (Enumeration<String> e=sessions.keys();e.hasMoreElements();) {
                val = e.nextElement();
                results.addElement(val);
                tmps=sessions.get(val);
                results.addElement(tmps.getHostName());
            }
            return results;
        }
        if (cmd.equals("SESSION")) {
            Vector results=new Vector();
            String key;
            sessionInfo session=getSession(sp,sp.sname);
            for (Enumeration<String> e=session.values.keys();e.hasMoreElements();) {
                key=e.nextElement();
                results.addElement("VAR");
                results.addElement(key);
                results.addElement(session.getValue(key));
            }
            for (Enumeration<String> e=session.setvalues.keys();e.hasMoreElements();) {
                key=e.nextElement();
                results.addElement("SET");
                results.addElement(key);
                results.addElement(session.getSetString(key));
            }
            return results;
        }

        String line = Strip.doubleQuote(cmd,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd2=tok.nextToken();
            if (cmd2.equals("GETSET")) return doGetSet(sp,tok);
        }
        return null;
    }

    /**
     * Handle a $MOD command
     */
    public String replace(scanpage sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();

            if (cmd.equals("CLEARSET"))     return doClearSet(sp,tok);
            if (cmd.equals("ADDSET"))       return doAddSet(sp,tok);
            if (cmd.equals("PUTSET"))       return doPutSet(sp,tok);
            if (cmd.equals("DELSET"))       return doDelSet(sp,tok);
            if (cmd.equals("CONTAINSSET"))  return getContainsSet(sp,tok);
            if (cmd.equals("SETSTRING"))    return getSetString(sp,tok);
            if (cmd.equals("SETCOUNT"))     return getSetCount(sp,tok);
            if (cmd.equals("AVGSET"))       return getAvgSet(sp,tok);

            log.warn("replace(" + cmds + "): Unknown command(" + cmd + ")!");
        }
        return this.getClass().getName() +"replace(): ERROR: No command defined";
        // michiel: I did not explore for which this return value is used.
        // the function is public, so I don't like to change the return value.
    }

    /**
     * Adds a sessionvariable with specified value
     */
    public String doAddSet(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String value=tok.nextToken();
                    session.addSetValue(key,value);
                }
            }
        }
        return "";
    }

    public String doPutSet(scanpage sp , StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String value=tok.nextToken();
                    session.putSetValue(key,value);
                }
            }
        }
        return "";
    }

    /**
     * This methode clears a SESSION variable
     */
    public String doClearSet(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                    session.clearSet(key);
            }
        }
        return "";
    }

    public String getAvgSet(scanpage sp,StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                return session.getAvgSet(key);
            }
        }
        return "";
    }

    public String doDelSet(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String value=tok.nextToken();
                    session.delSetValue(key,value);
                }
            }
        }
        return "";
    }

    /**
     * Checks if a Session variable contains a certain value.
     *
     * @return "YES" if the session variable contains the specified value
     *         "NO" if the session variable doesn't contains the specified value
     */
    public String getContainsSet(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String value=tok.nextToken();
                    return session.containsSetValue(key,value);
                }
            }
        }
        return "NO";
    }

    public Vector doGetSet(scanpage sp, StringTokenizer tok) {
        Vector results=new Vector();
        String line=getSetString(sp,tok);
        if(log.isDebugEnabled()) {
            log.debug("doGetSet(): SESSION LINE="+line);
        }
        if (line!=null) {
            StringTokenizer tok2 = new StringTokenizer(line,",\n\r");
            while (tok2.hasMoreTokens()) {
                results.addElement(tok2.nextToken());
            }
        }
        return results;
    }

    /**
     * returns the values of a session variable comma separated
     */
    public String getSetString(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                String tmp=session.getSetString(key);
                if (tmp!=null) return tmp;
                tmp=session.getValue(key);
                if (tmp!=null) return tmp;
            }
        }
        return "";
    }

    /**
     * Gives the number of values contained by a certain session variable
     * @return the number of values contained by the session variable
     */
    public String getSetCount(scanpage sp, StringTokenizer tok) {
        sessionInfo session=getSession(sp,sp.sname);
        if (session!=null) {
            if (tok.hasMoreTokens()) {
                String key=tok.nextToken();
                String tmp=session.getSetCount(key);
                if (tmp!=null) return tmp;
            }
        }
        return "0";
    }

    public int getSize() {
        return sessions.size();
    }

    public Map<String, String> getStates() {
        setState("Sessions", "" + getSize());
        return super.getStates();
    }

    public Map<String, String> state() {
        return getStates();
    }

    /**
     * Stores visiting info (counters) in a session
     * @deprecated-now remove in 1.7, not used
     */
    void setVisitInfo(sessionInfo session) {
        String counter=session.getValue("SESSIONCOUNT");
        if (counter==null) {
            session.setValue("SESSIONCOUNT","1");
        } else {
            try {
                int icounter=Integer.parseInt(counter);
                session.setValue("SESSIONCOUNT",""+(icounter+1));
            } catch (Exception e) {
                session.setValue("SESSIONCOUNT","1");
            }
            int time=(int)(System.currentTimeMillis()/1000);
            int day=(time/(3600*24));
            String hday=session.getValue("LASTVISIT");
            if (hday==null || !hday.equals(""+day)) {
                session.setValue("LASTVISIT",""+day);
                saveValue(session,"LASTVISIT");
                String visits=session.getValue("VISITCOUNT");
                if (visits==null || visits.equals("")) {
                    session.setValue("FIRSTVISIT",""+day);
                    saveValue(session,"FIRSTVISIT");
                    session.setValue("VISITCOUNT","1");
                    saveValue(session,"VISITCOUNT");
                } else {
                    try {
                        int val=Integer.parseInt(visits);
                        session.setValue("VISITCOUNT",""+(val+1));
                        saveValue(session,"VISITCOUNT");
                    } catch(Exception e) {}
                }
            }
        }
    }

    /**
     * the XML reader will correct the escaped characters again.
     * but scan will not evaluate the tags.
     * only &lt; has to be parsed to &amp;&lt;
     */
    public String xmlEscape (String s) {
        StringBuffer out = new StringBuffer();
        int l = s.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '&':
                    out.append("&amp;");
                    break;
                default:
                    out.append(c);
            }
        }
        return out.toString();
    }
}
