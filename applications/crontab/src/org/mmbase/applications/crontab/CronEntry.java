/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import java.util.*;
import java.util.regex.*;
import org.mmbase.module.core.MMBase;

import org.mmbase.util.logging.*;

/**
 * Defines one entry for CronDaemon. This class is used by the CronDaemon.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: CronEntry.java,v 1.12 2008-07-29 10:01:21 michiel Exp $
 */

public class CronEntry {

    private static final Logger log = Logging.getLoggerInstance(CronEntry.class);

    public static final Pattern ALL = Pattern.compile(".*");


    public enum Type {
        /**
         * A CronEntry of this type will run without the overhead of an extra thread. This does mean
         * though that such a job will halt the cron-daemon itself.  Such jobs must therefore be
         * extremely short-living, and used with care (only if you have a lot of those which must run
         * very often)
         *
         * Since we use a thread-pool for other types of jobs now any way, it is doubtfull if it is
         * ever usefull to opt for this type.
         */
        SHORT,
        /**
         * The default job type is the 'must be one' job. Such jobs are not started if the same job is
         * still running. They are wrapped in a seperate thread, so other jobs can be started during the
         * execution of this one.
         */
        MUSTBEONE,
        /**
         * The 'can be more' type job is like a 'must be one' job, but the run() method of such jobs is even
         * called (when scheduled) if it itself is still running.
         */
        CANBEMORE;

        public static Type DEFAULT = MUSTBEONE;
        public static Type valueOf(int i) {
            if (i == -1) return DEFAULT;
            if (i < 0 || i >= Type.values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return Type.values()[i];
        }
     }


    private CronJob cronJob;

    private List<Interruptable> threads = Collections.synchronizedList(new ArrayList<Interruptable>());

    private final String id;
    private final String name;
    private final String className;
    protected final String cronTime;
    private String configuration = null;

    protected Date  lastRun = new Date(0);
    protected int count = 0;
    protected int lastCost = -1;

    private final CronEntryField second      = new CronEntryField(); // 0-59
    private final CronEntryField minute      = new CronEntryField(); // 0-59
    private final CronEntryField hour        = new CronEntryField(); // 0-23
    private final CronEntryField dayOfMonth  = new CronEntryField(); // 1-31
    private final CronEntryField month       = new CronEntryField(); // 1-12
    private final CronEntryField dayOfWeek   = new CronEntryField(); // 0-7 (0 or 7 is sunday)

    private Type type = Type.DEFAULT;

    private final Pattern servers;

    public CronEntry(String id, String cronTime, String name, String className, String configuration) throws Exception {
        this(id, cronTime, name, className, configuration, Type.DEFAULT);
    }

    public CronEntry(String id, String cronTime, String name, String className, String configuration, String typeString) throws Exception {
        this(id, cronTime, name, className, configuration, typeString == null ? Type.DEFAULT : Type.valueOf(typeString.toUpperCase()));
    }
    public CronEntry(String id, String cronTime, String name, String className, String configuration, String typeString, Pattern servers) throws Exception {
        this(id, cronTime, name, className, configuration, typeString == null  ? Type.DEFAULT : Type.valueOf(typeString.toUpperCase()), servers);
    }

    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     * @throws RuntimeException if the cronTime format isn't correct
     */
    public CronEntry(String id, String cronTime, String name, String className, String configuration, Type type) throws Exception {
        this(id, cronTime, name, className, configuration, type, ALL);
    }


    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     * @throws RuntimeException if the cronTime format isn't correct
     */
    public CronEntry(String id, String cronTime, String name, String className, String configuration, Type type, Pattern servers) throws Exception {
        this.id = id;
        this.name = name == null ? "" : name;
        this.className = className;
        this.cronTime = cronTime;
        this.configuration = configuration;
        this.type = type;

        Runnable runnable = (Runnable) Class.forName(className).newInstance();
        if (! (runnable instanceof CronJob)) {
            cronJob = new RunnableCronJob(runnable);
        } else {
            cronJob = (CronJob) runnable;
        }

        setCronTime(cronTime);

        this.servers = servers;
    }

    public void init() {
        cronJob.init(this);
    }

    public void stop() {
        synchronized(threads) {
            for(Interruptable thread : threads) {
                thread.interrupt();
            }
        }
        cronJob.stop();
    }
    /**
     * @since MMBase-1.8
     */
    public Interruptable getThread(int i) {
        synchronized(threads) {
            if (threads.size() <= i) return null;
            return threads.get(i);
        }
    }
    public List<Interruptable> getThreads() {
        return new ArrayList<Interruptable>(threads);
    }
    /**
     * @since MMBase-1.8
     */
    public boolean isAlive(int i) {
        Interruptable t = getThread(i);
        return t != null && t.isAlive();

    }
    public boolean isAlive() {
        return isAlive(0);
    }

    public boolean kick() {
        final Date start = new Date();
        Runnable ready = new Runnable() {
                public void run() {
                    CronEntry.this.incCount();
                    CronEntry.this.setLastCost((int) (new Date().getTime() - start.getTime()));
                }
            };
        switch (type) {
        case SHORT:
            {
                try {
                    setLastRun(new Date());
                    Interruptable thread = new Interruptable(cronJob, threads, ready);
                    thread.run();
                } catch (Throwable t) {
                    log.error("Error during cron-job " + this +" : " + t.getClass().getName() + " " + t.getMessage() + "\n" + Logging.stackTrace(t));
                }
                return true;
            }
        case MUSTBEONE:
            if (isAlive()) {
                return false;
            }
            // fall through
        case CANBEMORE:
        default :
            setLastRun(start);
            Interruptable thread = new Interruptable(cronJob, threads, ready);
            org.mmbase.util.ThreadPools.jobsExecutor.execute(thread);
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
    public String getType() {
        return type.toString().toLowerCase();
    }
    public String getClassName() {
        return className;
    }
    public Date getLastRun() {
        return lastRun;
    }

    protected void setLastRun(Date d) {
        lastRun = d;
        setLastCost(-1);
    }
    public int getCount() {
        return count;
    }
    protected void incCount() {
        count++;
    }
    public int getLastCost() {
        return lastCost;
    }
    protected void setLastCost(int s) {
        lastCost = s;
    }

    boolean mustRun(Date date) {
        String machineName = MMBase.getMMBase().getMachineName();

        if (! servers.matcher(machineName).matches()) {
            log.debug("This cron entry " + this + " must not run because this machine " + machineName + " does not match " + servers);
            return false;
        } else {
            log.debug(" " + machineName + " matched " + servers + " so must run");
        }
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
        return id + ":" + cronTime + ":" + name + ": " + className + ":" + configuration + ": count" + count + " type " + type + " on servers " + servers;
    }

    public int hashCode() {
        return id.hashCode() + name.hashCode() + className.hashCode() + cronTime.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof CronEntry)) {
            return false;
        }
        CronEntry other = (CronEntry)o;
        return id.equals(other.id) && name.equals(other.name) && className.equals(other.className) && cronTime.equals(other.cronTime) && servers.equals(other.servers)  && (configuration == null ? other.configuration == null : configuration.equals(other.configuration));
    }




}
