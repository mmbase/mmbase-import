/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.tests;
import java.sql.*;

/**
 * This class contains static methods for MMBase tests.
 * 
 * @author Michiel Meeuwissen
 */
public class ShutdownHsql {
    public static void main(String[] argv) {
        try {
            Class.forName("org.hsqldb.jdbcDriver" );
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver." + e.getMessage());
            return;
        }
        try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test", "sa", "");
            c.createStatement().execute("SHUTDOWN");
        } catch (SQLException sqe) {
            System.err.println(sqe.getMessage());
        }
    }
}

