package org.mmbase.applications.crontab;



public abstract class BasicCronJob implements JCronJob {

    protected JCronEntry jCronEntry;
    public void init(JCronEntry j) {
        jCronEntry = j;
        init();
    }

    protected void init() {
    }

    public void stop() {
    }
    public abstract void run();
}
