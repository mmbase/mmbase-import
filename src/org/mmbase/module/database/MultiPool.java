 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database;

import java.sql.*;
import java.util.*;
import org.mmbase.util.DijkstraSemaphore;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBC Pool, a dummy interface to multiple real connection
 * @javadoc
 * @author vpro
 * @version $Id: MultiPool.java,v 1.37 2004-01-13 12:23:46 michiel Exp $
 */
public class MultiPool {

    private static final Logger log = Logging.getLoggerInstance(MultiPool.class);

    private List              pool     = new ArrayList();
    private List              busyPool = new ArrayList();
    private int               conMax   = 4;
    private DijkstraSemaphore semaphore;
    private int      totalConnections = 0;
    private int      maxQueries = 500;
    private String   url;
    private String   name;
    private String   password;
    private String   dbm;
    private DatabaseSupport databaseSupport;

    private static final boolean DORECONNECT  = true;

    /**
     * @javadoc
     */
    MultiPool(DatabaseSupport databaseSupport, String url, String name, String password,int conMax) throws SQLException {
        this(databaseSupport, url, name, password, conMax, 500);

    }
    /**
     * Establish connection to the JDBC Pool(s)
     */
    MultiPool(DatabaseSupport databaseSupport,String url, String name, String password, int conMax,int maxQueries) throws SQLException {

        log.service("Creating a multipool for database " + name + " containing : " + conMax + " connections, which will be refreshed after " + maxQueries + " queries");
        this.conMax          = conMax;
        this.url             = url;
        this.name            = name;
        this.password        = password;
        this.maxQueries      = maxQueries;
        this.databaseSupport = databaseSupport;

        int errors = 0;
        SQLException firstError = null;
        // put connections on the pool
        for (int i = 0; i < conMax ; i++) {
            try {
                pool.add(getMultiConnection());
            } catch (SQLException se) {               
                errors++;
                if (log.isDebugEnabled()) {
                    log.debug("i: " + "error " + errors + ": " + se.getMessage());
                }
                if (firstError == null) firstError = se;
            }
        }
        if (errors > 0) {
            if (pool.size() < 2) { // that is fatal.
                throw firstError;
            }
            log.error("Could not get all connections (" + errors + " failures). First error: " + firstError.getMessage() + Logging.stackTrace(firstError));
            log.info("Multipools size is now " + pool.size() + " rather then " + conMax);
            this.conMax = pool.size();
        }


        

        semaphore = new DijkstraSemaphore(pool.size());

        dbm = getDBMfromURL(url);
    }

    /**
     * Request a new 'real' Connection and wraps it in a new 'MultiConnection' object.
     *
     * @since MMBase-1.7
     */
    protected MultiConnection getMultiConnection() throws SQLException {
       Connection con;
       if (name.equals("url") && password.equals("url")) {
           con = DriverManager.getConnection(url);
       } else {
           con = DriverManager.getConnection(url, name, password);
       }
       databaseSupport.initConnection(con);
       return new MultiConnection(this, con);
    }

    protected void finalize() {
        shutdown();
    }

