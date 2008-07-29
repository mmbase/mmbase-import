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

/**

 * @author Michiel Meeuwissen
 * @version $Id: ProposedJobs.java,v 1.1 2008-07-29 13:36:34 michiel Exp $
 */

public class ProposedJobs {

    public static int TYPE_PROPOSE = 100;
    public static int TYPE_DONE    = 101;

    public static class Event extends org.mmbase.core.event.Event implements Delayed, java.io.Serializable {

        private static final long WAIT = 30 * 1000;

        protected final CronEntry entry;
        protected final Date cronStart;
        protected final long maxDuration;

        public Event(CronEntry entry, Date s, long duration) {
            this.entry = entry;
            this.cronStart = s;
            this.maxDuration = duration;
        };
        public CronEntry getCronEntry() {
            return entry;
        }
        public Date getCronStart() {
            return cronStart;
        }
        public long getMaxDuration() {
            return maxDuration;
        }

        public boolean equals(Object o) {
            if (o instanceof Event) {
                Event other = (Event) o;
                return
                    other.getCronEntry().equals(entry) &&
                    other.getCronStart() == cronStart;
            } else {
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
            return "Lucene Full Index Broker";
        }
    }


}
