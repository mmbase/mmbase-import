/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

/**
 * A cache for MMObjectNodes. 
 *
 * @author  Michiel Meeuwissen
 * @version $Id: NodeCache.java,v 1.3 2003-07-10 16:32:09 michiel Exp $
 */
public class NodeCache extends Cache {
    private static final int CACHE_SIZE = 4 * 1024;

    private static NodeCache cache;

    public static NodeCache getCache() {
        return cache;
    }

    static {
        cache = new NodeCache();
        cache.putCache();
    }

    public String getName() {
        return "Nodes";
    }
    public String getDescription() {
        return "MMBase Nodes";
    }

    /**
     * Creates the MMBase ObjectNodes Cache.
     */
    private NodeCache() {
        super(CACHE_SIZE);
    }
}
