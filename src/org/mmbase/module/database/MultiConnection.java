/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.math.*;
import java.net.*;
import java.io.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiConnection is a replacement class for Connection it provides you a
 * multiplexed and reuseable connections from the connection pool
 */
public class MultiConnection implements Connection
{

    private static Logger log = Logging.getLoggerInstance(MultiConnection.class.getName());
   
   Connection con=null;
   MultiPool parent;
   private int startTime=0;
   String lastSql;
   private int usage=0;
   public int state=0;

    // states
    public final static int CON_UNUSED = 0;
    public final static int CON_BUSY = 1;
    public final static int CON_FINISHED = 2;
    public final static int CON_FAILED = 3;

  /**
   *
   */
   MultiConnection(MultiPool parent,Connection con) {
		this.con=con;
		this.parent=parent;
		state=CON_UNUSED;
   } 

   public String getStateString() {
	if (state==CON_FINISHED) {
		return("Finished");
	} else if (state==CON_BUSY) {
		return("Busy");
	} else if (state==CON_FAILED) {
		return("Failed");
	} else if (state==CON_UNUSED) {
		return("Unused");
	}
	return("Unknown");
   }

   /**
   */
   public void setLastSQL(String sql) {
		lastSql=sql;
		log.debug("SQL : "+sql);
		state=CON_BUSY;
   }

   /**
   * i changed this jun 2001, since i didn't understand
   * the logic. I changed it to keep track of a state
   * daniel.
   */
   public void delLastSQL() {
		lastSql=null;
   }

   public String getLastSQL() {
		return(lastSql);
   }

   /**
	* createStatement returns an SQL Statement object
	*/
   public Statement createStatement() throws SQLException {
		MultiStatement s=new MultiStatement(this,con.createStatement());
		return(s);
   }

   /**
   * prepareStatement creates a pre-compiled SQL PreparedStatement object.
   */
   public PreparedStatement prepareStatement(String sql) throws SQLException {
		setLastSQL(sql);
		return(con.prepareStatement(sql));
   }

   // prepareCall create a pre-compiled SQL statement that is
   // a call on a stored procedure.
   public CallableStatement prepareCall(String sql) throws SQLException {
		setLastSQL(sql);
	  return(con.prepareCall(sql));
   }

   // Convert the given generic SQL statement to the drivers native SQL.
   public String nativeSQL(String query) throws SQLException {
		setLastSQL(query);
      return (con.nativeSQL(query));
   }

   /** 
   * If "autoCommit" is true, then all subsequent SQL statements will
   * be executed and committed as individual transactions.  Otherwise
   * (if "autoCommit" is false) then subsequent SQL statements will
   * all be part of the same transaction , which must be explicitly
   * committed with either a "commit" or "rollback" call.
   * By default new connections are initialized with autoCommit "true".
   */
   public void setAutoCommit(boolean enableAutoCommit) throws SQLException {
      con.setAutoCommit(enableAutoCommit);
   }

	
   /** 
   * get AutoCommit mode
   */
   public boolean getAutoCommit() throws SQLException
   {
      return(con.getAutoCommit());
   }


   /** 
   * Perform commit
   */
   public void commit() throws SQLException {
	  con.commit();
   }

   /** 
   * Perform rollback
   */
   public void rollback() throws SQLException {
	  con.rollback();
   }

   /** 
   * Close connections
   */
   public void close() throws SQLException {
		state=CON_FINISHED;
		parent.putBack(this);
   }


   /** 
   * Close connections
   */
   public void realclose() throws SQLException {
		con.close();
   }

   /**
   * isClosed returns true if the connection is closed, which can
   * occur either due to an explicit call on "close" or due to
   * some fatal error on the connection.
   */
   public boolean isClosed() throws SQLException {
	  return(con.isClosed());	
   }

   /**
   * Advanced features:
   * You can obtain a DatabaseMetaData object to get information
   * about the target database.
   */
   public DatabaseMetaData getMetaData() throws SQLException {
	  return(con.getMetaData());	
   }

   /**
   * You can put a connection in read-only mode as a hint to enable
   * database optimizations.  Note that setReadOnly cannot be called
   * while in the middle of a transaction.
   */
   public void setReadOnly(boolean readOnly) throws SQLException {
	  con.setReadOnly(readOnly);
   }

   /**
   * Is this database readonly ?
   */
   public boolean isReadOnly() throws SQLException {
	  return(isReadOnly());
   }

   /**
   * The "catalog" selects a sub-space of the target database.
   */
   public void setCatalog(String catalog) throws SQLException {
	  con.setCatalog(catalog);	
   }

   /**
   * The "catalog" name
   */
   public String getCatalog() throws SQLException {
	  return(con.getCatalog());
   }

   /**
   * You can call the following method to try to change the transaction
   * isolation level on a newly opened connection, using one of the
   * TRANSACTION_* values.  Use the DatabaseMetaData class to find what
   * isolation levels are supported by the current database.
   * Note that setTransactionIsolation cannot be called while in the
   * middle of a transaction.
   */
   public void setTransactionIsolation(int level) throws SQLException {
	  con.setTransactionIsolation(level);
   }


   /**
   * 
   */
   public int getTransactionIsolation() throws SQLException {
	  return(con.getTransactionIsolation());
   }

   /**
   * getWarnings will return any warning information related to
   * the current connection.  Note that SQLWarning may be a chain.
   */
   public SQLWarning getWarnings() throws SQLException {
      return(con.getWarnings());
   }


   /**
   * clear Warnings
   */
   public void clearWarnings() throws SQLException {
	  con.clearWarnings();
   }

   public boolean checkSQLError(Exception e) {
		log.error("JDBC CHECK ERROR="+e.toString());	
		return(true);
   }

	public void claim() {
		usage++;
		startTime=(int)(System.currentTimeMillis()/1000);
	}

	public void release() {
		startTime=0;
	}

   public int getUsage() {
		return(usage);
	}

	public int getStartTime() {
		return(startTime);
	}

	public String toString() {
		return("'"+getLastSQL()+"'@"+hashCode());
	}


	// new ones 

   // prepareCall create a pre-compiled SQL statement that is
   // a call on a stored procedure.
   public CallableStatement prepareCall(String sql, int i, int y) throws SQLException {
		setLastSQL(sql);
	  return(con.prepareCall(sql,i,y));
   }

   public void setTypeMap(Map mp) throws SQLException {
 		con.setTypeMap(mp);
	}

   public Map getTypeMap() throws SQLException {
 		return(con.getTypeMap());
	}

   /**
	* createStatement returns an SQL Statement object
	*/
   public Statement createStatement(int i,int y) throws SQLException {
		MultiStatement s=new MultiStatement(this,con.createStatement(i,y));
		return(s);
   }


   /**
   * prepareStatement creates a pre-compiled SQL PreparedStatement object.
   */
   public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException {
		setLastSQL(sql);
		return(con.prepareStatement(sql,i,y));
   }
}
