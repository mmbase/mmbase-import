/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

/**
 * 
 *
 * @author Pierre van Rooden
 * @version $Id: ChangeManager.java,v 1.2 2003-08-29 12:12:26 keesj Exp $
 */
public final class ChangeManager {

    // the class to broadcast changes with
    private MMBaseChangeInterface mmc;

    /**
     * Constructor.
     * @param mmbase the MMbase instance on which the changes are made
     */
    public ChangeManager(MMBase mmbase) {
        mmc = mmbase.mmc;
    }

    /**
     * Commit all changes stored in a Changes map. 
     * Clears the change status of all changed nodes, then broadcasts changes to the
     * nodes' builders.
     * @param changes a map wityh node/change value pairs
     */
    public void commit(Map changes) {
        for (Iterator i = changes.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry)i.next();
            MMObjectNode node = (MMObjectNode)e.getKey(); 
            String change = (String)e.getValue();
            commit(node,change);
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
        node.clearChanged();
        MMObjectBuilder builder = node.getBuilder();
        if (builder.broadcastChanges) {
            mmc.changedNode(node.getNumber(),builder.getTableName(),change);
            if (builder instanceof InsRel) {
                // figure out tables to send the changed relations
                MMObjectNode n1 = node.getNodeValue("snumber");
                MMObjectNode n2 = node.getNodeValue("dnumber");
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmc.changedNode(n1.getNumber(), n1.getBuilder().getTableName(), "r");
                mmc.changedNode(n2.getNumber(), n2.getBuilder().getTableName(), "r");
            }
        }
    }
}
