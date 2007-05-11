/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * Interface to support specific database things
 * for the JDBC module
 *
 * @duplicate Since neither method does anything, better to make {@link DatabaseSupport}
 *            a base class and have {@link DatabaseSupportInformix} extend from that class.
 * @author vpro
 * @version $Id: DatabaseSupportShim.java,v 1.5 2004-10-07 17:22:34 pierre Exp $
 */
public class DatabaseSupportShim implements DatabaseSupport {

    public void init() {
    }

    public void initConnection(Connection con) {
    }
}
