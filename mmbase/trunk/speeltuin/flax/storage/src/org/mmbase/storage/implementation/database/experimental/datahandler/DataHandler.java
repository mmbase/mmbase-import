/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database.experimental.datahandler;

import java.sql.*;
import java.util.*;

import org.mmbase.storage.*;
import org.mmbase.storage.implementation.database.experimental.*;

import org.mmbase.util.logging.*;
/**
 * This class is the baseclass for all Data Manipulations on the database
 * @author Eduard Witteveen
 * @since MMBase-1.8
 * @version $Id: DataHandler.java,v 1.1 2005-02-16 13:01:46 eduard Exp $
 */
public class DataHandler {
    private static final Logger log = Logging.getLoggerInstance(DataHandler.class);
    protected ViewDatabaseStorageManager parent = null;
            
    public DataHandler(ViewDatabaseStorageManager caller) {
        parent = caller;
    }
    
    public int getNextSequenceNumber() throws StorageException  {
        Connection con=null;
        Statement stmt=null;
        // select test8_autoincrement.nextval from dual;
        String sql = "SELECT " + parent.getSequenceName() + ".NEXTVAL FROM DUAL";
        int number = -1;
        try {
//            log.debug("gonna execute the following sql statement: " + sql);
            con = parent.getConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
                if (rs.next()) {
                    number=rs.getInt("NEXTVAL");
                }
                else {
                    log.warn("could not retieve the number for new node");
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
        }
        catch (SQLException sqle) {
            log.error("error, could not retrieve new object number:"+sql);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQL:" + sql);
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                con.close();
            }
            catch(Exception other) {}
            throw new StorageException(sqle.toString());
        }
        log.debug("new object id #"+number);
        return number;
    }
}
