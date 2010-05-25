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
 * Wraps a CronEntry with a proposed start date.
 *
 * @version $Id$
 */

public class ProposedJobs {
    private static final Logger log = Logging.getLoggerInstance(ProposedJobs.class);

    public static class Event extends org.mmbase.core.event.Event implements Delayed, java.io.Serializable {

        private static final long WAIT = 30 * 1000;

        protected final CronEntry entry;
        protected final Date cronStart;
        protected boolean ready = false;

        public Event(CronEntry entry, Date s) {
            this.entry = entry;
            this.cronStart = s;
        };
        public CronEntry getCronEntry() {
            return entry;
        }
        public Date getCronStart() {
            return cronStart;
        }

        public void setReady() {
            ready = true;
        }

        public boolean equals(Object o) {
            if (o instanceof Event) {
                Event other = (Event) o;
                return
                    other.getCronEntry().equals(entry) &&
                    other.getCronStart().equals(cronStart);
            } else {
                log.debug("no");
                return false;
            }
        }
        public int hashCode() {
            int result = entry.hashCode();
            result = HashCodeUtil.hashCode(result, cronStart);
            return result;
        }
        public long getDelay(TimeUnit unit) {
            long delay = cronStart.getTime() + WAIT - System.currentTimeMillis();
            if (delay < 0) delay = 0;
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }
        public int compareTo(Delayed d) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - d.getDelay(TimeUnit.MILLISECONDS));

        }

        public String toString() {
            return getMachine() + ":" + cronStart + ":" + entry;
        }

    }



    /**
     * The associated listener
     */
    public static interface Listener extends EventListener {
        void notify(ProposedJobs.Event event);

    }
    /**
     * The associated broker
     */
    public static class Broker extends AbstractEventBroker {

        public boolean canBrokerForListener(EventListener listener) {
            return listener instanceof ProposedJobs.Listener;
        }
        public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
            return event instanceof ProposedJobs.Event;
        }
        protected void notifyEventListener(org.mmbase.core.event.Event event, EventListener listener) {
            ProposedJobs.Event ne = (ProposedJobs.Event) event; //!!!!!
            Listener nel = (Listener) listener;
            nel.notify(ne);
        }
        public String toString() {
            return "Crontab Proposed Jobs Broker";
        }
    }


}
