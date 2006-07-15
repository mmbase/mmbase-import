/*
 * Created on 7-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.Properties;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is the event broker implementation for the NodeEvent
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class IdEventBroker extends AbstractEventBroker {

    private static Logger log = Logging.getLoggerInstance(NodeEventBroker.class);


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
