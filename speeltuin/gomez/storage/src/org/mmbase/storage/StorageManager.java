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
 * @version $Id: StorageManager.java,v 1.2 2003-07-23 11:19:46 pierre Exp $
 */
public interface StorageManager {

    /**
     * Returns the version of this factory implementation.
     * The factory uses this number to verify whether it can handle storage configuration files
     * that list version requirements.
     * @return the version as an integer
     */
    public double getVersion();

    /**
     * Initializes the manager.
     * @param factory the StorageManagerFactory instance that created this storage manager.
     * @throws StorageConfigurationException if the initialization failed
     */
    public void init(StorageManagerFactory factory) throws StorageException;

}

