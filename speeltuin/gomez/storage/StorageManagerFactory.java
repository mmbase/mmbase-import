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
 * @version $Id: StorageManagerFactory.java,v 1.2 2003-07-17 17:13:10 pierre Exp $
 */
public interface StorageManagerFactory {

    /**
     * Initialize the StorageManagerFactory.
     * This method is called after instantiation of the class.
     * This normally happens underwater by calling Storage.getStorageManagerFactory().
     * @param mmbase the MMBase instance to which this factory belongs
     * @throws StorageConfigurationException when something went wrong during configuration of the factory
     * @throws StorageInaccessibleException when the storage cannot be accessed
     */
    public void init(MMBase mmbase) throws StorageConfigurationException, StorageInaccessibleException;

    public StorageManager getStorageManager();

    public Map getAttributes();

    public void setAttributes(Map attributes);

    public Object getAttribute(Object key);

    public void setAttribute(Object key, Object value);

}
