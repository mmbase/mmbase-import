/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;

/**
 * EditState, controls the users edit session, keeps EditStateNodes
 *
 * @application SCAN - Removing this from Core requires changes in Module/MMObjectBuilder
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @version $Id: EditState.java,v 1.19 2007-06-21 15:50:23 nklasens Exp $
 */
public class EditState {

    /**
    * Logging instance
    */
    private static Logger log = Logging.getLoggerInstance(EditState.class.getName());

    private String user;

    Vector<EditStateNode> nodes=new Vector<EditStateNode>();
    EditStateNode curNode;
    MMBase mmBase;

    public EditState(String user,MMBase mmBase) {
        if( mmBase != null ) {
            this.mmBase=mmBase;
        } else {
            log.error("EditState("+mmBase+"): MMBase is not valid!");
        }
        if (user!=null) {
            this.user=user;
        } else {
            log.error("EditState("+user+"): User is not valid");
        }
    }

    public boolean pushState() {
        curNode=new EditStateNode(mmBase);
        nodes.addElement(curNode);
        return true;
    }

    public boolean clear() {
        nodes=new Vector<EditStateNode>();
        return true;
    }

    public boolean popState() {
        nodes.removeElement(curNode);
        curNode=nodes.lastElement();
        return true;
    }

    public Vector<EditStateNode> getEditStates() {
        return nodes;
    }

    public boolean setSearchValue(String fieldname,Object value) {
        boolean result = false;

        if( fieldname != null )
            if( !fieldname.equals("") )
                result = curNode.setSearchValue(fieldname,value);
            else
                log.error("setSearchValue("+fieldname+","+value+"): fieldname is empty!");
        else
            log.error("setSearchValue("+fieldname+","+value+"): fieldname is null!");

        return result;
    }

    public String getSearchValue(String name) {
        String result = null;

        if(name!=null)
            if( !name.equals("") )
                result = curNode.getSearchValue(name);
            else
                log.error("getSeachValue("+name+"): name is empty!");
        else
            log.error("getSeachValue("+name+"): name is null!");

        return result;
    }

    public Hashtable<String, Object> getSearchValues() {
        return curNode.getSearchValues();
    }

    public void clearSearchValues() {
        curNode.clearSearchValues();
    }

    public boolean setHtmlValue(String fieldname,Object value) {
        boolean result = false;

        if( fieldname != null )
            if( !fieldname.equals("") )
                result = curNode.setHtmlValue(fieldname,value);
            else
                log.error("setHtmlValue("+fieldname+","+value+"): fieldname is !");
        else
            log.error("setHtmlValue("+fieldname+","+value+"): fieldname is null!");

        return result;
    }

    public String getHtmlValue(String name) {
        String result = null;

        if( name != null )
            if( !name.equals("") )
                result = curNode.getHtmlValue(name);
            else
                log.error("getHtmlValue("+name+"): name is empty!");
        else
            log.error("getHtmlValue("+name+"): name is null!");

        return result;
    }

    public Hashtable<String, Object> getHtmlValues() {
        return curNode.getHtmlValues();
    }

    public void clearHtmlValues() {
        curNode.clearHtmlValues();
    }

    public void setEditNode(String number,String userName) {
        if( number != null )
            if(!number.equals(""))
                if(userName!=null)
                    if(!userName.equals("")) {
                        delInsSaveList();
                        curNode.setEditNode(number,userName);
                    } else
                        log.error("setEditNode("+number+","+userName+"): username is empty!");
                else
                    log.error("setEditNode("+number+","+userName+"): username is null!");
            else
                log.error("setEditNode("+number+","+userName+"): number is empty!");
        else
            log.error("setEditNode("+number+","+userName+"): number is null!");
    }

    public MMObjectNode getEditNode() {
        if (curNode == null) return null;
        return curNode.getEditNode();
    }

    public MMObjectNode getEditNode(int i) {
        int pos=nodes.indexOf(curNode);
        if ((pos-i)<0) return null;
        EditStateNode node=nodes.elementAt(pos-i);
        if (node==null) {
            return null;
        } else {
            return node.getEditNode();
        }
    }

    public EditStateNode getEditStateNode(int i) {
        int pos=nodes.indexOf(curNode);
        if ((pos-i)<0) return null;
        EditStateNode node=nodes.elementAt(pos-i);
        if (node==null) {
            return null;
        } else {
            return node;
        }
    }

    public void NewNode(String owner) {
        curNode.getNewNode(owner);
        delInsSaveList();
    }

    public void removeNode() {
        curNode.removeNode();
    }

