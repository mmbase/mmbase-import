/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;


/**
 * MultiStatement is a replacement class for Statement it provides you a
 * multiplexed and reuseable statements you obtain these from MultiConnection
 */
public class MultiStatement implements Statement
{
  MultiConnection parent;
  Statement s;

  MultiStatement(MultiConnection parent,Statement s) {
    this.parent = parent;
	this.s=s;
  }
  
   public int executeUpdate(String sql) throws SQLException {
		parent.setLastSQL(sql);
		return(s.executeUpdate(sql));
   }

   public void close() throws SQLException {
		s.close();
		// anyone knows what this does ?	parent.delLastSQL();
		s=null; // lets asign it to null to be sure
   }

   public int getMaxFieldSize() throws SQLException {
		return(s.getMaxFieldSize());
   }
   public void setMaxFieldSize(int max) throws SQLException {
		s.setMaxFieldSize(max);
   }

   public int getMaxRows() throws SQLException {
		return(s.getMaxRows());
   }

   public void setMaxRows(int max) throws SQLException {
		s.setMaxRows(max);
   }

   public void setEscapeProcessing(boolean enable) throws SQLException {
		s.setEscapeProcessing(enable);
   }

   public int getQueryTimeout() throws SQLException {
		return(s.getQueryTimeout());
   }

   public void setQueryTimeout(int seconds) throws SQLException {
		s.setQueryTimeout(seconds);
   }

   public void cancel() throws SQLException {
		s.cancel();
   }

   public SQLWarning getWarnings() throws SQLException {
		return(s.getWarnings());	
   }

   public void clearWarnings() throws SQLException {
		s.clearWarnings();	
   }

   public boolean execute(String sql) throws SQLException {
		parent.setLastSQL(sql);
		return(s.execute(sql));	
   }

   public ResultSet getResultSet() throws SQLException {
		return(s.getResultSet());	
   }

   public int getUpdateCount() throws SQLException {
		return(s.getUpdateCount());	
   }

   public boolean getMoreResults() throws SQLException {
		return(s.getMoreResults());	
   }

   public void setCursorName(String name) throws SQLException {
		s.setCursorName(name);
   }


   public ResultSet executeQuery(String sql) throws SQLException {
		parent.setLastSQL(sql);
		return(s.executeQuery(sql));
   }


	// JDBC new ones

   public int[] executeBatch() throws SQLException {
		return(s.executeBatch());
   }

 
   public void setFetchDirection(int dir) throws SQLException {
		s.setFetchDirection(dir);
   }

   public int getFetchDirection() throws SQLException {
		return(s.getFetchDirection());
   }

   public int getResultSetConcurrency() throws SQLException {
		return(s.getResultSetConcurrency());
   }

	public int getResultSetType() throws SQLException {
		return(s.getResultSetType());
	}


	public void addBatch(String sql) throws SQLException {
		s.addBatch(sql);
	}

	public void clearBatch() throws SQLException {
		s.clearBatch();
	}

	public Connection getConnection() throws SQLException {
		return(s.getConnection());
	}

	public int getFetchSize() throws SQLException {
		return(s.getFetchSize());
	}

	public void setFetchSize(int i) throws SQLException {
		s.setFetchSize(i);
	}
}

