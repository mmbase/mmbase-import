/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import org.mmbase.cache.Cache;
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
 * @version $Id: MMBaseStatsJob.java,v 1.2 2006-01-02 22:00:18 michiel Exp $
 */

public class MMBaseStatsJob extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseStatsJob.class);
    private static final int MEMORY = 1;
    private static final int CACHE  = 2;

    private int type;
    private Cache cache = null; // used if type == CACHE

    private Logger statsLogger;

    protected void init() {
        // determin what needs to be done in run().
        String what = cronEntry.getConfiguration();
        statsLogger = Logging.getLoggerInstance("org.mmbase.STATS." + what);
        String w = what.toUpperCase();
        if (w.equals("MEMORY")) {
            type = MEMORY;
        } else if (w.startsWith("CACHE.")) {
            type = CACHE;
            if (! getCache()) {
                log.info("No cache with name " + cronEntry.getConfiguration().substring(6)  + " found (yet).");
            }

        }

    }
    /**
     * Fills the 'cache' member.
     * @return Whether successful.
     */
    private boolean getCache() {
        String cacheName = cronEntry.getConfiguration().substring(6);
        cache     = Cache.getCache(cacheName);
        return cache != null;

    }

    public final void run() {
        switch(type) {
        case CACHE: {
            if (cache == null) getCache();
            if (cache != null) {
                int h = cache.getHits();
                statsLogger.service("" +  h + "\t" + (h + cache.getMisses()));
            }
            break;
        }
        case MEMORY: {
            Runtime runtime = Runtime.getRuntime();
            statsLogger.service("" + runtime.freeMemory() + "\t" + runtime.totalMemory());
            break;
        }
        }
    }
}
