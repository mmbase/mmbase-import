/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;
 

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;


/**
 * Simple file cache system that can be used by any servlet
 */
public class cache extends Module implements cacheInterface {

	boolean state_up = false;
	Hashtable lines = new Hashtable();
	int hits,miss;
	private int MaxLines=1000;
	private int MaxSize=100*1024;
	private boolean active=true;

	public void onload() {
	}

	public void reload() {
		readParams();
	}

	public void shutdown() {
	}


	/**
	 * Simple file cache system that can be used by any servlet
	 */
	public cache() {
	}

	/**
	* old interface to the inner table, will be removed soon
	*/
	public Hashtable lines() {
		return(lines);
	}

	/**
	* try to get a cacheline from cache, returns null if not found
	*/
	public cacheline get(Object wanted) {
		if (!active) return(null);
		cacheline o=(cacheline)lines.get(wanted);
		if (o==null) {
			//System.out.println("WOW CACHE MIS = "+wanted);
			miss++;
		} else {
			//System.out.println("WOW CACHE HIT = "+wanted);
			hits++;
		}
		return(o);
	}

	/**
	* try to put a cacheline in cache, returns old one if available
	* in all other cases returns null.
	*/
	public cacheline put(Object key,Object value) {
		if (!active) return(null);
		// check if there is still room in the cache
		if (lines.size()<MaxLines) {
			// there is room so look at the cacheline and check size
			cacheline line=(cacheline)value;
			// if size is to big ignore the entry
			if (line.filesize<MaxSize) {	
				// cacheline is oke place it in cache
				return((cacheline)lines.put(key,value));
			} else {
				// cacheline to big
				return(null);
			}
		} else {
			// to many lines in cache allready
			return(null);
		}
	}
	
	/**
	* Clear the whole cache in one go
	*/
	public boolean clear() {
		lines.clear();
		return(false);
	}

	public void init() {
		if (!state_up) {
			state_up=true;
		}
		readParams();
	}

	public void unload() {
	}

	public Hashtable state() {
		state.put("Hits",""+hits);
		state.put("Misses",""+miss);
		if (hits!=0 && miss!=0) {
			state.put("Cache hits %",""+((hits+miss)*100)/hits);
			state.put("Cache misses %",""+((hits+miss)*100)/miss);
		}
		state.put("Number cachelines",""+lines.size());
		cacheline line;
		int size=0;
		for (Enumeration t=lines.elements();t.hasMoreElements();) {
			line=(cacheline)t.nextElement();
			size+=line.filesize;
		}
		state.put("Cache Size (in kb)",""+(size+1)/1024);
		return(state);
	}
	
	/** 
	* maintainance call, will be called by the admin to perform managment
	* tasks. This can be used instead of its own thread.
	*/
	public void maintainance() {
		// if number of cachelines bigger than allowed delete some
		Enumeration t=lines.keys();
		while (lines.size()>MaxLines && t.hasMoreElements()) {
			lines.remove(t.nextElement());
		}
	}

	void readParams() {
		String tmp=getInitParameter("MaxLines");
		if (tmp!=null) MaxLines=Integer.parseInt(tmp);
		tmp=getInitParameter("MaxSize");
		if (tmp!=null) MaxSize=Integer.parseInt(tmp)*1024;
		tmp=getInitParameter("Active");
		if (tmp!=null && (tmp.equals("yes") || tmp.equals("Yes"))) {
			active=true;
		} else {
			active=false;
		}
	}

	public String getModuleInfo() {
		return("this module provides cache function for http requests");	
	}
}
