/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.DijkstraSemaphore;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBC Pool, a dummy interface to multiple real connection
 * @javadoc
 * @author vpro
 * @version $Id: MultiPool.java,v 1.16 2002-10-10 19:03:46 michiel Exp $
 */
public class MultiPool {

    private static Logger log = Logging.getLoggerInstance(MultiPool.class.getName());

    private List   pool     = new Vector();    
    private List   busyPool = new Vector();
    private int      conPos   = 0;
    private int      conMax   = 4;
    private DijkstraSemaphore semaphore;
    private int      totalConnections = 0;
    private int      maxQuerys = 500;
    private String   url;
    private String   name;
    private String   password;
    private String   dbm;
    //private Object   synobj          = new Object();
    // private Object   synobj_getfree  = new Object();
    private DatabaseSupport databasesupport;

    private static final boolean DORECONNECT  = true;

    /**
     * @javadoc
     */
    MultiPool(DatabaseSupport databasesupport, String url, String name, String password,int conMax) throws SQLException {
        this(databasesupport,url,name,password,conMax,500);        

    }
    /**
     * Establish connection to the JDBC Pool(s)
     */
    MultiPool(DatabaseSupport databasesupport,String url, String name, String password, int conMax,int maxQueries) throws SQLException {

        log.service("Creating a multipool for database " + name + " containing : " + conMax + " connections, which will be refreshed after " + maxQueries + " queries"); 
        this.conMax=conMax;
        this.url=url;
        this.name=name;
        this.password=password;
        this.maxQuerys=maxQuerys;
        this.databasesupport=databasesupport;

        semaphore = new DijkstraSemaphore(conMax);

        // put connections on the pool
        for (int i = 0; i < conMax ; i++) {
            if (name.equals("url") && password.equals("url")) {
                Connection con = DriverManager.getConnection(url);
                initConnection(con);
                pool.add(new MultiConnection(this,con));
            } else {
                Connection con = DriverManager.getConnection(url,name,password);
                initConnection(con);
                pool.add(new MultiConnection(this,con));
            }
        }
        dbm = getDBMfromURL(url);
   }

    /**
     * Check the connections
     */
    public void checkTime() {
        if (log.isDebugEnabled()) {
            log.debug("JDBC -> Starting the pool check (" + this + ") : busy=" + busyPool.size() + " free=" + pool.size());
        }

        int nowTime = (int) (System.currentTimeMillis() / 1000);
        
        List putBacks = new Vector();
        int releases = 0;
        synchronized (semaphore) { 
            //lock semaphore, so during the checks, no connections can be acquired or put back
            // (because the methods of semaphore are synchronized)
            
            for (Iterator i = busyPool.iterator(); i.hasNext();) {
                MultiConnection con = (MultiConnection) i.next();
                int diff = nowTime - con.getStartTime();
                if (diff > 5) {
                    if (log.isDebugEnabled()) {
                        log.debug("Checking a busy connection "+con);
                    }
                } 
                if (diff < 30) {
                    // below 30 we still wait
                } else if (diff < 120) {
                    // between 30 and 120 we putback 'zero' connections
                    if (con.lastSql==null || con.lastSql.length()==0) {
                        log.warn("null connection putBack");
                        putBacks.add(con);
                    }
                } else {
                    // above 120 we close the connection and open a new one
                    MultiConnection newcon = null;                           
                    log.service("KILLED SQL " + con.lastSql + " time " + diff + " because it took too long");
                    try {
                        Connection realcon = DriverManager.getConnection(url,name,password);
                        initConnection(realcon);
                        newcon = new MultiConnection(this,realcon);
                        if (log.isDebugEnabled()) {
                            log.debug("WOW added JDBC connection now ("+pool.size()+")");
                        }
                    } catch(Exception re) {
                        log.error("ERROR Can't add connection to pool");
                    }
                    if (newcon != null) { 
                        pool.add(newcon);
                        busyPool.remove(con);
                        releases++;
                        try {
                            con.realclose();
                        } catch(Exception re) {
                            log.error("Can't close a connection !!!");
                        }
                    }
                }
            }


            if ((busyPool.size() + pool.size()) != conMax) { // cannot happen, I hope.
                log.error("Number of connections is not correct: "+ (busyPool.size()+pool.size()) + " != " + conMax);
                // Check if there are dups in the pools
                for(Iterator i = busyPool.iterator(); i.hasNext();) {
                    MultiConnection bcon = (MultiConnection) i.next();
                    int j = pool.indexOf(bcon);
                    if (j >= 0) {
                        if (log.isDebugEnabled()) {
                            log.debug("duplicate connection found at " + j);
                        }
                        pool.remove(j);
                    }
                }
            
                while(((busyPool.size() + pool.size()) > conMax) && pool.size()>2) {
                    // Remove too much ones.
                    MultiConnection con = (MultiConnection) pool.remove(0);
                    if (log.isDebugEnabled()) {
                        log.debug("removing connection "+con);
                    }
                }
            
            }
        } // synchronized(semaphore)
        semaphore.release(releases);        
        for (Iterator i = putBacks.iterator(); i.hasNext();) {
            MultiConnection pb = (MultiConnection) i.next();
            putBack(pb);
        } 
        
    }

