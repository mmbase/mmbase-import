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
 * @version $Id: ChangeManager.java,v 1.7 2005-09-27 12:59:33 michiel Exp $
 * @see org.mmbase.storage.StorageManagerFactory#getChangeManager
 */
public final class ChangeManager {

    // the class to broadcast changes with
    private MMBaseChangeInterface mmc;
    
    private MMBase mmbase;

    /**
     * Constructor.
     * @param mmbase the MMbase instance on which the changes are made
     */
    public ChangeManager(MMBaseChangeInterface m) {
        mmc = m;
        mmbase = MMBase.getMMBase();
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
     * Fires the node change events through MMBase.propagateEvent() for all
     * local listeners. then calls the MMBaseChangeInterface implementation
     * (if there is one) to propagate the event into the clustering system.
     * Finally clears 'changed' state on the node
     * @param node the node to commit the change of
     * @param change the type of change: "n": new, "c": commit, "d": delete, "r" : relation changed
     */
    public void commit(MMObjectNode node, String change) {
        MMObjectBuilder builder = node.getBuilder();
        if (builder.broadcastChanges()) {
            NodeEvent event = new NodeEvent(node, NodeEvent.oldTypeToNewType(change));
 
            //regardless of wether this is a relatione event we fire a node event first
            mmbase.propagateEvent(event);
            
            //now if we have a MMBaseChangeManager send the event into the clustering system
            if (mmc != null) mmc.changedNode(event);
            
            //if the changed node is a relation, we fire a relation event as well
            if(builder instanceof InsRel) {
                RelationEvent relEvent = new RelationEvent(node, NodeEvent.oldTypeToNewType(change));
                
                //the relation event broker will make shure that listeners
                //for node-relation changes to a specific builder, will be
                //notified if this builder is either source or destination type
                //in the relation event
                mmbase.propagateEvent(relEvent);
                
                // now if we have a MMBaseChangeManager send the event into the clustering system
               if(mmc != null) mmc.changedNode((RelationEvent)event);
            }

        }
        node.clearChanged();
    }
}
