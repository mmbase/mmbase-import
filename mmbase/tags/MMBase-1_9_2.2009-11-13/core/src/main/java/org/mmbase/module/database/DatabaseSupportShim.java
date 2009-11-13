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
 * @author vpro
 * @version $Id$
 */
public class DatabaseSupportShim implements DatabaseSupport {

    public void init() {
    }

    public void initConnection(Connection con) {
    }
}
