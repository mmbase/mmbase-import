/* -*- tab-width: 8; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.sql.*;

import org.mmbase.storage.*;
import org.mmbase.util.functions.FunctionProvider;

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
 * @version $Id: MMTable.java,v 1.17 2005-01-25 12:45:19 pierre Exp $
 */
public class MMTable extends FunctionProvider {

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
     * Retrieve the table name (without the clouds' base name)
     * @return a <code>String</code> containing the table name
     * @since MMBase-1.7
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Retrieve the full table name (including the clouds' base name)
     * @return a <code>String</code> containing the full table name
     * @since MMBase-1.7
     */
    public String getFullTableName() {
        return mmb.baseName + "_" + tableName;
    }

    /**
     * Determine the number of objects in this table.
     * @return The number of entries in the table.
     */
    public int size() {
        try {
            return mmb.getStorageManager().size((MMObjectBuilder)this);
        } catch (StorageException se) {
            log.error(se.getMessage());
            return -1;
        }
    }

    /**
     * Check whether the table is accessible.
     * In general, this means the table does not exist. Please note that this routine may
     * also return false if the table is inaccessible due to insufficient rights.
     * @return <code>true</code> if the table is accessible, <code>false</code> otherwise.
     */
    public boolean created() {
        try {
            return mmb.getStorageManager().exists((MMObjectBuilder)this);
        } catch (StorageException se) {
            log.error(se.getMessage() + Logging.stackTrace(se));
            return false;
        }
    }

}
