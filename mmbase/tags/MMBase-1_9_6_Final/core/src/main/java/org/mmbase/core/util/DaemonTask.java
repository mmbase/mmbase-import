/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

/**
 * Defines a task to run in a daemon thread.
 * A task should define a {@link #executeTask()} method and a {@link #getSleepPeriod()} method to define it's behavior.
 *
 * @since MMBase-1.8
 * @deprecated  This class copies functionality present in
 * e.g. java.util.concurrent.ScheduledThreadPoolExecutor (a thread pool which can be used is e.g. {@link org.mmbase.util.ThreadPools#scheduler}).
 */
public interface DaemonTask {

    /**
     * Returns this task's sleep period.
     */
    public int getSleepPeriod();

    /**
     * Defines a task that need be run by a daemon thread's run() method.
     */
    public void executeTask();

}
