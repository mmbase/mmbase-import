/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.core.event.*;

/**
 * All the code necessary to propagate events with the meaning 'the lucene index has changed, you must refresh your searchers'.
 * @author Michiel Meeuwissen
 * @version $Id: NewSearcher.java,v 1.6 2008-07-28 13:03:36 michiel Exp $
 */

public class NewSearcher {

    public static class Event extends org.mmbase.core.event.Event {

        protected final String index;
        /**
         * The event itself
         */
        public Event(String i) {
            index = i;
        };
        public String getIndex() {
            return index;
        }
        public String toString() {
            return "new searcher for index '" + index + "'" + (isLocal() ? "" : (" from " + getMachine()));
         }

    }

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
        public String toString() {
            return "Lucene New Searcher Broker";
        }
    }




}
