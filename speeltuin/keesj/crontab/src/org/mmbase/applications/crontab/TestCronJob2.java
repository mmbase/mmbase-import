package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

public class TestCronJob2 extends BasicCronJob {

    private static final Logger log = Logging.getLoggerInstance(TestCronJob2.class);

    public void run() {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        log.info("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank());        

    }

}
