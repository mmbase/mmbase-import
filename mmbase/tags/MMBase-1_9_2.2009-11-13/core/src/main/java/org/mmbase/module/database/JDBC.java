
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database;

import java.util.*;
import java.sql.*;
import java.util.concurrent.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.MMBase;


import org.mmbase.util.logging.*;

/**
 * JDBC Module.
 * The module that provides you access to the loaded JDBC interfaces.
 * We use this as the base to get multiplexes/pooled JDBC connects.
 *
 * @author vpro
 * @version $Id$
 */
public class JDBC extends ProcessorModule {

    private static final Logger log = Logging.getLoggerInstance(JDBC.class);

    private Class  classdriver;
    private Driver driver;
    private String jdbcDriver;
    private String jdbcHost;
    private int  jdbcPort = -1;
    private int maxConnections;
    private int maxQueries;
    private String jdbcDatabase;
    private String databaseSupportClass;
    private DatabaseSupport databaseSupport;
    private MultiPoolHandler poolHandler;
    private String jdbcName;
    private String jdbcPassword;
    private long probeTime;
    private long maxLifeTime = 120000;
    private ScheduledFuture future;

    {
        addFunction(new GetNodeListFunction("POOLS", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("CONNECTIONS", PARAMS_PAGEINFO));
    }

    public JDBC(String name) {
        super(name);
    }

    public void onload() {
        if (Module.getModule(MMBase.class, false).getDataSource() == null) {
            getProps();
            getDriver();
            loadSupport();
            poolHandler = new MultiPoolHandler(databaseSupport, maxConnections, maxQueries);
            poolHandler.setMaxLifeTime(maxLifeTime);
        } else {
            log.service("Not setting up poolhandler, because we will use data-source of application container");
        }
    }

    /*
     * Initialize the properties and get the driver used
     */
    public void init() {
        if (Module.getModule(MMBase.class, false).getDataSource() == null) {

            future = ThreadPools.scheduler.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        JDBC.this.checkTime();
                    }
                },
                probeTime, probeTime, TimeUnit.MILLISECONDS);
            log.info("Module JDBC started (" + this + ")");
            ThreadPools.identify(future, "JDBC Probe");
        } else {
            log.service("Not setting up JDBC Probe, because we will use data-source of application container");
        }

    }

    /**
     * {@inheritDoc}
     * Reload the properties and driver
     */
    public void reload() {
        getProps();

       /* This doesn't work, have to figure out why
        try {
        DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
        debug("reload(): JDBC Module: Can't deregister driver");
        }
         */
        loadSupport();
        getDriver();
    }

    public void unload() {
    }
    protected void shutdown() {
        if (future != null) future.cancel(true);
        if (poolHandler != null) poolHandler.shutdown();
    }

    /**
     * Get the driver as specified in our properties
     */
    private void getDriver() {

        driver = null;
        try {
            classdriver = Class.forName(jdbcDriver);

            // marmaa@vpro.nl:
            // This is how McKoi's JDBC drivers wants itself
            // to be registered; should have no effect on other drivers
            Driver d = (Driver) Class.forName(jdbcDriver).newInstance();
            log.service("Loaded JDBC driver: " + jdbcDriver + " " + d.getMajorVersion() + "." + d.getMinorVersion());

        } catch (Exception e) {
            log.fatal("JDBC driver not found: " + jdbcDriver , e);
        }

        if (log.isDebugEnabled()) {
            log.debug("makeUrl(): " + makeUrl());
        }

        /* Also get the instance to unload it later */
        for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver d = (Driver) e.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Driver " + d);
            }
            if (classdriver == d.getClass()) {
                driver = d;
                break;
            }
        }
        if (driver == null) {
            log.warn("getDriver(): the jdbc driver specified in jdbc.xml '" + jdbcDriver + "' does not match any actually loaded drivers " + Collections.list(DriverManager.getDrivers()));
        }
    }

    /**
     * Get the driver as specified in our properties
     */
    private void loadSupport() {
        try {
            Class cl = Class.forName(databaseSupportClass);
            databaseSupport = (DatabaseSupport)cl.newInstance();
            databaseSupport.init();
            log.debug("Loaded load class : " + databaseSupportClass);
        } catch (Exception e) {
            log.error("Can't load class : " + databaseSupportClass + " " + e.getMessage(), e);
        }
    }

    /**
     * @since MMBase-1.8.5
     */
    public DatabaseSupport getSupport() {
        return databaseSupport;
    }

    /**
     * Get the properties
     */
    private void getProps() {

        jdbcDriver = getInitParameter("driver");
        jdbcHost   = getInitParameter("host");
        jdbcName = getInitParameter("user");
        if (jdbcName == null) {
            jdbcName = "wwwtech";
            log.warn("Name was not set, using default: '" + jdbcName +"'");
        }

        jdbcPassword = getInitParameter("password");

        databaseSupportClass = getInitParameter("supportclass");
        probeTime = 30000;
        String tmp = getInitParameter("probetime");
        if (tmp != null) {
            try {
                probeTime = Float.valueOf(tmp).longValue() * 1000;
                log.service("Set jdbc-probeTime to " + probeTime + " ms");
            } catch (NumberFormatException e) {
                log.warn("Specified probetime is not a invalid float :" + e + "(using default " + (probeTime / 1000) + " s)");
            }
        }


        tmp = getInitParameter("maxlifetime");
        if (tmp != null) {
            try {
                maxLifeTime = (long) (Float.parseFloat(tmp) * 1000f);
                log.service("Set jdbc max life time to " + maxLifeTime + " ms");
            } catch (NumberFormatException e) {
                log.warn("Specified max life time is not a invalid float :" + e + "(using default " + (maxLifeTime / 1000) + " s)");
            }
        }

        tmp = getInitParameter("port");
        if (tmp != null) {
            try {
                jdbcPort=Integer.parseInt(getInitParameter("port"));
            } catch (NumberFormatException e) {
                jdbcPort = 0;
                log.warn("portnumber was not set or a invalid integer :" + e + "(using default " + jdbcPort + ")");
            }
        }
        try {
            maxConnections=Integer.parseInt(getInitParameter("connections"));
        } catch (Exception e) {
            maxConnections = 8;
            log.warn("connections was not set or a invalid integer :" + e + "(using default " + maxConnections + ")");
        }
        try {
            maxQueries = Integer.parseInt(getInitParameter("queries"));
        } catch (Exception f) {
            try {
                maxQueries = Integer.parseInt(getInitParameter("querys")); //fall back backward compatible
            } catch (Exception e) {
                maxQueries = 500;
                log.warn("queries was not set or a invalid integer :" + e + "(using default " + maxQueries + ")");
            }
        }
        jdbcDatabase = getInitParameter("database");
        if (databaseSupportClass == null || databaseSupportClass.length() == 0) {
            databaseSupportClass="org.mmbase.module.database.DatabaseSupportShim";
            log.debug("database supportclass was not known, using default: " + databaseSupportClass);
        }
    }

    /**
     * Routine build the url to give to the DriverManager
     * to open the connection. This way a servlet/module
     * doesn't need to care about what database it talks to.
     * @see java.sql.DriverManager#getConnection(java.lang.String)
     */
    public String makeUrl() {
        return makeUrl(jdbcHost, jdbcPort, jdbcDatabase);
    }

    /**
     * Routine build the url to give to the DriverManager
     * to open the connection. This way a servlet/module
     * doesn't need to care about what database it talks to.
     * @see java.sql.DriverManager#getConnection(java.lang.String)
     */
    public String makeUrl(String dbm) {
        return makeUrl(jdbcHost, jdbcPort, dbm);
    }

    /**
     * Routine build the url to give to the DriverManager
     * to open the connection. This way a servlet/module
     * doesn't need to care about what database it talks to.
     * @see java.sql.DriverManager#getConnection(java.lang.String)
     */
    public String makeUrl(String host,String dbm) {
        return makeUrl(host, jdbcPort, dbm);
    }

    /**
     * Routine build the url to give to the DriverManager
     * to open the connection. This way a servlet/module
     * doesn't need to care about what database it talks to.
     * @see java.sql.DriverManager#getConnection(java.lang.String)
     */
    public String makeUrl(String host, int port, String dbm) {
        String url = getInitParameter("url");
        // $HOST $DBM $PORT
        if (dbm == null) dbm = "mmbase";
        url = url.replaceAll("\\$DBM", dbm);
        if (host == null) host = "localhost";
        url = url.replaceAll("\\$HOST", host);
        url = url.replaceAll("\\$PORT", "" + port);

        return url;
    }

    /**
     * @javadoc
     */
    public MultiConnection getConnection(String url, String name, String password) throws SQLException {
        return poolHandler.getConnection(url, name, password);
    }

    /**
     * @javadoc
     */
    public MultiConnection getConnection(String url) throws SQLException {
        return poolHandler.getConnection(url, jdbcName, jdbcPassword);
    }

    /**
     * @javadoc
     */
    public Connection getDirectConnection(String url,String name,String password) throws SQLException {
        return DriverManager.getConnection(url, name, password);
    }

    /**
     * @javadoc
     */
    public Connection getDirectConnection(String url) throws SQLException {
        return DriverManager.getConnection(url, jdbcName, jdbcPassword);
    }

    /**
     * @javadoc
     */
    public synchronized void checkTime() {
        try {
            if (poolHandler != null) poolHandler.checkTime();
        } catch(Exception e) {
            log.error("could not check the time: " + e, e);
        }
    }

    /**
     * User interface stuff
     * @javadoc
     */
    public Vector getList(PageInfo sp, StringTagger tagger, String value) {
        String line = Strip.doubleQuote(value, Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd = tok.nextToken();
            if (cmd.equals("POOLS")) return listPools(tagger);
            if (cmd.equals("CONNECTIONS")) return listConnections(tagger);
        }
        return null;
    }

    // Strips senssitive info (such as password and username) from the
    // database name
    private String stripSensistive(String name) {
        // strip either after the first '?', or the first ',',
        // whichever comes first
        int i = name.indexOf('?');
        int j = name.indexOf(',');
        if ((i > j) && (j != -1)) i = j;
        if (i !=- 1) {
            return name.substring(0,i);
        } else {
            return name;
        }
    }

    /**
     * @javadoc
     */
    public Vector listPools(StringTagger tagger) {
        Vector results = new Vector();
        if (poolHandler != null) {
            for (String name : poolHandler.keySet()) {
                MultiPool pool = poolHandler.get(name);
                results.addElement(stripSensistive(name));
                results.addElement("" + pool.getSize());
                results.addElement("" + pool.getTotalConnectionsCreated());
            }
        }
        tagger.setValue("ITEMS", "3");
        return results;
    }

    /**
     * @javadoc
     */
    public Vector listConnections(StringTagger tagger) {
        Vector results = new Vector();
        for (String name : poolHandler.keySet()) {
            MultiPool pool = poolHandler.get(name);
            for (Iterator f = pool.getBusyPool(); f.hasNext();) {
                MultiConnection realcon=(MultiConnection)f.next();
                results.addElement(stripSensistive(name.substring(name.lastIndexOf('/')+1)));
                results.addElement(realcon.getStateString());
                results.addElement("" + realcon.getLastSQL());
                results.addElement("" + realcon.getUsage());
                //results.addElement(""+pool.getStatementsCreated(realcon));
            }
            for (Iterator f = pool.getPool();f.hasNext();) {
                MultiConnection realcon=(MultiConnection)f.next();
                results.addElement(stripSensistive(name.substring(name.lastIndexOf('/')+1)));
                results.addElement(realcon.getStateString());
                results.addElement("" + realcon.getLastSQL());
                results.addElement("" + realcon.getUsage());
                //results.addElement(""+pool.getStatementsCreated(realcon));
            }
        }
        tagger.setValue("ITEMS", "4");
        return results;
    }

    /**
     * @javadoc
     */
    public String getUser() {
        return jdbcName;
    }

    /**
     * @javadoc
     */
    public String getPassword() {
        return jdbcPassword;
    }

    /**
     * @javadoc
     */
    public String getDatabaseName() {
        return getInitParameter("database");
    }

    /**
     * Give some info about the jdbc connection
     * @return a <code>String</code> whith some information about the connection
     */
    public String toString() {
        return "host: '" + jdbcHost + "' port: '"  + jdbcPort + "' database: '" + jdbcDatabase + "' user: '" + jdbcName + "'" + (driver != null ? " driver: " + driver.getClass().getName() + "'" : "") + " max life time: " + maxLifeTime + " ms  probe time: " + probeTime + " ms";
     }
}
