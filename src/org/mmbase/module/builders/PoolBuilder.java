/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Hans Speijer
 */
public class PoolBuilder extends MMObjectBuilder {
	
    private static Logger log = Logging.getLoggerInstance(PoolBuilder.class.getName()); 

	String cacheTableName;

	
	public Vector getPool(String pool, int status, int value) {
		SortedVector results = new SortedVector(new MMObjectCompare("number"));

		int poolId = 0;
		String poolSet = "{";
		Teasers teaserBuilder = (Teasers)mmb.getMMObject("teasers");

		log.info("Getting Teasers for :" + pool);

		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_"+tableName+" WHERE name = '"+pool+"'");

			while(rs.next()) {
				poolId = rs.getInt(1);
			}
			stmt.close();
			con.close();

			con=mmb.getConnection();
			stmt = con.createStatement();

			rs = stmt.executeQuery(
								   "SELECT a.decay, a.basedecay, a.online, b.number, b.title, b.body, b.value FROM "
								   +mmb.baseName+"_"+mmb.getMMObject("decayrel").tableName+" a, "
								   +mmb.baseName+"_"+mmb.getMMObject("teasers").tableName+" b"
								   +" WHERE (a.snumber = "+poolId+" OR a.dnumber = "+poolId+")  AND (b.number = a.dnumber or b.number = a.snumber ) AND b.state >"+status+" AND b.value >"+value);
			results = convertResultSet(rs,results);
			stmt.close();
			con.close();	
			
			teaserBuilder.insertRelations(results);
			results.setCompare(new MMObjectDCompare("basedecay"));
		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
			return(null);
		}		
	
		return (results);
	}

	public Vector searchFlat(String sqlWhere) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+cacheTableName+" WHERE "+sqlWhere);
			Vector results;

			results = convertResultSet(rs);

			stmt.close();
			con.close();
	
			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
			return(null);
		}		
	}

	public Vector getChildren(String pool) {
		Vector results;
		int poolId = 0;
		String poolSet = "(";
		String insRelName = mmb.getMMObject("insrel").tableName;

		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_"+tableName+" WHERE name = '"+pool+"'");

			while(rs.next()) {
				poolId = rs.getInt(1);
			}
			stmt.close();
			con.close();

			con=mmb.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT  dnumber FROM "+mmb.baseName+"_"+insRelName+" WHERE snumber = "+poolId);
			
			while(rs.next()) {
				if (poolSet.equals("(")) {
					poolSet += rs.getInt(1);
				}
				else {
					poolSet += ","+rs.getInt(1);
				}
			}
			poolSet += ")";
			stmt.close();
			con.close();

			if (!poolSet.equals("()")) {
			con=mmb.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT name FROM "+mmb.baseName+"_"+tableName+" WHERE number in "+poolSet);

			results = convertResultSet(rs);
			
			stmt.close();
			con.close();
			} else {
				results=new Vector();
			}

		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
			return(null);
		}		
		
		return (results);
	}



	/**
	 * Converts a result set into a Vector containing MMObjectNodes for the 
	 * different items in the JDBC Result Set
	 */
	public Vector convertResultSet(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();	
			MMObjectNode node;
			Vector results=new Vector();
			
			while(rs.next()) {
				node = new MMObjectNode(this);
				for (int index = 1; index <= numberOfColumns; index++) {
					String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,fieldname,rs,index);
				}
				results.addElement(node);
			}	

			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
		}
		
		return (null);		 
	}

	/**
	 * Converts a result set into a Vector containing MMObjectNodes for the 
	 * different items in the JDBC Result Set
	 */
	public Hashtable convertResultSet(ResultSet rs, String columnName) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();	
			MMObjectNode node;
			Hashtable results=new Hashtable();
			Object key = null;
			
			while(rs.next()) {
				node = new MMObjectNode(this);
				for (int index = 1; index <= numberOfColumns; index++) {
					//String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,fieldname,rs,index);
				}
				results.put(key, node);
			}	

			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
		}
		
		return (null);		 
	}

	public SortedVector convertResultSet(ResultSet rs, SortedVector sv) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();	
			MMObjectNode node;
			

			while(rs.next()) {
				node = new MMObjectNode(this);
				for (int index = 1; index <= numberOfColumns; index++) {
					//String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,fieldname,rs,index);
				}
				sv.addUniqueSorted(node);
			}	

			return (sv);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			log.error(Logging.stackTrace(e));
		}
		
		return (null);
	}

}
