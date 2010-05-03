/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.crontab;

import org.mmbase.core.event.*;


/**

 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class Events {


    public static final int STARTED = 100;
    public static final int DONE    = 101;
    public static final int INTERRUPTED    = 102;
    public static final int INTERRUPT      = 103;

    public static class Event extends org.mmbase.core.event.Event {

        protected final RunningCronEntry entry;

        public Event(RunningCronEntry entry, int type) {
            super(null, type);
            this.entry = entry;
        };
        public RunningCronEntry getEntry() {
            return entry;
        }


        @Override
        public String toString() {
            return getMachine() + ":" + entry;
        }

    }



    /**
     * The associated listener
     */
    public static interface Listener extends EventListener {
        void notify(Events.Event event);

    }
    /**
     * The associated broker
     */
    public static class Broker extends AbstractEventBroker {

        @Override
        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof Events.Listener;
        }
        @Override
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof Events.Event;
        }
        @Override
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            Events.Event ne = (Events.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
        @Override
        public String toString() {
            return "Crontab Events Broker";
        }
    }


}
