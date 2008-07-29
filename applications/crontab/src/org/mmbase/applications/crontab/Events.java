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
 * @version $Id: Events.java,v 1.4 2008-07-29 20:56:13 michiel Exp $
 */

public class Events {


    public static final int STARTED = 100;
    public static final int DONE    = 101;
    public static final int INTERRUPTED    = 102;
    public static final int INTERRUPT      = 103;

    public static class Event extends org.mmbase.core.event.Event implements Delayed {

        protected final CronEntry entry;
        protected final Date started;
        protected final int thread;
        protected final String destination;

        public Event(CronEntry entry, Date started, int type, int thread, String destination) {
            super(null, type);
            this.entry = entry;
            this.started = started;
            this.thread = thread;
            this.destination = destination;
        };
        public CronEntry getCronEntry() {
            return entry;
        }

        public Date getStart() {
            return started;
        }
        public int getId() {
            return thread;
        }
        public String getDestination() {
            return destination;
        }

        public String toString() {
            return getMachine() + ":" + thread + ":" + entry.getId();
        }
        public long getDelay(TimeUnit unit) {
            long delay = started.getTime() + entry.getMaxDuration() - System.currentTimeMillis();
            if (delay < 0) delay = 0;
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }
        public int compareTo(Delayed d) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - d.getDelay(TimeUnit.MILLISECONDS));

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
            return "Crontab Events Broker";
        }
    }


}
