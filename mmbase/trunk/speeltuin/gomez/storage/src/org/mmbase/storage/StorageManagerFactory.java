/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.io.InputStream;
import java.util.*;
import org.xml.sax.InputSource;

import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.storage.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;

/**
 * This class contains functionality for retrieving StorageManager instances, which give access to the storage device.
 * It also provides functionality for setting and retrieving configuration data.
 * This is an abstract class. You cannot instantiate it. Use the static {@link #newInstance()} or {@link #newInstance(MMBase)} 
 * methods to obtain a factory class.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: StorageManagerFactory.java,v 1.15 2003-08-04 14:23:20 pierre Exp $
 */
public abstract class StorageManagerFactory {

    /**
     * A reference to the MMBase module
     */
    protected MMBase mmbase;
    /**
     * The class used to instantiate storage managers.
     * The classname is retrieved from the storage configuration file
     */
    protected Class storageManagerClass;

    /**
     * The map with configuration data
     */
    protected Map attributes;

    /**
     * The list with type mappings
     */
    protected List typeMappings;

    /**
     * The ChangeManager object, used to register/broadcast changes to a node or set of nodes.
     */
    protected ChangeManager changeManager;

    /** The map with disallowed fieldnames and (if given) alternates
     *
     */
    protected Map disallowedFields;

    /**
     * The query handler to use with this factory.
     * Note: the current handler makes use of the JDBC2NodeInterface and is not optimized for storage: using it means
     * you call getNodeManager() TWICE.
     * Have to look into how this should work together.
     */
    protected SearchQueryHandler queryHandler;

    /**
     * The query handler class.
     * Assign a value to this class if you want to set a default query handler.
     */
    protected Class queryHandlerClass;
    
    /**
     * The default storage factory class.
     * This classname is used if you doe not spevify the clasanme in the 'storagemanagerfactory' proeprty in mmabseroot.xml. 
     */
    static private final String DEFAULT_FACTORY_CLASS = "org.mmbase.storage.database.DatabaseStorageManagerFactory";

    /**
     * The legacy storage factory class.
     * For backward compatibility with the old database support classes.
     * So... how to determine you should use this?
     */
    static private final String LEGACY_FACTORY_CLASS = "org.mmbase.storage.legacy.LegacyStorageManagerFactory";

    /**
     * Obtain the StorageManagerFactory belonging to the indicated MMBase module.
     * @param mmbase The MMBase module for which to retrieve the storagefactory
     * @return The StorageManagerFactory
     * @throws StorageException if the StorageManagerFactory class cannot be located, accessed, or instantiated,
     *         or when something went wrong during configuration of the factory
     */
    static public StorageManagerFactory newInstance(MMBase mmbase)
                  throws StorageException {
        // get the class name for the factory to instantiate
        String factoryClassName = mmbase.getInitParameter("storagemanagerfactory");
        if (factoryClassName == null) factoryClassName = DEFAULT_FACTORY_CLASS;
        // instantiate and initialize the class
        try {
            Class factoryClass = Class.forName(factoryClassName);
            StorageManagerFactory factory = (StorageManagerFactory)factoryClass.newInstance();
            factory.init(mmbase);
            return factory;
        } catch (ClassNotFoundException cnfe) {
            throw new StorageFactoryException(cnfe);
        } catch (IllegalAccessException iae) {
            throw new StorageFactoryException(iae);
        } catch (InstantiationException ie) {
            throw new StorageFactoryException(ie);
        }
    }

    /**
     * Obtain the storage manager factory belonging to the default MMBase module.
     * @return The StoragemanagerFactory
     * @throws StorageException if the StorageManagerFactory class cannot be located, accessed, or instantiated,
     *         or when something went wrong during configuration of the factory
     */
    static public StorageManagerFactory newInstance()
                  throws StorageException {
        // determine the default mmbase module.
        return newInstance(MMBase.getMMBase());
    }

    /**
     * Initialize the StorageManagerFactory.
     * This method should be called after instantiation of the factory class.
     * It is called automatically by {@link #newInstance()} and {@link #newInstance(MMBase)}.
     * @param mmbase the MMBase instance to which this factory belongs
     * @throws StorageError when something went wrong during configuration of the factory, or when the storage cannot be accessed
     */
    protected final void init(MMBase mmbase) throws StorageError {
        this.mmbase = mmbase;
        attributes = Collections.synchronizedMap(new HashMap());
        disallowedFields = new HashMap();
        typeMappings = Collections.synchronizedList(new ArrayList());
        changeManager = new ChangeManager(mmbase);
        try {
            load();
        } catch (StorageException se) {
            // pass exceptions as a StorageError to signal a serious (unrecoverable) error condition
            throw new StorageError(se);
        }
    }

    /**
     * Return the MMBase module for which the factory was instantiated
     * @return the MMBase instance
     */
    public MMBase getMMBase() {
        return mmbase;
    }

