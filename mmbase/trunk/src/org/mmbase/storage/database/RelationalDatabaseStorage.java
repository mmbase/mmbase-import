/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.support.MMJdbc2NodeInterface;

import org.mmbase.storage.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * RelationalDatabaseStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * Ansi SQL 92 relational databases.
 * This class extends AbstractDatabaseStorage to include methods for retrieving data
 * from a relational database systems.
 * It overrides the various update methods to allow for recursive updates on parent tables,
 * the methods for storing and retrieving huge texts and bytefields, and teh methods for determining
 * database key (for the object 'number' field).
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: RelationalDatabaseStorage.java,v 1.12 2004-01-27 12:04:46 pierre Exp $
 * @todo This function contains a lot of methods which do not seem
 *       specific for a 'relational' database. They should perhaps be moved
 *        to 'abstract' databasestorage
 */
public class RelationalDatabaseStorage extends SQL92DatabaseStorage implements DatabaseStorage, MMJdbc2NodeInterface {

    private static Logger log = Logging.getLoggerInstance(RelationalDatabaseStorage.class);

    /**
     * Constructs the Ansi SQL database layer support class
     */
    public RelationalDatabaseStorage() {
        super();
    }


    // javadoc inherited
    public boolean supportsExtendedTables() {
        return false;
    }

    /**
     * Prepares the database layer.
     * This code creates a 'numbertable' for MMBase to track number generation
     * by the {@link #createKey} method.
     * Override this method if you use a different way of generating keys,
     * or if you need to make other preparations for your database.
     */
    protected void prepare() {
        checkNumberTable();
    }


    /**
     * Get text from blob
     * @javadoc
     */
    public String getText(String tableName, String fieldName, int number) {
        log.debug("getText");
        String result = null;
        String sqlselect = selectSQL(tableName, fieldName, number);
        DatabaseTransaction trans = null;
        try {
            trans = createDatabaseTransaction();
            ResultSet rs = trans.executeQuery(sqlselect);
            if ((rs != null) && rs.next()) {
                result = getDBText(rs,1);
            }
            if (log.isDebugEnabled()) {
                log.debug("getText found " + result + " with '" + sqlselect + "'") ;
            }
            trans.commit();
        } catch (Exception e) {
            if (trans != null) trans.rollback();
        }
        return result;
    }


    // this looks a bit dirty.  perhaps the nicer version of
    // PostgreslStorage should be moved to AbstractDatabaseStorage,
    // and this one moved to MySqlStorage

    // javadoc inherited
    /*
    public byte[] getDBByte(ResultSet rs,int idx) {
        byte[] bytes=null;
        try {
            InputStream inp = rs.getBinaryStream(idx);
            int siz = inp.available(); // DIRTY
            DataInputStream input = new DataInputStream(inp);
            bytes = new byte[siz];
            if (log.isDebugEnabled()) {
                log.debug("got " + bytes.length + " bytes for field  " + idx);
            }
            input.readFully(bytes);
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("byte  exception "+e);
            log.error(Logging.stackTrace(e));
        }
        return bytes;
    }
    */


    /**
     * Checks if the numberTable exists.
     * If not this method will create one,
     * and inserts the DBKey retrieved by getCurrentKey
     */
    private void checkNumberTable() {
        if (log.isDebugEnabled()) log.trace("checks if table numberTable exists.");
        if(! created(getFullTableName("numberTable"))) {
            // Get the current object number
            int number = getCurrentKey();
            // integer should use getDataType?
            DatabaseTransaction trans=null;
            try {
                trans = createDatabaseTransaction();
                String sqlcreate=
                    createSQL(getFullTableName("numberTable"),
                        constructFieldDefinition("numberTable", "number", FieldDefs.TYPE_INTEGER, -1, KEY_PRIMARY));
                trans.executeUpdate(sqlcreate);
                String sqlinsert= insertSQL(getFullTableName("numberTable"),getNumberString(),""+number);
                trans.executeUpdate(sqlinsert);
                trans.commit();
            } catch (StorageException e) {
                log.error(e.toString());
                if (trans != null) trans.rollback();
                return;
            }
        }
    }

