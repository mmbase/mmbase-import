/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;


import org.mmbase.storage.*;
import org.mmbase.util.logging.*;

/**
 * PostreSqlStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * the Postgresql database.
 * It overrides the methods for storing and retrieving huge texts and bytefields, and the methods for determining
 * database key (for the object 'number' field).
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: PostgreSqlStorage.java,v 1.5 2004-01-27 12:04:46 pierre Exp $
 */
public class PostgreSqlStorage extends OODatabaseStorage implements DatabaseStorage {
    private static Logger log = Logging.getLoggerInstance(PostgreSqlStorage.class);

    public PostgreSqlStorage() {
        super();
    }

    // javadoc inherited
    protected String getText(String tableName, String fieldName, int number) {
        throw new UnsupportedOperationException("getText"); // why?
    }



    // javadoc inherited
    protected void prepare() {
        createSequence();
    }

    /**
     * Instead of the numberTable in postgresql a 'sequence' is used, which is created by this function.
     */
    private boolean createSequence() {
        boolean result = created(getFullTableName("autoincrement"));
        if (!result) {
            DatabaseTransaction trans = null;
            try {
                trans = createDatabaseTransaction();
                trans.executeUpdate("CREATE SEQUENCE " + getFullTableName("autoincrement") + " INCREMENT 1 START 1");
                trans.commit();
                result = true;
            } catch (StorageException e) {
                if (trans != null) trans.rollback();
                log.error(e.getMessage());
            }
        }
        return result;
    }

    // javadoc inherited
    public synchronized int createKey(Transaction trans) throws StorageException {
        DatabaseTransaction dbtrans = (DatabaseTransaction)trans;
        String sqlselect = "SELECT NEXTVAL ('"+  getFullTableName("autoincrement") + "')";
        dbtrans.executeQuery(sqlselect);
        return dbtrans.getIntegerResult();
    }

}

