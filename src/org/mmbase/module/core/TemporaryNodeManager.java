/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.util.*;
/*
	$Id: TemporaryNodeManager.java,v 1.3 2000-10-13 11:41:34 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.2  2000/10/13 09:39:54  vpro
	Rico: added a method
	
	Revision 1.1  2000/08/14 19:19:06  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManager.java,v 1.3 2000-10-13 11:41:34 vpro Exp $
 */
public class TemporaryNodeManager implements TemporaryNodeManagerInterface {
	private String	_classname = getClass().getName();
	private boolean _debug=true;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private MMBase mmbase;

	public TemporaryNodeManager(MMBase mmbase) {
		this.mmbase=mmbase;
	}

	public String createTmpNode(String type,String owner,String key) {
		if (_debug) debug("createTmpNode : type="+type+" owner="+owner+" key="+key);
		if (owner.length()>12) owner=owner.substring(0,12);
		MMObjectBuilder builder=mmbase.getMMObject(type);
		MMObjectNode node;
		if (builder!=null) {
			node=builder.getNewTmpNode(owner,owner+key);
			if (_debug) debug("New tmpnode "+node);
		} else {
			debug("Can't find builder "+type);
		}
		return(owner+key);
	}

	public String deleteTmpNode(String key) {
		MMObjectBuilder b=mmbase.getMMObject("typedef");
		b.removeTmpNode(key);
		if (_debug) debug("delete node "+key);
		return(key);
	}

	public MMObjectNode getNode(String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(key);
		// fallback to normal nodes
		if (node==null) {
			if (_debug) debug("getNode tmp not node found "+key);
			bul.getNode(key);
		}
		return(node);
	}

	/*
	 * added JohnB, 3MPS, 11/10/2000
	 *
	 */
	 public String getObject(String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(key);
		// fallback to normal nodes
		if (node==null) {
			if (_debug) debug("getObject not tmp node found "+key);
			bul.getNode(key);
		}
		if (node != null) {
			return(key);
		} else {
			return null;
		}
	}

	public String setObjectField(String key,String field,Object value) {
		MMObjectNode node;

		// Memo next can be done by new MMObjectNode.setValue
		node=getNode(key);
		if (node!=null) {
			int type=node.getDBType(field);
			if (type>=0) {
				node.setValue(field, value);
			} else {
				debug("Invalid type for field "+field);
			}
		} else {
			debug("setObjectField(): Can't find node : "+key);
		}
		return("");
	}


	public String getObjectFieldAsString(String key,String field) {
		String rtn;
		MMObjectNode node;
		node=getNode(key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

	public Object getObjectField(String key,String field) {
		Object rtn;
		MMObjectNode node;
		node=getNode(key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

}
