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
 * @version $Id: Storage.java,v 1.1 2003-07-21 09:31:01 pierre Exp $
 */
public final class Storage {

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
     * @throws StorageFactoryException if the StorageManagerFactory class cannot be located, accessed, or instantiated,
     *         or when something went wrong during configuration of the factory
     * @throws StorageInaccessibleException when the storage cannot be accessed
     */
    static public StorageManagerFactory getStorageManagerFactory()
                  throws StorageFactoryException, StorageInaccessibleException {
        // determine the default mmbase module.
        return getStorageManagerFactory(MMBase.getMMBase());
    }
}
