/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A cache for MMObjectNodes. 
 *
 * @author  Michiel Meeuwissen
 * @version $Id: NodeCache.java,v 1.1 2002-05-15 17:27:53 michiel Exp $
 */
public class NodeCache extends Cache {
    private static int cacheSize = 4 * 1024;    // Max size of the node cache
    private static NodeCache cache;

    public static NodeCache getCache() {
        return cache;
    }

    static {
        cache = new NodeCache(cacheSize);
        putCache(cache);
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
    private NodeCache(int size) {
        super(size);
    }
}
