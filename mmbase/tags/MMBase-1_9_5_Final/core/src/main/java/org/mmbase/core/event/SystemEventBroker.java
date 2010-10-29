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
 * @since MMBase-1.9.3
 * @version $Id: TransactionEventBroker.java 41369 2010-03-15 20:54:45Z michiel $
 */
public class SystemEventBroker extends AbstractEventBroker {

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#canBrokerFor(java.lang.Class)
     */
    public boolean canBrokerForListener(EventListener listener) {
        return listener instanceof SystemEventListener;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
     */
    public boolean canBrokerForEvent(Event event) {
        return event instanceof SystemEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#notifyEventListeners()
     */
    protected void notifyEventListener(Event event, EventListener listener) {
        SystemEvent te = (SystemEvent) event; //!!!!!
        SystemEventListener sel = (SystemEventListener) listener;
        sel.notify(te);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    @Override
    public String toString() {
        return "SystemEvent Broker";
    }

}
