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
import org.mmbase.module.database.JDBC;
import org.mmbase.storage.StorageInaccessibleException;
import org.mmbase.util.logging.*;

/**
 * This class functions as a Datasource wrapper around the JDBC Module.
 * It is intended for use when MMBase runs outside an applicationserver, or when the server does not (or poorly)
 * support pooled datasources.
 * I can also be used by older systems that use the JDBC Module and do not want to change their configuration.
 * Note that the JDBC Module will likely be fased out as a module at some point in the future,
 * with code and supporting classes to be moved to this class instead.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public final class GenericDataSource implements DataSource {
    private static final Logger log = Logging.getLoggerInstance(GenericDataSource.class);

    // Reference to the JDBC Module
    final private JDBC jdbc;

    private java.io.PrintWriter printWriter = null;

    final private String dataDir;
    final private boolean meta;

    private boolean basePathOk = false;

    /**
     * Constructs a datasource for accessing the database belonging to the given MMBase module.
     * The MMBase parameter is not currently used, but is included for future expansion
     * @param mmbase the MMBase instance
     * @param A Datadir (as a string ending in a /) which may be used in some URL's (most noticably those of HSQLDB). Can be <code>null</code> if not used.
     * @throws StorageInaccessibleException if the JDBC module used in creating the datasource is inaccessible
     */
    GenericDataSource(MMBase mmbase, String dataDir) throws StorageInaccessibleException {
        jdbc = (JDBC) Module.getModule("JDBC", true);
        if (jdbc == null) {
            throw new StorageInaccessibleException("Cannot load Datasource or JDBC Module");
        }
        this.dataDir = dataDir == null ? "" : dataDir;
        meta = false;
    }
    /**
     */
    GenericDataSource(MMBase mmbase) throws StorageInaccessibleException {
        jdbc = (JDBC) Module.getModule("JDBC", false);
        if (jdbc == null) {
            throw new StorageInaccessibleException("Cannot load Datasource or JDBC Module");
        }
        dataDir = null;
        meta = true;
    }

    // see javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        String url = makeUrl();
        if (log.isTraceEnabled()) {
            log.trace("Getting " + (meta ? "META " : "") + "connection for " + url);
        }
        if (meta) {
            String name     = jdbc.getInitParameter("user");
            String password = jdbc.getInitParameter("password");
            if (name.equals("url") && password.equals("url")) {
                return DriverManager.getConnection(url);
            } else {
                return DriverManager.getConnection(url, name, password);
            }
        } else {
            return jdbc.getConnection(url);
        }
    }
    /**
     * @since MMBase-1.8
     */
    public Connection getDirectConnection() throws SQLException {
        String url = makeUrl();
        return jdbc.getDirectConnection(url);
    }

    // see javax.sql.DataSource
    public Connection getConnection(String userName, String password) throws SQLException {
        String url = makeUrl();
        if (log.isDebugEnabled()) {
            log.trace("Getting " + (meta ? "META " : "") + "connection for " + url);
        }
        if (meta) {
            return DriverManager.getConnection(url, userName, password);
        } else {
            return jdbc.getConnection(url, userName, password);
        }
    }

    // see javax.sql.DataSource
    public int getLoginTimeout() {
        return 0;
    }

    // see javax.sql.DataSource
    public java.io.PrintWriter getLogWriter() {
        return printWriter;
    }

    /**
     * {@inheritDoc}
     * Note: currently this code does not actually change the timeout. Login timeout is not implemented by JDBC module
     */
    public void setLoginTimeout(int seconds) {
        // loginTimeout = seconds;
    }


    // see javax.sql.DataSource
    public void setLogWriter(java.io.PrintWriter out) {
        printWriter = out;
    }

    /**
     * Makes URL which can be used to produce a Conncetion. If this is a 'meta' datasource, then
     * 'lookup.xml' will be tried, for a 'meta url'.
     * @since MMBase-1.8
     */
    protected String makeUrl() {
        if (meta) {
            DatabaseStorageLookup lookup = new DatabaseStorageLookup();
            try {
                String metaUrl = lookup.getMetaURL(Class.forName(jdbc.getInitParameter("driver")));
                if (metaUrl != null) {
                    String database = jdbc.getInitParameter("database");
                    if (database != null) {
                        metaUrl = metaUrl.replaceAll("\\$DBM", database);
                    }
                    String host = jdbc.getInitParameter("host");
                    if (host != null) {
                        metaUrl = metaUrl.replaceAll("\\$HOST", host);
                    }
                    String port = jdbc.getInitParameter("port");
                    if (port != null) {
                        metaUrl = metaUrl.replaceAll("\\$PORT", port);
                    }
                    return metaUrl;
                }
            } catch (ClassNotFoundException cnfe) {
                log.error(cnfe);
            }
        }
        String url = jdbc.makeUrl();
        String newUrl = url.replaceAll("\\$DATADIR", dataDir);
        if ((!basePathOk) && (! newUrl.equals(url))) {
            basePathOk = DatabaseStorageManagerFactory.checkBinaryFileBasePath(dataDir);
        }
        return newUrl;
    }

    //untested
    public boolean isWrapperFor(Class iface) {
        return iface.isAssignableFrom(JDBC.class);
    }
    //untested
    public Object unwrap(Class iface) {
        return jdbc;
    }



}
