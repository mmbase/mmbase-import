/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiStatement is a wrapper class for a callable Statement
 * obtained by a MultiConnection object.
 * The sole function of this class is to log the sql statement passed to it
 * using the MultiConnection that called it - all calls are then passed to
 * the Statement object passed to the constructor.
 *
 * @sql It would possibly be better to pass the logging of the sql query
 *      to the code that calls the statement, rather than place it in
 *      the statement itself, as it's implementation leads to conflicts
 *      between various JDBC versions.
 *
 * @author vpro
 * @author Pierre van Rooden
 * @version $Id$
 */
public class MultiStatement implements Statement {
    private static final Logger log = Logging.getLoggerInstance(MultiStatement.class);

    /**
     * The connection that created this statement.
     */
    MultiConnection parent;
    /**
     * The actual statement (created by the database driver)
     */
    private Statement s;

    /**
     * @javadoc
     */
    MultiStatement(MultiConnection parent,Statement s) {
      this.parent = parent;
      this.s=s;
    }

    /**
     * @javadoc
     */
    public int executeUpdate(String sql) throws SQLException {
        parent.setLastSQL(sql);
        return s.executeUpdate(sql);
    }

    /**
     * @javadoc
     */
    public void close() throws SQLException {
        s.close();
        s = null; // lets asign it to null to be sure
    }

    /**
     * @javadoc
     */
    public int getMaxFieldSize() throws SQLException {
        return s.getMaxFieldSize();
    }

    /**
     * @javadoc
     */
    public void setMaxFieldSize(int max) throws SQLException {
        s.setMaxFieldSize(max);
    }

    /**
     * @javadoc
     */
    public int getMaxRows() throws SQLException {
        return s.getMaxRows();
    }

    /**
     * @javadoc
     */
    public void setMaxRows(int max) throws SQLException {
        s.setMaxRows(max);
    }

    /**
     * @javadoc
     */
    public void setEscapeProcessing(boolean enable) throws SQLException {
        s.setEscapeProcessing(enable);
    }

    /**
     * @javadoc
     */
    public int getQueryTimeout() throws SQLException {
        return s.getQueryTimeout();
    }

    /**
     * @javadoc
     */
    public void setQueryTimeout(int seconds) throws SQLException {
        s.setQueryTimeout(seconds);
    }

    /**
     * @javadoc
     */
    public void cancel() throws SQLException {
        s.cancel();
    }

    /**
     * @javadoc
     */
    public SQLWarning getWarnings() throws SQLException {
        return s.getWarnings();
    }

    /**
     * @javadoc
     */
    public void clearWarnings() throws SQLException {
        s.clearWarnings();
    }

    /**
     * @javadoc
     */
    public boolean execute(String sql) throws SQLException {
        parent.setLastSQL(sql);
        return s.execute(sql);
    }

    /**
     * @javadoc
     */
    public ResultSet getResultSet() throws SQLException {
        return s.getResultSet();
    }

    /**
     * @javadoc
     */
    public int getUpdateCount() throws SQLException {
        return s.getUpdateCount();
    }

    /**
     * @javadoc
     */
    public boolean getMoreResults() throws SQLException {
        return s.getMoreResults();
    }

    /**
     * @javadoc
     */
    public void setCursorName(String name) throws SQLException {
        s.setCursorName(name);
    }


