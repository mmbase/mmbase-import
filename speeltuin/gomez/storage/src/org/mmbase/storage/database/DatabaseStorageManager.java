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
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.*;
import org.mmbase.storage.util.Scheme;
import org.mmbase.storage.util.TypeMapping;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A JDBC implementation of a storage manager.
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManager.java,v 1.12 2003-07-31 17:19:53 pierre Exp $
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
     * @param node the node to retrieve the text for
     * @param field the field to retrieve
     * @return the retrieved text
     */
    public String getStringValue(MMObjectNode node, FieldDefs field) throws StorageException {
        try {
            MMObjectBuilder builder = node.getBuilder();
            Scheme scheme = factory.getScheme(Schemes.GET_TEXT_DATA, Schemes.GET_TEXT_DATA_DEFAULT);
            String query = scheme.format(new Object[] { this, builder, field, builder.getField("number"), node });
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            ResultSet result = s.executeQuery(query);
            if ((result != null) && result.next()) {
                return getStringValue(result,1,field);
            } else {
                throw new StorageException("Node with number "+node.getNumber()+" not found.");
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    /**
     * Retrieve a text for a specified object field.
     * The default method uses {@link ResultSet.getString()} to obtain text.
     * Override this method if you want to optimize retrieving large texts,
     * i.e by using clobs or streams.
     * @param result the resultset to retrieve the text from
     * @param index the index of the text in the resultset
     * @param field the (MMBase) fieldtype
     * @return the retrieved text
     * @throws SQLException when a database error occurs
     * @throws StorageException when data is incompatible or the function is not supported
     */
    protected String getStringValue(ResultSet result, int index, FieldDefs field) throws StorageException, SQLException {
        return result.getString(index);
    }

    /**
     * Determine whether a filed (i.e. large text or a blob) should be shortened or not.
     * A 'shortened' field contains a placeholder text ('$SHORTED') to indicate that the field is expected to be of large size
     * and should be retrieved by an explicit call to {@link #getStringValue(MMObjectNode, FieldDefs) or.
     * {@link #getBinaryValue(MMObjectNode, FieldDefs).
     * The default implementation returns <code>true</code> for binaries, and <code>false</code> for other
     * types.
     * Override this method if you want to be able to change the placeholder strategy.
     * @param field the (MMBase) fieldtype
     * @return <code>true</code> if the field should be shortened
     * @throws SQLException when a database error occurs
     * @throws StorageException when data is incompatible or the function is not supported
     */
    protected boolean shorten(FieldDefs field) throws StorageException, SQLException {
        return field.getDBType() == FieldDefs.TYPE_BYTE;
    }

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * @param node the node to retrieve the byte array from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved byte array
     */
    public byte[] getBinaryValue(MMObjectNode node, FieldDefs field) throws StorageException {
        if (factory.hasOption(Attributes.STORE_BINARY_AS_FILE)) {
            return readBinaryFromFile(node, field);
        } else try {
            MMObjectBuilder builder = node.getBuilder();
            Scheme scheme = factory.getScheme(Schemes.GET_BINARY_DATA, Schemes.GET_BINARY_DATA_DEFAULT);
            String query = scheme.format(new Object[] { this, builder, field, builder.getField("number"), node });
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            ResultSet result = s.executeQuery(query);
            if ((result != null) && result.next()) {
                return getBinaryValue(result,1,field);
            } else {
                throw new StorageException("Node with number "+node.getNumber()+" not found.");
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * The default method uses {@link ResultSet.getBytes()} to obtain text.
     * Override this method if you want to optimize retrieving large objects,
     * i.e by using clobs or streams.
     * @param result the resultset to retrieve the text from
     * @param index the index of the text in the resultset
     * @param field the (MMBase) fieldtype
     * @return the retrieved text
     * @throws SQLException when a database error occurs
     * @throws StorageException when data is incompatible or the function is not supported
     */
    protected byte[] getBinaryValue(ResultSet result, int index, FieldDefs field) throws StorageException, SQLException {
        if (factory.hasOption(Attributes.SUPPORTS_BLOB)) {
            Blob blob = result.getBlob(index);
            return blob.getBytes(0, (int) blob.length());
        } else {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                InputStream input = result.getBinaryStream(index);
                int c = input.read();
                while (c != -1) {
                    bytes.write(c);
                    c = input.read();
                }
                input.close(); // this also closes the underlying stream
                return bytes.toByteArray();
            } catch (IOException ie) {
                throw new StorageException(ie);
            }
        }
    }

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
     * Override this method to call {@link insert(MMObjectNode, MMObjectBuilder)} for all involved builders if you use
     * a relational database.
     * @todo: move precomit() call to MMObjectBuilder
     * @param node The node to insert
     * @return The (new) number for this node
     * @throws StorageException if an error occurred during insert
     */
    public int insert(MMObjectNode node) throws StorageException {
        // assign a new number if the node has not yet been assigned one
        if (node.getNumber() == -1) {
            node.setValue("number", createKey());
        }
        MMObjectBuilder builder = node.getBuilder();
        // precommit call, needed to convert or add things before a save
        // Should be done in MMObjectBuilder
        builder.preCommit(node);
        return insert(node,builder);
    }

    /**
     * This method inserts a new object in a specific builder, and registers the change.
     * This method makes it easier to implement relational databases, where you may need to update the node
     * in more than one builder.
     * @param node The node to insert
     * @param builder the builder to store the node
     * @return The (new) number for this node
     * @throws StorageException if an error occurred during commit
     */
    public int insert(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // Create a String that represents the fields and values to be used in the insert.
        StringBuffer fieldNames = null;
        StringBuffer fieldValues = null;
        // get a builders fields
        List fields = builder.getFields();
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs) f.next();
            // use field.inStorage()
            if (((field.getDBState() == FieldDefs.DBSTATE_PERSISTENT) ||
                 (field.getDBState() == FieldDefs.DBSTATE_SYSTEM))) {
                // skip bytevalues that are written to file
                if (factory.hasOption(Attributes.STORE_BINARY_AS_FILE) && (field.getDBType() == FieldDefs.TYPE_BYTE)) {
                    storeBinaryAsFile(node,field);
                    // do not handle this field further
                    f.remove();
                } else {
                    fields.add(field);
                    // store the fieldname and the value parameter
                    String fieldName = (String)factory.getStorageIdentifier(field);
                    if (fieldNames == null) {
                        fieldNames = new StringBuffer(fieldName);
                        fieldValues = new StringBuffer("?");
                    } else {
                        fieldNames.append(',').append(fieldName);
                        fieldValues.append(",?");
                    }
                }
            } else {
                // do not handle this field further
                f.remove();
            }
        }
        if (fields.size() > 0) {
            Scheme scheme = factory.getScheme(Schemes.INSERT_NODE, Schemes.INSERT_NODE_DEFAULT);
            try {
                String query = scheme.format(new Object[] { this, builder, fieldNames.toString(), fieldValues.toString() });
                getActiveConnection();
                PreparedStatement ps = activeConnection.prepareStatement(query);
                for (int fieldNumber = 1; fieldNumber < fields.size(); fieldNumber++) {
                    FieldDefs field = (FieldDefs) fields.get(fieldNumber);
                    setValue(ps, fieldNumber, node, field);
                }
                ps.executeUpdate();
            } catch (SQLException se) {
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
        commitChange(node,"n");
        return node.getNumber();
    }

    /**
     * Commit this node
     * Override this method to call {@link insert(MMObjectNode, MMObjectBuilder)} for all involved builders if you use
     * a relational database.
     * @todo: move precomit() call to MMObjectBuilder
     * @param node The node to commit
     * @throws StorageException if an error occurred during commit
     */
    public void commit(MMObjectNode node) throws StorageException {
        MMObjectBuilder builder = node.getBuilder();
        // precommit call, needed to convert or add things before a save
        // Should be done in MMObjectBuilder
        builder.preCommit(node);
        commit(node,builder);
    }

    /**
     * Commit this node to the specified builder.
     * This method makes it easier to implement relational databses, where you may need to update the node
     * in more than one builder.
     * @param node The node to commit
     * @param builder the builder to store the node
     * @throws StorageException if an error occurred during commit
     */
    public void commit(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // Create a String that represents the fields to be used in the commit
        StringBuffer setFields = null;
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
                if (factory.hasOption(Attributes.STORE_BINARY_AS_FILE) && (field.getDBType() == FieldDefs.TYPE_BYTE)) {
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
            Scheme scheme = factory.getScheme(Schemes.UPDATE_NODE, Schemes.UPDATE_NODE_DEFAULT);
            try {
                String query = scheme.format(new Object[] { this, builder, setFields.toString(), builder.getField("number"), node });
                getActiveConnection();
                PreparedStatement ps = activeConnection.prepareStatement(query);
                for (int fieldNumber = 1; fieldNumber < fields.size(); fieldNumber++) {
                    FieldDefs field = (FieldDefs) fields.get(fieldNumber);
                    setValue(ps, fieldNumber, node, field);
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
     * @todo Note that this code contains some code that should really be implemented in FieldDefs.
     * In particular, casting should be done in FieldDefs. It is also useful if the FieldDefs object
     * would have setStorageType()/getStorageType() methods, so we can accurately determine the type to
     * enter, or whether Blobs/Clobs should be used, etc.
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param node the node from which to retrieve the value
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the fieldtype is invalid, or data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setValue(PreparedStatement statement, int index, MMObjectNode node, FieldDefs field) throws StorageException, SQLException {
        String fieldName= field.getDBName();
        switch (field.getDBType()) {
            // Store numeric values
            case FieldDefs.TYPE_INTEGER:
            case FieldDefs.TYPE_FLOAT:
            case FieldDefs.TYPE_DOUBLE:
            case FieldDefs.TYPE_LONG:
                setNumericValue(statement,index,node.getValue(fieldName),field);
                break;
            // Store nodes
            case FieldDefs.TYPE_NODE:
                setNodeValue(statement,index,node.getNodeValue(fieldName),field);
                break;
            // Store strings
            case FieldDefs.TYPE_STRING:;
            case FieldDefs.TYPE_XML:
                setStringValue(statement, index, node.getStringValue(fieldName), field);
                break;
            // Store binary data
            case FieldDefs.TYPE_BYTE:
                setBinaryValue(statement, index, node.getByteValue(fieldName), field);
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
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param value the numeric value to store. This may be a String, MMObjectNode, Numeric, or other value - the
     *        method will convert it to the appropriate value.
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setNumericValue(PreparedStatement statement, int index, Object value, FieldDefs field) throws StorageException, SQLException {
        switch (field.getDBType()) {
            // Store integers
            case FieldDefs.TYPE_INTEGER:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(index, Casting.toInt(value));
                }
                break;
            // Store floats
            case FieldDefs.TYPE_FLOAT:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.REAL);
                } else {
                    statement.setFloat(index, Casting.toInt(value));
                }
                break;
            // Store doubles
            case FieldDefs.TYPE_DOUBLE:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.DOUBLE);
                } else {
                    statement.setDouble(index, Casting.toInt(value));
                }
                break;
            // Store longs
            case FieldDefs.TYPE_LONG:
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.BIGINT);
                } else {
                    statement.setLong(index, Casting.toInt(value));
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
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param value the node value to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setNodeValue(PreparedStatement statement, int index, MMObjectNode value, FieldDefs field) throws StorageException, SQLException {
        if (value == MMObjectNode.VALUE_NULL) {
            if (field.getDBNotNull()) {
                throw new StorageException("Field with name "+field.getDBName()+" can not be NULL.");
            } else {
                statement.setNull(index, java.sql.Types.INTEGER);
            }
        } else {
            // retrieve node as a numeric value
            statement.setInt(index, value.getIntValue(field.getDBName()));
        }
    }

    /**
     * Store binary data of a field in a prepared statement
     * This basic implementation uses a binary stream to set the data.
     * Null values are stored as NULL if possible, otherwise they are stored as an empty byte-array.
     * Override this method if you use another way to store binaries (i.e. Blobs).
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param value the data (byte array) to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setBinaryValue(PreparedStatement statement, int index, byte[] value, FieldDefs field) throws StorageException, SQLException {
        if (value == null) {
            if (field.getDBNotNull()) {
                value = new byte[]{};
            } else {
                statement.setNull(index, java.sql.Types.VARBINARY);
                return;
            }
        }
        try {
            InputStream stream = new ByteArrayInputStream(value);
            statement.setBinaryStream(index, stream, value.length);
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
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param value the text to store
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setStringValue(PreparedStatement statement, int index, String value, FieldDefs field) throws StorageException, SQLException {
        if (value == null) {
            if (field.getDBNotNull()) {
                value = "";
            } else {
                statement.setNull(index, java.sql.Types.VARCHAR);
                return;
            }
        }
        statement.setString(index, value);
    }

    /**
     * Delete a node
     * @param node The node to delete
     * @throws StorageException if an error occurred during delete
     */
    public void delete(MMObjectNode node) throws StorageException {
        delete(node,node.getBuilder());
    }

    /**
     * Delete a node from a specific builder
     * This method makes it easier to implement relational databses, where you may need to remove the node
     * in more than one builder.
     * @param node The node to delete
     * @throws StorageException if an error occurred during delete
     */
    protected void delete(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // determine parent
        if (node.hasRelations()) {
            throw new StorageException("cannot delete node "+node.getNumber()+", it still has relations");
        }
        try {
            Scheme scheme = factory.getScheme(Schemes.DELETE_NODE, Schemes.DELETE_NODE_DEFAULT);
            String query = scheme.format(new Object[] { this, builder, builder.getField("number"), node });
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            s.executeUpdate(query);
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
        commitChange(node,"d");
    }

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException {
        Scheme scheme = factory.getScheme(Schemes.SELECT_NODE, Schemes.SELECT_NODE_DEFAULT);
        try {
            getActiveConnection();
            String query = scheme.format(new Object[] { this, builder, builder.getField("number"), new Integer(number)});
            Statement s = activeConnection.createStatement();
            return getNode(s.executeQuery(query), builder);
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
    protected MMObjectNode getNode(ResultSet result, MMObjectBuilder builder) throws StorageException {
        try {
            if ((result != null) && result.next()) {
                // create a new node
                MMObjectNode node = builder.getNewNode("system");
                // iterate through all a builder's fields, and retrieve the value for that field
                // Note that if we would do it the other way around (iterate through the recordset's fields)
                // we might get inconsistencies if we 'remap' fieldnames that need not be mapped.
                // this also guarantees the number field is set first, which we  may need when retrieving blobs
                // from disk
                for (Iterator i = builder.getFields(FieldDefs.ORDER_CREATE).iterator(); i.hasNext(); ) {
                    FieldDefs field = (FieldDefs)i.next();
                    if (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT || field.getDBState() == FieldDefs.DBSTATE_SYSTEM) {
                        if (shorten(field)) {
                            node.setValue(field.getDBName(), "$SHORTED");
                        } else if (field.getDBType() == FieldDefs.TYPE_BYTE && factory.hasOption(Attributes.STORE_BINARY_AS_FILE)) {
                            node.setValue(field.getDBName(), readBinaryFromFile(node, field));
                        } else {
                            String id = (String) factory.getStorageIdentifier(field);
                            node.setValue(field.getDBName(), getValue(result, result.findColumn(id), field));
                        }
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
     * @param result the resultset
     * @param index the index of the field in the resultset
     * @param field the expected MMBase field type
     * @return the value
     * @throws StorageException if the value cannot be retrieved from the resultset
     */
    protected Object getValue(ResultSet result, int index, FieldDefs field) throws StorageException {
        try {
            switch (field.getDBType()) {
                // string-type fields
                case FieldDefs.TYPE_XML:
                case FieldDefs.TYPE_STRING: {
                    return getStringValue(result, index, field);
                }
                case FieldDefs.TYPE_BYTE: {
                    return getBinaryValue(result, index, field);
                }
                default : {
                    return result.getObject(index);
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
        Scheme scheme = factory.getScheme(Schemes.SELECT_NODE_TYPE, Schemes.SELECT_NODE_TYPE_DEFAULT);
        try {
            getActiveConnection();
            MMBase mmbase = factory.getMMBase();
            String query = scheme.format(new Object[] { this,mmbase,
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
     * Returns the parent builder of the specifed builder.
     * If the value is null, the builder either has no parent, or its builder is the
     * "object" table, but there was no builder created for this low-level table.
     * @todo This code is needed for older systems that do not have an 'object' builder, or that use old builder
     *       configuration files. It should be moved to MMObjectBuilder
     * @param builder the builder to find the parent of
     * @return the parent builder or null if it cannot be determined
     */
    protected MMObjectBuilder getParentBuilder(MMObjectBuilder builder) {
        MMObjectBuilder parent = builder.getParentBuilder();
        if ((parent == null) && (builder instanceof InsRel) && !builder.getTableName().equals("insrel")) {
            parent = factory.getMMBase().getInsRel();
        }
        return parent;
    }

    /**
     * Tests whether the specified field is a member of the given builder
     * @todo This code is needed for older systems that do not have an 'object' builder, or that use old builder
     *       configuration files. It should be moved to MMObjectBuilder.
     * @param builder the builder to find the parent of
     * @param field the field to test
     * @return true if the field belongs to the parent table
     */
    protected boolean hasField(MMObjectBuilder builder, FieldDefs field) {
        String fieldName = field.getDBName();
        if (builder == null) {
            return fieldName.equals("number") || fieldName.equals("otype") || fieldName.equals("owner");
        } else {
            return builder.getField(fieldName) != null;
        }
    }

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage for
     * @throws StorageException if an error occurred during the creation of the table
     */
    public void create(MMObjectBuilder builder) throws StorageException {
        // object table should be build with create();
        if (builder.getTableName().equals("object")) {
            create();
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating a table for " + builder);
        }
        // use the builder to get the fields and create a
        // valid create SQL string
        // for backward compatibility, fields are to be created in the order defined
        // TODO: check whether otype is returned in the correct position!
        List fields = builder.getFields(FieldDefs.ORDER_CREATE);
        StringBuffer createFields = new StringBuffer();
        StringBuffer createIndices = new StringBuffer();
        StringBuffer createFieldsAndIndices = new StringBuffer();
        StringBuffer createCompositeIndices = new StringBuffer();
        List compositeIndices = new ArrayList();
        // obtain the parentBuilder
        // TODO: add fall-back code so parentBuilder is NOT null!
        // maybe do this in MMObjectBuilder???
        MMObjectBuilder parentBuilder = getParentBuilder(builder);
        String parentTableName;
        if (parentBuilder != null) {
            parentTableName = (String)factory.getStorageIdentifier(parentBuilder);
        } else {
            parentTableName = (String)factory.getStorageIdentifier();
        }

        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs) f.next();
            // persistent field? ( use inStorage() )
            boolean storefield = field.getDBState() == FieldDefs.DBSTATE_PERSISTENT || field.getDBState() == FieldDefs.DBSTATE_SYSTEM;
            // skip binary fields when values are written to file
            storefield = storefield && field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORE_BINARY_AS_FILE);
            // also, if the database is OO, skip fields that are in the parent builder
            storefield = storefield &&  !factory.hasOption(Attributes.SUPPORTS_INHERITANCE) || !hasField(parentBuilder, field);
            // convert a fielddef to a field SQL createdefinition
            if (storefield) {
                String fieldDef = getFieldDefinition(field);
                if (createFields.length() > 0) createFields.append(", ");
                createFields.append(fieldDef);
                // test on composite indices
                if (field.isKey() && factory.hasOption(Attributes.SUPPORTS_COMPOSITE_INDEX)) {
                    compositeIndices.add(field);
                } else {
                    // test on other indices
                    String indexDef = getIndexDefinition(field);
                    if (indexDef != null) {
                        if (createIndices.length() > 0) createIndices.append(", ");
                        createIndices.append(indexDef);
                        if (createFieldsAndIndices.length() > 0) createFieldsAndIndices.append(", ");
                        createFieldsAndIndices.append(fieldDef+" "+indexDef);
                    }
                }
            }
            if (compositeIndices.size()>0) {
                // test on other indices
                String indexDef = getCompositeIndexDefinition(compositeIndices);
                if (indexDef != null) {
                    if (createCompositeIndices.length() > 0) createCompositeIndices.append(", ");
                    createCompositeIndices.append(indexDef);
                }
            }
        }
        try {
            getActiveConnection();
            // create a rowtype, if a scheme has been given
            // Note that creating a rowtype is optional
            Scheme scheme = factory.getScheme(Schemes.CREATE_ROW_TYPE);
            if (scheme!=null) {
                String query = scheme.format(new Object[] { this, builder, createFields.toString(), parentTableName });
                Statement s = activeConnection.createStatement();
                s.executeUpdate(query);
            }
            // create the table
            scheme = factory.getScheme(Schemes.CREATE_TABLE);
            String query = scheme.format(new Object[] { this, builder,
                                                        createFields.toString(),
                                                        createIndices.toString(),
                                                        createFieldsAndIndices.toString(),
                                                        createCompositeIndices.toString(),
                                                        parentTableName });
            Statement s = activeConnection.createStatement();
            s.executeUpdate(query);
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    /**
     * Creates a fielddefinition, of the format '[fieldname] [fieldtype] NULL' or
     * '[fieldname] [fieldtype] NOT NULL' (depending on whether the field is nullable).
     * The fieldtype is taken from the type mapping in the factory.
     * @param field the field
     * @return the typedefiniton as a String
     * @throws StorageException if the field type cannot be mapped
     */
    protected String getFieldDefinition(FieldDefs field) throws StorageException {
        // create the type mapping to search for
        String typeName = field.getDBTypeDescription();
        int size = field.getDBSize();
        TypeMapping mapping = new TypeMapping();
        mapping.name = typeName;
        mapping.setFixedSize(size);
        // search type mapping
        List typeMappings = factory.getTypeMappings();
        int found = typeMappings.indexOf(mapping);
        if (found > -1) {
            String fieldDef = factory.getStorageIdentifier(field)+" "+((TypeMapping)typeMappings.get(found)).getType(size);
            if (field.getDBNotNull()) {
                fieldDef += " NOT NULL";
            } else {
                fieldDef += " NULL";
            }
            return fieldDef;
        } else {
            throw new StorageException("Type for field "+field.getDBName()+": "+typeName+" ("+size+") undefined.");
        }
    }

    /**
     * Creates an index definition string to be passed when creating a table.
     * @param field the field for which to make the index definition
     * @return the index definition as a String, or <code>null</code> if no definition is available
     */
    protected String getIndexDefinition(FieldDefs field) throws StorageException {
        Scheme scheme = null;
        if (field.getDBName().equals("number")) {
            scheme = factory.getScheme(Schemes.CREATE_PRIMARY_KEY, Schemes.CREATE_PRIMARY_KEY_DEFAULT);
        } else if (field.isKey()) {
            scheme = factory.getScheme(Schemes.CREATE_SECONDARY_KEY);
        } else if (field.getDBType() == FieldDefs.TYPE_NODE) {
            scheme = factory.getScheme(Schemes.CREATE_FOREIGN_KEY);
        }
        if (scheme != null) {
            // fallback for cases where builder is null
            // this may happen if this code is called from create()
            // when there is no 'object' builder.
            if (field.getParent() == null) {
                return scheme.format(new Object[]{ this, factory.getMMBase(), field, field, factory.getMMBase()} );
            } else {
                return scheme.format(new Object[]{ this, field.getParent(), field, field, factory.getMMBase()} );
            }
        } else {
            return null;
        }
    }

    /**
     * Creates a composite index definition string (an idnex over one or more fields) to be passed when creating a table.
     * @param fields a List of fields for which to make the index definition
     * @return the index definition as a String, or <code>null</code> if no definition is available
     */
    protected String getCompositeIndexDefinition(List fields) throws StorageException {
        Scheme scheme = factory.getScheme(Schemes.CREATE_SECONDARY_KEY);
        if (scheme != null) {
            StringBuffer indices = new StringBuffer();
            for (Iterator i = fields.iterator(); i.hasNext();) {
                FieldDefs field = (FieldDefs)i.next();
                if (indices.length() > 0) indices.append(", ");
                indices.append(factory.getStorageIdentifier(field));
            }
            MMObjectBuilder builder = ((FieldDefs)fields.get(0)).getParent();
            // fallback for cases where builder is null
            // this may happen if this code is called from create()
            // when there is no 'object' builder.
            if (builder == null) {
                return scheme.format(new Object[]{ this, factory.getMMBase(), "object_key", indices.toString(), factory.getMMBase()} );
            } else {
                return scheme.format(new Object[]{ this, builder, builder.getTableName()+"_key", indices.toString(), factory.getMMBase()} );
            }
        } else {
            return null;
        }
    }

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    public void create() throws StorageException {
        MMBase mmbase = factory.getMMBase();
        MMObjectBuilder object = mmbase.getMMObject("object");
        if (object!=null) {
// use builder ...??
        } else {
// generate ourselves ??...
        }
    }

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

