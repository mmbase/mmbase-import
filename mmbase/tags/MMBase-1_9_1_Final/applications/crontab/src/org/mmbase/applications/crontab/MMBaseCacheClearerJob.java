/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import org.mmbase.cache.*;
import org.mmbase.util.logging.*;

/**
 * Periodically clears MMBase caches. The configuration string may be a comma-separated list of the
 * names of the caches to be cleared. Otherwise all caches.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id$
 */

public class MMBaseCacheClearerJob extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCacheClearerJob.class);

    protected String[] caches = null;
    protected void init() {
        caches = cronEntry.getConfiguration() != null ? caches = cronEntry.getConfiguration().split(",") : null;
    }

    public final void run() {
        if (caches != null && caches.length > 0) {
            log.service("Clearing " + java.util.Arrays.asList(caches));
            for (Object cache : caches) {
                CacheManager.getCache((String) cache).clear();
            }
        } else {
            log.service("Clearing " + CacheManager.getCaches());
            for (Object cache :  CacheManager.getCaches()) {
                CacheManager.getCache((String) cache).clear();
            }
        }
    }
}
