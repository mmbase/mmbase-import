/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;
import java.util.concurrent.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.UtilReader;

/**
 * Generic MMBase Thread Pools
 *
 * @since MMBase 1.8
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class ThreadPools {
    private static final Logger log = Logging.getLoggerInstance(ThreadPools.class);

    public static final ThreadGroup threadGroup =  new ThreadGroup("MMBase Thread Pool");

    private static Map<Future, String> identifiers =
        Collections.synchronizedMap(new WeakHashMap<Future, String>());

    /**
     * There is no way to identify the FutureTask objects returned in
     * the getQueue methods of the executors.  This works around that.
     * Used by admin pages.
     * @since MMBase-1.9
     */
    public static String identify(Future r, String s) {
        return identifiers.put(r, s);
    }

    /**
     * Wrapper around Thread.scheduler.scheduleAtFixedRate.
     * @deprecated Used ThreadPools.scheduler#scheduleAtFixedRate  This method is only provided to
     * use this in both 1.8 (concurrecy backport) and 1.9 (java 1.5).
     */
    public static ScheduledFuture scheduleAtFixedRate(Runnable pub, int time1, int time2) {
        return scheduler.scheduleAtFixedRate(pub,
                                             time1,
                                             time2, TimeUnit.SECONDS);
    }
    /**
     * returns a identifier string for the given task.
     * @since MMBase-1.9
     */
    public static String getString(Future r) {
        String s = identifiers.get(r);
        if (s == null) return "" + r;
        return s;
    }

    /**
     * Generic Thread Pools which can be used by 'filters'. Filters
     * are short living tasks. This is mainly used by {@link
     * org.mmbase.util.transformers.ChainedCharTransformer} (and only
     * when transforming a Reader).
     *
     * Code performing a similar task could also use this thread pool.
     */
    public static final ExecutorService filterExecutor = Executors.newCachedThreadPool();

    private static List<Thread> nameLess = new CopyOnWriteArrayList<Thread>();

    public static Thread newThread(final Runnable r, final String id) {
        boolean isUp = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().isUp();
        Thread t = new Thread(threadGroup, r,
                              isUp ? getMachineName() + ":" + id : id) {
                /**
                 * Overrides run of Thread to catch and log all exceptions. Otherwise they go through to app-server.
                 */
                @Override public void run() {
                    try {
                        super.run();
                    } catch (Throwable t) {
                        log.error("Error during job: " + r + ":" + id + " " + t.getClass().getName() + " " + t.getMessage(), t);
                    }
                }
            };
        t.setDaemon(true);
        if (! isUp) nameLess.add(t);
        return t;
    }



    private static long jobsSeq = 0;
    /**
     * All kind of jobs that should happen in a seperate Thread can be
     * executed by this executor. E.g. sending mail could be done by a
     * job of this type.
     *
     */
    public static final ThreadPoolExecutor jobsExecutor = new ThreadPoolExecutor(2, 200, 5 * 60 , TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300), new ThreadFactory() {

            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "JobsThread-" + (jobsSeq++));
            }
        });

    private static String getMachineName() {
        String machineName;
        try {
            org.mmbase.bridge.ContextProvider.getDefaultCloudContext().assertUp();
            machineName = org.mmbase.module.core.MMBaseContext.getMachineName();
        } catch (NoClassDefFoundError cnfe) {
            // happens if no MMBaseContext, because this is used with the
            // rmmci-client jar.
            machineName = "localhost";
        }
        return machineName;
    }

    static {
        jobsExecutor.execute(new Runnable() {
                public void run() {
                    String machineName = getMachineName();
                    for (Thread t : nameLess) {
                        t.setName(machineName + ":" + t.getName());
                    }
                }
            });
    }


    private static long schedSeq = 0;
    /**
     * This executor is for repeating tasks. E.g. every running
     * {@link org.mmbase.module.Module}  has a  {@link
     * org.mmbase.module.Module#maintainance} which is scheduled to
     * run every hour.
     *
     * @since MMBase-1.9
     */
    public static final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "SchedulerThread-" + (schedSeq++));
            }
        });
    static {
        ((ScheduledThreadPoolExecutor) scheduler).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    private static final Map<String, ExecutorService> threadPools = new ConcurrentHashMap<String, ExecutorService>();
    static {
        threadPools.put("jobs", jobsExecutor);
        threadPools.put("filters", filterExecutor);
        threadPools.put("schedules", scheduler);
    }

    public static Map<String, ExecutorService> getThreadPools() {
        return threadPools;
    }


    static final UtilReader properties = new UtilReader("threadpools.xml", new Runnable() { public void run() { configure(); }});

    /**
     * @since MMBase-1.9
     */
    static void configure() {

        Map<String,String> props = properties.getProperties();
        String max = props.get("jobs.maxsize");
        if (max != null) {
            int newSize = Integer.parseInt(max);
            if (jobsExecutor.getMaximumPoolSize() !=  newSize) {
                log.info("Setting max pool size from " + jobsExecutor.getMaximumPoolSize() + " to " + newSize);
                jobsExecutor.setMaximumPoolSize(newSize);
            }
        }
        String core = props.get("jobs.coresize");
        if (core != null) {
            int newSize = Integer.parseInt(core);
            if (jobsExecutor.getCorePoolSize() != newSize) {
                log.info("Setting core pool size from " + jobsExecutor.getCorePoolSize() + " to " + newSize);
                jobsExecutor.setCorePoolSize(newSize);
            }
        }

        String schedSize = props.get("scheduler.coresize");
        if (schedSize != null) {
            int newSize = Integer.parseInt(schedSize);
            if (scheduler.getCorePoolSize() != newSize) {
                log.info("Setting scheduler pool size from " + scheduler.getCorePoolSize() + " to " + schedSize);
                scheduler.setCorePoolSize(newSize);
            }
        }
    }

    /**
     * @since MMBase-1.8.4
     */
    public static void shutdown() {
        {
            List<Runnable> run = scheduler.shutdownNow();
            if (run.size() > 0) {
                log.info("Interrupted " + run);
            }
        }
        {

            List<Runnable> run = filterExecutor.shutdownNow();
            if (run.size() > 0) {
                log.info("Interrupted " + run);
            }

        }
        {
            List<Runnable> run = jobsExecutor.shutdownNow();
            if (run.size() > 0) {
                log.info("Interrupted " + run);
            }
        }
    }

    private ThreadPools() {
    }

}
