/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.net.*;
import java.sql.*;

import org.mmbase.storage.database.UnsupportedDatabaseOperationException;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * More info for differences between the versions: http://www.postgresql.org/idocs/index.php?jdbc-binary-data.html
 *
 * 7.2 is the first release of the JDBC Driver that supports the bytea data type. 
 * The introduction of this functionality in 7.2 has introduced a change in behavior as compared to previous 
 * releases. In 7.2 the methods getBytes(), setBytes(), getBinaryStream(), and setBinaryStream() operate 
 * on the bytea data type. In 7.1 these methods operated on the OID data type associated with Large Objects. 
 * It is possible to revert the driver back to the old 7.1 behavior by setting the compatible property on the 
 * Connection to a value of 7.1
 * To use the bytea data type you should simply use the getBytes(), setBytes(), getBinaryStream(), or 
 * setBinaryStream() methods.
 * To use the Large Object functionality you can use either the LargeObject API provided by the PostgreSQL JDBC 
 * Driver, or by using the getBLOB() and setBLOB() methods.
 *
 * Important: For PostgreSQL, you must access Large Objects within an SQL transaction. You would open a 
 * transaction by using the setAutoCommit() method with an input parameter of false.
 * 
 * Note: In a future release of the JDBC Driver, the getBLOB() and setBLOB() methods may no longer interact 
 * with Large Objects and will instead work on bytea data types. So it is recommended that you use the LargeObject 
 * API if you intend to use Large Objects. 
 *
 * Postgresql driver for MMBase
 * @author Eduard Witteveen
 * @version $Id: PostgreSQL72.java,v 1.1 2002-10-11 13:03:13 eduard Exp $
 */
//public class PostgreSQL72 extends PostgresSQL71 implements MMJdbc2NodeInterface  {
public class PostgreSQL72 extends  PostgreSQL71 {

    private static Logger log = Logging.getLoggerInstance(PostgreSQL72.class.getName());

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
		stmt.setBytes(i, node.getByteValue(key));
		log.trace("added bytes for field with name: " + key + " with with a length of #"+ node.getByteValue(key).length+"bytes");
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

	    // TODO: use prepare statement, can be cached..
            stmt = con.createStatement();
	    
            String sql = "SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" WHERE "+getNumberString()+" = "+number;
            log.debug("gonna excute the followin query: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
	    if(rs == null) throw new RuntimeException("Error retrieving the result set for query:"+sql);

           byte[] data=null;
            if(rs.next()) {
		data = rs.getBytes(1);
                log.debug("data was read from the database(#"+data.length+" bytes)");
            }
            rs.close();
            stmt.close();

            con.close();

            if (data != null) {
                log.debug("retrieved "+data.length+" bytes of data");
            } else {
                log.error("retrieved NO data for node #" + number + " it's field: " + fieldname + "\n" + sql);
            }
            return data;
        } catch (SQLException sqle) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName);
            log.error(Logging.stackTrace(sqle));
            for(SQLException se = sqle;se != null; se = se.getNextException()){
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
            } catch(Exception other) {}
            return null;
        } catch (Exception e) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName +" (possible IOError)");
            log.error(Logging.stackTrace(e));
            try {
                if(stmt!=null) stmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            } catch(Exception other) {}
            return null;
        }
    }
}
