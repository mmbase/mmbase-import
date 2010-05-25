package org.mmbase.storage.implementation.database;

import java.lang.reflect.Method;
import java.sql.*;
import org.mmbase.module.database.JDBC;
import org.mmbase.module.Module;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.database.MultiConnection;


public class InformixStorageManager extends DatabaseStorageManager {
    private static final Logger log = Logging.getLoggerInstance(InformixStorageManager.class);

    /**
     * Safely closes the active connection.
     * If a transaction has been started, the connection is not closed.
     */
    @Override protected void releaseActiveConnection() {
        if (!(inTransaction && factory.supportsTransactions()) && activeConnection != null) {
            try {
                // ensure that future attempts to obtain a connection (i.e.e if it came from a pool)
                // start with autocommit set to true
                // needed because Query interface does not use storage layer to obtain transactions
                activeConnection.setAutoCommit(true);
                closeInformix();
                activeConnection.close();
            } catch (SQLException se) {
                // if something went wrong, log, but do not throw exceptions
                log.error("Failure when closing connection: " + se.getMessage());
            }
            activeConnection = null;
        }
    }


    private void closeInformix() {
        Connection con = ((MultiConnection)activeConnection).unwrap(Connection.class);
        try {
            Method scrub = Class.forName("com.informix.jdbc.IfxConnection").getMethod("scrubConnection");
            scrub.invoke(con);
            ((JDBC) Module.getModule("jdbc")).getSupport().initConnection(con);
        } catch (Exception e) {
            log.error("Exception while calling releaseBlob(): " + e.getMessage());
        }
    }
}
