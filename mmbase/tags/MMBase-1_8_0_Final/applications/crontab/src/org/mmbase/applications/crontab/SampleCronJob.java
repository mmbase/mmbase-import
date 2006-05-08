
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
/**
 * Sample cron jobs shows ho to create a cronjob.
 * @author Kees Jongenburger
 */
//The cronjobs is the code that will get run when a cronentry gets "kicked"
//you are allowed to create a cronjob by only extending from thread 
//MyJob implements Runnable{ public void run(){}}
//but sometimes it beter to implement the Cronjob interface in order to get additional information
//the AbstractCronJob helps to implement the Cronjob interface
public class SampleCronJob extends AbstractCronJob implements CronJob {
	
    private static Logger log = Logging.getLoggerInstance(SampleCronJob.class);

    public SampleCronJob() {
        log.info("The constructor of a cronjob is only called once");
    }

    public void run() {
        log.info("The job has been started by the cronEntry" + cronEntry);
        log.info("the entry has this configuration " + cronEntry.getConfiguration());
        log.info("the entry has this id " + cronEntry.getId());
        
        //when running jobs you often need a cloud object.
        //the best way to get it is using class security
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); // testing Class Security
        //Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "anonymous", null);
        log.info("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank());
        log.info("The job has stopped");
        //cronEntry;
    }
}
