/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.io.File;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Support2Storage implements a number of methods that allow a DatabaseStorage class to also implement
 * the MMJdbc2NodeInterface, for backward compatibility.
 * This code may become deprecated in the future.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: Support2Storage.java,v 1.1 2002-04-08 12:21:33 pierre Exp $
 */
public abstract class Support2Storage implements DatabaseStorage, MMJdbc2NodeInterface {

    /**
     * Constructs the Support2Storage database layer support class
     */
    protected Support2Storage() {
    }

    /**
     * Utility method, defined in AbstractDatabaseStorage
     */
    abstract protected String getFullTableName(String name);

    abstract public boolean getStoreBinaryAsFile();

    abstract public String getBinaryFilePath();

    abstract public String mapToMMBaseFieldName(String allowedfield);

    abstract public String mapToTableFieldName(String disallowedfield);

    abstract public void loadFieldFromTable(MMObjectNode node,String fieldname, ResultSet rs,int i);

    /**
     * Utility method, defined in AbstractDatabaseStorage
     */
    abstract protected String getText(String tableName,String fieldname,int number);

    /**
     * Utility method, defined in AbstractDatabaseStorage
     */
    abstract protected byte[] getBytes(String tableName,String fieldname,int number);

    /**
     * Utility method, defined in SQL92DatabaseStorage
     */
    abstract protected byte[] readBytesFromFile(String path);

    abstract public int insert(MMObjectNode node);

    abstract public boolean commit(MMObjectNode node);

    abstract public void delete(MMObjectNode node);

    abstract public boolean createObjectStorage();

    abstract public int createKey();

    abstract public boolean updateStorage(MMObjectBuilder builder);

    /**
     * Maps a MMBase fieldname to a fieldname acceptable to the database.
     * @deprecated use {@link #mapToMMBaseFieldName() }
     * @param fieldname the fieldname to map
     */
    public String getDisallowedField(String allowedfield) {
        return mapToMMBaseFieldName(allowedfield);
    }

    /**
     * Maps a database fieldname to a fieldname as used by the MMbase system.
     * @deprecated use {@link #mapToTableFieldName() }
     * @param fieldname the fieldname to map
     */
    public String getAllowedField(String disallowedfield) {
        return mapToTableFieldName(disallowedfield);
    }

    /**
     * Maps the 'otype' fieldname to a fieldname acceptable to the database.
     */
    public String getOTypeString() {
        return mapToTableFieldName("otype");
    }

    /**
     * Maps the 'owner' fieldname to a fieldname acceptable to the database.
     */
    public String getOwnerString() {
        return mapToTableFieldName("owner");
    }

    /**
     * Stores a field in a table ResultSet in a MMObjectNode.
     * @deprecated use {@link #loadFieldFromTable() }
     * @param node the node to store the field in
     * @param fieldname the name of the field as it is known in the ResultSet
     * @param rs the ResultSet containing the table row
     * @param i the index of the field in the ResultSet
     * @return the MMObjectNode
     * @see #loadFieldFromTable()
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        fieldname=mapToMMBaseFieldName(fieldname);
        loadFieldFromTable(node,fieldname,rs,i);
        return node;
    }

    /**
     * Stores a field in a table ResultSet in a MMObjectNode.
     * @deprecated use {@link #loadFieldFromTable() }
     * @param node the node to store the field in
     * @param fieldname the name of the field as it is known in the ResultSet
     * @param rs the ResultSet containing the table row
     * @param i the index of the field in the ResultSet
     * @param prefix a prefix to use when determining the node's fieldname. used for clusternodes
     * @return the MMObjectNode
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
        fieldname=prefix+mapToMMBaseFieldName(fieldname);
        loadFieldFromTable(node,fieldname,rs,i);
        return node;
    }

    /**
     * Converts a SCAN NODE syntax to SQL.
     * @deprecated This is not supported by this database layer, and only included due to
     * the interface requirements.
     */
    public String getMMNodeSearch2SQL(String where,MMObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get text from blob
     * @deprecated use getText(MMObjectNode,fieldname);
     * @javadoc
     */
    public String getShortedText(String tableName,String fieldname,int number) {
        return getText(getFullTableName(tableName),fieldname,number);
    }

    /**
     * Get byte of a database blob
     * @deprecated use getBytes(MMObjectNode,fieldname)
     * @javadoc
     */
    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        if (getStoreBinaryAsFile()) {
            return readBytesFromFile(getBinaryFilePath()+tableName+File.separator+number+"."+fieldname);
        } else {
            return getBytes(getFullTableName(tableName),fieldname,number);
        }
    }

    /**
     * Obtain a connection to the database using the passed JDBC engine.
     * @deprecated Obsolete as the database should not connect to any other engine
     * than the one loaded by the associated (known) MMBase instance.
     * Use {@link #createTransaction()} or {@link #getConnection()} instead.
     * @param jdbc the JDBC engineto use
     * @throws SQLException if the connection could not be made
     */
    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        return jdbc.getConnection(jdbc.makeUrl());
    }

    /**
     * This method inserts a new object in a specified builder table.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored in the database tables.
     * @deprecated use {@link insert(MMObjectNode) }
     * @param owner The nodes' owner. Ignored
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     */
    public int insert(MMObjectBuilder builder,String owner, MMObjectNode node) {
        return insert(node);
    }

    /**
     * Commit this node to the specified builder table.
     * @deprecated use {@link commit(MMObjectNode) }
     * @param builder the builder to commit the node to. This can be a parentbuilder of the node's actual builder
     * @param node The node to commit
     * @return true of succesful, false otherwise
     */
    public boolean commit(MMObjectBuilder builder,MMObjectNode node){
        return commit(node);
    }

    /**
     * Remove a node from the specified builder table.
     * @deprecated use {@link delete(MMObjectNode) }
     * @param builder the builder to remove the node from. This can be a parentbuilder of the node's actual builder
     * @param node The node to delete
     */
    public void removeNode(MMObjectBuilder builder,MMObjectNode node) {
        delete(node);
    }

    /**
     * Create the object table (the basic table for all objects) for the specified basename.
     * @deprecated use {@link #createObjectStorage()}
     * @param baseName the basename of the table to create. This parameter is already known and is ignored.
     * @return true if the table was succesfully created
     */
    public boolean createObjectTable(String baseName) {
        return createObjectStorage();
    }

    /**
     * Gives an unique number for a node to be inserted.
     * This method will work with multiple mmbases
     * @deprecated use {@link #createKey()}
     * @return unique number
     */
    public int getDBKey(){
        return createKey();
    }

    /**
     * Changes the table of a builder to match its new configuration.
     * @deprecated use {@link #updateStorage() }
     * @param builder the builder whose table to change
     * @return true if succesful
     */
    public boolean updateTable(MMObjectBuilder builder) {
        return updateStorage(builder);
    }

    /**
     * Get byte of a database blob
     * @javadoc
     */
    abstract public byte[] getDBByte(ResultSet rs,int idx);

    /**
     * Get text of a database blob
     * @javadoc
     */
    abstract public String getDBText(ResultSet rs,int idx);


}
