package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * A test cron-job. Aquires a Cloud object, and sleeps vor 130 seconds (if you e.g. schedule it
 * every minute, you test overlapping jobs: the job should not be restarted if still running)
 */

public class TestCronJob implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(TestCronJob.class);

    public void run() {
        //Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); // testing Class Security
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "anonymous", null);
        log.info("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank());        
        try {
            log.info("sleeping");
            Thread.sleep(130 * 1000);
            log.info("sleeped");
        } catch (Exception e) {
        }

    }

}
