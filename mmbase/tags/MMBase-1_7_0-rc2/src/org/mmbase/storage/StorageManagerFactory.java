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
import org.mmbase.storage.search.implementation.database.BasicQueryHandler;
import org.mmbase.storage.search.implementation.database.SqlHandler;
import org.mmbase.storage.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class contains functionality for retrieving StorageManager instances, which give access to the storage device.
 * It also provides functionality for setting and retrieving configuration data.
 * This is an abstract class. You cannot instantiate it. Use the static {@link #newInstance()} or {@link #newInstance(MMBase)}
 * methods to obtain a factory class.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: StorageManagerFactory.java,v 1.6 2004-02-05 12:37:18 michiel Exp $
 */
public abstract class StorageManagerFactory {

    private static final Logger log = Logging.getLoggerInstance(StorageManagerFactory.class);

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
    static private final Class DEFAULT_FACTORY_CLASS = org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory.class;

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
        // instantiate and initialize the class
        try {
            Class factoryClass = DEFAULT_FACTORY_CLASS;
            if (factoryClassName != null) {
                factoryClass = Class.forName(factoryClassName);
            }
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
            log.fatal(se.getMessage() + Logging.stackTrace(se));
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
        if (reader == null) {
            if (storageManagerClass == null || queryHandlerClass == null) {
                throw new StorageConfigurationException("No storage reader specified, and no default values available.");
            } else {
                log.warn("No storage reader specified, continue using default values.");
                log.debug("Default storage manager : " + storageManagerClass.getName());
                log.debug("Default query handler : " + queryHandlerClass.getName());
                return;
            }
        }

        // get the storage manager class
        Class configuredClass = reader.getStorageManagerClass();
        if (configuredClass != null) {
            storageManagerClass = configuredClass;
        } else if (storageManagerClass == null) {
            throw new StorageConfigurationException("No StorageManager class specified, and no default available.");
        }

        // get attributes
        setAttributes(reader.getAttributes());
        // get disallowed fields, and add these to the default list
        disallowedFields.putAll(reader.getDisallowedFields());

        // add default replacements when DEFAULT_STORAGE_IDENTIFIER_PREFIX is given
        String prefix = (String)getAttribute(Attributes.DEFAULT_STORAGE_IDENTIFIER_PREFIX);
        if (prefix !=null) {
            for (Iterator i = disallowedFields.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry)i.next();
                String name = (String) e.getKey();
                String replacement = (String) e.getValue();
                if (replacement == null ) {
                    e.setValue(prefix + "_" + name);
                }
            }
        }

        // get type mappings
        typeMappings.addAll(reader.getTypeMappings());
        Collections.sort(typeMappings);

        // get the queryhandler class
        // has to be done last, as we have to passing the disallowedfields map (doh!)
        // need to move this to DatabaseStorageManagerFactory
        configuredClass = reader.getSearchQueryHandlerClass();
        if (configuredClass != null) {
            queryHandlerClass = configuredClass;
        } else if (queryHandlerClass == null) {
            throw new StorageConfigurationException("No SearchQueryHandler class specified, and no default available.");
        }
        // instantiate handler
        try {
            java.lang.reflect.Constructor constructor = queryHandlerClass.getConstructor(new Class[] {Map.class});
            SqlHandler sqlHandler = (SqlHandler)  constructor.newInstance( new Object[] { disallowedFields } );
            log.service("Instantiated SqlHandler of type " + queryHandlerClass.getName());

            // Chained handlers, to be implemented later.
            /*
            Iterator iHandlers = reader.getChainedHandlerClasses().iterator();
            while (iHandlers.hasNext()) {
                Class handlerClass = (Class) iHandlers.next();
                constructor = handlerClass.getConstructor(new Class[] {SqlHandler.class});
                queryHandler = (SearchQueryHandler) constr2.newInstance(new Object[] {queryHandler});
                log.service("Instantiated chained SearchQueryHandler of type "
                    + handlerClass.getName());
            }
            */
            // initialize query handler.
            queryHandler = new BasicQueryHandler(sqlHandler);

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
     * Locates and opens the storage configuration document, if available.
     * The configuration document to open can be set in mmbasereoot (using the storage property).
     * The property should point to a resource which is to be present in the MMBase classpath.
     * Overriding factories may provide ways to auto-detect the location of a configuration file.
     * @throws StorageException if something went wrong while obtaining the document reader
     * @return a StorageReader instance, or null if no reader has been configured
     */
    public StorageReader getDocumentReader() throws StorageException {
        // determine storage resource.
        String storagepath = mmbase.getInitParameter("storage");
        // use the parameter set in mmbaseroot if it is given
        if (storagepath != null) {
            InputStream resource = this.getClass().getResourceAsStream(storagepath);
            if (resource == null) {
                throw new StorageConfigurationException("Storage resource '"+storagepath+"' not found.");
            }
            InputSource in = new InputSource(resource);
            in.setSystemId("resource://" + storagepath);
            return new StorageReader(this, in);
        } else {
            // otherwise return null
            return null;
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
        return getScheme(key,null);
    }

    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be
     * expanded with arguments.
     * If no scheme is present, the default pattern is used to create a scheme and add it to the factory.
     * @param key the key of the attribute
     * @param defaultPattern the pattern to use for the default scheme, <code>null</code> if there is no default
     * @return the scheme value, <code>null</code> if there is no scheme
     */
    public Scheme getScheme(Object key, String defaultPattern) {
        Scheme scheme =(Scheme)getAttribute(key);
        if (scheme == null && defaultPattern != null) {
            if (attributes.containsKey(key)) return null;
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
        if (pattern == null || pattern.equals("")) {
            setAttribute(key,null);
        } else {
            setAttribute(key,new Scheme(this,pattern));
        }
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
        String id;
        if (mmobject instanceof StorageManager) {
            id = mmbase.getBaseName();
        } else if (mmobject == mmbase) {
            id = mmbase.getBaseName()+"_object";
        } else if (mmobject instanceof MMObjectBuilder) {
            id = mmbase.getBaseName()+"_"+((MMObjectBuilder)mmobject).getTableName();
        } else if (mmobject instanceof MMObjectNode) {
            return ((MMObjectNode)mmobject).getIntegerValue("number");
        } else if (mmobject instanceof String || mmobject instanceof FieldDefs) {
            if (mmobject instanceof FieldDefs) {
                id = ((FieldDefs)mmobject).getDBName();
            } else {
                id = (String)mmobject;
            }
            String key = id;
            if (!hasOption(Attributes.DISALLOWED_FIELD_CASE_SENSITIVE)) {
                key = key.toLowerCase();
            }
            if (disallowedFields.containsKey(key)) {
                String newid = (String)disallowedFields.get(key);
                if (newid == null) {
                    if (hasOption(Attributes.ENFORCE_DISALLOWED_FIELDS)) {
                        throw new StorageException("The name of the field '"+((FieldDefs)mmobject).getDBName()+"' is disallowed, and no alternate value is available.");
                    }
                } else {
                    id = newid;
                }
            }
        } else {
            throw new StorageException("Cannot obtain identifier for param of type '"+mmobject.getClass().getName()+".");
        }
        String toCase = (String)getAttribute(Attributes.STORAGE_IDENTIFIER_CASE);
        if ("lower".equals(toCase)) {
            return id.toLowerCase();
        } else if ("upper".equals(toCase)) {
            return id.toUpperCase();
        } else {
            return id;
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
     * Returns the name of the catalog used by this storage (<code>null</code> if no catalog is used).
     */
    public String getCatalog() {
        return null;
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
