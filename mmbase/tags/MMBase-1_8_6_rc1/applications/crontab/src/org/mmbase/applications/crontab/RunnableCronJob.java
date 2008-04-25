/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

/**
 * Just wraps a Runnable into a CronJob.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class RunnableCronJob extends AbstractCronJob {

    protected final Runnable runnable;

    public RunnableCronJob(Runnable run) {
        runnable = run;
    }
    public void run() {
        runnable.run();
    }
}
