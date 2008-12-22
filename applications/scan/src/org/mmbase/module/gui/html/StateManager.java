/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;


/**
 * The StateManager class maintains a list of EditStates for users logged on to MMBase through SCAN.
 * It provides the states so a user can browse the SCAN editors and edit objects, letting the server remember the change history.
 * Changes to the state are made either by calling a replace ($MOD) command, or by processing parameters passed to a SCAN page.
 * State info (such as the current editnode number) can be retrieved using $MOD.
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @version $Id: StateManager.java,v 1.20 2008-12-22 18:52:37 michiel Exp $
 */

public class StateManager implements CommandHandlerInterface {

    // Logger
    private static Logger log = Logging.getLoggerInstance(StateManager.class.getName());

    /**
     * Reference to the MMBase module.
     */
    public MMBase mmBase;

    /**
     * Username to EditState mappings.
     * Each user has an editstate, stored in the statemanager.
     * @scope private
     */
    Hashtable<String, EditState> editStates; // HashTable with editstates indexed by usernames

    /**
     * Initialises the StateManager, by creating a new (empty) map of editstates.
     * @param mmBase reference to the MMBase module
     */
    public StateManager(MMBase mmBase) {
        this.mmBase = mmBase;
        editStates = new Hashtable<String, EditState>();
    }

    /**
     * Loads all previously persistified editstates from the database.
     * @deprecated-now removed per 1.7, does not do anything, and is never called
     */
    public void initUserStates() {
    }

    /**
     * Retrieves the EditState for a user, or creates a new one if the user did not yet have an EditState assigned.
     * The EditState contains status information for a specific user (which node is being edited, for instance).
     * EditStates are associated by username. They are kept in memory as long as teh StateManager is.
     * @param user the user for which to retrieve an EditState object
     * @return the EditState objevt associated with this user
     */
    public EditState getEditState(String user) {
        EditState result = editStates.get(user);
        if (result == null) {
            result = new EditState(user,mmBase);
            editStates.put(user, result);
        }
        return result;
    }

