/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.lang.Exception;

import org.mmbase.util.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.RelDef;
/*
	$Id: TemporaryNodeManager.java,v 1.15 2001-03-02 13:56:44 install Exp $

	$Log: not supported by cvs2svn $
	Revision 1.14  2001/01/08 12:31:58  install
	Rob: fixed bug 5180, added check for valid relationname
	
	Revision 1.13  2000/12/30 14:06:56  daniel
	turned debug off again (please no debug turned on in cvs, some people have this in production and go nuts with debug
	
	Revision 1.12  2000/11/13 15:33:47  vpro
	Rico: added relation support, note that this must be changed when the whole relation mess changes
	
	Revision 1.11  2000/11/13 11:09:41  install
	*** empty log message ***
	
	Revision 1.10  2000/11/08 16:24:13  vpro
	Rico: fixed key bussiness
	
	Revision 1.9  2000/11/08 16:11:52  vpro
	Rico: added temporary key method
	
	Revision 1.8  2000/11/08 14:46:29  vpro
	Rico: added splitting into datatypes
	
	Revision 1.7  2000/11/08 14:31:23  vpro
	Rico: returns right keys
	
	Revision 1.6  2000/11/08 14:24:46  vpro
	Rico: fixed getObject
	
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
 * @version $Id: TemporaryNodeManager.java,v 1.15 2001-03-02 13:56:44 install Exp $
 */
public class TemporaryNodeManager implements TemporaryNodeManagerInterface {
	private String	_classname = getClass().getName();
	private boolean _debug=false;
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
			node=builder.getNewTmpNode(owner,getTmpKey(owner,key));
			if (_debug) debug("New tmpnode "+node);
		} else {
			debug("Can't find builder "+type);
		}
		return(key);
	}

	public String createTmpRelationNode(String type,String owner,String key, String source,String destination) throws Exception {
		String bulname="";
		MMObjectNode node=null;
		MMObjectBuilder builder=null;
		RelDef reldef;
		int rnumber;
		
		// decode type to a builder using reldef
		reldef=(RelDef)mmbase.getMMObject("reldef");
		rnumber=reldef.getGuessedByName(type);
		if(rnumber==-1) {
			throw new Exception("type "+type+" is not a proper relation");
		}
		builder=mmbase.getMMObject(type);
		if (builder==null) builder=mmbase.getMMObject("insrel");
		bulname=builder.getTableName();

		// Create node
		createTmpNode(bulname,owner,key);
		builder.checkAddTmpField("_snumber");
		builder.checkAddTmpField("_dnumber");
		setObjectField(owner,key,"_snumber",getTmpKey(owner,source));
		setObjectField(owner,key,"_dnumber",getTmpKey(owner,destination));
		setObjectField(owner,key,"rnumber",""+rnumber);
		return(key);
	}

	public String deleteTmpNode(String owner,String key) {
		MMObjectBuilder b=mmbase.getMMObject("typedef");
		b.removeTmpNode(getTmpKey(owner,key));
		if (_debug) debug("delete node "+getTmpKey(owner,key));
		return(key);
	}

	public MMObjectNode getNode(String owner,String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(getTmpKey(owner,key));
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
		node=bul.getTmpNode(getTmpKey(owner,key));
		if (node==null) {
			if (_debug) debug("getObject not tmp node found "+key);
			node=bul.getNode(dbkey);
			if (node==null) {
				debug("Node not found in database "+dbkey);
			} else {
				bul.putTmpNode(getTmpKey(owner,key),node);
			}
		}
		if (node != null) {
			return(key);
		} else {
			return null;
		}
	}

	public String setObjectField(String owner,String key,String field,Object value) {
		MMObjectNode node;
		int i;float f;double d;long l;
		String stringValue;

		// Memo next can be done by new MMObjectNode.setValue
		node=getNode(owner,key);
		if (node!=null) {
			int type=node.getDBType(field);
			if (type>=0) {
				if (value instanceof String) {
					stringValue=(String)value;
					switch(type) {
						case FieldDefs.TYPE_STRING:
							node.setValue(field, stringValue);
							break;
						case FieldDefs.TYPE_INTEGER:
							try {
								i=Integer.parseInt(stringValue);
								node.setValue(field,i);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_BYTE:
							debug("We don't support casts from String to Byte");
							break;
						case FieldDefs.TYPE_FLOAT:
							try {
								f=Float.parseFloat(stringValue);
								node.setValue(field,f);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_DOUBLE:
							try {
								d=Double.parseDouble(stringValue);
								node.setValue(field,d);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						case FieldDefs.TYPE_LONG:
							try {
								l=Long.parseLong(stringValue);
								node.setValue(field,l);
							} catch (NumberFormatException x) {
								debug("Value for field "+field+" is not a number "+stringValue);
							}
							break;
						default:
							debug("Unknown type for field "+field);
							break;
					}
				} else {
					node.setValue(field,value);
				}
			} else {
				node.setValue(field,value);
//				debug("Invalid type for field "+field);
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

	private String getTmpKey(String owner,String key) {
		return(owner+"_"+key);
	}
}
