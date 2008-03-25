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
 * MMBase wraps java.sql.Connection. This interface is more or less that plus java.sql.Connection of
 * java 1.5. (See also MMB-1409).
 *
 */
public interface MultiConnection {


    public void setLastSQL(String sql);
    public String getLastSQL();
    public Exception getStackTrace();

    public String getStateString();

    public void resetUsage();
    public void claim();
    public MultiPool getParent();

    public Statement createStatement() throws SQLException;
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException;
    public Statement createStatement(int type, int concurrency, int holdability) throws SQLException;
    public PreparedStatement prepareStatement(String sql) throws SQLException;
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException;
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException;
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException;
    public PreparedStatement prepareStatement(String sql, int type, int concurrency, int holdability) throws SQLException;
    public CallableStatement prepareCall(String sql) throws SQLException;
    public String nativeSQL(String query) throws SQLException;
    public void setAutoCommit(boolean enableAutoCommit) throws SQLException;
    public boolean getAutoCommit() throws SQLException;
    public void commit() throws SQLException;
    public void rollback() throws SQLException;
    public void close() throws SQLException;
    public boolean isClosed() throws SQLException;
    public DatabaseMetaData getMetaData() throws SQLException;
    public void setReadOnly(boolean readOnly) throws SQLException;
    public boolean isReadOnly() throws SQLException;
    public void setCatalog(String catalog) throws SQLException;
    public String getCatalog() throws SQLException;
    public void setTransactionIsolation(int level) throws SQLException;
    public int getTransactionIsolation() throws SQLException;
    public SQLWarning getWarnings() throws SQLException;
    public void clearWarnings() throws SQLException;
    public CallableStatement prepareCall(String sql, int i, int y) throws SQLException;
    public void setTypeMap(Map<String,Class<?>> mp) throws SQLException;
    public Map<String,Class<?>> getTypeMap() throws SQLException;
    public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException;
    public void setHoldability(int holdability) throws SQLException;
    public int getHoldability() throws SQLException;
    public Savepoint setSavepoint() throws SQLException;
    public Savepoint setSavepoint(String name) throws SQLException;
    public void rollback(Savepoint savepoint) throws SQLException;
    public void releaseSavepoint(Savepoint savepoint) throws SQLException;
    public CallableStatement prepareCall(String sql, int type, int concurrency, int holdability) throws SQLException;
    public boolean checkAfterException() throws SQLException;
    public void realclose() throws SQLException;
    public void release();
    public int getUsage();
    public int getStartTime();
    public long getStartTimeMillis();
    public <T> T unwrap(Class<T> iface);
    public void wrap(Connection con);


}