    /**
     * Handle a $MOD command.
     * This generally replaces the command in the SCAN page with the value returned by the command.<br />
     * Commands include:
     * <ul>
     *  <li>SETBUILDER-buildername[-joinnode]: Adds a new node of the specified builder type to the user's stack of working objects, and makes it current. If 'joinnode' is specified, the JOINNODE variable is set to this value</li>
     *  <li>GETBUILDER : lists the type of the current working object.</li>
     *  <li>DELBUILDER : drop the current working object. If the stack of working objects is not empty, the top object becomes current.</li>
     *  <li>CLEARBUILDERS : drop all working objects</li>
     *  <li>ADDRELATION : obsolete </li>
     *  <li>SETHTMLVALUE-name-value : sets teh value fo teh variable 'name' to 'value'</li>
     *  <li>GETHTMLVALUE-name : returns the value of the variable 'name' </li>
     *  <li>SETEDITNODE : ??? </li>
     *  <li>GETEDITNODE : ??? </li>
     *  <li>GETEDITSRCDUTCHNAME : ??? </li>
     *  <li>GETEDITDSTDUTCHNAME : ??? </li>
     *  <li>GETEDITSRCNAME : ??? </li>
     *  <li>GETEDITDSTNAME : ??? </li>
     *  <li>GETEDITSRCNODE : ??? </li>
     *  <li>GETEDITDSTNODE : ??? </li>
     *  <li>GETEDITSRCGUIINDICATOR : ??? </li>
     *  <li>GETEDITDSTGUIINDICATOR : ??? </li>
     *  <li>NEWNODE : ??? </li>
     *  <li>NEWINSNODE-sourcenr-destinationnr-role : Creates a relation object node using the specified parameters and makes it current</li>
     *  <li>REMOVENODE : ??? </li>
     *  <li>REMOVEEDITOR : ??? </li>
     *  <li>ISCHANGED : ??? </li>
     * </ul>
     */
    public String replace(PageInfo sp, StringTokenizer commands) {
        // Retrieve the username.
        // Or at least, that is the intention.
        // What this method REALLY does is authenticate the user (even if he was authenticated before).
        // Depending on the system you use this can drastically slow down the editors.
        String userName=HttpAuth.getRemoteUser(sp);
        if (userName==null) return "StateManager-> not logged in";

        // obtain an editstate for the user
        EditState state = getEditState(userName);

        if (commands.hasMoreTokens()) {
            String token = commands.nextToken();
            if (token.equals("SETBUILDER")) {
                if (commands.hasMoreTokens()) {
                    state.setBuilder(commands.nextToken());
                    if (commands.hasMoreTokens()) {
                        state.setHtmlValue("JOINNODE",commands.nextToken());
                    } else {
                        state.setHtmlValue("JOINNODE","");
                    }
                }
            } else if (token.equals("GETBUILDER")) {
                    return state.getBuilderName();
            } else if (token.equals("DELBUILDER")) {
                    state.popState();
            } else if (token.equals("CLEARBUILDERS")) {
                    state.clear();
            } else if (token.equals("ADDRELATION")) {
                    log.warn("ADDRELATION is deprecated in "+sp +"; use NEWINSNODE");
                    state.addRelation(userName);
            } else if (token.equals("SETHTMLVALUE")) {
                    state.setHtmlValue(commands.nextToken(),commands.nextToken());
            } else if (token.equals("GETHTMLVALUE")) {
                    return state.getHtmlValue(commands.nextToken());
            } else if (token.equals("SETEDITNODE")) {
                    state.setEditNode(commands.nextToken(),userName);
            } else if (token.equals("GETEDITNODE")) {
                    return ""+state.getEditNodeNumber();
            } else if (token.equals("GETEDITSRCDUTCHNAME")) {
                    return state.getEditNodeSrcDutchName();
            } else if (token.equals("GETEDITDSTDUTCHNAME")) {
                    return state.getEditNodeDstDutchName();
            } else if (token.equals("GETEDITSRCNAME")) {
                    return state.getEditNodeSrcName();
            } else if (token.equals("GETEDITDSTNAME")) {
                    return state.getEditNodeDstName();
            } else if (token.equals("GETEDITSRCNODE")) {
                    return ""+state.getEditNodeSrcNumber();
            } else if (token.equals("GETEDITDSTNODE")) {
                    return ""+state.getEditNodeDstNumber();
            } else if (token.equals("GETEDITSRCGUIINDICATOR")) {
                    return state.getEditNodeSrcGuiIndicator();
            } else if (token.equals("GETEDITDSTGUIINDICATOR")) {
                    return state.getEditNodeDstGuiIndicator();
            } else if (token.equals("NEWNODE")) {
                    state.NewNode(userName);
            } else if (token.equals("NEWINSNODE")) {
                    newInsNode(state,userName,commands);
            } else if (token.equals("REMOVENODE")) {
                    state.removeNode();
            } else if (token.equals("REMOVEEDITOR")) {
                    state.popState();
            } else if (token.equals("ISCHANGED")) {
                    if (state.isChanged()) {
                        return "YES";
                    } else {
                        return "NO";
                    }
            }
            return "";
        }

        return "Command not defined (StateManager)";
    }


