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
 
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MultiPoolHandler {
    private static Logger log = Logging.getLoggerInstance(MultiPoolHandler.class.getName());
    private int maxConnections;
    private int maxQuerys;
    private Map pools = new Hashtable();
    private DatabaseSupport databasesupport;

    public MultiPoolHandler(DatabaseSupport databasesupport, int maxConnections) {
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
	MultiPool pool = (MultiPool) pools.get(url+","+name+","+password);
	if (pool!=null) {
	    return pool.getFree();
	} else {
	    pool = new MultiPool(databasesupport, url, name, password, maxConnections, maxQuerys);
	    pools.put(url+","+name+","+password, pool);
	    return pool.getFree();
	}
    }

    /**
     * Calls shutdown of all registered MultiPools
     * @since MMBase-1.6.2
     */
    public void shutdown() {
        for (Iterator i = pools.values().iterator(); i.hasNext();) {
            MultiPool pool = (MultiPool) i.next();
	    pool.shutdown();
	}
    }

    public void checkTime() {
	for (Iterator i = pools.values().iterator(); i.hasNext();) {
	    MultiPool pool = (MultiPool) i.next();
	    pool.checkTime();
	}
    }

    public Set keySet() {
	return pools.keySet();
    }

    /*
    public Enumeration keys() {
	return pools.keys();
    }
    */

    public MultiPool get(String id) {
        return (MultiPool) pools.get(id);
    }

    public void setMaxConnections(int max) {
        maxConnections = max;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxQuerys(int max) {
	maxQuerys = max;
    }

    public int getMaxQuerys() {
	return maxQuerys;
    }
}
