/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * A test cron-job. Aquires a Cloud object, and sleeps vor 130 seconds (if you e.g. schedule it
 * every minute, you test overlapping jobs: the job should not be restarted if still running)
 */

public class TestCronJob extends AbstractCronJob implements CronJob {

    private static final Logger log = Logging.getLoggerInstance(TestCronJob.class);


    Map<String, String> properties;

    @Override
    protected void init() {
        properties = org.mmbase.util.StringSplitter.map(cronEntry.getConfiguration());
    }


    @Override
    public void run() {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); // testing Class Security
        //Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "anonymous", null);
        log.info("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank());
        long duration = properties.containsKey("duration") ? Long.valueOf(properties.get("duration")) : 128 * 1000;
        try {
            log.info("sleeping");
            if ("true".equals(properties.get("exceptions")) && cronEntry.getCount() % 2 == 0) {
                Thread.sleep(duration / 8);
                log.info("Will throw an exception now");
                throw new RuntimeException("This job throws sometimes an exception");
                //Thread.sleep(7 * duration / 8);
            } else {
                Thread.sleep(duration);
            }
            log.info("sleeped");
        } catch (InterruptedException e) {
            log.info("Interrupted");
        }
    }
}
