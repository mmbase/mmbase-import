/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.JDBCInterface;
import org.mmbase.module.database.support.dTypeInfos;
import org.mmbase.module.database.support.dTypeInfo;

import org.mmbase.storage.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.XMLDatabaseReader;
import org.mmbase.util.logging.*;

/**
 * AbstractDatabaseStorage implements part of the DatabaseStorage interface, and
 * supplies utility routines for other support classes.
 * In particulatr, this layer implements the 'stateless' builder update methods,
 * methods for creating Transactions, and methods for setting and retrieving configuration data.
 * Further, a number of utility methods are defined for constructing SQL-statements (or parts thereof)
 * using schemes. Finally, a number of abstract methods relating to teh constrcution of SQL statements
 * are defined, which a extending class should implement.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: AbstractDatabaseStorage.java,v 1.16 2004-01-27 12:04:45 pierre Exp $
 */
public abstract class AbstractDatabaseStorage extends Support2Storage implements DatabaseStorage {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(AbstractDatabaseStorage.class);

    // maps with type mappings (MMBase type to database type)
    private Map typeMap;
    // maps MMBase fieldnames to alternate fieldnames acceptable to the database
    // only contains fieldnames that actually deviate
    private Map disallowed2allowed;
    // maps database fieldnames to MMbase fieldnames
    // the reverse of disallowed2allowed
    private Map allowed2disallowed;
    // the table fieldname for the number field
    private String numberString;
    // if true, blobs are stored as files
    private boolean storeBinaryAsFile = false;
    // filepath in case blobs are stored as files
    private File binaryFilePath = new File("/tmp/data/"); // this default is never used.
    // scheme for creating primary keys
    private String primaryKeyScheme;
    // scheme for creating not null fields
    private String notNullScheme;
    // scheme for creating keys
    private String keyScheme;
    // scheme for creating foreign keys
    private String foreignKeyScheme;
    // scheme for creating tables
    private String createScheme;
    // scheme for creating tables that extend other tables
    private String createExtendedScheme;
    // default dropsize
    private int maxDropSize = 0;

    /**
     * The MMBase instance that uses this database
     */
    protected MMBase mmb;

    /**
     * Constructs the AbstractDatabaseSupport database layer support class
     */
    protected AbstractDatabaseStorage() {
        super();
    }

    /**
     * Initializes the database layer.
     * This reads database specific content from the database configuration document.
     * If needed, the code creates a 'numbertable' for mmbase to track number generation.
     * @param mmb the MBase instance that uses this database layer
     * @param document the database configuration document
     */
    public void init(MMBase mmb, XMLDatabaseReader reader) {
        this.mmb = mmb;
        deployDatabaseReader(reader);
    }

    /**
     * This reads database specific content from the database configuration document.
     * @param document the database configuration document
     */
    protected void deployDatabaseReader(XMLDatabaseReader reader) {
        String path = reader.getBlobDataDir();
        setStoreBinaryAsFile(path != null && ! path.equals(""));
        if (getStoreBinaryAsFile()) {

            File dir = new File(path); // relative paths must be possible.
            if (! dir.isAbsolute()) {
                //"file:///..."
                String parent = new File(reader.getFileName().substring(7)).getParent();
                if (log.isDebugEnabled()) {
                    log.service("Blobdatadir not specified absolutely, determining it relative to " + parent);
                }

                dir = new File(parent, path);
            }

            setBinaryFilePath(dir);
            try {
                log.service("Byte array blobs will be stored in the directory '" + dir.getCanonicalPath() + "'");
            } catch (IOException ioe) {
                log.error(ioe.toString());
            }
        }
        setTypeMap(reader.getTypeMapping());
        setFieldNameMap(reader.getDisallowedFields());
        setPrimaryKeyScheme(reader.getPrimaryKeyScheme());
        setNotNullScheme(reader.getNotNullScheme());
        setKeyScheme(reader.getKeyScheme());
        setForeignKeyScheme(reader.getForeignKeyScheme());
        setCreateScheme(reader.getCreateScheme());
        setCreateExtendedScheme(reader.getCreateExtendedScheme());
        setMaxDropSize(reader.getMaxDropSize());

        // Instantiate and initialize sql handler.
        super.init(getFieldNameMap(), reader);
    }

