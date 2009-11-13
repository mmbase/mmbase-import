/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * EditStateNode (hitlisted)
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @version $Id$
 */
public class EditStateNode {

    /**
    * Logging instance
    */
    private static Logger log = Logging.getLoggerInstance(EditStateNode.class.getName());

    private static boolean removeRelations=false; // remover relations on node delete

    Hashtable<String, Object> searchValues = new Hashtable<String, Object>();
    Hashtable<String, Object> htmlValues = new Hashtable<String, Object>();
    String editNode;
    String editor;
    String dutchEditor;
    String selectionQuery="";
    MMObjectBuilder mmObjectBuilder;
    MMObjectNode node;
    MMBase mmBase;
    int nodeEditPos=0;
    boolean insSave=false;
    Vector<MMObjectNode> insSaveList=new Vector<MMObjectNode>();

    public EditStateNode(MMBase mmBase) {
        this.mmBase=mmBase;
    }

    public boolean setSearchValue(String fieldname,Object value) {
        searchValues.put(fieldname,value);
        return true;
    }

    public String getSearchValue(String name) {
        String result = null;

        if( name != null )
            if( !name.equals("") )
                result = ((String)searchValues.get(name));
            else
                log.error("getSearchValue("+name+"): name is empty!");
        else
            log.error("getSearchValue("+name+"): name is null!");

        return result;
    }

    public Hashtable<String, Object> getSearchValues() {
        return searchValues;
    }

    public boolean isChanged() {
        if (node.isChanged() || insSaveList.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    public void clearSearchValues() {
        searchValues=new Hashtable<String, Object>();
    }


    public boolean setHtmlValue(String fieldname,Object value) {
        if( fieldname != null )
        {
            if(!fieldname.equals(""))
            {
                htmlValues.put(fieldname,value);
            }
            else
                log.error("setHtmlValue("+fieldname+","+value+"): fieldname is empty!");
        }
        else
            log.error("setHtmlValue("+fieldname+","+value+"): fieldname is null!");

        return true;
    }

    public String getHtmlValue(String name) {
        return (String)htmlValues.get(name);
    }

    public Hashtable<String, Object> getHtmlValues() {
        return htmlValues;
    }

    public void clearHtmlValues() {
        htmlValues=new Hashtable<String, Object>();
    }

    public void setEditNode(String number,String userName) {
        node = mmObjectBuilder.getNode(number);
        editNode = number;
    }


    public void getNewNode(String owner) {
        node = mmObjectBuilder.getNewNode(owner);
        editNode="-1";
        delRelationTable();
        //editNode = "-1"; // signal its a new node
    }

    public void removeNode() {
        mmObjectBuilder.removeNode(node);
    }

    public void removeRelations() {
        if (removeRelations) {
            mmObjectBuilder.removeRelations(node);
        }
    }

    public MMObjectNode getEditNode() {
        return node;
    }

    public MMObjectNode getEditSrcNode() {
        int snum=node.getIntValue("snumber");
        if (snum!=-1) {
            MMObjectNode rnode=mmObjectBuilder.getNode(snum);
            return rnode;
        } else {
            return null;
        }
    }


    public MMObjectNode getEditDstNode() {
        int dnum=node.getIntValue("dnumber");
        if (dnum!=-1) {
            MMObjectNode rnode=mmObjectBuilder.getNode(dnum);
            return rnode;
        } else {
            return null;
        }
    }

    public int getEditNodeNumber() {
        try {
            int i=Integer.parseInt(editNode);
            return i;
        } catch(Exception e) {
        }
        return -1;
    }

    public void setBuilder(String name) {
        if( name != null )
            if( mmBase != null )
            {
                mmObjectBuilder = mmBase.getMMObject(name);
                if( mmObjectBuilder != null )
                    dutchEditor= mmObjectBuilder.getSingularName();
                else
                    log.error("setBuilder("+name+"): No MMObjectBuilder found with this name!");
                editor = name;
            }
            else
                log.error("setBuilder("+name+"): MMBase is not defined!");
        else
            log.error("setBuilder("+name+"): Name is not valid!");
    }

    public String getBuilderName() {
        return editor;
    }

    public String getDutchBuilderName() {
        return dutchEditor;
    }

    public MMObjectBuilder getBuilder() {
        return mmObjectBuilder;
    }

    public void setSelectionQuery(String query) {
        selectionQuery = query;
    }

    public String getSelectionQuery() {
        return selectionQuery;
    }

    public boolean getInsSave() {
        return insSave;
    }

    public void setInsSave(boolean set) {
        insSave=set;
    }

    public void setInsSaveNode(MMObjectNode node) {
        insSaveList.addElement(node);
        log.debug("setInsSaveNode(): "+insSaveList.toString());
    }

    public Vector<MMObjectNode> getInsSaveList() {
        return insSaveList;
    }

    public void delInsSaveList() {
        insSaveList=new Vector<MMObjectNode>();
    }

    public void delRelationTable() {
        log.debug("delRelationTable(): Del on relation table, here not implemented!");
    }

    /**
     * Returns Hashtable with the currently linked items sorted by relation
     * type.
     */
    public Hashtable<String, Vector> getRelationTable() {
        Enumeration enumeration = mmBase.getTypeRel().getAllowedRelations(node);
        MMObjectNode typeRel;
        String typeName;

        // build Hashtable with Vectors for all allowed relations
        // Key = TypeName for objects that may be linked

        Hashtable<String, Vector> relationTable = new Hashtable<String, Vector>();
        while (enumeration.hasMoreElements()) {
            typeRel = (MMObjectNode)enumeration.nextElement();
            int j=typeRel.getIntValue("snumber");
            if (j== node.getIntValue("otype")) {
                j=typeRel.getIntValue("dnumber");
            }
            if (j!=-1) {
                typeName = mmBase.getTypeDef().getValue(j);
                relationTable.put(typeName,new Vector());
            } else {
                log.warn("getRelationTable(): Problem on "+typeRel.toString());
            }

        }

        // Hashtable is done now fill it up !!
        // enumeration contains all objectnodes that are linked to the
        // currently edited Node.

        if (getEditNodeNumber()!=-1) {

            // is this the correct way to get Relations ???? my vote is no !
            // enumeration = mmBase.getInsRel().getRelations(getEditNodeNumber());

            enumeration = node.getRelations();

            MMObjectNode rel;
            MMObjectNode target;

            while (enumeration.hasMoreElements()) {
                try {
                    rel = (MMObjectNode)enumeration.nextElement();
                    if (rel.getIntValue("snumber") == node.getIntValue("number"))
                        target = mmObjectBuilder.getNode(rel.getIntValue("dnumber"));
                    else
                        target = mmObjectBuilder.getNode(rel.getIntValue("snumber"));
                    typeName = target.getBuilder().getTableName();
                    Vector<MMObjectNode> relList = relationTable.get(typeName);
                    if (relList != null) {
                        relList.addElement(target);
                        relList.addElement(rel);
                    } else {
                        log.warn("Relation ("+typeName+") defined, but typerel for this relation does not exist");
                    }
                } catch(Exception e) {
                    log.warn("getRelationTable(): Problem with a relation, probably a relation with a non active builder");
                }
            }
        } else {
            log.debug("getRelation(): EditNodeNumber is -1");
        }
        return relationTable;
    }
}
