/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.*;
import java.sql.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.SearchQueryHandler;

import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.util.XMLDatabaseReader;

/**
 * Wrapper of MMJdbc2NodeInterface for the storage classes
 *
 * @author Pierre van Rooden
 * @version $Id: JDBC2NodeWrapper.java,v 1.3 2003-08-04 11:38:23 pierre Exp $
 */
public abstract class JDBC2NodeWrapper implements MMJdbc2NodeInterface {
    
    private StorageManagerFactory factory;
    
    private Map allowedFields;
    
    private Map disallowedFields;
    
    public JDBC2NodeWrapper() {
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
    public int getDBKey() {
        try {
            return factory.getStorageManager().createKey();
        } catch (StorageException se) {
            return -1;
        }
    }
    
    public void init(MMBase mmb,XMLDatabaseReader parser) {
        try {
            this.factory = StorageManagerFactory.newInstance(mmb);
        } catch (StorageException se) {
            throw new StorageError();
        }
        disallowedFields = factory.getDisallowedFields();
        allowedFields = new HashMap();
        for (Iterator i = disallowedFields.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry)i.next();
            String val = (String) e.getValue();
            String key = (String) e.getKey();
            if (val != null) {
                allowedFields.put(val, key);
            }
        }
    }
    
    /**
     * @javadoc
     */
    abstract public void setDBByte(int i, PreparedStatement stmt,byte[] bytes);
    
    /**
     * Tells if a table already exists
     * @return true if table exists, false if table doesn't exists
     */
    abstract public boolean created(String tableName);

    public boolean create(MMObjectBuilder bul) {
        try {
            factory.getStorageManager().create(bul);
            return true;
        } catch (StorageException se) {
            return false;
        }
    }
    
    public boolean createObjectTable(String baseName) {
        try {
            factory.getStorageManager().create();
            return true;
        } catch (StorageException se) {
            return false;
        }
    }

     /**
     * @javadoc
     */
    abstract public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException;

    public String getDisallowedField(String allowedfield) {
        String disallowedfield = (String)allowedFields.get(allowedfield);
        if (disallowedfield != null) {
            return disallowedfield;
        } else {
            return allowedfield;
        }
    }

    public String getAllowedField(String disallowedfield) {
        String allowedfield = (String)disallowedFields.get(disallowedfield);
        if (allowedfield != null) {
            return allowedfield;
        } else {
            return disallowedfield;
        }
    }

    public String getNumberString() {
        return getAllowedField("number");
    }

    public String getOwnerString() {
        return getAllowedField("owner");
    }
    
    public String getOTypeString() {
        return getAllowedField("otype");
    }

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

