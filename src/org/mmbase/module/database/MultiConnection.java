/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database;

import java.sql.*;
import java.util.Map;
import java.lang.reflect.Method;

import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiConnection is a replacement class for Connection it provides you a
 * multiplexed and reuseable connections from the connection pool.
 * The main function of this class is to 'log' (keep) the last sql statement passed to it.
 * Another function is to keep state (i.e. notifying that it is busy),
 * and to make itself available again to teh connectionpool once it is finished (closed).
 *
 * @sql It would possibly be better to pass the logging of the sql query
 *      to the code that calls the conenction, rather than place it in
 *      the conenction itself, as it's implementation leads to conflicts
 *      between various JDBC versions.
 *      This also goes for freeing the connection once it is 'closed'.
 * @author vpro
 * @author Pierre van Rooden
 * @version $Id$
 */
public class MultiConnection extends ConnectionWrapper {
    // states
    public final static int CON_UNUSED   = 0;
    public final static int CON_BUSY     = 1;
    public final static int CON_FINISHED = 2;
    public final static int CON_FAILED   = 3;


    public static long queries = 0;
    private static final Logger log = Logging.getLoggerInstance(MultiConnection.class);

    /**
     * @javadoc
     */
    MultiPool parent;
    /**
     * @javadoc
     */
    String lastSql;

    private long startTimeMillis = 0;
    private int usage = 0;
    public int state = 0;

    /**
     * @javadoc
     * @todo in 1.7 this method was made public,document why?
     * @since MMBase-1.7
     */
    public MultiConnection(MultiPool parent,Connection con) {
        super(con);
        this.parent = parent;
        state = CON_UNUSED;
    }

    /**
     * @javadoc
     */
    public String getStateString() {
        if (state == CON_FINISHED) {
            return "Finished";
        } else if (state==CON_BUSY) {
            return "Busy";
        } else if (state==CON_FAILED) {
            return "Failed";
        } else if (state==CON_UNUSED) {
            return "Unused";
        }
        return "Unknown";
    }

    /**
     * @javadoc
     */
    public void setLastSQL(String sql) {
        lastSql = sql;
        state = CON_BUSY;
    }

    /**
     * @javadoc
     */
    public String getLastSQL() {
        return lastSql;
    }

    /**
     * createStatement returns an SQL Statement object
     */
    public Statement createStatement() throws SQLException {
        MultiStatement s = new MultiStatement(this, con.createStatement());
        return s;
    }

    /**
     * Tries to fix the this connection, if it proves to be broken. It is supposed to be broken if
     * the query "SELECT 1 FROM <OBJECT TABLE>" does yield an exception.
     * This method is meant to be called in the catch after trying to use the connection.
     *
     * @return <code>true</code> if connection was broken and successfully repaired. <code>false</code> if connection was not broken.
     * @throws SQLException If connection is broken and no new one could be obtained.
     *
     * @since MMBase-1.7.1
     */

