/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.List;
import java.util.Map;

import org.mmbase.storage.util.*;

import org.mmbase.module.core.MMBase;

/**
 * This interface contains functionality for retrieving StorageManager instances, which give access to the storage device.
 * It also provides functionality for setting and retrieving configuration data.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: StorageManagerFactory.java,v 1.12 2003-07-31 09:53:36 pierre Exp $
 */
public interface StorageManagerFactory {

    /**
     * Initialize the StorageManagerFactory.
     * This method should be called after instantiation of the factory class.
     * It is called automatically by {@link Storage.getStorageManagerFactory()} and {@link Storage.getStorageManagerFactory(MMBase)}.
     * @param mmbase the MMBase instance to which this factory belongs
     * @throws StorageError when something went wrong during configuration of the factory, or when the storage cannot be accessed
     */
    public void init(MMBase mmbase) throws StorageError;

    /**
     * Return the MMBase module for which the factory was instantiated
     * @return the MMBase instance
     */
    public MMBase getMMBase();
    
    /**
     * Obtains a StorageManager from the factory.
     * The instance represents a temporary connection to the datasource -
     * do not store the result of this call as a static or long-term member of a class.
     * @return a StorageManager instance
     * @throws StorageException when the storagemanager cannot be created
     */
    public StorageManager getStorageManager() throws StorageException;

    /**
     * Locates and opens the storage configuration document.
     * @throws StorageException if something went wrong while obtaining the document reader, or if no reader can be found
     * @return a StorageReader instance
     */
    public StorageReader getDocumentReader() throws StorageException;
    
    /**
     * Retrieve a map of attributes for this factory.
     * The attributes are the configuration parameters for the factory.
     * You cannot change this map, though you can change the attributes themselves.
     * @return an unmodifiable Map
     */
    public Map getAttributes();

    /**
     * Add a map of attributes for this factory.
     * The attributes are the configuration parameters for the factory.
     * The actual content the factory expects is dependent on the implementation.
     * The attributes are added to any attributes already knwon to the factory.
     * @param attributes the map of attributes to add
     */
    public void setAttributes(Map attributes);

    /**
     * Obtain an attribute from this factory.
     * Attributes are the configuration parameters for the storagefactory.
     * @param key the key of the attribute
     * @return the attribute value, or null if it is unknown
     */
    public Object getAttribute(Object key);

    /**
     * Set an attribute of this factory.
     * Attributes are the configuration parameters for the factory.
     * The actual content the factory expects is dependent on the implementation.
     * To invalidate an attribute, you can pass the <code>null</code> value.
     * @param key the key of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(Object key, Object value);

    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * @param key the key of the attribute
     * @return the scheme value, or null if it is unknown
     */
    public Scheme getScheme(Object key);
    
    /**
     * Obtain a scheme from this factory.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * If no scheme is present, the default pattern is used to create a scheme and add it to the factory.
     * @param key the key of the attribute
     * @param defaltPattern the pattern to use for the default scheme 
     * @return the scheme value
     */
    public Scheme getScheme(Object key, String defaultPattern);

    /**
     * Set a scheme of this factory, using a string pattern to base the Scheme on.
     * Schemes are special attributes, consisting of patterned strings that can be 
     * expanded with arguments.
     * @param key the key of the scheme
     * @param pattern the pattern to use for the scheme
     */
    public void setScheme(Object key, String pattern);

    /**
     * Check whether an option was set.
     * Options are attributes that return a boolean value.
     * @param key the key of the option
     * @return <code>true</code> if the option was set
     */
    public boolean hasOption(Object key);

    /**
     * Set an option to true or false.
     * @param key the key of the option
     * @param value the value of the option (true or false)
     */
    public void setOption(Object key, boolean value);

    /**
     * Returns the version of this factory implementation.
     * The factory uses this number to verify whether it can handle storage configuration files
     * that list version requirements.
     * @return the version as an integer
     */
    public double getVersion();

    /**
     * Returns a sorted list of type mappings for this storage.
     * @return  the list of TypeMapping objects
     */
	public List getTypeMappings();

    /**
     * Returns a map of disallowed field names and their possible alternate values.
     * @return  A Map of disallowed field names
     */
	public Map getDisallowedFields();

    /**
     * Obtains the identifier for the basic storage element.
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier() throws StorageException;
    
    /**
     * Obtains a identifier for an MMBase object.
     * @see Storable
     * @param mmobject the MMBase objecty
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier(Object mmobject) throws StorageException;
    
    /**
     * Returns whether transactions, and specifically rollback, is supported in the storage layer.
     * @return  <code>true</code> if trasnactions are supported
     */
	public boolean supportsTransactions();
    
    /**
     * Returns the ChangeManager utility instance, used to register and broadcast changes to nodes
     * in the storage layer.
     * This method is for use by the StorageManager
     */
    public ChangeManager getChangeManager();
}

