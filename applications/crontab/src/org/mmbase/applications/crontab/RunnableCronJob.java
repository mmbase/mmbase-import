/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import java.util.*;
import org.mmbase.util.logging.*;


/**
 * Just wraps a Runnable into a CronJob.
 *
 * Runnable can optionally be configured using 'bean' properties, where the key/values are read
 * from the configuration string using {@link org.mmbase.util.StringSplitter#map}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: RunnableCronJob.java,v 1.5 2009-02-02 13:27:46 michiel Exp $
 */

public class RunnableCronJob extends AbstractCronJob {

    private static final Logger log = Logging.getLoggerInstance(RunnableCronJob.class);

    protected final Runnable runnable;

    public RunnableCronJob(Runnable run) {
        runnable = run;
    }
    public void run() {
        runnable.run();
    }

    @Override protected void init() {
        String delimiter =
            cronEntry instanceof org.mmbase.applications.crontab.builders.NodeCronEntry
            ? "\n" : ",";
        Map<String, String> config = org.mmbase.util.StringSplitter.map(cronEntry.getConfiguration(), delimiter);
        for (Map.Entry<String, String> entry : config.entrySet()) {
            try {
                org.mmbase.util.xml.Instantiator.setProperty(entry.getKey(), runnable.getClass(), runnable, entry.getValue());
                log.service("Set property " + entry + " on " + runnable);
            } catch (Throwable t) {
                log.error("For property " + entry + ": " + t.getMessage());
            }
        }

    }
}
