/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.util.Iterator;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 *
 * expire handler, removes email nodes that are
 * need to be expired
 */
public class EmailExpireHandler implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(EmailExpireHandler.class);

    // sleeptime between expire runs in seconds
    int sleeptime;

    // expire time (default 30min, set in the builder.xml), defined in seconds
    int expiretime;

    // parent builder needed for callbacks
    EmailBuilder parent;

    Thread kicker = null;
    /**
    *  create a handler with sleeptime and expiretime
    */
    public EmailExpireHandler(EmailBuilder parent, int sleeptime, int expiretime) {
        this.parent = parent;
        this.sleeptime = sleeptime;
        this.expiretime = expiretime;
        kicker = new Thread(this, "emailexpireprobe");
        kicker.setDaemon(true);
        kicker.start();
    }

    /**
    * Main loop, exception protected
    */
    public void run() {
        try {
            while (kicker != null) {
                // get the nodes we want to expire
                for (Iterator i = parent.getDeliveredMailOlderThan(expiretime).iterator(); i.hasNext(); ) {
                    // get next node
                    MMObjectNode expirenode = (MMObjectNode)i.next();
                    // remove all its relations
                    expirenode.removeRelations();
                    // remove the node itself, by asking its builder
                    parent.removeNode(expirenode);
                }
                try {
                    Thread.sleep(sleeptime * 1000);
                } catch (InterruptedException f) {
                    return;
                }
            }
        } catch (Exception e) {
            log.error("run(): ERROR: Exception in emailqueueprobe thread!");
            log.error(Logging.stackTrace(e));
            return;
        }
    }
}
