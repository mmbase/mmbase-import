/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.sql.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.SearchQueryHandler;

import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.util.XMLDatabaseReader;

/**
 * MMJdbc2NodeInterface interface needs to be implemented to support a new database
 * It is used to abstract the query's needed for mmbase for each database.
 * @author Vpro
 * @author Pierre van Rooden
 * @version $Id: JDBC2NodeWrapper.java,v 1.2 2003-08-04 10:57:35 pierre Exp $
 */
public abstract class JDBC2NodeWrapper implements MMJdbc2NodeInterface {
    
    StorageManagerFactory factory;
    
    /**
     * @javadoc
     */
    public JDBC2NodeWrapper(StorageManagerFactory factory) {
        this.factory = factory;
    }

    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        // should always be true in storage
        return true;
    }

    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child) throws StorageException {
        // ignore this call
    }

    /**
     * @javadoc please
     */
    abstract public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i);
    /**
     * @javadoc please
     */
    abstract public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix);

    /**
     * Converts an MMNODE expression to an SQL expression. Returns the
     * result as an SQL where-clause, but with the leading "WHERE " left out.
     *
     * @param where The MMNODE expression.
     * @param bul The builder for the type of nodes that is queried.
     * @return The SQL expression.
     * @see org.mmbase.module.core.MMObjectBuilder#convertMMNode2SQL(String)
     */
    abstract public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul);
    /**
     * @javadoc
     */
    abstract public String getShortedText(String tableName,String fieldname,int number);
    /**
     * @javadoc
     */
    abstract public byte[] getShortedByte(String tableName,String fieldname,int number);
    /**
     * @javadoc
     */
    abstract public byte[] getDBByte(ResultSet rs,int idx);
    /**
     * @javadoc
     */
    abstract public String getDBText(ResultSet rs,int idx);
    
    public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
        try {
            return factory.getStorageManager().create(node);
        } catch (StorageException se) {
            return -1;
        }
    }
    
    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        try {
            factory.getStorageManager().change(node);
            return true;
        } catch (StorageException se) {
            return false;
        }
    }
    
    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        try {
            factory.getStorageManager().delete(node);
        } catch (StorageException se) {
        }
    }
    
    /**
     * Gives an unique number for a node to be inserted.
     * This method should be implemneted to work with multiple mmbase instances working on
     * the same storage.
     * @return unique number
     */
    abstract public int getDBKey();
    
    /**
     * @javadoc
     */
    abstract public void init(MMBase mmb,XMLDatabaseReader parser);
    
    /**
     * @javadoc
     */
    abstract public void setDBByte(int i, PreparedStatement stmt,byte[] bytes);
    
    /**
     * Tells if a table already exists
     * @return true if table exists, false if table doesn't exists
     */
    abstract public boolean created(String tableName);
    /**
     * @javadoc
     */
    abstract public boolean create(MMObjectBuilder bul);
    /**
     * @javadoc
     */
    abstract public boolean createObjectTable(String baseName);
     /**
     * @javadoc
     */
    abstract public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException;
     /**
     * @javadoc
     */
    abstract public String getDisallowedField(String allowedfield);
     /**
     * @javadoc
     */
    abstract public String getAllowedField(String disallowedfield);
     /**
     * @javadoc
     */
    abstract public String getNumberString();

    /**
     * @javadoc
     */
    abstract public String getOwnerString();
    /**
     * @javadoc
     */
    abstract public String getOTypeString();

    public boolean drop(MMObjectBuilder bul) {
        try {
            factory.getStorageManager().delete(bul);
            return true;
        } catch (StorageException se) {
            return false;
        }
    }

    
    public boolean updateTable(MMObjectBuilder bul) {
        try {
            factory.getStorageManager().change(bul);
            return true;
        } catch (StorageException se) {
            return false;
        }
    }

    public boolean addField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().create(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            return false;
        }
    }

    public boolean removeField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().delete(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            return false;
        }
    }

    public boolean changeField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().change(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            return false;
        }
    }
}

