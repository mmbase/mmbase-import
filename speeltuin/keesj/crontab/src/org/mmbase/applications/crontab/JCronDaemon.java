package org.mmbase.applications.crontab;

import java.util.*;
/**
 * JCronDaemonn is a "crontab" clone written in java.
 * The daemon starts a thread that wakes up every minute
 *(it keeps sync by calculating the time to sleep)
 **/
public class JCronDaemon implements Runnable{
    
    private static JCronDaemon jCronDaemon;
    private Thread cronThread;
    private JCronEntries jCronEntries;
    
    private JCronDaemon() {
        jCronEntries = new JCronEntries();
        start();
    }
    
    public void addJCronEntry(JCronEntry entry){
        getJCronEntries().add(entry);
    }
    private JCronEntries getJCronEntries(){
        return jCronEntries;
    }
    
    public void start(){
        cronThread = new Thread(this,"JCronDaemon");
        //cronThread.setDaemon(true);
        cronThread.start();
    }
    
    public void stop(){
        System.err.println("stop");
        cronThread.interrupt();
        cronThread= null;
    }
    
    public static synchronized JCronDaemon getInstance(){
        if (jCronDaemon == null){
            jCronDaemon = new JCronDaemon();
        }
        return jCronDaemon;
    }
    
    public long getTime(){
        return System.currentTimeMillis();
    }
    
    public void run() {
        Thread thisThread = Thread.currentThread();
        while(thisThread == cronThread){
            long now = getTime();
            long next = (now  + 60 * 1000 )/60000 * 60000;
            
            try {
                Thread.sleep(next - now);
            } catch (InterruptedException ie){
                return;
            }
            
            System.err.println("--------------");
            Date date = new Date();
            date.setTime(next); //nice "rounded" time
            
            for (int z = 0 ; z < jCronEntries.size(); z ++){
                JCronEntry entry = jCronEntries.getJCronEntry(z);
                if (entry.mustRun(date)){
                    entry.kick();
                    System.err.println(date + " run\t: " + entry.getName());
                } else {
                    System.err.println(date + " skip\t: " + entry.getName());
                }
            }
        }
    }
    
    public static void main(String[] argv) throws Exception{
        JCronDaemon d = JCronDaemon.getInstance();
        
        //entries.add(new JCronEntry("20 10 31 8 *","happy birthday"));
        //entries.add(new JCronEntry("* * * * 1","monday"));
        //entries.add(new JCronEntry("* * * * 2","tuesday"));
        
        //entries.add(new JCronEntry("* * 19 * *","the 19'st  day of the month"));
        
        //entries.add(new JCronEntry("* * * 1 *","the first month of the year"));
        //entries.add(new JCronEntry("*/2 * * * *","every 2 minutes stating from 0"));
        //entries.add(new JCronEntry("1-59/2 * * * *","every 2 minutes stating from 1"));
        d.addJCronEntry(new JCronEntry("1","*/2 5-23 * * *","every 2 minute from 5 till 11 pm","org.mmbase.applications.crontab.TestCronJob"));
        //entries.add(new JCronEntry("40-45,50-59 * * * *","test 40-45,50-60","Dummy"));
        
        try {Thread.currentThread().sleep(240 * 1000 * 60); } catch (Exception e){};
        d.stop();
    }
}
