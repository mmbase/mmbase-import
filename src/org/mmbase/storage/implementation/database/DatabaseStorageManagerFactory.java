/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;
import java.util.StringTokenizer;

import javax.naming.*;
import javax.sql.DataSource;
import java.io.File;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.storage.*;
import org.mmbase.storage.search.implementation.database.*;
import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.storage.util.StorageReader;
import org.mmbase.util.logging.*;
import org.mmbase.util.ResourceLoader;
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
 * @version $Id: DatabaseStorageManagerFactory.java,v 1.27 2005-12-14 10:52:12 ernst Exp $
 */
public class DatabaseStorageManagerFactory extends StorageManagerFactory {

    private static final Logger log = Logging.getLoggerInstance(DatabaseStorageManagerFactory.class);

    // standard sql reserved words
    private final static String STANDARD_SQL_KEYWORDS =
      "absolute,action,add,all,allocate,alter,and,any,are,as,asc,assertion,at,authorization,avg,begin,between,bit,bit_length,"+
      "both,by,cascade,cascaded,case,cast,catalog,char,character,char_length,character_length,check,close,coalesce,collate,collation,"+
      "column,commit,connect,connection,constraint,constraints,continue,convert,corresponding,count,create,cross,current,current_date,"+
      "current_time,current_timestamp,current_user,cursor,date,day,deallocate,dec,decimal,declare,default,deferrable,deferred,delete,"+
      "desc,describe,descriptor,diagnostics,disconnect,distinct,domain,double,drop,else,end,end-exec,escape,except,exception,exec,"+
      "execute,exists,external,extract,false,fetch,first,float,for,foreign,found,from,full,get,global,go,goto,grant,group,having,hour,"+
      "identity,immediate,in,indicator,initially,inner,input,insensitive,insert,int,integer,intersect,interval,into,is,isolation,join,"+
      "key,language,last,leading,left,level,like,local,lower,match,max,min,minute,module,month,names,national,natural,nchar,next,no,"+
      "not,null,nullif,numeric,octet_length,of,on,only,open,option,or,order,outer,output,overlaps,pad,partial,position,precision,"+
      "prepare,preserve,primary,prior,privileges,procedure,public,read,real,references,relative,restrict,revoke,right,rollback,rows,"+
      "schema,scroll,second,section,select,session,session_user,set,size,smallint,some,space,sql,sqlcode,sqlerror,sqlstate,substring,"+
      "sum,system_user,table,temporary,then,time,timestamp,timezone_hour,timezone_minute,to,trailing,transaction,translate,translation,"+
      "trim,true,union,unique,unknown,update,upper,usage,user,using,value,values,varchar,varying,view,when,whenever,where,with,work,"+
      "write,year,zone";

    // Default query handler class.
    private final static Class DEFAULT_QUERY_HANDLER_CLASS =
        org.mmbase.storage.search.implementation.database.BasicSqlHandler.class;

    // Default storage manager class
    private final static Class DEFAULT_STORAGE_MANAGER_CLASS =
        org.mmbase.storage.implementation.database.RelationalDatabaseStorageManager.class;

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


