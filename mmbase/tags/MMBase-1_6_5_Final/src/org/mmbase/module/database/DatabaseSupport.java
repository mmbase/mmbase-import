/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: DatabaseSupport.java,v 1.3 2000-03-30 13:11:43 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.2  2000/03/29 10:45:02  wwwtech
	Rob: Licenses changed
	
	Revision 1.1  2000/02/25 14:06:36  wwwtech
	Rico: added database specific connection init support
	
*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * Interface to support specific database things
 * for the JDBC module
 * @version $Id: DatabaseSupport.java,v 1.3 2000-03-30 13:11:43 wwwtech Exp $
 */
public interface DatabaseSupport {

	public void init();
	public void initConnection(Connection con);
}
