/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;
/*
	$Id: TransactionResolver.java,v 1.2 2000-10-13 11:41:34 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.1  2000/08/14 19:19:05  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TransactionResolver.java,v 1.2 2000-10-13 11:41:34 vpro Exp $
 */
public class TransactionResolver {
	private String	_classname = getClass().getName();
	private boolean _debug=true;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	MMBase mmbase;

	public TransactionResolver(MMBase mmbase) {
		this.mmbase=mmbase;
	}

	public boolean resolve(Vector nodes) {
		return(resolve(nodes,false));
	}

	public boolean resolve(Vector nodes,boolean debug) {
		Hashtable numbers=new Hashtable();
		Hashtable nnodes=new Hashtable();
		MMObjectNode node;
		Integer neg=new Integer(-1);
		MMObjectBuilder bul;
		FieldDefs fd;
		String field,tmpfield,key;
		int state,number,ikey,dbtype,tmpstate;
		Vector v;
		boolean rtn=true;

		// Find all unique keys and store them in a hashtable to remap them later
		// Also store the nodes with which fields uses them.
		for (Enumeration e=nodes.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			bul=mmbase.getMMObject(node.getName());
			if (_debug) debug("builder "+node.getName()+" builder "+bul);
			for (Enumeration f=bul.getFields().elements();f.hasMoreElements();) {
				fd=(FieldDefs)f.nextElement();
				dbtype=fd.getDBType();
				if (_debug) debug("type "+dbtype+","+fd.getDBName()+","+fd.getDBState());
				if (dbtype==FieldDefs.TYPE_INTEGER) {
					state=fd.getDBState();
					if (state==FieldDefs.DBSTATE_PERSISTENT || state==FieldDefs.DBSTATE_SYSTEM) {
						// Database field of type integer
						field=fd.getDBName();
						tmpfield="_"+field;
						tmpstate=node.getDBState(tmpfield);
						if (tmpstate==FieldDefs.DBSTATE_VIRTUAL || tmpstate==FieldDefs.DBSTATE_UNKNOWN) {
							ikey=node.getIntValue(field);
							if (ikey<0) {
								// Key is not set
								key=node.getStringValue(tmpfield);
								if (key!=null) {
									if (_debug) debug("key,field "+field+" - "+key);
									// keep fieldnumber key
									if (!numbers.containsKey(key)) numbers.put(key,neg);
									// keep node + field to change
									v=(Vector)nnodes.get(node);
									if (v!=null) {
										v.addElement(field);
									} else {
										v=new Vector();
										v.addElement(field);
										nnodes.put(node,v);
									}
								} else {
									debug("Can't find key for field "+tmpfield+" node "+node+" (warning)");
								}
								if (field.equals("number")) node.setValue("_exists","no");
							} else {
								// Key is already set
								debug("Key for value "+field+" is already set "+ikey);
								// Mark it as existing
								// node.removeValue(tmpfield);
								if (field.equals("number")) node.setValue("_exists","yes");
							}
						} else {
							debug("DBstate for "+tmpfield+" is not set to 0 but is "+node.getDBState(field));
						}
					}
				}
			}
		}
		debug("numbers "+numbers);
		if (_debug) debug("nnodes "+nnodes);

		// Get the numbers
		number=0;
		for (Enumeration e=numbers.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			if (debug) {
				number++; // get real number later
			} else {
				number=mmbase.getDBKey();
			}
			numbers.put(key,new Integer(number));
		}

		// put numbers in the right place
		for (Enumeration e=nnodes.keys();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			v=(Vector)nnodes.get(node);
			for (Enumeration f=v.elements();f.hasMoreElements();) {
				field=(String)f.nextElement();
				tmpfield="_"+field;
				key=node.getStringValue(tmpfield);
				number=((Integer)numbers.get(key)).intValue();
				node.setValue(field,number);
			}
		}

		// Verify resolving stage
		for (Enumeration e=nodes.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			bul=mmbase.getMMObject(node.getName());
			for (Enumeration f=bul.getFields().elements();f.hasMoreElements();) {
				fd=(FieldDefs)f.nextElement();
				dbtype=fd.getDBType();
				if (dbtype==FieldDefs.TYPE_INTEGER) {
					field=fd.getDBName();
					number=node.getIntValue(field);
					if (number==-1) {
						tmpfield="_"+field;
						if (node.getDBState(tmpfield)==0) {
							key=node.getStringValue(tmpfield);
							if (key!=null && key.length()>0) {
								rtn=false;
							}
						}
					}
				}
			}
		}
		return(rtn);
	}
}
