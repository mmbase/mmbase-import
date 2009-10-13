/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class sets the 'lock mode' to 30.
 * @author vpro
 * @version $Id$
 * @deprecated Use ;IFX_LOCK_MODE_WAIT=31  on the connection string in jdbc.xml in stead
 */
public class DatabaseSupportInformix implements DatabaseSupport {

    private static final Logger log = Logging.getLoggerInstance(DatabaseSupportInformix.class);

    public void init() {
    }

    public void initConnection(Connection con) {
        setLockMode(con, 30);
    }

    protected void setLockMode(Connection con,int sec) {
        PreparedStatement statement;
        try {
            if (sec>0) {
                statement=con.prepareStatement("set lock mode to wait "+sec);
            } else {
                statement=con.prepareStatement("set lock mode to wait");
            }
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            log.error("failed to set lock mode " + e.getMessage(), e);
        }
    }
}
