/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;
import java.util.*;


import javax.sql.DataSource;
import java.io.*;
import javax.servlet.ServletContext;
import java.text.*;


import org.mmbase.module.core.MMBaseContext;
import org.mmbase.storage.*;
import org.mmbase.storage.search.implementation.database.*;
import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.storage.util.StorageReader;
import org.mmbase.util.logging.*;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.UtilReader;
import org.xml.sax.InputSource;

/**
 * A storage manager factory for database storages.
 * This factory sets up a datasource for connecting to the database.
 * If you specify the datasource URI in the 'datasource' property in mmbaseroot.xml configuration file,
 * the factory attempts to obtain the datasource from the application server. If this fails, or no datasource URI is given,
 * it attempts to use the connectivity offered by the JDBC Module, which is then wrapped in a datasource.
 * Note that if you provide a datasource you should make the JDBC Module inactive to prevent the module from
 * interfering with the storage layer.
 * @todo backward compatibility with the old supportclasses should be done by creating a separate Factory
 * (LegacyStorageManagerFactory ?).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */
public class DatabaseStorageManagerFactory extends StorageManagerFactory<DatabaseStorageManager> {

    private static final Logger log = Logging.getLoggerInstance(DatabaseStorageManagerFactory.class);

    // standard sql reserved words
    private final static String[] STANDARD_SQL_KEYWORDS =
    {"absolute","action","add","all","allocate","alter","and","any","are","as","asc","assertion","at","authorization","avg","begin","between","bit","bit_length",
     "both","by","cascade","cascaded","case","cast","catalog","char","character","char_length","character_length","check","close","coalesce","collate","collation",
     "column","commit","connect","connection","constraint","constraints","continue","convert","corresponding","count","create","cross","current","current_date",
     "current_time","current_timestamp","current_user","cursor","date","day","deallocate","dec","decimal","declare","default","deferrable","deferred","delete",
     "desc","describe","descriptor","diagnostics","disconnect","distinct","domain","double","drop","else","end","end-exec","escape","except","exception","exec",
     "execute","exists","external","extract","false","fetch","first","float","for","foreign","found","from","full","get","global","go","goto","grant","group","having","hour",
     "identity","immediate","in","indicator","initially","inner","input","insensitive","insert","int","integer","intersect","interval","into","is","isolation","join",
     "key","language","last","leading","left","level","like","local","lower","match","max","min","minute","module","month","names","national","natural","nchar","next","no",
     "not","null","nullif","numeric","octet_length","of","on","only","open","option","or","order","outer","output","overlaps","pad","partial","position","precision",
     "prepare","preserve","primary","prior","privileges","procedure","public","read","real","references","relative","restrict","revoke","right","rollback","rows",
     "schema","scroll","second","section","select","session","session_user","set","size","smallint","some","space","sql","sqlcode","sqlerror","sqlstate","substring",
     "sum","system_user","table","temporary","then","time","timestamp","timezone_hour","timezone_minute","to","trailing","transaction","translate","translation",
     "trim","true","union","unique","unknown","update","upper","usage","user","using","value","values","varchar","varying","view","when","whenever","where","with","work",
     "write","year","zone"};


    // Default query handler class.
    private final static Class DEFAULT_QUERY_HANDLER_CLASS = org.mmbase.storage.search.implementation.database.BasicSqlHandler.class;

    // Default storage manager class
    private final static Class DEFAULT_STORAGE_MANAGER_CLASS = org.mmbase.storage.implementation.database.RelationalDatabaseStorageManager.class;

    /**
     * The catalog used by this storage.
     */
    protected String catalog = null;
    private   String databaseName = null;
    /**
     * The datasource in use by this factory.
     * The datasource is retrieved either from the application server, or by wrapping the JDBC Module in a generic datasource.
     */
    protected DataSource dataSource;

    /**
     * The transaction isolation level available for this storage.
     * Default TRANSACTION_NONE (no transaction support).
     * The actual value is determined from the database metadata.
     */
    protected int transactionIsolation = Connection.TRANSACTION_NONE;

    /**
     * Whether transactions and rollback are supported by this database
     */
    protected boolean supportsTransactions = false;


