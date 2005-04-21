/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/

package org.mmbase.module.database;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;

/**
 * Class to interface our database calls to JDBC
 * Uses the JDBC module.
 * 
 * @see org.mmbase.module.database.DBMdispatcher
 * @see org.mmbase.module.database.JDBC
 * @see org.mmbase.module.database.DBMaccessInterface
 *
 * @author      Rico Jansen
 * @version		13 Feb 1997
 */
public class JDBCAccess implements DBMaccessInterface {

	public boolean state_up = false;
	int maxretries=10;
	int retry=maxretries;
	int sleeptime=500;
	int currentsleep=sleeptime;
	boolean debug=false;


	Connection connection;

	String dbm=new String("vpro");
	int dbmsocket=1112;
	String dbmhost=new String("www.vpro.nl");

	String jdbcurl;
	JDBCInterface jdbcmodule;

	Hashtable properties;

	/**
	 * Create an instance, and set the properties we need
	 */
	public JDBCAccess() {
	}

	/**
	 * Get the properties from DBMdispatcher and get them out
	 */
	void getproperties() {
		int tmpi;
		String tmpstr;

		tmpi=Integer.parseInt((String)properties.get("port"));
		if (tmpi>0) {
			dbmsocket=tmpi;
		}
		tmpi=Integer.parseInt((String)properties.get("retries"));
		if (tmpi>1) { // should try atleast once
			maxretries=tmpi;
		}
		tmpstr=(String)properties.get("database");
		if (tmpstr!=null) {
			dbm=tmpstr;
		}
		tmpstr=(String)properties.get("host");
		if (tmpstr!=null) {
			dbmhost=tmpstr;
		}
		tmpstr=(String)properties.get("debug");
		if (tmpstr!=null) {
			debug=tmpstr.equalsIgnoreCase("true");
		}
		jdbcmodule=(JDBCInterface)properties.get("JDBC");
		jdbcurl=jdbcmodule.makeUrl(dbmhost,dbmsocket,dbm);
	}

	void report() {
		System.out.println("JDBCaccess started for "+dbm+" on "+dbmhost+":"+dbmsocket);
	}

	/**
	 * Initialize the properties and report
	 */
	public void init(Hashtable properties) {
		this.properties=properties;
		getproperties();
		report();
	}

	/**
	 * Start a connection
	 */
	public synchronized boolean start_connection() {
		int rsp;
		boolean error=false;

		try {
			connection=DriverManager.getConnection(jdbcurl);
			state_up=true;
		} catch (Exception e) {
			state_up=false;
		}
		return(state_up);
	}

	/**
	 * Close the connection
	 */
	public synchronized void stop_connection() {
		try {
			connection.close();
			connection=null;
		} catch(SQLException e) {
			System.out.println("Database connection gave errors");
			e.printStackTrace();
		}
		state_up=false;
	}

	/**
	 * Recover the connection
	 */
	public synchronized boolean retry_connection() {

		while(state_up==false && retry>0) {
			System.out.println("Retrying connection for database "+dbm+" on "+dbmhost+":"+dbmsocket+" ....");
			try { 
				if (connection!=null) connection.close();
			} catch (SQLException e) {
				System.out.println("Close connection error");
				e.printStackTrace();
			}
			state_up=start_connection();
			if (state_up==true) {
				retry=maxretries;
				currentsleep=sleeptime;
			} else {
				System.out.println("Sleeping "+currentsleep);
				try {
					Thread.sleep(currentsleep);
				} catch (Exception e) {};
				currentsleep+=sleeptime;
				retry--;
			}
		}
		return(state_up);
	}

	/**
	 * Put an object in the database
	 */
	public synchronized boolean put_object(String name,String obj) {
			String query;
			int idx;
			String object,field,value;

			Statement statement;
			ResultSet res;

			try {
				idx=name.indexOf('.');
				object=name.substring(0,idx);
				field=name.substring(idx+1);
				value=Escape.singlequote(obj);

				statement=connection.createStatement();
				res=statement.executeQuery("SELECT object FROM scanobjects where object='"+object+"' AND field='"+field+"'");
				if (res.next()) {
					// Execute update
					query="UPDATE scanobjects set value='"+value+"' where object='"+object+"' AND field='"+field+"'";
				} else {
					// Execute insert
					query="INSERT INTO scanobjects (object,field,value) VALUES('"+object+"','"+field+"','"+value+"'";
				}
				statement.executeUpdate(query);
				statement.close();
			} catch(SQLException e) {
				System.out.println("Error writing to database (putobject)");
				e.printStackTrace();
				state_up=false;
			}
		return(true);
	}

	/**
	 * Get an object from the database
	 */
	public synchronized String get_object(String name) {
			String query;
			int idx;
			String obj,field,value;
			String line="";

			Statement statement;
			ResultSet res;

			try {
				idx=name.indexOf('.');
				obj=name.substring(0,idx);
				field=name.substring(idx+1);

				statement=connection.createStatement();
				res=statement.executeQuery("SELECT object FROM scanobjects where object='"+obj+"' AND field='"+field+"'");
				if (res.next()) {
					line=res.getString(1);
				} else {
					line="";
				}
			} catch(SQLException e) {
				System.out.println("Error getting from database (getobject)");
				e.printStackTrace();
				state_up=false;
			}
			return(line);
	}

