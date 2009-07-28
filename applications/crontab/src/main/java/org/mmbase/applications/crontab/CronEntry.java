/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import java.util.*;
import java.util.regex.*;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.HashCodeUtil;

import org.mmbase.core.event.EventManager;
import org.mmbase.util.logging.*;

/**
 * Defines one entry for CronDaemon. This class is used by the CronDaemon.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CronEntry implements java.io.Serializable {


    private static final long serialVersionUID = 5523591459873053633L;

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
        SHORT, //0
        /**
         * The default job type is the 'must be one' job. Such jobs are not started if the same job is
         * still running. They are wrapped in a seperate thread, so other jobs can be started during the
         * execution of this one.
         */
         MUSTBEONE, //1
        /**
         * The 'can be more' type job is like a 'must be one' job, but the run() method of such jobs is even
         * called (when scheduled) if it itself is still running.
         */
         CANBEMORE, //2


         /**
          * Don't run at all
          */
          DISABLED,
        /**
         * A job of this type runs exactly once in the load balanced mmbase cluster. Before the job
         * is started, communication between mmbase's in the server will be done, to negotiate who
         * is going to do it.
         */
         BALANCE,

         /**
          * As BALANCED, but no job is started as the previous was not yet finished.
          */
         BALANCE_MUSTBEONE;



        public static Type DEFAULT = MUSTBEONE;
        public static Type valueOf(int i) {
            if (i == -1) return DEFAULT;
            if (i < 0 || i >= Type.values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return Type.values()[i];
        }
     }


    private transient CronJob cronJob;

    private transient List<Interruptable> threads = Collections.synchronizedList(new ArrayList<Interruptable>());

    private final String id;
    private final String name;
    private final String className;
    protected final String cronTime;
    private String configuration = null;

    protected Date  lastRun = new Date(0);
    protected int count = 0;
    protected int lastCost = -1;

    protected long maxDuration = Long.MAX_VALUE;


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
        this(id, cronTime, name, className, configuration, (typeString == null || "".equals(typeString)) ? Type.DEFAULT : Type.valueOf(typeString.toUpperCase()));
    }
    public CronEntry(String id, String cronTime, String name, String className, String configuration, String typeString, Pattern servers) throws Exception {
        this(id, cronTime, name, className, configuration, (typeString == null  || "".equals(typeString)) ? Type.DEFAULT : Type.valueOf(typeString.toUpperCase()), servers);
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
            for (Interruptable in : threads) {
                if (in.getId() == i) return in;
            }
            return null;
        }
    }
    public List<Interruptable> getThreads() {
        return new ArrayList<Interruptable>(threads);
    }

    public boolean interrupt(int thread) {
        Interruptable t = getThread(thread);
        boolean r = t != null && t.interrupt();
        if (r) {
            EventManager.getInstance().propagateEvent(new Events.Event(new RunningCronEntry(CronEntry.this, t.getStartTime(), MMBaseContext.getMachineName(), t.getId()), Events.INTERRUPTED));
        }
        return r;
    }

    /**
     * Wether a job associated with this cron entry is currently alive on this machine.
     * @since MMBase-1.8
     */
    public boolean isAlive(int i) {
        Interruptable t = getThread(i);
        return t != null && t.isAlive();

    }
    public boolean isAlive() {
        return isAlive(0);
    }

    /**
     * Whether this Entry would run. It would not run if it is only scheduled to run on other machines.
     * @since MMBase-1.8.7
     */
    public boolean isActive() {
        String machineName = MMBaseContext.getMachineName();
        return servers.matcher(machineName).matches();
    }

    public boolean isMustBeOne() {
        return type == Type.MUSTBEONE || type == Type.BALANCE_MUSTBEONE;
    }
    /**
     * A String indicating on the servers on wich this Job must run. This may be regular expression
     * and used in the implementation of {@link #isActive}, but this is not required.
     */
    public String getServers() {
        return servers.pattern();
    }


    Interruptable getExecutable() {
        final Date start = new Date();
        Interruptable.CallBack ready = new Interruptable.CallBack() {
                public void run(Interruptable i) {
                    CronEntry.this.incCount();
                    CronEntry.this.setLastCost((int) (new Date().getTime() - start.getTime()));
                    String message = i.getRunException() == null ? null : i.getRunException().getMessage();
                    EventManager.getInstance().propagateEvent(new Events.Event(new RunningCronEntry(CronEntry.this, start, MMBaseContext.getMachineName(), i.getId(), message), Events.DONE));
                }
            };
        Interruptable.CallBack begin = new Interruptable.CallBack() {
                public void run(Interruptable i) {
                    EventManager.getInstance().propagateEvent(new Events.Event(new RunningCronEntry(CronEntry.this, start, MMBaseContext.getMachineName(), i.getId()), Events.STARTED));
                }
            };

        setLastRun(start);
        Interruptable thread = new Interruptable(cronJob, threads, begin, ready);
        return thread;
    }

    public boolean kick(Date currentTime) {
        switch (type) {
        case DISABLED:
            return false;
        case SHORT:
            {
                try {
                    getExecutable().run();
                } catch (Throwable t) {
                    log.error("Error during cron-job " + this +" : " + t.getClass().getName() + " " + t.getMessage() + "\n" + Logging.stackTrace(t));
                }
                return true;
            }
        case BALANCE_MUSTBEONE:
        case BALANCE: {
            EventManager.getInstance().propagateEvent(new ProposedJobs.Event(this, currentTime));
            return true;
        }
        case MUSTBEONE:
            if (isAlive()) {
                return false;
            }
            // fall through
        case CANBEMORE:
        default :
            org.mmbase.util.ThreadPools.jobsExecutor.execute(getExecutable());
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
    public Type getType() {
        return type;
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

    public long getMaxDuration() {
        return maxDuration;
    }



    boolean mustRun(Date date) {
        String machineName = MMBaseContext.getMachineName();

        if (! isActive()) {
            log.debug("This cron entry " + this + " must not run because it is not active on this machine " + machineName);
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
        int result = 0;
        result = HashCodeUtil.hashCode(result, id);
        result = HashCodeUtil.hashCode(result, name);
        result = HashCodeUtil.hashCode(result, className);
        result = HashCodeUtil.hashCode(result, cronTime);
        return result;
    }

    public boolean equals(Object o) {
        if (!(o instanceof CronEntry)) {
            return false;
        }
        CronEntry other = (CronEntry)o;
        //
        return id.equals(other.id) && name.equals(other.name) &&
            className.equals(other.className) && cronTime.equals(other.cronTime) && servers.pattern().equals(other.servers.pattern())
            && (configuration == null ? other.configuration == null : configuration.equals(other.configuration));
    }




}
