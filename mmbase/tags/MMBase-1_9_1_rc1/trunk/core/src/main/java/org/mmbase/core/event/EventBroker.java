/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is the base class for all event brokers in mmbase. the function of an
 * event broker is to know about a specific kind of event, as well as a specific
 * kind of event listener. All events should be derived from the
 * org.mmbase.core.event.Event class, and all listeners from the
 * org.mmbase.core.event.EventListener interface.<br/> Allthough event
 * listeners have to implement the EventListener interface, the actual method
 * that will be called to pass on the event is not part of this interface, as it
 * is specific for the kind of event you want to listen for. This is a contract
 * between the broker implementation and the event listerer interface.<br/>
 * This class does most of the work of keeping references to all the listeners
 * and allowing for adding/removing them. Only a fiew type specific actions are
 * delegated to the super class.<br/> The EventListener also provides a method
 * for passing on constraint properties to a event broker. If you want to create
 * your own event type you can use this feature to accomplish that not all
 * events of your type are propagated to the listener.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public abstract class EventBroker {

    private static final Logger log = Logging.getLoggerInstance(EventBroker.class);

    /**
     * this method should return true if this broker can accept and propagate
     * events to the listener of this type. There are no fixed criteria for this.

     * Most implementions do <code>event instanceof &lt;EventListener associated with this broker&gt;</code>
     *
     * @param listener
     */
    public abstract boolean canBrokerForListener(EventListener listener);

    /**
     * this method should return true if this event broker can broker for
     * events of this type. There are no fixed criteria for this.
     *
     * Most implementions do <code>event instanceof &lt;EvenType associated with this broker&gt;</code>
     *
     * @param event
     */
    public abstract boolean canBrokerForEvent(Event event);

    /**
     * This method has two functions. It must cast both event and listener to
     * the proper type and invoke the event on the listener. But it must allso
     * check if the listener has constraint properties set. if so it must use
     * them to decide if the event should be invoked on this listener.
     *
     * @param event
     * @param listener
     * @throws ClassCastException
     */
    protected abstract void notifyEventListener(Event event, EventListener listener) throws ClassCastException;

    public abstract boolean addListener(EventListener listener);

    public abstract void removeListener(EventListener listener);

    /**
     * @since MMBase-1.8.5
     */
    protected abstract Collection<EventListener> backing();

    /**
     * @since MMBase-1.8.5
     */
    public Collection<EventListener> getListeners() {
        return Collections.unmodifiableCollection(backing());
    }


    public void notifyForEvent(Event event) {
        for (EventListener listener : backing()) {
            assert canBrokerForListener(listener);
            notifyEventListener(event, listener);
        }
    }

    public abstract String toString();

    public boolean equals(Object o) {
        //  we can only have one instance so this will do to prevent adding more instances of an envent broker
        return o != null && this.getClass().equals(o.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}
