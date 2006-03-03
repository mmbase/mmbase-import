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
 * @todo   Should we use java.util.Timer?
 */
public class CronDaemon implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(CronDaemon.class);

    private static CronDaemon cronDaemon;
    private Thread cronThread;
    private Set cronEntries;
    private Set removedCronEntries;
    private Set addedCronEntries;

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
    protected static CronEntry getById(Set set, String id) {
        Iterator i = set.iterator();
        while (i.hasNext()) {
            CronEntry entry = (CronEntry)i.next();
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
        log.info("Removed entry " + entry);
    }

    /** 
     * Starts the daemon, which you might want to do if you have stopped if for some reason. The
     * daemon is already started on default.
     */
    public void start() {
        log.info("Starting CronDaemon");
        cronThread = new Thread(this, "CronDaemon");
        // some tasks need a decent shutdown (database administration), so depend op 'stop'.
        cronThread.setDaemon(false);
        cronThread.start();
    }

    /**
     * If you like to temporary stop the daemon, call this.
     */
    public void stop() {
        log.info("Stopping CronDaemon");
        cronThread.interrupt();
        cronThread = null;
        Iterator i = cronEntries.iterator();
        while (i.hasNext()) {
            CronEntry entry = (CronEntry)i.next();
            entry.stop();
        }
    }

    public boolean isAlive() {
        return cronThread != null && cronThread.isAlive();
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
     * The main loop of the daemon, which of course is a Thread, implemented in run() to satisfy the
     * 'Runnable' interface.
     */
    public void run() {
        Thread thisThread = Thread.currentThread();

        while (thisThread == cronThread) { // run is stopped, by setting cronThread to null.

            long now = System.currentTimeMillis();
            long next = (now + 60 * 1000) / 60000 * 60000; // next minute, rounded to minute

            try {
                Thread.sleep(next - now); // sleep until  next minute

                Date currentMinute = new Date(next);

                if (log.isDebugEnabled()) {
                    log.debug("Checking for " + currentMinute);
                }

                // remove jobs which were scheduled for removal
                Iterator z = removedCronEntries.iterator();
                while (z.hasNext()) {
                    CronEntry entry = (CronEntry)z.next();
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
                z = cronEntries.iterator();
                while (z.hasNext()) {
                    if (Thread.currentThread().isInterrupted()) return;
                    CronEntry entry = (CronEntry)z.next();
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
            } catch (InterruptedException ie) {
                log.info("Interrupted: " + ie.getMessage());
                return;
            } catch (Throwable t) {
                log.error(t.getClass().getName() + " " + t.getMessage(), t);
            }
        }
    }
    /**
     * @since MMBase-1.8
     */
    public Set getEntries() {
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
