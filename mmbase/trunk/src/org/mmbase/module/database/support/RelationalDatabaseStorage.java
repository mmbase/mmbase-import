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
 * RelationalDatabaseStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * Ansi SQL 92 relational databases.
 * This class extends AbstractDatabaseStorage to include methods for retrieving data
 * from a relational database systems.
 * It overrides the various update methods to allow for recursive updates on parent tables,
 * the methods for storing and retrieving huge texts and bytefields, and teh methods for determining
 * database key (for the object 'number' field).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: RelationalDatabaseStorage.java,v 1.1 2002-04-08 12:21:32 pierre Exp $
 */
public class RelationalDatabaseStorage extends SQL92DatabaseStorage implements DatabaseStorage, MMJdbc2NodeInterface {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(RelationalDatabaseStorage.class.getName());

    /**
     * Constructs the Ansi SQL database layer support class
     */
    public RelationalDatabaseStorage() {
    }

    /**
     * Prepares the database layer.
     * This code creates a 'numbertable' for MMBase to track number generation
     * by the {@link #createKey()} method.
     * Override this method if you use a different way of generating keys,
     * or if you need to make other preparations for your database.
     */
    protected void prepare() {
        checkNumberTable();
    }

    /**
     * Returns whether this storage layer supports extended tables.
     * @return boolean true if extended tables are supported
     */
    public boolean supportsExtendedTables() {
        return false;
    }
    /**
     * Get text from blob
     * @javadoc
     */
    public String getText(String tableName,String fieldname,int number) {
        String result=null;
        String sqlselect=selectSQL(tableName,fieldname,number);
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            ResultSet rs=trans.executeQuery(sqlselect);
            if ((rs!=null) && rs.next()) {
                result=getDBText(rs,1);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans!=null) trans.rollback();
        }
        return result;
    }


    /**
     * Get byte of a database blob
     * @javadoc
     */
    public byte[] getBytes(String tableName,String fieldname,int number) {
        byte[] result=null;
        String sqlselect=selectSQL(tableName,fieldname,number);
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            ResultSet rs=trans.executeQuery(sqlselect);
            if ((rs!=null) && rs.next()) {
                result=getDBByte(rs,1);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans!=null) trans.rollback();
        }
        return result;
    }

    /**
     * Get byte of a database blob
     * @javadoc
     */
    public byte[] getDBByte(ResultSet rs,int idx) {
        byte[] bytes=null;
        try {
            InputStream inp=rs.getBinaryStream(idx);
            int siz=inp.available(); // DIRTY
            DataInputStream input=new DataInputStream(inp);
            bytes=new byte[siz];
            input.readFully(bytes);
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql byte  exception "+e);
            log.error(Logging.stackTrace(e));
        }
        return bytes;
    }

    /**
     * Get text of a database blob
     * @javadoc
     */
    public String getDBText(ResultSet rs,int idx) {
        String str=null;
        try {
            InputStream inp = rs.getBinaryStream(idx);
            if ((inp==null) || rs.wasNull()) {
                return("");
            }
            int siz=inp.available(); // DIRTY
            if (siz<=0) return("");
            DataInputStream input=new DataInputStream(inp);
            byte[] rawchars = new byte[siz];
            input.readFully(rawchars);
            str = new String(rawchars, mmb.getEncoding());
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql text  exception "+e);
            log.error(Logging.stackTrace(e));
            return "";
        }
        return str;
    }

    /**
     * Set text array in database
     * @javadoc
     */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] rawchars=null;
        try {
            rawchars=body.getBytes(mmb.getEncoding());
        } catch (Exception e) {
            log.error("MMObjectBuilder -> String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(rawchars);
            stmt.setBinaryStream(i,stream,rawchars.length);
            stream.close();
        } catch (Exception e) {
            log.error("MMObjectBuilder : Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Set byte array in database
     * @javadoc
     */
    public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
        try {
            ByteArrayInputStream stream=new ByteArrayInputStream(bytes);
            stmt.setBinaryStream(i,stream,bytes.length);
            stream.close();
        } catch (Exception e) {
            log.error("MMObjectBuilder : Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Checks if the numberTable exists.
     * If not this method will create one,
     * and inserts the DBKey retrieved by getCurrentKey
     */
    private void checkNumberTable() {
        if (log.isDebugEnabled()) log.trace("checks if table numberTable exists.");
        if(!created(getFullTableName("numberTable"))) {
            // Get the current object number
            int number = getCurrentKey();
            // integer should use getDataType?
            DatabaseTransaction trans=null;
            try {
                trans=createDatabaseTransaction();
                String sqlcreate=
                    createSQL(getFullTableName("numberTable"),
                        constructFieldDefinition("numberTable","number",FieldDefs.TYPE_INTEGER,-1,KEY_PRIMARY));
                trans.executeUpdate(sqlcreate);
                String sqlinsert= insertSQL(getFullTableName("numberTable"),getNumberString(),""+number);
                trans.executeUpdate(sqlinsert);
                trans.commit();
            } catch (StorageException e) {
                log.error(e.toString());
                if (trans!=null) trans.rollback();
                return;
            }
        }
    }

    /**
     * @javadoc
     */
    private synchronized int getCurrentKey() {
        int number=0;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            String sqlselect=selectSQL(getFullTableName("object"),"max("+getNumberString()+")");
            trans.executeQuery(sqlselect);
            number=trans.getIntegerResult();
            trans.commit();
        } catch (StorageException e) {
            log.error(e.toString());
            if (trans!=null) trans.rollback();
        }
        return number;
    }

    /**
     * Gives an unique number for a node to be inserted.
     * This method will work with multiple mmbases
     * @return unique number
     */
    public synchronized int createKey() {
        int number =-1;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            trans.executeUpdate("UPDATE "+getFullTableName("numberTable")+" SET "+getNumberString()+" = "+getNumberString()+"+1;");
            String sqlselect=selectSQL(getFullTableName("numberTable"),getNumberString());
            trans.executeQuery(sqlselect);
            number=trans.getIntegerResult();
            trans.commit();
        } catch (StorageException e) {
            log.error(e.toString());
            if (trans!=null) trans.rollback();
        }
        return number;
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

        MMObjectBuilder parent=getParentBuilder(builder);

        // call the database to update the parent table
        if (parent!=null) {
            insertIntoTable(parent,node,trans);
        } else if (!builder.getTableName().equals("object")) {
            // parent is object table
            insertObjectTable(node,trans);
        }
        return node.getNumber();
    }

    /**
     * This method inserts a new object in the object table.
     * Called by {@link #insert()} to maintain consistency.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    protected int insertObjectTable(MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqlinsert=
            insertSQL(getFullTableName("object"),
                         getNumberString()+getOTypeString()+getOwnerString(),
                         node.getNumber()+","+node.getIntValue("otype")+",'"+node.getStringValue("owner"));
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
    public void deleteFromTable(MMObjectBuilder builder,MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        super.deleteFromTable(builder,node,trans);
        // obtain the parent builder, if any
        MMObjectBuilder parent=getParentBuilder(builder);
        // call the database to update the parent table
        if (parent!=null) {
            deleteFromTable(parent,node,trans);
        } else if (!builder.getTableName().equals("object")) {
            // parent is object table
            deleteObjectTable(node,trans);
        }
    }

    /**
     * Remove a node from the object table.
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     */
    protected void deleteObjectTable(MMObjectNode node, DatabaseTransaction trans) throws StorageException {
        String sqldelete=deleteSQL(getFullTableName("object"),node.getNumber());
        trans.executeUpdate(sqldelete);
    }

}
