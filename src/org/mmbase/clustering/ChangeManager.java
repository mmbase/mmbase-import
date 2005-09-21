/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import java.util.*;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

/**
 * This utility class contains the methods for broadcasting/registering changes on nodes. It is
 * available as 'getChangeManager()' from the StorageManagerFactory.
 *
 * @author Pierre van Rooden
 * @version $Id: ChangeManager.java,v 1.5 2005-09-21 08:22:34 ernst Exp $
 * @see org.mmbase.storage.StorageManagerFactory#getChangeManager
 */
public final class ChangeManager {

    // the class to broadcast changes with
    private MMBaseChangeInterface mmc;

    /**
     * Constructor.
     * @param mmbase the MMbase instance on which the changes are made
     */
    public ChangeManager(MMBaseChangeInterface m) {
        mmc = m;
    }

    /**
     * Commit all changes stored in a Changes map.
     * Clears the change status of all changed nodes, then broadcasts changes to the
     * nodes' builders.
     * @param changes a map with node/change value pairs
     */
    public void commit(Map changes) {
        for (Iterator i = changes.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry)i.next();
            MMObjectNode node = (MMObjectNode)e.getKey();
            String change = (String)e.getValue();
            commit(node, change);
            i.remove();
        }
    }

    /**
     * Commits the change to a node.
     * Clears the change status of a node, then broadcasts changes to the
     * node's parent builder. If the node is a relation, it also updates the relationcache and
     * broadcasts these changes to the relation' s source and destination.
     * @param node the node to commit the change of
     * @param change the type of change: "n": new, "c": commit, "d": delete, "r" : relation changed
     */
    public void commit(MMObjectNode node, String change) {
        //node.clearChanged();
        MMObjectBuilder builder = node.getBuilder();
        if (builder.broadcastChanges()) {
           mmc.changedNode(new NodeEvent(node, mapEventType(change)));
           if (builder instanceof InsRel) {
               // figure out tables to send the changed relations
               mmc.changedNode(new RelationEvent(node, mapEventType(change)));

           }
        }
        node.clearChanged();
    }

    /**
     * @param change
     * @return
     */
    private int mapEventType(String change) {
        if("c".equals(change)){
            return NodeEvent.EVENT_TYPE_CHANGED;
        } else if("d".equals(change)){
            return NodeEvent.EVENT_TYPE_DELETE;
        } else if ("n".equals(change)){
            return NodeEvent.EVENT_TYPE_NEW;
        }
        //this should never happen. But what to do when it does?
        throw new IllegalArgumentException("change type "+change+" is not supported");
    }
}
