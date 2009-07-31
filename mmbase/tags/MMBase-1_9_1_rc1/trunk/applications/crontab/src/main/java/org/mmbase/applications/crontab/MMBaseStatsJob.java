/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import org.mmbase.util.ThreadPools;
import org.mmbase.cache.Cache;
import org.mmbase.cache.CacheManager;
import org.mmbase.util.logging.*;

/**
 * An example cron-job.
 *
 * A Job to log MMBase statistics to a logger. (By means of logj4 you can configure the time stamp and logfile location).
 * The configuration string is one of the following
 <ul>
  <li>MEMORY: Logs free and total memory</li>
  <li>CACHE.&lt;cache-name&gt;: Logs hits and total request of cache with given name</li>
 </ul>
In log4j.xml you may add something like this:
<pre>
  &lt;appender name="stats" class="org.apache.log4j.FileAppender" &gt;
    &lt;param name="File" value="/tmp/mmbase.stats" /&gt;
    &lt;param name="Encoding"   value="UTF-8" /&gt;
    &lt;layout class="org.apache.log4j.PatternLayout"&gt;
      &lt;param name="ConversionPattern" value="%d{YYYY-MM-dd HH:mm:ss} %c{1} %m%n" /&gt;
    &lt;/layout&gt;
  &lt;/appender&gt;
</pre>
and:
<pre>
 &lt;logger name="org.mmbase.STATS" additivity="false"&gt;
    &lt;level class="&mmlevel;" value ="service" /&gt;
    &lt;appender-ref ref="stats" /&gt;
  &lt;/logger&gt;
</pre>
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MMBaseStatsJob extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseStatsJob.class);

    private Runnable job;

    private Logger statsLogger;

    protected void init() {
        // determin what needs to be done in run().
        String what = cronEntry.getConfiguration();
        statsLogger = Logging.getLoggerInstance("org.mmbase.STATS." + what);
        String w = what.toUpperCase();
        if (w.equals("MEMORY")) {
            job = new Runnable() {
                    public void run() {
                        Runtime runtime = Runtime.getRuntime();
                        statsLogger.service("" + runtime.freeMemory() + "\t" + runtime.totalMemory());
                    }
                };
        } else if (w.equals("QUERIES")) {
            job = new Runnable() {
                    public void run() {
                        statsLogger.service("" + org.mmbase.module.database.MultiConnectionImplementation.queries);
                    }
                };
        } else if (w.equals("JOBSPOOL")) {
            job = new Runnable() {
                    public void run() {
                        statsLogger.service("" + ThreadPools.jobsExecutor.getCompletedTaskCount() + '\t' +
                                            ThreadPools.jobsExecutor.getActiveCount() + '\t'+
                                            ThreadPools.jobsExecutor.getQueue().size() + '\t' +
                                            ThreadPools.jobsExecutor.getPoolSize() + '\t' +
                                            ThreadPools.jobsExecutor.getLargestPoolSize() + '\t' +
                                            ThreadPools.jobsExecutor.getCorePoolSize() + '\t' +
                                            ThreadPools.jobsExecutor.getMaximumPoolSize());
                    }
                };
        } else if (w.startsWith("CACHE.")) {
            job = new Runnable() {
                    private Cache cache = getCache();
                    {
                        if (cache == null) {
                            log.info("No cache with name " + cronEntry.getConfiguration().substring(6)  + " found (yet).");
                        }
                    }
                    public void run() {
                        if (cache == null) cache = getCache();
                        if (cache != null) {
                            long h = cache.getHits();
                            statsLogger.service("" +  h + "\t" + (h + cache.getMisses()));
                        }
                    }
                };
        } else {
            job = new Runnable() {
                    public void run() {
                    }
                };
        }

    }
    /**
     * Fills the 'cache' member.
     * @return Whether successful.
     */
    private Cache getCache() {
        String cacheName = cronEntry.getConfiguration().substring(6);
        return CacheManager.getCache(cacheName);
    }

    public final void run() {
        job.run();
    }
}
