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
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBC Pool, a dummy interface to multiple real connection
 * @javadoc
 * @author vpro
 * @version $Id$
 */
public class MultiPool {

    private static final Logger log    = Logging.getLoggerInstance(MultiPool.class);

    private List              pool     = null;
    private List              busyPool = new ArrayList();
    private int               conMax   = 4;
    private DijkstraSemaphore semaphore = null;
    private int      totalConnections = 0;
    private int      maxQueries = 500;
    private String   name;
    private String   password;
    private String   url;
    private DatabaseSupport databaseSupport;

    private boolean doReconnect  = true;

    private long    maxLifeTime  = 120000;
    private long    maxZeroTime  = maxLifeTime / 4;

    /**
     * @javadoc
     */
    MultiPool(DatabaseSupport databaseSupport, String url, String name, String password, int conMax) throws SQLException {
        this(databaseSupport, url, name, password, conMax, 500);

    }
    /**
     * Establish connection to the JDBC Pool(s)
     */
    MultiPool(DatabaseSupport databaseSupport, String url, String name, String password, int conMax,int maxQueries) throws SQLException {

        this.conMax          = conMax;
        this.name            = name;
        this.url             = url;
        this.password        = password;
        this.maxQueries      = maxQueries;
        if (this.maxQueries <= 0) doReconnect = false;
        this.databaseSupport = databaseSupport;
        log.service("Creating a multipool for database " + name + "(" + url + ") containing : " + conMax + " connections, " + (doReconnect ? "which will be refreshed after " + this.maxQueries + " queries"  : "which will not be refreshed"));
        createPool();
    }

    /**
     * Set the time in ms how long a query may live before it is killed.
     * @since MMBase-1.8
     */
    void setMaxLifeTime(long maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
        maxZeroTime = maxLifeTime / 4;
    }

    /**
     * Gets the time in ms how long a query may live before it is killed.
     * @since MMBase-1.8
     */
    long getMaxLifeTime() {
        return maxLifeTime;
    }

    /**
     * Creates and fills the connection pool
     * @since MMBase-1.7
    */
    protected void createPool() {
        pool = new ArrayList();
        MMBase mmb = MMBase.getMMBase();
        boolean logStack = true;
        try {
            while (!fillPool(logStack)) {
                log.error("Cannot run with no connections, retrying after 10 seconds for " + mmb + " " + (mmb.isShutdown() ? "(shutdown)" : ""));
                Thread.sleep(10000);
                logStack = false; // don't log that mess a second time
                if (mmb.isShutdown()) {
                    log.info("MMBase has been shutted down.");
                    return;
                }
            }
            semaphore = new DijkstraSemaphore(pool.size());
            log.service("Connection pool has " + conMax + " connections");
        } catch (InterruptedException ie) {
            log.info("Interrupted: " + ie.getMessage());
        }


    }


