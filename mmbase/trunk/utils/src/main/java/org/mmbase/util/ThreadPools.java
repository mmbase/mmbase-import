/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;
import java.lang.ref.WeakReference;
import java.util.concurrent.*;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.util.xml.Instantiator;

/**
 * Generic MMBase Thread Pools
 *
 * @since MMBase 1.8
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class ThreadPools {
    private static final Logger LOG = Logging.getLoggerInstance(ThreadPools.class);

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


    private static List<WeakReference<Thread>> nameLess = new CopyOnWriteArrayList<WeakReference<Thread>>();


    public static Thread newThread(final Runnable r, final String id) {
        String mn = getMachineName();
        LOG.service("Found mn " + mn + "(" + (mn == null) + ")");
        Thread t = new Thread(threadGroup, r,
                              (mn == null ? "" : mn) + ":" + id) {
                /**
                 * Overrides run of Thread to catch and log all exceptions. Otherwise they go through to app-server.
                 */
                @Override
                public void run() {
                    try {
                        super.run();
                        //} catch (org.mmbase.bridge.NotFoundException nf) {
                    } catch (RuntimeException nf) {
                        LOG.error("Error during job: " + r + ":" + id + " " + nf.getClass().getName() + " " + nf.getMessage(), nf);
                    } catch (Throwable e) {
                        LOG.error("Error during job: " + r + ":" + id + " " + e.getClass().getName() + " " + e.getMessage(), e);
                    }
                }
            };
        t.setDaemon(true);
        if (mn == null) {
            nameLess.add(new WeakReference<Thread>(t));
        }
        return t;
    }


    private static long jobsSeq = 0;
    /**
     * All kind of jobs that should happen in a seperate Thread can be
     * executed by this executor. E.g. sending mail could be done by a
     * job of this type.
     *
     */
    public static final ThreadPoolExecutor jobsExecutor = new ThreadPoolExecutor(2, 2000, 1 * 60 , TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "JobsThread-" + (jobsSeq++));
            }
        }) {
            @Override
            public void execute(Runnable r) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Executing " + r + " because ", new Exception());
                }
                super.execute(r);
            }
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                LOG.debug("Now executing " + r + " in thread " + t);


            }
        };


    private static String getMachineName() {
        return Logging.getMachineName();
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
        @Override
        public Thread newThread(Runnable r) {
            return ThreadPools.newThread(r, "SchedulerThread-" + (schedSeq++));
        }
    });

    private static final Map<String, ExecutorService> threadPools = new ConcurrentHashMap<String, ExecutorService>();

    static {
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);


        try {
            EventManager manager = EventManager.getInstance();

            if (manager == null) {
                LOG.fatal("No event manager!");

            } else {

                manager.addEventListener(new SystemEventListener() {
                    @Override
                    public void notify(SystemEvent systemEvent) {
                        if (systemEvent instanceof SystemEvent.MachineName) {

                            String machineName = ((SystemEvent.MachineName) systemEvent).getName();
                            for (WeakReference<Thread> tr : nameLess) {
                                Thread t = tr.get();
                                if (t != null) {
                                    String stringBefore = "" + t;
                                    t.setName(machineName + t.getName());
                                    LOG.debug("Fixed name of " + stringBefore + " -> " + t);
                                }
                            }
                            nameLess.clear();
                        } else if (systemEvent instanceof SystemEvent.Shutdown) {
                            ThreadPools.shutdown();
                        }
                    }

                    @Override
                    public int getWeight() {
                        return 0;
                    }
                });
            }
            threadPools.put("jobs", jobsExecutor);
            threadPools.put("filters", filterExecutor);
            threadPools.put("schedules", scheduler);
        } catch (Throwable t) {
            LOG.fatal(t.getMessage(), t);
        }

    }

    public static Map<String, ExecutorService> getThreadPools() {
        return threadPools;
    }


    static final UtilReader properties = new UtilReader("threadpools.xml", new Runnable() {
            @Override
            public void run() {
                configure();
            }
        });


    /**
     * @since MMBase-1.9.2
     */
    static protected void setProperty(ThreadPoolExecutor object, String key, String value) {
        if (key.equals("maxsize")) {
            int newSize = Integer.parseInt(value);
            if (object.getMaximumPoolSize() !=  newSize) {
                LOG.info("Setting max pool size from " + object.getMaximumPoolSize() + " to " + newSize);
                object.setMaximumPoolSize(newSize);
            }
        } else if (key.equals("coresize")) {
            int newSize = Integer.parseInt(value);
            if (object.getCorePoolSize() != newSize) {
                LOG.info("Setting core pool size from " + object.getCorePoolSize() + " to " + newSize);
                object.setCorePoolSize(newSize);
            }
        } else if (key.equals("keepAliveTime")) {
            int newTime = Integer.parseInt(value);
            if (object.getKeepAliveTime(TimeUnit.SECONDS) != newTime) {
                LOG.info("Setting keep alive time  from " + object.getKeepAliveTime(TimeUnit.SECONDS) + " to " + newTime);
                object.setKeepAliveTime(newTime, TimeUnit.SECONDS);
            }
        } else {
            Instantiator.setProperty(key, object.getClass(), object, value);
        }
    }


    /**
     * @since MMBase-1.9
     */
    static void configure() {

        Map<String,String> props = properties.getProperties();

        for (Map.Entry<String, String> entry : props.entrySet()) {
            if (entry.getKey().startsWith("jobs.")) {
                setProperty(jobsExecutor, entry.getKey().substring("jobs.".length()), entry.getValue());
            } else if (entry.getKey().startsWith("scheduler.")) {
                setProperty(scheduler, entry.getKey().substring("scheduler.".length()), entry.getValue());
            } else if (entry.getKey().startsWith("filters.")) {
                setProperty(scheduler, entry.getKey().substring("filters.".length()), entry.getValue());
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
                LOG.info("Interrupted " + run);
            }
        }
        {

            List<Runnable> run = filterExecutor.shutdownNow();
            if (run.size() > 0) {
                LOG.info("Interrupted " + run);
            }

        }
        {
            List<Runnable> run = jobsExecutor.shutdownNow();
            if (run.size() > 0) {
                LOG.info("Interrupted " + run);
            }
        }
    }

    private ThreadPools() {
    }

}
