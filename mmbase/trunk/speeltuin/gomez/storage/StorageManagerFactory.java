/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.Map;
import org.mmbase.module.core.MMBase;

/**
 * This interface contains functionality for retrieving StorageManager instances, which give access to the storage device.
 * It also provides functionality for setting and retrieving configuration data.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: StorageManagerFactory.java,v 1.4 2003-07-18 15:00:17 pierre Exp $
 */
public interface StorageManagerFactory {

    /**
     * Initialize the StorageManagerFactory.
     * This method should be called after instantiation of the factory class.
     * It is called automatically by {@link Storage.getStorageManagerFactory()} and {@link Storage.getStorageManagerFactory(MMBase)}.
     * @param mmbase the MMBase instance to which this factory belongs
     * @throws StorageConfigurationException when something went wrong during configuration of the factory
     * @throws StorageInaccessibleException when the storage cannot be accessed
     */
    public void init(MMBase mmbase) throws StorageConfigurationException, StorageInaccessibleException;

    /**
     * Obtains a StorageManager from the factory.
     * The instance represents a temporary connection to the datasource - 
     * do not store the result of this call as a static or long-term member of a class.
     * @return a StorageManager instance
     */
    public StorageManager getStorageManager();

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
     * @return the attribute value, or null if it is unknown
     * @param key the key of the attribute
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
}

