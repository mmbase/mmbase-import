/* -*- tab-width: 8; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;

import org.mmbase.module.*;
import org.mmbase.module.database.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MMTable is the base abstraction of a cloud of objects stored in one database tabel,
 * essentially a cloud of objects of the same type.
 * It provides a starting point for MMObjectBuilder by defining a scope - the database table -
 * and basic functionality to create the table and query properties such as its size.
 * This class does not contain actual management of nodes (this is left to MMOBjectBuilder).
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadoc)
 * @version 31 januari 2001
 */
public class MMTable {

    private static Logger log = Logging.getLoggerInstance(MMTable.class.getName()); 

    /**
    * The MMBase module that this table belongs to
    */
	public MMBase mmb;
	
	/**
	* The table name
	*/
	public String tableName;

    /**
    * Empty constructor.
    */
	public MMTable() {
	}

    /**
    * Constructor.
    * Associates the table with a MMBase module.
    * @param m MMBase module to associate the table with
    */
	public MMTable(MMBase m) {
		mmb=m; 
	}

    /**
    * Determine the number of objects in this table.
    * @return The number of entries in the table.
    */
	public int size() {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
            String query = "SELECT count(*) FROM " + mmb.getBaseName() + "_" + tableName + ";";
			log.info(query);
			ResultSet rs=stmt.executeQuery(query);
			int i=-1;
			while(rs.next()) {
				i=rs.getInt(1);
			}	
			stmt.close();
			con.close();
			return i;
		} catch (Exception e) {
			return(-1);
		}
		// better: (but have to move getFullTableName() from MOBjectBuilder to MMTable first)
 	    // return database.size(getFullTableName());
	}


    /**
    * Check whether the table is accessible.
    * In general, this means the table does not exist. Please note that this routine may
    * also return false if the table is inaccessible due to insufficient rights.
    * @return <code>true</code> if the table is accessible, <code>false</code> otherwise.
    */
	public boolean created() {
		if (size()==-1) {
			log.error("TABLE "+tableName+" NOT FOUND");
			return(false);
		} else {
			log.error("TABLE "+tableName+" FOUND");
			return(true);
		}
		// better:
 	    // return database.created(getFullTableName());
	}

}