    /**
     * Returns whether binary objects are stored as files (rather than in the database)
     * @return true if binary objects are stored as files
     */
    public boolean getStoreBinaryAsFile() {
        return storeBinaryAsFile;
    }

    /**
     * Sets whether binary objects are stored as files (rather than in the database)
     * @param value if true, binary objects will be stored as files
     */
    public void setStoreBinaryAsFile(boolean value) {
        storeBinaryAsFile = value;
    }

    /**
     * Returns the filepath where binary objects are stored.
     * Only applies if {@link #getStoreBinaryAsFile} returns true.
     * @return the file path
     */
    public File getBinaryFilePath() {
        return binaryFilePath;
    }

    /**
     * Sets the filepath where binary objects are stored.
     * Only applies if {@link #getStoreBinaryAsFile} returns true.
     * @param path the file path
     */
    public void setBinaryFilePath(File path) {
        binaryFilePath = path;
    }

    /**
     * Sets the mapping of MMBase fieldnames (typically reserved words) to database fieldnames.
     * This map is used to map MMBase fieldnames to fieldnames that are acceptable to
     * the database, i.e. Oracle does not accept 'number' as a fieldname, though MMBase
     * uses it as a field name.
     * @param fieldmap Map of field mappings. The map contins a key value pairs where the key is
     *                 the MMbase fieldname, and the value the replacement acceptable to the database.
     */
    public void setFieldNameMap(Map fieldmap) {
        disallowed2allowed=fieldmap;
        allowed2disallowed=new HashMap();
        for (Iterator i=disallowed2allowed.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry=(Map.Entry)i.next();
            allowed2disallowed.put(entry.getValue(),entry.getKey());
        }
        // map the default types
        numberString=mapToTableFieldName("number");
    }

    /**
     * Returns the current mapping of disallowed fieldsnames (typically reserved words)
     * and their replacement names.
     * Note that this is a copy. Changes made to this map will not affect the databse until
     * set with {@link #setFieldNameMap}.
     * @return a Map of field mappings.
     */
    public Map getFieldNameMap() {
        return new HashMap(disallowed2allowed);
    }

    /**
     * Sets the type map.
     * The type map is used to convert MMBase types to database types (needed for creating tables).
     * @param typemap Map of MMBase types and their database type.
     */
    public void setTypeMap(Map typeMap) {
        this.typeMap=typeMap;
    }

    /**
     * Obtains the type map.
     * The type map is used to convert MMBase types to database types (needed for creating tables).
     * Note that this is a copy. Changes made to this map will not affect the database until
     * set with {@link #setTypeMap}.
     * However, the individual map elements are not copies - changing the dTypeInfo objects wil
     * affect the database layer directly.
     * @return a Map of MMBase types and their database type.
     */
    public Map getTypeMap() {
        return new HashMap(typeMap);
    }

