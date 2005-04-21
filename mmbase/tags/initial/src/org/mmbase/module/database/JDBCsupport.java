/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.database;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import org.mmbase.util.*;

/**
 * JDBC support routes not sure if they are needed anymore (hitlisted)
 *
 * @author Rico Jansen
 * @version 10 Jun 1998
 */
public class JDBCsupport {

	public static boolean update(Connection connection,String query)  throws SQLException{
		return(update(connection,query,false));
	}
	public static boolean update(Connection connection,String query,boolean wait)  throws SQLException{
		boolean rtn=false;
		Statement statement;
		statement=connection.createStatement();
		statement.executeUpdate(query);
		if (wait) statement.getUpdateCount();
		statement.close();
		rtn=true;
		return(rtn);
	}

	public static Vector selectflat(Connection connection,String query)  throws SQLException{
		if (connection!=null) {
			return(select(connection,query,true));
		} else {
			System.out.println("JDBCsupport selectflat no connection ? ("+query+")");
			return(null);
		}
	}

	public static Vector select(Connection connection,String query)  throws SQLException{
		if (connection!=null) {
			return(select(connection,query,false));
		} else {
			System.out.println("JDBCsupport select no connection ? ("+query+")");
			return(null);
		}	
	}

	public static Vector select(Connection connection,String query,boolean flat) throws SQLException {

		Vector rtn=null;
		if (query!=null) {
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			Vector results=new Vector();
			Vector row=new Vector();
			Integer number;
			String tmp;
			String fieldname;String fieldtype;
			ResultSetMetaData rd;

			while(rs.next()) {
				// create a new object and add it to the result vector
				if (!flat) row=new Vector();
				rd=rs.getMetaData();
				for (int i=1;i<=rd.getColumnCount();i++) {
					fieldname=rd.getColumnName(i);	
					fieldtype=rd.getColumnTypeName(i);	
					if (fieldtype.equals("varchar")|| fieldtype.equals("char") || fieldtype.equals("lvarchar")) {
						tmp=rs.getString(i);
						if (tmp==null) {
							row.addElement("");
						} else {
							row.addElement(tmp);
						}
					} else if (fieldtype.equals("clob") || fieldtype.equals("text")) {
						row.addElement(fetchCLOB(rs,i));
					} else if (fieldtype.equals("integer")||fieldtype.equals("int")||fieldtype.equals("smallint")) {
						row.addElement(""+rs.getInt(i));
					} else if (fieldtype.equals("decimal")) {
						row.addElement(""+rs.getBigDecimal(i,0));
					} else if (fieldtype.equals("large_object")) {
						tmp=rs.getString(i);
						if (tmp==null) {
							row.addElement("");
						} else {
							row.addElement(tmp);
						}
					} else if (fieldtype.equals("large_text")) {
						tmp=rs.getString(i);
						if (tmp==null) {
							row.addElement("");
						} else {
							row.addElement(tmp);
						}
					} else if (fieldtype.startsWith("date")) {
						tmp=""+DateSupport.date2date((int)((rs.getDate(i).getTime())/1000));
						if (tmp==null) {
							row.addElement("");
						} else {
							row.addElement(tmp);
						}
					} else {
						System.out.println("JDBCsupport -> Unknown fieldtype fieldname="+fieldname+" type="+fieldtype);
						System.out.println("JDBCsupport -> Query = "+query);
					}
				}
				if (!flat) results.addElement(row);
			}	
			stmt.close();
			// return the results
			if (flat) results=row;
			if (results.size()>0) rtn=results;
		} else {
			System.out.println("JDBCsupport null query");
		}
		return(rtn);
	}

	public static String fetchTEXT(ResultSet rs,int idx) {
		return(fetchCLOB(rs,idx));
	}

	public static String fetchCLOB(ResultSet rs,int idx) {
		String str=null;
		InputStream inp;
		DataInputStream input;
		byte[] isochars;
		int siz;

		try {
			inp=rs.getAsciiStream(idx);
			siz=inp.available(); // DIRTY
			input=new DataInputStream(inp);
			System.out.println("JDBCsupport fetch CLOB/TEXT size : "+siz);
			isochars=new byte[siz];
			input.readFully(isochars);
			str=new String(isochars,"ISO-8859-1");
			inp.close();
			input.close();
		} catch (Exception e) {
			System.out.println("JDBCsupport CLOB exception "+e);
			e.printStackTrace();
		}
		return(str);
	}

	public static Connection getConnection(JDBCInterface jdbc,String dbm) {
		Connection conn=null;
		try {
			conn=jdbc.getConnection(jdbc.makeUrl(dbm));
		} catch(Exception e) {
			System.out.println("JDBCsupport -> can't get database connection "+e);
		}
		return(conn);
	}
	public void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println("JDBCsupport -> can't close "+e);
		}
	}

	public static void setLockMode(Connection con,int sec) {
		/*
		PreparedStatement statement;
		try {
			if (sec>0) {
				statement=con.prepareStatement("set lock mode to wait "+sec);
			} else {
				statement=con.prepareStatement("set lock mode to wait");
			}
			statement.executeUpdate();
	        statement.close();
		} catch (Exception e) {
			System.out.println("JDBCsupport : failed to set lock mode "+e);
			e.printStackTrace();
		}
		*/
	}
}