    /**
     * get a free connection from the pool
     */
    public MultiConnection getFree() {
        try {
            totalConnections++;
            semaphore.acquire();
            MultiConnection con = (MultiConnection) pool.remove(0);
            con.claim();
            busyPool.add(con);
            return con;
        } catch (InterruptedException e) {
            log.error("GetFree was Interrupted");
            return null;
        }
    }

    /**
     * putback the used connection in the pool
     */
    public void putBack(MultiConnection con) {
        con.release();
        if (! busyPool.contains(con)) {
            log.warn("Put back connection was not in busyPool!!");
        }

        if (DORECONNECT && (con.getUsage() > maxQuerys)) {
            MultiConnection oldcon = con;
            if (log.isDebugEnabled()) {
                log.debug("Re-Opening connection");
            }
            try {
                oldcon.realclose();
            } catch(Exception re) {
                log.error("Can't close a connection !!!");
            }

            try {
                if (name.equals("url") && password.equals("url")) {
                    Connection realcon = DriverManager.getConnection(url);
                    initConnection(realcon);
                    con = new MultiConnection(this,realcon);
                } else {
                    Connection realcon = DriverManager.getConnection(url,name,password);
                    initConnection(realcon);
                    con = new MultiConnection(this,realcon);
                }
            } catch(Exception re) {
                log.error("Can't add connection to pool");
            }
            busyPool.remove(oldcon);
        } 
        pool.add(con);
        busyPool.remove(con);
        semaphore.release();
    }

    /**
     * get the pool size
     */
    public int getSize() {
        return pool.size();
    }

    /**
     * get the number of statements performed
     */
    public int getTotalConnectionsCreated() {
        return totalConnections;
    }

    /**
     * @javadoc
     */
    public Iterator getPool() {
        return pool.iterator();
    }
   

    /**
     * @javadoc
     */
   
    public Iterator getBusyPool() {
        return busyPool.iterator();
    }
    

    /**
     * @javadoc
     */
    public String toString() {
        return "dbm=" + dbm + ",name=" + name + ",conmax=" + conMax;
    }

    /**
     * @javadoc
     */
    private String getDBMfromURL(String url) {
        return url;
    }

    /**
     * @javadoc
     */
    private boolean checkConnection(Connection conn) {
        Statement statement;
        boolean rtn;
        try {
            statement=conn.createStatement();
            statement.executeQuery("select count(*) from systables");
            statement.close();
            rtn=true;
        } catch (Exception e) {
            rtn=false;
            log.error("checkConnection failed");
            log.error(Logging.stackTrace(e));
        }
        return(rtn);
    }

    /**
     * @javadoc
     */
    protected void initConnection(Connection conn) {
        databasesupport.initConnection(conn);
    }
}