    /**
     * Creates a new Node, depending on the builder name (or relation name) specified in the StringTokenizer.
     * This method is used to create relation nodes
     * @param ed Editstate in which to add the new node.
     * @param userName User who becomes owner of the new node
     * @param tok Tokens used to configure the node. The next three tokens should be:
     * <ul>
     *   <li> The number of the node to link FROM </li>
     *   <li> The number of the node to link TO</li>
     *   <li> The name of the builder to use or relation to add (determines type of node and/or relation)</li>
     * </ul>
     * @return Always true. If the addition was successful, a new node has been added to the EditState object.
     */
    boolean newInsNode(EditState ed,String userName,StringTokenizer tok) {
        try {
            String tmp=tok.nextToken();
            int n1=Integer.parseInt(tmp);

            tmp=tok.nextToken();
            int n2=Integer.parseInt(tmp);

            String builder=tok.nextToken();

            int rtype=-1;

            // tests if the 'builder' specified is actually a relationname.
            // If so, the number of the relation in RelDef is obtained,
            // and the name of the builder to use is determined (if explicitly given)).

            MMObjectBuilder bul = null;
            rtype = mmBase.getRelDef().getNumberByName(builder);

            if ((rtype!=-1) && (RelDef.usesbuilder))  { // relation found
                MMObjectNode node=mmBase.getRelDef().getNode(rtype);  // retrieve the reldef node
                bul = mmBase.getRelDef().getBuilder(node);
            } else {
                bul = mmBase.getMMObject(builder);
            }

            // check whether the builder is a valid relationbuilder.
            // otherwise assign InsRel
            // note that is the builder specified is not an InsRel-derived builder, it will be overridden

            if (!(bul instanceof InsRel)) {
                bul=mmBase.getInsRel();
            }

            ed.popState();
            ed.setBuilder(bul.getTableName());

            ed.NewNode(userName);
            MMObjectNode node=ed.getEditNode();
            node.setValue("snumber",n1);
            node.setValue("dnumber",n2);

            // If rtype is unknown, try to get the type from TypeRel

            if ((bul == mmBase.getInsRel()) && (rtype==-1)) {
                rtype=mmBase.getTypeRel().getAllowedRelationType(bul.getNodeType(n1),bul.getNodeType(n2));
            }

            // assign rtype. Note that rtype is only set if it is actually known.
            // in rare cases, rtype can be -1. This happens if the relationname specified is the name of a
            // relationbuilder for which no relation definition with the same name is defined.
            if (rtype!=-1) {
                node.setValue("rnumber",rtype);
            }

        } catch (Exception e) {
            log.error("StateManager -> Can't create insnode");
            e.printStackTrace();
        }
        return true;
    }


    /**
     * setSearchVals
     * @javadoc
     */
    boolean setSearchValues(EditState ed, Hashtable vars) {
        String varline;
        ed.clearSearchValues();
        for (Enumeration h=vars.keys();h.hasMoreElements();) {
            varline=(String)h.nextElement();
            StringTokenizer tok = new StringTokenizer(varline,"-\n\r");
                String var=tok.nextToken();
                if (var.equals("STATE")) var=tok.nextToken();
                if (var.equals("SEARCHVALUE")) {
                    String key=tok.nextToken();
                    String keyval=(String)vars.get("STATE-SEARCHVALUE-"+key);
                    ed.setSearchValue(key,keyval);
                }
        }
        MMObjectBuilder bul=ed.getBuilder();
        ed.setSelectionQuery(createSelectionQuery(ed.getSearchValues(),bul));
        return true;
    }

    /**
     * @javadoc
     */
    String createSelectionQuery(Hashtable<String, Object> skeys,MMObjectBuilder bul) {
        String where="MMNODE ",key,val;
        String name=bul.getTableName();
            for (Enumeration<String> h=skeys.keys();h.hasMoreElements();) {
                key=h.nextElement();
                val=(String)skeys.get(key);
                    if (val!=null && !val.equals("")) {
                    // val to lower for search
                    val=val.toLowerCase();
                    if (key.equals("maxage")) {
                        int ival=30;
                        try {
                            ival=Integer.parseInt(bul.getSearchAge());
                        } catch(Exception e) {}
                        try {
                            ival=Integer.parseInt(val);
                        } catch(Exception e) {}
                        DayMarkers daym=(DayMarkers)bul.getMMBase().getMMObject("daymarks");
                        int mark=daym.getDayCountAge(ival);
                        if (where.equals("MMNODE ")) {
                            where+=name+".number=G"+mark;
                        } else {
                            where+="+"+name+".number=G"+mark;
                        }
                    } else {
                        if (where.equals("MMNODE ")) {
                            where+=name+"."+key+"==*"+val+"*";
                        } else {
                            where+="+"+name+"."+key+"==*"+val+"*";
                        }
                    }
                }
            }

        return where;
    }

    /**
     * An object has been selected and the EditState of the specific user
     * is updated.
     * @deprecated-now removed per 1.7, does not do anything, and is never called
     */
    void updateSelectedObject(String user, String objectID) {
    }

    /**
     * A field has been selected to edit and the EditState for the specific
     * user is updated.
     * @deprecated-now removed per 1.7, does not do anything, and is never called
     */
    void updateEditField(String user, String fieldName) {
    }