    protected boolean checkAfterException() throws SQLException {
        Statement s = null;
        ResultSet rs = null;
        try {
            // check wether connection is still functional
            s = createStatement();
            rs = s.executeQuery("SELECT 1 FROM " + MMBase.getMMBase().getBuilder("object").getFullTableName() + " WHERE 1 = 0"); // if this goes wrong too it can't be the query
        } catch (SQLException isqe) {
             // so, connection must be broken.
            log.service("Found broken connection, will try to fix it.");
            // get a temporary new one for this query
            parent.replaceConnection(this);
            return true;
        } finally {
            if (s != null) s.close();
            if (rs != null) rs.close();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * If "autoCommit" is true, then all subsequent SQL statements will
     * be executed and committed as individual transactions.  Otherwise
     * (if "autoCommit" is false) then subsequent SQL statements will
     * all be part of the same transaction , which must be explicitly
     * committed with either a "commit" or "rollback" call.
     * By default new connections are initialized with autoCommit "true".
     */
    public void setAutoCommit(boolean enableAutoCommit) throws SQLException {
        try {
            con.setAutoCommit(enableAutoCommit);
        } catch (SQLException sqe) {
            if (checkAfterException()) {
                con.setAutoCommit(enableAutoCommit);
            } else {
                throw sqe;
            }
        }
    }

    /**
     * @since MMBase-1.7
     */
    private String getLogSqlMessage(long time) {
        StringBuffer mes = new StringBuffer();
        mes.append('#');
        mes.append(queries);
        mes.append("  ");
        if (time < 10) mes.append(' ');
        if (time < 100) mes.append(' ');
        if (time < 1000) mes.append(' ');
        mes.append(time);
        mes.append(" ms: ").append(getLastSQL());
        return mes.toString();
    }
    /**
     * Close connections
     */
    public void close() throws SQLException {
        long time = System.currentTimeMillis() - getStartTimeMillis();
        long maxLifeTime = parent.getMaxLifeTime();
        if (time < maxLifeTime / 24) {  //  ok, you can switch on query logging with setting logging of this class on debug
            log.debug(getLogSqlMessage(time));
        } else if (time < maxLifeTime / 4) {     // maxLifeTime / 24 (~ 5 s) is too long, but perhaps that's still ok.
            if (log.isServiceEnabled()) {
                log.service(getLogSqlMessage(time));
            }
        } else if (time < maxLifeTime / 2) {   // over maxLifeTime / 4 (~ 30 s), that too is good to know
            log.info(getLogSqlMessage(time));
        } else {                      // query took more than maxLifeTime / 2 (~ 60 s), that's worth a warning
            log.warn(getLogSqlMessage(time));
        }
        if (log.isDebugEnabled()) {
            log.trace("because", new Exception());
        }
        
        state = CON_FINISHED;
        // If there is a parent object, this connection belongs to a pool and should not be closed,
        // but placed back in the pool
        // If there is no parent, the connection belongs to a datasource (thus pooling is done by the appserver)
        // and should be closed normally
        if (parent != null) {
            parent.putBack(this);
        } else {
            realclose();
        }
    }

    /**
     * Close connections
     */
    public void realclose() throws SQLException {
        con.close();
    }

    /**
     * @javadoc
     */
    public boolean checkSQLError(Exception e) {
        log.error("JDBC CHECK ERROR=" + e.toString());
        return true;
    }

    /**
     * @javadoc
     */
    public void claim() {
        usage++;
        queries++;
        startTimeMillis = System.currentTimeMillis();
    }

    /**
     * @javadoc
     */
    public void release() {
        startTimeMillis = 0;
    }

    /**
     * @javadoc
     */
    public int getUsage() {
        return usage;
    }

    /**
     * @since MMBase-1.8
     */
    void resetUsage() {
        usage = 0;
    }

    /**
     * Returns the moment on which the last SQL statement was started in seconds after 1970.
     */
    public int getStartTime() {
        return (int) (startTimeMillis / 1000);
    }

    /**
     * Returns the moment on which the last SQL statement was started in milliseconds after 1970.
     */
    public long getStartTimeMillis() {
        return startTimeMillis;
    }


    /**
     * @javadoc
     */
    public String toString() {
        return "'"+getLastSQL()+"'@"+hashCode();
    }
    /**
     * createStatement returns an SQL Statement object
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new MultiStatement(this, con.createStatement(resultSetType, resultSetConcurrency));
    }


    /**
     * {@inheritDoc}
     * @since MMBase 1.5, JDBC 1.4
     */
    public Statement createStatement(int type, int concurrency, int holdability) throws SQLException {
        return new MultiStatement(this, con.createStatement(type, concurrency, holdability));
    }

    /**
     * Return the underlying real connection. NOTE: use with extreme caution! MMBase is supposed to look
     * after it's own connections. This method is public only for the reason that specific database
     * implementations need access to this connection in order to safely clear them before they 
     * can be put back in the connection pool.
     */
    public Connection getRealConnection() {
        return con;
    }
}


