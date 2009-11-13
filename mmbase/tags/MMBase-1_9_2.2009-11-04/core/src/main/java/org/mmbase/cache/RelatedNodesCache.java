/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import org.mmbase.storage.search.*;
import org.mmbase.bridge.implementation.BasicQuery;
import org.mmbase.module.core.MMObjectNode;

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

    @Override
    public String getName() {
        return "RelatedNodesCache";
    }
    @Override
    public String getDescription() {
        return "Caches related nodes of a certain node";
    }

    /**
     * Creates the Node list cache.
     */
    private RelatedNodesCache(int size) {
        super(size);
    }

}
