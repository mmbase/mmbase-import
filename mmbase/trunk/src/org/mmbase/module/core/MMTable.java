/* -*- tab-width: 8; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.storage.*;

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
 * @version $Id: MMTable.java,v 1.11 2003-11-10 21:17:36 michiel Exp $
 */
public class MMTable {

    private static final Logger log = Logging.getLoggerInstance(MMTable.class);

    /**
     * The MMBase module that this table belongs to
     * @scope protected
     */
    public MMBase mmb;

    /**
     * The table name
     * @scope protected
     */
    public String tableName;

    /**
     * Empty constructor.
     */
    public MMTable() {
    }

    /**
     * Retrieve the full table name (including the clouds' base name)
     * @return a <code>String</code> containing the full table name
     */
    public String getFullTableName() {
        return mmb.baseName+"_"+tableName;
    }

    /**
     * Determine the number of objects in this table.
     * @return The number of entries in the table.
     */
    public int size() {
        StorageManagerFactory factory = mmb.getStorageManagerFactory();
        if (factory!=null) {
            try {
                return factory.getStorageManager().size((MMObjectBuilder)this);
            } catch (StorageException se) {
                log.error(se.getMessage());
                return -1;
            }
        } else {
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
                return -1;
            }
        }
    }

    /**
     * Check whether the table is accessible.
     * In general, this means the table does not exist. Please note that this routine may
     * also return false if the table is inaccessible due to insufficient rights.
     * @return <code>true</code> if the table is accessible, <code>false</code> otherwise.
     */
    public boolean created() {
        StorageManagerFactory factory = mmb.getStorageManagerFactory();
        if (factory != null) {
            try {
                return factory.getStorageManager().exists((MMObjectBuilder)this);
            } catch (StorageException se) {
                log.error(se.getMessage() + Logging.stackTrace(se));
                return false;
            }
        } else {
           return mmb.getDatabase().created(getFullTableName());
        }
    }

}
