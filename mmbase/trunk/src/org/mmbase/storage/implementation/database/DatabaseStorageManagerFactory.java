/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.io.InputStream;
import java.sql.*;
import java.util.StringTokenizer;

import javax.naming.*;
import javax.sql.DataSource;

import org.mmbase.storage.*;
import org.mmbase.storage.util.StorageReader;
import org.mmbase.util.logging.*;
import org.xml.sax.InputSource;

/**
 * A storage manager factory for database storages.
 * This factory sets up a datasource for connecting to the database.
 * If you specify the datasource URI in the 'dataource' property in mmbaseroot.xml configuration file,
 * the factory attempts to obtain the datasource from the appplication server. If this fails, or no datasource URI is given,
 * It attempts to use the connectivity offered by the JDBC Module,w hcih si then warpped in a datasource.
 * Note that if you provide a datasource you should make the JDBC Module inactive to prevent the module from
 * interfering with the storage layer.
 * @todo backward compatibility with the old supportclasses should be done by creating a separate Factory
 * (LegacyStorageManagerFactory ?).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManagerFactory.java,v 1.9 2003-12-29 16:41:36 nico Exp $
 */
public class DatabaseStorageManagerFactory extends StorageManagerFactory {

    // logger
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

    /** Prefix used to escape disallowed fields */
    private final static String ESCAPE_PREFIX = "m_";
    
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

    public double getVersion() {
        return 0.1;
    }

    public boolean supportsTransactions() {
        return supportsTransactions;
    }

    public String getCatalog() {
        return catalog;
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
        queryHandlerClass = DEFAULT_QUERY_HANDLER_CLASS;

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
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup(contextName);
                dataSource = (DataSource)environmentContext.lookup(dataSourceURI);
            } catch(NamingException ne) {
                log.warn("Datasource '"+dataSourceURI+"' not available. ("+ne.getMessage()+"). Attempt to use JDBC Module to access database.");
            }
        }
        if (dataSource == null) {
            // if no datasource is provided, try to obtain the generic datasource (which uses JDBC Module)
            // This datasource should only be needed in cases were MMBase runs without application server.
            dataSource = new GenericDataSource(mmbase);
        }
        // store the datasource as an attribute
        setAttribute(Attributes.DATA_SOURCE, dataSource);

        // test the datasource and retrieves options,
        // which are stored as options in the factory's attribute
        // this allows for easy retrieval of database options
        try {
            Connection con = dataSource.getConnection();
            catalog = con.getCatalog();
            log.debug("Connecting to catalog with name "+catalog);
            DatabaseMetaData metaData = con.getMetaData();

            // set transaction options
            supportsTransactions = metaData.supportsTransactions() && metaData.supportsMultipleTransactions();
            setOption(Attributes.SUPPORTS_TRANSACTIONS, supportsTransactions);
            setOption(Attributes.SUPPORTS_COMPOSITE_INDEX, true);
            setOption(Attributes.SUPPORTS_DATA_DEFINITION, true);

            // determine transactionlevels
            if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
                transactionIsolation = Connection.TRANSACTION_SERIALIZABLE;
            } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED)) {
                transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
            } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED)) {
                transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED;
            } else if (metaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED)) {
                transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
            }
            setAttribute(Attributes.TRANSACTION_ISOLATION_LEVEL, new Integer(transactionIsolation));

            // create a default disallowedfields list:
            // get the standard sql keywords
            StringTokenizer tokens = new StringTokenizer(STANDARD_SQL_KEYWORDS,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok, ESCAPE_PREFIX + tok);
            }

            // get the extra reserved sql keywords (according to the JDBC driver)
            // not sure what case these are in ???
            String sqlKeywords = (""+metaData.getSQLKeywords()).toLowerCase();
            tokens = new StringTokenizer(sqlKeywords,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok, ESCAPE_PREFIX + tok);
            }

            con.close();
        } catch (SQLException se) {
            // log.fatal(se.getMessage() + Logging.stackTrace(se)); will be logged in StorageManagerFactory already
            throw new StorageInaccessibleException(se);
        }

        // load configuration data.
        super.load();

        // determine transaction support again (may be manually switched off)
        supportsTransactions = hasOption(Attributes.SUPPORTS_TRANSACTIONS);
    }

    /**
     * Locates and opens the storage configuration document.
     * The configuration document to open is dependent on the storage type and version.
     * You can explicitly set this type in mmbasreoot (using the storage property), or let
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
            String databaseName =mmbase.getInitParameter("database");
            if (databaseName != null) {
                // if databasename is specified, attempt to use the database resource of that name
                if (databaseName.startsWith("/")) {
                    databaseResourcePath = databaseName;
                } else {
                    databaseResourcePath = "/org/mmbase/storage/implementation/database/resources/"+databaseName+".xml";
                }
            } else {
                // otherwise, search for supported drivers using the lookup xml
                DatabaseStorageLookup lookup = new DatabaseStorageLookup();
                Connection con = null;
                try {
                    con = dataSource.getConnection();
                    databaseResourcePath = lookup.getResourcePath(con.getMetaData());
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
            log.service("Use for storage configuration :"+databaseResourcePath);
            InputStream stream = DatabaseStorageManagerFactory.class.getResourceAsStream(databaseResourcePath);
            InputSource in = new InputSource(stream);
            reader = new StorageReader(this, in);
        }
        return reader;
    }

}


