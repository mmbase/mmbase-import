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
 * @version $Id: CronEntry.java,v 1.3 2006-01-02 22:01:05 michiel Exp $
 */

public class CronEntry {

    private static final Logger log = Logging.getLoggerInstance(CronEntry.class);

    /**
     * A CronEntry of this type will run without the overhead of an extra thread. This does mean
     * though that such a job will halt the cron-daemon itself.  Such jobs must therefore be
     * extremely short-living, and used with care (only if you have a lot of those which must run
     * very often)
     */
    public static final int SHORT_JOB_TYPE = 0;

    public static final String SHORT_JOB_TYPE_STRING = "short";

    /**
     * The default job type is the 'must be one' job. Such jobs are not started if the same job is
     * still running. They are wrapped in a seperate thread, so other jobs can be started during the
     * execution of this one.
     */
    public static final int MUSTBEONE_JOB_TYPE = 1;

    public static final String MUSTBEONE_JOB_TYPE_STRING = "mustbeone";

    /**
     * The 'can be more' type job is like a 'must be one' job, but the run() method of such jobs is even
     * called (when scheduled) if it itself is still running.
     */
    public static final int CANBEMORE_JOB_TYPE = 2;

    public static final String CANBEMORE_JOB_TYPE_STRING = "canbemore";

    public static final int DEFAULT_JOB_TYPE = MUSTBEONE_JOB_TYPE;

    public static final String DEFAULT_JOB_TYPE_STRING = MUSTBEONE_JOB_TYPE_STRING;

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
    private CronEntryField dayOfMonth; // 1-31
    private CronEntryField month; // 1-12
    private CronEntryField dayOfWeek; // 0-7 (0 or 7 is sunday)

    private int type = DEFAULT_JOB_TYPE;

    public CronEntry(String id, String cronTime, String name, String className, String configuration) throws Exception {
        this(id, cronTime, name, className, configuration, DEFAULT_JOB_TYPE);
    }

    public CronEntry(String id, String cronTime, String name, String className, String configuration, String typeString) throws Exception {
        this(id, cronTime, name, className, configuration, stringToJobType(typeString));
    }

    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     * @throws RuntimeException if the cronTime format isn't correct
     */
    public CronEntry(String id, String cronTime, String name, String className, String configuration, int type) throws Exception {
        this.id = id;
        this.name = name == null ? "" : name;
        this.className = className;
        this.cronTime = cronTime;
        this.configuration = configuration;
        this.type = type;

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
        switch (type) {
            case SHORT_JOB_TYPE :
                {
                    count++;
                    try {
                        cronJob.run();
                    } catch (Throwable t) {
                        log.error("Error during cron-job " + this +" : " + t.getClass().getName() + " " + t.getMessage() + "\n" + Logging.stackTrace(t));
                    }
                    return true;
                }
            case MUSTBEONE_JOB_TYPE :
                if (isAlive()) {
                    return false;
                }
                // fall through
            case CANBEMORE_JOB_TYPE :
            default :
                org.mmbase.util.ThreadPools.jobsExecutor.execute(cronJob);
                return true;
        }

    }

    protected void setCronTime(String cronTime) {
        StringTokenizer st = new StringTokenizer(cronTime, " ");
        if (st.countTokens() != 5) {
            throw new RuntimeException("A crontime must contain 5 field  please refer to the UNIX man page http://www.rt.com/man/crontab.5.html");
        }

        minute.setTimeVal(st.nextToken());
        hour.setTimeVal(st.nextToken());
        dayOfMonth.setTimeVal(st.nextToken());
        month.setTimeVal(st.nextToken());
        String dow = st.nextToken();

        dayOfWeek.setTimeVal(dow);
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
        return id + ":" + cronTime + ":" + name + ": " + className + ":" + configuration + ": count" + count + " type " + jobTypeToString(type);
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


    /**
     * Convert a jobType int to a jobType String. invalid types are accepted and return DEFAULT_JOB_TYPE_STRING
     * @param type the job type
     * @return The string representation of the job type
     */
    public static String jobTypeToString(int type) {
        switch (type) {
        case SHORT_JOB_TYPE :
            return SHORT_JOB_TYPE_STRING;
        case MUSTBEONE_JOB_TYPE :
            return MUSTBEONE_JOB_TYPE_STRING;
        case CANBEMORE_JOB_TYPE :
            return CANBEMORE_JOB_TYPE_STRING;
        }
        return DEFAULT_JOB_TYPE_STRING;
    }

    /**
     * Convert a jobType String to a jobType int. first the string is lowered cased and trimed.
     * null values and invalid values are accepted and return the DEFAULT_JOB_TYPE
     * @param type the string representation of the job type
     * @return the int representation of the jobType
     */
    public static int stringToJobType(String type) {

        if (type == null) {
            return DEFAULT_JOB_TYPE;
        }
        type = type.toLowerCase().trim();

        if (type.equals(SHORT_JOB_TYPE_STRING)) {
            return SHORT_JOB_TYPE;
        } else if (type.equals(MUSTBEONE_JOB_TYPE_STRING)) {
            return MUSTBEONE_JOB_TYPE;
        } else if (type.equals(CANBEMORE_JOB_TYPE_STRING)) {
            return CANBEMORE_JOB_TYPE;
        }

        return DEFAULT_JOB_TYPE;
    }

}
