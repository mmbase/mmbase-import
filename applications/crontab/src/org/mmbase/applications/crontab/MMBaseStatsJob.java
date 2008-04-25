/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import org.mmbase.util.ThreadPools;
import org.mmbase.cache.Cache;
import org.mmbase.util.logging.*;

/**
 * An example cron-job.
 *
 * A Job to log MMBase statistics to a logger. (By means of logj4 you can configure the time stamp and logfile location).
 * The configuration string is one of the following
 <ul>
  <li>MEMORY: Logs free and total memory</li>
  <li>CACHE.&lt;cache-name&gt;: Logs hits,  total number of requests and size  of cache with given name</li>
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
 * @version $Id: MMBaseStatsJob.java,v 1.4.2.1 2008-04-10 09:18:43 michiel Exp $
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
                        statsLogger.service("" + org.mmbase.module.database.MultiConnection.queries);
                    }
                };
        } else if (w.equals("JOBSPOOL")) {
            job = new Runnable() {
                    public void run() {
                        edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor j =
                            (edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor) ThreadPools.jobsExecutor;
                        statsLogger.service("" + j.getCompletedTaskCount() + '\t' + j.getActiveCount() + '\t'+ j.getQueue().size() + '\t' +
                                            j.getPoolSize() + '\t' + j.getLargestPoolSize() + '\t' + j.getCorePoolSize() + '\t' + j.getMaximumPoolSize());
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
                            int h = cache.getHits();
                            statsLogger.service("" +  h + "\t" + (h + cache.getMisses()) + "\t" + cache.size());
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
        return Cache.getCache(cacheName);
    }

    public final void run() {
        job.run();
    }
}
