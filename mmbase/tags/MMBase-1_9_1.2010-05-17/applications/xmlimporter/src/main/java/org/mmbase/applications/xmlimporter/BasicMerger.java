/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.xmlimporter;

import java.util.*;

/**
 * This is a basic implementation of ObjectMerger.
 * It applies these rules:
 * <ul>
 * <li>Leave the fields of the merging objects unaffected.
 * <li>Move the relations of both to the merged object.
 * <li>Relations are considered duplicates when of same type and
 * with same source and destination.
 * <li>Add objects to the persistent cloud that are not present already.
 * </ul>
 *
 * @author Rob van Maris: Finalist IT Group
 * @since MMBase-1.5
 * @version $Id$
 */
public class BasicMerger implements ObjectMerger {

    /** Initialize this instance. This implementation simply stores
     * the initialization parameters.
     * @param params The initialization parameters, provided as
     * name/value pairs (both String).
     */
    public void init(HashMap<String, String> params) {
    }

    /** Merge a field. This implementation leaves all fields unaffected.
     * @param name The name of the field.
     * (Note: "number" and "owner" are not considered fields in this context,
     * so this method will not be called with these values for name.)
     * @param tmpObj1 The first object to be merged. This will hold
     * the resulting merged object afterwards.
     * @param tmpObj2 The second object. this object must be deleted
     * afterwards.
     */
    public void mergeField(TmpObject tmpObj1, TmpObject tmpObj2, String name) {}

    /** Merge relations. This implementation moves all relations of the second
     * object to the merged object.
     * @param tmpObj1 The first object to be merged. This will hold
     * the resulting merged object afterwards.
     * @param tmpObj2 The second object. this object must be deleted
     * afterwards.
     * @param relations1 List of all relations of the first object.
     * @param relations2 List of all relations of the second object.
     */
    public void mergeRelations(TmpObject tmpObj1, TmpObject tmpObj2,
            List<TmpObject> relations1, List<TmpObject> relations2) {

        Iterator<TmpObject> i = relations2.iterator();
        while (i.hasNext()) {
            TmpObject relation = i.next();
            if (tmpObj2.isSourceOf(relation)) {
               relation.setSource(tmpObj1);
            }
            if (tmpObj2.isDestinationOf(relation)) {
               relation.setDestination(tmpObj1);
            }
        }
    }

    /** Tests if two relations should be considered duplicates,
     * indicating that one of them can be disposed of.
     * This test will only be called for pairs of relations that
     * have already been verified to be of the same type, and have the
     * same source, destination.
     * This implementation always considers these pairs duplicates,
     * it provides no additional tests.
     * @param relation1 The first relation.
     * @param relation2 The second relation.
     * @return true if these relations should be considered duplicates.
     */
    public boolean areDuplicates(TmpObject relation1, TmpObject relation2) {
        return true;
    }

    /** Tests if this object should be added to the persistent cloud
     * when not present already.
     * When this returns false, the object will be deleted from the
     * transaction if no object is found to merge it with.
     * This implementation allows all objects to be added when not present
     * already.
     * @param tmpObj The object.
     * @return true If this object should be added, when not present already.
     */
    public boolean isAllowedToAdd(TmpObject tmpObj) {
        return true;
    }

}
