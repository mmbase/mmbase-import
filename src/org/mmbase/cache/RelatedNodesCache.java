/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import org.mmbase.storage.search.*;

/**
 * Query result cache used for getRelatedNodes from MMObjectNodes. Entries are invalidated on the
 * normal QueryResultCache way, but also if the one node from which the related nodes were requested is
 * removed from the Node Cache itself.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see   org.mmbase.module.core.MMObjectNode#getRelatedNodes
 * @since MMBase-1.7
 */

public class RelatedNodesCache extends QueryResultCache {

    // There will be only one list cache, and here it is:
    private static RelatedNodesCache relatedNodesCache;

    public static RelatedNodesCache getCache() {
        return relatedNodesCache;
    }

    static {
        relatedNodesCache = new RelatedNodesCache(300);
        relatedNodesCache.putCache();
    }

    public String getName() {
        return "RelatedNodesCache";
    }
    public String getDescription() {
        return "Caches related nodes of a certain node";
    }

    // nodenumber -> set of keys
    // Used to sync this cache with node-cache. If node not any more in node-cache, then we decide to also remove its related nodes.
    // This seems a plausible thing to do.

    private Map numberToKeys = new HashMap();


    public synchronized Object put(SearchQuery query, List queryResult) {
        // test cache policy before caching
        if (!checkCachePolicy(query)) return null;
        Integer number = (Integer) ((Step) query.getSteps().get(0)).getNodes().first();
        Set keys = (Set) numberToKeys.get(number);
        if (keys == null) {
            keys = new HashSet();
            numberToKeys.put(number, keys);
        }
        keys.add(query);
        return super.put(query, queryResult);
    }


    public synchronized Object remove(Object key) {
        SearchQuery query = (SearchQuery) key;
        Integer number = (Integer) ((Step) query.getSteps().get(0)).getNodes().first();
        Set keys = (Set) numberToKeys.get(number);
        if (keys != null) {
            keys.remove(query);
            if (keys.size() == 0) numberToKeys.remove(number);
        }
        return super.remove(key);
    }

    synchronized void removeNode(Integer number) {
        Set keys = (Set) numberToKeys.get(number);
        if (keys != null) {
            Iterator i = keys.iterator();
            while (i.hasNext()) {
                super.remove(i.next());
            }
            numberToKeys.remove(number);
        }
    }

    /**
     * Creates the Node list cache.
     */
    private RelatedNodesCache(int size) {
        super(size);
    }

    public void clear(){
        super.clear();
        numberToKeys.clear();
    }
}
