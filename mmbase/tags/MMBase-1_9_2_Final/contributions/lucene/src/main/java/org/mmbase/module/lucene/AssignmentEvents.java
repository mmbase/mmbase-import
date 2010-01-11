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
 * @version $Id$
 */

public class AssignmentEvents {

    public static final int FULL = 100;
    public static final int UPDATE = 101;
    public static final int DELETE = 102;
    public static final int CLEAR = 103;

    public static class Event extends org.mmbase.core.event.Event {
        private static final long serialVersionUID = -4175938509232134026L;
        
        protected final String index;
        protected final List<String> machines;

        protected final Class<? extends IndexDefinition> klas;
        protected final String identifier;
        protected boolean copy = false;

        /**
         * The event itself
         */
        public Event(String i, List<String> m, int type, String id, Class<? extends IndexDefinition> k) {
            super(null, type);
            index = i;
            machines = m == null ? Collections.singletonList(getMachine()) : m;
            klas = k;
            identifier = id;

        };
        public String getIndex() {
            return index;
        }
        public List<String> getMachines() {
            return machines;
        }
        public Class<? extends IndexDefinition> getClassFilter() {
            return klas;
        }
        public String getIdentifier() {
            return identifier;
        }
        public boolean getCopy() {
            return copy;
        }
        public void setCopy(boolean b) {
            copy = b;
        }
        @Override
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
