/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import java.lang.*;
import java.util.*;


/**
 * This object handles cache multilevel tag
 * cache requests. it removed invalid lines 
 * adding listeners to builders used in the
 * multilevel query's
 *
 * @author Daniel Ockeloen
 */
public class MultilevelCacheHandler extends LRUHashtable {

	// listeners, keeps a list of entry's per objectmanager
	private Hashtable listeners = new Hashtable();

	// reference to main MMBase class
	private static MMBase mmb;

	// reference to itself needed to give instance back in a static (weird)
	private static Hashtable caches=new Hashtable();

	// the state, true is active
	private static boolean state=true;
	
	// 
	public MultilevelCacheHandler(String name,int size) {
		super(size);
		caches.put(name,this);
	}

	public Object put(Object hash,Object o,Vector types,StringTagger tagger) {
		MultilevelCacheEntry n=new MultilevelCacheEntry(this,hash,o,tagger);
		addListeners(types,n);
		return(put(hash,n));
	}

	public static void setMMBase(MMBase m) {
		mmb=m;
	}

	public static MultilevelCacheHandler getCache(String name) {
		Object result=caches.get(name);
		if (result==null) {
			MultilevelCacheHandler nc=new MultilevelCacheHandler(name,300);
			return(nc);
		} else {
			return((MultilevelCacheHandler)result);
		}
	}
	

	public synchronized Object get(Object key) {
		// get the wrapper but return the
		// object 
		MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
		if (n==null) {
			return(null);
		} else {
			return(n.getObject());
		}
	}

	private void addListeners(Vector types,MultilevelCacheEntry n) {
		Enumeration e=types.elements();
		while (e.hasMoreElements()) {
			String type=(String)e.nextElement();
			char lastchar=type.charAt(type.length()-1);
			if (lastchar>='1' && lastchar<='9') {
				type=type.substring(0,type.length()-1);
			}
			MultilevelSubscribeNode l=(MultilevelSubscribeNode)listeners.get(type);
			if (l==null) {
				l=new MultilevelSubscribeNode(mmb,type);	
				listeners.put(type,l);	
			}
			l.addCacheEntry(n);
		}
	}
	

	// intercept remove of LRU to make sure
 	// we remove from all Observers first
	// the clear will call the real remove
	public synchronized Object remove(Object key) {
		MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
		n.clear();
		return(n);
	}

	// call the real remove in the LRU
	public synchronized void callbackRemove(Object key) {
		super.remove(key);
	}


	/**
	 * support methode, creates a hashcode for all the
	 * parameters in a multilevel request comming from
	 * the mmci	
	 */
	public Integer calcHashMultiLevel(StringTagger tagger) {
		int hash=1;
		Object obj;

		obj=tagger.Values("TYPES");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("SORTED");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("DIR");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("WHERE");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("FIELDS");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("NODES");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("DISTINCT");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("SEARCH");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());

		return(new Integer(hash));
	}

	public static void setState(boolean s) {
		state=s;
	}

	public static boolean isActive() {
		return(state);
	}
}
