/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * An EventBroker which administrates the listeners in a {@link java.util.concurrent.CopyOnWriteArraySet}.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public abstract class AbstractEventBroker extends EventBroker {

    private static final Logger log = Logging.getLoggerInstance(AbstractEventBroker.class);

    private final Set<EventListener> listeners = new CopyOnWriteArraySet<EventListener>();

    protected Collection<EventListener> backing() {
        return listeners;
    }

    public boolean addListener(EventListener listener) {
        if (canBrokerForListener(listener)) {
            if (! listeners.add(listener)) {
                if (log.isDebugEnabled()) {
                    log.debug("" + listener + " was already in " + getClass() + ". Ignored.");
                }
                return false;
            } else if (log.isDebugEnabled()) {
                log.debug("listener added to " + getClass());
            }
            return true;
        } else {
            log.warn("Ignored listener for" + getClass() + " because it cannot broker for that.");
        }
        return false;
    }

    public void removeListener(EventListener listener) {
        if (! listeners.remove(listener)) {
            log.warn("Tried to remove " + listener + " from " + getClass()+ " but it was not found. Ignored.");
        }

    }

}
