/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

/**
 * Query result cache used for getNodes from MMObjectBuilder. So it contains only simple nodes (no
 * clusternodes).
 * 
 * The query <em>can</em> contain more than one <code>Step</step>, the queried fields are, however, excactly all
 * fields of one of the steps (the 'node step').
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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

    @Override
    public String getName() {
        return "NodeListCache";
    }
    @Override
    public String getDescription() {
        return "List Results";
    }

    /**
     * Creates the Node list cache.
     */
    private NodeListCache(int size) {
        super(size);
    }

}
