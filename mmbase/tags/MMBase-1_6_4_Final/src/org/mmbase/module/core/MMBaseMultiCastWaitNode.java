/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MMBaseMultiCastWaitNode is a wrapper class for MMObjectNode we want to
 * put into a 'waiting for a change' mode we don't block on the object
 * itself because we deed to check its number before we nofity it again.
 *
 */
public class MMBaseMultiCastWaitNode {

    // logging
    private static Logger log = Logging.getLoggerInstance(MMBaseMultiCastWaitNode.class.getName());

    /**
     * @javadoc
     * @scope private
     */
    MMObjectNode node;
    /**
     * @javadoc
     * @scope private
     */
    int number;

    /**
     * @javadoc
     */
    public MMBaseMultiCastWaitNode(MMObjectNode node) {
        this.node=node;
        this.number=node.getNumber();
    }

    /**
     * @javadoc
     */
    public synchronized void doWait(int time) {
        try {
            wait(time);
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * @javadoc
     */
    public boolean doNotifyCheck(int wantednumber) {
        if (number==wantednumber) {
            doNotify();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @javadoc
     */
    public synchronized void doNotify() {
        notify();
    }

}

