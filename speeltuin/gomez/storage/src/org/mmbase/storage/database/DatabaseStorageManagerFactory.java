/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.io.InputStream;
import java.util.StringTokenizer;
import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.xml.sax.InputSource;

import org.mmbase.storage.*;
import org.mmbase.storage.util.StorageReader;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.DatabaseLookup;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
 * @version $Id: DatabaseStorageManagerFactory.java,v 1.12 2003-08-05 11:12:20 pierre Exp $
 */
public class DatabaseStorageManagerFactory extends StorageManagerFactory {

    // logger
    private static Logger log = Logging.getLoggerInstance(DatabaseStorageManagerFactory.class);

    // standard sql reserved words
    private final static String STANDARD_SQL_KEYWORDS =
      "ABSOLUTE,ACTION,ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,AS,ASC,ASSERTION,AT,AUTHORIZATION,AVG,BEGIN,BETWEEN,BIT,BIT_LENGTH,"+
      "BOTH,BY,CASCADE,CASCADED,CASE,CAST,CATALOG,CHAR,CHARACTER,CHAR_LENGTH,CHARACTER_LENGTH,CHECK,CLOSE,COALESCE,COLLATE,COLLATION,"+
      "COLUMN,COMMIT,CONNECT,CONNECTION,CONSTRAINT,CONSTRAINTS,CONTINUE,CONVERT,CORRESPONDING,COUNT,CREATE,CROSS,CURRENT,CURRENT_DATE,"+
      "CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DATE,DAY,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFAULT,DEFERRABLE,DEFERRED,DELETE,"+
      "DESC,DESCRIBE,DESCRIPTOR,DIAGNOSTICS,DISCONNECT,DISTINCT,DOMAIN,DOUBLE,DROP,ELSE,END,END-EXEC,ESCAPE,EXCEPT,EXCEPTION,EXEC,"+
      "EXECUTE,EXISTS,EXTERNAL,EXTRACT,FALSE,FETCH,FIRST,FLOAT,FOR,FOREIGN,FOUND,FROM,FULL,GET,GLOBAL,GO,GOTO,GRANT,GROUP,HAVING,HOUR,"+
      "IDENTITY,IMMEDIATE,IN,INDICATOR,INITIALLY,INNER,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTERVAL,INTO,IS,ISOLATION,JOIN,"+
      "KEY,LANGUAGE,LAST,LEADING,LEFT,LEVEL,LIKE,LOCAL,LOWER,MATCH,MAX,MIN,MINUTE,MODULE,MONTH,NAMES,NATIONAL,NATURAL,NCHAR,NEXT,NO,"+
      "NOT,NULL,NULLIF,NUMERIC,OCTET_LENGTH,OF,ON,ONLY,OPEN,OPTION,OR,ORDER,OUTER,OUTPUT,OVERLAPS,PAD,PARTIAL,POSITION,PRECISION,"+
      "PREPARE,PRESERVE,PRIMARY,PRIOR,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,RELATIVE,RESTRICT,REVOKE,RIGHT,ROLLBACK,ROWS,"+
      "SCHEMA,SCROLL,SECOND,SECTION,SELECT,SESSION,SESSION_USER,SET,SIZE,SMALLINT,SOME,SPACE,SQL,SQLCODE,SQLERROR,SQLSTATE,SUBSTRING,"+
      "SUM,SYSTEM_USER,TABLE,TEMPORARY,THEN,TIME,TIMESTAMP,TIMEZONE_HOUR,TIMEZONE_MINUTE,TO,TRAILING,TRANSACTION,TRANSLATE,TRANSLATION,"+
      "TRIM,TRUE,UNION,UNIQUE,UNKNOWN,UPDATE,UPPER,USAGE,USER,USING,VALUE,VALUES,VARCHAR,VARYING,VIEW,WHEN,WHENEVER,WHERE,WITH,WORK,"+
      "WRITE,YEAR,ZONE";
    
    // Default query handler class.
    private final static Class DEFAULT_QUERY_HANDLER_CLASS =
        org.mmbase.storage.search.implementation.database.BasicSqlHandler.class;
    
    // Default storage manager class
    private final static Class DEFAULT_STORAGE_MANAGER_CLASS =
        org.mmbase.storage.database.RelationalDatabaseStorageManager.class;

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

    /**
     * Opens and reads the storage configuration document.
     * Obtain a datasource to the storage, and load configuration attributes.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected void load() throws StorageException {
        // get the Datasource for the database to use
        // the datasource uri (i.e. 'jdbc/xa/MMBase' )
        // is stored in the mmbaseroot module configuration file
        String dataSourceURI = mmbase.getInitParameter("datasource");
        if (dataSourceURI != null) {
            try {
                Context jndiCntx = new InitialContext();
                dataSource = (DataSource)jndiCntx.lookup(dataSourceURI);
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
        setAttribute("database.dataSource", dataSource);

        // test the datasource and retrieves options, 
        // which are stored as options in the factory's attribute
        // this allows for easy retrieval of database options
        try {
            Connection con = dataSource.getConnection();
            DatabaseMetaData metaData = con.getMetaData();
            // set transaction options
            supportsTransactions = metaData.supportsTransactions() && metaData.supportsMultipleTransactions();
            setOption("database.dataDefinitionCausesTransactionCommit", metaData.dataDefinitionCausesTransactionCommit());
            setOption("database.dataDefinitionIgnoredInTransactions", metaData.dataDefinitionIgnoredInTransactions());
            setOption("database.supportsDataManipulationTransactionsOnly", metaData.supportsDataManipulationTransactionsOnly());
            setOption("database.supportsDataDefinitionAndDataManipulationTransactions", metaData.supportsDataDefinitionAndDataManipulationTransactions());
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
            setAttribute("database.transactionIsolationLevel", new Integer(transactionIsolation));
            // alter table support options
            setOption("database.supportsAlterTableWithAddColumn",metaData.supportsAlterTableWithAddColumn());
            setOption("database.supportsAlterTableWithDropColumn",metaData.supportsAlterTableWithDropColumn());
            // create a default disallowedfields list:
            // get the standard sql keywords
            StringTokenizer tokens = new StringTokenizer(STANDARD_SQL_KEYWORDS,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok,null);
            }
            // get the extra reserved sql keywords (according to the JDBC driver)
            // not sure what case these are in ???
            String sqlKeywords = (""+metaData.getSQLKeywords()).toUpperCase();
            tokens = new StringTokenizer(sqlKeywords,", ");
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                disallowedFields.put(tok,null);
            }
            /* It would theoretically be possible to also create a default typemapping by
               calling metaData.getTypeInfo()
               Were not doing this yet, but maybe something to add for the future
            */
            con.close();
        } catch (SQLException se) {
            throw new StorageInaccessibleException(se);
        }
        
        // default storagemanager class
        queryHandlerClass = DEFAULT_STORAGE_MANAGER_CLASS;

        // default searchquery handler class
        queryHandlerClass = DEFAULT_QUERY_HANDLER_CLASS;
        
        // load configuration data.
        super.load();
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
                // if databsename is specified, use that database resource
                databaseResourcePath = "/org/mmbase/storage/database/resource/"+databaseName+".xml";
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
            InputStream stream = DatabaseStorageManagerFactory.class.getResourceAsStream(databaseResourcePath);
            InputSource in = new InputSource(stream);
            reader = new StorageReader(this, in);
        }
        return reader;
    }
        
}


