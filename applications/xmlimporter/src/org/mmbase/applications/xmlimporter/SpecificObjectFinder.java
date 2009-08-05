/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

import java.util.*;

/**
 * SpecificObjectFinder implements a SimilarObjectFinder in such a way
 * that a search for an object similar to tmpObj1 always returns tmpObj2,
 * and nothing for other objects, where tmpObj1 and tmpObj2 are provided
 * by the user. An example of the useage is provided by the method
 * mergePersistentObjects(), wich merges two objects in the persistent
 * cloud (i.e. the database).
 *
 * @author Rob van Maris (Finalist IT Group)
 * @since MMBase-1.5
 * @version $Id: SpecificObjectFinder.java,v 1.4 2007-06-21 15:50:20 nklasens Exp $
 */
public class SpecificObjectFinder implements SimilarObjectFinder {

    // Base for unique transaction key.
    private static long uniqueId = System.currentTimeMillis();

    // The object searched.
    private TmpObject tmpObj1;

    // The object returned by the search.
    private TmpObject tmpObj2;

    /**
     * Creates new SpecificObjectFinder.
     * @param tmpObj1 The only object for which a similar object
     *  will be returned.
     * @param tmpObj2 The only object that will be returned as
     *  similar to tmpObj1.
     */
    public SpecificObjectFinder(TmpObject tmpObj1, TmpObject tmpObj2) {
        this.tmpObj1 = tmpObj1;
        this.tmpObj2 = tmpObj2;
    }

    /**
     * Initialize this instance. This implementation does nothing.
     * @param params The initialization parameters, provided as
     * name/value pairs (both String).
     */
    public void init(HashMap<String, String> params) {}

    /**
     * Searches for similar object. This implementation returns a list that
     * contains tmpObj2 when the object to search for is tmpObj1, or an empty
     * list when the object to search for is not tmpObj1.
     * @return List of the similar objects found.
     * @param transaction The transaction where the tmpObj belongs to.
     * @param tmpObj The object to search for.
     */
    public List<TmpObject> findSimilarObject(Transaction transaction, TmpObject tmpObj) {
        List<TmpObject> results = new ArrayList<TmpObject>();
        if (tmpObj == tmpObj1) {
            results.add(tmpObj2);
        }
        return results;
    }

    /**
     * Merge two objects in the persistent cloud (the database).
     * @param mmbaseId1 MMBase number of the first object. Afterward this
     *  object will be deleted.
     * @param mmbaseId2 MMBase number of the second object. Afterward this
     *  object will hold the merged result.
     * @param merger The merger to be used.
     * @throws TransactionHandlerException If a failure occurred.
     */
    public static void mergePersistentObjects(
        int mmbaseId1, int mmbaseId2, ObjectMerger merger)
    throws TransactionHandlerException {

        // Create user for transaction.
        UserTransactionInfo uti = new UserTransactionInfo();
        uti.user = new User("SpecificObjectFinder.java");

        // Create transaction.
        Transaction transaction
            = Transaction.createTransaction(uti,
            "SpecificObjectFinder" + (uniqueId++), true, 600);

        // Access both objects.
        TmpObject obj1 = transaction.accessObject("obj1", mmbaseId1);
        TmpObject obj2 = transaction.accessObject("obj2", mmbaseId2);

        // Merge obj1 and obj2.
        transaction.mergeObjects(
            obj1.getNode().getName(),
            new SpecificObjectFinder(obj1, obj2),
            merger);

        // Commit transaction.
        transaction.commit();
    }

}
