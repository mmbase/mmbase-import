/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import org.mmbase.util.logging.*;

/**
 * This cache handles multilevel query results from the bridge, namely only for the 'getList'
 * functions of BasicCloud
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: MultilevelCache.java,v 1.5 2003-08-29 12:12:27 keesj Exp $
 * @see   org.mmbase.bridge.implementation.BasicCloud#getList
 * @todo  It is odd that this one query cache is called in the bridge implementation and not in the core.
 */

// This used to be implemented in MultilevelCacheHandler, MultilevelCacheEntry and
// MultilevelSubscribeNode (functionality was generalized to 'QueryResultCache'. See CVS history for
// old implemention.

public class MultilevelCache extends QueryResultCache {

    private static Logger log = Logging.getLoggerInstance(MultilevelCache.class);

    // There will be only one multilevel cache, and here it is:
    private static MultilevelCache multiCache;

    public static MultilevelCache getCache() {
        return multiCache;
    }

    static {
        multiCache = new MultilevelCache(300);
        multiCache.putCache();
    }

    public String getName() {
        return "MultilevelCache";
    }
    public String getDescription() {
        return "Multi-level List Results";
    }

    /**
     * Creates the MultiLevel  Cache.
     */
    private MultilevelCache(int size) {
        super(size);
    }
        
}
