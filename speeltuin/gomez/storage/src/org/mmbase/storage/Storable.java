/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.List;
import java.util.Map;
import org.mmbase.storage.util.StorageReader;
import org.mmbase.storage.util.Scheme;
import org.mmbase.module.core.MMBase;

/**
 * This interface contains functionality for retrieving a storage identifier - a name or id
 * suitable for storing the object.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Storable.java,v 1.1 2003-07-28 10:19:20 pierre Exp $
 */
public interface Storable {

    /**
     * Returns a storage identifier for this object.
     * This should return:
     * <ul>
     *  <li>For MMBase: the object storage element identifier as a String (i.e. fully expanded table name)</li>
     *  <li>For MMObjectBuilder: the builder storage element identifier as a String (i.e. fully expanded table name)</li>
     *  <li>For MMObjectNode: the object number as a Integer</li>
     *  <li>For FieldDefs: a storage-compatible field name as a String (if no such name exists a StorageException is thrown)</li>
     * </ul>
     * A Storable object (except for MMObjectNode) should retrieve its storage identifier using 
     * {@link StorageMagagerFactory.getStorageIdentifier()} when it is first instantiated.
     * @return the identifier
     */
    Object getStorageIdentifier() throws StorageException;


}
