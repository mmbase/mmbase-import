/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import org.mmbase.module.core.MMBase;

/**
 * The Storage class is used to instantiate the StorageManagerFactory for a
 * MMBase system.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Storage.java,v 1.3 2003-07-17 17:13:09 pierre Exp $
 */
public final class Storage {

    /**
     * The default storage factory class.
     * For backward compatibility with classes that do not provide the factory classname in the
     * mmbaseroot configuration file.
     */
    static private final String DEFAULT_FACTORY_CLASS = "org.mmbase.storage.database.DatabaseStorageManagerFactory";

    /**
     * Obtain the storage manager factory belonging to the indicated MMBase module.
     * @param mmbase The MMBase module for which to retrieve the storagefactory
     * @return The StorageManagerFactory
     * @throws StorageFactoryException if the StorageManagerFactory class cannot be located, accessed, or instantiated,
     *         or when something went wrong during configuration of the factory
     * @throws StorageInaccessibleException when the storage cannot be accessed
     */
    static public StorageManagerFactory getStorageManagerFactory(MMBase mmbase)
                  throws StorageFactoryException, StorageInaccessibleException {
        // get the class name for the factory to instantiate
        String factoryClassName = mmbase.getInitParameter("storagemanagerfactory");
        if (factoryClassName == null) factoryClassName = DEFAULT_FACTORY_CLASS;
        // instantiate and initialize the class
        try {
            Class factoryClass = Class.forName(factoryClassName);
            StorageManagerFactory factory= (StorageManagerFactory)factoryClass.newInstance();
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
     * @throws StorageFactoryException if the StorageManagerFactory class cannot be located, accessed, or instantiated,
     *         or when something went wrong during configuration of the factory
     * @throws StorageInaccessibleException when the storage cannot be accessed
     */
    static public StorageManagerFactory getStorageManagerFactory()
                  throws StorageFactoryException, StorageInaccessibleException {
        // determine the mmbase module.
        return getStorageManagerFactory(MMBase.getMMBase());
    }
}
