package org.mmbase.applications.crontab;



public abstract class BasicCronJob implements JCronJob {

    protected JCronEntry jCronEntry;
    public void init(JCronEntry j) {
        jCronEntry = j;
    }
    public void stop() {
    }
    public abstract void run();
}
