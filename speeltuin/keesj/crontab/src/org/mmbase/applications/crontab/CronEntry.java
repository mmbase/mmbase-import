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
 * Defines one entry for CronDaemon.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: CronEntry.java,v 1.4 2004-05-24 13:00:50 keesj Exp $
 */

public class CronEntry {

    private static final Logger log = Logging.getLoggerInstance(CronEntry.class);

    private Runnable cronJob;

    private Thread thread;

    private String id;
    private String name;
    private String className;
    private String cronTime;
    private String configuration = null;

    private int count = 0;

    private CronEntryField second; // 0-59
    private CronEntryField minute; // 0-59
    private CronEntryField hour; // 0-23
    private CronEntryField dayOfMonth; //1-31
    private CronEntryField month; //1-12
    private CronEntryField dayOfWeek; //0-7 (0 or 7 is sunday)

    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     */
    public CronEntry(String id, String cronTime, String name, String className, String configuration) throws Exception {
        this.id = id;
        this.name = name;
        if (this.name == null)
            this.name = "";
        this.className = className;
        this.cronTime = cronTime;
        this.configuration = configuration;
        cronJob = (Runnable)Class.forName(className).newInstance();

        second = new CronEntryField();
        minute = new CronEntryField();
        hour = new CronEntryField();
        dayOfMonth = new CronEntryField();
        month = new CronEntryField();
        dayOfWeek = new CronEntryField();
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
        if (isAlive()) {
            return false;
        } else {
            count++;
            thread = new ExceptionLoggingThread(cronJob, "JCronJob " + toString());
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
        return id + ":" + cronTime + ":" + name + ": " + className + ":" + configuration + ": " + count;
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
