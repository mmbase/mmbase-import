/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.core.event.*;
import java.util.List;
import java.util.Collections;

/**
 *
 * @since  MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id: AssignmentEvents.java,v 1.1 2008-07-28 13:03:36 michiel Exp $
 */

public class AssignmentEvents {

    public static final int TYPE_FULL = 100;
    public static final int TYPE_UPDATE = 101;

    public static class Event extends org.mmbase.core.event.Event {

        protected final String index;
        protected final List<String> machines;

        /**
         * The event itself
         */
        public Event(String i, List<String> m) {
            super(null, TYPE_FULL);
            index = i;
            machines = m == null ? Collections.singletonList(getMachine()) : m;

        };
        public String getIndex() {
            return index;
        }
        public List<String> getMachines() {
            return machines;
        }
        public String toString() {
            return super.toString() + " " + index;
        }

    }

    /**
     * The associated listener
     */
    public static interface Listener extends EventListener {
        void notify(AssignmentEvents.Event event);

    }
    /**
     * The associated broker
     */
    public static class Broker extends AbstractEventBroker {

        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof AssignmentEvents.Listener;
        }
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof AssignmentEvents.Event;
        }
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            AssignmentEvents.Event ne = (AssignmentEvents.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
        public String toString() {
            return "Lucene Assignments Broker";
        }
    }




}
