/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.util.*;

import javax.servlet.http.*;

import org.mmbase.util.Cachehash;

/**
 * Users.
 *
 * @author Daniel Ockeloen
 */
public class User {
	private Hashtable properties = new Hashtable(); // holds the different property hashes
	private objectstore os;
	private String userName;
	private long active,timeout;

	public User(objectstore os, String userName) {
		this.os=os;
		this.userName=userName;
		active=System.currentTimeMillis();
		timeout=60*15*1000; // set default timeout to 15 min.
	}	


	public void signalActive() {
		active=System.currentTimeMillis();
	}

	public boolean timeOut() {
		long tmp=(System.currentTimeMillis()-timeout);
		if (tmp>active) { 
			return(true);
		} else {
			return(false);
		}
	}

	public boolean setProperty(String table, String name, String value) {
		signalActive();
		Cachehash H=(Cachehash)properties.get(table);
		if (H!=null) {
			// Cachehash found so put & check
		 	H.put(name,value);
			int t=H.getType(); // should be a switch if possible
			//System.out.println("name="+name+" val="+value+" type="+t);
			if (t==Cachehash.SAVE) {
				// do save or something
				os.putProperties(userName,table,(Hashtable)H);	
			} else if (t==Cachehash.DELAYEDSAVE) {
				// do save or something delayed
			}
			int m=H.getMax(); 
			if (H.size()>m) {
				// reduce size with save if needed see t
			}
			return(true);
		} else {
			// Cachehash not found
			return(false);
		}
	}	

	public void addPropertyCache(String name,int type, int max) {
		signalActive();
		Cachehash H=new Cachehash(type,max);
		properties.put(name,H);
	}

	public boolean loadPropertyCache(String name,int type, int max) {
		signalActive();
		Hashtable tmpHash;
		tmpHash=os.getProperties(userName,name);	
		if (tmpHash!=null) {
			Cachehash H=new Cachehash(type,max,tmpHash);
			properties.put(name,H);
			return(true);
		} else {
			return(false);
		}
	}

	public boolean CacheHashAvail(String table) {
		if (properties.containsKey(table)) {
			return(true);
		} else {
			return(false);
		}
	}	

	public String getProperty(String table, String name) {
		signalActive();
		Cachehash H=(Cachehash)properties.get(table);
		if (H!=null) {
		 	return((String)H.get(name));
		} else {
			// Cachehash not found
			return(null);
		}
	}	


	public String delProperty(String table, String name) {
		signalActive();
		Cachehash H=(Cachehash)properties.get(table);
		if (H!=null) {
			String val=(String)H.get(name);
			if (val!=null) {
				H.remove(name);
				return(val);
			} else {
				return(null);
			}
		} else {
			// Cachehash not found
			return(null);
		}
	}	

	public Hashtable getProperties(String table) {
		signalActive();
		Hashtable H=(Hashtable)properties.get(table);
		if (H!=null) {
		 	return(H);
		} else {
			// Cachehash not found
			return(null);
		}
	}	

}
