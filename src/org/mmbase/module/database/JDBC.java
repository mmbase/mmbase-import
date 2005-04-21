/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.database;

import java.util.*;
import java.sql.*;
import java.util.*;

import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.*;

/**
 * JDBC Module the module that provides you access to the loaded JDBC interfaces
 * we use this as the base to get multiplexes/pooled JDBC connects.
 *
 * @see org.mmbase.module.database.JDBCAccess
 * @see org.mmbase.module.servlets.JDBCServlet
 */
public class JDBC extends ProcessorModule implements JDBCInterface {
Class  classdriver;
Driver driver;
String JDBCdriver;
String JDBCurl;
String JDBChost;
int JDBCport;
int maxConnections;
int maxQuerys;
String JDBCdatabase;
MultiPoolHandler poolHandler;
JDBCProbe probe=null;
private String defaultname;
private String defaultpassword;

	public void onload() {
		getprops();
		getdriver();
		poolHandler=new MultiPoolHandler(maxConnections,maxQuerys);
		//System.out.println("JDBC -> Created pool "+poolHandler);
	}

	/*
	 * Initialize the properties and get the driver used
	 */
	public void init() {
		// old
		getprops();
		probe=new JDBCProbe(this);
	}

	/**
	 * Reload the properties and driver
	 */
	public void reload() {
		getprops();

		/* This doesn't work, have to figure out why
		try {
			DriverManager.deregisterDriver(driver);
		} catch (SQLException e) {
			System.out.println("JDBC Module: Can't deregister driver");
		}
		*/
		getdriver();
	}

	public void unload() {
	}
	public void shutdown() {
	}

	/**
	 * Get the driver as specified in our properties
	 */
	private void getdriver() {
		Driver d;

		driver=null;
		try {
			classdriver=Class.forName(JDBCdriver);
			//System.out.println("JDBC Module: Loaded load class : "+JDBCdriver);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Module: Can't load class : "+JDBCdriver);
		}
		/* Also get the instance to unload it later */
		for (Enumeration e=DriverManager.getDrivers();e.hasMoreElements();) {
			d=(Driver)e.nextElement();
			//System.out.println("Driver "+d);
			if (classdriver==d.getClass()) {
				driver=d;
				break;
			}
		}
		if (driver==null) {
			System.out.println("JDBC Module : Can't get driver from DriverManager");
		}
	}

	/**
	 * Get the properties
	 */
	private void getprops() {

		JDBCdriver=getInitParameter("driver");
		JDBCurl=getInitParameter("url");
		JDBChost=getInitParameter("host");
		defaultname=getInitParameter("user");
		defaultpassword=getInitParameter("password");
		if (defaultname==null) {
			defaultname="wwwtech";
		}
		if (defaultpassword==null) {
			defaultpassword="xxxxxx";
		}
		try {
			JDBCport=Integer.parseInt(getInitParameter("port"));
		} catch (NumberFormatException e) {
			JDBCport=0;
		}
		try {
			maxConnections=Integer.parseInt(getInitParameter("connections"));
		} catch (Exception e) {
			maxConnections=8;
		}
		try {
			maxQuerys=Integer.parseInt(getInitParameter("querys"));
		} catch (Exception e) {
			maxQuerys=500;
		}
		JDBCdatabase=getInitParameter("database");
	}

	/**
	 * Routine build the url to give to the DriverManager
	 * to open the connection. This way a servlet/module
	 * doesn't need to care about what database it talks to.
	 * @see java.sql.DriverManager.getConnection
	 */
	public String makeUrl() {
		return(makeUrl(JDBChost,JDBCport,JDBCdatabase));
	}

	/**
	 * Routine build the url to give to the DriverManager
	 * to open the connection. This way a servlet/module
	 * doesn't need to care about what database it talks to.
	 * @see java.sql.DriverManager.getConnection
	 */
	public String makeUrl(String dbm) {
		return(makeUrl(JDBChost,JDBCport,dbm));
	}

	/**
	 * Routine build the url to give to the DriverManager
	 * to open the connection. This way a servlet/module
	 * doesn't need to care about what database it talks to.
	 * @see java.sql.DriverManager.getConnection
	 */
	public String makeUrl(String host,String dbm) {
		return(makeUrl(host,JDBCport,dbm));
	}

