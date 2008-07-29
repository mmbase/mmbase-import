/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.crontab;

import org.mmbase.core.event.*;
import org.mmbase.util.HashCodeUtil;
import java.util.Date;
import java.util.concurrent.*;

import org.mmbase.util.logging.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id: Events.java,v 1.1 2008-07-29 15:42:24 michiel Exp $
 */

public class Events {


    public static int STARTED = 100;
    public static int DONE    = 101;

    public static class Event extends org.mmbase.core.event.Event {

        protected final CronEntry entry;

        public Event(CronEntry entry, int type) {
            super(null, type);
            this.entry = entry;
        };
        public CronEntry getCronEntry() {
            return entry;
        }

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

        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof Events.Listener;
        }
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof Events.Event;
        }
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            Events.Event ne = (Events.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
        public String toString() {
            return "Lucene Full Index Broker";
        }
    }


}
