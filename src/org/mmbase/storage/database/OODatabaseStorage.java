/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

/**
 * OODatabaseStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * an objectoriented database.
 *
 * There is nothing specific for OO databases right now. Inheritance is handles in super-classes.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since MMBase-1.6
 * @version $Id: OODatabaseStorage.java,v 1.7 2004-01-27 12:04:46 pierre Exp $
 */
public abstract class OODatabaseStorage extends SQL92DatabaseStorage implements DatabaseStorage {

    // javadoc inherited
    public final boolean supportsExtendedTables() {
        return true;
    }

}

