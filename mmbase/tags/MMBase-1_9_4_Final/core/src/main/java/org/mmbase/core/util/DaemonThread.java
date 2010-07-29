/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;

/**
 * Defines a daemon thread that runs in the threadgroup belonging to this MMBase context.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id$
 * @deprecated  This class copies functionality present in
 * e.g. java.util.concurrent.ScheduledThreadPoolExecutor (a thread pool which can be used is
 * e.g. {@link org.mmbase.util.ThreadPools#scheduler}). If scheduling is not used, then only the
 * thread group is special, which can just as well be obtained by {@link MMBaseContext#getThreadGroup}.
 */
public class DaemonThread extends Thread implements DaemonTask  {

    /**
     * Default sleep period for a daemon thread (one minute).
     */
    public static final int DEFAULT_SLEEP_PERIOD = 60000;

    private static final Logger log = Logging.getLoggerInstance(DaemonThread.class);

    /**
     * The threads sleep period.
     * This period is used when a Daemonthread runs on its own (that is, without an assigned task)
     * When a DaemonThread is assigned a task, it uses the sleep period of that task.
     */
    protected int sleepPeriod = DEFAULT_SLEEP_PERIOD;

    private Runnable target = null;
    private DaemonTask task = null;
    private boolean running = false;

    /**
     * Create a MMBase daemon thread (associated with this MMBase's threadgroup).
     */
    public DaemonThread() {
        this((Runnable)null, (String)null);
    }

    /**
     * Create a MMBase daemon thread (associated with this MMBase's threadgroup).
     * @param name the name of the thread
     */
    public DaemonThread(String name) {
        this((Runnable)null, name);
    }

    /**
     * Create a MMBase daemon thread (associated with this MMBase's threadgroup).
     * @param target the target thread
     * @param name the name of the thread
     */
    public DaemonThread(Runnable target, String name) {
        super(MMBaseContext.getThreadGroup(), target, MMBaseContext.getMachineName() + ":" + name);
        this.target = target;
        setDaemon(true);
    }

    /**
     * Sets the task this thread should run when started.
     * @param task the task to run
     */
    public void setTask(DaemonTask task) {
        this.task = task;
    }

    /**
     * Returns the task this thread runs when started.
     */
    public DaemonTask getTask() {
        return task;
    }

    public int getSleepPeriod() {
        if (task != null) {
            return task.getSleepPeriod();
        } else {
            return sleepPeriod;
        }
    }

    public void start() {
        running = true;
        log.service("Starting " + getName());
        super.start();
    }

    public void interrupt() {
        running = false;
        super.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public void executeTask() {
        if (task != null) {
            task.executeTask();
        } else {
            throw new UnsupportedOperationException("No execute task defined");
        }
    }

    /**
     * Default behavior (when no target is specified) is to run continuously until interrupted.
     */
    public void run() {
        if (target != null) {
            target.run();
        } else {
            while (isRunning()) {
                try {
                    Thread.sleep(getSleepPeriod());
                    executeTask();
                } catch (InterruptedException e){
                    log.debug(Thread.currentThread().getName() +" was interrupted.");
                }
            }
        }
    }

}