    private static final File BASE_PATH_UNSET = new File("UNSET");
    /**
     * Used by #getBinaryFileBasePath
     */
    private File basePath = BASE_PATH_UNSET;


    /**
     * @since MMBase-1.9.1
     */
    static final long MS = 1000000; // 1 ms in ns
    private long debugDuration   = 50      * MS;
    private long serviceDuration = 100     * MS;
    private long infoDuration    = 500     * MS;
    private long warnDuration    = 2000    * MS;
    private long errorDuration   = 5000    * MS;
    private long fatalDuration   = 1000000 * MS;
    private String durationFormat = "0.00";

    final UtilReader.PropertiesMap<String> utilProperties =  new UtilReader("querylogging.xml",
                                                                            new Runnable() {public void run() {readDurations(); }}).getProperties();
    private void readDurations() {
        debugDuration     = new Float(Float.parseFloat(utilProperties.getProperty("debug",   "" + debugDuration)) * MS).longValue();
        serviceDuration   = new Float(Float.parseFloat(utilProperties.getProperty("service", "" + serviceDuration)) * MS).longValue();
        infoDuration      = new Float(Float.parseFloat(utilProperties.getProperty("info",    "" + infoDuration)) * MS).longValue();
        warnDuration      = new Float(Float.parseFloat(utilProperties.getProperty("warn",    "" + warnDuration)) * MS).longValue();
        errorDuration     = new Float(Float.parseFloat(utilProperties.getProperty("error",   "" + errorDuration)) * MS).longValue();
        fatalDuration     = new Float(Float.parseFloat(utilProperties.getProperty("fatal",   "" + fatalDuration)) * MS).longValue();

        durationFormat     = utilProperties.getProperty("durationFormat", durationFormat);
    }
    {
        readDurations();
    }

    public double getVersion() {
        return 0.1;
    }

    public boolean supportsTransactions() {
        return supportsTransactions;
    }

    public String getCatalog() {
        return catalog;
    }

    // this is more or less common
    private static final java.util.regex.Pattern JDBC_URL_DB = java.util.regex.Pattern.compile("(?i)jdbc:.*;.*DatabaseName=([^;]+?)");

    // this too
    private static final java.util.regex.Pattern JDBC_URL    = java.util.regex.Pattern.compile("(?i)jdbc:.*:(?:.*[/@])?(.*?)(?:[;\\?].*)?");

