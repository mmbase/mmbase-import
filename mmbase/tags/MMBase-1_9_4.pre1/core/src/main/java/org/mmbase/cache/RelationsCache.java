/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.List;

import org.mmbase.module.core.MMObjectNode;

/**
 * Cache from MMObjectNode number to List of InsRel MMObjectNodes instances (relation nodes).
 * Used in MMObjectNode.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see   org.mmbase.module.core.MMObjectNode#getRelations
 * @see   org.mmbase.module.core.MMObjectNode#getRelationNodes
 * @since MMBase-1.7
 */


public class RelationsCache extends Cache<Integer,List<MMObjectNode>> {

    // There will be only one list cache, and here it is:
    private static RelationsCache relationsCache;

    public static RelationsCache getCache() {
        return relationsCache;
    }

    static {
        relationsCache = new RelationsCache(300);
        relationsCache.putCache();
    }

    @Override
    public String getName() {
        return "RelationsCache";
    }
    @Override
    public String getDescription() {
        return "Caches relations to/from a certain node";
    }

    
    /**
     * Creates the Node list cache.
     */
    private RelationsCache(int size) {
        super(size);
    }
        
}
