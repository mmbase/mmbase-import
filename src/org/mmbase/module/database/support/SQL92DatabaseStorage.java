/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.XMLDatabaseReader;
import org.mmbase.util.logging.*;

/**
 * SQL92DatabaseStorage extends AbstractDatabaseStorage to implement the basic functionality for creating and
 * updating databse tables, using SQL.
 * Most statements created here are ANSI SQL 92, with teh exception of statements for creating
 * extended tables (see {@link #createTable()}).
 * It does not take into account db-specific effects of inheritance - as various databases have their own
 * methods, it may be necessary to override the {@link #createTable()}, {@link #insertIntoTable()},
 * {@link #commitToTable()}, and {@link #deleteFromTable()} methods.
 * The basic implementation of these methods assumes an OO-database that does not require the use of
 * specific database routines (i.e. alternate SQL syntax).
 *
 * Furthermore, most sql statements are now contained in their own wrapper methods:
 * {@link #createSQL()}, {@link #insertSQL()}, {@link #updateSQL()}, {@link #deleteSQL()} and various
 * methods for returning SQL SELECT statements.
 * You can override these method to change the sql statements used by the database layer.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: SQL92DatabaseStorage.java,v 1.1 2002-04-08 12:21:32 pierre Exp $
 */
public abstract class SQL92DatabaseStorage extends AbstractDatabaseStorage implements DatabaseStorage {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(SQL92DatabaseStorage.class.getName());
    // map with tables that are known to exist
    private List existingTables=null;
    // has this layer support for rollback?
    private boolean supportsRollback=false;

    /**
     * Constructs the AbstractDatabaseSupport database layer support class
     */
    protected SQL92DatabaseStorage() {
    }

    /**
     * Initializes the database layer.
     * This reads database specific content from the database configuration document.
     * @param mmb the MBase instance that uses this database layer
     * @param document the database configuration document
     */
    public void init(MMBase mmb,XMLDatabaseReader document) {
        super.init(mmb,document);
        loadExistingTables();
        loadSupportInformation();
        prepare();
    }

    /**
     * Load the names of the existing tables for this MMBase instance in memory.
     */
    protected void loadExistingTables() {
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction(false);
            existingTables=trans.getTables(mmb.baseName);
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
        if (existingTables==null) existingTables=new Vector();
    }