    /**
     * @javadoc
     */
    private synchronized int getCurrentKey() {
        int number = 0;
        DatabaseTransaction trans = null;
        try {
            trans = createDatabaseTransaction();
            String sqlselect = selectSQL(getFullTableName("object"), "max(" + getNumberString() + ")");
            trans.executeQuery(sqlselect);
            number=trans.getIntegerResult();
            trans.commit();
        } catch (StorageException e) {
            log.debug(e.toString()); // probably object table did not exist yet, which is all right
            if (trans!=null) trans.rollback();
        }
        return number;
    }

    // javadoc inherited from Storage
    public synchronized int createKey(Transaction trans) throws StorageException {
        DatabaseTransaction dbtrans = (DatabaseTransaction) trans;
        dbtrans.executeUpdate("UPDATE "+getFullTableName("numberTable")+" SET "+getNumberString()+" = "+getNumberString()+"+1;");
        String sqlselect=selectSQL(getFullTableName("numberTable"),getNumberString());
        dbtrans.executeQuery(sqlselect);
        return dbtrans.getIntegerResult();
    }

    /**
     * This method inserts a new object in a specified builder table.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @param builder the builder to store teh node in. This can be a parentbuilder of the node's actual builder
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    public int insertIntoTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {

        if (super.insertIntoTable(builder,node,trans)==-1) return -1;

        MMObjectBuilder parent = getParentBuilder(builder);

        // call the database to update the parent table
        if (parent != null) {
            insertIntoTable(parent, node, trans);
        } else if (!builder.getTableName().equals("object")) {
            // parent is object table
            insertObjectTable(node,trans);
        }
        return node.getNumber();
    }

    /**
     * This method inserts a new object in the object table.
     * Called by {@link #insert} to maintain consistency.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    protected int insertObjectTable(MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqlinsert=
            insertSQL(getFullTableName("object"),
                         getNumberString()+ "," + getOTypeString() + "," + getOwnerString(),
                         node.getNumber()+","+node.getIntValue("otype")+",'"+node.getStringValue("owner")+"'");
        if (!trans.executeUpdate(sqlinsert)) {
            return -1;
        } else {
            return node.getNumber();
        }
    }

    /**
     * Commit this node to the specified builder table.
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    public boolean commitToTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {

        if (!super.commitToTable(builder,node,trans)) return false;

        // determine if parent builders need be updated
        boolean changeParentFields = false;
        for (Iterator f=node.getChanged().iterator(); (!changeParentFields) && f.hasNext();) {
            String key=(String)f.next();
            changeParentFields=isParentField(builder,key);
        }
        if (changeParentFields) {
            // obtain the parent builder, if any
            MMObjectBuilder parent=getParentBuilder(builder);
            if (parent!=null) {
                return commitToTable(parent,node,trans);
            } else if (!builder.getTableName().equals("object")) {
                // parent is object table
                return commitObjectTable(node,trans);
            }
        }
        return true;
    }

    /**
     * Commit this node to the object table.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    protected boolean commitObjectTable(MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqlupdate=updateSQL(getFullTableName("object"),
                                   getOwnerString()+"='"+node.getStringValue("owner")+"'",
                                   node.getNumber());
        return trans.executeUpdate(sqlupdate);
    }

    /**
     * Remove a node from the specified builder table.
     * @param builder the builder to remove the node from. This can be a parentbuilder of the node's actual builder
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    public boolean deleteFromTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        super.deleteFromTable(builder,node,trans);
        // obtain the parent builder, if any
        MMObjectBuilder parent=getParentBuilder(builder);
        // call the database to update the parent table
        if (parent!=null) {
            return deleteFromTable(parent,node,trans);
        } else if (!builder.getTableName().equals("object")) {
            // parent is object table
            return deleteObjectTable(node,trans);
        }
        return true;
    }

    /**
     * Remove a node from the object table.
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    protected boolean deleteObjectTable(MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqldelete=deleteSQL(getFullTableName("object"),node.getNumber());
        trans.executeUpdate(sqldelete);
        return true;
    }

}