	/**
	 * Process the query, it returns a Vector which contains the rows
	 * which are Vector's again, which contain the columns which are Strings
	 *
	 * @see util.Vector
	 */
	public synchronized Vector query(String query) {
		int cols,rows=0;
		int c;
		boolean first=true;
		Vector d_rows=null,d_cols=null;

		long querytime=0,buildtime=0,tstart=0,tstop=0;

		Statement statement;
		ResultSet res;
		ResultSetMetaData meta;

		try {
			if (query.endsWith(";")) {
				query=query.substring(0,query.length()-1);
			}
			if (query.regionMatches(true,0,"SELECT",0,6) || query.regionMatches(true,0,"RETURN",0,6)) {
				if (debug) {
					tstart=System.currentTimeMillis();
				}
				statement=connection.createStatement();
				res=statement.executeQuery(query);
				if (debug) {
					tstop=System.currentTimeMillis();
					querytime=tstop-tstart;
					tstart=tstop;
				}
				meta=res.getMetaData();
				cols=meta.getColumnCount();
				rows=0;
				d_rows=new Vector();
				while (res.next()) {
					d_cols=new Vector();
					for (c=1;c<=cols;c++) {
						d_cols.addElement(res.getString(c));
					}
					rows++;
					d_rows.addElement(d_cols);
					if (first) first=false;
				}
			} else {
				if (debug) {
					tstart=System.currentTimeMillis();
				}
				statement=connection.createStatement();
				rows=statement.executeUpdate(query);
				if (debug) {
					tstop=System.currentTimeMillis();
					querytime=tstop-tstart;
					tstart=tstop;
				}
			}
		} catch(SQLException e) {
			state_up=false;
			e.printStackTrace();
		}
		if (debug) {
			tstop=System.currentTimeMillis();
			buildtime=tstop-tstart;
			tstart=tstop;
			System.out.println("JDBCAccess: Query '"+query+"'\n  Querytime "+querytime+" , Vector building "+buildtime+" (inclusive reading from jdbc) , Rows affected : "+rows+".");
		}
		if (first) return(null);
		return(d_rows);
	}

	/**
	 * Process the query, it returns a Vector which contains the rows
	 * which are Vector's again, which contain the columns which are Strings
	 *
	 * @see util.Vector
	 */
	public synchronized Vector queryflat(String query) {
		int cols,rows=0;
		int c;
		boolean first=true;
		Vector d_rows=null,d_cols=null;

		long querytime=0,buildtime=0,tstart=0,tstop=0;

		Statement statement;
		ResultSet res;
		ResultSetMetaData meta;

		try {
			if (query.endsWith(";")) {
				query=query.substring(0,query.length()-1);
			}
			if (query.regionMatches(true,0,"SELECT",0,6) || query.regionMatches(true,0,"RETURN",0,6)) {
				if (debug) {
					tstart=System.currentTimeMillis();
				}
				statement=connection.createStatement();
				res=statement.executeQuery(query);
				if (debug) {
					tstop=System.currentTimeMillis();
					querytime=tstop-tstart;
					tstart=tstop;
				}
				meta=res.getMetaData();
				cols=meta.getColumnCount();
				rows=0;
				d_rows=new Vector();
				while (res.next()) {
					for (c=1;c<=cols;c++) {
						d_rows.addElement(res.getString(c));
					}
					rows++;
					if (first) first=false;
				}
			} else {
				if (debug) {
					tstart=System.currentTimeMillis();
				}
				statement=connection.createStatement();
				rows=statement.executeUpdate(query);
				if (debug) {
					tstop=System.currentTimeMillis();
					querytime=tstop-tstart;
					tstart=tstop;
				}
			}
		} catch(SQLException e) {
			state_up=false;
			e.printStackTrace();
		}
		if (debug) {
			tstop=System.currentTimeMillis();
			buildtime=tstop-tstart;
			tstart=tstop;
			System.out.println("JDBCAccess: Query '"+query+"'\n  Querytime "+querytime+" , Vector building "+buildtime+" (inclusive reading from jdbc) Rows affected: "+rows+".");
		}
		if (first) return(null);
		return(d_rows);
	}

	/**
	 * Process the query, it returns a Vector which contains the rows
	 * which are Vector's again, which contain the columns which are Strings
	 *
	 * @see util.Vector
	 */
	public synchronized Vector query_nol(String query) {
		return(query(query));
	}

	public synchronized byte[] getbinary(String handle) {
		System.out.println("Get binary not supported on jdbc");
		return(null);
	}

	public synchronized String putbinary(byte data[]) {
		System.out.println("Put binary not supported on jdbc");
		return("");
	}
	public synchronized boolean delbinary(String handle) {
		System.out.println("Put binary not supported on jdbc");
		return(false);
	}

	/**
	 * Set the debug flag
	 */
	public boolean setdebug(boolean debug) {
		this.debug=debug;
		return(this.debug);
	}

	/**
	 * Get the current state of the debug flag
	 */
	public boolean getdebug() {
		return(debug);
	}

	/**
	 * Return the state of the connection
	 */
	public boolean state_up() {
		return(state_up);
	}
}

