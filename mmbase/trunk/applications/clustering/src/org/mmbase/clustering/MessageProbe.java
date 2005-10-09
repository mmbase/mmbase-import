/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import org.mmbase.core.event.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * MessageProbe a thread object started to handle all nofity's needed when
 * one is received.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @version $Id: MessageProbe.java,v 1.5 2005-10-09 14:56:17 ernst Exp $
 */
public class MessageProbe implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(MessageProbe.class);

    private ClusterManager parent;
    private Event event;

    /**
     * @javadoc
     */
    MessageProbe(Event event) {
        this.event = event;
    }

    /**
     * @javadoc
     */
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Handling event " + event + " for " + event.getMachine());
        }

        //pass on the event to the EventManager. we ONLY have to do this for remote events
        //the local events are bening send by the ChangeManager, and are
        //no longer handeled by the clustering system.
        try {
            if(! event.getMachine().equals(parent.mmbase.getMachineName())){
                EventManager.getInstance().propagateEvent(event);
            }
        } catch(Throwable t) {
            log.error(Logging.stackTrace(t));
        }

    }
}
