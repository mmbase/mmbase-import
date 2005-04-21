/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.hitlisted;

import java.util.*;
import java.sql.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.support.*;
import org.mmbase.module.database.MultiConnection;

/**
 * We feel that all these methods should be removed/zapped/replaced
 * but need some time to make them go away so we all place them in
 * this packages. In other words its bad to use them and the other
 * packages provide better ways than the ones found in this class.
 */
public class hitlisted extends MMObjectBuilder {

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
					node=database.decodeDBnodeField(node,type,fieldname,rs,index);
				}
				results.addElement(node);
			}	

			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();	
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
					String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,type,fieldname,rs,index);
				}
				results.put(key, node);
			}	

			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();	
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
					String type=rsmd.getColumnTypeName(index);	
					String fieldname=rsmd.getColumnName(index);
					node=database.decodeDBnodeField(node,type,fieldname,rs,index);
				}
				sv.addUniqueSorted(node);
			}	

			return (sv);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();	
		}
		
		return (null);
	}

}
