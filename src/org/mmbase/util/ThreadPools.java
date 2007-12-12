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
 * @version $Id: ThreadPools.java,v 1.5.2.1 2007-03-28 12:20:43 michiel Exp $
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
    public static final Executor jobsExecutor = new ThreadPoolExecutor(2, 10, 5, TimeUnit.MINUTES, new  LinkedBlockingQueue(), new ThreadFactory() {

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


    /**
     * @since MMBase-1.8.4
     */
    public static final void shutdown() {
        ((ExecutorService) filterExecutor).shutdown();
        ((ExecutorService) jobsExecutor).shutdown();
    }

}