    /**
     * 'realcloses' all connections.
     * @since MMBase-1.6.2
     */
    public void shutdown() {
        synchronized (semaphore) {
            try {
                for (Iterator i = busyPool.iterator(); i.hasNext();) {
                    MultiConnection con = (MultiConnection) i.next();
                    con.realclose();
                }
                busyPool.clear();
                for (Iterator i = pool.iterator(); i.hasNext();) {
                    MultiConnection con = (MultiConnection) i.next();
                    con.realclose();
                }
                pool.clear();
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    /**
     * Check the connections
     * @bad-constant  Max life-time of a query must be configurable
     */
    public void checkTime() {
        int releasecount=0;
        if (log.isDebugEnabled()) {
            log.debug("JDBC -> Starting the pool check (" + this + ") : busy=" + busyPool.size() + " free=" + pool.size());
        }

        synchronized (semaphore) {
            //lock semaphore, so during the checks, no connections can be acquired or put back

            //// (because the methods of semaphore are synchronized)
            //// commented out above commentline, because this is not true.
            //// The synchronized in java works on instnaces of a class.
            //// The code
            ////
            ////   void synchronized myMethod() ( statement; )
            ////
            //// can be rewritten as
            ////
            ////   void myMethod(){
            ////       synchronized(this) { statement; }
            ////   }
            ////
            //// This way the instance is only locked when the method is executing.
            //// The statements before and after this are not synchronized.
            //// When an instnace is not in a synchronized block the instance is not locked
            //// and every thread can modify the instance.
            //// Busypool is in this method not locked and can be modified in getFree and putBack.
            //// if the busypool is iterated and modified at the same time a
            //// ConcurrentModificationException is thrown.

            // Michiel: but the getFree and putBack actions on the two pools are also synchronized on semaphore.
            //          so nothing can edit them without having acquired the lock on sempahore.


            int nowTime = (int) (System.currentTimeMillis() / 1000);

            for (Iterator i = busyPool.iterator(); i.hasNext();) {
                MultiConnection con = (MultiConnection) i.next();
                int diff = nowTime - con.getStartTime();

                if (diff > 5) {
                    if (log.isDebugEnabled()) {
                        log.debug("Checking a busy connection "+con +" time = "+ diff + " seconds");
                    }
                }

                if (diff < 30) {
                    // ok, just wait
                } else if (diff < 120) {
                    // between 30 and 120 we putback 'zero' connections
                    if (con.lastSql == null || con.lastSql.length() == 0) {
                        log.warn("null connection putBack");
                        pool.add(con);
                        releasecount++;
                        i.remove();
                    }
                } else {
                    // above 120 we close the connection and open a new one
                    MultiConnection newcon = null;
                    log.warn("WILL KILL SQL " + con.lastSql + " after " + diff + " seconds, because it took too long");
                    try {
                        // get a new connection to replace this one
                        newcon = getMultiConnection();
                    } catch(SQLException e) {
                        log.error("ERROR Can't add connection to pool " + e.toString());
                    }
                    if (newcon != null) {
                        pool.add(newcon);
                        releasecount++;
                    }
                    i.remove();
                    // we close connections in a seperate thread, for those broken JDBC drivers out there                    
                    con.markedClosed = true;
                    new ConnectionCloser(con);
                }
            }

            if ((busyPool.size() + pool.size()) != conMax) {
                // cannot happen, I hope...
                log.error("Number of connections is not correct: " + busyPool.size() + " + " + pool.size () + " = " + (busyPool.size() + pool.size()) + " != " + conMax);
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
            semaphore.release(releasecount);
        } // synchronized(semaphore)
        if (log.isDebugEnabled()){
            log.debug("finished  checkTime()");
        }
    }

    /**
     * get a free connection from the pool
     */
    public MultiConnection getFree() {
        MultiConnection con = null;
        try {
            totalConnections++;
            //see comment in method checkTime()
            semaphore.acquire();
            synchronized (semaphore) {
                con = (MultiConnection) pool.remove(0);
                con.claim();
                busyPool.add(con);
            }
        } catch (InterruptedException e) {
            log.error("GetFree was Interrupted");
        }
        return con;
    }

    /**
     * putback the used connection in the pool
     */
    public void putBack(MultiConnection con) {
        // Don't put back bad connections;
        try {
            if (con.isClosed() || con.markedClosed) {
                return;
            }
        } catch (SQLException e) {
            return;
        }
        //see comment in method checkTime()
        synchronized (semaphore) {
            if (! busyPool.contains(con)) {
                log.warn("Put back connection (" + con.lastSql + ") was not in busyPool!!");
            }

            con.release(); //Resets time connection is busy.
            MultiConnection oldcon = con;

            if (DORECONNECT && (con.getUsage() > maxQueries)) {
                if (log.isDebugEnabled()) {
                    log.debug("Re-Opening connection");
                }
                try {
                    oldcon.realclose();
                } catch(SQLException re) {
                    log.error("Can't close a connection !!!");
                }

                try {
                    con = getMultiConnection();
                } catch(SQLException re) {
                    log.error("Can't add connection to pool " + re.toString());
                }
            }
            pool.add(con);
            busyPool.remove(oldcon);
            semaphore.release();
        }
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
     * For reporting purposes the connections in pool can be listed.
     * An Iterator on a copy of the Pool is returned.
     *
     * @see JDBC#listConnections
     */
    public Iterator getPool() {
        synchronized(semaphore) {
            return new ArrayList(pool).iterator();
        }
    }


    /**
     * For reporting purposes the connections in busypool can be listed.
     * An Iterator on a copy of the BusyPool is returned.
     * @see JDBC#listConnections
     */

    public Iterator getBusyPool() {
        synchronized(semaphore) {
            return new ArrayList(busyPool).iterator();
        }
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
            statement = conn.createStatement();
            statement.executeQuery("select count(*) from systables");
            statement.close();
            rtn = true;
        } catch (Exception e) {
            rtn = false;
            log.error("checkConnection failed");
            log.error(Logging.stackTrace(e));
        }
        return(rtn);
    }


    /**
     * Support class to close connections in a seperate thread, as some JDBC drivers
     * have a tendency to hang themselves on a runing sql close.
     */
    static class ConnectionCloser implements Runnable {
        private static final Logger log = Logging.getLoggerInstance(ConnectionCloser.class);

        private MultiConnection connection;

        public ConnectionCloser(MultiConnection con) {
            connection = con;
            start();
        }

        /**
         * Starts a Thread and runs this Runnable
         */
        protected void start() {
            // Start up the thread
            Thread kicker = new Thread(this, "ConnectionCloser");
            kicker.setDaemon(true);
            // MM: why is is a daemon thread. Can run() actually hang? That would be bad!
            kicker.start();
        }

        /**
         * Close the database connection.
         */
        public void run() {
            log.warn("Closing " + connection);
            try {
                connection.realclose();
            } catch(Exception re) {
                log.error("Can't close a connection !!!" + re);
            }
            log.warn("Closed  " + connection);
        }
    }
}