    /**
     * Opens and reads the storage configuration document.
     * Override this method to add additional configuration code before or after the configuration document is read.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected void load() throws StorageException {
        StorageReader reader = getDocumentReader();
        
        // get the storage manager class
        Class configuredClass = reader.getStorageManagerClass();
        if (configuredClass != null) {
            storageManagerClass = configuredClass;
        } else if (storageManagerClass == null) {
            throw new StorageConfigurationException("No StorageManager class specified, and no default available.");
        }
        
        // get the queryhandler class
        configuredClass = reader.getSearchQueryHandlerClass();
        if (configuredClass != null) {
            queryHandlerClass = configuredClass;
        } else if (queryHandlerClass == null) {
            throw new StorageConfigurationException("No SearchQueryHandler class specified, and no default available.");
        }
        // intantiate handler
        try {
            queryHandler = (SearchQueryHandler)queryHandlerClass.newInstance();
        } catch (IllegalAccessException iae) {
            throw new StorageConfigurationException(iae);
        } catch (InstantiationException ie) {
            throw new StorageConfigurationException(ie);
        }

        // get attributes
        setAttributes(reader.getAttributes());
        // get disallowed fields
        setDisallowedFields(reader.getDisallowedFields());
        // get type mappings
        typeMappings.addAll(reader.getTypeMappings());
        Collections.sort(typeMappings);
    }

    /**
     * Obtains a StorageManager from the factory.
     * The instance represents a temporary connection to the datasource -
     * do not store the result of this call as a static or long-term member of a class.
     * @return a StorageManager instance
     * @throws StorageException when the storagemanager cannot be created
     */
    public StorageManager getStorageManager() throws StorageException {
        try {
            StorageManager storageManager = (StorageManager)storageManagerClass.newInstance();
            storageManager.init(this);
            return storageManager;
        } catch(InstantiationException ie) {
            throw new StorageException(ie);
        } catch(IllegalAccessException iae) {
            throw new StorageException(iae);
        }
    }

    // javadoc inherited
    /**
     * Obtains a SearchQueryHandler from the factory.
     * This provides ways to query for data using the SearchQuery interface.
     * Note that  cannot run the querys on a transaction (since SearchQuery does not support them).
     *
     * @return a SearchQueryHandler instance
     * @throws StorageException when the handler cannot be created
     */
    public SearchQueryHandler getSearchQueryHandler() throws StorageException {
        if (queryHandler==null) {
            throw new StorageException("Cannot obtain a query handler.");
        } else {
            return queryHandler;
        }
    }
    
    /**
     * Locates and opens the storage configuration document.
     * The configuration document to open can be set in mmbasereoot (using the storage property).
     * The property should point to a resource which is to be present in the MMBase classpath.
     * If not given or the resource cannot be found, this method throws an exception.
     * Overriding factories may provide ways to auto-detect the location of a configuration file, or
     * dismiss with its use.
     * @throws StorageException if something went wrong while obtaining the document reader, or if no reader can be found
     * @return a StorageReader instance
     */
    public StorageReader getDocumentReader() throws StorageException {
        // determine storage resource.
        // use the parameter set in mmbaseroot if it is given
        String storagepath = mmbase.getInitParameter("storage");
        if (storagepath == null) {
            throw new StorageConfigurationException("No storage resource specified.");
        } else {
            InputStream resource = this.getClass().getResourceAsStream(storagepath);
            if (resource == null) {
                throw new StorageConfigurationException("Storage resource '"+storagepath+"' not found.");
            }
            InputSource in = new InputSource(resource);
            in.setSystemId("resource://" + storagepath);
            return new StorageReader(this,in);
        }
    }

    /**
     * Retrieve a map of attributes for this factory.
     * The attributes are the configuration parameters for the factory.
     * You cannot change this map, though you can change the attributes themselves.
     * @return an unmodifiable Map
     */
    public Map getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Add a map of attributes for this factory.
     * The attributes are the configuration parameters for the factory.
     * The actual content the factory expects is dependent on the implementation.
     * The attributes are added to any attributes already knwon to the factory.
     * @param attributes the map of attributes to add
     */
    public void setAttributes(Map attributes) {
        this.attributes.putAll(attributes);
    }

    /**
     * Obtain an attribute from this factory.
     * Attributes are the configuration parameters for the storagefactory.
     * @param key the key of the attribute
     * @return the attribute value, or null if it is unknown
     */
    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    /**
     * Set an attribute of this factory.
     * Attributes are the configuration parameters for the factory.
     * The actual content the factory expects is dependent on the implementation.
     * To invalidate an attribute, you can pass the <code>null</code> value.
     * @param key the key of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(Object key, Object value) {
        attributes.put(key,value);
    }

    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * @param key the key of the attribute
     * @return the scheme value, or null if it is unknown
     */
    public Scheme getScheme(Object key) {
        return (Scheme)getAttribute(key);
    }

    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * If no scheme is present, the default pattern is used to create a scheme and add it to the factory.
     * @param key the key of the attribute
     * @param defaltPattern the pattern to use for the default scheme 
     * @return the scheme value
     */
    public Scheme getScheme(Object key, String defaultPattern) {
        Scheme scheme = getScheme(key);
        if (scheme == null) {
            scheme = new Scheme(this,defaultPattern);
            setAttribute(key,scheme);
        }
        return scheme;
    }

