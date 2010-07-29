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
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */
public class IdEventBroker extends AbstractEventBroker {


    // javadoc inherited
    public boolean canBrokerForListener(EventListener listener) {
        return listener instanceof IdEventListener;
    }

    // javadoc inherited
    public boolean canBrokerForEvent(Event event) {
        return event instanceof IdEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#notifyEventListeners()
     */
    protected void notifyEventListener(Event event, EventListener listener) {
        IdEvent ne = (IdEvent) event; //!!!!!
        IdEventListener nel = (IdEventListener) listener;
        nel.notify(ne);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    public String toString() {
        return "IdEvent Broker";
    }

}
