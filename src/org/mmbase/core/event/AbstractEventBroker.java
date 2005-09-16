/*
 * Created on 7-sep-2005. 
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
 * This class dous most of the work of keeping references to all the listeners
 * and allowing for adding/removing them. Only a fiew type specific actions are
 * delegated to the super class.<br/> The EventListener also provides a method
 * for passing on constraint properties to a event broker. If you want to create
 * your own event type you can use this feature to accomplish that not all
 * events of your type are propagated to the listener.
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public abstract class AbstractEventBroker {

    private static Logger log = Logging
        .getLoggerInstance(AbstractEventBroker.class);

    protected List listeners = Collections.synchronizedList(new ArrayList());

    /**
     * this method should return true if the provided listener object is an
     * instance of the listener type handeled by the specific broker
     * 
     * @param listener
     * @return
     */
    public abstract boolean canBrokerForListener(EventListener listener);

    /**
     * this method should return true if the provided event object is of the
     * type the specific broker handles
     * 
     * @param event
     * @return
     */
    public abstract boolean canBrokerForEvent(Event event);

    /**
     * This method has twoo functions. It must cast both event and listener to
     * the proper type and invoke the event on the listener. But it must allso
     * check if the listener has constraint properties set. if so it must use
     * them to decide if the event should be invoked on this listener.
     * 
     * @param event
     * @param listener
     * @throws ClassCastException
     */
    protected abstract void notifyEventListener(Event event,
            EventListener listener) throws ClassCastException;

    public void addListener(EventListener listener) {
        if (canBrokerForListener(listener) && !listeners.contains(listener)) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }
    }

    public void removeListener(EventListener listener) {
        if (listeners.contains(listener)) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }

    public void notifyForEvent(Event event) {
        if (listeners.size() > 0) {
            synchronized (listeners) {
                for (Iterator i = listeners.iterator(); i.hasNext();) {
                    EventListener listener = (EventListener) i.next();
                    try {
                        notifyEventListener(event, listener);
                    } catch (ClassCastException e) {
                        // warn the world!
                        // this event is not proper for this broker
                        // (this should never happen)
                    }

                }
            }
        }
    }

}
