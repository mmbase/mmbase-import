/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * This class is the event broker implementation for the NodeEvent
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public class NodeEventBroker extends AbstractEventBroker {

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#canBrokerFor(java.lang.Class)
     */
    public boolean canBrokerForListener(EventListener listener) {
        return listener instanceof NodeEventListener;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
     */
    public boolean canBrokerForEvent(Event event) {
        return event instanceof NodeEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#notifyEventListeners()
     */
    protected void notifyEventListener(Event event, EventListener listener) {
        NodeEvent ne = (NodeEvent) event; //!!!!!
        NodeEventListener nel = (NodeEventListener) listener;
        nel.notify(ne);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    public String toString() {
        return "NodeEvent Broker";
    }

}
