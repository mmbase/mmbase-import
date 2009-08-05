/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database.experimental;

//import java.io.*;
import java.sql.*;
//import java.util.*;

//import javax.sql.DataSource;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.*;

//import org.mmbase.storage.util.*;
//import org.mmbase.util.Casting;
import org.mmbase.storage.implementation.database.*;
import org.mmbase.storage.implementation.database.experimental.datahandler.*;
import org.mmbase.storage.implementation.database.experimental.metahandler.*;

import org.mmbase.util.logging.*;
/**
 * A storage manager factory for database storages.
 * This factory sets up a datasource for connecting to the database.
 * If you specify the datasource URI in the 'dataource' property in mmbaseroot.xml configuration file,
 * the factory attempts to obtain the datasource from the appplication server. If this fails, or no datasource URI is given,
 * it attempts to use the connectivity offered by the JDBC Module, which is then wrapped in a datasource.
 * Note that if you provide a datasource you should make the JDBC Module inactive to prevent the module from
 * interfering with the storage layer.
 * This database implementation is focussed on retrieving the database information with usage of views based upon JDBC
 *
 * @author Eduard Witteveen
 * @since MMBase-1.8
 * @version $Id: ViewDatabaseStorageManager.java,v 1.1 2005-02-16 13:01:46 eduard Exp $
 */
public class ViewDatabaseStorageManager extends DatabaseStorageManager implements org.mmbase.storage.StorageManager {
    /*
     *Below are the parts that were used from the DatabaseStorageManager, 
     *could be moved to a AbstractDatabaseStorageManager.
     *When used this way, both  ViewDatabaseStorageManager and DatabaseStorageManager
     *can extend this AbstractDatabaseStorageManager
     *  init() method
     */
    private static final Logger log = Logging.getLoggerInstance(ViewDatabaseStorageManager.class);
    protected DataHandler datahandler = null;
    protected MetaHandler metahandler = null;
    
    private static int instancecount = 0;
    public ViewDatabaseStorageManager() {
        instancecount++;
        if(instancecount == 1) {
            log.info("[[ == A Storage which tries to store it's data on a single place in the database, instead of storing duplicate information in the table == ]]");
            log.info("[[ ==    Also it tries to be very strict about the database rules, so that the data will stay consistent enoforced by database rules    == ]]");
        }
        else {
            log.info("[[ ==    This is instance number:" + instancecount + ", why dont we have 1 instance running all?");            
        }
    }
    
    // javadoc is inherited
    public double getVersion() {
        return 0.1;
    }

    // javadoc is inherited
    public void init(StorageManagerFactory factory) throws StorageException {
        super.init(factory);
        
        // load the handlers
        // TODO: name should be in the config, where now the create statements are mentioned
        datahandler = new DataHandler(this);
        metahandler = new MetaHandler(this);
    }

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean exists() throws StorageException {
        if(!metahandler.environmentExists()) {
            return false;
        }
        return metahandler.viewExists(getObjectBuilder());
    }
    
    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if the storage element exists, false if it doesn't
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean exists(MMObjectBuilder builder) throws StorageException {
        return metahandler.viewExists(builder);
    }

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    public void create() throws StorageException {
        if(!metahandler.environmentExists()) {
            metahandler.environmentCreate();
        }            
        if(!metahandler.viewExists(getObjectBuilder())) {
            metahandler.viewCreate(getObjectBuilder());   
        }        
    }

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage element for
     * @throws StorageException if an error occurred during the creation of the storage element
     */
    public void create(MMObjectBuilder builder) throws StorageException {
        metahandler.viewCreate(builder);
    }