    private static String getDatabaseName(String url) {
        if (url == null) return null;
        java.util.regex.Matcher matcher = JDBC_URL_DB.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        matcher = JDBC_URL.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Doing some best effort to get a 'database name'.
     * @since MMBase-1.8
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Returns the DataSource associated with this factory.
     * @since MMBase-1.8
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param binaryFileBasePath
     *            For some datasource a file base path may be needed (some configurations of hsql).
     *            It can be <code>null</code> during bootstrap. In lookup.xml an alternative URL
     *            may be configured then which does not need the file base path.
     * @since MMBase-1.8
     */
    protected DataSource createDataSource(File binaryFileBasePath) {
        DataSource ds = mmbase.getDataSource();

        if (ds == null) {
            log.service("No data-source configured, using Generic data source");
            // if no datasource is provided, try to obtain the generic datasource (which uses JDBC Module)
            // This datasource should only be needed in cases were MMBase runs without application server.
            if (binaryFileBasePath == null) {
                ds = new GenericDataSource(mmbase); // one argument version triggers also 'meta' mode, which in case of hsql may give another mem-only URL (just for the meta-data).
            } else {
                ds = new GenericDataSource(mmbase, binaryFileBasePath);
            }
        }
        //ds.setLogWriter(new LoggerPrintWriter(Logging.getInstance("datasource"));
        return ds;

    }




    /**
     * Opens and reads the storage configuration document.
     * Obtain a datasource to the storage, and load configuration attributes.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected synchronized void load() throws StorageException {
        // default storagemanager class
        storageManagerClass = DEFAULT_STORAGE_MANAGER_CLASS;

        // default searchquery handler class
        queryHandlerClasses.add(DEFAULT_QUERY_HANDLER_CLASS);



        dataSource = createDataSource(null);
        // temporary source only used once, for the meta data.




        String sqlKeywords;

        // test the datasource and retrieves options,
        // which are stored as options in the factory's attribute
        // this allows for easy retrieval of database options
        {
            Connection con = null;
            try {
                con = dataSource.getConnection();
                if (con == null) throw new StorageException("Did get 'null' connection from data source " + dataSource);
                catalog = con.getCatalog();
                log.service("Connecting to catalog with name " + catalog);

                DatabaseMetaData metaData = con.getMetaData();
                String url = metaData.getURL();
                String db = getDatabaseName(url);
                if (db != null) {
                    databaseName = db;
                } else {
                    log.service("No db found in database connection meta data URL '" + url + "'");
                    databaseName = catalog;
                }
                log.service("Connecting to database with name " + getDatabaseName());

                // set transaction options
                supportsTransactions = metaData.supportsTransactions() && metaData.supportsMultipleTransactions();

                // determine transactionlevels
                if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
                    transactionIsolation = Connection.TRANSACTION_SERIALIZABLE;
                } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ)) {
                    transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ;
                } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED)) {
                    transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
                } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED)) {
                    transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED;
                } else {
                    supportsTransactions = false;
                }
                sqlKeywords = ("" + metaData.getSQLKeywords()).toLowerCase();

            } catch (SQLException se) {
                // log.fatal(se.getMessage() + Logging.stackTrace(se)); will be logged in StorageManagerFactory already
                throw new StorageInaccessibleException(se);
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException se) {
                        log.error(se);
                    }
                }
            }
        }


        // why is this not stored in real properties?

        setOption(Attributes.SUPPORTS_TRANSACTIONS, supportsTransactions);
        setAttribute(Attributes.TRANSACTION_ISOLATION_LEVEL, transactionIsolation);
        setOption(Attributes.SUPPORTS_COMPOSITE_INDEX, true);
        setOption(Attributes.SUPPORTS_DATA_DEFINITION, true);

        for (String element : STANDARD_SQL_KEYWORDS) {
            disallowedFields.put(element, null); // during super.load, the null values will be replaced by actual replace-values.
        }

        // get the extra reserved sql keywords (according to the JDBC driver)
        // not sure what case these are in ???
        StringTokenizer tokens = new StringTokenizer(sqlKeywords,", ");
        while (tokens.hasMoreTokens()) {
            String tok = tokens.nextToken();
            disallowedFields.put(tok, null);
        }


        // load configuration data (is also needing the temprary datasource in getDocumentReader..)
        super.load();
        log.service("Now creating the real data source");
        dataSource = createDataSource(getBinaryFileBasePath(false));

        // store the datasource as an attribute
        // mm: WTF. This seems to be a rather essential property, so why it is not wrapped by a normal, comprehensible getDataSource method or so.
        setAttribute(Attributes.DATA_SOURCE, dataSource);

        // determine transaction support again (may be manually switched off)
        supportsTransactions = hasOption(Attributes.SUPPORTS_TRANSACTIONS);
    }

    /**
     * {@inheritDoc}
     * MMBase determine it using information gained from the datasource, and the lookup.xml file
     * in the database configuration directory
     * Storage configuration files should become resource files, and configurable using a storageresource property.
     * The type of reader to return should be a StorageReader.
     * @throws StorageException if the storage could not be accessed while determining the database type
     * @return a StorageReader instance
     */
    public StorageReader getDocumentReader() throws StorageException {
        StorageReader reader = super.getDocumentReader();
        // if no storage reader configuration has been specified, auto-detect
        if (reader == null) {
            String databaseResourcePath;
            // First, determine the database name from the parameter set in mmbaseroot
            String databaseName = mmbase.getInitParameter("database");
            if (databaseName != null && ! "".equals(databaseName)) {
                // if databasename is specified, attempt to use the database resource of that name
                if (databaseName.endsWith(".xml")) {
                    databaseResourcePath = databaseName;
                } else {
                    databaseResourcePath = "storage/databases/" + databaseName + ".xml";
                }
                log.service("Using " + databaseResourcePath + " as database configuration file");
            } else {
                // WTF to configure storage, we need a connection already?!

                // otherwise, search for supported drivers using the lookup xml
                DatabaseStorageLookup lookup = new DatabaseStorageLookup();
                Connection con = null;
                try {
                    con = dataSource.getConnection();
                    DatabaseMetaData metaData = con.getMetaData();
                    databaseResourcePath = lookup.getResourcePath(metaData);
                    if(databaseResourcePath == null) {
                        // TODO: ask the lookup for a string containing all information on which the lookup could verify and display this instead of the classname
                        throw new StorageConfigurationException("No filter found in " + lookup.getSystemId() + " for driver class:" + metaData.getConnection().getClass().getName() + "\n");
                    }
                } catch (SQLException sqle) {
                    throw new StorageInaccessibleException(sqle);
                } finally {
                    // close connection
                    if (con != null) {
                        try { con.close(); } catch (SQLException sqle) {}
                    }
                }
                log.service("Using " + databaseResourcePath + " as database configuration file (from lookup.xml)");
            }
            // get configuration
            java.net.URL url = ResourceLoader.getConfigurationRoot().getResource(databaseResourcePath);
            log.info("Configuration used for database storage: " + url);
            try {
                InputSource in = ResourceLoader.getInputSource(url);
                reader = new StorageReader(this, in);
            } catch (java.io.IOException ioe) {
                throw new StorageConfigurationException(ioe);
            }

        }
        return reader;
    }

    /**
     * As {@link #getBinaryFileBasePath(boolean)} with <code>true</code>
     */
    public File getBinaryFileBasePath() {
        return getBinaryFileBasePath(true);
    }

    /**
     * Returns the base path for 'binary file'
     * @param check If the path is only perhaps needed, you may want to provide 'false' here.
     * @since MMBase-1.8.1
     */
    protected File getBinaryFileBasePath(boolean check) {
        if (basePath == BASE_PATH_UNSET) {
            String path = (String) getAttribute(Attributes.BINARY_FILE_PATH);
            if (path == null || path.equals("")) {
                basePath = mmbase.getDataDir();
            } else {
                MessageFormat mf = new MessageFormat(path);
                basePath = new java.io.File(mf.format(new String[] {mmbase.getDataDir().toString()}));
                if (! basePath.isAbsolute()) {
                    ServletContext sc = MMBaseContext.getServletContext();
                    String absolute = sc != null ? sc.getRealPath("/") + File.separator : null;
                    if (absolute == null) absolute = System.getProperty("user.dir") + File.separator;
                    basePath = new File(absolute + basePath);
                }
            }
            if (basePath == null) {
                log.warn("Cannot determin a Binary File Base Path");
                return null;
            } else {
                log.service("Binary file base path " + basePath);
            }
            if (check) checkBinaryFileBasePath(basePath);
        }
        return basePath;
    }

    /**
     * Tries to ensure that baseDir exists and is writable. Logs error and returns false otherwise.
     * @param baseDir a Directory name
     * @since MMBase-1.8.1
     */
    public static boolean checkBinaryFileBasePath(File baseDir) {
        if (! baseDir.mkdirs() && ! baseDir.exists()) {
            log.error("Cannot create the binary file path " + baseDir);
        }
        if (! baseDir.canWrite()) {
            log.error("Cannot write in the binary file path " + baseDir);
            return false;
        } else {
            return true;
        }
    }

    protected Object instantiateBasicHandler(Class handlerClass) {
        // first handler
        try {
            java.lang.reflect.Constructor constructor = handlerClass.getConstructor();
            SqlHandler sqlHandler = (SqlHandler) constructor.newInstance();
            log.service("Instantiated SqlHandler of type " + handlerClass.getName());
            return sqlHandler;
        } catch (NoSuchMethodException nsme) {
            throw new StorageConfigurationException(nsme);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            throw new StorageConfigurationException(ite);
        } catch (IllegalAccessException iae) {
            throw new StorageConfigurationException(iae);
        } catch (InstantiationException ie) {
            throw new StorageConfigurationException(ie);
        }
    }

    protected Object instantiateChainedHandler(Class handlerClass, Object handler) {
        // Chained handlers
        try {
            java.lang.reflect.Constructor constructor = handlerClass.getConstructor(new Class[] {SqlHandler.class});
            ChainedSqlHandler sqlHandler = (ChainedSqlHandler) constructor.newInstance(new Object[] { handler });
            log.service("Instantiated chained SQLHandler of type " + handlerClass.getName());
            return sqlHandler;
        } catch (NoSuchMethodException nsme) {
            throw new StorageConfigurationException(nsme);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            throw new StorageConfigurationException(ite);
        } catch (IllegalAccessException iae) {
            throw new StorageConfigurationException(iae);
        } catch (InstantiationException ie) {
            throw new StorageConfigurationException(ie);
        }
    }

    protected SearchQueryHandler instantiateQueryHandler(Object data) {
        return new BasicQueryHandler((SqlHandler)data);
    }


    /**
     * @since MMBase-1.9.1
     */
    private String getLogSqlMessage(String sql, long queries,  long time) {
        StringBuilder mes = new StringBuilder();
        mes.append('#');
        mes.append(queries);
        mes.append(" ");
        float t = ((float) time) / MS;
        //DecimalFormat format = new DecimalFormat(durationFormat, DecimalFormatSymbols.getInstance(java.util.Locale.US)); // java 1.6
        DecimalFormat format = new DecimalFormat(durationFormat, new DecimalFormatSymbols(java.util.Locale.US));
        String f = format.format(t);
        for (int i = f.length() ; i < durationFormat.length() + 3; i++) {
            mes.append(' ');
        }
        mes.append(f);
        mes.append(" ms: ").append(sql);
        return mes.toString();
    }

    /**
     * @since MMBase-1.9.1
     */
    private Logger getLogger(String sql) {
        return Logging.getLoggerInstance("org.mmbase.QUERIES." + sql.split(" ", 2)[0].toUpperCase());
    }

    private Logger getStackTraceLogger(String sql) {
        return Logging.getLoggerInstance("org.mmbase.STACK.QUERIES." + sql.split(" ", 2)[0].toUpperCase());
    }

    private long count = 0;

    protected Throwable getTraceException() {
        Throwable ex = new Throwable();
        List<StackTraceElement> result = new ArrayList<StackTraceElement>();
        for (StackTraceElement el : ex.getStackTrace()) {
            if (el.getClassName().startsWith("org.mmbase.") &&
                (! el.getClassName().startsWith("org.mmbase.storage.implementation.database"))) {
                result.add(el);
            }
        }

        ex.setStackTrace(result.toArray(new StackTraceElement[result.size()]));
        return ex;
    }

    /**
     * @since MMBase-1.9.1
     */
    protected void logQuery(String query, long duration) {
        count++;
        Logger qlog = getLogger(query);
        Logger slog = getStackTraceLogger(query);
        if (duration < debugDuration) {
            if (qlog.isTraceEnabled()) {
                qlog.trace(getLogSqlMessage(query, count, duration));
            }
            if (slog.isTraceEnabled()) {
                slog.trace("trace for #" + count, getTraceException());
            }
        } else if (duration < serviceDuration) {
            if (qlog.isDebugEnabled()) {
                qlog.debug(getLogSqlMessage(query, count, duration));
            }
            if (slog.isDebugEnabled()) {
                slog.debug("trace for #" + count, getTraceException());
            }
        } else if (duration < infoDuration) {
            if (qlog.isServiceEnabled()) {
                qlog.service(getLogSqlMessage(query, count, duration));
            }
            if (slog.isServiceEnabled()) {
                slog.service("trace for #" + count, getTraceException());
            }
        } else if (duration < warnDuration) {
            qlog.info(getLogSqlMessage(query, count, duration));
            slog.info("trace for #" + count, getTraceException());
        } else if (duration < errorDuration) {
            qlog.warn(getLogSqlMessage(query, count, duration));
            slog.warn("trace for #" + count, getTraceException());
        } else if (duration < fatalDuration) {
            qlog.error(getLogSqlMessage(query, count, duration));
            slog.error("trace for #" + count, getTraceException());
        } else {
            qlog.fatal(getLogSqlMessage(query, count, duration));
            slog.fatal("trace for #" + count, getTraceException());
        }
    }



    public static void main(String[] args) {
        String u = "jdbc:hsql:test;test=b";
        if (args.length > 0) u = args[0];
        System.out.println("Database " + getDatabaseName(u));
    }
}


