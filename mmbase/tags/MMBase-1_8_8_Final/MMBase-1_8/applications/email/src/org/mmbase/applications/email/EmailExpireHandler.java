/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;


import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * Checks email nodes that can be deleted, because they were sent already some time ago, and were
 * marked as 'one shot'.
 *
 * @author Daniel Ockeloen
 * @version $Id$
 */
class EmailExpireHandler implements Runnable {


    static private final Logger log = Logging.getLoggerInstance(EmailExpireHandler.class);

    final int expiretime;
    final EmailBuilder parent;

    /**
    *  create a handler with sleeptime and expiretime
    */
    public EmailExpireHandler(EmailBuilder parent, int expiretime) {
        this.parent = parent;
        this.expiretime = expiretime;
    }

    /**
    * Main loop, exception protected
    */
    public void run() {
        try {
            log.debug("Checking for to-be-deleted mail");
            // get the nodes we want to expire
            for (MMObjectNode expiredNode : parent.getDeliveredMailOlderThan(expiretime)) {
                log.service("Removing successfully mailed email 'one shot' email node " + expiredNode.getNumber());
                // remove all its relations
                expiredNode.removeRelations();
                // remove the node itself, by asking its builder
                parent.removeNode(expiredNode);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
