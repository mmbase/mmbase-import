/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
/*
	$Id: DatabaseSupportInformix.java,v 1.1 2000-02-25 14:06:36 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * Interface to support specific database things
 * for the JDBC module
 * @version $Id: DatabaseSupportInformix.java,v 1.1 2000-02-25 14:06:36 wwwtech Exp $
 */
public class DatabaseSupportInformix implements DatabaseSupport {

	public void init() {
	}

	public void initConnection(Connection con) {
		setLockMode(con,30);
	}

	public void setLockMode(Connection con,int sec) {
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
			System.out.println("DatabaseSupportInformix : failed to set lock mode "+e);
			e.printStackTrace();
		}
	}
}
