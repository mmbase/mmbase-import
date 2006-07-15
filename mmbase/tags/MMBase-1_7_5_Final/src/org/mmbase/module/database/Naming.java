/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.ConnectionPoolDataSource;
import javax.naming.InitialContext;
import javax.naming.Context;

import org.mmbase.module.*;
import org.mmbase.util.logging.*;

/**
 * This class is used to retrieve a connection, which is provided by naming resources.
 * With the usage of naming resource, it is possible to configure the database resource
 * inside the application server and let the application server do the pooling. Since
 * this is a J2EE concept, this class provides support for usage of this.
 *
 * @author Eduard
 * @version $Id: Naming.java,v 1.3 2002-10-09 14:07:12 michiel Exp $
 */
public class Naming extends ProcessorModule implements JDBCInterface {
    private static Logger log = Logging.getLoggerInstance(Naming.class.getName());
    private static String PROPERTY_CONTEXT_NAME = "context";
    private static String PROPERTY_DATASOURCE_NAME = "datasource";
    private Object datasource = null;

    /** our own multi-connection implementation.. arrgg....*/
    private class NamingMultiConnection extends MultiConnection {
	/** constructor to set the connection which has to be retrieved */
	NamingMultiConnection(Connection con) {
	    super();
	    // this should take care of everything (we hope)
	    this.con = con;
	    state = CON_BUSY;
	    this.parent = null;
	}
	//public String getStateString()
	//public void setLastSQL(String sql)
	//public String getLastSQL()
	//public Statement createStatement() throws SQLException 
	//public PreparedStatement prepareStatement(String sql) throws SQLException 
	//public CallableStatement prepareCall(String sql) throws SQLException 
	//public String nativeSQL(String query) throws SQLException
	//public void setAutoCommit(boolean enableAutoCommit) throws SQLException 
	//public boolean getAutoCommit() throws SQLException
	//public void commit() throws SQLException 
	//public void rollback() throws SQLException 
	/** override the close, since the MultiConnection want to tell the pool that it is closed */
	public void close() throws SQLException {
	    con.close();
	}
	//public void realclose() throws SQLException
	//public boolean isClosed() throws SQLException
	//public DatabaseMetaData getMetaData() throws SQLException
	//public void setReadOnly(boolean readOnly) throws SQLException
	//public boolean isReadOnly() throws SQLException
	//public void setCatalog(String catalog) throws SQLException
	//public String getCatalog() throws SQLException
	//public void setTransactionIsolation(int level) throws SQLException 
	//public int getTransactionIsolation() throws SQLException
	//public SQLWarning getWarnings() throws SQLException
	//public void clearWarnings() throws SQLException
	//public boolean checkSQLError(Exception e)
	/** claim what? */
	public void claim() {
	}
	/** release what? */
	public void release() {
	}
	/** the usage of what? */
	public int getUsage() {
	    return 0;
	}
	/** the start time of what? */
	public int getStartTime() {
	    return 0;
	}
	/** the start time of what? */
	public long getStartTimeMillis() {
	    return 0;
	}
	//public String toString()
	//public CallableStatement prepareCall(String sql, int i, int y) throws SQLException
	//public void setTypeMap(Map mp) throws SQLException
	//public Map getTypeMap() throws SQLException
	//public Statement createStatement(int i,int y) throws SQLException
	//public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException
	// The 1.4 methods are also skipped..
    }

    /**
     * Init this module. Will check if properties are available and try to get a datasource, to 
     * test if we can use it.
     */
    public void init() {
	String context = getInitParameter(PROPERTY_CONTEXT_NAME);
	if(context == null) throw new RuntimeException("the property '"+PROPERTY_CONTEXT_NAME+"' was not set");
	String source = getInitParameter(PROPERTY_DATASOURCE_NAME);
	if(source == null) throw new RuntimeException("the property '"+PROPERTY_CONTEXT_NAME+"' was not set");
	
	// do the naming stuff..
	try {
	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup(context);
	    datasource = envCtx.lookup(source);
	    if(datasource == null) {
		String msg = "datasource was null for context:" + context + " with source:" + source;
		log.error(msg);
		throw new RuntimeException(msg);
	    }
	    if(datasource instanceof ConnectionPoolDataSource) {
		ConnectionPoolDataSource ds = (ConnectionPoolDataSource)datasource;	       
		log.info("Using the interface:" + ConnectionPoolDataSource.class.getName() + "(implemented by:" + ds.getClass().getName() + " to get new database connections(time out: " + ds.getLoginTimeout()  + " seconds).");
	    }
	    else if(datasource instanceof DataSource) {
		log.info("Using the interface:" + DataSource.class.getName() + "(implemented by:" + datasource.getClass().getName() + " to get new database connections.");
	    }
	    else {
		String msg = "Dont know how to retrieve a connection from datasource:" + datasource.getClass().getName();
		log.error(msg);
		throw new RuntimeException(msg);
	    }

	    // try to get an connection, so we can see if it all works..
	    Connection con = getConnection();
	    if(con == null) {
		String msg = "Test run of retrieving a test-run failed.";
		log.error(msg);
		throw new RuntimeException(msg);
	    }
	    // closing a connection is very important!
	    con.close();

	} catch(javax.naming.NamingException ne) {
	    String msg = "The following error occured while trying to initalise the datasource for context:'" + context + "' datasource:'" + source + "' :\n" + Logging.stackTrace(ne);
	    log.error(msg);
	    throw new RuntimeException(msg);
	}
	catch(java.sql.SQLException se) {
	    String msg = "The following error occured while trying to retrieve a connection from the datasource for context:'" + context + "' datasource:'" + source + "' :\n" + Logging.stackTrace(se);
	    log.error(msg);
	    throw new RuntimeException(msg);
	}
    }

    /**
     * is a reload the same as an init?
     */
    public void reload() {
	init();
    }

    /**
     * retrieves an connection to the database, depending on the class which is used as datasource
     * @return Connection A connection to the database
     */
    private Connection getConnection() throws java.sql.SQLException {     
        if (datasource == null) {
            log.error("Getting connection before init of jdbc module. Trying to reinitalize the database layer.");
            init();            
        }

	if(datasource instanceof ConnectionPoolDataSource) {
	    ConnectionPoolDataSource ds = (ConnectionPoolDataSource) datasource;
	    PooledConnection pc = ds.getPooledConnection();
	    return pc.getConnection();
	}
	else if(datasource instanceof DataSource) {
	    DataSource ds = (DataSource) datasource;
	    return ds.getConnection();
	}
	else {
	    String msg = "Dont know how to retrieve a connection from datasource:" + (datasource != null ? datasource : datasource.getClass().getName());
	    log.error(msg);
	    throw new RuntimeException(msg);
	}
    }
    public MultiConnection getConnection(String url, String name, String password) throws SQLException {
	return new NamingMultiConnection(getConnection());
    }
    public MultiConnection getConnection(String url) throws SQLException {
	return new NamingMultiConnection(getConnection());
    }
    
    public Connection getDirectConnection(String url) throws SQLException {
	return getConnection();
    }
    public Connection getDirectConnection(String url,String name,String password) throws SQLException {
	return getConnection();
    }

    // below all the things we dont use..
    public void unload() {}
    public void shutdown() {}
    public String makeUrl(){return null;}
    public String makeUrl(String dbm) {return null;}
    public String makeUrl(String host,String dbm) {return null;}
    public String makeUrl(String host,int port,String dbm) {return null;}
    public String getUser() {return null;}
    public String getPassword() {return null;}
    public String getDatabaseName(){return null;}
    public void checkTime(){}
}
