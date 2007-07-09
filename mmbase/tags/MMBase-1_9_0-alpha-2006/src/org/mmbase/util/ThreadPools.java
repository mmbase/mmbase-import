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
 * @author Michiel Meewissen
 * @version $Id: ThreadPools.java,v 1.6 2006-08-30 21:01:23 michiel Exp $
 */
public abstract class ThreadPools {
    private static final Logger log = Logging.getLoggerInstance(ThreadPools.class);

    /**
     * Generic Thread Pools which can be used by 'filters'.
     */
    public static final Executor filterExecutor = Executors.newCachedThreadPool();


    /**
     * For jobs there are 'scheduled', and typically happen on larger time-scales.
     */
    public static final Executor jobsExecutor = new ThreadPoolExecutor(2, 10, 5 * 60 , TimeUnit.SECONDS, new ArrayBlockingQueue(200), new ThreadFactory() {

            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "JOBTHREAD") {
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
        });



    private static final UtilReader properties = new UtilReader("threadpools.xml", new Runnable() { public void run() { configure(); }});

    /**
     * @since MMBase-1.9
     */
    public static void configure() {

        Map props = properties.getProperties();
        String max = (String) props.get("jobs.maxsize");
        if (max != null) {
            log.info("Setting max pool size from " + ((ThreadPoolExecutor) jobsExecutor).getMaximumPoolSize() + " to " + max);
            ((ThreadPoolExecutor) jobsExecutor).setMaximumPoolSize(Integer.parseInt(max));
        }
        String core = (String) props.get("jobs.coresize");
        if (core != null) {
            log.info("Setting core pool size from " + ((ThreadPoolExecutor) jobsExecutor).getCorePoolSize() + " to " + core);
            ((ThreadPoolExecutor) jobsExecutor).setCorePoolSize(Integer.parseInt(core));
        }
    }

}
