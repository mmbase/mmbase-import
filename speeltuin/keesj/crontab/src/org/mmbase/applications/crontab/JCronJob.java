package org.mmbase.applications.crontab;

/**
 * JCronJobs are simply 'Runnable' but also have a init-method, which is called by JCronDaemon. It
 * is wrapped in JCronEntries first.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: JCronJob.java,v 1.4 2004-04-01 22:16:47 michiel Exp $
 */


public interface JCronJob extends Runnable {

    /**
     * If the CronJobs needs some initializing before the first run, then that can be put in this.
     */
    void init(JCronEntry jCronEntry);

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
