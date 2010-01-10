/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * JDBCInterface is _only_ the module JDBC interface who setup the connections
 * it has nothing tofo with the JDBC interface.
 *
 * @duplicate Not really needed. Remove and reference JDBC directly. Note that direct
 *            references to JDBC will be removed from most of the core (only the storagemanagerfactory
 *            will reference the JDBC module directly)
 * @author vpro
 * @version $Id$
 */
public interface JDBCInterface {

    /**
     * @javadoc
     */
    public String makeUrl();
    /**
     * @javadoc
     */
    public String makeUrl(String dbm);
    /**
     * @javadoc
     */
    public String makeUrl(String host, String dbm);
    /**
     * @javadoc
     */
    public String makeUrl(String host, int port, String dbm);

    // JDBC Pools
    /**
     * @javadoc
     */
    public MultiConnection getConnection(String url, String name, String password) throws SQLException;

    /**
     * @javadoc
     */
    public MultiConnection getConnection(String url) throws SQLException;

    /**
     * @javadoc
     */
    public Connection getDirectConnection(String url) throws SQLException;

    /**
     * @javadoc
     */
    public Connection getDirectConnection(String url, String name, String password) throws SQLException;

    /**
     * @javadoc
     */
    public String getUser();

    /**
     * @javadoc
     */
    public String getPassword();

    /**
     * @javadoc
     */
    public String getDatabaseName();

    /**
     * @javadoc
     */
    public void checkTime();
}
