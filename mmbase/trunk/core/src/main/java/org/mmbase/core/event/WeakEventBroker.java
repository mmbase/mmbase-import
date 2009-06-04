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
 * An EventBroker which administrates the listeners in a {@link java.util.WeakHashMap}. This means
 * that such listeners can be garbage collected, even if they are still brokered.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.5
 * @version $Id$
 */
public abstract class WeakEventBroker extends EventBroker {

    private static final Logger log = Logging.getLoggerInstance(WeakEventBroker.class);

    private final Map<EventListener, Boolean> listeners = new WeakHashMap<EventListener, Boolean>();

    protected Collection<EventListener> backing() {
        return listeners.keySet();
    }

    public synchronized boolean addListener(EventListener listener) {
        if (canBrokerForListener(listener)) {
            if (listeners.containsKey(listener)) {
                return false;
            } else {
                listeners.put(listener, null);
                return true;
            }
        } else {
            log.warn("Ignored listener for" + getClass() + " because it cannot broker for that.");
        }
        return false;
    }

    public synchronized void removeListener(EventListener listener) {
        if (! listeners.remove(listener)) {
            log.warn("Tried to remove " + listener + " from " + getClass()+ " but it was not found. Ignored.");
        }

    }
    /**
     * Only adds synchronization, because backing is not concurrency proof.
     */
    public synchronized void notifyForEvent(Event event) {
        super.notifyForEvent(event);
    }

    public String toString(){
        return "Weak Event Broker";
    }


    public static void main(String[] argv) {
        Map<Object, Object> weakSet = new WeakHashMap<Object, Object>();
        weakSet.put(new Object(), null);
        System.out.println("set " + weakSet.keySet());
        Runtime.getRuntime().gc();
        System.out.println("set " + weakSet.keySet());
    }

}
