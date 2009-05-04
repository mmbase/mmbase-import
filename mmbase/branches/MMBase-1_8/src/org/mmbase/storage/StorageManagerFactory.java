/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.*;

import org.xml.sax.InputSource;
import javax.servlet.ServletContext;

import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.storage.util.*;

import org.mmbase.module.core.*;
import org.mmbase.clustering.ChangeManager;
import org.mmbase.clustering.MMBaseChangeInterface;
import org.mmbase.core.CoreField;

import org.mmbase.util.ResourceLoader;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.Transformers;

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
 * @version $Id$
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
     * The list with objects of which binary data should not be stored in database
     */
    protected List storeBinaryAsFileObjects;

    /**
     * The ChangeManager object, used to register/broadcast changes to a node or set of nodes.
     */
    protected ChangeManager changeManager;


    /**
     * The map with disallowed fieldnames and (if given) alternates
     */
    protected final SortedMap disallowedFields = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    /**
     * The query handler to use with this factory.
     * Note: the current handler makes use of the JDBC2NodeInterface and is not optimized for storage: using it means
     * you call getNodeManager() TWICE.
     * Have to look into how this should work together.
     */
    protected SearchQueryHandler queryHandler;

    /**
     * The query handler classes.
     * Assign a value to this class if you want to set a default query handler.
     */
    protected List queryHandlerClasses = new ArrayList();

    /**
     * @see #getSetSurrogator()
     */
    protected CharTransformer setSurrogator = null;

    /**
     * @see #getGetSurrogator()
     */
    protected CharTransformer getSurrogator = null;


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
    static public StorageManagerFactory newInstance() throws StorageException {
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
        log.service("initializing Storage Manager factory " + this.getClass().getName());
        this.mmbase = mmbase;
        attributes    = Collections.synchronizedMap(new HashMap());    // ConcurrentHashMap not possible because null-values are put (TODO)
        typeMappings  = Collections.synchronizedList(new ArrayList()); // CopyOnWriteArrayList not possible because Collections.sort is done (TODO)
        storeBinaryAsFileObjects = Collections.synchronizedList(new ArrayList());
        changeManager = new ChangeManager();
        try {
            log.debug("loading Storage Manager factory " + this.getClass().getName());
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
     * Instantiate a basic handler object using the specified class.
     * A basic handler can be any type of class and is dependent on the
     * factory implementation.
     * For instance, the database factory expects an
     * org.mmbase.storage.search.implentation.database.SQLHandler class.
     * @param handlerClass the class to instantuate teh object with
     * @return the new handler class
     */
    abstract protected Object instantiateBasicHandler(Class handlerClass);

    /**
     * Instantiate a chained handler object using the specified class.
     * A chained handler can be any type of class and is dependent on the
     * factory implementation.
     * For instance, the database factory expects an
     * org.mmbase.storage.search.implentation.database.ChainedSQLHandler class.
     * @param handlerClass the class to instantuate teh object with
     * @param previousHandler a handler thatw a sinstantiated previously.
     *        this handler should be passed to the new handler class during or
     *        after constrcution, so the ne whandler can 'chain' any events it cannot
     *        handle to this class.
     * @return the new handler class
     */
    abstract protected Object instantiateChainedHandler(Class handlerClass, Object previousHandler);

    /**
     * Instantiate a SearchQueryHandler object using the specified object.
     * The specified parameter may be an actual SearchQueryHandler object, or it may be a utility class.
     * For instance, the database factory expects an org.mmbase.storage.search.implentation.database.SQLHandler object,
     * which is used as a parameter in the construction of the actual SearchQueryHandler class.
     * @param data the object to instantuate a SearchQueryHandler object with
     */
    abstract protected SearchQueryHandler instantiateQueryHandler(Object data);

    /**
     * Opens and reads the storage configuration document.
     * Override this method to add additional configuration code before or after the configuration document is read.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected void load() throws StorageException {
        StorageReader reader = getDocumentReader();
        if (reader == null) {
            if (storageManagerClass == null || queryHandlerClasses.size() == 0) {
                throw new StorageConfigurationException("No storage reader specified, and no default values available.");
            } else {
                log.warn("No storage reader specified, continue using default values.");
                log.debug("Default storage manager : " + storageManagerClass.getName());
                log.debug("Default query handler : " + ((Class)queryHandlerClasses.get(0)).getName());
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

        log.service("get objects with binary data that should not be stored in database");
        storeBinaryAsFileObjects.addAll(reader.getStoreBinaryAsFileObjects());

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

        log.service("get type mappings");
        typeMappings.addAll(reader.getTypeMappings());
        Collections.sort(typeMappings);

        // get the queryhandler class
        // has to be done last, as we have to passing the disallowedfields map (doh!)
        // need to move this to DatabaseStorageManagerFactory
        List configuredClasses = reader.getSearchQueryHandlerClasses();
        if (configuredClasses.size() != 0) {
            queryHandlerClasses = configuredClasses;
        } else if (queryHandlerClasses.size() == 0) {
            throw new StorageConfigurationException("No SearchQueryHandler class specified, and no default available.");
        }
        log.service("Found queryhandlers " + queryHandlerClasses);
        // instantiate handler(s)
        Iterator iHandlers = reader.getSearchQueryHandlerClasses().iterator();
        Object handler = null;
        while (iHandlers.hasNext()) {
            Class handlerClass = (Class) iHandlers.next();
            if (handler == null) {
                handler = instantiateBasicHandler(handlerClass);
            } else {
                handler = instantiateChainedHandler(handlerClass, handler);
            }
        }
        // initialize query handler.
        queryHandler = instantiateQueryHandler(handler);

        String surr = (String) getAttribute(Attributes.SET_SURROGATOR);
        if (surr != null && ! surr.equals("")) {
            setSurrogator = Transformers.getCharTransformer(surr, null, "StorageManagerFactory#load", false);
        }

        surr = (String) getAttribute(Attributes.GET_SURROGATOR);
        if (surr != null && ! surr.equals("")) {
            getSurrogator = Transformers.getCharTransformer(surr, null, "StorageManagerFactory#load", false);
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
        String storagePath = mmbase.getInitParameter("storage");
        // use the parameter set in mmbaseroot if it is given
        if (storagePath != null) {
            try {
                InputSource resource = ResourceLoader.getConfigurationRoot().getInputSource(storagePath);
                if (resource == null) {
                    throw new StorageConfigurationException("Storage resource '" + storagePath + "' not found.");
                }
                return new StorageReader(this, resource);
            } catch (java.io.IOException ioe) {
                throw  new StorageConfigurationException(ioe);
            }
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
        log.debug("Database attributes " + this.attributes);
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
        attributes.put(key, value);
    }

    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be
     * expanded with arguments.
     * @param key the key of the attribute
     * @return the scheme value, or null if it is unknown
     */
    public Scheme getScheme(Object key) {
        return getScheme(key, null);
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
        setAttribute(key, Boolean.valueOf(value));
    }

    /**
     * Returns a sorted list of type mappings for this storage.
     * @return  the list of TypeMapping objects
     */
    public List getTypeMappings() {
        return Collections.unmodifiableList(typeMappings);
    }

    /**
     * Returns a list of objects of which binary data should be stored in a file.
     * @return the list of objects of which BLOB fields should not be stored in database.
     */
    public List getStoreBinaryAsFileObjects() {
    	return Collections.unmodifiableList(storeBinaryAsFileObjects);
    }

    /**
     * Returns a map of disallowed field names and their possible alternate values.
     * @return  A Map of disallowed field names
     */
    public Map getDisallowedFields() {
        return Collections.unmodifiableSortedMap(disallowedFields);
    }

    /**
     * Sets the map of disallowed Fields.
     * Unlike setAttributes(), this actually replaces the existing disallowed fields map.
     */
    protected void setDisallowedFields(Map disallowedFields) {
        this.disallowedFields.clear();
        this.disallowedFields.putAll(disallowedFields);
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
     *  <li>For Indices: the String '[basename]_[builder name]_[index name]_idx'</li>
     *  <li>For MMObjectNode: the object number as a Integer</li>
     *  <li>For CoreField or String: the field name, or the replacement fieldfname (from the disallowedfields map)</li>
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
        } else if (mmobject instanceof Index) {
            id = mmbase.getBaseName()+"_"+((Index)mmobject).getParent().getTableName() + "_" + ((Index)mmobject).getName() + "_idx";
        } else if (mmobject instanceof String || mmobject instanceof CoreField) {
            if (mmobject instanceof CoreField) {
                id = ((CoreField)mmobject).getName();
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
                        throw new StorageException("The name of the field '"+((CoreField)mmobject).getName()+"' is disallowed, and no alternate value is available.");
                    }
                } else {
                    id = newid;
                }
            }
        } else {
            throw new StorageException("Cannot obtain identifier for param of type '"+mmobject.getClass().getName()+".");
        }

        String maxIdentifierLength = (String)getAttribute(Attributes.MAX_IDENTIFIER_LENGTH);
        if (maxIdentifierLength != null && !"".equals(maxIdentifierLength)) {
          try {
            int maxlength = Integer.parseInt(maxIdentifierLength);
            if (id.length() > maxlength) {
              // Truncate the id, leave 8 characters space to put the hashcode
              String base = id.substring(0, maxlength - 8);
              long hashcode = id.hashCode();
              if (hashcode < 0) hashcode = Integer.MAX_VALUE + hashcode;

              // This generates a 8-character hex representation of the strings hashcode
              id = base + (new java.math.BigInteger(""+hashcode)).toString(16);
            }
          } catch (NumberFormatException e) {
            log.warn("Exception parsing the 'max-identifier-length' parameter; ignoring it!");
          }
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



    /**
     * Returns a filter which can be used to filter strings taken from storage or <code>null</code> if none defined.
     * @since MMBase-1.7.4
     */
    public CharTransformer getGetSurrogator() {
        return getSurrogator;
    }
    /**
     * Returns a filter which can be used to filter strings which are to be set into storage or <code>null</code> if none defined.
     * @since MMBase-1.7.4
     */
    public CharTransformer getSetSurrogator() {
        return setSurrogator;
    }


    /**
     * Returns the offset which must be used in the database. Currently this is based on the system's
     * default time zone. It is imaginable that can have configuration or database specific details later.
     * @param time The time at which it is evaluated (summer time issues)
     * @since MMBase-1.8
     * @todo experimental
     */
    public int getTimeZoneOffset(long time) {
        return TimeZone.getDefault().getOffset(time);
    }

    protected String getDataDir() {
        String dataDir = mmbase.getInitParameter("datadir");
        if (dataDir == null || dataDir.equals("")) {
            ServletContext sc = MMBaseContext.getServletContext();
            dataDir = sc != null ? sc.getRealPath("/WEB-INF/data") : null;
            if (dataDir == null) {
                dataDir = System.getProperty("user.dir") + java.io.File.separator + "data";
            }
        }
        return dataDir;
    }


}
