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
 * @version $Id: StorageManagerFactory.java,v 1.3 2003-07-18 12:09:05 pierre Exp $
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
     */
    public void setAttributes(Map attributes);

    /**
     * Obtain an attribute from this factory.
     * Attributes are the configuration parameters for the storagefactory. 
     * @return the attribute value, or null if it is unknown
     */
    public Object getAttribute(Object key);

    /**
     * Set an attribute of this factory.
     * Attributes are the configuration parameters for the factory. 
     * The actual content the factory expects is dependent on the implementation.
     * To invalidate an attribute, you can pass the <code>null</code> value.
     */
    public void setAttribute(Object key, Object value);

}
