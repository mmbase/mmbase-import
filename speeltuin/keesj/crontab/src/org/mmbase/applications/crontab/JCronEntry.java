package org.mmbase.applications.crontab;


import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Defines one entry for JCronDaemon.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id: JCronEntry.java,v 1.7 2004-04-05 18:29:28 michiel Exp $
 */

public class JCronEntry {
    
    private static final Logger log = Logging.getLoggerInstance(JCronEntry.class);
    private Runnable jCronJob;

    private Thread thread;
    
    private String id;
    private String name;
    private String className;
    private String cronTime;
    private String configuration = null;

    private int count = 0;

    private JCronEntryField second    ;// 0-59
    private JCronEntryField minute    ;// 0-59
    private JCronEntryField hour      ;// 0-23
    private JCronEntryField dayOfMonth;//1-31
    private JCronEntryField month     ;//1-12
    private JCronEntryField dayOfWeek ;//0-7 (0 or 7 is sunday)
   

    /**
     * @throws ClassCastException if className does not refer to a Runnable.
     */ 
    public JCronEntry(String id, String cronTime, String name, String className) throws Exception {
        this.id = id;
        this.name = name;
        this.className = className;
        this.cronTime = cronTime;
        jCronJob = (Runnable) Class.forName(className).newInstance();

        second     = new JCronEntryField();
        minute     = new JCronEntryField();
        hour       = new JCronEntryField();
        dayOfMonth = new JCronEntryField();
        month      = new JCronEntryField();
        dayOfWeek  = new JCronEntryField();
        setTimeVal(cronTime);
    }

    public void init() {
        if (jCronJob instanceof JCronJob) {
            ((JCronJob) jCronJob).init(this);
        }
    }
    public void stop() {
        if (jCronJob instanceof JCronJob) {
            ((JCronJob) jCronJob).stop();
        }
    }

    protected boolean isAlive() {
        return thread != null && thread.isAlive();
    }
    
    protected boolean kick() {
        if (isAlive()) {
            return false;
        } else {
            count++;
            thread = new Thread(jCronJob, "JCronJob " + toString());
            thread.setDaemon(true);            
            thread.start();
            return true;
        }

    }
    
    protected void setTimeVal(String cronTime){
        StringTokenizer st = new StringTokenizer(cronTime," ");
        if (st.countTokens() > 5) {
            throw new RuntimeException("Too many (" + st.countTokens() + "> 6)  tokens in " + cronTime);
        }
        /* not implemented
        if (st.countTokens() == 6) {
            second.setTimeVal(st.nextToken());
        } else {
            second.setTimeVal("0");
        }
        */
        minute.setTimeVal(st.nextToken());
        hour.setTimeVal(st.nextToken());
        dayOfMonth.setTimeVal(st.nextToken());
        month.setTimeVal(st.nextToken());
        dayOfWeek.setTimeVal(st.nextToken());
    }
    
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public void setConfiguration(String conf) {
        configuration = conf;
    }
    public String getConfiguration() {
        return configuration;
    }
    
    boolean mustRun(Date date){
        Calendar cal = Calendar.getInstance();
        if (
        minute.valid(cal.get(cal.MINUTE)) &&
        hour.valid(cal.get(cal.HOUR_OF_DAY)) &&
        dayOfMonth.valid(cal.get(cal.DAY_OF_MONTH)) &&
        month.valid(cal.get(cal.MONTH) + 1) &&
        dayOfWeek.valid(cal.get(cal.DAY_OF_WEEK) -1)){
            return true;
        }
        return false;
    }
    
    public JCronEntryField getMinuteEntry(){
        return minute;
    }
    public JCronEntryField getHourEntry(){
        return hour;
    }
    
    public JCronEntryField getDayOfMonthEntry(){
        return dayOfMonth;
    }
    
    public JCronEntryField getMonthEntry(){
        return month;
    }
    
    public JCronEntryField getDayOfWeekEntry(){
        return dayOfWeek;
    }

    public String toString() {
        return id + ":" + cronTime + ":" + name + ": " + className + ":" + configuration + ": " +  count;
    }


    public int hashCode() {
        return id.hashCode() + name.hashCode() + className.hashCode() + cronTime.hashCode();
    }
    

    public boolean equals(Object o) {
        if (! (o instanceof JCronEntry)) {
            return false;
        }
        JCronEntry other = (JCronEntry) o;
        return id.equals(other.id) && name.equals(other.name) && className.equals(other.className) && cronTime.equals(other.cronTime);
        
    }

}
