/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.*;
import org.mmbase.storage.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * A JDBC implementation of a storage manager.
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManager.java,v 1.38 2004-01-13 09:59:00 michiel Exp $
 */
public class DatabaseStorageManager implements StorageManager {

    // logger
    private static final Logger log = Logging.getLoggerInstance(DatabaseStorageManager.class);

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
     * This member is set by {!link #getActiveConnection()} and unset by {@link #releaseActiveConnection()}
     */
    protected Connection activeConnection;

    /**
     * <code>true</code> if a transaction has been started.
     * This member is for state maitenance and may be true even if the storage does not support transactions
     */
    protected boolean inTransaction = false;

    /**
     * The transaction issolation level to use when starting a transaction.
     * This value is retrieved from the factory's {@link Attributes#TRANSACTION_ISOLATION_LEVEL} attribute, which is commonly set
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
    public DatabaseStorageManager() {}

    // for debug purposes
    protected final void logQuery(String msg) {
        log.debug("Query :" + msg);
    }

    // javadoc is inherited
    public double getVersion() {
        return 1.0;
    }

    // javadoc is inherited
    public void init(StorageManagerFactory factory) throws StorageException {
        this.factory = factory;
        dataSource = (DataSource)factory.getAttribute(Attributes.DATA_SOURCE);
        if (factory.supportsTransactions()) {
            transactionIsolation = ((Integer)factory.getAttribute(Attributes.TRANSACTION_ISOLATION_LEVEL)).intValue();
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
        // set autocommit to true
        activeConnection.setAutoCommit(true);
        return activeConnection;
    }

    /**
     * Safely closes the active connection.
     * If a transaction has been started, the connection is not closed.
     */
    protected void releaseActiveConnection() {
        if (!(inTransaction && factory.supportsTransactions()) && 
            activeConnection != null) {
            try {
                activeConnection.close();
            } catch (SQLException se) {
                // if something went wrong, log, but do not throw exceptions
                log.error("Failure when closing connection: " + se.getMessage());
            }
            activeConnection = null;
        }
    }

    // javadoc is inherited
    public void beginTransaction() throws StorageException {
        if (inTransaction) {
            throw new StorageException("Cannot start Transaction when one is already active.");
        } else {
            if (factory.supportsTransactions()) {
                try {
                    getActiveConnection();
                    activeConnection.setTransactionIsolation(transactionIsolation);
                    activeConnection.setAutoCommit(false);
                } catch (SQLException se) {
                    releaseActiveConnection();
                    inTransaction = false;
                    throw new StorageException(se);
                }
            }
            inTransaction = true;
            changes = new HashMap();
        }

    }

    // javadoc is inherited
    public void commit() throws StorageException {
        if (!inTransaction) {
            throw new StorageException("No transaction started.");
        } else {
            inTransaction = false;
            if (factory.supportsTransactions()) {
                if (activeConnection == null) {
                    throw new StorageException("No active connection");
                }

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

    // javadoc is inherited
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
     * {@link #changes} object (to be committed after the transaction ends).
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

    public int createKey() throws StorageException {
        try {
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            String query;
            Scheme scheme = factory.getScheme(Schemes.UPDATE_SEQUENCE, Schemes.UPDATE_SEQUENCE_DEFAULT);
            if (scheme != null) {
                query = scheme.format(new Object[] { this, factory.getStorageIdentifier("number")});
                logQuery(query);
                s.executeUpdate(query);
            }
            scheme = factory.getScheme(Schemes.READ_SEQUENCE, Schemes.READ_SEQUENCE_DEFAULT);
            query = scheme.format(new Object[] { this, factory.getStorageIdentifier("number")});
            logQuery(query);
            ResultSet result = s.executeQuery(query);
            if (result.next()) {
                return result.getInt(1);
            } else {
                throw new StorageException("The sequence table is empty.");
            }
        } catch (SQLException se) {
            log.error(Logging.stackTrace(se));
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public String getStringValue(MMObjectNode node, FieldDefs field) throws StorageException {
        try {
            MMObjectBuilder builder = node.getBuilder();
            Scheme scheme = factory.getScheme(Schemes.GET_TEXT_DATA, Schemes.GET_TEXT_DATA_DEFAULT);
            String query = scheme.format(new Object[] { this, builder, field, builder.getField("number"), node });
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            ResultSet result = s.executeQuery(query);
            if ((result != null) && result.next()) {
                return getStringValue(result, 1, field);
            } else {
                throw new StorageException("Node with number " + node.getNumber() + " not found.");
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    /**
     * Retrieve a text for a specified object field.
     * The default method uses {@link ResultSet#getString(int)} to obtain text.
     * Override this method if you want to optimize retrieving large texts,
     * i.e by using clobs or streams.

     * @param result the resultset to retrieve the text from
     * @param index the index of the text in the resultset
     * @param field the (MMBase) fieldtype. This value can be null
     * @return the retrieved text, <code>null</code> if no text was stored
     * @throws SQLException when a database error occurs
     * @throws StorageException when data is incompatible or the function is not supported
     */
    protected String getStringValue(ResultSet result, int index, FieldDefs field) throws StorageException, SQLException {
        if (field.getStorageType() == Types.CLOB || field.getStorageType() == Types.BLOB || factory.hasOption(Attributes.FORCE_ENCODE_TEXT)) {
            InputStream inStream = result.getBinaryStream(index);
            if (result.wasNull()) {
                return null;
            }
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                int c = inStream.read();
                while (c != -1) {
                    bytes.write(c);
                    c = inStream.read();
                }
                inStream.close();
                return new String(bytes.toByteArray(), factory.getMMBase().getEncoding());
            } catch (IOException ie) {
                throw new StorageException(ie);
            }
        } else {
            return result.getString(index);
        }
    }

    /**
     * Determine whether a field (such as a large text or a blob) should be shortened or not.
     * A 'shortened' field contains a placeholder text ('$SHORTED') to indicate that the field is expected to be of large size
     * and should be retrieved by an explicit call to {@link #getStringValue(MMObjectNode, FieldDefs)} or.
     * {@link #getBinaryValue(MMObjectNode, FieldDefs)}.
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

    // javadoc is inherited
    public byte[] getBinaryValue(MMObjectNode node, FieldDefs field) throws StorageException {
        if (factory.hasOption(Attributes.STORES_BINARY_AS_FILE)) {
            return readBinaryFromFile(node, field);
        } else
            try {
                MMObjectBuilder builder = node.getBuilder();
                Scheme scheme = factory.getScheme(Schemes.GET_BINARY_DATA, Schemes.GET_BINARY_DATA_DEFAULT);
                String query = scheme.format(new Object[] { this, builder, field, builder.getField("number"), node });
                getActiveConnection();
                Statement s = activeConnection.createStatement();
                ResultSet result = s.executeQuery(query);
                if ((result != null) && result.next()) {
                    return getBinaryValue(result, 1, field);
                } else {
                    throw new StorageException("Node with number " + node.getNumber() + " not found.");
                }
            } catch (SQLException se) {
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
    }

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * The default method uses {@link ResultSet#getBytes(int)} to obtain text.
     * Override this method if you want to optimize retrieving large objects,
     * i.e by using clobs or streams.
     * @param result the resultset to retrieve the text from
     * @param index the index of the text in the resultset
     * @param field the (MMBase) fieldtype. This value can be null
     * @return the retrieved data, <code>null</code> if no binary data was stored
     * @throws SQLException when a database error occurs
     * @throws StorageException when data is incompatible or the function is not supported
     */
    protected byte[] getBinaryValue(ResultSet result, int index, FieldDefs field) throws StorageException, SQLException {
        if (factory.hasOption(Attributes.SUPPORTS_BLOB)) {
            Blob blob = result.getBlob(index);
            if (result.wasNull()) {
                return null;
            }
            return blob.getBytes(1, (int)blob.length());
        } else {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                InputStream inStream = result.getBinaryStream(index);
                if (result.wasNull()) {
                    return null;
                }
                int c = inStream.read();
                while (c != -1) {
                    bytes.write(c);
                    c = inStream.read();
                }
                inStream.close(); // this also closes the underlying stream
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
        String basePath = (String)factory.getAttribute(Attributes.BINARY_FILE_PATH);
        if (basePath == null || basePath.equals("")) {
            basePath = MMBaseContext.getServletContext().getRealPath("/WEB-INF/data");
        } else if (!basePath.startsWith("/")) {
            basePath = MMBaseContext.getServletContext().getRealPath("/") + File.separator + basePath;
        }
        File dir = new File(basePath + File.separator + factory.getCatalog(), node.getBuilder().getFullTableName());
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
            String fieldName = field.getDBName();
            File binaryFile = getBinaryFile(node, fieldName);
            byte[] value = node.getByteValue(fieldName);
            DataOutputStream byteStream = new DataOutputStream(new FileOutputStream(binaryFile));
            byteStream.write(value);
            byteStream.flush();
            byteStream.close();
        } catch (IOException ie) {
            throw new StorageException(ie);
        }
    }

    /**
     * Read a binary (blob) data file
     * @todo how to do this in a transaction???
     * @param node the node the binary data belongs to
     * @param field the binary field
     * @return the byte array containing the binary data, <code>null</code> if no binary data was stored
     */
    protected byte[] readBinaryFromFile(MMObjectNode node, FieldDefs field) throws StorageException {
        try {
            String fieldName = field.getDBName();
            File binaryFile = getBinaryFile(node, fieldName);
            if (!binaryFile.exists()) {
                return null;
            }
            int fileSize = (int)binaryFile.length();
            byte[] buffer = new byte[fileSize];
            if (fileSize > 0) {
                FileInputStream byteStream = new FileInputStream(binaryFile);
                int len = byteStream.read(buffer, 0, fileSize);
                byteStream.close();
            }
            return buffer;
        } catch (IOException ie) {
            throw new StorageException(ie);
        }
    }

    // javadoc is inherited
    public int create(MMObjectNode node) throws StorageException {
        // assign a new number if the node has not yet been assigned one
        int nodeNumber = node.getNumber();
        if (nodeNumber == -1) {
            nodeNumber = createKey();
            node.setValue("number", nodeNumber);
        }
        MMObjectBuilder builder = node.getBuilder();
        // precommit call, needed to convert or add things before a save
        // Should be done in MMObjectBuilder
        builder.preCommit(node);
        create(node, builder);
        commitChange(node, "n");
        return nodeNumber;
    }

    /**
     * This method inserts a new object in a specific builder, and registers the change.
     * This method makes it easier to implement relational databases, where you may need to update the node
     * in more than one builder.
     * Call this method for all involved builders if you use a relational database.
     * @param node The node to insert. The node already needs to have a (new) number assigned
     * @param builder the builder to store the node
     * @throws StorageException if an error occurred during creation
     */
    protected void create(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // Create a String that represents the fields and values to be used in the insert.
        StringBuffer fieldNames = null;
        StringBuffer fieldValues = null;
        // get a builders fields
        List builderFields = builder.getFields(FieldDefs.ORDER_CREATE);
        List fields = new ArrayList();
        for (Iterator f = builderFields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            if (field.inStorage()) {
                // skip bytevalues that are written to file
                if (factory.hasOption(Attributes.STORES_BINARY_AS_FILE) && (field.getDBType() == FieldDefs.TYPE_BYTE)) {
                    storeBinaryAsFile(node, field);
                    // do not handle this field further
                } else {
                    // store the fieldname and the value parameter
                    fields.add(field);
                    String fieldName = (String)factory.getStorageIdentifier(field);
                    if (fieldNames == null) {
                        fieldNames = new StringBuffer(fieldName);
                        fieldValues = new StringBuffer("?");
                    } else {
                        fieldNames.append(',').append(fieldName);
                        fieldValues.append(",?");
                    }
                }
            }
        }
        if (fields.size() > 0) {
            Scheme scheme = factory.getScheme(Schemes.INSERT_NODE, Schemes.INSERT_NODE_DEFAULT);
            try {
                String query = scheme.format(new Object[] { this, builder, fieldNames.toString(), fieldValues.toString()});
                getActiveConnection();
                PreparedStatement ps = activeConnection.prepareStatement(query);
                for (int fieldNumber = 0; fieldNumber < fields.size(); fieldNumber++) {
                    FieldDefs field = (FieldDefs)fields.get(fieldNumber);
                    setValue(ps, fieldNumber + 1, node, field);
                }
                logQuery(query);
                ps.executeUpdate();
            } catch (SQLException se) {
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
    }

    // javadoc is inherited
    public void change(MMObjectNode node) throws StorageException {
        MMObjectBuilder builder = node.getBuilder();
        // precommit call, needed to convert or add things before a save
        // Should be done in MMObjectBuilder
        builder.preCommit(node);
        change(node, builder);
        commitChange(node, "c");
    }

    /**
     * Change this node in the specified builder.
     * This method makes it easier to implement relational databses, where you may need to update the node
     * in more than one builder.
     * Call this method for all involved builders if you use a relational database.
     * @param node The node to change
     * @param builder the builder to store the node
     * @throws StorageException if an error occurred during change
     */
    protected void change(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // Create a String that represents the fields to be used in the commit
        StringBuffer setFields = null;
        // obtain the node's changed fields
        List fieldNames = node.getChanged();
        List fields = new ArrayList();
        for (Iterator f = fieldNames.iterator(); f.hasNext();) {
            String key = (String)f.next();
            // changing number is not allowed
            if (key.equals("number") || key.equals("otype")) {
                throw new StorageException("trying to change the '" + key + "' field");
            }
            FieldDefs field = builder.getField(key);
            if ((field != null) && field.inStorage()) {
                // skip bytevalues that are written to file
                if (factory.hasOption(Attributes.STORES_BINARY_AS_FILE) && (field.getDBType() == FieldDefs.TYPE_BYTE)) {
                    storeBinaryAsFile(node, field);
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
                for (int fieldNumber = 0; fieldNumber < fields.size(); fieldNumber++) {
                    FieldDefs field = (FieldDefs)fields.get(fieldNumber);
                    setValue(ps, fieldNumber + 1, node, field);
                }
                logQuery(query);
                ps.executeUpdate();
            } catch (SQLException se) {
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
    }

    /**
     * Store the value of a field in a prepared statement
     * @todo Note that this code contains some code that should really be implemented in FieldDefs.
     * In particular, casting should be done in FieldDefs, IMO.
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param node the node from which to retrieve the value
     * @param field the MMBase field, containing meta-information
     * @throws StorageException if the fieldtype is invalid, or data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setValue(PreparedStatement statement, int index, MMObjectNode node, FieldDefs field) throws StorageException, SQLException {
        String fieldName = field.getDBName();
        switch (field.getDBType()) {
            // Store numeric values
            case FieldDefs.TYPE_INTEGER :
            case FieldDefs.TYPE_FLOAT :
            case FieldDefs.TYPE_DOUBLE :
            case FieldDefs.TYPE_LONG :
                setNumericValue(statement, index, node.getValue(fieldName), field);
                break;
                // Store nodes
            case FieldDefs.TYPE_NODE :
                setNodeValue(statement, index, node.getNodeValue(fieldName), field);
                break;
                // Store strings
            case FieldDefs.TYPE_STRING :
                ;
            case FieldDefs.TYPE_XML :
                // note: do not use getStringValue, as this may attempt to
                // retrieve a (old, or nonexistent) value from the storage
                Object textValue = node.getValue(fieldName);
                if (textValue instanceof String) {
                    setStringValue(statement, index, (String)textValue, field);
                } else {
                    setStringValue(statement, index, Casting.toString(textValue), field);
                }
                break;
                // Store binary data
            case FieldDefs.TYPE_BYTE :
                // note: do not use getByteValue, as this may attempt to
                // retrieve a (old, or nonexistent) value from the storage
                Object byteValue = node.getValue(fieldName);
                if (byteValue instanceof byte[]) {
                    setBinaryValue(statement, index, (byte[])byteValue, field);
                } else {
                    setBinaryValue(statement, index, null, field);
                }
                break;
                // unknown field type - error
            default :
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
            case FieldDefs.TYPE_INTEGER :
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(index, Casting.toInt(value));
                }
                break;
                // Store floats
            case FieldDefs.TYPE_FLOAT :
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.REAL);
                } else {
                    statement.setFloat(index, Casting.toFloat(value));
                }
                break;
                // Store doubles
            case FieldDefs.TYPE_DOUBLE :
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.DOUBLE);
                } else {
                    statement.setDouble(index, Casting.toDouble(value));
                }
                break;
                // Store longs
            case FieldDefs.TYPE_LONG :
                if (value == MMObjectNode.VALUE_NULL && !field.getDBNotNull()) {
                    statement.setNull(index, java.sql.Types.BIGINT);
                } else {
                    statement.setLong(index, Casting.toLong(value));
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
        if (value == MMObjectNode.VALUE_NULL || value == null) {
            if (field.getDBNotNull()) {
                throw new StorageException("The NODE field with name " + field.getDBName() + " can not be NULL.");
            } else {
                statement.setNull(index, java.sql.Types.INTEGER);
            }
        } else {
            // retrieve node as a numeric value
            statement.setInt(index, value.getNumber());
        }
    }

    /**
     * Store binary data of a field in a prepared statement.
     * This basic implementation uses a binary stream to set the data.
     * Null values are stored as NULL if possible, otherwise they are stored as an empty byte-array.
     * Override this method if you use another way to store binaries (i.e. Blobs).
     * @param statement the prepared statement
     * @param index the index of the field in the prepared statement
     * @param value the data (byte array) to store
     * @param field the MMBase field, containing meta-information. This value can be null
     * @throws StorageException if the data is invalid or missing
     * @throws SQLException if an error occurred while filling in the fields
     */
    protected void setBinaryValue(PreparedStatement statement, int index, byte[] value, FieldDefs field) throws StorageException, SQLException {
        if (value == null) {
            if (field == null || field.getDBNotNull()) {
                value = new byte[] {};
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
     * Store the text value of a field in a prepared statement.
     * Null values are stored as NULL if possible, otherwise they are stored as an empty string.
     * If the FORCE_ENCODE_TEXT option is set, text is encoded (using the MMBase encoding) to a byte array
     * and stored as a binary stream.
     * Otherwise it uses {@link PreparedStatement#setString(int, String)} to set the data.
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
        // Store data as a binary stream when the code is a clob or blob, or
        // when database-force-encode-text is true.
        if (field.getStorageType() == Types.CLOB || field.getStorageType() == Types.BLOB || factory.hasOption(Attributes.FORCE_ENCODE_TEXT)) {
            byte[] rawchars = null;
            try {
                rawchars = value.getBytes(factory.getMMBase().getEncoding());
                ByteArrayInputStream stream = new ByteArrayInputStream(rawchars);
                statement.setBinaryStream(index, stream, rawchars.length);
                stream.close();
            } catch (IOException ie) {
                throw new StorageException(ie);
            }
        } else {
            statement.setString(index, value);
        }
    }

    // javadoc is inherited
    public void delete(MMObjectNode node) throws StorageException {
        delete(node, node.getBuilder());
        commitChange(node, "d");
    }

    /**
     * Delete a node from a specific builder
     * This method makes it easier to implement relational databses, where you may need to remove the node
     * in more than one builder.
     * Call this method for all involved builders if you use a relational database.
     * @param node The node to delete
     * @throws StorageException if an error occurred during delete
     */
    protected void delete(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        // determine parent
        if (node.hasRelations()) {
            throw new StorageException("cannot delete node " + node.getNumber() + ", it still has relations");
        }
        try {
            Scheme scheme = factory.getScheme(Schemes.DELETE_NODE, Schemes.DELETE_NODE_DEFAULT);
            String query = scheme.format(new Object[] { this, builder, builder.getField("number"), node });
            getActiveConnection();
            Statement s = activeConnection.createStatement();
            logQuery(query);
            s.executeUpdate(query);
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException {
        Scheme scheme = factory.getScheme(Schemes.SELECT_NODE, Schemes.SELECT_NODE_DEFAULT);
        try {
            getActiveConnection();
            // get a builders fields
            List builderFields = builder.getFields(FieldDefs.ORDER_CREATE);
            List fields = new ArrayList();
            StringBuffer fieldNames = null;
            for (Iterator f = builderFields.iterator(); f.hasNext();) {
                FieldDefs field = (FieldDefs)f.next();
                if (field.inStorage() && !shorten(field)) {
                    // store the fieldname and the value parameter
                    String fieldName = (String)factory.getStorageIdentifier(field);
                    if (fieldNames == null) {
                        fieldNames = new StringBuffer(fieldName);
                    } else {
                        fieldNames.append(',').append(fieldName);
                    }
                }
            }
            String query = scheme.format(new Object[] { this, builder, fieldNames.toString(), builder.getField("number"), new Integer(number)});
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
                for (Iterator i = builder.getFields(FieldDefs.ORDER_CREATE).iterator(); i.hasNext();) {
                    FieldDefs field = (FieldDefs)i.next();
                    if (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT || field.getDBState() == FieldDefs.DBSTATE_SYSTEM) {
                        if (shorten(field)) {
                            node.setValue(field.getDBName(), "$SHORTED");
                        } else if (field.getDBType() == FieldDefs.TYPE_BYTE && factory.hasOption(Attributes.STORES_BINARY_AS_FILE)) {
                            node.setValue(field.getDBName(), readBinaryFromFile(node, field));
                        } else {
                            String id = (String)factory.getStorageIdentifier(field);
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
     * @param field the expected MMBase field type. This can be null
     * @return the value
     * @throws StorageException if the value cannot be retrieved from the resultset
     */
    protected Object getValue(ResultSet result, int index, FieldDefs field) throws StorageException {
        try {
            int dbtype = FieldDefs.TYPE_UNKNOWN;
            if (field != null)
                dbtype = field.getDBType();
            switch (dbtype) {
                // string-type fields
            case FieldDefs.TYPE_XML :
            case FieldDefs.TYPE_STRING : {
                return getStringValue(result, index, field);
            }
            case FieldDefs.TYPE_BYTE : {
                return getBinaryValue(result, index, field);
            }
            default : {
                return result.getObject(index);
            }
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        }
    }

    // javadoc is inherited
    public int getNodeType(int number) throws StorageException {
        Scheme scheme = factory.getScheme(Schemes.SELECT_NODE_TYPE, Schemes.SELECT_NODE_TYPE_DEFAULT);
        try {
            getActiveConnection();
            MMBase mmbase = factory.getMMBase();
            String query = scheme.format(new Object[] { this, mmbase, mmbase.getTypeDef().getField("number"), new Integer(number)});
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
     * Returns whether tables inherit fields form parent tables.
     * this determines whether fields that are inherited in mmbase builders
     * are redefined in the database tables.
     */
    protected boolean tablesInheritFields() {
        return true;
    }

    /**
     * Determines whether the storage should make a field definition in a builder table for a
     * specified field.
     */
    protected boolean isPartOfBuilderDefinition(FieldDefs field) {
        // persistent field?
        // skip binary fields when values are written to file
        boolean isPart = field.inStorage() && (field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE));
        // also, if the database is OO, and the builder has a parent,
        // skip fields that are in the parent builder
        MMObjectBuilder parentBuilder = field.getParent().getParentBuilder();
        if (isPart && parentBuilder != null) {
            isPart = !tablesInheritFields() || parentBuilder.getField(field.getDBName()) == null;
        }
        return isPart;
    }

    // javadoc is inherited
    public void create(MMObjectBuilder builder) throws StorageException {
        log.debug("Creating a table for " + builder);
        // use the builder to get the fields and create a
        // valid create SQL string
        // for backward compatibility, fields are to be created in the order defined
        List fields = builder.getFields(FieldDefs.ORDER_CREATE);
        StringBuffer createFields = new StringBuffer();
        StringBuffer createIndices = new StringBuffer();
        StringBuffer createFieldsAndIndices = new StringBuffer();
        StringBuffer createCompositeIndices = new StringBuffer();
        List compositeIndices = new ArrayList();
        // obtain the parentBuilder
        MMObjectBuilder parentBuilder = builder.getParentBuilder();
        Scheme rowtypeScheme;
        Scheme tableScheme;
        // if the builder has no parent, it is an object table,
        // so use CREATE_OBJECT_ROW_TYPE and CREATE_OBJECT_TABLE schemes.
        // Otherwise use CREATE_ROW_TYPE and CREATE_TABLE schemes.
        //
        if (parentBuilder == null) {
            rowtypeScheme = factory.getScheme(Schemes.CREATE_OBJECT_ROW_TYPE);
            tableScheme = factory.getScheme(Schemes.CREATE_OBJECT_TABLE, Schemes.CREATE_OBJECT_TABLE_DEFAULT);
        } else {
            rowtypeScheme = factory.getScheme(Schemes.CREATE_ROW_TYPE);
            tableScheme = factory.getScheme(Schemes.CREATE_TABLE, Schemes.CREATE_TABLE_DEFAULT);
        }
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            // convert a fielddef to a field SQL createdefinition
            if (isPartOfBuilderDefinition(field)) {
                String fieldDef = getFieldDefinition(field);
                if (createFields.length() > 0)
                    createFields.append(", ");
                createFields.append(fieldDef);
                // test on other indices
                String constraintDef = getConstraintDefinition(field);
                if (constraintDef != null) {
                    // note: the indices are prefixed with a comma, as they generally follow the fieldlist.
                    // if the database uses rowtypes, however, fields are not included in the CREATE TABLE statement,
                    // and the comma should not be prefixed.
                    if (rowtypeScheme == null || createIndices.length() > 0)
                        createIndices.append(", ");
                    createIndices.append(constraintDef);
                    if (createFieldsAndIndices.length() > 0)
                        createFieldsAndIndices.append(", ");
                    createFieldsAndIndices.append(fieldDef + ", " + constraintDef);
                } else {
                    if (createFieldsAndIndices.length() > 0)
                        createFieldsAndIndices.append(", ");
                    createFieldsAndIndices.append(fieldDef);
                }
            }
        }
        //  composite constraints
        String compConstraintDef = getCompositeConstraintDefinition(builder);
        if (compConstraintDef != null) {
            // note: the indices are prefixed with a comma, as they generally follow the fieldlist.
            // if the database uses rowtypes, however, fields are not included in the CREATE TABLE statement,
            // and the comma should not be prefixed.
            if (rowtypeScheme == null || createIndices.length() > 0)
                createCompositeIndices.append(", ");
            createCompositeIndices.append(compConstraintDef);
        }
        try {
            getActiveConnection();
            // create a rowtype, if a scheme has been given
            // Note that creating a rowtype is optional
            if (rowtypeScheme != null) {
                String query = rowtypeScheme.format(new Object[] { this, builder, createFields.toString(), parentBuilder });
                // remove parenthesis with empty field definitions -
                // unfortunately Schems don't take this into account
                if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                    query = query.replaceAll("\\(\\s*\\)", "");
                }
                Statement s = activeConnection.createStatement();
                logQuery(query);
                s.executeUpdate(query);
            }
            // create the table
            String query =
                tableScheme.format(
                    new Object[] {
                        this,
                        builder,
                        createFields.toString(),
                        createIndices.toString(),
                        createFieldsAndIndices.toString(),
                        createCompositeIndices.toString(),
                        parentBuilder });
            // remove parenthesis with empty field definitions -
            // unfortunately Schemes don't take this into account
            if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                query = query.replaceAll("\\(\\s*\\)", "");
            }

            Statement s = activeConnection.createStatement();
            logQuery(query);
            s.executeUpdate(query);
            // TODO: use CREATE_SECONDARY_INDEX to create indices for all fields that have one
            // has to be done seperate
        } catch (SQLException se) {
            log.error(Logging.stackTrace(se));
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
        verify(builder);
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
            String fieldDef = factory.getStorageIdentifier(field) + " " + ((TypeMapping)typeMappings.get(found)).getType(size);
            if (field.getDBNotNull()) {
                fieldDef += " NOT NULL";
            }
            return fieldDef;
        } else {
            throw new StorageException("Type for field " + field.getDBName() + ": " + typeName + " (" + size + ") undefined.");
        }
    }

    /**
     * Creates an index definition string to be passed when creating a table.
     * @param field the field for which to make the index definition
     * @return the index definition as a String, or <code>null</code> if no definition is available
     */
    protected String getConstraintDefinition(FieldDefs field) throws StorageException {
        String definitions = null;
        Scheme scheme = null;
        if (field.getDBName().equals("number")) {
            scheme = factory.getScheme(Schemes.CREATE_PRIMARY_KEY, Schemes.CREATE_PRIMARY_KEY_DEFAULT);
            if (scheme != null) {
                definitions = scheme.format(new Object[] { this, field.getParent(), field });
            }
        } else {
            if (field.isKey() && !factory.hasOption(Attributes.SUPPORTS_COMPOSITE_INDEX)) {
                scheme = factory.getScheme(Schemes.CREATE_UNIQUE_KEY, Schemes.CREATE_UNIQUE_KEY_DEFAULT);
                if (scheme != null) {
                    definitions = scheme.format(new Object[] { this, field.getParent(), field, field });
                }
            }
            if (field.getDBType() == FieldDefs.TYPE_NODE) {
                scheme = factory.getScheme(Schemes.CREATE_FOREIGN_KEY, Schemes.CREATE_FOREIGN_KEY_DEFAULT);
                if (scheme != null) {
                    String definition = scheme.format(new Object[] { this, field.getParent(), field, factory.getMMBase(), factory.getStorageIdentifier("number")});
                    if (definitions != null) {
                        definitions += ", " + definition;
                    } else {
                        definitions = definition;
                    }
                }
            }
        }
        return definitions;
    }

    /**
     * Creates a composite index definition string (an index over one or more fields) to be
     * passed when creating a table.
     * @param fields a List of fields for which to make the index definition
     * @return the index definition as a String, or <code>null</code> if no definition is available
     */
    protected String getCompositeConstraintDefinition(MMObjectBuilder builder) throws StorageException {
        Scheme scheme = factory.getScheme(Schemes.CREATE_COMPOSITE_KEY, Schemes.CREATE_COMPOSITE_KEY_DEFAULT);
        if (scheme != null) {
            StringBuffer indices = new StringBuffer();
            List fields = builder.getFields(FieldDefs.ORDER_CREATE);
            List compositeIndices = new ArrayList();
            // obtain the parentBuilder
            MMObjectBuilder parentBuilder = builder.getParentBuilder();
            for (Iterator f = fields.iterator(); f.hasNext();) {
                FieldDefs field = (FieldDefs)f.next();
                if (isPartOfBuilderDefinition(field) && !"number".equals(field.getDBName()) && field.isKey() && factory.hasOption(Attributes.SUPPORTS_COMPOSITE_INDEX)) {
                    if (indices.length() > 0)
                        indices.append(", ");
                    indices.append(factory.getStorageIdentifier(field));
                }
            }
            if (indices.length() > 0) {
                return scheme.format(new Object[] { this, builder, indices.toString()});
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // javadoc is inherited
    public void change(MMObjectBuilder builder) throws StorageException {
        // test if you can make changes
        // iterate through the fields,
        // use metadata.getColumns(...)  to select fields
        //      (incl. name, datatype, size, null)
        // use metadata.getImportedKeys(...) to get foreign keys
        // use metadata.getIndexInfo(...) to get composite and other indexes
        // determine changes and run them
        throw new StorageException("Operation not supported");
    }

    // javadoc is inherited
    public void delete(MMObjectBuilder builder) throws StorageException {
        int size = size(builder);
        if (size != 0) {
            throw new StorageException("Can not drop builder, it still contains " + size + " node(s)");
        }
        try {
            getActiveConnection();
            Scheme scheme = factory.getScheme(Schemes.DROP_TABLE, Schemes.DROP_TABLE_DEFAULT);
            String query = scheme.format(new Object[] { this, builder });
            Statement s = activeConnection.createStatement();
            logQuery(query);
            s.executeUpdate(query);
            scheme = factory.getScheme(Schemes.DROP_ROW_TYPE);
            if (scheme != null) {
                query = scheme.format(new Object[] { this, builder });
                s = activeConnection.createStatement();
                logQuery(query);
                s.executeUpdate(query);
            }
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public void create() throws StorageException {
        MMBase mmbase = factory.getMMBase();
        create(factory.getMMBase().getRootBuilder());
        createSequence();
    }

    /**
     * Creates a means for the database to pre-create keys with increasing numbers.
     * A sequence can be a database routine, a number table, or anything else that can be used to create unique numbers.
     * Keys can be obtained from the sequence by calling {@link #createKey()}.
     * @throws StorageException when the sequence can not be created
     */
    protected void createSequence() throws StorageException {
        try {
            getActiveConnection();
            // create the type mapping to search for
            String typeName = FieldDefs.getDBTypeDescription(FieldDefs.TYPE_INTEGER);
            TypeMapping mapping = new TypeMapping();
            mapping.name = typeName;
            // search type mapping
            List typeMappings = factory.getTypeMappings();
            int found = typeMappings.indexOf(mapping);
            if (found == -1) {
                throw new StorageException("Type " + typeName + " undefined.");
            }
            String fieldName = (String)factory.getStorageIdentifier("number");
            String fieldDef = fieldName + " " + ((TypeMapping)typeMappings.get(found)).type + " NOT NULL, PRIMARY KEY(" + fieldName + ")";
            String query;
            Statement s = activeConnection.createStatement();
            Scheme scheme = factory.getScheme(Schemes.CREATE_SEQUENCE, Schemes.CREATE_SEQUENCE_DEFAULT);
            if (scheme != null) {
                query = scheme.format(new Object[] { this, fieldDef });
                logQuery(query);
                s.executeUpdate(query);
            }
            scheme = factory.getScheme(Schemes.INIT_SEQUENCE, Schemes.INIT_SEQUENCE_DEFAULT);
            if (scheme != null) {
                query = scheme.format(new Object[] { this, factory.getStorageIdentifier("number"), new Integer(1)});
                logQuery(query);
                s.executeUpdate(query);
            }
        } catch (SQLException se) {
            throw new StorageException(se);
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public boolean exists(MMObjectBuilder builder) throws StorageException {
        boolean result = exists((String)factory.getStorageIdentifier(builder));
        if (result) {
            verify(builder);
        }
        return result;
    }

    /**
     * Queries the database metadata to test whether a given table exists.
     * @param tableName name of the table to look for
     * @throws StorageException when the metadata could not be retrieved
     * @return <code>true</code> if the table exists
     */
    protected boolean exists(String tableName) throws StorageException {
        try {
            getActiveConnection();
            DatabaseMetaData metaData = activeConnection.getMetaData();
            ResultSet res = metaData.getTables(null, null, tableName, null);
            boolean result = res.next();
            return result;
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public boolean exists() throws StorageException {
        return exists(factory.getMMBase().getRootBuilder());
    }

    // javadoc is inherited
    public int size(MMObjectBuilder builder) throws StorageException {
        try {
            getActiveConnection();
            Scheme scheme = factory.getScheme(Schemes.GET_TABLE_SIZE, Schemes.GET_TABLE_SIZE_DEFAULT);
            String query = scheme.format(new Object[] { this, builder });
            Statement s = activeConnection.createStatement();
            ResultSet res = s.executeQuery(query);
            res.next();
            return res.getInt(1);
        } catch (Exception e) {
            throw new StorageException(e);
        } finally {
            releaseActiveConnection();
        }
    }

    // javadoc is inherited
    public int size() throws StorageException {
        return size(factory.getMMBase().getRootBuilder());
    }

    /**
     * Guess the (mmbase) type in storage using the JDNC type.
     * Because a JDBC type can represent more than one mmbase Type,
     * the current type is also passed - if the current type matches, that type
     * is returned, otherwise the method returns the closest matching MMBase type.
     */
    protected int getJDBCtoMMBaseType(int jdbcType, int mmbaseType) {
        switch (jdbcType) {
            case Types.INTEGER :
                ;
            case Types.SMALLINT :
                ;
            case Types.TINYINT :
                ;
            case Types.BIGINT :
                ;
                if (mmbaseType == FieldDefs.TYPE_INTEGER || mmbaseType == FieldDefs.TYPE_LONG || mmbaseType == FieldDefs.TYPE_NODE) {
                    return mmbaseType;
                } else {
                    return FieldDefs.TYPE_INTEGER;
                }
            case Types.FLOAT :
                ;
            case Types.REAL :
                ;
            case Types.DOUBLE :
                ;
            case Types.NUMERIC :
                ;
            case Types.DECIMAL :
                ;
                if (mmbaseType == FieldDefs.TYPE_FLOAT || mmbaseType == FieldDefs.TYPE_DOUBLE) {
                    return mmbaseType;
                } else {
                    return FieldDefs.TYPE_DOUBLE;
                }
            case Types.BINARY :
                ;
            case Types.LONGVARBINARY :
                ;
            case Types.VARBINARY :
                ;
            case Types.BLOB :
                ;
                if (mmbaseType == FieldDefs.TYPE_BYTE || mmbaseType == FieldDefs.TYPE_STRING || mmbaseType == FieldDefs.TYPE_XML) {
                    return mmbaseType;
                } else {
                    return FieldDefs.TYPE_BYTE;
                }
            case Types.CHAR :
                ;
            case Types.CLOB :
                ;
            case Types.LONGVARCHAR :
                ;
            case Types.VARCHAR :
                ;
                if (mmbaseType == FieldDefs.TYPE_STRING || mmbaseType == FieldDefs.TYPE_XML) {
                    return mmbaseType;
                } else {
                    return FieldDefs.TYPE_STRING;
                }
            default :
                return FieldDefs.TYPE_UNKNOWN;
        }
    }

    /**
     * Tests whether a builder and the table present in the database match.
     */
    public void verify(MMObjectBuilder builder) throws StorageException {
        try {
            getActiveConnection();
            String tableName = (String)factory.getStorageIdentifier(builder);
            DatabaseMetaData metaData = activeConnection.getMetaData();
            // skip if does not support inheritance, or if this is the object table
            if (tablesInheritFields()) {
                MMObjectBuilder parent = builder.getParentBuilder();
                try {
                    ResultSet superTablesSet = metaData.getSuperTables(null, null, tableName);
                    if (superTablesSet.next()) {
                        String parentName = superTablesSet.getString("SUPERTABLE_NAME");
                        if (parent == null || !parentName.equals(factory.getStorageIdentifier(parent))) {
                            log.error(
                                "VERIFY: parent builder in storage for builder "
                                    + builder.getTableName()
                                    + " should be "
                                    + parent.getTableName()
                                    + " but defined as "
                                    + parentName);
                        } else {
                            log.debug("VERIFY: parent builder in storage for builder " + builder.getTableName() + " defined as " + parentName);
                        }
                    } else if (parent != null) {
                        log.error("VERIFY: no parent builder defined in storage for builder " + builder.getTableName());
                    }
                } catch (java.lang.AbstractMethodError ae) {
                    // ignore: the method is not implemented by the JDBC Driver, (i.e. postgresql)
                    // so no results can be retrieved
                    log.debug("VERIFY: Driver does not fully implement the 3.0 API, skipping inheritance consistency tests.");
                }
            }
            ResultSet columnsSet = metaData.getColumns(null, null, tableName, null);
            // get column information
            Map columns = new HashMap();
            while (columnsSet.next()) {
                Map colInfo = new HashMap();
                colInfo.put("DATA_TYPE", new Integer(columnsSet.getInt("DATA_TYPE")));
                colInfo.put("TYPE_NAME", columnsSet.getString("TYPE_NAME"));
                colInfo.put("COLUMN_SIZE", new Integer(columnsSet.getInt("COLUMN_SIZE")));
                colInfo.put("NULLABLE", new Boolean(columnsSet.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls));
                columns.put(columnsSet.getString("COLUMN_NAME"), colInfo);
            }
            columnsSet.close();
            // iterate through fields and check all fields present
            int pos = 0;
            List builderFields = builder.getFields(FieldDefs.ORDER_CREATE);
            for (Iterator i = builderFields.iterator(); i.hasNext();) {
                FieldDefs field = (FieldDefs)i.next();
                if (field.inStorage() && (field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
                    pos++;
                    Object id = field.getStorageIdentifier();
                    Map colInfo = (Map)columns.get(id);
                    if ((colInfo == null)) {
                        log.error("VERIFY: Field '" + field.getDBName() + "' of builder '" + builder.getTableName() + "' does NOT exist in storage! Field will be concidered virtual.");
                        // set field to virtual so it will not be stored -
                        // prevents future queries or statements from failing
                        field.setDBState(FieldDefs.DBSTATE_VIRTUAL);
                    } else {
                        // compare type
                        int curtype = field.getDBType();
                        int storageType = ((Integer)colInfo.get("DATA_TYPE")).intValue();
                        field.setStorageType(storageType);
                        int type = getJDBCtoMMBaseType(storageType, curtype);
                        if (type != curtype) {
                            log.error(
                                "VERIFY: Field '"
                                    + field.getDBName()
                                    + "' of builder '" + builder.getTableName() + "' mismatch : type defined as "
                                    + FieldDefs.getDBTypeDescription(curtype)
                                    + ", but in storage "
                                    + FieldDefs.getDBTypeDescription(type)
                                    + " ("
                                    + colInfo.get("TYPE_NAME")
                                    + ")");
                        } else {
                            boolean nullable = ((Boolean)colInfo.get("NULLABLE")).booleanValue();
                            if (nullable == field.getDBNotNull()) {
                                // only correct if storage is more restrictive
                                if (!nullable) {
                                    field.setDBNotNull(!nullable);
                                    log.warn("VERIFY: Field '" + field.getDBName() + "' of builder '" + builder.getTableName() + "' mismatch : notnull in storage is " + !nullable + " (value corrected for this session)");
                                } else {
                                    log.debug("VERIFY: Field '" + field.getDBName() + "' of builder '" + builder.getTableName() + "' mismatch : notnull in storage is " + !nullable);
                                }
                            }
                            // compare size
                            int size = ((Integer)colInfo.get("COLUMN_SIZE")).intValue();
                            int cursize = field.getDBSize();
                            if (cursize != -1 && size != -1 && size != cursize) {
                                if (size < cursize || cursize <= 255) {
                                    // only correct if storage is more restrictive
                                    field.setDBSize(size);
                                    log.warn(
                                        "VERIFY: Field '"
                                            + field.getDBName()
                                            + "' of builder '" + builder.getTableName() + "' mismatch : size defined as "
                                            + cursize
                                            + ", but in storage "
                                            + size
                                            + " (value corrected for this session)");
                                } else if (cursize <= 255) {
                                    // ignore the size difference for large fields (blobs or texts) if
                                    // the storage size is larger than that defined for the builder
                                    log.debug("VERIFY: Field '" + field.getDBName() + "' of builder '" + builder.getTableName() + "' mismatch : size defined as " + cursize + ", but in storage " + size);
                                }
                            }
                        }
                        columns.remove(id);
                    }
                }
            }
            // if any are left, these fields were removed!
            for (Iterator i = columns.keySet().iterator(); i.hasNext();) {
                String column = (String)i.next();
                log.warn("VERIFY: Column '" + column + "' for builder '" + builder.getTableName() + "' in Storage but not defined!");
            }
        } catch (Exception e) {
            log.error("Error during check of table. Asuming it correct." + e.getMessage() + Logging.stackTrace(e));
            //throw new StorageException(e.getMessage(), e);
        } finally {
            releaseActiveConnection();
        }
    }

    /**
     * Drop a constraint for a composite index.
     * You should have an active connection before calling this method.
     * @param builder the builder for which to drop the composite key
     * @throws
     * @throws
     */
    protected void deleteCompositeIndex(MMObjectBuilder builder) throws StorageException, SQLException {
        if (factory.hasOption(Attributes.SUPPORTS_COMPOSITE_INDEX)) {
            //  TODO: We should determine if there IS an index before removing it...
            //  Scheme: DELETE_CONSTRAINT
            Scheme deleteIndexScheme = factory.getScheme(Schemes.DELETE_CONSTRAINT_SCHEME, Schemes.DELETE_CONSTRAINT_SCHEME_DEFAULT);
            if (deleteIndexScheme != null) {
                DatabaseMetaData metaData = activeConnection.getMetaData();
                ResultSet indexSet = metaData.getIndexInfo(null, null, builder.getTableName(), true, false);
                // get index information
                String indexName = null;
                while (indexSet.next()) {
                    int indexType = indexSet.getInt("TYPE");
                    if (indexType == DatabaseMetaData.tableIndexClustered) {
                        indexName = indexSet.getString("INDEX_NAME");
                    }
                }
                indexSet.close();
                // remove index if found
                if (indexName != null) {
                    Statement s = activeConnection.createStatement();
                    String query = deleteIndexScheme.format(new Object[] { this, builder, indexName });
                    logQuery(query);
                    s.executeUpdate(query);
                }
            }
        }
    }

    /**
     * Create a constraint for a composite index.
     * @param builder the builder for which to add the composite key
     */
    protected void createCompositeIndex(MMObjectBuilder builder) throws StorageException, SQLException {
        if (factory.hasOption(Attributes.SUPPORTS_COMPOSITE_INDEX)) {
            //  Scheme: CREATE_CONSTRAINT
            Scheme createIndexScheme = factory.getScheme(Schemes.CREATE_CONSTRAINT_SCHEME, Schemes.CREATE_CONSTRAINT_SCHEME_DEFAULT);
            if (createIndexScheme != null) {
                Statement s = activeConnection.createStatement();
                String constraintDef = getCompositeConstraintDefinition(builder);
                String query = createIndexScheme.format(new Object[] { this, builder, constraintDef });
                logQuery(query);
                s.executeUpdate(query);
            }
        }
    }

    // javadoc is inherited
    public void create(FieldDefs field) throws StorageException {
        if (!factory.hasOption(Attributes.SUPPORTS_DATA_DEFINITION)) {
            throw new StorageException("Data definiton statements (create new field) are not supported.");
        }
        if (factory.getScheme(Schemes.CREATE_OBJECT_ROW_TYPE) != null) {
            throw new StorageException("Can not use data definiton statements (create new field) on row types.");
        }
        if (field.inStorage() && (field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
            Scheme scheme = factory.getScheme(Schemes.CREATE_FIELD_SCHEME, Schemes.CREATE_FIELD_SCHEME_DEFAULT);
            if (scheme == null) {
                throw new StorageException("Storage layer does not support the dynamic creation of fields");
            } else {
                try {
                    getActiveConnection();
                    // add field
                    String fieldDef = getFieldDefinition(field);
                    String query = scheme.format(new Object[] { this, field.getParent(), fieldDef });
                    Statement s = activeConnection.createStatement();
                    logQuery(query);
                    s.executeUpdate(query);
                    // add constraints
                    String constraintDef = getConstraintDefinition(field);
                    if (constraintDef != null) {
                        scheme = factory.getScheme(Schemes.CREATE_CONSTRAINT_SCHEME, Schemes.CREATE_CONSTRAINT_SCHEME_DEFAULT);
                        if (scheme != null) {
                            query = scheme.format(new Object[] { this, field.getParent(), constraintDef });
                            s = activeConnection.createStatement();
                            logQuery(query);
                            s.executeUpdate(query);
                        }
                    }
                    // if the field is a key, redefine the composite key
                    if (field.isKey()) {
                        deleteCompositeIndex(field.getParent());
                        createCompositeIndex(field.getParent());
                    }
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                }
            }
        }
    }

    // javadoc is inherited
    public void change(FieldDefs field) throws StorageException {
        if (!factory.hasOption(Attributes.SUPPORTS_DATA_DEFINITION)) {
            throw new StorageException("Data definiton statements (change field) are not supported.");
        }
        if (factory.getScheme(Schemes.CREATE_OBJECT_ROW_TYPE) != null) {
            throw new StorageException("Can not use data definiton statements (change field) on row types.");
        }
        if (field.inStorage() && (field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
            Scheme scheme = factory.getScheme(Schemes.CHANGE_FIELD_SCHEME, Schemes.CHANGE_FIELD_SCHEME_DEFAULT);
            if (scheme == null) {
                throw new StorageException("Storage layer does not support the dynamic changing of fields");
            } else {
                try {
                    getActiveConnection();
                    // if the field is a key, delete the composite key
                    // Note: changes in whether a field is part of a unique key or not cannot be
                    // made at this moment.
                    if (field.isKey()) {
                        deleteCompositeIndex(field.getParent());
                    }
                    // Todo: explicitly remove indices ??
                    String fieldDef = getFieldDefinition(field);
                    String query = scheme.format(new Object[] { this, field.getParent(), fieldDef });
                    Statement s = activeConnection.createStatement();
                    logQuery(query);
                    s.executeUpdate(query);
                    // add constraints
                    String constraintDef = getConstraintDefinition(field);
                    if (constraintDef != null) {
                        scheme = factory.getScheme(Schemes.CREATE_CONSTRAINT_SCHEME, Schemes.CREATE_CONSTRAINT_SCHEME_DEFAULT);
                        if (scheme != null) {
                            query = scheme.format(new Object[] { this, field.getParent(), constraintDef });
                            s = activeConnection.createStatement();
                            logQuery(query);
                            s.executeUpdate(query);
                        }
                    }
                    // if the field is a key, add the composite key
                    if (field.isKey()) {
                        createCompositeIndex(field.getParent());
                    }
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                }
            }
        }
    }

    // javadoc is inherited
    public void delete(FieldDefs field) throws StorageException {
        if (!factory.hasOption(Attributes.SUPPORTS_DATA_DEFINITION)) {
            throw new StorageException("Data definiton statements (delete field) are not supported.");
        }
        if (factory.getScheme(Schemes.CREATE_OBJECT_ROW_TYPE) != null) {
            throw new StorageException("Can not use data definiton statements (delete field) on row types.");
        }
        if (field.inStorage() && (field.getDBType() != FieldDefs.TYPE_BYTE || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
            Scheme scheme = factory.getScheme(Schemes.DELETE_FIELD_SCHEME, Schemes.DELETE_FIELD_SCHEME_DEFAULT);
            if (scheme == null) {
                throw new StorageException("Storage layer does not support the dynamic deleting of fields");
            } else {
                try {
                    getActiveConnection();
                    // if the field is a key, delete the composite key
                    if (field.isKey()) {
                        deleteCompositeIndex(field.getParent());
                    }
                    // Todo: explicitly remove indices ??
                    String query = scheme.format(new Object[] { this, field.getParent(), field });
                    Statement s = activeConnection.createStatement();
                    logQuery(query);
                    s.executeUpdate(query);
                    // if the field is a key, add the composite key
                    if (field.isKey()) {
                        createCompositeIndex(field.getParent());
                    }
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                }
            }
        }
    }
}
