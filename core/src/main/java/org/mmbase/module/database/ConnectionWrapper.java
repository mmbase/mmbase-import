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
 * Wraps  a java.sql.Connection object. Extending this makes it possible to intercept calls.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public abstract class ConnectionWrapper { //implements Connection {
    /**
     * The wrapped connection
     */
    protected Connection con;

    public  ConnectionWrapper(Connection c) {
        con = c;
    }

    /**
     * Called just before every prepare statement. Can be overridden, because this default implementation is empty.
     */
    protected void setLastSQL(String sql) {
    }
    /**
     * @see java.sql.Connection#createStatement()
     */
    public Statement createStatement() throws SQLException {
        return con.createStatement();
    }
    /**
     * @see java.sql.Connection#createStatement(int, int)
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return con.createStatement(resultSetType, resultSetConcurrency);
    }
    /**
     * @see java.sql.Connection#createStatement(int, int, int)
     */
    public Statement createStatement(int type, int concurrency, int holdability) throws SQLException {
        return con.createStatement(type, concurrency, holdability);
    }
    /**
     * @see java.sql.Connection#prepareStatement(String)
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql);
    }
    /**
     * @see java.sql.Connection#prepareStatement(String, int)
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * @see java.sql.Connection#prepareStatement(String, int[])
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, columnIndexes);
    }

    /**
     * @see java.sql.Connection#prepareStatement(String, String[])
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, columnNames);
    }
    /**
     * @see java.sql.Connection#prepareStatement(String, int, int, int)
     */
    public PreparedStatement prepareStatement(String sql, int type, int concurrency, int holdability) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, type, concurrency, holdability);
    }

    /**
     * @see java.sql.Connection#prepareCall(String)
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql);
    }
    /**
     * @see java.sql.Connection#nativeSQL(String)
     */
    public String nativeSQL(String query) throws SQLException {
        setLastSQL(query);
        return con.nativeSQL(query);
    }
    /**
     * @see java.sql.Connection#setAutoCommit(boolean)
     */
    public void setAutoCommit(boolean enableAutoCommit) throws SQLException {
        con.setAutoCommit(enableAutoCommit);
    }
    /**
     * @see java.sql.Connection#getAutoCommit()
     */
    public boolean getAutoCommit() throws SQLException {
        return con.getAutoCommit();
    }
    /**
     * @see java.sql.Connection#commit()
     */
    public void commit() throws SQLException {
        con.commit();
    }

    /**
     * @see java.sql.Connection#rollback()
     */
    public void rollback() throws SQLException {
        con.rollback();
    }
    /**
     * @see java.sql.Connection#close()
     */
    public void close() throws SQLException {
        con.close();
    }

    /**
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() throws SQLException {
        return con.isClosed();
    }


    /**
     * @see java.sql.Connection#getMetaData()
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return con.getMetaData();
    }


    /**
     * @see java.sql.Connection#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        con.setReadOnly(readOnly);
    }

    /**
     * @see java.sql.Connection#isReadOnly()
     */
    public boolean isReadOnly() throws SQLException {
        return con.isReadOnly();
    }
    /**
     * @see java.sql.Connection#setCatalog(String)
     */
    public void setCatalog(String catalog) throws SQLException {
        con.setCatalog(catalog);
    }

    /**
     * @see java.sql.Connection#getCatalog()
     */
    public String getCatalog() throws SQLException {
        return con.getCatalog();
    }

    /**
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    public void setTransactionIsolation(int level) throws SQLException {
        con.setTransactionIsolation(level);
    }

    /**
     * @see java.sql.Connection#getTransactionIsolation()
     */
    public int getTransactionIsolation() throws SQLException {
        return con.getTransactionIsolation();
    }

    /**
     * @see java.sql.Connection#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {
        return con.getWarnings();
    }

    /**
     * clear Warnings
     * @see java.sql.Connection#clearWarnings()
     */
    public void clearWarnings() throws SQLException {
        con.clearWarnings();
    }

    /**
     * @see java.sql.Connection#prepareCall(String, int, int)
     */
    public CallableStatement prepareCall(String sql, int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql,i,y);
    }

    /**
     * @see java.sql.Connection#setTypeMap(Map)
     */
    public void setTypeMap(Map<String,Class<?>> mp) throws SQLException {
        con.setTypeMap(mp);
    }

    /**
     * @see java.sql.Connection#getTypeMap()
     */
    public Map<String,Class<?>> getTypeMap() throws SQLException {
        return con.getTypeMap();
    }


    /**
     * @see java.sql.Connection#prepareStatement(String, int, int)
     */
    public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql,i,y);
    }

    /**
     * @see java.sql.Connection#setHoldability(int)
     */
    public void setHoldability(int holdability) throws SQLException {
        con.setHoldability(holdability);
    }

    /**
     * @see java.sql.Connection#getHoldability()
     */
    public int getHoldability() throws SQLException {
        return con.getHoldability();
    }

    /**
     * @see java.sql.Connection#setSavepoint()
     */
    public Savepoint setSavepoint() throws SQLException {
        return con.setSavepoint();
    }

    /**
     * @see java.sql.Connection#setSavepoint(String)
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        return con.setSavepoint(name);
    }

    /**
     * @see java.sql.Connection#rollback(Savepoint)
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        con.rollback(savepoint);
    }
    /**
     * @see java.sql.Connection#releaseSavepoint(Savepoint)
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        con.releaseSavepoint(savepoint);
    }


    /**
     * @see java.sql.Connection#prepareCall(String, int, int, int)
     */
    public CallableStatement prepareCall(String sql, int type, int concurrency, int holdability) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql, type, concurrency, holdability);
    }

    /*
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException();
    }
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException();
    }
    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException();
    }
    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException();
    }
    */
    public boolean isValid(int i) throws SQLException {
        throw new UnsupportedOperationException();
    }
   
    /*
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new UnsupportedOperationException();
    }
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new UnsupportedOperationException();
    }
    */

    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }
    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException();
    }

    
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    public <T> T unwrap(Class<T> iface) {
        return (T) con;
    }

    public boolean isWrapperFor(Class<?> iface) {
        return iface.isAssignableFrom(con.getClass());
    }

}