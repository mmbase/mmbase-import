/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database;

import java.util.*;
import java.sql.*;
import java.sql.DriverManager;

import org.mmbase.util.*;
import org.mmbase.module.*;

import org.mmbase.util.logging.*;

/**
 * JDBC Module.
 * The module that provides you access to the loaded JDBC interfaces.
 * We use this as the base to get multiplexes/pooled JDBC connects.
 *
 * @deprecation-used drop reference to {@link JDBCInterface}
 * @author vpro
 * @version $Id: JDBC.java,v 1.45 2005-12-23 14:56:39 michiel Exp $
 */
public class JDBC extends ProcessorModule implements JDBCInterface {

    private static final Logger log = Logging.getLoggerInstance(JDBC.class);

    private Class  classdriver;
    private Driver driver;
    private String jdbcDriver;
    private String jdbcURL;
    private String jdbcHost;
    private int  jdbcPort = -1;
    private int maxConnections;
    private int maxQueries;
    private String jdbcDatabase;
    private String databasesupportclass;
    private DatabaseSupport databasesupport;
    private MultiPoolHandler poolHandler;
    private JDBCProbe probe = null;
    private String defaultname;
    private String defaultpassword;
    private long probeTime;
    private long maxLifeTime = 120000;

    {
        addFunction(new GetNodeListFunction("POOLS", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("CONNECTIONS", PARAMS_PAGEINFO));
    }    

    public void onload() {
        getProps();
        getDriver();
        loadSupport();
        poolHandler = new MultiPoolHandler(databasesupport,maxConnections,maxQueries);
    }

    /*
     * Initialize the properties and get the driver used
     */
    public void init() {
        // This is now called in onload(), which is called before init()
        // getProps();
        probe = new JDBCProbe(this, probeTime);
        log.info("Module JDBC started (" + this + ")");

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
        poolHandler.shutdown();
    }

    /**
     * Get the driver as specified in our properties
     */
    private void getDriver() {
        Driver d;

        driver = null;
        try {
            classdriver = Class.forName(jdbcDriver);

            // marmaa@vpro.nl:
            // This is how McKoi's JDBC drivers wants itself
            // to be registered; should have no effect on other drivers
            Class.forName(jdbcDriver).newInstance();

            log.info("Loaded JDBC driver: " + jdbcDriver);

        } catch (Exception e) {
            log.fatal("JDBC driver not found: " + jdbcDriver + "\n" + Logging.stackTrace(e));
        }

        log.debug("makeUrl(): " + makeUrl());

        /* Also get the instance to unload it later */
        for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
            d = (Driver)e.nextElement();
            log.debug("Driver " + d);
            if (classdriver == d.getClass()) {
                driver = d;
                break;
            }
        }
        if (driver == null) {
            log.warn("getDriver(): the jdbc driver specified in jdbc.xml does not match the actual loaded driver ");
        }
    }

    /**
     * Get the driver as specified in our properties
     */
    private void loadSupport() {
        Class cl;

        try {
            cl=Class.forName(databasesupportclass);
            databasesupport=(DatabaseSupport)cl.newInstance();
            databasesupport.init();
            log.debug("Loaded load class : "+databasesupportclass);
        } catch (Exception e) {
            log.error("Can't load class : "+databasesupportclass+"\n"+Logging.stackTrace(e));
        }
    }

