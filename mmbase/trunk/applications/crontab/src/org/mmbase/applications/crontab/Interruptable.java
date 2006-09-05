/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * A Runnable wich also has an 'interrupt' method. This only works well
 * if the job does sleeps (InterruptedException) or check Thread.isInterrupted().
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: Interruptable.java,v 1.3 2006-09-05 16:24:06 michiel Exp $
 */

public class Interruptable implements Runnable {
    private static final Logger log = Logging.getLoggerInstance(Interruptable.class);
    private Thread runThread = null;
    private Date   startTime;
    private final Runnable runnable;
    private final Collection<Interruptable> collection;
    public Interruptable(Runnable run, Collection<Interruptable> col) {
        runnable = run;
        collection = col;
    }

    public void run() {
        if (runThread != null) throw new IllegalStateException();
        if (collection != null) collection.add(this);
        runThread = Thread.currentThread();
        startTime = new Date();
        try {
            runnable.run();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }


        runThread = null;
        if (collection != null) collection.remove(this);
    }

    public boolean interrupt() {
        Thread t = runThread;
        if (t != null) {
            t.interrupt();
            return true;
        }
        return false;
    }
    public boolean isAlive() {
        Thread t = runThread;
        return t != null && t.isAlive();
    }
    public Date getStartTime() {
        return startTime;
    }
}
