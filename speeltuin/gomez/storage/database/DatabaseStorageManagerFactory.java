/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.database.JDBCInterface;
import org.mmbase.util.DatabaseLookup;
import org.mmbase.util.XMLBasicReader;

/**
 * ...
 *
 * basename
 * database
 * datasource
 * storagemanagerfactory
 *
 *
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManagerFactory.java,v 1.1 2003-07-17 13:05:56 pierre Exp $
 */
public class DatabaseStorageManagerFactory extends AbstractStorageManagerFactory implements StorageManagerFactory {

    /**
     * The datasource in use by this factory.
     * The datasource is retrieved either from the application server, or by wrapping the JDBC Module in a generic datasource.
     */
    Protected DataSource datasource;
    
    /**
     * The class used to instantiate storage managers.
     * The classname is retrieved from the database configuration file
     */
    Class storageManagerClass;

    /**
     * Initialize the Factory for this instance of MMBase.
     * @param mmbase the MMBase instance
     */
	public void init(MMBase mmbase) {
        super.init(mmbase);

        // get the Datasource for the database to use
        // the datasource uri (i.e. 'jdbc/xa/PostgresqlXADS' )
        // is stored in the mmbaseroot module configuration file
        String datasourceURI = mmbase.getInitParameter("datasource");
        if (datasourceURI != null) {
            try {
                Context jndiCbtx = new InitialContext();
                datasource = (DataSource)jndiCntx.lookup(datasourceURI);
            } catch(NamingException ne) {
                log.warn("Datasource '"+datasourceURI+"' nota available. ("+ne.getMessage()+"). Attempt to use JDBC Module to access database.");
            }
        }
        if (datasource == null) {
            // if no datasource is provided, try to obtain the generic datasource (which uses JDBC Module)
            // This datasource should only be needed in cases were MMBase runs without application server.
            datasource = new GenericDataSource(mmbase);
        }
        
        // load configuration data.
        load();

        // print information about our storage..
        log.info("Using class: '" + storageManagerClass.getName() + "' with config: '" + databaseConfig + "'.");
    }
    
    /**
     *
     */
    protected load() {
        XMLBasicReader reader = getDocumentReader();
        // determine the storagemanager class
        Class storageManagerClass = Class.forName(reader.getMMBaseDatabaseDriver());
        // ... more
    }

    /**
     *
     */
    public DataSource getDataSource() {
        return datasource;
    }
    
    /**
     *
     */
	public StorageManager getStorageManager() {
        storageManager = (StorageManager)storageManagerClass.newInstance();
        storageManager.setStorageManagerFactory(this);
        return storageManager;
    }

    /**
     *
     */
    public XMLBasicReader getDocumentReader() {
        File databaseConfig = null;
        // configuration path
        String databaseConfigDir = MMBaseContext.getConfigPath() + File.separator + "databases" + File.separator;

        // determine database name.
        // use the parameter set in mmbaseroot if it is given
        String databasename = MMBase.getInitParameter("database");
        if (databasename == null) {
            // otherwise, search for supported drivers using the lookup xml
            //
            DatabaseLookup lookup =
                new DatabaseLookup(new File(databaseConfigDir + "lookup.xml"), new File(databaseConfigDir));
            try {
                databaseConfig = lookup.getDatabaseConfig(datasource.getConnection());
            } catch (java.sql.SQLException sqle) {
                log.error(sqle);
                log.error(Logging.stackTrace(sqle));
                throw new RuntimeException("Error retrieving an connection to the database:" + sqle);
            }
        } else {
            // use the correct database-xml
            databaseConfig = new File(databaseConfigDir + databasename + ".xml");
        }
        // get our config...
        // mostly static so maybe make this a resource?
        return new XMLDatabaseReader(databaseConfig.getPath());
    }

}


