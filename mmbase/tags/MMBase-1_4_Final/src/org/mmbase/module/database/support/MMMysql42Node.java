/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;


/**
* MMMysql42Node implements the MMJdbc2NodeInterface for
* mysql this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @author Daniel Ockeloen
* @version 12 Mar 1997
* @$Revision: 1.19 $ $Date: 2001-03-09 09:12:04 $
*/
public class MMMysql42Node extends MMSQL92Node implements MMJdbc2NodeInterface {
    /**
    * Logging instance
    */
	private static Logger log = Logging.getLoggerInstance(MMMysql42Node.class.getName());
	
	/**
     * gives an unique number
     * this method will work with multiple mmbases
     * @return unique number
     */
    public synchronized int getDBKey() {
        int number =-1;
        try {
            MultiConnection con=mmb.getConnection();
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
            log.error("MMSQL92NODE -> SERIOUS ERROR, Problem with retrieving DBNumber from databse");
            log.error(Logging.stackTrace(e));
        }
        if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> retrieving number "+number+" from the database");
        return (number);
    }
}
