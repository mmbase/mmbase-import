/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;

import javax.sql.DataSource;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.database.JDBCInterface;
import org.mmbase.storage.StorageInaccessibleException;

/**
 * This class functions as a Datasource wrapper around the JDBC Module.
 * It is intended for use when MMBase runs outside an applicationserver, or when the server does not (or poorly)
 * support pooled datasources.
 * I can also be used by older systems that use the JDBC Module and do not want to change their configuration.
 * Note that the JDBC Module will likely be fased out as a module at some point in the future,
 * with code and supporting classes to be moved to this class instead.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: GenericDataSource.java,v 1.4 2004-01-21 09:06:02 michiel Exp $
 */
public final class GenericDataSource implements DataSource {

    // Reference to the JDBC Module
    private JDBCInterface jdbc;
    // Datasource specific log writer, default disabled
    private java.io.PrintWriter printWriter = null;
    // Datasource login timeout, initially zero
    private int loginTimeout = 0;

    /**
     * Constructs a datasource for accessing the database belonging to the given MMBase module.
     * The MMBase parameter is not currently used, but is included for future expansion
     * @param mmbase the MMBase instance
     * @throws StorageInaccessibleException if the JDBC module used in creating the datasource is inaccessible
     */
    public GenericDataSource(MMBase mmbase) throws StorageInaccessibleException {
        jdbc = (JDBCInterface)Module.getModule("JDBC", true);
        if (jdbc == null) {
            throw new StorageInaccessibleException("Cannot load Datasource or JDBC Module");
        }
    }

    /**
     * Attempt to establish a database connection.
     * @return a Connection to the database
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return jdbc.getConnection(jdbc.makeUrl());
    }

    /**
     * Attempt to establish a database connection.
     * @param userName - the database user on whose behalf the Connection is being made
     * @param password - the user's password
     * @return a Connection to the database
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public Connection getConnection(String userName, String password) throws SQLException {
        return jdbc.getConnection(jdbc.makeUrl(), userName, password);
    }

    /**
     * Gets the maximum time in seconds that this data source will wait while attempting to connect to a
     * database. A value of zero specifies that the timeout is the default system timeout if there is one;
     * otherwise it specifies that there is no timeout. When a DataSource object is created the login
     * timeout is initially zero.<br />
     * Note: the default system timeout is 30 seconds (unless otherwise specified in the JDBC configuration file)
     * @return the data source login time limit
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * Get the log writer for this data source.
     * The log writer is a character output stream to which all logging and tracing messages for this
     * data source object instance will be printed. This includes messages printed by the methods of
     * this object, messages printed by methods of other objects manufactured by this object, and so on.
     * Messages printed to a data source specific log writer are not printed to the log writer associated
     * with the java.sql.DriverManager class. When a DataSource object is created the log writer is
     * initially null, in other words, logging is disabled.
     * @return the log writer for this data source, null if disabled
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public java.io.PrintWriter getLogWriter() {
        return printWriter;
    }

    /**
     * Sets the maximum time in seconds that this data source will wait while attempting to connect to a
     * database. A value of zero specifies that the timeout is the default system timeout if there is one;
     * otherwise it specifies that there is no timeout. When a DataSource object is created the login
     * timeout is initially zero. <br />
     * Note: currently this code does not actually change the timeout.
     * @param seconds - the data source login time limit
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public void setLoginTimeout(int seconds) {
        // loginTimeout = seconds;
    }

    /**
     * Set the log writer for this data source.
     * The log writer is a character output stream to which all logging and tracing messages for this
     * data source object instance will be printed. This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured by this object, and so on.
     * Messages printed to a data source specific log writer are not printed to the log writer associated
     * with the java.sql.Drivermanager class. When a DataSource object is created the log writer is
     * initially null, in other words, logging is disabled.
     * @param out - the new log writer; to disable, set to null
     * @throws java.sql.SQLException - if a database-access error occurs.
     */
    public void setLogWriter(java.io.PrintWriter out) {
        printWriter = out;
    }

}
