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
 * Defines one entry for CronDaemon. This class is used by the CronDaemon.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: CronEntry.java,v 1.5 2004-09-23 17:20:36 michiel Exp $
 */

public class CronEntry {

    private static final Logger log = Logging.getLoggerInstance(CronEntry.class);

    /**
     * A CronEntry of this type will run without the overhead of an extra thread. This does mean
     * though that such a job will halt the cron-daemon itself.  Such jobs must therefore be
     * extremely short-living, and used with care (only if you have a lot of those which must run
     * very often)
     */
    public static final int SHORT_JOB      = 0;

    /**
     * The default job type is the 'must be one' job. Such jobs are not started if the same job is
     * still running. They are wrapped in a seperate thread, so other jobs can be started during the
     * execution of this one.
     */
    public static final int MUSTBEONE_JOB  = 1;

    /**
     * The 'can be more' type job is like a 'must be one' job, but the run() method of such jobs is even
     * called (when scheduled) if it itself is still running.
     */
    public static final int CANBEMORE_JOB  = 2;

    private Runnable cronJob;

    private Thread thread;

    private String id;
    private String name;
    private String className;
    private String cronTime;
    private String configuration = null;

    private int count = 0;

    private CronEntryField second;     // 0-59
    private CronEntryField minute;     // 0-59
    private CronEntryField hour;       // 0-23
    private CronEntryField dayOfMonth; // 1-31
    private CronEntryField month;      // 1-12
    private CronEntryField dayOfWeek;  // 0-7 (0 or 7 is sunday)

    private int type = MUSTBEONE_JOB;

    public CronEntry(String id, String cronTime, String name, String className, String configuration) throws Exception {
        this(id, cronTime, name, className, configuration, MUSTBEONE_JOB);
    }

    public CronEntry(String id, String cronTime, String name, String className, String configuration, String type) throws Exception {
        this(id, cronTime, name, className, configuration);
        if (type != null) {
            type = type.toLowerCase();
            if ("short".equals(type)) {
                this.type = SHORT_JOB;
            } else if ("mustbeone".equals(type)) {
                this.type = MUSTBEONE_JOB;
            } else if ("canbemore".equals(type)) {
                this.type = CANBEMORE_JOB;
            }
        }
    }

    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     */
    public CronEntry(String id, String cronTime, String name, String className, String configuration, int type) throws Exception {
        this.id            = id;
        this.name          = name == null ? "" : name;
        this.className     = className;
        this.cronTime      = cronTime;
        this.configuration = configuration;
        this.type       = type;

        cronJob = (Runnable) Class.forName(className).newInstance();

        second     = new CronEntryField();
        minute     = new CronEntryField();
        hour       = new CronEntryField();
        dayOfMonth = new CronEntryField();
        month      = new CronEntryField();
        dayOfWeek  = new CronEntryField();

        setCronTime(cronTime);
    }

    public void init() {
        if (cronJob instanceof CronJob) {
            ((CronJob)cronJob).init(this);
        }
    }

    public void stop() {
        if (cronJob instanceof CronJob) {
            ((CronJob)cronJob).stop();
        }
    }

    protected boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    protected boolean kick() {
        switch(type) {
        case SHORT_JOB: {
            count++; 
            try {
                cronJob.run();
            } catch (Throwable t) {
                log.error("Error during cron-job " + this +" : " + t.getClass().getName() + " " + t.getMessage() + "\n" + Logging.stackTrace(t));
            }
            return true;
        }
        case MUSTBEONE_JOB:
            if (isAlive()) {
                return false;
            } 
            // fall through
        case CANBEMORE_JOB:
        default:
            thread = new ExceptionLoggingThread(cronJob, "CronJob " + toString());
            thread.setDaemon(true);
            thread.start();
            return true;
        }


    }

    protected void setCronTime(String cronTime) {
        StringTokenizer st = new StringTokenizer(cronTime, " ");
        if (st.countTokens() > 5) {
            throw new RuntimeException("Too many (" + st.countTokens() + "> 6)  tokens in " + cronTime);
        }

        minute.setTimeVal(st.nextToken());
        hour.setTimeVal(st.nextToken());
        dayOfMonth.setTimeVal(st.nextToken());
        month.setTimeVal(st.nextToken());
        dayOfWeek.setTimeVal(st.nextToken());
    }

    public String getCronTime() {
        return cronTime;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setConfiguration(String conf) {
        configuration = conf;
    }
    public String getConfiguration() {
        return configuration;
    }

    boolean mustRun(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (minute.valid(cal.get(Calendar.MINUTE))
            && hour.valid(cal.get(Calendar.HOUR_OF_DAY))
            && dayOfMonth.valid(cal.get(Calendar.DAY_OF_MONTH))
            && month.valid(cal.get(Calendar.MONTH) + 1)
            && dayOfWeek.valid(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
            return true;
        }
        return false;
    }

    public CronEntryField getMinuteEntry() {
        return minute;
    }

    public CronEntryField getHourEntry() {
        return hour;
    }

    public CronEntryField getDayOfMonthEntry() {
        return dayOfMonth;
    }

    public CronEntryField getMonthEntry() {
        return month;
    }

    public CronEntryField getDayOfWeekEntry() {
        return dayOfWeek;
    }

    public String toString() {
        return id + ":" + cronTime + ":" + name + ": " + className + ":" + configuration + ": count" + count + " type " + type;
    }

    public int hashCode() {
        return id.hashCode() + name.hashCode() + className.hashCode() + cronTime.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof CronEntry)) {
            return false;
        }
        CronEntry other = (CronEntry)o;
        return id.equals(other.id) && name.equals(other.name) && className.equals(other.className) && cronTime.equals(other.cronTime);
    }

    private class ExceptionLoggingThread extends Thread {
        ExceptionLoggingThread(Runnable run, String name) {
            super(run, name);
        }

        /**
         * Overrides run of Thread to catch and log all exceptions. Otherwise they go through to app-server.
         */
        public void run() {
            try {
                super.run();
            } catch (Throwable t) {
                log.error("Error during cron-job " + CronEntry.this +" : " + t.getClass().getName() + " " + t.getMessage() + "\n" + Logging.stackTrace(t));
            }
        }
    }
}
