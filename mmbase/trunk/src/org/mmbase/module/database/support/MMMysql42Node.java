/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;


/**
 * MMMysql42Node implements the MMJdbc2NodeInterface for
 * mysql.
 *
 * @author Daniel Ockeloen
 * @version $Id: MMMysql42Node.java,v 1.20 2002-03-26 13:05:49 pierre Exp $
 */
public class MMMysql42Node extends MMSQL92Node implements MMJdbc2NodeInterface {
    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(MMMysql42Node.class.getName());

    /**
     * Returns an unique number.
     * This method will work with multiple MMBase instances.
     * @return the unique number as an int
     */
    public synchronized int getDBKey() {
        int number =-1;
        try {
            Connection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate("lock tables "+mmb.baseName+"_numberTable WRITE;");
            stmt.executeUpdate("update "+mmb.baseName+"_numberTable set number = number+1");
            ResultSet rs=stmt.executeQuery("select number from "+mmb.baseName+"_numberTable;");
            while(rs.next()) {
                number=rs.getInt(1);
            }
            stmt.executeUpdate("unlock tables;");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("MMSQL92NODE -> SERIOUS ERROR, Problem with retrieving DBNumber from database");
            log.error(Logging.stackTrace(e));
        }
        if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> retrieving number "+number+" from the database");
        return number;
    }
}
