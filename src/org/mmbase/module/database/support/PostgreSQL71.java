/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.logging.*;

/**
 * Postgresql driver for MMBase, only works with Postgresql 7.1 + that supports inheritance on default.
 * This is a seperated class, since the handling of the byte stream has some difficulties in the standard
 * JDBC 7.1 drivers
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Eduard Witteveen
 * @version $Id: PostgreSQL71.java,v 1.29 2004-01-27 12:04:49 pierre Exp $
 */
public class PostgreSQL71 extends PostgreSQL {
    private static Logger log = Logging.getLoggerInstance(PostgreSQL71.class.getName());

    protected boolean setValuePreparedStatement(PreparedStatement stmt, MMObjectNode node, String key, int i) throws SQLException {
        switch(node.getDBType(key)) {
        case FieldDefs.TYPE_NODE:
        case FieldDefs.TYPE_INTEGER:
            stmt.setInt(i, node.getIntValue(key));
            log.trace("added integer for field with name: " + key + " with value: " + node.getIntValue(key));
            break;
        case FieldDefs.TYPE_FLOAT:
            stmt.setFloat(i, node.getFloatValue(key));
            log.trace("added float for field with name: " + key + " with value: " + node.getFloatValue(key));
            break;
        case FieldDefs.TYPE_DOUBLE:
            stmt.setDouble(i, node.getDoubleValue(key));
            log.trace("added double for field with name: " + key + " with value: " + node.getDoubleValue(key));
            break;
        case FieldDefs.TYPE_LONG:
            stmt.setLong(i, node.getLongValue(key));
            log.trace("added long for field with name: " + key + " with value: " + node.getLongValue(key));
            break;
        case FieldDefs.TYPE_XML:
        case FieldDefs.TYPE_STRING:
            stmt.setString(i, node.getStringValue(key));
            log.trace("added string for field with name: " + key + " with value: " + node.getStringValue(key));
            break;
        case FieldDefs.TYPE_BYTE:
            // arrg...
            try {
                byte[] bytes = node.getByteValue(key);
                java.io.InputStream stream=new java.io.ByteArrayInputStream(bytes);
                if (log.isDebugEnabled()) log.trace("in setDBByte ... before stmt");
                stmt.setBinaryStream(i, stream, bytes.length);
                if (log.isDebugEnabled()) log.trace("in setDBByte ... after stmt");
                stream.close();
                log.trace("added bytes for field with name: " + key + " with with a length of #"+bytes.length+"bytes");
            } catch (Exception e) {
                log.error("Can't set byte stream");
                log.error(Logging.stackTrace(e));
            }
            // was setDBByte(i, stmtstmt, node.getByteValue(key))
            break;
        default:
            log.warn("unknown type for field with name : " + key);
            log.trace("added string for field with name: " + key + " with value: " + node.getStringValue(key));
            break;
        }
        return true;
    }

    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        MultiConnection con = null;
        Statement stmt = null;
        try {
            log.debug("retrieving the field :" + fieldname +" of object("+number+") of type : " + tableName);
            con = mmb.getConnection();

            // support for larger objects...
            con.setAutoCommit(false);

            stmt = con.createStatement();

            String sql = "SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" WHERE "+getNumberString()+" = "+number;
            log.debug("gonna excute the followin query: " + sql);
            ResultSet rs=stmt.executeQuery(sql);
            java.io.DataInputStream is = null;

            byte[] data=null;
            if(rs.next())
            {
                log.debug("found a record, now trying to retieve the stream..with loid #" + rs.getInt(fieldname));
                Blob b = rs.getBlob(1);
                data = b.getBytes(0, (int)b.length());
                log.debug("data was read from the database(#"+data.length+" bytes)");
            }
            rs.close();
            stmt.close();
            // a get doesnt make changes !!
            con.commit();
            con.setAutoCommit(true);
            con.close();

            if (data != null)
            {
                log.debug("retrieved "+data.length+" bytes of data");
            }
            else
            {
                log.error("retrieved NO data for node #" + number + " it's field: " + fieldname + "\n" + sql);
            }
            return data;
        } catch (SQLException sqle) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName);
            log.error(sqle);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            log.error(Logging.stackTrace(sqle));
            try {
                if(stmt!=null) stmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            }
            catch(Exception other) {}
            return null;
        }
        catch (Exception e) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName +" (possible IOError)");
            log.error(e);
            log.error(Logging.stackTrace(e));
            try
            {
                if(stmt!=null) stmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            }
            catch(Exception other) {}
            return null;
        }
    }
}
