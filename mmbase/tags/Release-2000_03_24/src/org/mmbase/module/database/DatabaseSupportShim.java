/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
/*
	$Id: DatabaseSupportShim.java,v 1.1 2000-02-25 14:06:37 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * Interface to support specific database things
 * for the JDBC module
 * @version $Id: DatabaseSupportShim.java,v 1.1 2000-02-25 14:06:37 wwwtech Exp $
 */
public class DatabaseSupportShim implements DatabaseSupport {

	public void init() {
	}

	public void initConnection(Connection con) {
	}
}