    /**
     * Tries to fix the parent connection, if it proves to be broken. It is supposed to be broken if
     * the query "SELECT 1 FROM <OBJECT TABLE> WHERE 1 = 0" does yield an exception.
     * This method is meant to be called in the catch after trying to exceute a statement.
     *
     * @return A new Statement object if a new Connection was successfully obtained. Or null, if 'SELECT 1' did succeed.
     * @throws  SQLException if SELECT 1 did fail an no new Connection could be obtained.
     * @since MMBase-1.7.1
     */
    protected Statement checkAfterException() throws SQLException {
        if (!  org.mmbase.module.core.MMBase.getMMBase().getBuilder("object").created()) {
            // if this table does not exist, this is impossible
            return s;
        }
        ResultSet rs = null;
        // check wether connection is still functional
        try {
            rs = s.executeQuery("SELECT 1 FROM " + org.mmbase.module.core.MMBase.getMMBase().getBuilder("object").getFullTableName() + " WHERE 1 = 0"); // if this goes wrong too it can't be the query);
        } catch (SQLException isqe) {
             // so, connection must be broken.
            log.service("Found broken connection, will try to fix it.");
            s.close();
            parent.parent.replaceConnection(parent);
            s = parent.createStatement();
            // this would be more correct:
            //s = parent.createStatement(s.getResultSetType(), s.getResultSetConcurrency(), s.getResultSetHoldability());
            // but I have a psql driver:
            // org.postgresql.util.PSQLException: This method is not yet implemented.
            //  at org.postgresql.Driver.notImplemented(Driver.java:388)
            //  at org.postgresql.jdbc3.AbstractJdbc3Statement.getResultSetHoldability(AbstractJdbc3Statement.java:278)
            // It does not matter much because in all of MMBase only 'createStatement()' is used.
            return s;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return null;
    }

    /**
     * @javadoc
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.executeQuery(sql);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.executeQuery(sql);
            } else {
                throw sqe;
            }

        }
    }

    /**
     * @javadoc
     */
    public int[] executeBatch() throws SQLException {
        return s.executeBatch();
    }

    /**
     * @javadoc
     */
    public void setFetchDirection(int dir) throws SQLException {
        s.setFetchDirection(dir);
    }

    /**
     * @javadoc
     */
    public int getFetchDirection() throws SQLException {
        return s.getFetchDirection();
    }

    /**
     * @javadoc
     */
    public int getResultSetConcurrency() throws SQLException {
        return s.getResultSetConcurrency();
    }

    /**
     * @javadoc
     */
    public int getResultSetType() throws SQLException {
        return s.getResultSetType();
    }

    /**
     * @javadoc
     */
    public void addBatch(String sql) throws SQLException {
        s.addBatch(sql);
    }

    /**
     * @javadoc
     */
    public void clearBatch() throws SQLException {
        s.clearBatch();
    }

    /**
     * @javadoc
     */
    public Connection getConnection() throws SQLException {
        return s.getConnection();
    }

    /**
     * @javadoc
     */
    public int getFetchSize() throws SQLException {
        return s.getFetchSize();
    }

    /**
     * @javadoc
     */
    public void setFetchSize(int i) throws SQLException {
        s.setFetchSize(i);
    }

    /**
     * Moves to this Statement object's next result, deals with any current ResultSet object(s) according to
     * the instructions specified by the given flag, and returns true if the next result is a ResultSet object.
     * @param current one of CLOSE_CURRENT_RESULT, KEEP_CURRENT_RESULT, or CLOSE_ALL_RESULTS
     * @return true if the next result is a ResultSet object; false if it is an update count or there are no more results
     * @since MMBase 1.5, JDBC 1.4
     */
    public boolean getMoreResults(int current) throws SQLException {
        return s.getMoreResults(current);
    }

    /**
     * Retrieves any auto-generated keys created as a result of executing this Statement object.
     * @return a ResultSet object containing the auto-generated key(s) generated by the execution of this Statement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public ResultSet getGeneratedKeys() throws SQLException {
        return s.getGeneratedKeys();
    }

    /**
     * Executes the given SQL statement and signals the driver with the given flag about whether the
     * auto-generated keys produced by this Statement object should be made available for retrieval.
     * @param sql must be an SQL INSERT, UPDATE or DELETE statement or an SQL statement that returns nothing
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be made available for retrieval
     * @return either the row count for INSERT, UPDATE  or DELETE statements, or 0 for SQL statements that return nothing
     * @since MMBase 1.5, JDBC 1.4
     */
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.executeUpdate(sql, autoGeneratedKeys);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.executeUpdate(sql, autoGeneratedKeys);
            } else {
                throw sqe;
            }
        }
    }

    /**
     * Executes the given SQL statement and signals the driver that the auto-generated keys indicated in
     * the given array should be made available for retrieval.
     * @param sql must be an SQL INSERT, UPDATE or DELETE statement or an SQL statement that returns nothing
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted row
     * @return either the row count for INSERT, UPDATE  or DELETE statements, or 0 for SQL statements that return nothing
     * @since MMBase 1.5, JDBC 1.4
     */
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.executeUpdate(sql, columnIndexes);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.executeUpdate(sql, columnIndexes);
            } else {
                throw sqe;
            }

        }
    }

    /**
     * Executes the given SQL statement and signals the driver that the auto-generated keys indicated in the given array
     * should be made available for retrieval.
     * @param sql must be an SQL INSERT, UPDATE or DELETE statement or an SQL statement that returns nothing
     * @param columnNames - an array of the names of the columns that should be returned from the inserted row
     * @return either the row count for INSERT, UPDATE  or DELETE statements, or 0 for SQL statements that return nothing
     * @since MMBase 1.5, JDBC 1.4
     */
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.executeUpdate(sql, columnNames);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.executeUpdate(sql, columnNames);
            } else {
                throw sqe;
            }

        }
    }

    /**
     * Executes the given SQL statement, which may return multiple results, and signals the driver that
     * any auto-generated keys should be made available for retrieval.
     * @param sql any SQL statement
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be made available for retrieval
     * @return true if the first result is a ResultSet  object; false if it is an update count or there are no results
     * @since MMBase 1.5, JDBC 1.4
     */
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.execute(sql, autoGeneratedKeys);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.execute(sql, autoGeneratedKeys);
            } else {
                throw sqe;
            }
        }
    }

    /**
     * Executes the given SQL statement, which may return multiple results, and signals the driver that
     * the auto-generated keys indicated in the given array should be made available for retrieval.
     * @param sql any SQL statement
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted row
     * @return true if the first result is a ResultSet  object; false if it is an update count or there are no results
     * @since MMBase 1.5, JDBC 1.4
     */
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.execute(sql, columnIndexes);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.execute(sql,  columnIndexes);
            } else {
                throw sqe;
            }
        }
    }

    /**
     * Executes the given SQL statement, which may return multiple results, and signals the driver that
     * the auto-generated keys indicated in the given array should be made available for retrieval.
     * @param sql any SQL statement
     * @param columnNames - an array of the names of the columns that should be returned from the inserted row
     * @return true if the first result is a ResultSet  object; false if it is an update count or there are no results
     * @since MMBase 1.5, JDBC 1.4
     */
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        try {
            parent.setLastSQL(sql);
            return s.execute(sql, columnNames);
        } catch (SQLException sqe) {
            Statement ts = checkAfterException();
            if (ts != null) { // there was something wrong with the connection, try it once again
                return ts.execute(sql, columnNames);
            } else {
                throw sqe;
            }
        }
    }

    /**
     * Retrieves the result set holdability for ResultSet objects generated by this Statement object.
     * @return either ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @since MMBase 1.5, JDBC 1.4
     */
    public int getResultSetHoldability() throws SQLException {
        return s.getResultSetHoldability();
    }


    public boolean isClosed() throws SQLException {
        return s == null;// || s.isClosed(); // java 6
    }
    public void setPoolable(boolean p) throws SQLException {
        if (s == null) throw new SQLException("Statement is closed");
        //s.setPoolable(p);
    }

    public boolean isPoolable() throws SQLException {
        if (s == null) throw new SQLException("Statement is closed");
        //return s.isPoolable(); // java 6
        return false;
    }

    public Object unwrap(Class iface) {
        return s;
    }

    public boolean isWrapperFor(Class iface) {
        return iface.isAssignableFrom(s.getClass());
    }

}

