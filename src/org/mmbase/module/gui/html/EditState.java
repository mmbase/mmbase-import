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

	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); } 

	Vector nodes=new Vector();
	EditStateNode curNode;
	MMBase mmBase;
	
	public EditState(MMBase mmBase) {
		if( mmBase != null )
		{
			this.mmBase=mmBase;
			//pushState();
		}
		else
			debug("EditState("+mmBase+"): ERROR: MMBase is not valid!");
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
		boolean result = false;

		if( fieldname != null )
			if( !fieldname.equals("") )
				result = curNode.setSearchValue(fieldname,value);
			else
				debug("setSearchValue("+fieldname+","+value+"): ERROR: fieldname is empty!");
		else
			debug("setSearchValue("+fieldname+","+value+"): ERROR: fieldname is null!");
		
		return result;
	}

	public String getSearchValue(String name) {
		String result = null;

		if(name!=null)
			if( !name.equals("") )
				result = curNode.getSearchValue(name);
			else
				debug("getSeachValue("+name+"): ERROR: name is empty!");
		else
			debug("getSeachValue("+name+"): ERROR: name is null!");

		return result;
	}

	public Hashtable getSearchValues() {
		return (curNode.getSearchValues());
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
				debug("setHtmlValue("+fieldname+","+value+"): ERROR: fieldname is !");
		else
			debug("setHtmlValue("+fieldname+","+value+"): ERROR: fieldname is null!");
		
		return result;
	}

	public String getHtmlValue(String name) {
		String result = null;
		
		if( name != null )
			if( !name.equals("") )	
				result = curNode.getHtmlValue(name);
			else
				debug("getHtmlValue("+name+"): ERROR: name is empty!");
		else
			debug("getHtmlValue("+name+"): ERROR: name is null!");

		return result;
	}

	public Hashtable getHtmlValues() {
		return (curNode.getHtmlValues());
	}

	public void clearHtmlValues() {
		curNode.clearHtmlValues();
	}

	public void setEditNode(String number,String userName) {
		if( number != null )
			if(!number.equals(""))
				if(userName!=null)
					if(!userName.equals(""))
					{
						delInsSaveList();
						curNode.setEditNode(number,userName);
					}
					else
						debug("setEditNode("+number+","+userName+"): ERROR: username is empty!");
				else
					debug("setEditNode("+number+","+userName+"): ERROR: username is null!");
			else
				debug("setEditNode("+number+","+userName+"): ERROR: number is empty!");
		else
			debug("setEditNode("+number+","+userName+"): ERROR: number is null!");
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
		if( name != null )
		{
			if( !name.equals("") )
			{
				//if( curNode != null )
				{
					pushState();
					curNode.setBuilder(name);
				} //else debug("setBuilder("+name+"): ERROR: curNode is null!");
			} else debug("setBuilder("+name+"): ERROR: name is empty!");
		} else debug("setBuilder("+name+"): ERROR: name is null!");
	}

	public String getBuilderName() {
		String result= null;
		if( curNode != null )
			result = curNode.getBuilderName();
		else
			debug("getBuilderName(): ERROR: curNode("+curNode+") is null!");

		return result;
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
			debug("addRelation("+owner+"): Create relation from "+node2.getEditNodeNumber()+" to "+src+" reltype 2");
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
