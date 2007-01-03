/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import org.mmbase.core.event.RelationEvent;

/**
 * Query result cache used for getNodes from MMObjectBuilder. So it contains only simple nodes (no
 * clusternodes)
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeListCache.java,v 1.4.2.1 2007-01-03 09:16:27 nklasens Exp $
 * @see   org.mmbase.module.core.MMObjectBuilder#getNodes
 * @since MMBase-1.7
 */
public class NodeListCache extends QueryResultCache {

    // There will be only one list cache, and here it is:
    private static NodeListCache nodeListCache;

    public static NodeListCache getCache() {
        return nodeListCache;
    }

    static {
        nodeListCache = new NodeListCache(300);
        nodeListCache.putCache();
    }

    public String getName() {
        return "NodeListCache";
    }
    public String getDescription() {
        return "List Results";
    }

    /**
     * Creates the Node list cache.
     */
    private NodeListCache(int size) {
        super(size);
    }

    /**
     * @see org.mmbase.cache.QueryResultCache#notify(org.mmbase.core.event.RelationEvent)
     */
    public void notify(RelationEvent event) {
        // only queries with a single step are in this cache. Cache will only invalidate when a node
        // changes and notwhen a relation to a node changes.
        // A list on a relation type will invalidate by a NodeEvent, because a relation is a node. 
    }
    
}