    /**
     * Starts a transaction on this StorageManager instance.
     * All commands passed to the instance will be treated as being in this transaction.
     * If transactions are not supported by the storage, no actual storage transaction is started, but the code continues as if it has.
     * @throws StorageException if the transaction could not be created
     */
    public void beginTransaction() throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Closes any transaction that was started and commits all changes.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed), but the code continues as if it has.
     * @throws StorageException if a transaction is not currently active, or an error occurred while committing
     */
    public void commit() throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Cancels any transaction that was started and rollback changes if possible.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed),
     * but the code continues as if it has (through in that case it will return false).
     * @return <code>true</code> if changes were rolled back, <code>false</code> if the transaction was
     * canceled but the storage does not support rollback
     * @throws StorageException if a transaction is not currently active, or an error occurred during rollback
     */
    public boolean rollback() throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Gives an unique number for a new node, to be inserted in the storage.
     * This method should work with multiple mmbases
     * @return unique number
     */
    public int createKey() throws StorageException {
        return datahandler.getNextSequenceNumber();
    }

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param field the FieldDefs of the field to retrieve
     * @return the retrieved text
     * @throws StorageException if an error occurred while retrieving the text value
     */
    public String getStringValue(MMObjectNode node, FieldDefs field) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param field the FieldDefs of the field to retrieve
     * @return the retrieved byte array
     * @throws StorageException if an error occurred while retrieving the binary value
     */
    public byte[] getBinaryValue(MMObjectNode node, FieldDefs field) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * This method creates a new object in the storage, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    public int create(MMObjectNode node) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @throws StorageException if an error occurred during commit
     */
    public void change(MMObjectNode node) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Delete a node
     * @param node The node to delete
     * @return <code>true</code> if succesful
     * @throws StorageException if an error occurred during delete
     */
    public void delete(MMObjectNode node) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    public int getNodeType(int number) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     */
    public void change(MMObjectBuilder builder) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     */
    public void delete(MMObjectBuilder builder) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Return the number of objects of a builder in the storage
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has
     * @throws StorageException if the storage element for the builder does not exists
     */
    public int size(MMObjectBuilder builder) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Return the total number of objects in the storage
     * @return the number of objects
     * @throws StorageException if the basic storage elements do not exist
     */
    public int size() throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Creates a field and adds it to the storage of this builder.
     * @param field the FieldDefs of the field to add
     */
    public void create(FieldDefs field) throws StorageException{
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Changes a field to the storage of this builder.
     * @param field the FieldDefs of the field to change
     */
    public void change(FieldDefs field) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }

    /**
     * Deletes a field from the storage of this builder.
     * @param field the FieldDefs of the field to delete
     */
    public void delete(FieldDefs field) throws StorageException {
        throw new UnsupportedOperationException("TODO: implement!");
    }
    // methods that are used by the handlers,..
    public Connection getConnection() throws SQLException {
        return getConnection(true);
    }
    public Connection getConnection(boolean autocommit) throws SQLException {
        Connection con = dataSource.getConnection();
        con.setAutoCommit(autocommit);
        return con;
    }
    // next methods return the correct name for the sequence (the metahandler has possibility to interact)
    public String getSequenceName() {
       return metahandler.getSequenceName(factory.getMMBase().baseName);
    }
    // next methods return the correct name for the view (the metahandler has possibility to interact)
    public String getMMObjectBuilderViewName(MMObjectBuilder builder) {
        return metahandler.getViewName((String)factory.getStorageIdentifier(builder));
    }
    // next methods return the correct name for the table (the metahandler has possibility to interact)
    public String getMMObjectBuilderTableName(MMObjectBuilder builder) {
        return metahandler.getTableName(((String)factory.getStorageIdentifier(builder)));
    }
    public String getFieldName(FieldDefs field) {
        String fieldname = (String)factory.getStorageIdentifier(field);
        return fieldname;
    }            
    // there is also such a method in database storage manager, only that one checks also on 
    // strange things like disk storage, etc,...
    // or even global variables which change the result of this method,..
    public boolean isInheritedField(FieldDefs field) {
        MMObjectBuilder inheritedBuilder = field.getParent().getParentBuilder();
        if(inheritedBuilder == null) {
            // no parent, thus cannot inherit anything
            return false;
        }
        return (inheritedBuilder.getField(field.getDBName()) != null);
    }
    public boolean executeCommand(String sql) {
        Connection con = null;
        Statement stmt = null;
        try {
            log.info("EXECUTING:\n" + sql);
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("ERROR, could execute sql:\n"+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            } try {
                if(stmt!=null) stmt.close();
                con.close();
            }
            catch(Exception other) {}
            return false;
        }
        return true;
    }
    public MMObjectBuilder getObjectBuilder() {
        return factory.getMMBase().getRootBuilder();
    }
    public FieldDefs getNumberField() {
        return factory.getMMBase().getRootBuilder().getField("number");    
    }
}
