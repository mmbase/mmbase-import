/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.util.*;
import java.sql.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.util.XMLDatabaseReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Wrapper of MMJdbc2NodeInterface for the storage classes
 *
 * @author Pierre van Rooden
 * @version $Id: JDBC2NodeWrapper.java,v 1.9 2004-01-06 13:18:10 pierre Exp $
 */
public class JDBC2NodeWrapper implements MMJdbc2NodeInterface {

    private static final Logger log = Logging.getLoggerInstance(JDBC2NodeWrapper.class);

    private StorageManagerFactory factory;

    private Map allowedFields;

    private Map disallowedFields;

    private MMBase mmbase;

    public JDBC2NodeWrapper(StorageManagerFactory factory) {
        this.factory = factory;
        mmbase = factory.getMMBase();
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

    public void init(MMBase mmb,XMLDatabaseReader parser) {
        // do nothing
    }

    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        // should always be true in storage
        return true;
    }

    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child) throws StorageException {
        // ignore this call
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node, String fieldName, ResultSet rs, int i) {
        return decodeDBnodeField(node, fieldName, rs, i, "");
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node, String fieldName, ResultSet rs, int i, String prefix) {
        try {
            String mmfieldName = prefix + getDisallowedField(fieldName);
            FieldDefs field = node.getBuilder().getField(mmfieldName);
            if (field == null && !node.getBuilder().isVirtual()) {
                log.warn("Cannot determine field, fieldname passed: " + fieldName +
                          " prefix passed: " + prefix +
                          " from builder : " + node.getBuilder().getTableName());
            }
            DatabaseStorageManager sm = (DatabaseStorageManager)factory.getStorageManager();
            // getValue is protected, so can call it from the same package..
            Object value = sm.getValue(rs, i, field);
            node.setValue(mmfieldName, value);
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
        }
        return node;
    }

    public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul) {
        throw new UnsupportedOperationException("Storage classes do not support MMNODE syntax. Use SearchQuery.");
    }

    /**
     * @javadoc
     */
    public String getShortedText(String tableName,String fieldname,int number) {
        // capture calls form temporary nodes
        if (number < 0) return null;
        try {
            MMObjectBuilder bul = mmbase.getMMObject(tableName);
            return factory.getStorageManager().getStringValue(bul.getNode(number),bul.getField(fieldname));
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
            return null;
        }
    }

    /**
     * @javadoc
     */
    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        // capture calls form temporary nodes
        if (number < 0) return null;
        try {
            MMObjectBuilder bul = mmbase.getMMObject(tableName);
            return factory.getStorageManager().getBinaryValue(bul.getNode(number),bul.getField(fieldname));
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
            return null;
        }
    }

    public byte[] getDBByte(ResultSet rs,int idx) {
        try {
            DatabaseStorageManager sm = (DatabaseStorageManager)factory.getStorageManager();
            // getValue is protected, so can call it from the same package..
            return sm.getBinaryValue(rs, idx, null);
        } catch (SQLException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
            return null;
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
            return null;
        }
    }

    public String getDBText(ResultSet rs,int idx) {
        try {
            DatabaseStorageManager sm = (DatabaseStorageManager)factory.getStorageManager();
            // getValue is protected, so can call it from the same package..
            return sm.getStringValue(rs, idx, null);
        } catch (SQLException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
            return null;
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
            return null;
        }
    }

    public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
        try {
            return factory.getStorageManager().create(node);
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        try {
            factory.getStorageManager().change(node);
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        try {
            factory.getStorageManager().delete(node);
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
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
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public void setDBByte(int i, PreparedStatement stmt, byte[] bytes) {
        try {
            DatabaseStorageManager sm = (DatabaseStorageManager)factory.getStorageManager();
            // getValue is protected, so can call it from the same package..
            sm.setBinaryValue(stmt, i, bytes, null);
        } catch (SQLException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        } catch (StorageException se) {
            log.error(se.getMessage());
            log.error(Logging.stackTrace(se));
        }
    }

    public boolean created(String tableName) {
        try {
            DatabaseStorageManager sm = (DatabaseStorageManager)factory.getStorageManager();
            // getValue is protected, so can call it from the same package..
            // also call getStorageIdentifier to take into account any case-sensitivity
            return sm.exists((String)factory.getStorageIdentifier(tableName));
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public boolean create(MMObjectBuilder bul) {
        try {
            StorageManager mn = factory.getStorageManager();
            if (bul.getTableName().equals("object")) {
                mn.create();
            } else {
                mn.create(bul);
            }
            return true;
        } catch (StorageException se) {
            log.error(Logging.stackTrace(se));
            throw new StorageError(se);
        }
    }

    public boolean createObjectTable(String baseName) {
        try {
            StorageManager mn = factory.getStorageManager();
            if (!mn.exists()) mn.create();
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        javax.sql.DataSource ds = (javax.sql.DataSource)factory.getAttribute(Attributes.DATA_SOURCE);
        Connection con = ds.getConnection();
        if (con instanceof MultiConnection) {
            return (MultiConnection)con;
        } else {
            return new MultiConnection(null, con);
        }
    }

    public String getDisallowedField(String allowedfield) {
        // to lowercase, temporary hack
        allowedfield = allowedfield.toLowerCase();
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
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }


    public boolean updateTable(MMObjectBuilder bul) {
        try {
            factory.getStorageManager().change(bul);
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public boolean addField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().create(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public boolean removeField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().delete(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public boolean changeField(MMObjectBuilder bul,String dbname) {
        try {
            factory.getStorageManager().change(bul.getField(dbname));
            return true;
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError(se);
        }
    }

    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        try {
            return factory.getSearchQueryHandler().getSupportLevel(feature, query);
        } catch (StorageException se) {
            throw new SearchQueryException(se);
        }
    }

    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
        try {
            return factory.getSearchQueryHandler().getSupportLevel(constraint, query);
        } catch (StorageException se) {
            throw new SearchQueryException(se);
        }
    }

    public List getNodes(SearchQuery query, MMObjectBuilder builder) throws SearchQueryException {
        try {
            return factory.getSearchQueryHandler().getNodes(query, builder);
        } catch (StorageException se) {
            throw new SearchQueryException(se);
        }
    }
}

