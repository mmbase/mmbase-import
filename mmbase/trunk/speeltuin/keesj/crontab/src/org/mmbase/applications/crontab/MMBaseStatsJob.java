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
 * A Job to log MMBase statistics to a logger. (By means of logj4j you can configure the time stamp and logfile location).
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
 * @version $Id: MMBaseStatsJob.java,v 1.1 2004-09-23 13:44:22 michiel Exp $
 */

public class MMBaseStatsJob extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseStatsJob.class);

    public void run() {
        String what = cronEntry.getConfiguration();
        Logger statsLogger = Logging.getLoggerInstance("org.mmbase.STATS." + what);
        String w = what.toUpperCase();
        if (w.equals("MEMORY")) {
            Runtime runtime = Runtime.getRuntime();
            statsLogger.service("" + runtime.freeMemory() + "\t" + runtime.totalMemory());
        } else if (w.startsWith("CACHE.")) {
            String cacheName = what.substring(6);
            Cache  cache     = Cache.getCache(cacheName);
            if (cache != null) {
                statsLogger.service("" +  cache.getHits() + "\t" + cache.getHits() + "\t" + cache.getMisses());
            } else {
                log.error("No cache with name " + cacheName  + " found");
            }
        }

    }
}
