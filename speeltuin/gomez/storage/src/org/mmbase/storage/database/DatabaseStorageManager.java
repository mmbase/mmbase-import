/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.sql.DataSource;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.*;
import org.mmbase.storage.util.Scheme;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManager.java,v 1.6 2003-07-30 10:19:40 pierre Exp $
 */
public abstract class DatabaseStorageManager implements StorageManager {

    // logger
    private static Logger log = Logging.getLoggerInstance(DatabaseStorageManager.class);

    /**
     * The factory that created this manager
     */
    protected StorageManagerFactory factory;

    /**
     * The datasource through which to access the database. 
     */
    protected DataSource dataSource;

    /**
     * The currently active Connection.
     * This member is set by {!link #getActiveConnection()} and unset by {@link releaseActiveConnection()}
     */
    protected Connection activeConnection;
    
    /**
     * <code>true</code> if a transaction has been started.
     * This member is for state maitenance and may be true even if the storage does not support transactions
     */
    protected boolean inTransaction = false;

    /**
     * The transaction issolation level to use when starting a transaction.
     * This value is retrieved from the factory's 'database.transactionIsolationLevel' attribute, which is commonly set
     * to the highest (most secure) transaction isolation level available.
     */
    protected int transactionIsolation = Connection.TRANSACTION_NONE;
    
    /**
     * Pool of changed nodes in a transaction
     */
    protected Map changes;
    
    /**
     * Constructor
     */
    public DatabaseStorageManager() {
    }
    
    public double getVersion() {
        return 1.0;
    }

    /**
     * Initializes the manager.
     * Called by a StorageManagerFactory when instantiating the manager with the getStorageManager() method.
     * @param factory the StorageManagerFactory instance that created this storage manager.
     * @throws StorageConfigurationException if the initialization failed
     */
    public void init(StorageManagerFactory factory) throws StorageException {
        this.factory = factory;
        dataSource = (DataSource)factory.getAttribute("database.dataSource");
        if (factory.supportsTransactions()) {
            transactionIsolation = ((Integer)factory.getAttribute("database.transactionIsolationLevel")).intValue();
        }
    }

    /**
     * Obtains an active connection, opening a new one if needed. 
     * This method sets and then returns the {@link #activeConnection} member.
     * If an active connection was allready open, and the manager is in a database transaction, that connection is returned instead.
     * Otherwise, the connection is closed before a new one is opened.
     * @throws SQLException if opening the connection failed
     */
    protected Connection getActiveConnection() throws SQLException {
        if (activeConnection != null) {
            if (factory.supportsTransactions() && inTransaction) {
                return activeConnection;
            } else {
                releaseActiveConnection();
            }
        }
        activeConnection = dataSource.getConnection();
        return activeConnection;
    }

    /**
     * Safely closes the active connection.
     * If a transaction has been started, the connection is not closed.
     */    
    protected void releaseActiveConnection() {
        if (!(inTransaction && factory.supportsTransactions()) && activeConnection != null) {
            try {
                activeConnection.close();
            } catch (SQLException se) {
                // if something went wrong, log, but do not throw exceptions
                log.error("Failure when closing connection: "+se.getMessage());
            }
            activeConnection = null;
        }
    }
    
