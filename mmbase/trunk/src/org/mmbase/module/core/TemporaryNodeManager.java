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
	$Id: TemporaryNodeManager.java,v 1.6 2000-11-08 14:24:46 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.5  2000/11/08 13:24:19  vpro
	Rico: included owner in operations
	
	Revision 1.4  2000/10/26 13:10:37  vpro
	Rico: fixed b0rken uncompilable code
	
	Revision 1.3  2000/10/13 11:41:34  vpro
	Rico: made it working
	
	Revision 1.2  2000/10/13 09:39:54  vpro
	Rico: added a method
	
	Revision 1.1  2000/08/14 19:19:06  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManager.java,v 1.6 2000-11-08 14:24:46 vpro Exp $
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
			node=builder.getNewTmpNode(owner,makeKey(owner,key));
			if (_debug) debug("New tmpnode "+node);
		} else {
			debug("Can't find builder "+type);
		}
		return(owner+key);
	}

	public String deleteTmpNode(String owner,String key) {
		MMObjectBuilder b=mmbase.getMMObject("typedef");
		b.removeTmpNode(makeKey(owner,key));
		if (_debug) debug("delete node "+makeKey(owner,key));
		return(key);
	}

	public MMObjectNode getNode(String owner,String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(makeKey(owner,key));
		// fallback to normal nodes
		if (node==null) {
			if (_debug) debug("getNode tmp not node found "+key);
			node=bul.getNode(key);
		}
		return(node);
	}

	public String getObject(String owner,String key,String dbkey) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(makeKey(owner,key));
		if (node==null) {
			if (_debug) debug("getObject not tmp node found "+key);
			node=bul.getNode(dbkey);
			if (node==null) {
				debug("Node not found in database "+dbkey);
			} else {
				bul.putTmpNode(makeKey(owner,key),node);
			}
		}
		if (node != null) {
			return(makeKey(owner,key));
		} else {
			return null;
		}
	}

	public String setObjectField(String owner,String key,String field,Object value) {
		MMObjectNode node;

		// Memo next can be done by new MMObjectNode.setValue
		node=getNode(owner,key);
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


	public String getObjectFieldAsString(String owner,String key,String field) {
		String rtn;
		MMObjectNode node;
		node=getNode(owner,key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

	public Object getObjectField(String owner,String key,String field) {
		Object rtn;
		MMObjectNode node;
		node=getNode(owner,key);
		if (node==null) {
			debug("getObjectFieldAsString(): node "+key+" not found!");
			rtn="";
		} else {
			rtn=node.getValueAsString(field);
		} 
		return(rtn);
	}

	private String makeKey(String owner,String key) {
		return(owner+"_"+key);
	}
}
