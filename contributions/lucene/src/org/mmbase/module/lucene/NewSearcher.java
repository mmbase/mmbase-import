/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.core.event.*;


public class NewSearcher {

    public static class Event extends org.mmbase.core.event.Event {

    }

    public static interface Listener extends EventListener {
        void notify(NewSearcher.Event event);

    }

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