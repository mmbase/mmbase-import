/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import edu.emory.mathcs.backport.java.util.concurrent.*;
import org.mmbase.util.logging.*;
/**
 * Generic MMBase Thread Pools
 *
 * @since MMBase 1.8
 * @author Michiel Meewissen
 * @version $Id$
 */
public abstract class ThreadPools {
    private static final Logger log = Logging.getLoggerInstance(ThreadPools.class);


    /**
     * Wrapper around Thread.scheduler.scheduleAtFixedRate. Provided for forward compatibility.
     * @deprecated
     */

    public static ScheduledFuture scheduleAtFixedRate(Runnable pub, int time1, int time2) {
        return scheduler.scheduleAtFixedRate(pub,
                                             time1,
                                             time2, TimeUnit.SECONDS);
    }


    private static Thread newThread(Runnable r, String id) {
        Thread t = new Thread(org.mmbase.module.core.MMBaseContext.getThreadGroup(), r, id) {
                /**
                 * Overrides run of Thread to catch and log all exceptions. Otherwise they go through to app-server.
                 */
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable t) {
                        log.error("Error during job: " + t.getClass().getName() + " " + t.getMessage(), t);
                    }
                }
            };
        t.setDaemon(true);
        return t;
    }

    /**
     * Generic Thread Pools which can be used by 'filters'.
     */
    public static final Executor filterExecutor = Executors.newCachedThreadPool();


    /**
     * For jobs there are 'scheduled', and typically happen on larger time-scales.
     */
    public static final ThreadPoolExecutor jobsExecutor = new ThreadPoolExecutor(2, 10, 5, TimeUnit.MINUTES, new  LinkedBlockingQueue(), new ThreadFactory() {

            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "JOBTHREAD");
            }
        });

    public static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "SCHEDULERTHREAD");
            }
        });


    /**
     * @since MMBase-1.8.4
     */
    public static final void shutdown() {
        ((ExecutorService) filterExecutor).shutdown();
        ((ExecutorService) jobsExecutor).shutdown();
        ((ExecutorService) scheduler).shutdown();
    }

}