    /**
     * Starts a transaction on this StorageManager instance.
     * All commands passed to the instance will be treated as being in this transaction.
     * If transactions are not supported by the database, no actual database transaction created, but the code continues as if it has.
     * @throws StorageException if a transaction is currently active, or a database error occurred
     */
    public void beginTransaction() throws StorageException {
        if (inTransaction) {
            throw new StorageException("Cannot start Transaction when one is already active.");
        } else if (factory.supportsTransactions()) {
            try {
                getActiveConnection();
                activeConnection.setTransactionIsolation(transactionIsolation);
                activeConnection.setAutoCommit(false);
            } catch (SQLException se) {
                releaseActiveConnection();
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
        changes = new HashMap();
        inTransaction = true;
    }

    /**
     * Closes any transaction that was started and commits all changes.
     * If transactions are not supported by the database, nothing really happens (as changes are allready committed), but the code continues as if it has.
     * @throws StorageException if a transaction is not currently active, or a database error occurred
     */
    public void commit() throws StorageException {
        if (!inTransaction) {
            throw new StorageException("No transaction started.");
        } else {
            inTransaction = false;
            if (factory.supportsTransactions()) { 
                try {
                    activeConnection.commit();
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                    factory.getChangeManager().commit(changes);
                }
            }
        }
    }

    /**
     * Cancels any transaction that was started and rollback changes.
     * If transactions are not supported by the database, nothing really happens (as changes are allready committed), 
     * but the code continues as if it has (through in that case it will return false).
     * @return <code>true</code> if changes were rolled back, <code>false</code> if the transaction was 
     * canceled but rollback could not take place.
     * @throws StorageException if a transaction is not currently active, or a database error occurred
     */
    public boolean rollback() throws StorageException {
        if (!inTransaction) {
            throw new StorageException("No transaction started.");
        } else {
            inTransaction = false;
            if (factory.supportsTransactions()) {
                try {
                    activeConnection.rollback();
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                    changes.clear();
                }
            }
            return factory.supportsTransactions();
        }
    }

    /**
     * Commits the change to a node.
     * If the manager is in a transaction (and supports it), the change is stored in a 
     * {@link Changes} object (to be committed after the transaction ends). 
     * Otherwise it directly commits and broadcasts the changes
     * @param node the node to register
     * @param change the type of change: "n": new, "c": commit, "d": delete, "r" : relation changed
     */
    protected void commitChange(MMObjectNode node, String change) {
        if (inTransaction && factory.supportsTransactions()) {
            changes.put(node, change);
        } else {
            factory.getChangeManager().commit(node, change);
        }
    }

    /**
     * Gives an unique number for a new node, to be inserted in the storage.
     * This method should work with multiple mmbases
     * @return unique number
     */
    abstract public int createKey();

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved text
     */
    abstract public String getText(MMObjectNode node,String fieldname);

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved byte array
     */
    abstract public byte[] getBytes(MMObjectNode node,String fieldname);

    /**
     * Defines how binary (blob) data files must look like.
     * @param node the node the binary data belongs to
     * @param fieldName the name of the binary field
     * @return The File where to store or read the binary data
     */
    protected File getBinaryFile(MMObjectNode node, String fieldName) {
        File dir = new File((String)factory.getAttribute("database.binaryFilePath"), node.getBuilder().getTableName());
        dir.mkdirs();
        return new File(dir, "" + node.getNumber() + "." + fieldName);
    }

    /**
     * Store a binary (blob) data file
     * @todo how to do this in a transaction???
     * @param node the node the binary data belongs to
     * @param field the binary field
     */
    protected void storeBinaryAsFile(MMObjectNode node, FieldDefs field) throws StorageException {
        try {
            String fieldName= field.getDBName();
            File binaryFile = getBinaryFile(node, fieldName);
            byte[] value = node.getByteValue(fieldName);
            DataOutputStream byteStream = new DataOutputStream(new FileOutputStream(binaryFile));
            byteStream.write(value);
            byteStream.flush();
            byteStream.close();
        } catch(IOException ie) {
            throw new StorageException(ie);
        }
    }

    /**
     * Read a binary (blob) data file
     * @todo how to do this in a transaction???
     * @param node the node the binary data belongs to
     * @param field the binary field
     * @return the byte array containing the binary data
     */
    protected byte[] readBinaryFromFile(MMObjectNode node, FieldDefs field) throws StorageException {
        try {
            String fieldName= field.getDBName();
            File binaryFile = getBinaryFile(node, fieldName);
            int fileSize = (int) binaryFile.length();
            byte[] buffer = new byte[fileSize];
            if (fileSize > 0) {
                FileInputStream byteStream = new FileInputStream(binaryFile);
                int len = byteStream.read(buffer, 0, fileSize);
                byteStream.close();
            }
            return buffer;
        } catch(IOException ie) {
            throw new StorageException(ie);
        }
    }

    /**
     * This method inserts a new object, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    abstract public int insert(MMObjectNode node) throws StorageException;

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @return <code>true</code> of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    public void commit(MMObjectNode node) throws StorageException {
        // get node builder
        MMObjectBuilder builder = node.getBuilder();
        //  precommit call, needed to convert or add things before a save
        builder.preCommit(node);

        // Create a String that represents the DB fields to be used in the insert.
        StringBuffer setFields=null;
        // obtain the node's changed fields
        List fieldNames = node.getChanged();
        List fields = new ArrayList();
        for (Iterator f= fieldNames.iterator(); f.hasNext();) {
            String key = (String) f.next();
            // changing number is not allowed
            if(key.equals("number") || key.equals("otype")) {
                throw new StorageException("trying to change the '"+key+"' field");
            }
            FieldDefs field = builder.getField(key);
            // use field.inStorage()
            if ((field != null) &&
                ((field.getDBState() == FieldDefs.DBSTATE_PERSISTENT) ||
                 (field.getDBState() == FieldDefs.DBSTATE_SYSTEM))) {
                // skip bytevalues that are written to file
                if (factory.hasOption("database.storeBinaryAsFile") && (field.getDBType() == FieldDefs.TYPE_BYTE)) {
                    storeBinaryAsFile(node,field);
                } else {
                    // handle this field - store it in fields
                    fields.add(field);
                    // store the fieldname and the value parameter
                    String fieldName = (String)factory.getStorageIdentifier(field);
                    if (setFields == null) {
                        setFields = new StringBuffer(fieldName + "=?");
                    } else {
                        setFields.append(',').append(fieldName).append("=?");
                    }
                }
            }
        }
        if (fields.size() > 0) {
            Scheme scheme = factory.getScheme(DatabaseSchemes.UPDATE_NODE_SCHEME, DatabaseSchemes.UPDATE_NODE_SCHEME_DFP);
            try {
                String query = scheme.format(new Object[] { builder, setFields.toString(), builder.getField("number"), node });
                getActiveConnection();
                PreparedStatement ps = activeConnection.prepareStatement(query);
                for (int fieldNumber = 1; fieldNumber < fields.size(); fieldNumber++) {
                    FieldDefs field = (FieldDefs) fields.get(fieldNumber);
                    storeValue(ps, fieldNumber, node, field);
                }
                ps.executeUpdate();
            } catch (SQLException se) {
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
        commitChange(node,"c");
    }

    /**
     * Store the value of a field in a prepared statement
     * @param stmt the prepared statement
     * @param i the index of the field in the prepared statement
     * @param node the node from which to retrieve the value
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the fieldtype is invalid, or data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void storeValue(PreparedStatement stmt, int i, MMObjectNode node, FieldDefs field) throws StorageException, SQLException {
        String fieldName= field.getDBName();
        switch (field.getDBType()) {
            // Store numeric values
            case FieldDefs.TYPE_INTEGER:
            case FieldDefs.TYPE_FLOAT:
            case FieldDefs.TYPE_DOUBLE:
            case FieldDefs.TYPE_LONG:
                storeNumericValue(stmt,i,node.getValue(fieldName),field);
                break;
            // Store nodes
            case FieldDefs.TYPE_NODE:
                storeNodeValue(stmt,i,node.getNodeValue(fieldName),field);
                break;
            // Store strings
            case FieldDefs.TYPE_STRING:;
            case FieldDefs.TYPE_XML:
                storeStringValue(stmt, i, node.getStringValue(fieldName), field);
                break;
            // Store binary data
            case FieldDefs.TYPE_BYTE:
                storeByteValue(stmt, i, node.getByteValue(fieldName), field);
                break;
            // unknown field type - error
            default:
                throw new StorageException("unknown fieldtype");
        }
    }

    /**
     * Store a numeric value of a field in a prepared statement
     * The method uses the Casting class to convert to the appropriate value.
     * Null values are stored as NULL if possible, otherwise they are stored as -1.
     * Override this method if you want to override this behavior.
     * @param stmt the prepared statement
     * @param i the index of the field in the prepared statement
     * @param value the numeric value to store. This may be a String, MMObjectNode, Numeric, or other value - the
     *        method will convert it to the appropriate value.
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void storeNumericValue(PreparedStatement stmt, int i, Object value, FieldDefs field) throws StorageException, SQLException {
        switch (field.getDBType()) {
            // Store integers
            case FieldDefs.TYPE_INTEGER:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    stmt.setNull(i, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(i, Casting.toInt(value));
                }
                break;
            // Store floats
            case FieldDefs.TYPE_FLOAT:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    stmt.setNull(i, java.sql.Types.FLOAT);
                } else {
                    stmt.setFloat(i, Casting.toInt(value));
                }
                break;
            // Store doubles
            case FieldDefs.TYPE_DOUBLE:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    stmt.setNull(i, java.sql.Types.DOUBLE);
                } else {
                    stmt.setDouble(i, Casting.toInt(value));
                }
                break;
            // Store longs
            case FieldDefs.TYPE_LONG:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    stmt.setNull(i, java.sql.Types.BIGINT);
                } else {
                    stmt.setLong(i, Casting.toInt(value));
                }
                break;
        }
    }

    /**
     * Store a node value of a field in a prepared statement
     * Nodes are stored in the database as numeric values.
     * Since a node value can be a (referential) key (depending on implementation),
     * Null values should be stored as NULL, not -1. If a field cannot be null when a
     * value is not given, an exception is thrown.
     * Override this method if you want to override this behavior.
     * @param stmt the prepared statement
     * @param i the index of the field in the prepared statement
     * @param value the node value to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void storeNodeValue(PreparedStatement stmt, int i, MMObjectNode value, FieldDefs field) throws StorageException, SQLException {
        if (value == MMObjectNode.VALUE_NULL) {
            if (field.getDBNotNull()) {
                throw new StorageException("Field with name "+field.getDBName()+" can not be NULL.");
            } else {
                stmt.setNull(i, java.sql.Types.INTEGER);
            }
        } else {
            // retrieve node as a numeric value                    
            stmt.setInt(i, value.getIntValue(field.getDBName()));
        }
    }

    /**
     * Store binary data of a field in a prepared statement
     * This basic implementation uses a binary stream to set the data.
     * Null values are stored as NULL if possible, otherwise they are stored as an empty byte-array.
     * Override this method if you use another way to store binaries (i.e. Blobs).
     * @param stmt the prepared statement
     * @param i the index of the field in the prepared statement
     * @param value the data (byte array) to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void storeByteValue(PreparedStatement stmt, int i, byte[] value, FieldDefs field) throws StorageException, SQLException {
        if (value == null) {
            if (field.getDBNotNull()) {
                value = new byte[]{};
            } else {
                stmt.setNull(i, java.sql.Types.LONGVARBINARY);
                return;
            }
        }
        // how do we do this with blobs?
        try {
            InputStream stream = new ByteArrayInputStream(value);
            stmt.setBinaryStream(i, stream, value.length);
            stream.close();
        } catch (IOException ie) {
            throw new StorageException(ie);
        }
    }

    /**
     * Store the text value of a field in a prepared statement
     * This basic implementation uses {@link PreparedStatement.setString()} to set the data.
     * Null values are stored as NULL if possible, otherwise they are stored as an empty string.
     * Override this method if you use another way to store large texts (i.e. Clobs).
     * @param stmt the prepared statement
     * @param i the index of the field in the prepared statement
     * @param value the text to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void storeStringValue(PreparedStatement stmt, int i, String value, FieldDefs field) throws StorageException, SQLException {
        if (value == null) {
            if (field.getDBNotNull()) {
                value = "";
            } else {
                stmt.setNull(i, java.sql.Types.VARCHAR);
                return;
            }
        }
        stmt.setString(i, value);
    }

    /**
     * Delete a node
     * @param node The node to delete
     * @return <code>true</code> if succesful
     * @throws StorageException if an error occurred during delete
     */
    abstract public boolean delete(MMObjectNode node) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException {
        Scheme scheme = factory.getScheme(DatabaseSchemes.SELECT_NODE_SCHEME, DatabaseSchemes.SELECT_NODE_SCHEME_DFP);
        try {
            getActiveConnection();
            String query = scheme.format(new Object[] { builder, builder.getField("number"), new Integer(number)});
            Statement s = activeConnection.createStatement();
            return createNodeFromQuery(s.executeQuery(query), builder);
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }
    
    /**
     * Attempts to return a single Node from the resultset of a query.
     * You can use this method to iterate through a query, creating multiple nodes, provided the resultset still contains
     * members (that is, <code>result.isAfterLast</code> returns <code>false</code>)
     * @param res the resultset
     * @param builder the builder to use for creating the node
     * @return the node
     * @throws StorageException if the resultset is exhausted or a database error occurred
     */
    protected MMObjectNode createNodeFromQuery(ResultSet result, MMObjectBuilder builder) throws StorageException {
        try {
            if ((result != null) && result.next()) {
                // create a new node
                MMObjectNode node = builder.getNewNode("system");
                // iterate through all a builder's fields, and retrieve the value for that field
                // Note that if we would do it the other way around (iterate through the recordset's fields)
                // we might get inconsistencies if we 'remap' fieldnames that need not be mapped.
                for (Iterator i = builder.getFields(FieldDefs.ORDER_CREATE).iterator(); i.hasNext(); ) {
                    FieldDefs fd = (FieldDefs)i.next();
                    if (fd.getDBState() == FieldDefs.DBSTATE_PERSISTENT || fd.getDBState() == FieldDefs.DBSTATE_SYSTEM) {
                        String id = (String) factory.getStorageIdentifier(fd);
                        node.setValue(fd.getDBName(), createFieldValueFromQuery(fd.getDBType(), result, id));
                    }
                }
                // clear the changed signal on the node
                node.clearChanged();
                return node;
            } else {
                throw new StorageException("Node not found");
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        }
    }

    /**
     * Attempts to return a single field value from the resultset of a query.
     * @param fieldType the expected MMBase fieldType for this field
     * @param res the resultset
     * @param i the index of the field in the resultset
     * @return the value
     * @throws StorageException if the value cannot be retrieved from the resultset
     */
    protected Object createFieldValueFromQuery(int fieldType, ResultSet result, String name) throws StorageException {
        try {
            switch (fieldType) {
                // string-type fields
                // should test for MMBase encoding
                case FieldDefs.TYPE_XML:
                case FieldDefs.TYPE_STRING: {
                    return result.getString(name);
                }
                // binary fields: mark as $shorted, retrieve later
                case FieldDefs.TYPE_BYTE: {
                    return "$SHORTED";
                }
                default : {
                    return result.getObject(name);
                }
            }
        } catch(SQLException se) {
            throw new StorageException(se);
        }
    }

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    public int getNodeType(int number) throws StorageException {
        Scheme scheme = factory.getScheme(DatabaseSchemes.SELECT_NODE_TYPE_SCHEME, DatabaseSchemes.SELECT_NODE_TYPE_SCHEME_DFP);
        try {
            getActiveConnection();
            MMBase mmbase = factory.getMMBase();
            String query = scheme.format(new Object[] { mmbase, 
                                                        mmbase.getTypeDef().getField("number"), 
                                                        new Integer(number)});
            Statement s = activeConnection.createStatement();
            ResultSet result = s.executeQuery(query);
            if ((result != null) && result.next()) {
                return result.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }
    

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage for
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation fo the table
     */
    abstract public boolean create(MMObjectBuilder builder) throws StorageException;

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    abstract public boolean create() throws StorageException;

    /**
     * Queries the database metadata to test whether a given table exists.
     * @param tableName name of the table to look for
     * @throws StorageException when the metadata could not be retrieved
     * @return <code>true</code> if the table exists
     */
    protected boolean hasTable(String tableName) throws StorageException {
        boolean result = false;
        try {
            getActiveConnection();
            DatabaseMetaData metaData = activeConnection.getMetaData();
            ResultSet res = metaData.getTables(null, null, tableName, null);
            result = res.next();
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        } finally {
            releaseActiveConnection();
        }
        return result;
    }

    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if a storage element exists
     * @throws StorageException if an error occurred while querying the storage 
     */
    public boolean created(MMObjectBuilder builder) throws StorageException {
        return hasTable(builder.getFullTableName());
    }

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage 
     */
    abstract public boolean created() throws StorageException;

    /**
     * Return the number of objects of a builder in the storage
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has
     * @throws StorageException if the storage element for the builder does not exists
     */
    abstract public int size(MMObjectBuilder builder);

    /**
     * Return the total number of objects in the storage
     * @return the number of objects 
     * @throws StorageException if the basic storage elements do not exist
     */
    abstract public int size();

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     * @return <code>true</code> if succesful
     */
    abstract public boolean drop(MMObjectBuilder builder);

    /**
     * Adds a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to add
     * @return <code>true</code> if succesful
     */
    abstract public boolean addField(MMObjectBuilder builder,String fieldname);

    /**
     * Deletes a field from the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to delete
     * @return <code>true</code> if succesful
     */
    abstract public boolean removeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to change
     * @return <code>true</code> if succesful
     */
    abstract public boolean changeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     * @return <code>true</code> if succesful
     */
    abstract public boolean updateStorage(MMObjectBuilder builder);

}