    /**
     * Used by #getBinaryFileBasePath
     */
    private String basePath = null;

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
    private static final java.util.regex.Pattern JDBC_URL    = java.util.regex.Pattern.compile("(?i)jdbc:.*:(?:.*[/@])?(.*?)(?:;.*)?");

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
     * Opens and reads the storage configuration document.
     * Obtain a datasource to the storage, and load configuration attributes.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected void load() throws StorageException {
        // default storagemanager class
        storageManagerClass = DEFAULT_STORAGE_MANAGER_CLASS;

        // default searchquery handler class
        queryHandlerClasses.add(DEFAULT_QUERY_HANDLER_CLASS);


        
        // get the Datasource for the database to use
        // the datasource uri (i.e. 'jdbc/xa/MMBase' )
        // is stored in the mmbaseroot module configuration file
        String dataSourceURI = mmbase.getInitParameter("datasource");
        if (dataSourceURI != null) {
            try {
                String contextName = mmbase.getInitParameter("datasource-context");
                if (contextName == null) {
                    contextName = "java:comp/env";
                }
                log.service("Using configured datasource " + dataSourceURI);
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup(contextName);
                dataSource = (DataSource)environmentContext.lookup(dataSourceURI);
            } catch(NamingException ne) {
                log.warn("Datasource '" + dataSourceURI + "' not available. (" + ne.getMessage() + "). Attempt to use JDBC Module to access database.");
            }
        }
        if (dataSource == null) {
            log.service("No data-source configured, using Generic data source");
            // if no datasource is provided, try to obtain the generic datasource (which uses JDBC Module)
            // This datasource should only be needed in cases were MMBase runs without application server.
            dataSource = new GenericDataSource(mmbase, null);
        }

        
        // store the datasource as an attribute
        setAttribute(Attributes.DATA_SOURCE, dataSource);
        
//      load configuration data. 
        super.load();
        
        //now we can set the data dir for blobs if we have a generic data source
        getBinaryFileBasePath();
        if(dataSource instanceof GenericDataSource){
            ((GenericDataSource)dataSource).setDataDir(basePath);
            log.service("Set Generic datasource blob-path to: " + basePath);
        }else{
            log.service("Data source is not GenericDataSource. datapath: " + basePath);
        }

        // test the datasource and retrieves options,
        // which are stored as options in the factory's attribute
        // this allows for easy retrieval of database options
        try {
            Connection con = dataSource.getConnection();
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

            setOption(Attributes.SUPPORTS_TRANSACTIONS, supportsTransactions);
            setAttribute(Attributes.TRANSACTION_ISOLATION_LEVEL, new Integer(transactionIsolation));
            setOption(Attributes.SUPPORTS_COMPOSITE_INDEX, true);
            setOption(Attributes.SUPPORTS_DATA_DEFINITION, true);

            // create a default disallowedfields list:
            // get the standard sql keywords
            StringTokenizer tokens = new StringTokenizer(STANDARD_SQL_KEYWORDS,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok,null);
            }

            // get the extra reserved sql keywords (according to the JDBC driver)
            // not sure what case these are in ???
            String sqlKeywords = (""+metaData.getSQLKeywords()).toLowerCase();
            tokens = new StringTokenizer(sqlKeywords,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok,null);
            }

            con.close();
        } catch (SQLException se) {
            // log.fatal(se.getMessage() + Logging.stackTrace(se)); will be logged in StorageManagerFactory already
            throw new StorageInaccessibleException(se);
        }

        // load configuration data.
        //super.load();

        // determine transaction support again (may be manually switched off)
        supportsTransactions = hasOption(Attributes.SUPPORTS_TRANSACTIONS);
    }

    /**
     * Locates and opens the storage configuration document.
     * The configuration document to open is dependent on the storage type and version.
     * You can explicitly set this type in mmbaseroot (using the storage property), or let
     * MMBase determine it using information gained from the datasource, and the lookup.xml file
     * in the database configuration directory
     * @todo configuration path should be retrieved from the MMBase instance, rather than directly from the (static)
     * MMBaseContext class.
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
            if (databaseName != null) {
                // if databasename is specified, attempt to use the database resource of that name
                if (databaseName.endsWith(".xml")) {
                    databaseResourcePath = databaseName;
                } else {
                    databaseResourcePath = "storage/databases/" + databaseName + ".xml";
                }
            } else {
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
            }
            // get configuration
            log.service("Use for storage configuration :" + databaseResourcePath);
            try {
                InputSource in = ResourceLoader.getConfigurationRoot().getInputSource(databaseResourcePath);
                reader = new StorageReader(this, in);
            } catch (java.io.IOException ioe) {
                throw new StorageConfigurationException(ioe);
            }

        }
        return reader;
    }


    /**
     * Returns the base path for 'binary file'
     */
    protected String getBinaryFileBasePath() {
        if (basePath == null) {
            basePath = (String) getAttribute(Attributes.BINARY_FILE_PATH);
            if (basePath == null || basePath.equals("")) {
                if (MMBaseContext.getServletContext() != null) {
                    basePath = MMBaseContext.getServletContext().getRealPath("/WEB-INF/data");
                } else {
                    basePath = System.getProperty("user.dir") + File.separator + "data";
                }
            } else {
                java.io.File baseFile = new java.io.File(basePath);
                if (! baseFile.isAbsolute()) {
                    if (MMBaseContext.getServletContext() != null) {
                        basePath = MMBaseContext.getServletContext().getRealPath("/") + File.separator + basePath;
                    } else {
                        basePath = System.getProperty("user.dir") + File.separator + basePath;
                    }
                }
            }
        }
        return basePath;
    }

    protected Object instantiateBasicHandler(Class handlerClass) {
        // first handler
        try {
            java.lang.reflect.Constructor constructor = handlerClass.getConstructor(new Class[] {});
            SqlHandler sqlHandler = (SqlHandler) constructor.newInstance( new Object[] {} );
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


    public static void main(String[] args) {
        String u = "jdbc:hsql:test;test=b";
        if (args.length > 0) u = args[0];
        System.out.println("Database " + getDatabaseName(u));
    }
}


