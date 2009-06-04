/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

import java.util.*;

/**
 * This interface provides methods to customize the way objects
 * are merged in a temporary cloud.
 *
 * @author Rob van Maris: Finalist IT Group
 * @since MMBase-1.5
 * @version $Id$
 */
public interface ObjectMerger {

    /**
     * Initialize this instance (called once per transaction).
     * @param params The initialization parameters, provided as
     *  name/value pairs (both String).
     * @throws TransactionHandlerException if a failure occurred.
     */
    public void init(HashMap<String, String> params) throws TransactionHandlerException;

    /**
     * Merges a field.
     * @param tmpObj1 The first object to be merged. This will hold
     *  the resulting merged object afterwards.
     * @param tmpObj2 The second object. this object must be deleted
     *  afterwards.
     * @param name The name of the field.
     *  (Note: "number" and "owner" are not considered fields in this context,
     *  so this method will not be called with these values for name.)
     */
    public void mergeField(TmpObject tmpObj1, TmpObject tmpObj2, String name);

    /**
     * Merges relations.
     * @param tmpObj1 The first object to be merged. This will hold
     *  the resulting merged object afterwards.
     * @param tmpObj2 The second object. this object must be deleted
     *  afterwards.
     * @param relations1 List of all relations of the first
     *  object (as TmpObject instances).
     * @param relations2 List of all relations of the second
     *  object (as TmpObject instances).
     */
    public void mergeRelations(TmpObject tmpObj1, TmpObject tmpObj2,
        List<TmpObject> relations1, List<TmpObject> relations2);

    /**
     * Tests if two relations should be considered duplicates,
     * indicating that one of them must be disposed of.
     * This test will only be called for pairs of relations that
     * have already been verified to be of the same type, and have the
     * same source and destination.
     * This method may provide additional tests.
     * @param relation1 The first relation.
     * @param relation2 The second relation.
     * @return true if these relations should be considered duplicates.
     */
    public boolean areDuplicates(TmpObject relation1, TmpObject relation2);

    /**
     * Tests if this object should be added to the persistent cloud
     * when not present already.
     * When this returns false, the object will be deleted from the
     * transaction if no object is found to merge it with.
     * @param tmpObj The object.
     * @return true If this object should be added, when not present already.
     */
    public boolean isAllowedToAdd(TmpObject tmpObj);

}