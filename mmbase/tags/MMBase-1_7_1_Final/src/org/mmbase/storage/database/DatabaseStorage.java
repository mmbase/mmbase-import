/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.*;
import java.sql.*;
import java.io.File;

import org.mmbase.module.database.JDBCInterface;

import org.mmbase.storage.*;
import org.mmbase.module.core.*;
import org.mmbase.util.XMLDatabaseReader;

/**
 * Storage interface for use with a database.
 * This interface adds two type of methods.
 * The first are methods used to configure the database, or to store the current
 * configuration, either by using a Document or through seperate calls.
 * The second methods are used as callback methods by a DatabaseTransaction object,
 * to handle specific database dependent routines, such as storing binary objects.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: DatabaseStorage.java,v 1.6 2004-01-27 12:04:46 pierre Exp $
 */
public interface DatabaseStorage extends Storage  {

    /** Indicates a field is not a key, and nullable */
    public static final int KEY_NONE = 0;
    /** Indicates a field is a primary key */
    public static final int KEY_PRIMARY = 1;
    /** Indicates a field is a unique secondary key */
    public static final int KEY_SECONDARY = 2;
    /** Indicates a field is a foreign key */
    public static final int KEY_FOREIGN = 3;
    /** Indicates a field is not a key, and not nullable */
    public static final int KEY_NOTNULL = 4;

    /**
     * Initializes the database layer.
     * This reads database specific content from the database configuration document.
     * If needed, the code creates a 'numbertable' for mmbase to track number generation.
     * @param mmb the MBase instance that uses this database layer
     * @param reader the database configuration reader
     */
    public void init(MMBase mmb, XMLDatabaseReader reader);

    /**
     * Returns whether binary objects are stored as files (rather than in the database)
     * @return true if binary objects are stored as files
     */
    public boolean getStoreBinaryAsFile();

    /**
     * Sets whether binary objects are stored as files (rather than in the database)
     * @param value if true, binary objects will be stored as files
     */
    public void setStoreBinaryAsFile(boolean value);

    /**
     * Returns the filepath where binary objects are stored.
     * Only applies if {@link #getStoreBinaryAsFile} returns true.
     * @return the file path
     */
    public File getBinaryFilePath();

    /**
     * Sets the filepath where binary objects are stored.
     * Only applies if {@link #getStoreBinaryAsFile} returns true.
     * @param path the file path
     */
    public void setBinaryFilePath(File path);

    /**
     * Sets the mapping of MMBase fieldnames (typically reserved words) to database fieldnames.
     * This map is used to map MMBase fieldnames to fieldnames that are acceptable to
     * the database, i.e. Oracle does not accept 'number' as a fieldname, though MMBase
     * uses it as a field name.
     * @param fieldmap Map of field mappings. The map contins a key value pairs where the key is
     *                 the MMbase fieldname, and the value the replacement acceptable to the database.
     */
    public void setFieldNameMap(Map fieldmap);

    /**
     * Returns the current mapping of disallowed fieldsnames (typically reserved words)
     * and their replacement names.
     * Note that this is a copy. Changes made to this map will not affect the databse until
     * set with {@link #setFieldNameMap}.
     * @return a Map of field mappings.
     */
    public Map getFieldNameMap();

    /**
     * Sets the type map.
     * The type map is used to convert MMBase types to database types (needed for creating tables).
     * @param typemap Map of MMBase types and their database type.
     */
    public void setTypeMap(Map typeMap);

    /**
     * Obtains the type map.
     * The type map is used to convert MMBase types to database types (needed for creating tables).
     * Note that this is a copy. Changes made to this map will not affect the database until
     * set with {@link #setTypeMap}.
     * However, the individual map elements are not copies - changing the dTypeInfo objects wil
     * affect the database layer directly.
     * @return a Map of MMBase types and their database type.
     */
    public Map getTypeMap();

    /**
     * Sets the scheme (SQL command) to use for creating a primary key
     * @param scheme the scheme to use
     */
    public void setPrimaryKeyScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a primary key
     */
    public String getPrimaryKeyScheme();

    /**
     * Sets the scheme (SQL command) to use for creating a non-null field
     * @param scheme the scheme to use
     */
    public void setNotNullScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a non-null field
     */
    public String getNotNullScheme();

    /**
     * Sets the scheme (SQL command) to use for creating a key
     * @param scheme the scheme to use
     */
    public void setKeyScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a key
     */
    public String getKeyScheme();

    /**
     * Sets the scheme (SQL command) to use for creating a key
     * @param scheme the scheme to use
     */
    public void setForeignKeyScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a key
     */
    public String getForeignKeyScheme();

    /**
     * Sets the scheme (SQL command) to use for creating a table
     * @param scheme the scheme to use
     */
    public void setCreateScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a table
     */
    public String getCreateScheme();

    /**
     * Sets the scheme (SQL command) to use for creating a table that extends another table
     * @param scheme the scheme to use
     */
    public void setCreateExtendedScheme(String scheme);

    /**
     * Returns the scheme (SQL command) to use for creating a table that extends another table
     */
    public String getCreateExtendedScheme();

    /**
     * Sets the maximum table drop size.
     * @param value the maximum size to set
     */
    public void setMaxDropSize(int value);

    /**
     * Returns the maximum table drop size.
     */
    public int getMaxDropSize();

    /**
     * Returns whether this storage layer supports extended tables.
     * @return boolean true if extended tables are supported
     */
    public boolean supportsExtendedTables();

    /**
     * Maps a MMBase fieldname to a fieldname acceptable to the database
     * @param fieldname the fieldname to map
     */
    public String mapToTableFieldName(String fieldName);

    /**
     * Maps a database fieldname to a fieldname as used by the MMbase system
     * @param fieldname the fieldname to map
     */
    public String mapToMMBaseFieldName(String fieldName);

    /**
     * Returns the JDBC module used by this class to connect to the database.
     * Note that this interface may be up to change, so use with care.
     * @return the JDBC Module.
     */
    public JDBCInterface getJDBC();

    /**
     * Stores a field in a table ResultSet in a MMObjectNode.
     * @param node the node to store the field in
     * @param fieldname the name of the field as it is known to MMBase
     * @param rs the ResultSet containing the table row
     * @param i the index of the field in the ResultSet
     */
    public void loadFieldFromTable(MMObjectNode node, String fieldName, ResultSet rs,int i);

    /**
     * Set prepared statement field i with value of key from node
     * @javadoc
     * @throws SQLException if an error occurred while filling in the fields
     */
    public boolean setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
        throws SQLException;

    /**
     * Registers the change to a node.
     * Clears the change status of a ndoe, then broadcasts changes to the
     * node's parent builder. If the node is a relation, it also updates the relationcache and
     * broadcasts these changes to the relation' s source and destination.
     * @todo should pass Transaction!
     * @param node the node to register
     * @param change the type of change: "n": new, "c": commit, "d": delete
     */
    public void registerChanged(MMObjectNode node, String change);

}
