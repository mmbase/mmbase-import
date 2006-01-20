/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;
import java.util.*;

/**
 * A Runnable wich also has an 'interrupt' method.
 * @author Michiel Meeuwissen
 */

public class Interruptable implements Runnable {

    private Thread runThread = null;
    private Date   startTime;
    private final Runnable runnable;
    private final Collection collection;
    public Interruptable(Runnable run, Collection col) {
        runnable = run;
        collection = col;
    }

    public void run() {
        if (runThread != null) throw new IllegalStateException();
        if (collection != null) collection.add(this);
        runThread = Thread.currentThread();
        startTime = new Date();
        runnable.run();
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
