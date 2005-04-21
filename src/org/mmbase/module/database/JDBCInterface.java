/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
	public void checkTime();
}
