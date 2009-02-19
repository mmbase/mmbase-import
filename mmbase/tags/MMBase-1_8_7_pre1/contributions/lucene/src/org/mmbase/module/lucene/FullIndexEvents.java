/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.core.event.*;

/**
 * Events send during the execution of a full index.
 *
 * @since  MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id: FullIndexEvents.java,v 1.2 2008-07-28 13:03:36 michiel Exp $
 */

public class FullIndexEvents {


    enum Status {
        START,
        BUSY,
        IDLE
    }
    public static class Event extends org.mmbase.core.event.Event {

        protected final String index;
        protected final Status status;
        protected final int indexed;
        /**
         * The event itself
         */
        public Event(String i, Status s, int ied) {
            index = i;
            status = s;
            indexed = ied;
        };
        public String getIndex() {
            return index;
        }
        public Status getStatus() {
            return status;
        }
        public int getIndexed() {
            return indexed;
        }

    }

    /**
     * The associated listener
     */
    public static interface Listener extends EventListener {
        void notify(FullIndexEvents.Event event);

    }
    /**
     * The associated broker
     */
    public static class Broker extends AbstractEventBroker {

        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof FullIndexEvents.Listener;
        }
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof FullIndexEvents.Event;
        }
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            FullIndexEvents.Event ne = (FullIndexEvents.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
        public String toString() {
            return "Lucene Full Index Broker";
        }
    }


}