    /**
     * Load information on database support (i.e support of rollback).
     */
    protected void loadSupportInformation() {
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction(false);
            supportsRollback=trans.databaseSupportsRollback();
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.error(e.getMessage());
        }
    }

    /**
     * Prepares the database layer.
     * Override this method if you need to make preparations for your database.
     */
    protected void prepare() {
    }

    /**
     * Returns whether this storage layer supports extended tables.
     * @return boolean true if extended tables are supported
     */
    public boolean supportsExtendedTables() {
        return true;
    }

    /**
     * Returns whether rollback on storage level is supported.
     * @return true if transactions are supported
     */
    public boolean supportsRollback() {
        return supportsRollback;
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
        String scheme=super.applyCreateScheme(tableName,fieldDefinitions,parentTableName);
        if (scheme==null) {
            // maybe we should throw an exception instead?
            if (supportsExtendedTables() && (parentTableName!=null)) {
                scheme="CREATE TABLE "+tableName+" ("+fieldDefinitions+") EXTENDS ("+parentTableName+")";
            } else {
                scheme="CREATE TABLE "+tableName+" ("+fieldDefinitions+")";
            }
        }
        return scheme;
    }

    /**
     * Returns the SQL command to use for creating a specified table, optionally
     * extending another supplied table.
     * Overide this method to add a database-specific syntax or optimalization
     * Note that the default implementation uses Ansi SQL and ignores the parentTableName
     * parameter, instead using parentFields to complete the table creation command.
     * @param tableName the name of the table to create
     * @param fields the definitions of the fields
     * @param parentTableName the name of the parent table. this value is null if
     *                  the table to be created has no parent table. The table is assumed to exist.
     * @param parentFields the definitions of the fields of the parent table (for use by relational databases)
     * @return the sql query
     */
    protected String createSQL(String tableName, String fields, String parentTableName, String parentFields) {
        if (!supportsExtendedTables() && (parentFields!=null)) {
            if (fields==null) {
                fields=parentFields;
            } else {
                fields=parentFields+", "+fields;
            }
        }
        return applyCreateScheme(tableName, fields, parentTableName)+";";
    }

    /**
     * Returns the SQL command to use for inserting an object in a table.
     * Overide this method to add a database-specific syntax or optimalization
     * @param tableName the name of the table where to insert
     * @param fieldNames the names of the fields to insert
     * @param fieldValues the values (generally '?' tokens that will be replaced) of the fields to insert
     * @return the sql query
     */
    protected String insertSQL(String tableName,String fieldNames, String fieldValues) {
        return "INSERT INTO "+tableName+" ( "+fieldNames+" ) VALUES ( "+fieldValues+" );";
    }

    /**
     * Returns the SQL command to use for updating an object in a table.
     * Overide this method to add a database-specific syntax or optimalization
     * @param tableName the name of the table where to update
     * @param setfields the set-commands for the table fields, generally of the format 'field1=?, field2=? ...'
     * @param number the number of the object to update
     * @return the sql query
     */
    protected String updateSQL(String tableName,String setFields,int number) {
        return "UPDATE "+tableName+" SET "+setFields+" WHERE "+getNumberString()+"="+number+";";
    }

    /**
     * Returns the SQL command to use for deleting an object in a table.
     * Overide this method to add a database-specific syntax or optimalization
     * @param tableName the name of the table where to delete
     * @param number the number of the object to delete
     * @return the sql query
     */
    protected String deleteSQL(String tableName,int number) {
        return "DELETE FROM "+tableName+" WHERE "+getNumberString()+"="+number+";";
    }

    /**
     * Returns the SQL command to use for selecting data from a table.
     * Overide this method to add a database-specific syntax or optimalization
     * @param tableName the name of the table where to update
     * @param fieldNames commaseparated list of fieldnames to retrieve, can be null (retrieve all fields)
     * @param where constraints, can be null (no constraints)
     * @param orderby optional fields to order by, can be null (no order)
     * @param offset offset from where to select records. Note: if you specify an offset larger than 0, you have to specify max
     * @param max maximum number of records, can be -1 (no max)
     * @return the sql query
     */
    protected String selectSQL(String tableName, String fieldNames, String where, String orderby,
                               int offset, int max) {
        if (fieldNames==null) {
            fieldNames="*";
        }
        String result="SELECT "+fieldNames+" FROM "+tableName;
        if ((where!=null) && !where.equals("")) {
            result+=" WHERE "+where;
        }
        if ((orderby!=null) && !orderby.equals("")) {
            result+=" ORDER BY "+orderby;
        }
        if (offset>0) {
            result+=" LIMIT "+offset+","+max;
        } else if (max>0) {
            result+=" LIMIT "+max;
        }
        result+=";";
        return result;
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
    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        String buildername=builder.getTableName();
        return buildername.equals("object") || buildername.equals("insrel");
    }

    /**
     * Set a prepared statement field i with value of key from the given node.
     * @throws SQLException if an error occurred while filling in the fields
     */
    public boolean setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
        throws SQLException {
        int type = node.getDBType(key);
        if (type==FieldDefs.TYPE_INTEGER) {
            stmt.setInt(i, node.getIntValue(key));
        } else if (type==FieldDefs.TYPE_FLOAT) {
            stmt.setFloat(i, node.getFloatValue(key));
        } else if (type==FieldDefs.TYPE_DOUBLE) {
            stmt.setDouble(i, node.getDoubleValue(key));
        } else if (type==FieldDefs.TYPE_LONG) {
            stmt.setLong(i, node.getLongValue(key));
        } else if (type==FieldDefs.TYPE_STRING || type==FieldDefs.TYPE_XML) {
            String tmp=node.getStringValue(key);
            if (tmp!=null) {
                setDBText(i, stmt,tmp);
            } else {
                setDBText(i, stmt,"");
            }
        } else if (type==FieldDefs.TYPE_BYTE) {
            if (getStoreBinaryAsFile()) {
                String stype=node.getBuilder().getTableName();
                File file = new File(getBinaryFilePath()+stype);
                try {
                    file.mkdirs();
                } catch(Exception e) {
                    log.error("Can't create dir : "+getBinaryFilePath()+stype);
                    return false;
                }
                byte[] value=node.getByteValue(key);
                writeBytesToFile(getBinaryFilePath()+stype+File.separator+node.getNumber()+"."+key,value);
                return false;
            } else {
                setDBByte(i, stmt, node.getByteValue(key));
            }
        } else {
            String tmp=node.getStringValue(key);
            if (tmp!=null) {
                stmt.setString(i, tmp);
            } else {
                stmt.setString(i, "");
            }
        }
        return true;
    }

    /**
     * Stores a field in a table ResultSet in a MMObjectNode.
     * @param node the node to store the field in
     * @param fieldname the name of the field as it is known to MMBase
     * @param rs the ResultSet containing the table row
     * @param i the index of the field in the ResultSet
     */
    public void loadFieldFromTable(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        try {
            switch (node.getDBType(fieldname)) {
                // string-type fields, use mmbase encoding
                case FieldDefs.TYPE_XML:
                case FieldDefs.TYPE_STRING: {
                    String tmp = "";
                    try {
                        tmp = new String(rs.getBytes(i), mmb.getEncoding());
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                    node.setValue(fieldname,tmp);
                    break;
                }
                // Number fields (can be passed as-is: node evaluates it)
                case FieldDefs.TYPE_INTEGER:;
                case FieldDefs.TYPE_LONG:;
                case FieldDefs.TYPE_FLOAT:;
                case FieldDefs.TYPE_DOUBLE: {
                    node.setValue(fieldname,rs.getObject(i));
                    break;
                }
                // binary fields: mark as $shorted, retrieve later
                case FieldDefs.TYPE_BYTE: {
                    node.setValue(fieldname,"$SHORTED");
                    break;
                }
            }
        } catch(SQLException e) {
            log.error("Cannot decode field "+fieldname+" node="+node.getNumber());
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Get text from blob
     * @javadoc
     */
    abstract protected String getText(String tableName,String fieldname,int number);

    /**
     * Get text from blob
     * @javadoc
     */
    public String getText(MMObjectNode node,String fieldname) {
        return getText(node.getBuilder().getFullTableName(),fieldname,node.getNumber());
    }

    /**
     * Get bytes from blob
     * @javadoc
     */
    abstract protected byte[] getBytes(String tableName,String fieldname,int number);

    /**
     * Get bytes from blob
     * @javadoc
     */
    public byte[] getBytes(MMObjectNode node,String fieldname) {
        if (getStoreBinaryAsFile()) {
            return readBytesFromFile(getBinaryFilePath()+node.getBuilder().getTableName()+File.separator+node.getNumber()+"."+fieldname);
        } else {
            return getBytes(node.getBuilder().getFullTableName(),fieldname,node.getNumber());
        }
    }


    /**
     * This method inserts a new object within a transaction, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    public int insert(MMObjectNode node, Transaction trans) throws StorageException {
        // determine parent
        MMObjectBuilder builder=node.getBuilder();
        int number=node.getNumber();
        // did the user supply a number allready?
        // if not, try to obtain one and assign it
        if (number==-1) {
            number=createKey();
            // did it fail ? if so return -1
            if (number == -1) return -1;
            node.setValue("number",number);
        }

        insertIntoTable(builder,node,(DatabaseTransaction)trans);

        // register change
        registerChanged(node,"n");
        return number;
    }

    /**
     * This method inserts a new object in a specified builder table.
     * It performs a simple insert statement (obtained with {@link  #insertSQL()},
     * using the name and fields of the specified builder.
     * Override this method to add required code, such as recursive updates for relational databases.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @param builder the builder to store the node in. This can be a parentbuilder of the node's actual builder
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    protected int insertIntoTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        // Create a String that represents the DB fields to be used in the insert.
        String fieldnames=null;
        String fieldvalues=null;

        // obtain the builder's table fields
        List fields =builder.getFields();
        for (Iterator f=fields.iterator(); f.hasNext();) {
            FieldDefs field=(FieldDefs)f.next();
            if ((field.getDBState()!=FieldDefs.DBSTATE_PERSISTENT) &&
                (field.getDBState()!=FieldDefs.DBSTATE_SYSTEM)) {
                // do not handle this field
                // remove it from the field list so we need not check on it later
                f.remove();
            } else {
                // skip bytevalues that are written to file
                if (getStoreBinaryAsFile() && (field.getDBType()==FieldDefs.TYPE_BYTE)) {
                    continue;
                }
                // store the fieldname and the value parameter
                String fieldname= mapToTableFieldName(field.getDBName());
                if (fieldnames==null) {
                    fieldnames=fieldname;
                    fieldvalues="?";
                } else {
                    fieldnames+=","+fieldname;
                    fieldvalues+=",?";
                }
            }
        }
        // Prepare the statement using the amount of fields found.
        String sqlinsert=insertSQL(builder.getFullTableName(),fieldnames,fieldvalues);

        if (!trans.executeUpdate(sqlinsert,fields,node)) {
            return -1;
        } else {
            return node.getNumber();
        }
    }

    /**
     * Commit this node to the specified builder table within a transaction.
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to commit
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    public boolean commit(MMObjectNode node, Transaction trans) throws StorageException {
        // get node builder
        MMObjectBuilder builder = node.getBuilder();
        //  precommit call, needed to convert or add things before a save
        builder.preCommit(node);

        if (!commitToTable(builder,node,(DatabaseTransaction)trans)) return false;

        registerChanged(node,"c");
        return true;

    }

    /**
     * Commit the changes to this node to the specified builder table.
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to commit
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    protected boolean commitToTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String tableName=builder.getTableName();
        // Create a String that represents the DB fields to be used in the insert.
        String setFields=null;
        // obtain the node's changed fields
        List fieldnames =node.getChanged();
        List fields = new Vector();
        for (Iterator f=fieldnames.iterator(); f.hasNext();) {
            String key=(String)f.next();

            // changing number is not allowed
            if(key.equals("number") || key.equals("otype")) {
                log.fatal("trying to change the '"+key+"' field");
                throw new RuntimeException("trying to change the '"+key+"' field");
            }
            FieldDefs field=builder.getField(key);
            if ((field!=null) &&
                ((field.getDBState()==FieldDefs.DBSTATE_PERSISTENT) ||
                 (field.getDBState()==FieldDefs.DBSTATE_SYSTEM))) {
                // handle this field - store it in fields
                fields.add(field);
                // skip bytevalues that are written to file
                if (getStoreBinaryAsFile() && (field.getDBType()==FieldDefs.TYPE_BYTE)) continue;
                // store the fieldname and the value parameter
                String fieldname= mapToTableFieldName(field.getDBName());
                if (setFields==null) {
                    setFields=fieldname+"=?";
                } else {
                    setFields+=","+fieldname+"=?";
                }
            }
        }
        if (fields.size()>0) {
            String sqlupdate=
                updateSQL(builder.getFullTableName(), setFields,node.getNumber());
            return trans.executeUpdate(sqlupdate,fields,node);
        }
        return true;
    }

    /**
     * Delete a node within a transaction
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    public void delete(MMObjectNode node, Transaction trans) throws StorageException {
        // determine parent
        MMObjectBuilder builder=node.getBuilder();
        int number=node.getNumber();
        if (number>-1) {
            if (node.hasRelations()) {
                log.error("cannot delete node, relations are still attached :"+number);
                return;
            }
            deleteFromTable(builder,node,(DatabaseTransaction)trans);
            // register change once the node's parent builder is finsihed
            registerChanged(node,"d");
        }
    }

    /**
     * Remove a node from the specified builder table.
     * @param builder the builder to remove the node from. This can be a parentbuilder of the node's actual builder
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    public void deleteFromTable(MMObjectBuilder builder, MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqldelete=deleteSQL(builder.getFullTableName(),node.getNumber());
        trans.executeUpdate(sqldelete);
    }

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during selection
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number, Transaction trans) throws StorageException {
        String tableName=builder.getFullTableName();
        String sqlselect= selectSQL(tableName,null,number);
        ResultSet result=((DatabaseTransaction)trans).executeQuery(sqlselect);
        return ((DatabaseTransaction)trans).getNodeResult(builder);
    }

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    public int getNodeType(int number, Transaction trans) throws StorageException {
        String tableName=getFullTableName("object");
        String sqlselect= selectSQL(tableName,"otype",number);
        ResultSet result=((DatabaseTransaction)trans).executeQuery(sqlselect);
        return ((DatabaseTransaction)trans).getIntegerResult();
    }

    /**
     * Returns the fields of a builder in the order in which
     * they should be created.
     * <br>
     * Note: order is not of import with this particular database layer,
     * which refers to fields by name (not index).
     * However, older databases DO rely on a specific order of the fields,
     * specifically when inserting data. Also, some older routines in MMBase that
     * still execute SQL on their own (shudder!) access fields on index.
     * Therefor, this supportclass (and extending classes) should make sure
     * the desired order is maintained.
     * @param builder the buidler whose fields to retrieve and sort
     * @return a list of fields.
     */
    protected List getFieldsOrderCreate(MMObjectBuilder builder) {
        List fields=builder.getFields();
        Collections.sort(fields);
        // Place the "otype" field second place, as this is the convention
        // in older databases, but the builder delivers "otype" as the first field
        // (don't ask me why)
        if (fields.size()>1) {
            if ( ((FieldDefs)fields.get(0)).getDBName().equals("otype")) {
               Object f=fields.get(0);
               fields.set(0,fields.get(1));
               fields.set(1,f);
            }
        }
        return fields;
    }


    /**
     * Returns the parent builder of the specifed builder.
     * If the value is null, the builder either has no parent, or its builder is the
     * "object" table, but there was no builder created for this low-level table.
     * @param builder the buidler to find the parent of
     * @return the parent buidler or null if it cannot be determined
     */
    protected MMObjectBuilder getParentBuilder(MMObjectBuilder builder) {
        MMObjectBuilder parent=builder.getParentBuilder();
        if ((parent==null) && (builder instanceof InsRel) && !builder.getTableName().equals("insrel")) {
            parent=mmb.getInsRel();
        }
        return parent;
    }

    /**
     * Returns the name of the parent table of the specifed builder.
     * If the value is null, the builder has no parent table.
     * @param builder the builder to find the parent of
     * @return the parent table or null if it has no parent
     */
    protected String getParentTableName(MMObjectBuilder builder) {
        if (!builder.getTableName().equals("object")) return null;
        MMObjectBuilder parent=getParentBuilder(builder);
        if (parent!=null) {
            return parent.getTableName();
        } else {
            return "object";
        }
    }

    /**
     * Tests whether the specified field is a member of the parent table of the specifed builder.
     * @param builder the builder to find the parent of
     * @param fieldname the name to test
     * @return true if the field belongs to the parent table
     */
    protected boolean isParentField(MMObjectBuilder builder,String fieldName) {
        if (builder.getTableName().equals("object")) return false;
        MMObjectBuilder parent=getParentBuilder(builder);
        if (parent==null) {
            return fieldName.equals("number") || fieldName.equals("otype") || fieldName.equals("owner");
        } else {
            return parent.getField(fieldName)!=null;
        }
    }

    /**
     * Create a database table for the specified builder.
     * @param builder the builder to create the table for\
     * @param trans the transaction to perform the insert in
     * @return true if the table was succesfully created
     * @throws StorageException if an error occurred during create
     */
    public boolean create(MMObjectBuilder builder, Transaction trans) throws StorageException {
        // use the builder to get the fields and create a
        // valid create SQL string
        List fields=getFieldsOrderCreate(builder);
        String createfields=null;
        String parentfields=null;
        for (Iterator f=fields.iterator(); f.hasNext();) {
            FieldDefs field=(FieldDefs)f.next();
            if ((field.getDBState()==FieldDefs.DBSTATE_PERSISTENT) ||
                (field.getDBState()==FieldDefs.DBSTATE_SYSTEM)) {
                // skip bytefields when values are written to file
                if (getStoreBinaryAsFile() && (field.getDBType()==FieldDefs.TYPE_BYTE)) continue;
                // convert a fielddef to a field SQL createdefinition
                String part=constructFieldDefinition(builder,field);
                if (isParentField(builder,field.getDBName())) {
                    if (parentfields==null) {
                        parentfields=part;
                    } else {
                        parentfields+=", "+part;
                    }
                } else {
                    if (createfields==null) {
                        createfields=part;
                    } else {
                        createfields+=", "+part;
                    }
                }
            }
        }
        String tableName=builder.getFullTableName();
        String sqlcreate= createSQL(tableName,createfields,
                                    getFullTableName(getParentTableName(builder)),parentfields);
        boolean exists=((DatabaseTransaction)trans).executeUpdate(sqlcreate);
        if (exists) existingTables.add(tableName);
        return exists;
    }

    /**
     * Create the object table (the basic table for all objects) within a transaction
     * @param trans the transaction to perform the insert in
     * @return true if the table was succesfully created
     * @throws StorageException if an error occurred during craete
     */
    public boolean createObjectStorage(Transaction trans) throws StorageException {
        String sqlcreate=
            createSQL(getFullTableName("object"),
            constructFieldDefinition("object", "number",FieldDefs.TYPE_INTEGER,-1,KEY_PRIMARY)+", "+
            constructFieldDefinition("object", "otype",FieldDefs.TYPE_INTEGER,-1,KEY_NOTNULL)+", "+
            constructFieldDefinition("object", "owner",FieldDefs.TYPE_STRING,12,KEY_NOTNULL));
        boolean result=((DatabaseTransaction)trans).executeUpdate(sqlcreate);
        if (result) existingTables.add(getFullTableName("object"));
        return result;
    }

    /**
     * Tells if a table for the builder already exists
     * @param builder the builder to check
     * @return true if table exists, false if table doesn't exists
     */
    public boolean created(MMObjectBuilder builder) {
        return created(builder.getFullTableName());
    }

    /**
     * Tells if a table already exists
     * @scope protected
     * @param tableName name of the table to check
     * @return true if table exists, false if table doesn't exists
     */
    public boolean created(String tableName) {
        // check whether the table is already known to exist
        boolean exists=existingTables.contains(tableName);
        // if not, ask explicitly (table could have been created in the meantime by
        // another MMBase instance - paranoia? Maybe, but it is possible)
        if (!exists) {
            DatabaseTransaction trans=null;
            try {
                trans=createDatabaseTransaction(false);
                exists=trans.hasTable(tableName);
                trans.commit();
            } catch (StorageException e) {
                if (trans!=null) trans.rollback();
                log.debug(e.getMessage());
            }
        }
        return exists;
    }

    /**
     * Return number of objects in a builder
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has, or -1 if the builder does not exist.
     */
    public int size(MMObjectBuilder builder) {
        return size(builder.getFullTableName());
    }

    /**
     * Return number of entries consisting in given table.
     * If the table does not exist, the function returns -1.
     * @scope protected
     * @param tableName the table whose records to counted
     * @return the number of items the table has, or -1 if the table does not exist.
     */
    public int size(String tableName) {
        int number=-1;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction(false);
            String sqlselect=selectSQL(tableName,"count(*)");
            trans.executeQuery(sqlselect);
            number=trans.getIntegerResult();
            trans.commit();
        } catch (StorageException e) {
            if (trans!=null) trans.rollback();
            log.debug(e.getMessage());
        }
        return number;
    }

    /**
     * Writes a byte array to a file.
     * @param file name the path of the file
     * @param value the value to write
     * @return true if succesful
     */
    protected boolean writeBytesToFile(String filename,byte[] value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e.toString());
        }
        return true;
    }

    /**
     * Reads a byte array from a file.
     * @param file name the path of the file
     * @return value the value read
     */
    protected byte[] readBytesFromFile(String filename) {
        File bfile = new File(filename);
        int filesize = (int)bfile.length();
        byte[] buffer=new byte[filesize];
        if (filesize>0) {
            try {
                FileInputStream scan = new FileInputStream(bfile);
                int len=scan.read(buffer,0,filesize);
                scan.close();
            } catch(IOException e) {
                log.error(e.toString());
            }
        }
        return buffer;
    }

    /**
     * Drops the table of this builder.
     * @param builder the builder whose table to drop
     * @return true if succesful
     */
    public boolean drop(MMObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a field to the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to add
     * @return true if succesful
     */
    public boolean addField(MMObjectBuilder builder,String fieldname) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a field from the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to delete
     * @return true if succesful
     */
    public boolean removeField(MMObjectBuilder builder,String fieldname) {
        throw new UnsupportedOperationException();
    }

    /**
     * Changes a field to the table of this builder.
     * @param builder the builder whose table to change
     * @param fieldname the name fo the field to change
     * @return true if succesful
     */
    public boolean changeField(MMObjectBuilder builder,String fieldname) {
        throw new UnsupportedOperationException();
    }

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose table to change
     * @return true if succesful
     */
    public boolean updateStorage(MMObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

}