    /**
     * Sets the scheme (SQL command) to use for creating a primary key.
     * An acceptable scheme is:
     * <code>
     * {0} {1} NOT NULL, PRIMARY KEY ({0})
     * </code>
     * where {0} and [1} are replaced with the field name and type definition.
     * @param scheme the scheme to use
     */
    public void setPrimaryKeyScheme(String scheme) {
        primaryKeyScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a primary key
     */
    public String getPrimaryKeyScheme() {
        return primaryKeyScheme;
    }

    /**
     * Sets the scheme (SQL command) to use for creating a non-null field
     * An acceptable scheme is:
     * <code>{0} {1} NOT NULL</code>
     * where {0} and [1} are replaced with the field name and type definition.
     * @param scheme the scheme to use
     */
    public void setNotNullScheme(String scheme) {
        notNullScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a non-null field
     */
    public String getNotNullScheme() {
        return notNullScheme;
    }

    /**
     * Sets the scheme (SQL command) to use for creating a key
     * An acceptable scheme is:
     * <code>{0} {1} NOT NULL, UNIQUE ({0})</code>
     * where {0} and [1} are replaced with the field name and type definition.
     * @param scheme the scheme to use
     */
    public void setKeyScheme(String scheme) {
        keyScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a key
     */
    public String getKeyScheme() {
        return keyScheme;
    }

    /**
     * Sets the scheme (SQL command) to use for creating a foreign key
     * An acceptable scheme is:
     * <code>{0} {1} NOT NULL, FOREIGN KEY {0} ({0}) REFERENCES {2} (number)</code>
     * where {0} and [1} are replaced with the field name and type definition,
     * and {2} is the table referenced.
     * @param scheme the scheme to use
     */
    public void setForeignKeyScheme(String scheme) {
        foreignKeyScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a foreign key
     */
    public String getForeignKeyScheme() {
        return foreignKeyScheme;
    }

    /**
     * Sets the scheme (SQL command) to use for creating a table
     * An acceptable scheme is:
     * <code>CREATE TABLE {0} ({1})</code>
     * where {0} is replaced with the table name and [1} with the field definitions.
     * @param scheme the scheme to use
     */
    public void setCreateScheme(String scheme) {
         createScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a table
     */
    public String getCreateScheme() {
        return createScheme;
    }

    /**
     * Sets the scheme (SQL command) to use for creating a table that extends another table
     * An acceptable scheme is:
     * <code>CREATE TABLE {0} ({1}) EXTENDS ({2})</code>
     * where {0} is replaced with the table name, [1} with the field definitions,
     * and {2} with teh parent table name
     * @param scheme the scheme to use
     */
    public void setCreateExtendedScheme(String scheme) {
        createExtendedScheme=scheme;
    }

    /**
     * Returns the scheme (SQL command) to use for creating a table that extends another table
     */
    public String getCreateExtendedScheme() {
        return createExtendedScheme;
    }

    /**
     * Sets the maximum table drop size.
     * @param value the maximum size to set
     */
    public void setMaxDropSize(int value) {
        maxDropSize=value;
    }

    /**
     * Returns the maximum table drop size.
     */
    public int getMaxDropSize() {
        return maxDropSize;
    }

    /**
     * Returns whether this storage layer supports extended tables.
     * @return boolean true if extended tables are supported
     */
    abstract public boolean supportsExtendedTables();

    /**
     * Returns the fully expanded tablename (includes the MMbase basename prefix)
     * @param tableName the table name to expand
     * @return the expanded tablename
     */
    protected String getFullTableName(String tableName) {
        return getTableName(mmb.baseName + "_" + tableName);
    }
    /**
     * @since MMBase-1.7
     */
    protected final String getFullTableName(MMObjectBuilder buil) {
        return getTableName(buil.getFullTableName());
    }

    /**
     * Maps the full table name to a table name acceptable to the database.
     *
     * @since MMBase-1.7
     */
    protected String getTableName(String tableName) {
        return tableName;
    }

    /**
     * Applies a given scheme.
     * @param scheme the scheme to apply
     * @param a1 the first parameter to substitute in the scheme
     * @param a2 the second parameter to substitute in the scheme
     * @param a3 the third parameter to substitute in the scheme
     * @return the applied scheme
     */
    protected String applyScheme(String scheme, String a1, String a2, String a3) {
        if ((scheme==null) || scheme.equals("")) {
            return null;
        }
        String[] args = new String[3];
        args[0] = a1;
        args[1] = a2;
        args[2] = a3;
        return java.text.MessageFormat.format(scheme, args);
    }

    /**
     * Applies the primary key scheme (if available),
     * and returns the result (a field type defintiton string)
     * @param fieldName the database fieldname
     * @param fieldType the database field type defintition
     * @return the applied scheme
     */
    protected String applyPrimaryKeyScheme(String fieldName, String fieldType) {
        String scheme=applyScheme(getPrimaryKeyScheme(),fieldName, fieldType, fieldName);
        if (scheme==null) {
            return applyNotNullScheme(fieldName,fieldType);
        } else {
            return scheme;
        }
    }

    /**
     * Applies the not null scheme (if available),
     * and returns the result (a field type defintiton string)
     * @param fieldName the database fieldname
     * @param fieldType the database field type defintition
     * @return the applied scheme
     */
    protected String applyNotNullScheme(String fieldName, String fieldType) {
        String scheme=applyScheme(getNotNullScheme(),fieldName, fieldType, "");
        if (scheme==null) {
            return fieldName+" "+fieldType;
        } else {
            return scheme;
        }
    }

    /**
     * Applies the key scheme (if available),
     * and returns the result (a field type defintiton string)
     * @param fieldName the database fieldname
     * @param fieldType the database field type defintition
     * @param indexName the name for the index
     * @return the applied scheme
     */
    protected String applyKeyScheme(String fieldName, String fieldType, String indexName) {
        String scheme=applyScheme(getKeyScheme(),fieldName, fieldType, indexName);
        if (scheme==null) {
            return applyNotNullScheme(fieldName,fieldType);
        } else {
            return scheme;
        }
    }

    /**
     * Applies the foreign key scheme (if available),
     * and returns the result (a field type defintiton string)
     * @param fieldName the database fieldname
     * @param fieldType the database field type defintition
     * @param reference the name of the table refrenced
     * @return the applied scheme
     */
    protected String applyForeignKeyScheme(String fieldName, String fieldType, String reference) {
        String scheme=applyScheme(getForeignKeyScheme(),fieldName, fieldType, reference);
        if (scheme==null) {
            return applyNotNullScheme(fieldName,fieldType);
        } else {
            return scheme;
        }
    }

    /**
     * Applies the create scheme (if available),
     * and returns the result (a field type defintiton string)
     * @param tableName the table to create
     * @param fieldDefinitions comma separated list of field definitions
     * @param parentTableName the parent table (only used in OO databases)
     * @return the applied scheme
     */
    protected String applyCreateScheme(String tableName, String fieldDefinitions, String parentTableName) {
        String scheme=null;
        if (supportsExtendedTables() && (parentTableName!=null)) {
            scheme=applyScheme(getCreateExtendedScheme(),tableName,fieldDefinitions, parentTableName);
        } else {
            scheme=applyScheme(getCreateScheme(),tableName,fieldDefinitions, "");
        }
        return scheme;
    }

    /**
     * Returns the SQL command to use for creating a specified table
     * @param tableName the _full_ name of the table to create
     * @param fields the definitions of the fields
     * @return the sql query
     */
    protected String createSQL(String tableName, String fields) {
        return createSQL(tableName, fields,null,null);
    }

    /**
     * Returns the SQL command to use for creating a specified table, optionally
     * extending another supplied table.
     * Implement this method to add a database-specific syntax or optimalization.
     * @param tableName the _full_  name of the table to create
     * @param fields the definitions of the fields
     * @param parentTableName the name of the parent table. this value is null if
     *                  the table to be created has no parent table. The table is assumed to exist.
     * @param parentFields the definitions of the fields of the parent table (for use by relational databases)
     * @return the sql query
     */
    abstract protected String createSQL(String tableName, String fields, String parentTableName, String parentFields);

    /**
     * Returns the SQL command to use for inserting an object in a table.
     * Implement this method to add a database-specific syntax or optimalization.
     * @param tableName the _full_ name of the table where to insert
     * @param fieldNames the names of the fields to insert
     * @param fieldValues the values (generally '?' tokens that will be replaced) of the fields to insert
     * @return the sql query
     */
    abstract protected String insertSQL(String tableName,String fieldNames, String fieldValues);

    /**
     * Returns the SQL command to use for updating an object in a table.
     * Implement this method to add a database-specific syntax or optimalization.
     * @param tableName the name of the table where to update
     * @param setfields the set-commands for the table fields, generally of the format 'field1=?, field2=? ...'
     * @param number the number of the object to update
     * @return the sql query
     */
    abstract protected String updateSQL(String tableName,String setFields,int number);

    /**
     * Returns the SQL command to use for deleting an object in a table.
     * Implement this method to add a database-specific syntax or optimalization.
     * @param tableName the name of the table where to delete
     * @param number the number of the object to delete
     * @return the sql query
     */
    abstract protected String deleteSQL(String tableName,int number);

    /**
     * Returns the SQL command to use for selecting data from one object.
     * @param tableName the name of the table where to update
     * @param fieldNames commaseparated list of fieldnames to retrieve, can be null (retrieve all fields)
     * @param number the number of the object to update
     * @return the sql query
     */
    protected String selectSQL(String tableName, String fieldNames, int number) {
        return selectSQL(tableName, fieldNames,getNumberString()+"="+number,null,-1,-1);
    }

    /**
     * Returns the SQL command to use for selecting data from a table.
     * @param tableName the name of the table where to update
     * @param fieldNames commaseparated list of fieldnames to retrieve, can be null (retrieve all fields)
     * @return the sql query
     */
    protected String selectSQL(String tableName, String fieldNames) {
        return selectSQL(tableName, fieldNames, null,null,-1,-1);
    }

    /**
     * Returns the SQL command to use for selecting data from a table.
     * @param tableName the name of the table where to update
     * @param fieldNames commaseparated list of fieldnames to retrieve, can be null (retrieve all fields)
     * @param where constraints, can be null (no constraints)
     * @param orderby optional fields to order by, can be null (no order)
     * @return the sql query
     */
    protected String selectSQL(String tableName, String fieldNames, String where, String orderby) {
        return selectSQL(tableName, fieldNames, where, orderby,-1,-1);
    }

    /**
     * Returns the SQL command to use for selecting data from a table.
     * Implement this method to add a database-specific syntax or optimalization.
     * @param tableName the name of the table where to update
     * @param fieldNames commaseparated list of fieldnames to retrieve, can be null (retrieve all fields)
     * @param where constraints, can be null (no constraints)
     * @param orderby optional fields to order by, can be null (no order)
     * @param offset offset from where to select records. Note: if you specify an offset larger than 0, you have to specify max
     * @param max maximum number of records, can be -1 (no max)
     * @return the sql query
     */
    abstract protected String selectSQL(String tableName, String fieldNames, String where, String orderby,
                               int offset, int max);

    /**
     * Constructs a creation definition segment for one field, to use in a CREATE TABLE
     * sql comand
     * @param builder the fields builder
     * @param field the FieldDefs object to make a create definition for
     * @return the create definition
     */
    protected String constructFieldDefinition(MMObjectBuilder builder, FieldDefs field) {

        // determine mmbase type
        int type = field.getDBType();
        // determine field name
        String name = field.getDBName();
        // determine size
        int size = field.getDBSize();
        // determine key type
        int keyType= KEY_NONE;
        if (name.equals("number")) {
            keyType = KEY_PRIMARY;
        } else if (field.isKey()) {
            keyType = KEY_SECONDARY;
        } else if (type==FieldDefs.TYPE_NODE) {
            keyType = KEY_FOREIGN;
        } else if (field.getDBNotNull()) {
            keyType=KEY_NOTNULL;
        }
        return constructFieldDefinition(builder.getTableName(), name, type, size, keyType);
    }

    /**
     * Map a MMBase Type to a database specific type.
     * If the type cannot be determined the result is null
     * @param type the MMBase type
     * @param size the desired size (or -1 if unspecifed)
     * @return the database type as a string, or null if it cannot be determined
     */
    protected String matchType(int type, int size) {
        String result=null;
        if (typeMap!=null) {
            dTypeInfos typs=(dTypeInfos)typeMap.get(new Integer(type));
            if (typs!=null) {
                String lastresult=null;
                for (Iterator i=typs.maps.iterator(); i.hasNext(); ) {
                    dTypeInfo typ = (dTypeInfo)i.next();
                    lastresult=typ.dbType;
                    // needs smart mapping code
                    if ((size==-1) ||
                        (((typ.minSize==-1) || (size>=typ.minSize)) &&
                         ((typ.maxSize==-1) || (size<=typ.maxSize)))) {
                        result=lastresult;
                    }
                }
                if (result==null) {
                    result=lastresult;
                }
                int pos=result.indexOf("size");
                if (pos!=-1) {
                    result=result.substring(0,pos)+size+result.substring(pos+4);
                }
            }
        }
        return result;
    }

    /**
     * Constructs a creation definition segment for one field, to use in a CREATE TABLE
     * sql comand
     * @param tablename name of the table this field belongs to
     * @param fieldname name of the field in MMBase
     * @param type MMBase type of the field
     * @param size size of the field (or -1 if n.a.)
     * @param keyType one of KEY_NONE, KEY_PRIMARY, KEY_SECONDARY, KEY_FOREIGN, KEY_NOTNULL
     * @return the create definition
     */
    protected String constructFieldDefinition(String tablename, String fieldname, int type, int size, int keyType) {
        String name = mapToTableFieldName(fieldname);
        String result = matchType(type,size);
        // cannot determine database type. log the error, but continue -
        // but note that the sql will likely fail!
        if (result == null) {
            log.error("Cannot determine database type for : " + FieldDefs.getDBTypeDescription(type)+"(" + size + ")");
        }
        if (keyType == KEY_PRIMARY) {
            result = applyPrimaryKeyScheme(name,result);
        } else if (keyType==KEY_SECONDARY) {
            result = applyKeyScheme(name,result,name);
        } else if (keyType==KEY_FOREIGN) {
            result=applyForeignKeyScheme(name, result, getFullTableName("object"));
        } else if (keyType==KEY_NOTNULL) {
            result=applyNotNullScheme(name,result);
        } else {
            result = name + " " + result;
        }
        return result;
    }

    /**
     * Maps a MMBase fieldname to a fieldname acceptable to the database
     * @param fieldname the fieldname to map
     */
    public String mapToTableFieldName(String fieldname) {
        String name=(String)disallowed2allowed.get(fieldname);
        if (name!=null) {
            return name;
        } else {
            return fieldname;
        }
    }

    /**
     * Maps a database fieldname to a fieldname as used by the MMbase system
     * @param fieldname the fieldname to map
     */
    public String mapToMMBaseFieldName(String fieldname) {
        String name=(String)allowed2disallowed.get(fieldname);
        if (name!=null) {
            return name;
        } else {
            return fieldname;
        }
    }

    /**
     * Maps the 'number' fieldname to a fieldname acceptable to the database.
     */
    public String getNumberString() {
        return numberString;
    }

    /**
     * Returns whether this database support layer allows for builder to be a parent builder
     * (that is, other builders can 'extend' this builder and its database tables).
     * The default behavior is to disallow any object other than "object" or "insrel".
     * Database layers that support other builders should override this mnethod.
     *
     * @since MMBase-1.6
     * @param builder the builder to test
     * @return true if the builder can be extended
     */
    abstract public boolean isAllowedParentBuilder(MMObjectBuilder builder);

    /**
     * Registers a builder as a parent builder (that is, other builders can 'extend' this
     * builder and its database tables).
     * At the least, this code should check whether the builder is allowed as a parent builder,
     * and throw an exception if this is not possible.
     * This method can be overridden to allow for optimization of code regarding such builders.
     *
     * @since MMBase-1.6
     * @param parent the parent builder to register
     * @param child the builder to register as the parent's child
     * @throws UnsupportedDatabaseOperationException when the database layer does not allow extension of this builder
     */
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child)
        throws UnsupportedDatabaseOperationException {
        if (!isAllowedParentBuilder(parent)) {
            throw new UnsupportedDatabaseOperationException("Cannot extend the builder with name "+parent.getTableName());
        }
    }


    //javadoc inherited from DatabaseStorage
    abstract public void loadFieldFromTable(MMObjectNode node,String fieldname, ResultSet rs,int i);

    /**
     * Get text from blob
     * @javadoc
     */
    abstract public String getText(MMObjectNode node,String fieldname);

    /**
     * Get bytes from blob
     * @javadoc
     */
    abstract public byte[] getBytes(MMObjectNode node,String fieldname);


    /**
     * creates an sql statements and calls {@link #getBytes(MMObjectNode,String)}
     * @javadoc
     */
    public final byte[] getBytes(String tableName, String fieldName, int number) {
        byte[] result = null;
        String sqlselect=selectSQL(tableName, fieldName, number);
        DatabaseTransaction trans=null;
        try {
            trans = createDatabaseTransaction();
            ResultSet rs = trans.executeQuery(sqlselect);
            if ((rs!=null) && rs.next()) {
                result = getDBByte(rs,1);
                if (log.isDebugEnabled()) {
                    log.debug("got " + result.length + " bytes for field  " + fieldName);
                }
            }
            trans.commit();
        } catch (Exception e) {
            log.error(e.toString());
            if (trans != null) trans.rollback();
        }
        return result;
    }

    /**
     * Set a prepared statement field i with value of key from the given node.
     * @throws SQLException if an error occurred while filling in the fields
     * @return true if actual set a value. false if somewhy this did not happen (stored blob to disk e.g.)
     */
    abstract public boolean setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
        throws SQLException;

    /**
     * Returns the JDBC module used by this class to connect to the database.
     * @return the JDBC Module.
     */
    public JDBCInterface getJDBC() {
        return mmb.getJDBC();
    }

    /**
     * Returns whether rollback on storage level is supported.
     * @return true if transactions are supported
     */
    abstract public boolean supportsRollback();

    /**
     * Returns a newly created transaction object.
     * Override this method if you want to use your own transaction control.
     * @return the new transaction
     * @throws StorageException if an error occurred when creating the transaction
     */
    public Transaction createTransaction() throws StorageException {
        return createDatabaseTransaction(true);
    }

    /**
     * Returns a newly created database transaction object.
     * Override this method if you want to use your own transaction control.
     * @return the new database transaction
     * @throws StorageException if an error occurred when creating the transaction
     */
    public DatabaseTransaction createDatabaseTransaction() throws StorageException {
        return createDatabaseTransaction(true);
    }

    /**
     * Returns a newly created database transaction object.
     * Override this method if you want to use your own transaction control.
     * You can explicitly turn off rollback facilities (which might slow down database access)
     * for internal routines by specifying useRollbacka s false.
     * @param useRollback if true, the transaction should use rollback facilities if the database supports them
     * @return the new database transaction
     * @throws StorageException if an error occurred when creating the transaction
     */
    public DatabaseTransaction createDatabaseTransaction(boolean useRollback) throws StorageException {
        return new DatabaseTransaction(this, useRollback);
    }

    /**
     * Registers the change to a node.
     * Clears the change status of a node, then broadcasts changes to the
     * node's parent builder. If the node is a relation, it also updates the relationcache and
     * broadcasts these changes to the relation' s source and destination.
     * @todo should pass Transaction!
     * @param node the node to register
     * @param change the type of change: "n": new, "c": commit, "d": delete
     */
    public void registerChanged(MMObjectNode node, String change) {
        node.clearChanged();
        MMObjectBuilder builder = node.getBuilder();
        if (builder.broadcastChanges) {
            mmb.mmc.changedNode(node.getNumber(),builder.getTableName(),change);
            if (builder instanceof InsRel) {
                // figure out tables to send the changed relations
                MMObjectNode n1 = node.getNodeValue("snumber");
                MMObjectNode n2 = node.getNodeValue("dnumber");
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmb.mmc.changedNode(n1.getNumber(), n1.getBuilder().getTableName(), "r");
                mmb.mmc.changedNode(n2.getNumber(), n2.getBuilder().getTableName(), "r");
            }
        }
    }


    /**
     * Get a String form the resultset
     */
    public String getDBText(ResultSet rs, int idx) {
        try {
            String result = rs.getString(idx);
            if (log.isDebugEnabled()) {
                log.debug("getdbtext on field " + idx + " resulted: '" + result + "'");
            }
            return rs.getString(idx);
        } catch (Exception e) {
            log.error(e.toString());
            return "";
        }
    }

    /**
     * Set text array in database
     * @javadoc
     */
    public void setDBText(int i, PreparedStatement stmt, String body) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("setdbtext on " + i + " with '" + (body == null ? "NULL" : (body.length() <= 10 ? body :  body.substring(0, 10))) + "'");
            }
            stmt.setString(i, body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Gets a byte array from a resultset
     *
     */
    // public for Support2Storage
    public byte[] getDBByte(ResultSet rs, int idx) {
        byte[] bytes = null;
        try {
            Blob b = rs.getBlob(idx);
            return b.getBytes(0, (int) b.length());
        } catch (Exception e) {
            log.error("Byte  exception " + e);
            log.error(Logging.stackTrace(e));
        }
        return bytes;
    }


    /**
     * Drivers which do not understand getBlob can use this.
     * @since MMBase-1.7
     */
    protected final byte[] getDBByteBinaryStream(ResultSet rs,int idx) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            InputStream inp = rs.getBinaryStream(idx);
            while (true) {
                int c = inp.read();
                if (c == -1) break;
                bytes.write(c);
            }
            inp.close(); // this also closes the underlying stream
        } catch (SQLException e) {
            log.error("Byte  exception " + e);
            log.error(Logging.stackTrace(e));
        } catch (IOException io) {
            log.error("Byte  io-exception " + io);
        }
        return bytes.toByteArray();
    }



    /**
     * Set byte array in database
     * @javadoc
     */
    // public for Support2Storage
    public void setDBByte(int i, PreparedStatement stmt, byte[] bytes) {
        try {
            InputStream stream = new ByteArrayInputStream(bytes);
            stmt.setBinaryStream(i, stream, bytes.length);
            stream.close();
        } catch (Exception e) {
            log.error("Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     * Gives an unique number for a node to be inserted.
     * This method will work with multiple mmbases
     * @return unique number
     */
    // public because this implements Support2Storage
    public final int createKey() {
        int number =-1;
        DatabaseTransaction trans = null;
        try {
            trans = createDatabaseTransaction();
            number = createKey(trans);
            trans.commit();
        } catch (StorageException e) {
            log.error(e.toString());
            if (trans != null) trans.rollback();
        }
        return number;
    }

    /**
     * Gives an unique number for a node to be inserted.
     * This method should work with multiple mmbases
     * @param trans the transaction to use for obtaining the key
     * @return unique number
     * @throws StorageException if an error occurred while obtaining the key
     */
    // from Storage
    abstract public int createKey(Transaction trans) throws StorageException;

    /**
     * This method inserts a new object, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     */
    public int insert(MMObjectNode node) {
        int result = -1;
        DatabaseTransaction trans = null;
        try {
            trans = createDatabaseTransaction();
            result = insert(node, trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans != null) trans.rollback();
            log.error("Trouble inserting node: " + node + "\n" + e.getMessage());
            log.error(Logging.stackTrace(e));
        }
        return result;
    }

    /**
     * This method inserts a new object within a transaction, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    abstract public int insert(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Commit this node to the specified builder table.
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to commit
     * @return true of succesful, false otherwise
     */
    public boolean commit(MMObjectNode node){
        boolean result=false;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            result=commit(node,trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Commit this node to the specified builder table within a transaction.
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to commit
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    abstract public boolean commit(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Delete a node
     * @param node The node to delete
     * @return true of succesful, false otherwise
     */
    public boolean delete(MMObjectNode node) {
        boolean result=false;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            result=delete(node,trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Delete a node within a transaction
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    abstract public boolean delete(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number) {
        MMObjectNode result=null;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction(false);
            result=getNode(builder,number,trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during selection
     */
    abstract public MMObjectNode getNode(MMObjectBuilder builder, int number, Transaction trans) throws StorageException;

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     */
    public int getNodeType(int number) {
        int result=-1;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction(false);
            result=getNodeType(number,trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    abstract public int getNodeType(int number, Transaction trans) throws StorageException;

    /**
     * Create a database table for the specified builder.
     * @param builder the builder to create the table for\
     * @return true if the table was succesfully created
     */
    public boolean create(MMObjectBuilder builder) {
        boolean result=false;
        DatabaseTransaction trans=null;
        try {
            trans = createDatabaseTransaction();
            result = create(builder, trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans != null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Create a database table for the specified builder.
     * @param builder the builder to create the table for\
     * @param trans the transaction to perform the insert in
     * @return true if the table was succesfully created
     * @throws StorageException if an error occurred during create
     */
    abstract public boolean create(MMObjectBuilder builder, Transaction trans) throws StorageException;

    /**
     * Create the object table (the basic table for all objects).
     * @return true if the table was succesfully created
     */
    public boolean createObjectStorage() {
        boolean result=false;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            result=createObjectStorage(trans);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * Create the object table (the basic table for all objects) within a transaction
     * @param trans the transaction to perform the insert in
     * @return true if the table was succesfully created
     * @throws StorageException if an error occurred during craete
     */
    abstract public boolean createObjectStorage(Transaction trans) throws StorageException;

    /**
     * Tells if a table for the builder already exists
     * @param builder the builder to check
     * @return true if table exists, false if table doesn't exists
     */
    abstract public boolean created(MMObjectBuilder builder);

    /**
     * Return number of objects in a builder
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has, or -1 if the builder does not exist.
     */
    abstract public int size(MMObjectBuilder builder);

    /**
     * Drops the table of this builder.
     * @param builder the builder whose table to drop
     * @return true if succesful
     */
    abstract public boolean drop(MMObjectBuilder builder);

    /**
     * Adds a field to the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to add
     * @return true if succesful
     */
    abstract public boolean addField(MMObjectBuilder builder,String fieldname);

    /**
     * Deletes a field from the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to delete
     * @return true if succesful
     */
    abstract public boolean removeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes a field to the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to change
     * @return true if succesful
     */
    abstract public boolean changeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose table to change
     * @return true if succesful
     */
    abstract public boolean updateStorage(MMObjectBuilder builder);
}
