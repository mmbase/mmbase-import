package org.mmbase.applications.crontab;



public interface JCronJob extends Runnable {

    public void init(JCronEntry jCronEntry);
    public void stop();
}
