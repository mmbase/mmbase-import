/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;

import java.io.File;
import javax.sql.DataSource;
import java.lang.reflect.*;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.database.JDBC;
import org.mmbase.module.database.MultiConnection;
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

    final private File dataDir;
    final private boolean meta;

    private boolean basePathOk = false;

    /**
     * Constructs a datasource for accessing the database belonging to the given MMBase module.
     * The MMBase parameter is not currently used, but is included for future expansion
     * @param mmbase the MMBase instance
     * @param A Datadir (as a string ending in a /) which may be used in some URL's (most noticably those of HSQLDB). Can be <code>null</code> if not used.
     * @throws StorageInaccessibleException if the JDBC module used in creating the datasource is inaccessible
     */
    GenericDataSource(MMBase mmbase, File dataDir) throws StorageInaccessibleException {
        jdbc = Module.getModule(JDBC.class);
        if (jdbc == null) {
            throw new StorageInaccessibleException("Cannot load Datasource or JDBC Module");
        }
        jdbc.startModule();
        this.dataDir = dataDir;
        meta = false;
    }
    /**
     */
    GenericDataSource(MMBase mmbase) throws StorageInaccessibleException {
        jdbc = Module.getModule(JDBC.class);
        if (jdbc == null) {
            throw new StorageInaccessibleException("Cannot load Datasource or JDBC Module");
        }
        dataDir = null;
        meta = true;
    }

    /**
     * Interesting trick to make things compile in both java 1.5 and 1.6
     */
    public static class ConnectionProxy implements java.lang.reflect.InvocationHandler {

        private final MultiConnection multiCon;

        public static Connection newInstance(MultiConnection multiConnection) {
            return (Connection) java.lang.reflect.Proxy.newProxyInstance(multiConnection.getClass().getClassLoader(),
                                                                         new Class[] {Connection.class, MultiConnection.class},
                                                                         new ConnectionProxy(multiConnection));
        }

        private ConnectionProxy(MultiConnection m) {
            this.multiCon = m;
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            Object result;
            try {

                Method multiMethod = multiCon.getClass().getMethod(m.getName(), m.getParameterTypes());
                result = multiMethod.invoke(multiCon, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (IllegalArgumentException iae) {
                log.service("Probably called unimplemented method " + m + ", falling back to wrapped object. " + iae.getMessage(), iae);
                try {
                    result = m.invoke(multiCon.unwrap(Connection.class), args);
                } catch (Exception e) {
                    throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
            } finally {

            }
            return result;
        }
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
            // why is this
            return ConnectionProxy.newInstance(jdbc.getConnection(url));

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
            return ConnectionProxy.newInstance(jdbc.getConnection(url, userName, password));
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
        String data = "";
        if (dataDir !=null) {
            try {
                data = dataDir.getCanonicalPath();
            } catch (Exception e) {
                log.error(e + " Falling back to " + dataDir);
                data = dataDir.toString();
				    }
        }
        String newUrl = url.replaceAll("\\$DATADIR", data + File.separator);
        if ((!basePathOk) && (! newUrl.equals(url))) {
            basePathOk = DatabaseStorageManagerFactory.checkBinaryFileBasePath(dataDir);
        }
        return newUrl;
    }

    //untested
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isAssignableFrom(JDBC.class);
    }
    //untested
    public <T> T unwrap(Class<T> iface) {
        return (T) jdbc;
    }


}