    /**
     * Get the properties
     */
    private void getProps() {

        jdbcDriver = getInitParameter("driver");
        jdbcURL    = getInitParameter("url");
        jdbcHost   = getInitParameter("host");
        defaultname=getInitParameter("user");
        defaultpassword=getInitParameter("password");
        databasesupportclass=getInitParameter("supportclass");
        probeTime = 30000;
        String tmp = getInitParameter("probetime");
        if (tmp != null) {
            try {
                probeTime = (new Float(tmp)).longValue() * 1000;
                log.info("Set jdbc-probeTime to " + probeTime + " ms");
            } catch (NumberFormatException e) {
                log.warn("Specified probetime is not a invalid float :" + e + "(using default " + (probeTime / 1000) + " s)");
            }
        }


        tmp = getInitParameter("maxlifetime");
        if (tmp != null) {
            try {
                maxLifeTime = (new Float(tmp)).longValue() * 1000;
                log.info("Set jdbc max life time to " + maxLifeTime + " ms");
            } catch (NumberFormatException e) {
                log.warn("Specified max life time is not a invalid float :" + e + "(using default " + (maxLifeTime / 1000) + " s)");
            }
        }

        /*
        log.trace("jdbcDriver="+jdbcDriver +
                  "\njdbcURL="+jdbcURL +
                  "\njdbcHost="+jdbcHost +
                  "\ndefaultname="+defaultname +
                  "\ndefaultpassword="+defaultpassword +
                  "\ndatabasesupportclass="+databasesupportclass);
        */

        if (defaultname==null) {
            defaultname="wwwtech";
            log.warn("name was not set, using default: '" + defaultname +"'");
        }
        if (defaultpassword==null) {
            defaultpassword="xxxxxx";
            log.warn("name was not set, using default: '" + defaultpassword +"'");
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
                log.warn("querys was not set or a invalid integer :" + e + "(using default " + maxQueries + ")");
            }
        }
        jdbcDatabase=getInitParameter("database");
        if (databasesupportclass==null || databasesupportclass.length()==0) {
            databasesupportclass="org.mmbase.module.database.DatabaseSupportShim";
            log.warn("database supportclass was not known, using default: " + databasesupportclass);
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
        String url = jdbcURL;
        // $HOST $DBM $PORT

        url = url.replaceAll("\\$DBM", dbm);
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
        return poolHandler.getConnection(url, defaultname, defaultpassword);
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
        return DriverManager.getConnection(url, defaultname, defaultpassword);
    }

    /**
     * @javadoc
     */
    public synchronized void checkTime() {
        try {
            if (poolHandler != null) poolHandler.checkTime();
        } catch(Exception e) {
            log.error("could not check the time: " + e);
            log.error(Logging.stackTrace(e));
            //e.printStackTrace();
        }
    }

    /**
     * User interface stuff
     * @javadoc
     */
    public Vector getList(PageInfo sp, StringTagger tagger, String value) {
        String line = Strip.DoubleQuote(value,Strip.BOTH);
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
        int i=name.indexOf('?');
        int j=name.indexOf(',');
        if ((i>j) && (j!=-1)) i=j;
        if (i!=-1) {
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
        for (Iterator i = poolHandler.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            MultiPool pool = poolHandler.get(name);
            results.addElement(stripSensistive(name));
            results.addElement(""+pool.getSize());
            results.addElement(""+pool.getTotalConnectionsCreated());
        }
        tagger.setValue("ITEMS","3");
        return results;
    }

    /**
     * @javadoc
     */
    public Vector listConnections(StringTagger tagger) {
        Vector results = new Vector();
        for (Iterator i = poolHandler.keySet().iterator(); i.hasNext();) {
            String name= (String) i.next();
            MultiPool pool = poolHandler.get(name);
            for (Iterator f = pool.getBusyPool(); f.hasNext();) {
                MultiConnection realcon=(MultiConnection)f.next();
                results.addElement(stripSensistive(name.substring(name.lastIndexOf('/')+1)));
                results.addElement(realcon.getStateString());
                results.addElement(""+realcon.getLastSQL());
                results.addElement(""+realcon.getUsage());
                //results.addElement(""+pool.getStatementsCreated(realcon));
            }
            for (Iterator f=pool.getPool();f.hasNext();) {
                MultiConnection realcon=(MultiConnection)f.next();
                results.addElement(stripSensistive(name.substring(name.lastIndexOf('/')+1)));
                results.addElement(realcon.getStateString());
                results.addElement(""+realcon.getLastSQL());
                results.addElement(""+realcon.getUsage());
                //results.addElement(""+pool.getStatementsCreated(realcon));
            }
        }
        tagger.setValue("ITEMS","4");
        return results;
    }

    /**
     * @javadoc
     */
    public String getUser() {
        return defaultname;
    }

    /**
     * @javadoc
     */
    public String getPassword() {
        return defaultpassword;
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
            if (driver == null) return "host: '" + jdbcHost + "' port: '"  + jdbcPort + "' database: '" + jdbcDatabase + "' user: '" + defaultname + "'";
            return "host: '" + jdbcHost + "' port: '"  + jdbcPort + "' database: '" + jdbcDatabase + "' user: '" + defaultname + "' driver: '" + driver.getClass().getName() + "'";
     }
}
