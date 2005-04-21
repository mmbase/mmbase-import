/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.gui.html;

import java.util.*;
import java.sql.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;

/**
 * EditState, controls the users edit session, keeps EditStatesNodes
 * (hitlisted)
 *
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 */
public class EditState {
	Vector nodes=new Vector();
	EditStateNode curNode;
	MMBase mmBase;
	
	public EditState(MMBase mmBase) {
		this.mmBase=mmBase;
		//pushState();
	}

	public boolean pushState() {
		curNode=new EditStateNode(mmBase);		
		nodes.addElement(curNode);
		return(true);
	}

	public boolean clear() {
		nodes=new Vector();
		return(true);
	}

	public boolean popState() {
		nodes.removeElement(curNode);	
		curNode=(EditStateNode)nodes.lastElement();		
		return(true);
	}

	public Vector getEditStates() {
		return(nodes);
	}

	public boolean setSearchValue(String fieldname,Object value) {
		return(curNode.setSearchValue(fieldname,value));
	}

	public String getSearchValue(String name) {
		return(curNode.getSearchValue(name));
	}

	public Hashtable getSearchValues() {
		return (curNode.getSearchValues());
	}

	public void clearSearchValues() {
		curNode.clearSearchValues();
	}


	public boolean setHtmlValue(String fieldname,Object value) {
		return(curNode.setHtmlValue(fieldname,value));
	}

	public String getHtmlValue(String name) {
		return(curNode.getHtmlValue(name));
	}

	public Hashtable getHtmlValues() {
		return (curNode.getHtmlValues());
	}

	public void clearHtmlValues() {
		curNode.clearHtmlValues();
	}

	public void setEditNode(String number,String userName) {
		delInsSaveList();
		curNode.setEditNode(number,userName);
	}

	public MMObjectNode getEditNode() {
		if (curNode == null) return null;

		return(curNode.getEditNode());
	}



	public MMObjectNode getEditNode(int i) {
		int pos=nodes.indexOf(curNode);		
		EditStateNode node=(EditStateNode)nodes.elementAt(pos-i);
		if (node==null) {
			return(null);
		} else {
			return(node.getEditNode());
		}
	}

	public EditStateNode getEditStateNode(int i) {
		int pos=nodes.indexOf(curNode);		
		EditStateNode node=(EditStateNode)nodes.elementAt(pos-i);
		if (node==null) {
			return(null);
		} else {
			return(node);
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
		return(curNode.getEditNodeNumber());
	}

	public int getEditNodeSrcNumber() {
		MMObjectNode snode=getEditSrcNode();
		if (snode!=null) {
			return(snode.getIntValue("number"));
		}
		return(-1);
	}

	public int getEditNodeDstNumber() {
		MMObjectNode snode=getEditDstNode();
		if (snode!=null) {
			return(snode.getIntValue("number"));
		}
		return(-1);
	}

	public String getEditNodeSrcDutchName() {
		MMObjectNode snode=getEditSrcNode();
		if (snode!=null) {
			String dname=snode.getDutchSName();
			if (dname!=null) return(dname);
		}
		return("");
	}


	public String getEditNodeDstGuiIndicator() {
		MMObjectNode dnode=getEditDstNode();
		if (dnode!=null) {
			String dgui=dnode.getGUIIndicator();
			if (dgui!=null) return(dgui);
		}
		return("");
	}


	public String getEditNodeSrcGuiIndicator() {
		MMObjectNode snode=getEditSrcNode();
		if (snode!=null) {
			String sgui=snode.getGUIIndicator();
			if (sgui!=null) return(sgui);
		}
		return("");
	}


	public String getEditNodeDstDutchName() {
		MMObjectNode snode=getEditDstNode();
		if (snode!=null) {
			String dname=snode.getDutchSName();
			if (dname!=null) return(dname);
		}
		return("");
	}


	public String getEditNodeSrcName() {
		MMObjectNode snode=getEditSrcNode();
		if (snode!=null) {
			String dname=snode.getName();
			if (dname!=null) return(dname);
		}
		return("");
	}


	public String getEditNodeDstName() {
		MMObjectNode snode=getEditDstNode();
		if (snode!=null) {
			String dname=snode.getName();
			if (dname!=null) return(dname);
		}
		return("");
	}

	public MMObjectNode getEditSrcNode() {
		return(curNode.getEditSrcNode());
	}

	public MMObjectNode getEditDstNode() {
		return(curNode.getEditDstNode());
	}

	public void setBuilder(String name) {
		pushState();
		curNode.setBuilder(name);
	}

	public String getBuilderName() {
		return (curNode.getBuilderName());
	}

	public MMObjectBuilder getBuilder() {
		return (curNode.getBuilder());
	}

	public void setSelectionQuery(String query) {
		curNode.setSelectionQuery(query);
	}

	public String getSelectionQuery() {
		return (curNode.getSelectionQuery());
	}

	public boolean isChanged() {
		return (curNode.isChanged());
	}

	public boolean addRelation(String owner) {
		// relations are not saved by themself but saved or dropped
		// by there caller !!
		int pos=nodes.indexOf(curNode);		
		int src=curNode.getEditNodeNumber();
		EditStateNode node2=(EditStateNode)nodes.elementAt(pos-1);
		if (node2!=null) {
			System.out.println("EditState -> Create relation from "+node2.getEditNodeNumber()+" to "+src+" reltype 2");
		}
		mmBase.getInsRel().insert(owner,node2.getEditNodeNumber(),src,14);
		return(true);
	}

	public boolean getInsSave() {
		return (curNode.getInsSave());
	}

	public void setInsSave(boolean set) {
		curNode.setInsSave(set);
	}

	public Vector getInsSaveList() {
		return(curNode.getInsSaveList());
	}

	public void delInsSaveList() {
		if (curNode!=null) curNode.delInsSaveList();
	}

	public Hashtable getRelationTable() {
		return(curNode.getRelationTable());
	}

}
