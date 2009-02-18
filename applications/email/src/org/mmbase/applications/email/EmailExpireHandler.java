/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.util.Iterator;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 *
 * expire handler, removes email nodes that are
 * need to be expired
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
        Thread kicker = MMBaseContext.startThread(this, "emailexpireprobe");
    }

    /**
    * Main loop, exception protected
    */
    public void run() {
        try {
            MMBase mmbase = MMBase.getMMBase();
            while (!mmbase.isShutdown()) {
                // get the nodes we want to expire
                for (Iterator i = parent.getDeliveredMailOlderThan(expiretime).iterator(); i.hasNext(); ) {
                    // get next node
                    MMObjectNode expiredNode = (MMObjectNode)i.next();
                    log.service("Removing successfully mailed email 'one shot' email node " + expiredNode.getNumber());
                    // remove all its relations
                    expiredNode.removeRelations();
                    // remove the node itself, by asking its builder
                    parent.removeNode(expiredNode);
                }
                try {
                    Thread.sleep(sleeptime * 1000);
                } catch (InterruptedException f) {
                    log.debug(Thread.currentThread().getName() +" was interrupted.");
                    continue;
                }
                if (MMBase.getMMBase().isShutdown()) {
                    log.service("MMBase has been shutdown, breaking out of email expire probe too");
                    break;
                } else {
                    log.debug("MMBase still running");
                }
            }
        } catch (Exception e) {
            log.error("run(): ERROR: Exception in emailexpireprobe thread!");
            log.error(Logging.stackTrace(e));
            return;
        }
    }
}