	/**
	 * Routine build the url to give to the DriverManager
	 * to open the connection. This way a servlet/module
	 * doesn't need to care about what database it talks to.
	 * @see java.sql.DriverManager.getConnection
	 */
	public String makeUrl(String host,int port,String dbm) {
		String pre,post;
		int pos;
		String end;
		// $HOST $DBM $PORT

		pos=JDBCurl.indexOf("$DBM");
		pre=JDBCurl.substring(0,pos);
		post=JDBCurl.substring(pos+4);
		end=pre+dbm+post;
		pos=end.indexOf("$HOST");
		pre=end.substring(0,pos);
		post=end.substring(pos+5);
		end=pre+host+post;
		pos=end.indexOf("$PORT");
		if (pos!=-1) {
			pre=end.substring(0,pos);
			post=end.substring(pos+5);
			end=pre+port+post;
		}

		//System.out.println("JDBC URL=\""+end+"\"");
		return(end);
	}

	public MultiConnection getConnection(String url, String name, String password) throws SQLException {
		return(poolHandler.getConnection(url,name,password));
	}

	public MultiConnection getConnection(String url) throws SQLException {
		return(poolHandler.getConnection(url,defaultname,defaultpassword));
	}

	public Connection getDirectConnection(String url,String name,String password) throws SQLException {

		return(DriverManager.getConnection(url,name,password));
	}

	public Connection getDirectConnection(String url) throws SQLException {

		return(DriverManager.getConnection(url,defaultname,defaultpassword));
	}

	public synchronized void checkTime() {
		try {
			if (poolHandler!=null) poolHandler.checkTime();
		} catch(Exception e) {
			System.out.println("JDBC->checkTime Exception");
		}
	}

	/*
	 * User interface stuff
	 */

	public Vector getList(HttpServletRequest requestInfo,StringTagger tagger, String value) {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("POOLS")) return(listPools(tagger));
			if (cmd.equals("CONNECTIONS")) return(listConnections(tagger));
		}
		return(null);
	}

	public Vector listPools(StringTagger tagger) {
		Vector results=new Vector();
		int i;
		for (Enumeration e=poolHandler.keys();e.hasMoreElements();) {
			String name=(String)e.nextElement();
			MultiPool pool=poolHandler.get(name);
			i=name.indexOf(',');
			if (i!=-1) {
				results.addElement(name.substring(0,i));
			} else {
				results.addElement(name);
			}
			results.addElement(""+pool.getSize());
			results.addElement(""+pool.getTotalConnectionsCreated());
		}
		tagger.setValue("ITEMS","3");
		return(results);
	}


	public Vector listConnections(StringTagger tagger) {
		int i;
		Vector results=new Vector();
		for (Enumeration e=poolHandler.keys();e.hasMoreElements();) {
			String name=(String)e.nextElement();
			MultiPool pool=poolHandler.get(name);
			for (Enumeration f=pool.busyelements();f.hasMoreElements();) {
				MultiConnection realcon=(MultiConnection)f.nextElement();
				i=name.indexOf(',');
				if (i!=-1) {
					results.addElement(name.substring(name.lastIndexOf('/')+1,i));
				} else {
					results.addElement(name.substring(name.lastIndexOf('/')+1));
				}
				results.addElement(realcon.toString());
				results.addElement(""+realcon.getLastSQL());
				results.addElement(""+realcon.getUsage());
				//results.addElement(""+pool.getStatementsCreated(realcon));
			}
			for (Enumeration f=pool.elements();f.hasMoreElements();) {
				MultiConnection realcon=(MultiConnection)f.nextElement();
				i=name.indexOf(',');
				if (i!=-1) {
					results.addElement(name.substring(name.lastIndexOf('/')+1,i));
				} else {
					results.addElement(name.substring(name.lastIndexOf('/')+1));
				}
				results.addElement(realcon.toString());
				results.addElement("&nbsp;");
				results.addElement(""+realcon.getUsage());
				//results.addElement(""+pool.getStatementsCreated(realcon));
			}
		}
		tagger.setValue("ITEMS","4");
		return(results);
	}

}
