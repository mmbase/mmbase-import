/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.core.event.*;

/**
 * All the code necessary to propagate events with the meaning 'the lucene index has changes, you must refresh your searchers'.
 * @since  MMBase-1.9
 * @author Michiel Meeuwissen
 */

public class NewSearcher {

    /**
     * The event itself
     */
    public static class Event extends org.mmbase.core.event.Event {
        private Event() {};

    }
    /**
     * No further structure, so it can as well be a singleton
     */
    public static Event EVENT = new Event();


    /**
     * The associated listener
     */
    public static interface Listener extends EventListener {
        void notify(NewSearcher.Event event);

    }
    /**
     * The associated broker
     */
    public static class Broker extends AbstractEventBroker {

        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof NewSearcher.Listener;
        }
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof NewSearcher.Event;
        }
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            NewSearcher.Event ne = (NewSearcher.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
    }




}