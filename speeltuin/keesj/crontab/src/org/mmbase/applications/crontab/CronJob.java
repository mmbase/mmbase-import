/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

/**
 * JCronJobs are simply 'Runnable' but also have a init-method, which is called by JCronDaemon. It
 * is wrapped in JCronEntries first.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: CronJob.java,v 1.1 2004-05-04 09:00:05 keesj Exp $
 */


public interface CronJob extends Runnable {

    /**
     * If the CronJobs needs some initializing before the first run, then that can be put in this.
     */
    void init(CronEntry jCronEntry);

    /**
     * If after the last run of the job, there need to be shutdown things, then that can be done here.
     */
    void stop();

    /**
     * {@inheritDoc}
     * This is the method which is repeatedly called.
     */
    void run();


    
}