    /**
     * Fills the connection pool.
     * @return true if the pool contains connections and mmbase can be started.
     * @since MMBase-1.7
     */
    protected boolean fillPool(boolean logStack) {
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
                if (firstError == null) {
                    firstError = se;
                }
            }
        }
        if (errors > 0) {
            String message = firstError.getMessage();
            if (logStack) {
                message += " " + Logging.stackTrace(firstError);
            }  else {
                int nl = message.indexOf('\n'); // some stupid drivers (postgresql) add stacktrace to message
                if (nl > 0) {
                    message = message.substring(0, nl) + "..."; // take most of it away again.
                }
            }

            log.error("Could not get all connections (" + errors + " failures, multipool size now " + pool.size() + " rather then " + conMax +"). First error: " + message);
            if (pool.size() < 1) { // that is fatal.
                return false;
            }
            this.conMax = pool.size();
        }

        return true;


    }

    /**
     * Request a new 'real' Connection and wraps it in a new 'MultiConnection' object.
     *
     * @since MMBase-1.7
     */
    protected MultiConnection getMultiConnection() throws SQLException {
        log.debug("Getting a new connection for url '" + url + "'");
        Connection con;
        if (name.equals("url") && password.equals("url")) {
            con = DriverManager.getConnection(url);
        } else {
            con = DriverManager.getConnection(url, name, password);
        }
        databaseSupport.initConnection(con);
        return new MultiConnectionImplementation(this, con);
    }

    /**
     * Tries to fix this multi-connection if it is broken (e.g. if database restarted).
     * @since MMBase-1.7.1
     */
    protected void replaceConnection(MultiConnection multiCon) throws SQLException {
        if (name.equals("url") && password.equals("url")) {
            multiCon.con = DriverManager.getConnection(url);
        } else {
            multiCon.con = DriverManager.getConnection(url, name, password);
        }
        databaseSupport.initConnection(multiCon.con);

    }

    protected void finalize() {
        shutdown();
    }

    /**
     * 'realcloses' all connections.
     * @since MMBase-1.6.2
     */
    public void shutdown() {
        log.info("Shutting down multipool " + this);
        if (semaphore == null) return; // nothing to shut down
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
            } catch (Throwable e) {
                log.error(e);
            } finally {
                conMax = busyPool.size() + pool.size(); // should be 0 now
                log.info("Having " + conMax + " connections now.");
                if (conMax != 0) log.error("Still having connections!");
            }
        }
    }

    /**
     * Check the connections
     * @bad-constant  Max life-time of a query must be configurable
     */
    void checkTime() {

        if (log.isDebugEnabled()) {
            log.debug("JDBC -> Starting the pool check (" + this + ") : busy=" + busyPool.size() + " free=" + pool.size());
        }
        if (semaphore == null ||  // during start-up this could happen.
            conMax == 0           // during shut-down this could happen
            ) return;
        synchronized (semaphore) {

            int releaseCount = 0; // number of connection that are put back to pool

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
            //          so nothing can edit them without having acquired the lock on semaphore.


            long nowTime = System.currentTimeMillis();

            for (Iterator i = busyPool.iterator(); i.hasNext();) {
                MultiConnection con = (MultiConnection) i.next();

                boolean isClosed = true;

                try {
                    isClosed = con.isClosed();
                } catch (SQLException e) {
                    log.warn("Could not check isClosed on connection, assuming it closed: " + e.getMessage());
                }



                if (isClosed) {
                    MultiConnection newCon = null;
                    log.warn("WILL KILL SQL because connection was closed. ID=" + con.hashCode() + " SQL: " + con.lastSql);
                    try {
                        // get a new connection to replace this one
                        newCon = getMultiConnection();
                    } catch(SQLException e) {
                        log.error("Can't add connection to pool (after close) " + e.toString());
                    }
                    if (newCon != null) { // successfully created new connection
                        // we close connections in a seperate thread, for those broken JDBC drivers out there
                        new ConnectionCloser(con);
                    } else {
                        // could not create new connection somewhy, but this one is **** up as well, what to do?
                        // fail every future query:
                        newCon = con; // simply put it back in the available pool, so everything will fail
                    }
                    pool.add(newCon);
                    releaseCount++;
                    i.remove();

                    continue;
                }

                long diff = nowTime - con.getStartTimeMillis();

                if (log.isDebugEnabled()) {
                    if (diff > 5000 || diff > maxZeroTime) {  // don't log too often
                        log.debug("Checking a busy connection " + con + " time = " + diff + " seconds");
                    }
                }

                if (diff < maxZeroTime) {
                    // ok, just wait
                } else if (diff < maxLifeTime) {
                    // between 30 and 120 we putback 'zero' connections
                    if (con.lastSql == null || con.lastSql.length() == 0) {
                        log.warn("null connection putBack " + Logging.stackTrace());
                        pool.add(con);
                        releaseCount++;
                        i.remove();
                    }
                } else {
                    // above 120 we close the connection and open a new one
                    MultiConnection newCon = null;
                    log.warn("WILL KILL SQL. It took already " + (diff / 1000) + " seconds, which is too long. ID=" + con.hashCode() + " SQL: " + con.lastSql);
                    try {
                        // get a new connection to replace this one
                        newCon = getMultiConnection();
                    } catch(SQLException e) {
                        log.error("Can't add connection to pool (after kill) " + e.toString());
                    }
                    if (newCon != null) { // successfully created new connection
                        pool.add(newCon);
                        releaseCount++;
                        i.remove();
                        // we close connections in a seperate thread, for those broken JDBC drivers out there
                        new ConnectionCloser(con);
                    } else {
                        // could not create new connection somewhy, will be retried in the next cycle
                    }
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
            semaphore.release(releaseCount);
        } // synchronized(semaphore)
        if (log.isDebugEnabled()){
            log.debug("finished  checkTime()");
        }
    }

    /**
     * Get a free connection from the pool
     */
    MultiConnection getFree() {

        MultiConnection con = null;
        try {
            if (semaphore == null) return null; // during start-up this could happen.
            //see comment in method checkTime()
            synchronized (semaphore) {
                totalConnections++;
                if (conMax == 0) { // could happen during shut down of MMBase
                    try {
                        con = getMultiConnection(); // hm....
                        con.claim();
                        return con;
                    } catch (SQLException sqe) {
                        return null; // will probably cause NPE's but well
                    }
                }

                semaphore.acquire(); // locks until something free
                if (log.isDebugEnabled()) {
                    log.debug("Getting free connection from pool " + pool.size());
                }
                con = (MultiConnection) pool.remove(0);
                con.claim();
                try {
                    if (con.isClosed()) {
                        con = getMultiConnection();
                        con.claim();
                        log.service("Got a closed connection from the connection Pool, it was replaced by a new one.");
                    }
                } catch (SQLException sqe) {
                    log.error("Closed connection could not be replaced because: " + sqe.getClass().getName() + ": " + sqe.getMessage());
                }
                busyPool.add(con);
                if (log.isDebugEnabled()) {
                    log.debug("Pool: " + pool.size() + " busypool: " + busyPool.size());
                }
            }
        } catch (InterruptedException e) {
            log.error("GetFree was Interrupted");
        }
        return con;
    }

    /**
     * putback the used connection in the pool
     */
    void putBack(MultiConnection con) {
        //see comment in method checkTime()
        synchronized (semaphore) {
            if (log.isDebugEnabled()) {
                log.debug("Putting back to Pool: " + pool.size() + " from busypool: " + busyPool.size());
            }

            if (! busyPool.contains(con)) {
                // try to guess wat happend, to report that in the logs
                MMBase mmb = MMBase.getMMBase();
                if (! mmb.isShutdown()) {
                    try {
                        if (con.isClosed()) {
                            log.debug("Connection " + con + " as closed an not in busypool, so it was removed from busyPool by checkTime");
                        } else {
                            log.warn("Put back connection (" + con.lastSql + ") was not in busyPool!!");
                        }
                    } catch (SQLException sqe) {
                        log.warn("Connection " + con + " not in busypool : " + sqe.getMessage());
                    }
                } else {
                    log.service("Connection " + con.lastSql + " was put back, but MMBase is shut down, so it was ignored.");
                }
                return;
            }


            con.release(); //Only resets time connection is busy.

            MultiConnection oldCon = con;

            if (doReconnect && (con.getUsage() > maxQueries)) {
                log.debug("Re-oppening connection");

                boolean gotNew = false;
                try {
                    con = getMultiConnection();
                    gotNew = true;
                } catch(SQLException sqe) {
                    log.error("Can't add connection to pool (during reconnect) " + sqe.toString());
                }

                if (gotNew) { // a new conection has successfully created, the old one can be closed
                    new ConnectionCloser(oldCon);
                } else { // use the old one another cycle
                    log.service("Will continue use original connection");
                    con.resetUsage();
                }
            }

            pool.add(con);
            busyPool.remove(oldCon);
            if (log.isDebugEnabled()) {
                log.debug("Pool: " + pool.size() + " busypool: " + busyPool.size());
            }
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
        return "dbm=" + url + ",name=" + name + ",conmax=" + conMax;
    }


    /**
     * Support class to close connections in a seperate thread, as some JDBC drivers
     * have a tendency to hang themselves on a running sql close.
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
            kicker.setDaemon(true); // For the case if indeed the close 'hangs' the thread.
            kicker.start();
        }

        /**
         * Close the database connection.
         */
        public void run() {
            try {
                connection.realclose();
            } catch(Exception re) {
                log.error("Can't close connection with ID " + connection.hashCode() + ", cause: " + re);
            }
            log.debug("Closed connection with ID " + connection.hashCode());
        }
    }
}
