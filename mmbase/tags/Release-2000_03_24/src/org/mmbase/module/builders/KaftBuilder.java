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
public class KaftBuilder extends MMObjectBuilder {

	LRUHashtable teaserCache=new LRUHashtable();

	public Vector getTeaser(String pool, String pos) {
		// added a cache
		Vector results = (Vector)teaserCache.get(pool+pos);
		if (results!=null) return(results);
		results = new Vector();
		int poolId = 0;
		Teasers teaserBuilder = (Teasers)mmb.getMMObject("teasers");		

		System.out.println("Kaftbuilder ->  Getting Teasers for :"+pool);

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
								   "SELECT b.number, b.title, b.body, b.value FROM "
								   +mmb.baseName+"_"+mmb.getMMObject("posrel").tableName+" a, "
								   +mmb.baseName+"_"+mmb.getMMObject("teasers").tableName+" b"
								   +" WHERE a.snumber = "+poolId+" AND b.number = a.dnumber AND a.pos ="+pos);

			results = (new hitlisted()).convertResultSet(rs);
			stmt.close();
			con.close();	
			
			teaserBuilder.insertRelations(results);

		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(null);
		}		
		teaserCache.put(pool+pos,results);
		return (results);
	}

}
