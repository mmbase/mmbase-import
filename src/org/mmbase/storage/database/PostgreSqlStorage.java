/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.sql.*;

import org.mmbase.storage.*;
import org.mmbase.util.logging.*;

/**
 * PostreSqlStorage implements the DatabaseStorage interface and the MMJdbc2NodeInterface for
 * the Postgresql database.
 * It overrides the methods for storing and retrieving huge texts and bytefields, and the methods for determining
 * database key (for the object 'number' field).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: PostgreSqlStorage.java,v 1.1 2003-05-02 14:23:05 michiel Exp $
 */
public class PostgreSqlStorage extends OODatabaseStorage implements DatabaseStorage {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(PostgreSqlStorage.class.getName());

    /*
     * Constructs the AbstractDatabaseSupport database layer support class
     */
    public PostgreSqlStorage() {
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
     * Gives an unique number for a node to be inserted.
     * This method will work with multiple mmbases
     * @param trans the transaction to use for obtaining the key
     * @return unique number
     * @throws StorageException if an error occurred while obtaining the key
     */
    public synchronized int createKey(Transaction trans) throws StorageException {
        DatabaseTransaction dbtrans = (DatabaseTransaction)trans;
        String sqlselect="SELECT NEXTVAL ('"+  getFullTableName("autoincrement") + "')";
        dbtrans.executeQuery(sqlselect);
        return dbtrans.getIntegerResult();
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


}

