/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
/**
 * MultiPoolHandler handles multi pools so we can have more than one database
 * open and they can all have a multipool.
 *
 */
 
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MultiPoolHandler {
    private static final Logger log = Logging.getLoggerInstance(MultiPoolHandler.class);
    private int maxConnections;
    private int maxQueries;
    private Map<String,MultiPool> pools = new ConcurrentHashMap<String,MultiPool>();

    private DatabaseSupport databaseSupport;
    private long maxLifeTime = 120000;

    public MultiPoolHandler(DatabaseSupport databaseSupport, int maxConnections) {
        this(databaseSupport, maxConnections, 500);
    }

    public MultiPoolHandler(DatabaseSupport databaseSupport, int maxConnections,int maxQueries) {
	this.maxConnections = maxConnections;
	this.maxQueries     = maxQueries;
	this.databaseSupport= databaseSupport;
    }

    /**
     * @since MMBase-1.8.3
     */
    void setMaxLifeTime(long l) {
        maxLifeTime = l;
    }

    public MultiConnection getConnection(String url, String name, String password) throws SQLException {
	MultiPool pool = pools.get(url + "," + name + "," + password);
	if (pool != null) {
	    return pool.getFree();
	} else {
            log.service("No multipool present, creating one now");
            pool = new MultiPool(databaseSupport, url, name, password, maxConnections, maxQueries);
            pool.setMaxLifeTime(maxLifeTime);
            if (pools.put(url + "," + name + "," + password, pool) != null) {
                log.error("Replaced an old MultiPool!? " + Logging.stackTrace());
            }
            return pool.getFree();
	}
    }

    /**
     * Calls shutdown of all registered MultiPools
     * @since MMBase-1.6.2
     */
    public void shutdown() {
        for (MultiPool pool : pools.values()) {
            pool.shutdown();
	}
    }

    public void checkTime() {
	for (MultiPool pool : pools.values()) {
	    pool.checkTime();
	}
    }

    public Set<String> keySet() {
        return pools.keySet();
    }

    /*
    public Enumeration keys() {
	return pools.keys();
    }
    */

    public MultiPool get(String id) {
        return pools.get(id);
    }

    public void setMaxConnections(int max) {
        maxConnections = max;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxQuerys(int max) {
	maxQueries = max;
    }

    public int getMaxQuerys() {
	return maxQueries;
    }
}
