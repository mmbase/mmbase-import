/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
	$Id: MMContainerCompare.java,v 1.1 2000-05-23 11:47:00 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.util.*;

/**
 * Class to compare two MMObjectNodes, used by SortedVector.
 * @see vpro.james.util.SortedVector
 * @see vpro.james.util.CompareInterface
 *
 * @author Rico Jansen
 * @version $Id: MMContainerCompare.java,v 1.1 2000-05-23 11:47:00 wwwtech Exp $
 */
public class MMContainerCompare implements CompareInterface {
public final static boolean ASC=true;
public final static boolean DESC=false;
private Vector orderfields;
private Vector orderdirections;
private Hashtable dataCompare=new Hashtable();
private StringCompare strcompare=new StringCompare();
private IntegerCompare intcompare=new IntegerCompare();

private String	_classname = getClass().getName();
private boolean debug=false;
private void debug(String msg) { System.out.println( _classname +":"+ msg ); }

	public MMContainerCompare() {
		orderfields=new Vector();
		orderdirections=new Vector();
	}

	public MMContainerCompare(Vector orderfields) {
		String field;

		orderdirections=new Vector();
		this.orderfields=orderfields;
		for (Enumeration e=orderfields.elements();e.hasMoreElements();) {
			field=(String)e.nextElement();
			orderdirections.addElement(new Boolean(ASC));
		}
	}

	public MMContainerCompare(Vector orderfields,Vector orderdirections) {
		this.orderfields=orderfields;
		this.orderdirections=orderdirections;
	}

	public void setCompare(String field,CompareInterface handler) {
		dataCompare.put(field,handler);
	}

	public void addField(String field) {
		addField(field,ASC);
	}

	public void addField(String field,boolean direction) {
		orderfields.addElement(field);
		orderdirections.addElement(new Boolean(direction));
	}

	public boolean setField(String field,boolean direction) {
		int i;
		boolean rtn=false;

		i=orderfields.indexOf(field);
		if (i!=-1) {
			orderdirections.setElementAt(new Boolean(direction),i);
			rtn=true;
		} else {
			rtn=false;
		}
		return(rtn);
	}

	public void setOrderFields(Vector orderfields) {
		this.orderfields=orderfields;
	}

	public void setOrderDirections(Vector orderdirections) {
		this.orderdirections=orderdirections;
	}

	public void setOrder(Vector orderfields,Vector orderdirections) {
		this.orderfields=orderfields;
		this.orderdirections=orderdirections;
	}


	/** 
	 * The compare function called by SortedVector to sort things
	 * @see vpro.james.util.SortedVector
	 * @see vpro.james.util.CompareInterface
	 *
	 * this<other return -1 or number<0;
	 * this>other return 1 or number>0;
	 * this==other return 0;
	 */
	public int compare(Object thisone,Object other) {
		MMObjectNode thisnode,othernode;
		int rtn=0;
		int i;
		String field;
		Object thisdata;
		Object otherdata;
		CompareInterface comp;

		thisnode=(MMObjectNode)thisone;
		othernode=(MMObjectNode)other;
		
		i=0;
		for (Enumeration e=orderfields.elements();e.hasMoreElements();i++) {
			field=(String)e.nextElement();
			thisdata=thisnode.getValue(field);
			otherdata=othernode.getValue(field);
			// Handle null data cases
			if (thisdata!=null) {
				if (otherdata!=null) {
					// data ? data
					comp=(CompareInterface)dataCompare.get(field);
					if (comp==null) {
						if (thisdata instanceof String) {
							comp=strcompare;
						} else if (thisdata instanceof Integer) {
							comp=intcompare;
						} else {
							debug("no handler for field "+field);
							comp=null;
							rtn=0;
						}
					}
					if (comp!=null) {
						rtn=comp.compare(thisdata,otherdata);
					}
				} else {
					// data < null
					rtn=-1;
				}
			} else {
				if (otherdata!=null) {
					// null > data
					rtn=1;
				} else {
					// null == null 
					rtn=0;
				}
			}
			if (((Boolean)orderdirections.elementAt(i)).booleanValue()==DESC) {
				rtn=0-rtn;
			}
			// No need to compare further if they are unequal
			if (rtn!=0) break;
		}
		return(rtn);
	}
}