    /**
     * a new relation has been initiated and the EditState for the specific
     * user is updated.
     * @deprecated-now removed per 1.7, does not do anything, and is never called
     */
    void initLink(String user, String objectType) {
    }

    /**
     * List commands
     * @javadoc
     */
    public List<String> getList(PageInfo sp, StringTagger args, StringTokenizer command) throws org.mmbase.module.ParseException {
        String token;
        String userName=HttpAuth.getRemoteUser(sp);
        EditState state = getEditState(userName);
        Vector result = new Vector();

        if (command.hasMoreTokens()) {
            token = command.nextToken();
            if (token.equals("GETOPENBUILDERS")) {
                return getOpenBuilders(state,args);
            }
        }
        result.addElement("No List command defined (FieldEditor)");
        return result;
    }

    /**
     * The hook that passes all form related pages to the correct handler
     * @javadoc
     */
    public boolean process(PageInfo sp, StringTokenizer command,Hashtable cmds, Hashtable vars) {
        String userName=HttpAuth.getRemoteUser(sp);
        EditState state = getEditState(userName);

        String cmd,cmdline;
        for (Enumeration h=cmds.keys();h.hasMoreElements();) {
            cmdline=(String)h.nextElement();
            StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
            if (tok.hasMoreTokens()) {
                cmd=tok.nextToken(); // read away dummy STATE-
                cmd=tok.nextToken();
                if (cmd.equals("SETSEARCHVALUES")) return setSearchValues(state,vars);
                if (cmd.equals("REMOVENODE")) {
                    String qw=(String)cmds.get("STATE-REMOVENODE");
                    if (qw.equals("YES")) {
                        // delete the relations to this node and
                        // the node itself
                        state.removeRelations();
                        state.removeNode();
                        state.setHtmlValue("Chooser","select");
                        state.setHtmlValue("Work","empty");
                    }
                } else
                if (cmd.equals("REMOVERELATION")) {
                    String qw=(String)cmds.get("STATE-REMOVERELATION");
                    if (qw.equals("YES")) {
                        state.removeNode();
                        state.popState();
                        state.setHtmlValue("Chooser","realFieldEdit");
                        state.setHtmlValue("Work","empty");
                    }
                } else
                if (cmd.equals("NEXTFIELD")) {
                    String currentfield=(String)cmds.get("STATE-NEXTFIELD");
                    state.setHtmlValue("Work","nextfield");
                    MMObjectBuilder bul=state.getBuilder();
                    FieldDefs ndefs=bul.getNextField(currentfield);
                    if (ndefs!=null) {
                        state.setHtmlValue("NEXTFIELD",""+ndefs.getGUIType()+".shtml?"+ndefs.getDBName()+"+"+ndefs.getGUIName(state.getLanguage()));
                    } else {
                        state.setHtmlValue("NEXTFIELD","empty.shtml");
                    }
                } else
                if (cmd.equals("SETHTMLVALUE")) {
                    String field=tok.nextToken();
                    String value=(String)cmds.get("STATE-SETHTMLVALUE-"+field);
                    state.setHtmlValue(field,value);
                }
            }
        }
        return false;
    }

    /**
     * @javadoc
     */
    public Vector getOpenBuilders(EditState state,StringTagger args) {
        Vector results=new Vector();
        Vector<EditStateNode> nodes=state.getEditStates();
        EditStateNode node;
        MMObjectNode curnode=state.getEditNode(); // problem
        for (Enumeration<EditStateNode> h=nodes.elements();h.hasMoreElements();) {
            node=h.nextElement();
            results.addElement(node.getDutchBuilderName());
            if (curnode==node.getEditNode()) {
                results.addElement("a");
            } else {
                results.addElement("n");
            }
        }
        args.setValue("ITEMS","2");
        return results;
    }

    /**
     * Retrieves the EditState for a user, or creates a new one if the user did not yet have an EditState assigned.
     * @deprecated-now removed per 1.7, use getEditState() instead.
     * @param user the user for which to retrieve an EditState object
     * @return the EditState objevt associated with this user
     */
    public EditState getState(String user) {
        EditState result;

        result = editStates.get(user);
        if (result == null) result = new EditState(user,mmBase);

        return result;
    }

}

