package org.mmbase.applications.crontab;

public interface JCronJob{
    public void kick(JCronEntry jCronEntry);
    public void stop(JCronEntry jCronEntry);
}
