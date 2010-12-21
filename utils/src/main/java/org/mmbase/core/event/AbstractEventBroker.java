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
 * An EventBroker which makes sure the the same listener is added only once.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public abstract class AbstractEventBroker extends EventBroker {

    private static final Logger log = Logging.getLoggerInstance(AbstractEventBroker.class);

    private List<EventListener> listeners = Collections.unmodifiableList(new ArrayList<EventListener>());

    private static final Comparator<EventListener> COMPARATOR = new Comparator<EventListener>() {

        @Override
        public int compare(EventListener e1, EventListener e2) {
            try {
                if (e1 instanceof WeightEventListener && e2 instanceof WeightEventListener) {
                    int diff = ((WeightEventListener) e1).getWeight() - ((WeightEventListener) e2).getWeight();
                    if (diff != 0) {
                        return diff;
                    }
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
            return e1 == null ? (e2 == null ? 0 : 1) : e1.getClass().getName().compareTo(e2.getClass().getName());
        }
    };

    @Override
    protected Collection<EventListener> backing() {
        return listeners;
    }


    @Override
    public boolean addListener(EventListener listener) {
        if (canBrokerForListener(listener)) {
            synchronized(COMPARATOR) {

                if (listeners.contains(listener)) {
                    if (log.isDebugEnabled()) {
                        log.debug("" + listener + " was already in " + getClass() + ". Ignored.");
                    }
                    return false;
                } else {
                    List<EventListener> newList = new ArrayList<EventListener>(listeners.size() + 1);
                    newList.addAll(listeners);
                    newList.add(listener);
                    Collections.sort(newList, COMPARATOR);
                    listeners = Collections.unmodifiableList(newList);
                    log.debug("listener added to " + getClass());
                }
                return true;
            }
        } else {
            log.warn("Ignored listener for" + getClass() + " because it cannot broker for that.");
        }
        return false;
    }

    @Override
    public void removeListener(EventListener listener) {
        synchronized(COMPARATOR) {
            List<EventListener> newList = new ArrayList<EventListener>(listeners.size() + 1);
            newList.addAll(listeners);
            if (! newList.remove(listener)) {
                log.warn("Tried to remove " + listener + " from " + getClass()+ " but it was not found. Ignored.");
                //return false;
            }
            listeners = Collections.unmodifiableList(newList);
            //return true;
        }

    }

}
