/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.5
 * @version $Id$
 */
public class WeakNodeEventBroker extends AbstractEventBroker {

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#canBrokerFor(java.lang.Class)
     */
    public boolean canBrokerForListener(EventListener listener) {
        return listener instanceof WeakNodeEventListener;
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
        WeakNodeEventListener nel = (WeakNodeEventListener) listener;
        nel.notify(ne);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    public String toString() {
        return "Weak NodeEvent Broker";
    }

}
