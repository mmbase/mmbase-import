/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import org.mmbase.module.core.*;

/**
 * The Storage class.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Storage.java,v 1.1 2003-07-17 08:06:02 pierre Exp $
 */
public final class Storage {

    /**
     * Obtain the storage manager factory belonging to the indicated MMBase module.
     * @param mmbase The MMbas emodule for which to retrieve the storagefactory
     * @return The StoragemanagerFactory
     */
    static public StorageManagerFactory getStorageManagerFactory(MMBase mmbase) {
        throw new UnsupportedOperationException("cannot load storage manager factory");
    }

}
