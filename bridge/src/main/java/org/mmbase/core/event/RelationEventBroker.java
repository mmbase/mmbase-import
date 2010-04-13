/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 * This class is the event broker implementation for the RelationEvent
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @author Ernst Bunders
 * @version $Id$
 */
public class RelationEventBroker extends AbstractEventBroker {

    /**
     * use this property to make shure your listener only gets the relation
     * events where the node number matches the given value.
     */
    public static final String PROPERTY_NODETYPE = "nodetype";

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#canBrokerFor(event.EventListener)
     */
    public boolean canBrokerForListener(EventListener listener) {
        // if(listener.getClass().equals(RelationEventListener.class))return
        // true;
        return listener instanceof RelationEventListener;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
     */
    public boolean canBrokerForEvent(Event event) {
        return event instanceof RelationEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see event.AbstractEventBroker#notifyEventListener(event.Event,
     *      event.EventListener)
     */
    protected void notifyEventListener(Event event, EventListener listener) throws ClassCastException {
        RelationEvent re = (RelationEvent) event;
        RelationEventListener rel = (RelationEventListener) listener;
        rel.notify(re);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    public String toString() {
        return "RelationEvent Broker";
    }

}
