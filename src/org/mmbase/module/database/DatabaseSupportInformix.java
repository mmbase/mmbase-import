/* -*- tab-width?: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: DatabaseSupportInformix.java,v 1.4 2001-04-11 11:41:23 michiel Exp $

	$Log: not supported by cvs2svn $
	Revision 1.3  2000/03/30 13:11:45  wwwtech
	Rico: added license
	
	Revision 1.2  2000/03/29 10:45:02  wwwtech
	Rob: Licenses changed
	
	Revision 1.1  2000/02/25 14:06:36  wwwtech
	Rico: added database specific connection init support
	
*/
package org.mmbase.module.database;

import java.sql.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Interface to support specific database things
 * for the JDBC module
 * @version $Id: DatabaseSupportInformix.java,v 1.4 2001-04-11 11:41:23 michiel Exp $
 */
public class DatabaseSupportInformix implements DatabaseSupport {

    private static Logger log = Logging.getLoggerInstance(DatabaseSupportInformix.class.getName()); 

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
			log.error("failed to set lock mode "+e);
			log.error(Logging.stackTrace(e));
		}
	}
}
