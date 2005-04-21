/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.hitlisted.*;

/**
 * @author Hans Speijer
 */
public class PoolBuilder extends MMObjectBuilder {

	String cacheTableName;

	
	public Vector getPool(String pool, int status, int value) {
		SortedVector results = new SortedVector(new MMObjectCompare("number"));

		int poolId = 0;
		String poolSet = "{";
		Teasers teaserBuilder = (Teasers)mmb.getMMObject("teasers");

		System.out.println("PoolBuilder -> Getting Teasers for :"+pool);

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
			results = (new hitlisted()).convertResultSet(rs,results);
			stmt.close();
			con.close();	
			
			teaserBuilder.insertRelations(results);
			results.setCompare(new MMObjectDCompare("basedecay"));
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
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

			results = (new hitlisted()).convertResultSet(rs);

			stmt.close();
			con.close();
	
			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
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

			results = (new hitlisted()).convertResultSet(rs);
			
			stmt.close();
			con.close();
			} else {
				results=new Vector();
			}

		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(null);
		}		
		
		return (results);
	}
}
