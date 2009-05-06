/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.module.ParseException;
import org.mmbase.module.core.ClusterBuilder;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.*;

/**
 * The ObjectSelector class offers the functionality to search for objects
 * and select found objects to be edited by the FieldSelector and FieldEditor
 * classes (hitlisted).
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @version $Id: ObjectSelector.java,v 1.25 2008-12-22 18:52:37 michiel Exp $
 */
public class ObjectSelector implements CommandHandlerInterface {

    StateManager stateMngr;

    /**
     * Constructor to setup reference to the StateManager.
     */
    public ObjectSelector(StateManager manager) {
        stateMngr = manager;
    }

    /**
     * General List pages coming from MMEdit.
     * @javadoc
     */
    public List<String> getList(PageInfo sp, StringTagger args, StringTokenizer commands) throws ParseException {
        String token;
        String userName=HttpAuth.getRemoteUser(sp);

        EditState state = stateMngr.getEditState(userName);
        Vector result = new Vector();

        if (commands.hasMoreTokens()) {
            token = commands.nextToken();
            if (token.equals("GETOBJECTS")) {
                args.setValue("ITEMS","3");
                return getObjectSelectionList(state);
            } else if (token.equals("GETOBJECTTITLES")) {
                args.setValue("ITEMS","1");
                return getObjectSelectionTitles(state);
            } else if (token.equals("GETOBJECTFIELDS")) {
                args.setValue("ITEMS","5");
                return getObjectFields(state);
            } else if (token.equals("GETOBJECTRELATIONS")) {
                args.setValue("ITEMS","4");
                return getObjectRelations(state);
            } else if (token.equals("GETOBJECTRELATIONS2")) {
                args.setValue("ITEMS","4");
                return getObjectRelations2(state);
            } else if (token.equals("GETOBJECTRELATIONS3")) {
                args.setValue("ITEMS","8");
                return getObjectRelations3(state,args);
            }
        }
        result.addElement("List not defined (ObjectSelector)");
        return result;
    }

