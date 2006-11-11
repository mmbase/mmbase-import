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
 * CronDaemon is a "crontab" clone written in java.
 * The daemon starts a thread that wakes up every minute
 *(it keeps sync by calculating the time to sleep)
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: CronDaemon.java,v 1.13 2006-11-11 16:03:21 michiel Exp $
 */
public class CronDaemon  {

    private static final Logger log = Logging.getLoggerInstance(CronDaemon.class);

    private static CronDaemon cronDaemon;
    private Timer cronTimer;
    private Set<CronEntry> cronEntries;
    private Set<CronEntry> removedCronEntries;
    private Set<CronEntry> addedCronEntries;

    /**
     * CronDaemon is a Singleton. This makes the one instance and starts the Thread.
     */
    private CronDaemon() {
        cronEntries = Collections.synchronizedSet(new LinkedHashSet()); // predictable order
        removedCronEntries = Collections.synchronizedSet(new HashSet());
        addedCronEntries = Collections.synchronizedSet(new LinkedHashSet()); // predictable order
        start();
    }

    /**
     * Finds in given set the CronEntry with the given id.
     * @return a CronEntry if found, <code>null</code> otherwise.
     */
    protected static CronEntry getById(Set<CronEntry> set, String id) {
        for (CronEntry entry : set) {
            if (entry.getId().equals(id))
                return entry;
        }
        return null;
    }

    /**
     * Adds the given CronEntry to this daemon. 
     * @throws RuntimeException If an entry with the same id is present already (unless it is running and scheduled for removal already)
     */

    public void add(CronEntry entry) {
        CronEntry containing = getById(cronEntries, entry.getId());
        if (containing != null) {
            if (removedCronEntries.contains(containing)) {
                addedCronEntries.add(entry);
                // do copy the 'crontime' allready.
                containing.setCronTime(entry.getCronTime());
                return;
            } else {
                throw new RuntimeException("There is an entry  " + entry + " already");
            }
        } else {
            addEntry(entry);
        }

    }

    /**
     * Actually adds, no checks for 'removedEntries' and so on.
     */
    protected void addEntry(CronEntry entry) {
        entry.init();
        cronEntries.add(entry);
        log.service("Added entry " + entry);
    }

    public CronEntry getCronEntry(String id) {
        return getById(cronEntries, id);
    }
    /**
     * Remove the given CronEntry from this daemon. If the entry is currently running, it will be
     * postponed until this job is ready.
     */
    public void remove(CronEntry entry) {
        if (!entry.isAlive()) {
            removeEntry(entry);
        } else {
            // it is alive, only schedule for removal.
            removedCronEntries.add(entry);
        }
    }

    /**
     * Actually removes, nor checks for removedEntries' and so on.
     */
    protected void removeEntry(CronEntry entry) {
        cronEntries.remove(entry);
        entry.stop();
        log.service("Removed entry " + entry);
    }

    /** 
     * Starts the daemon, which you might want to do if you have stopped if for some reason. The
     * daemon is already started on default.
     */
    public void start() {
        log.info("Starting CronDaemon");
        cronTimer = new Timer(true);
        cronTimer.scheduleAtFixedRate(new TimerTask() { public void run() {CronDaemon.this.run();} }, 0, 60 * 1000);
    }

    /**
     * If you like to temporary stop the daemon, call this.
     */
    public void stop() {
        log.info("Stopping CronDaemon");
        cronTimer.cancel();
        cronTimer = null;
        for (CronEntry entry : cronEntries) {
            entry.stop();
        }
    }

    public boolean isAlive() {
        return cronTimer != null;
    }

    /**
     * Singleton, Gets (and instantiates, and starts) the one CronDaemon instance.
     */

    public static synchronized CronDaemon getInstance() {
        if (cronDaemon == null) {
            cronDaemon = new CronDaemon();
        }
        return cronDaemon;
    }

    /**
     * The main loop of the daemon.
     */
    protected void run() {
        long now = System.currentTimeMillis();
        try {
            Date currentMinute = new Date(now / 60000 * 60000);

            if (log.isDebugEnabled()) {
                log.debug("Checking for " + currentMinute);
            }

            // remove jobs which were scheduled for removal
            Iterator<CronEntry> z = removedCronEntries.iterator();
            while (z.hasNext()) {
                CronEntry entry = z.next();
                if (entry.isAlive()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Job " + entry + " still running, so could not yet be removed");
                    }
                } else {
                    removeEntry(entry);
                    z.remove();
                    CronEntry added = getById(addedCronEntries, entry.getId());
                    if (added != null) {
                        addEntry(added);
                        addedCronEntries.remove(added);
                    }
                }
            }
            // start jobs which need starting on this minute
            for (CronEntry entry : cronEntries) {
                if (Thread.currentThread().isInterrupted()) return;
                if (entry.mustRun(currentMinute)) {
                    if (entry.kick()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Started " + entry);
                        }
                    } else {
                        log.warn("Job " + entry + " still running, so not restarting it again.");
                    }
                }
            }
        } catch (Throwable t) {
            log.error(t.getClass().getName() + " " + t.getMessage(), t);
        }
    }

    /**
     * @since MMBase-1.8
     */
    public Set<CronEntry> getEntries() {
        return Collections.unmodifiableSet(cronEntries);
    }

    /**
     * main only for testing purposes
     */

    public static void main(String[] argv) throws Exception {
        CronDaemon d = CronDaemon.getInstance();

        //d.add(new CronEntry("20 10 31 8 *","happy birthday",null));
        //d.add(new CronEntry("* * * * 1","monday",null));
        //d.add(new CronEntry("* * * * 2","tuesday",null));

        //d.add(new CronEntry("* * 19 * *","the 19'st  day of the month",null));

        //d.add(new CronEntry("* * * 1 *","the first month of the year",null));
        //d.add(new CronEntry("*/2 * * * *","every 2 minutes stating from 0",null));
        //d.add(new CronEntry("1-59/2 * * * *","every 2 minutes stating from 1",null));
        d.add(new CronEntry("1", "*/2 5-23 * * *", "every 2 minute from 5 till 11 pm", "org.mmbase.applications.crontab.TestCronJob", null));
        //d.add(new CronEntry("40-45,50-59 * * * *","test 40-45,50-60","Dummy",null));

        try {
            Thread.sleep(240 * 1000 * 60);
        } catch (Exception e) {};
        d.stop();
    }
}
