/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.util.*;
import java.sql.*;

/**
 * JDBCInterface is _only_ the module JDBC interface who setup the connections
 * it has nothing tofo with the JDBC interface.
 */
public interface JDBCInterface {

	public String makeUrl();
	public String makeUrl(String dbm);
	public String makeUrl(String host,String dbm);
	public String makeUrl(String host,int port,String dbm);

	// JDBC Pools
	public MultiConnection getConnection(String url, String name, String password) throws SQLException;
	public MultiConnection getConnection(String url) throws SQLException;

	public Connection getDirectConnection(String url) throws SQLException;
	public Connection getDirectConnection(String url,String name,String password) throws SQLException;
	public String getUser();
	public String getPassword();
	public String getDatabaseName();
	public void checkTime();
}
