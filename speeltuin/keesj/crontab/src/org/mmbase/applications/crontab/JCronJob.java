package org.mmbase.applications.crontab;

/**
 * JCronJobs are simply 'Runnable' but also have a init-method, which is called by JCronDaemon.
 */


public interface JCronJob extends Runnable {

    public void init(JCronEntry jCronEntry);
    public void stop();
}
