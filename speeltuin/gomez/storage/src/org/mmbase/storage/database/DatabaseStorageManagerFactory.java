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
mport org.mmbase.util.XMLBasicReader;

/**
 * A storage manager factory for database storages.
 * This factory sets up a datasource for connecting to the databse.
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
 * @version $Id: DatabaseStorageManagerFactory.java,v 1.1 2003-07-21 09:31:04 pierre Exp $
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

	public double getVersion() {
        return 0.1;
    }

    /**
     * Initialize the Factory for this instance of MMBase.
     * Obtain a datasource to the storage, and load configuration attributes.
     * @see load()
     * @param mmbase the MMBase instance
     */
	public void init(MMBase mmbase) throws StorageConfigurationException, StorageInaccessibleException {
        super.init(mmbase);

        // get the Datasource for the database to use
        // the datasource uri (i.e. 'jdbc/xa/MMBase' )
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
        // test the datasource
        try {
            Connection con = datasource.getConnection();
            con.close();
        } catch (SQLException se) {
            throw new StorageInaccessibleException(se);
        }
        // load configuration data.
        load();
        // print information about our storage..
        log.info("Using class: '" + storageManagerClass.getName() + "' with config: '" + databaseConfig + "'.");
    }
    
    /**
     * Opens and reads the database configuration document.
     * @todo The type of reader used should be a StorageReader.
     * @throws StorageInaccessibleException if the storage could not be accessed while determining the database type
     * @throws StorageConfigurationException if necessary configuration data is missing or invalid 
     */
    protected void load() throws StorageConfigurationException, StorageInaccessibleException {
        StorageReader reader = getDocumentReader();
        storageManagerClass = reader.getStorageManagerClass(this);
        
        // determine the storagemanager classname and load the class
        String storageManagerClassName = Class.forName(reader.getMMBaseDatabaseDriver());
        if (storageManagerClassName==null) {
            throw new StorageConfigurationException("StorageManager class name missing in storage configuration");
        }
        try {
            Class storageManagerClass = Class.forName(storageManagerClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new StorageConfigurationException(cnfe);
        }
        // ... more configuration
        
        
    }

    /**
     * Locates and opens the database configuration document.
     * The configuration document to open is dependent on the database type and version.
     * You can explicitly set this type in mmbasreoot (using the database property), or let
     * MMBase determine it using information gained from the datasource, and the lookup.xml file 
     * in the database configuration directory
     * @todo configuration path should be retrieved from the MMBase instance, rather than directly from the (static)
     * MMBaseContext class.
     * Storage configuration files should become resource files, and configurable using a storageresource property.
     * The type of reader to return should be a StorageReader.
     * @throws StorageInaccessibleException if the storage could not be accessed while determining the database type
     * @return a XMLDatabaseReader instance
     */
    public StorageReader getDocumentReader() throws StorageInaccessibleException {
        File databaseConfig = null;
        // configuration path. 
        String databaseConfigDir = MMBaseContext.getConfigPath() + File.separator + "databases" + File.separator;

        // determine database name.
        // use the parameter set in mmbaseroot if it is given
        String databasename =mmbase.getInitParameter("database");
        if (databasename == null) {
            // otherwise, search for supported drivers using the lookup xml
            DatabaseLookup lookup = new DatabaseLookup(new File(databaseConfigDir + "lookup.xml"), new File(databaseConfigDir));
            try {
                databaseConfig = lookup.getDatabaseConfig(datasource.getConnection());
            } catch (java.sql.SQLException sqle) {
                throw new StorageInaccessibleException(sqle);
            }
        } else {
            // use the correct database-xml
            databaseConfig = new File(databaseConfigDir + databasename + ".xml");
        }
        // get our config...
        // mostly static so maybe make this a resource?
        return new StorageReader(databaseConfig.getPath());
    }

    /**
     * Obtains a StorageManager that grants access to teh databse.
     .
     * The instance represents a temporary connection to the datasource - 
     * do not store the result of this call as a static or long-term member of a class.
     * @return a StorageManager instance
      */
	public StorageManager getStorageManager() {
        storageManager = storageManagerClass.newInstance();
        storageManager.setStorageManagerFactory(this);
        return storageManager;
    }

    /**
     * Returns the datasource that provides access to the storage.
     * @return a DataSource instance, or <code>null</code> if none is (yet) available
     */
    public DataSource getDataSource() {
        return datasource;
    }

}


