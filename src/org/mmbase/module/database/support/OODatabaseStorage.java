/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.util.logging.*;

/**
 * OODatabaseStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * an objectoriented database (i.e. Postgresql).
 * It overrides the methods for storing and retrieving huge texts and bytefields, and the methods for determining
 * database key (for the object 'number' field).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: OODatabaseStorage.java,v 1.1 2002-04-09 15:40:31 pierre Exp $
 */
public class OODatabaseStorage extends SQL92DatabaseStorage implements DatabaseStorage {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(OODatabaseStorage.class.getName());

    /*
     * Constructs the AbstractDatabaseSupport database layer support class
     */
    public OODatabaseStorage() {
    }

    /**
     * Get text from blob
     * @javadoc
     */
    protected String getText(String tableName,String fieldname,int number) {
        throw new UnsupportedOperationException("getText");
    }

    /**
     * Get bytes from blob
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

    protected void prepare() {
        createSequence();
    }

    private boolean createSequence() {
        boolean result=created(getFullTableName("autoincrement"));
        if (!result) {
            DatabaseTransaction trans=null;
            try {
                trans=createDatabaseTransaction();
                trans.executeUpdate("CREATE SEQUENCE "+getFullTableName("autoincrement")+" INCREMENT 1 START 1");
                trans.commit();
                result=true;
            } catch (StorageException e) {
                if (trans!=null) trans.rollback();
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Retrieves a new unique number, which can be used to inside _object table
     * @return a new unique number for new nodes or -1 on failure
     */
    public int createKey() {
        int number =-1;
        DatabaseTransaction trans=null;
        try {
            trans=createDatabaseTransaction();
            String sqlselect="SELECT NEXTVAL ('"+  getFullTableName("autoincrement") + "')";
            trans.executeQuery(sqlselect);
            number=trans.getIntegerResult();
            trans.commit();
        } catch (StorageException e) {
            log.error(e.toString());
            if (trans!=null) trans.rollback();
        }
        return number;
    }

    /** is next function nessecary? */
    public byte[] getDBByte(ResultSet rs,int idx) {
        byte[] bytes=null;
        try {
            Blob b = rs.getBlob(idx);
            return b.getBytes(0, (int)b.length());
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql byte  exception "+e);
            log.error(Logging.stackTrace(e));
        }
        return bytes;
    }

    /** is next function nessecary? */
    public void setDBByte(int i, PreparedStatement stmt, byte[] bytes) {
        try {
            java.io.InputStream stream=new java.io.ByteArrayInputStream(bytes);
            stmt.setBinaryStream(i, stream, bytes.length);
            stream.close();
        } catch (Exception e) {
            log.error("Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }
    }

    /** is next function nessecary? */
    public String getDBText(ResultSet rs,int idx) {
        try {
            return rs.getString(idx);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Set text array in database
     * @javadoc
     */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        try {
            stmt.setString(i, body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

