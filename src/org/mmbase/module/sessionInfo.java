/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class sessionInfo {

	private String classname = getClass().getName();
	private boolean debug = false;

	private String hostname;
	private String cookie;
	private MMObjectNode node;

	Hashtable values = new Hashtable();
	Hashtable setvalues = new Hashtable();

	public void setNode(MMObjectNode node) {
		this.node=node;
	}

	public MMObjectNode getNode() {
		return(node);
	}
	
	public String getCookie() {
		return(cookie);
	}

	public String getValue(String wanted) {
		return((String)values.get(wanted));
	}

	public String setValue(String key,String value) {
		return((String)values.put(key,value));
	}

	/**
	* adds a value to a set, no duplicates are allowed.
	*/
	public void addSetValue(String key,String value) {

		if( debug ) debug("addSetValue("+key+","+value+")");

		Vector v=(Vector)setvalues.get(key);
		if (v==null) {
			// not found so create it
			v=new Vector();
			v.addElement(value);
			setvalues.put(key,v);
			if( debug ) debug("sessionset="+v.toString());	
		} else {
			if (!v.contains(value)) {
				v.addElement(value);
				if( debug ) debug("sessionset="+v.toString());	
			}
		}
		if( debug ) debug("addSetValue() -> getSetString("+key+"): " +getSetString(key));
	}


	/**
	* add a value to a set, duplicates are allowed.
	*/
	public void putSetValue(String key,String value) {

		if( debug ) debug("putSetValue("+key+","+value+")");

		Vector v=(Vector)setvalues.get(key);
		if (v==null) {
			// not found so create it
			v=new Vector();
			v.addElement(value);
			setvalues.put(key,v);
			if( debug ) debug("sessionset="+v.toString());	
		} else {
			v.addElement(value);
			if( debug ) debug("sessionset="+v.toString());	
		}
	}


	/**
	* deletes a value from the SESSION set.
	*/
	public void delSetValue(String key,String value) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			if (v.contains(value)) {
				v.removeElement(value);
				if( debug ) debug("sessionset="+v.toString());	
			}
		}
	}


	/**
	* does this set contain the value ?
	*/
	public String containsSetValue(String key,String value) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			if (v.contains(value)) {
				return("YES");
			}
		}
		return("NO");
	}


	/**
	* delete the values belonging to the key
	*/
	public String clearSet(String key) {
		if( debug ) debug("sessionset="+key);	
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			v=new Vector();
			setvalues.put(key,v);
			if( debug ) debug("sessionset="+v.toString());	
		}
		return("");
	}


	/**
	* returns the session variable values comma separaterd
	* @param key the name of the session variable 
	*/
	public String getSetString(String key) {

		if( debug ) debug("getSetString("+key+")");

		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			String result="";
			Enumeration res=v.elements();
			while (res.hasMoreElements()) {
				String tmp=(String)res.nextElement();
				if (result.equals("")) {
					result=tmp;
				} else {
					result+=","+tmp;
				}
			}
			return(result);
		} else {
			if( debug ) debug("getSetString("+key+"): ERROR: this key is non-existent!");
			return(null);
		}
	}

	/**
	* return the number of values contained by a session variable
	*/
	public String getSetCount(String key) {

		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			return(""+v.size());
		} else {
			return(null);
		}
	}


	/**
	* return the average of a set of numbers
	*/
	public String getAvgSet(String key) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			int total=0;
			int count=0;
			Enumeration res=v.elements();
			while (res.hasMoreElements()) {
				try {
					String tmp=(String)res.nextElement();
					int tmpi=Integer.parseInt(tmp);
					total+=tmpi;
					count++;
				} catch(Exception e) {}
			}
			int res1=total/count;
			return(""+res1);
		} else {
			return(null);
		}
	}

	/**	
	 * returns the hostname of a user
	 */
	public String getHostName() {
		return(hostname);
	}

	public sessionInfo(String hostname, String cookie) {
		this.hostname=hostname;
		this.cookie=cookie;
	}

	public sessionInfo(String hostname) {
		this.hostname=hostname;
	}

	private void debug( String msg )
	{
		System.out.println( "("+this+")"+ classname +": ["+cookie+"] "+ msg );
	}

	public String toString() {
		return("sessionInfo="+values.toString());
	}
}

