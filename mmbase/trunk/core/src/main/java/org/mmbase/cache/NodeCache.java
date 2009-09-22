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
import java.util.*;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

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

    private transient final ReferenceQueue<MMObjectNode> queue = new ReferenceQueue<MMObjectNode>();
    private transient final Map<Integer, NodeReference> weak = new HashMap<Integer, NodeReference>();

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


    /*
    @Override
    public  MMObjectNode get(Object key) {
        synchronized(weak) {
            purge();
            MMObjectNode s = super.get(key);
            if (s != null) {
                return s;
            } else {
                NodeReference ref = weak.get(key);
                if (ref != null) {
                    MMObjectNode n = ref.get();
                    if (n != null) {
                        super.put((Integer) key, n);
                    }
                    return n;
                } else {
                    return null;
                }

            }
        }
    }

    @Override
    public MMObjectNode put(Integer key, MMObjectNode value) {
        synchronized(weak) {
            purge();
            weak.put(key, new NodeReference(value, key));
            return super.put(key, value);
        }
    }
    */

    protected void purge() {
        NodeReference ref = (NodeReference) queue.poll();
        while (ref != null) {
            weak.remove(ref.number);
            ref = (NodeReference) queue.poll();
        }
    }



    public void notify(NodeEvent event) {
        int type = event.getType();
        if(type == Event.TYPE_DELETE || ((! event.isLocal()) && type == Event.TYPE_CHANGE)) {
            remove(event.getNodeNumber());
        }
    }

    private class NodeReference extends WeakReference<MMObjectNode> {
        public final int number;
        NodeReference(MMObjectNode n, int number) {
            super(n, queue);
            this.number = number;
        }

    }
}
