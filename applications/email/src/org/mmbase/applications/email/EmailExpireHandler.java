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
 * Starts a Thread which checks email nodes that can e deleted, because they were sent already some
 * time ago, and were marked as 'one shot'.
 *
 * @author Daniel Ockeloen
 */
public class EmailExpireHandler implements Runnable {


    static private final Logger log = Logging.getLoggerInstance(EmailExpireHandler.class);

    // sleeptime between expire runs in seconds
    int sleeptime;

    // expire time (default 30min, set in the builder.xml), defined in seconds
    int expiretime;

    // parent builder needed for callbacks
    EmailBuilder parent;

    /**
    *  create a handler with sleeptime and expiretime
    */
    public EmailExpireHandler(EmailBuilder parent, int sleeptime, int expiretime) {
        this.parent = parent;
        this.sleeptime = sleeptime;
        this.expiretime = expiretime;
        MMBaseContext.startThread(this, "emailexpireprobe");
    }

    /**
    * Main loop, exception protected
    */
    public void run() {
        try {
            MMBase mmbase = MMBase.getMMBase();
            while (!mmbase.isShutdown()) {
                // get the nodes we want to expire
                for (MMObjectNode expiredNode : parent.getDeliveredMailOlderThan(expiretime)) {
                    log.service("Removing successfully mailed email 'one shot' email node " + expiredNode.getNumber());
                    // remove all its relations
                    expiredNode.removeRelations();
                    // remove the node itself, by asking its builder
                    parent.removeNode(expiredNode);
                }
                try {
                    Thread.sleep(sleeptime * 1000);
                } catch (InterruptedException f) {
                    log.service(Thread.currentThread().getName() +" was interrupted.");
                    break;
                }
                if (MMBase.getMMBase().isShutdown()) {
                    log.service("MMBase has been shutdown, breaking out of email expire probe too");
                    break;
                } else {
                    log.debug("MMBase still running");
                }
            }
        } catch (Exception e) {
            log.error("Exception in emailqueueprobe thread!", e);
            return;
        }
    }
}
