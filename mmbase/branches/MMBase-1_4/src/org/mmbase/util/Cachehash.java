/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * CacheHash, a hashtable that keeps info on max size, type to be able to
 * let the parent control it better for cache/writeback caches
 *
 * @author Daniel Ockeloen
 */
public class Cachehash extends Hashtable {
    	public static final int TEMP 		= 0;
    	public static final int SAVE 		= 1;
    	public static final int DELAYEDSAVE 	= 2;
	private Object obj;
	private int type;
	private int max;

	public Cachehash(int type, int max) {
		this.type=type;	
		this.max=max;	
	}

	public Cachehash(int type, int max, Hashtable newHash) {
		Object O;
		this.type=type;	
		this.max=max;	
		for (Enumeration t=newHash.keys();t.hasMoreElements();) {
			O=t.nextElement();
			put(O,newHash.get(O));
		}
	}
		
	public int getType() {
		return(type);
	}

	public int getMax() {
		return(max);
	}
}
