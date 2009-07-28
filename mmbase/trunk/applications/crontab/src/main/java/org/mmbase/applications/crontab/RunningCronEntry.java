/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.crontab;

import org.mmbase.core.event.*;
import java.util.concurrent.*;
import java.util.Date;

import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class RunningCronEntry  implements Delayed, java.io.Serializable {

    static final long serialVersionUID = 7750051360512560297L;

    protected final CronEntry entry;
    protected final Date started;
    protected final int thread;
    protected final String machine;
    protected final String message;

    public RunningCronEntry(CronEntry entry, Date started, String machine, int thread, String message) {
        this.entry = entry;
        this.started = started;
        this.thread = thread;
        this.machine = machine;
        this.message = message;
    }

    public RunningCronEntry(CronEntry entry, Date started, String machine, int thread) {
        this(entry, started, machine, thread, null);
    }
    public CronEntry getCronEntry() {
        return entry;
    }

    /**
     * Time when this jobs was supposed to be started. Optional, and not essential for equals. Jobs
     * is uniquely identify by id and thread.
     */
    public Date getStart() {
        return started;
    }
    public int getId() {
        return thread;
    }
    public String getMachine() {
        return machine;
    }

    public String getMessage() {
        return message;
    }

    public boolean equals(Object o) {
        if (o instanceof RunningCronEntry) {
            RunningCronEntry e = (RunningCronEntry) o;
            return getId() == e.getId()  &&
                getMachine().equals(e.getMachine()) &&
                getCronEntry().equals(e.getCronEntry());
        } else {
            return false;
        }
    }
    public long getDelay(TimeUnit unit) {
        long delay = getStart().getTime() + entry.getMaxDuration() - System.currentTimeMillis();
        if (delay < 0) delay = 0;
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }
    public int compareTo(Delayed d) {
        return (int) (getDelay(TimeUnit.MILLISECONDS) - d.getDelay(TimeUnit.MILLISECONDS));
    }

    public String toString() {
        return  getMachine() + ":" + thread + ":" + entry.getId();
    }


}
