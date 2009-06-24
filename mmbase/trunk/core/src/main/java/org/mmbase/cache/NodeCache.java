/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;
import org.mmbase.core.event.*;
import org.mmbase.module.core.*;
import org.mmbase.util.Casting;

/**
 * A cache for MMObjectNodes.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class NodeCache extends Cache<Integer, MMObjectNode> implements NodeEventListener {
    private static final int CACHE_SIZE = 4 * 1024;

    private static NodeCache cache;

    public static NodeCache getCache() {
        return cache;
    }

    static {
        cache = new NodeCache();
        cache.putCache();
    }

    @Override
    public String getName() {
        return "Nodes";
    }
    @Override
    public String getDescription() {
        return "Node number -> MMObjectNodes";
    }

    /**
     * Creates the MMBase ObjectNodes Cache.
     */
    private NodeCache() {
        super(CACHE_SIZE);
        // node cache is registered as a Listener in MMBase.java.
    }

    @Override
    public MMObjectNode remove(Object key) {

        Integer nodeNumber = Casting.toInt(key);
        RelatedNodesCache.getCache().removeNode(nodeNumber);

        return super.remove(nodeNumber);
    }


    public void notify(NodeEvent event) {
        int type = event.getType();
        if(type == Event.TYPE_DELETE || ((! event.isLocal()) && type == Event.TYPE_CHANGE)) {
            remove(event.getNodeNumber());
        }
    }
}
