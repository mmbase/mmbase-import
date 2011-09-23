/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.xmlimporter;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A basic implementation of SimilarObjectFinder.
 * @since MMBase-1.5
 * @version $Id$
 */
public abstract class BasicFinder implements SimilarObjectFinder {

    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(BasicFinder.class.getName());

    /**
     * Convenience method: finds MMBase id's for all objects in the
     * persistent cloud of a given type and satisfying a criterium.
     * @param builder The builder for this type.
     * @param criterium The criterium: SQL where-clause, but
     * without the "where ".
     * @return List of (Integer) MMBase id's.
     */
    protected static List<Integer> findPersistentObjects(
    MMObjectBuilder builder, String criterium) {

        Enumeration<MMObjectNode> en = builder.search("WHERE " + criterium);
        List<Integer> result = new ArrayList<Integer>();
        while (en.hasMoreElements()) {
            MMObjectNode node = en.nextElement();
            result.add(node.getIntegerValue("number"));
        }
        return result;
    }

    /** Creates new BasicFinder */
    public BasicFinder() {}

    /**
     * Initializes this instance.
     * @param params The initialization parameters, provided as
     * name/value pairs (both String).
     */
    public void init(HashMap<String, String> params) {
    }

    /**
     * Searches for similar object. Objects found in the
     * persistent cloud will be accessed in the transaction.
     * @return List of the similar objects found.
     * @param transaction The transaction.
     * @param tmpObj The object to search for.
     * @throws TransactionHandlerException If a failure occurred.
     */
    public List<TmpObject> findSimilarObject(Transaction transaction, TmpObject tmpObj)
    throws TransactionHandlerException {
        Set<TmpObject> exactMatches = new HashSet<TmpObject>();
        Set<TmpObject> closeMatches = new HashSet<TmpObject>();

        MMObjectNode node1 = tmpObj.getNode();
        int otype = node1.getOType();
        Integer mmBaseId1 = new Integer(tmpObj.getMMBaseId());

        // Search temporary cloud for matching nodes,
        // add exact matches to exactMatches, close matches to closeMatches.
        Iterator<TmpObject> iTmpObjects = transaction.getTmpObjects().iterator();
        while (iTmpObjects.hasNext()) {
            TmpObject tmpObj2 = iTmpObjects.next();
            if (tmpObj2 == tmpObj) {
                // Traversal stops at this object (tmpnode1).
                // This is important, because
                // 1: all pairs get matched only once
                // 2: all objects it is compared with are in the part
                // of the transaction that is already merged.
                break;
            }
            if (!tmpObj2.isRelation()) {
                if (tmpObj2.getNode().getOType() == otype) {
                    evaluateMatch(tmpObj2, tmpObj, exactMatches, closeMatches);
                }
            }
        }

        // Search persistent cloud for exactly matching nodes,
        // add these to exactMatches.
        Iterator<Integer> iPersistentObjects
            = getExactPersistentObjects(tmpObj).iterator();
        while (iPersistentObjects.hasNext()) {
            Integer mmBaseId2 = iPersistentObjects.next();

            // Ignore if this is the node to match to.
            if (mmBaseId2.equals(mmBaseId1)) {
                continue;
            }

            // Access the object in the transaction context.
            TmpObject persObj2
            = transaction.getAccessObject(mmBaseId2.intValue());

            // Add to exact matches.
            exactMatches.add(persObj2);
        }

        // When exact matches are found, return these.
        if (exactMatches.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Matches (exact) found for " + tmpObj + ":\n"
                + exactMatches);
            }
            return new ArrayList<TmpObject>(exactMatches);
        }

        // When no exact matches found, search persistent cloud for
        // close matching nodes as well.
        Iterator<Integer> iCloseObjects
            = getClosePersistentObjects(tmpObj).iterator();
        while (iCloseObjects.hasNext()) {
            Integer mmBaseId2 = iCloseObjects.next();

            // Ignore if this is the node to match to.
            if (mmBaseId2.equals(mmBaseId1)) {
                continue;
            }

            // Access the object in the transaction context.
            TmpObject persObj2
            = transaction.getAccessObject(mmBaseId2.intValue());

            // Evaluate matching rate, and add to exactMatches
            // or closeMatches accordingly.
            evaluateMatch(persObj2, tmpObj, exactMatches, closeMatches);
        }

        // Return the close matches.
        if (log.isDebugEnabled()) {
            log.debug("Matches (close) found for " + tmpObj + ":\n"
            + closeMatches);
        }
        return new ArrayList<TmpObject>(closeMatches);
    }

    /**
     * Calculates matching rate for two objects.
     * e.g. the rate in which tmpObj1 matches tmpObj2, represented by a value ranging
     * from 0 to 1: <ul>
     * <li>1.0 for exact match,
     * <li>between 1.0 and 0.0 for not-exact but qualifying match,
     * <li>0.0 for match that is not close enough to qualify.
     * </ul>
     * @param tmpObj1 The object for which the matching rate is wanted.
     * @param tmpObj2 The object to match with.
     * @return Matching rate.
     */
    public abstract float scoreNode(TmpObject tmpObj1, TmpObject tmpObj2);

    /**
     * Gets MMBase id's for all objects from persistent cloud that
     * produce an exact match with the given object (possibly
     * including the object itself).
     * This can be used to prevent a more extensive search for close
     * matches when exact matches are possible.
     * @param tmpObj The object to match with.
     * @return Collection of (Integer) MMBase id's for objects from the
     *  persistent cloud that produce an exact match with the given
     *  object.
     */
    public abstract Collection<Integer> getExactPersistentObjects(TmpObject tmpObj);

    /**
     * Gets MMBase id's for all objects from persistent cloud that
     * might produce a qualifying match with the given object
     * (possibly including the object itself).
     * When looking for a fuzzy match, this can be used to make a
     * pre-selection from all the objects in the persistent cloud,
     * to reduce the total number of objects to be inspected closer.
     * @param tmpObj The object to match with.
     * @return Collection of (Integer) MMBase id's for objects from the
     *  persistent cloud that might produce a qualifying match with the
     *  given object.
     */
    public abstract Collection<Integer> getClosePersistentObjects(TmpObject tmpObj);

    /**
     * Calculates and evaluates matching rate of an object with respect
     * to a given object, and adds the object/match rate to a list of
     * exact matches - when the match is exact, or a list of close matches
     * - when the match is qualifying but not exact.
     * @param tmpObj1 The object for which the matching rate is wanted.
     * @param tmpObj2 The object to match with.
     * @param exactMatches Set of exact matching objects.
     * @param closeMatches Set of close matching objects.
     */
    private void evaluateMatch(
    TmpObject tmpObj1, TmpObject tmpObj2, Set<TmpObject> exactMatches, Set<TmpObject> closeMatches) {
        float matchingRate = scoreNode(tmpObj1, tmpObj2);
        if (matchingRate == 1.0) {
            // Exact match.
            exactMatches.add(tmpObj1);
        } else if (matchingRate > 0.0) {
            // Close match.
            closeMatches.add(tmpObj1);
        }
    }

}