    public void removeRelations() {
        curNode.removeRelations();
    }

    public void removeEd() {
        curNode.removeNode();
    }

    public int getEditNodeNumber() {
        return curNode.getEditNodeNumber();
    }

    public int getEditNodeSrcNumber() {
        MMObjectNode snode=getEditSrcNode();
        if (snode!=null) {
            return snode.getIntValue("number");
        }
        return -1;
    }

    public int getEditNodeDstNumber() {
        MMObjectNode snode=getEditDstNode();
        if (snode!=null) {
            return snode.getIntValue("number");
        }
        return -1;
    }

    public String getEditNodeSrcDutchName() {
        MMObjectNode snode=getEditSrcNode();
        if (snode!=null) {
            String dname=snode.getBuilder().getSingularName();
            if (dname!=null) return dname;
        }
        return "";
    }

    public String getEditNodeDstGuiIndicator() {
        MMObjectNode dnode=getEditDstNode();
        if (dnode!=null) {
            String dgui=dnode.getGUIIndicator();
            if (dgui!=null) return dgui;
        }
        return "";
    }

    public String getEditNodeSrcGuiIndicator() {
        MMObjectNode snode=getEditSrcNode();
        if (snode!=null) {
            String sgui=snode.getGUIIndicator();
            if (sgui!=null) return sgui;
        }
        return "";
    }

    public String getEditNodeDstDutchName() {
        MMObjectNode snode=getEditDstNode();
        if (snode!=null) {
            String dname=snode.getBuilder().getSingularName();
            if (dname!=null) return dname;
        }
        return "";
    }

    public String getEditNodeSrcName() {
        MMObjectNode snode=getEditSrcNode();
        if (snode!=null) {
            String dname=snode.getName();
            if (dname!=null) return dname;
        }
        return "";
    }

    public String getEditNodeDstName() {
        MMObjectNode snode=getEditDstNode();
        if (snode!=null) {
            String dname=snode.getName();
            if (dname!=null) return dname;
        }
        return "";
    }

    public MMObjectNode getEditSrcNode() {
        return curNode.getEditSrcNode();
    }

    public MMObjectNode getEditDstNode() {
        return curNode.getEditDstNode();
    }

    public void setBuilder(String name) {
        if( name != null ) {
            if( !name.equals("") ) {
                pushState();
                curNode.setBuilder(name);
            } else log.error("setBuilder("+name+"): name is empty!");
        } else log.error("setBuilder("+name+"): name is null!");
    }

    public String getBuilderName() {
        String result= null;
        if( curNode != null )
            result = curNode.getBuilderName();
        else
            log.error("getBuilderName(): curNode("+curNode+") is null!");

        return result;
    }

    public MMObjectBuilder getBuilder() {
        return curNode.getBuilder();
    }

    public void setSelectionQuery(String query) {
        curNode.setSelectionQuery(query);
    }

    public String getSelectionQuery() {
        return curNode.getSelectionQuery();
    }

    public boolean isChanged() {
        return curNode.isChanged();
    }

    /**
     * Add a relation (insrel) to the cloud.
     * Does not change the editstate.
     * @vpro 14 is a hardcoded value for the vpro reldef type
     * @deprecated-now this code should not be called
     */
    public boolean addRelation(String owner) {
        boolean result=false;
        // relations are not saved by themself but saved or dropped
        // by there caller !!
        int pos=nodes.indexOf(curNode);
        int src=curNode.getEditNodeNumber();
        EditStateNode node2=nodes.elementAt(pos-1);
        if (node2!=null) {
            log.debug("addRelation("+owner+"): Create relation from "+node2.getEditNodeNumber()+" to "+src+" reltype 2");
            mmBase.getInsRel().insert(owner,node2.getEditNodeNumber(),src,14);
            result=true;
        } else {
            log.error("addRelation("+owner+"): src("+src+"), pos("+pos+"), cannot create relation from "+node2+" to "+src+" reltype 2");
        }
        return result;
    }

    public boolean getInsSave() {
        return curNode.getInsSave();
    }

    public void setInsSave(boolean set) {
        curNode.setInsSave(set);
    }

    public Vector<MMObjectNode> getInsSaveList() {
        return curNode.getInsSaveList();
    }

    public void delInsSaveList() {
        if (curNode!=null) curNode.delInsSaveList();
    }

    public Hashtable<String, Vector> getRelationTable() {
        return curNode.getRelationTable();
    }

    public String getLanguage() {
        return mmBase.getLanguage();
    }

    public String getUser() {
        return user;
    }
}