    /**
     * @javadoc
     */
    Vector getObjectFields(EditState ed) {
        String language=ed.getLanguage();
        Vector results=new Vector();
        MMObjectBuilder obj=ed.getBuilder();
        MMObjectNode node=ed.getEditNode();
        if (node!=null) {
            FieldDefs def;
            String DBName,val;
            for (Object element : obj.getFields(FieldDefs.ORDER_EDIT)) {
                def=(FieldDefs)element;
                DBName=def.getDBName();
                if (!DBName.equals("owner") && !DBName.equals("number") && !DBName.equals("otype")) {
                    val=obj.getGUIIndicator(DBName,node);
                    if (val==null) val = node.getStringValue( DBName );
                    else if (val.equals("null")) val="";
                    results.addElement(DBName);
                    results.addElement(val);
                    if (val.length()>14 && val.indexOf("<")==-1) {
                        results.addElement(val.substring(0,14)+"...");
                    } else {
                        results.addElement(val);
                    }
                    results.addElement(def.getGUIName(language));
                    results.addElement(def.getGUIType());
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getObjectRelations2(EditState ed) {
        Vector results=new Vector();
        MMObjectBuilder obj=ed.getBuilder();
        MMObjectNode node=ed.getEditNode();

        if (node!=null && node.getIntValue("number")!=-1) {
            Enumeration e=stateMngr.mmBase.getInsRel().getRelations(ed.getEditNodeNumber());
            MMObjectNode rel;
            for (;e.hasMoreElements();) {
                rel=(MMObjectNode)e.nextElement();
                MMObjectNode rdn=stateMngr.mmBase.getRelDef().getNode(rel.getIntValue("rnumber"));
                // Am I the source of the desitination of the relation ?
                if (rel.getIntValue("snumber")==ed.getEditNodeNumber()) {
                    MMObjectNode other=obj.getNode(rel.getIntValue("dnumber"));
                    results.addElement(""+rel.getIntValue("number"));
                    results.addElement(stateMngr.mmBase.getTypeDef().getValue(rel.getIntValue("otype")));
                    results.addElement(stateMngr.mmBase.getTypeDef().getValue(other.getIntValue("otype")));
                    results.addElement(""+other.getGUIIndicator());
                } else {
                    MMObjectNode other=obj.getNode(rel.getIntValue("snumber"));
                    results.addElement(""+rel.getIntValue("number"));
                    results.addElement(stateMngr.mmBase.getTypeDef().getValue(rel.getIntValue("otype")));
                    results.addElement(stateMngr.mmBase.getTypeDef().getValue(other.getIntValue("otype")));
                    results.addElement(""+other.getGUIIndicator());
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector<String> getAllowedBuilders(String user) {
        Vector<String> allowed=null;
        if (stateMngr.mmBase.getAuthType().equals("basic")) {
            allowed=new Vector<String>();
            ClusterBuilder clusterBuilder = stateMngr.mmBase.getClusterBuilder();
            Vector tables=new Vector();
            tables.addElement("typedef");
            // bug bug, daniel (9 aug) tables.addElement("authrel");
            tables.addElement("insrel");
            tables.addElement("people");
            Vector fields=new Vector();
            fields.addElement("typedef.name");
            fields.addElement("people.account");
            Vector ordervec=new Vector();
            Vector dirvec=new Vector();
            dirvec.addElement("UP");

            Vector vec = clusterBuilder.searchMultiLevelVector(-1,fields,"NO",tables,"people.account=E'"+user+"'",ordervec,dirvec);
            for (Enumeration h=vec.elements();h.hasMoreElements();)	{
                MMObjectNode node=(MMObjectNode)h.nextElement();
                String builder=node.getStringValue("typedef.name");
                allowed.addElement(builder);
            }
        }
        return allowed;
    }

    /**
     * Retrieves a list of existing relations and allowed relation types to a specific node.
     * Represented by a vector of strings, in which each set of 8 consequetive strings
     * Represents either an existing relation or a relationtype to be used for creating a new one.<br>
     * The strings represent, in order:<br>
     * 1 - The builder name of the type linked to (i.e. people) <br>
     * 2 - The builder name of the relation (i.e. insrel) if an existing relation, otherwise the builder name of the typed linked to <br>
     * 3 - The number of the relation node if an existing relation, otehrwise the number of the current (edited) node <br>
     * 4 - Empty when an existing realtion. Otherwise it is either the name of the relation defintiton for a typed relation, or,
     *     when more than one relationtype exists for this node type, the value "multiple" <br>
     * 5 - GUI name for the node linked to. (empty when a relation type)<br>
     * 6 - "insEditor" if this is an existing node, "addeditor" if this is a rel;ation type. <br>
     * 7 - GUI name of the linked-to node's builder (language dependent) <br>
     * 8 - The gui name of the relation definition for this relation (appropriate for direction, empty when a relation type). <br>
     * @param ed the EditState object that governs this edit
     * @args the arguments to this command. The only argument available is "USER". If set, a crude authorization is checked which allows
     *       or disallows access to relations to specific node types.
     * @return a <code>Vector</code> of <code>String</code>
     */
    Vector getObjectRelations3(EditState ed,StringTagger args) {
        Vector results=new Vector();
        MMObjectBuilder obj=ed.getBuilder();
        MMObjectNode node=ed.getEditNode();

        String user=args.Value("USER");
        Vector<String> allowed=null;
        if (user!=null && !user.equals("")) {
            allowed=getAllowedBuilders(user);
        }

        if (node!=null) {
            String name;
            Hashtable<String, Vector> res=ed.getRelationTable();
            Enumeration<String> e=res.keys();
            MMObjectNode rel;
            MMObjectNode other;
            Vector qw;
            for (;e.hasMoreElements();) {
                name=e.nextElement();

                if (allowed==null || allowed.contains(name)) {
                    qw=res.get(name);
                    for (Enumeration h=qw.elements();h.hasMoreElements();) {
                        other=(MMObjectNode)h.nextElement();
                        rel=(MMObjectNode)h.nextElement();
                        results.add(name);
                        results.add(obj.getMMBase().getTypeDef().getValue(rel.getOType()));
                        results.add(""+rel.getIntValue("number"));
                        results.add("");
                        results.add(other.getGUIIndicator());
                        results.add("insEditor");
                        results.add(other.getBuilder().getSingularName());
                        MMObjectNode reldef = obj.getNode(rel.getIntValue("rnumber"));
                        if (reldef==null) {
                            results.add("");
                        } else {
                            if (rel.getIntValue("snumber")==node.getIntValue("number")) {
                                results.add(reldef.getStringValue("dguiname"));
                            } else {
                                results.add(reldef.getStringValue("sguiname"));
                            }
                        }
                    }
                    // the empty dummy
                    results.add(name);
                    results.add(name);
                    results.add(""+node.getIntValue("number"));

                    // startnewfix
                    int reltype=stateMngr.mmBase.getTypeRel().getAllowedRelationType(node.getIntValue("otype"),stateMngr.mmBase.getTypeDef().getIntValue(name));
                    if (reltype!=-1) {
                        MMObjectNode rdn=stateMngr.mmBase.getRelDef().getNode(reltype);
                        results.add(rdn.getStringValue("sname"));
                    } else {
                        results.add("multiple");
                    }
                    // end new fix

                    results.add("");
                    results.add("addEditor");
                    results.add(stateMngr.mmBase.getTypeDef().getSingularName(name,null));
                    results.add("");
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getObjectRelations(EditState ed) {
        Vector results=new Vector();
        MMObjectBuilder obj=ed.getBuilder();
        MMObjectNode node=ed.getEditNode();
        if (node!=null) {
            // find all the typeRel that are allowed
            Enumeration e=stateMngr.mmBase.getTypeRel().getAllowedRelations(node);
            MMObjectNode trn;
            MMObjectNode rdn;
            MMObjectNode tdn;
            for (;e.hasMoreElements();) {
                trn=(MMObjectNode)e.nextElement();
                rdn=stateMngr.mmBase.getRelDef().getNode(trn.getIntValue("rnumber"));
                if (trn.getIntValue("snumber")==node.getIntValue("otype")) {
                    tdn=stateMngr.mmBase.getTypeDef().getNode(trn.getIntValue("dnumber"));
                    results.addElement(""+trn.getIntValue("snumber"));
                    results.addElement(tdn.getStringValue("name"));
                    results.addElement(rdn.getStringValue("sGUIName"));
                    results.addElement(rdn.getStringValue("sname"));
                } else {
                    tdn=stateMngr.mmBase.getTypeDef().getNode(trn.getIntValue("snumber"));
                    results.addElement(""+trn.getIntValue("dnumber"));
                    results.addElement(tdn.getStringValue("name"));
                    results.addElement(rdn.getStringValue("dGUIName"));
                    results.addElement(rdn.getStringValue("sname"));
                }
            }
        }
        return results;
    }

    /**
     * Builds a list of HTML Table rows to display a table of found values.
     * It gets the values out of the database using the selectionQuery of
     * the current EditState
     * Item1 = Object ID for selectable object
     * Item2 = HTML String met record waardes
     */
    Vector getObjectSelectionList(EditState state) {
        Vector result = new Vector();
        String key,guival;
        MMObjectBuilder builder = state.getBuilder();
        MMObjectNode node;

        String conditions = state.getSelectionQuery();
        Enumeration searchResult;

        String HTMLString = "";

        if (builder != null) {
            Vector vals = new Vector(builder.getFields(org.mmbase.bridge.NodeManager.ORDER_LIST));
            //searchResult = builder.search(conditions);
            //searchResult = builder.search(conditions,"number");
            searchResult = HtmlBase.search(builder,conditions,"number",false);

            while (searchResult.hasMoreElements()) {
                node = (MMObjectNode)searchResult.nextElement();
                result.addElement(node.getValue("number").toString());
                HTMLString = "";

                for (Enumeration enumeration = vals.elements(); enumeration.hasMoreElements();) {
                    key = ((FieldDefs)enumeration.nextElement()).getDBName();
                    guival=builder.getGUIIndicator(key,node);
                    if (guival!=null) {
                        HTMLString += "<td bgcolor=\"#FFFFFF\" width=\"500\"> "+guival+"&nbsp;</td> ";
                    } else {
                            Object o=node.getValue(key);
                            if (o==null || o.toString().equals("null")) {
                                HTMLString += "<td bgcolor=\"#FFFFFF\" width=\"500\">&nbsp;</td> ";
                            } else {
                                // should be replaced soon mapping is weird
                                if (o.toString().equals("$SHORTED")) {
                                    o=node.getStringValue(key);
                                }
                                HTMLString += "<td bgcolor=\"#FFFFFF\" width=\"500\"> "+o.toString()+"&nbsp; </td> ";
                            }
                    }
                }
                result.addElement(HTMLString);
                result.addElement(""+vals.size());
            }
        }
        return result;
    }

    /**
     * Builds a list of title strings containing the fields to be displayed
     * Item1 = Name of the field (GUI Name)
     */
    Vector getObjectSelectionTitles(EditState state) {
        Vector result = new Vector();
        String language=state.getLanguage();
        MMObjectBuilder builder = state.getBuilder();
        Vector fieldDefs;

        if (builder != null) {
            fieldDefs = new Vector(builder.getFields(org.mmbase.bridge.NodeManager.ORDER_LIST));
            for (Enumeration enumeration = fieldDefs.elements(); enumeration.hasMoreElements();) {
                result.addElement(((FieldDefs)enumeration.nextElement()).getGUIName(language));
            }
        }

        return result;
    }

    /**
     * General proces pages coming from MMEdit.
     */
    public boolean process(PageInfo sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        return false;
    }

    /**
     * Sets the selection query for this user in this editor.
     */
    boolean setObjectSelectionConditions(String user, Hashtable vars) {
        EditState state = stateMngr.getEditState(user);

        // Waardes uit de values lezen en met setQueryString() aan
        // de userstate geven

        return true;
    }

    /**
     * Handle a $MOD command.
     * ObjectSelector does not offer any commands.
     */
    public String replace(PageInfo sp, StringTokenizer cmds) {
        return "Command not defined (ObjectSelector)";
        // bedoeld voor het clearen van de serachvalues
    }

    /**
     * Clears the search fields for the searchfields
     * @deprecated-now removed per 1.7, does not do anything, and is never called
     */
    void clearSearchFields(String user) {
    }

}




