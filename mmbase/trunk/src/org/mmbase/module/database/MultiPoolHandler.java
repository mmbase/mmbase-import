/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.util.*;

/**
 * MultiPoolHandler handles multi pools so we can have more than one database
 * open and they can all have a multipool.
 *
 */	
public class MultiPoolHandler {
private int maxConnections;
private int maxQuerys;
Hashtable Pools=new Hashtable();
DatabaseSupport databasesupport;

	public MultiPoolHandler(DatabaseSupport databasesupport,int maxConnections) {
		this.maxConnections=maxConnections;
		this.maxQuerys=500;
		this.databasesupport=databasesupport;
	}
	public MultiPoolHandler(DatabaseSupport databasesupport,int maxConnections,int maxQuerys) {
		this.maxConnections=maxConnections;
		this.maxQuerys=maxQuerys;
		this.databasesupport=databasesupport;
	}

	public MultiConnection getConnection(String url, String name, String password) throws SQLException {
		MultiPool pool=(MultiPool)Pools.get(url+","+name+","+password);
		if (pool!=null) {
			return(pool.getFree());
		} else {
			pool=new MultiPool(databasesupport,url,name,password,maxConnections,maxQuerys);
			Pools.put(url+","+name+","+password,pool);
			return(pool.getFree());
		}
	}

	public void checkTime() {
		for (Enumeration e=Pools.elements();e.hasMoreElements();) {
			MultiPool pool=(MultiPool)e.nextElement();
			pool.checkTime();
		}
	}

	public Enumeration elements() {
		return(Pools.elements());
	}

	public Enumeration keys() {
		return(Pools.keys());
	}

	public MultiPool get(String id) {
		return((MultiPool)Pools.get(id));
	}

	public void setMaxConnections(int max) {
		maxConnections=max;
	}

	public int getMaxConnections() {
		return(maxConnections);
	}

	public void setMaxQuerys(int max) {
		maxQuerys=max;
	}

	public int getMaxQuerys() {
		return(maxQuerys);
	}
}