    /**
     * Set a scheme of this factory, using a string pattern to base the Scheme on.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * @param key the key of the scheme
     * @param pattern the pattern to use for the scheme
     */
    public void setScheme(Object key, String pattern) {
        setAttribute(key,new Scheme(this,pattern));
    }

    /**
     * Check whether an option was set.
     * Options are attributes that return a boolean value.
     * @param key the key of the option
     * @return <code>true</code> if the option was set
     */
    public boolean hasOption(Object key) {
        Object o = getAttribute(key);
        return (o instanceof Boolean) && ((Boolean)o).booleanValue();
    }

    /**
     * Set an option to true or false.
     * @param key the key of the option
     * @param value the value of the option (true or false)
     */
    public void setOption(Object key, boolean value) {
        setAttribute(key,new Boolean(value));
    }

    /**
     * Returns a sorted list of type mappings for this storage.
     * @return  the list of TypeMapping objects
     */
    public List getTypeMappings() {
        return Collections.unmodifiableList(typeMappings);
    }

    /**
     * Returns a map of disallowed field names and their possible alternate values.
     * @return  A Map of disallowed field names
     */
    public Map getDisallowedFields() {
        return Collections.unmodifiableMap(disallowedFields);
    }

    /**
     * Sets the map of disallowed Fields.
     * Unlike setAttributes(), this actually replaces the existing disallowed fields map.
     */
    protected void setDisallowedFields(Map disallowedFields) {
        this.disallowedFields = new HashMap(disallowedFields);
    }

    /**
     * Obtains the identifier for the basic storage element.
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier() throws StorageException {
        return getStorageIdentifier(mmbase);
    }

    /**
     * Obtains a identifier for an MMBase object.
     * The default implementation returns the following type of identifiers:
     * <ul>
     *  <li>For StorageManager: the basename</li>
     *  <li>For MMBase: the String '[basename]_object</li>
     *  <li>For MMObjectBuilder: the String '[basename]_[builder name]'</li>
     *  <li>For MMObjectNode: the object number as a Integer</li>
     *  <li>For FieldDefs or String: the field name, or the replacement fieldfname (from the disallowedfields map)</li>
     * </ul>
     * Note that 'basename' is a property from the mmbase module, set in mmbaseroot.xml.<br />
     * You can override this method if you intend to use different ids.
     *
     * @see Storable
     * @param mmobject the MMBase objecty
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier(Object mmobject) throws StorageException {
        if (mmobject instanceof StorageManager) {
            return mmbase.getBaseName();
        } else if (mmobject == mmbase) {
            return mmbase.getBaseName()+"_object";
        } else if (mmobject instanceof MMObjectBuilder) {
            return mmbase.getBaseName()+"_"+((MMObjectBuilder)mmobject).getTableName();
        } else if (mmobject instanceof MMObjectNode) {
            return ((MMObjectNode)mmobject).getIntegerValue("number");
        } else if (mmobject instanceof String || mmobject instanceof FieldDefs) {
            String id;
            if (mmobject instanceof FieldDefs) {
                id = ((FieldDefs)mmobject).getDBName();
            } else {
                id = (String)mmobject;
            }
            if (!hasOption(Attributes.DISALLOWED_FIELD_CASE_SENSITIVE)) {
                id = id.toUpperCase();
            }
            if (disallowedFields.containsKey(id)) {
                id = (String)disallowedFields.get(id);
                if (id == null) {
                    String prefix = (String)getAttribute("defaultStorageIdentifierPrefix");
                     if (prefix!=null) {
                        return prefix+"_"+id; 
                    } else {
                        throw new StorageException("The name of the field '"+((FieldDefs)mmobject).getDBName()+"' is disallowed, and no alternate value is available.");
                    }
                }
            }
            return id;
        } else {
            throw new StorageException("Cannot obtain identifier for param of type '"+mmobject.getClass().getName()+".");
        }
    }

    /**
     * Returns the ChangeManager utility instance, used to register and broadcast changes to nodes
     * in the storage layer.
     * This method is for use by the StorageManager
     */
    public ChangeManager getChangeManager() {
        return changeManager;
    }

    /**
     * Returns the version of this factory implementation.
     * The factory uses this number to verify whether it can handle storage configuration files
     * that list version requirements.
     * @return the version as an integer
     */
    abstract public double getVersion();

    /**
     * Returns whether transactions, and specifically rollback, is supported in the storage layer.
     * @return  <code>true</code> if trasnactions are supported
     */
    abstract public boolean supportsTransactions();

}
