/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.w3c.dom.*;

/**
 * Basic application task, needs to be extended for real tasks.
 * this providesthe basic tools and interface for the Manager
 */
public class ApplicationTask implements Runnable {
    
    private static Logger log = Logging.getLoggerInstance(ApplicationTask.class.getName());
    
    Thread kicker = null;
    public MMBase mmb;
    private ApplicationTaskManager atm;
    private int taskid;
    private String state;
    private Vector logbuffer=new Vector();
    private Hashtable values=new Hashtable();
    private Hashtable lists=new Hashtable();
    
    
    public ApplicationTask(int taskid,ApplicationTaskManager atm,MMBase mmb) {
        this.taskid=taskid;
        this.atm=atm;
        this.mmb=mmb;
        state="init";
    }
    
    public void init() {
        this.start();
    }
    
    public void start() {
                /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"applicationtask");
            kicker.start();
        }
    }
    
    
    
    public void stop() {
                /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }
    
    /**
     * Main loop, exception protected
     */
    public void run() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);
        try {
            doWork();
        } catch(Exception e) {
            log.error("Exception in mmservers thread!" + Logging.stackTrace(e));
        }
        //atm.removeTask(taskid);
    }
    
    /**
     * Main work loop
     */
    public void doWork() {
        setState("ready","dummy task, should be overriden will fake a action");
        String ns=waitForNewState();
        setState("running","dummy task, doing nothing for 60 seconds");
        try {Thread.sleep(60*1000);} catch (InterruptedException e){}
        setState("finished","dummy task done");
    }
    
    public synchronized String waitForNewState() {
        try {
            wait();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(state);
    }
    
    public synchronized boolean setField(String name,String value) {
        values.put(name,value);
        log.info("setfield on "+taskid+" name="+name+" value="+value);
        return(true);
    }
    
    
    public synchronized boolean addListValue(String listname,String value) {
        // get the list from memory
        Vector list=(Vector)lists.get(listname);
        if (list==null) {
            list=new Vector();
            lists.put(listname,list);
        }
        list.addElement(value);
        log.info("addListValue on "+taskid+" listname="+listname+" value="+value);
        return(true);
    }
    
    
    public synchronized void setState(String state, String logline) {
        this.state=state;
        taskLog(logline,state);
        notify();
    }
    
    
    public String getState() {
        return(state);
    }
    
    public void taskLog(String logline) {
        taskLog(logline,state);
    }
    
    public void taskLog(String logline,String state) {
        Date d=new Date();
        String line="<log state=\""+state+"\" date=\""+d.toGMTString()+"\" log=\""+logline+"\" />";
        logbuffer.addElement(line);
        log.info(line);
    }
    
    public String getField(String name) {
        Object result=values.get(name);
        if (result!=null) return((String)result);
        return(null);
    }
    
    public Vector getListValues(String listname) {
        Object result=lists.get(listname);
        if (result!=null) return((Vector)result);
        return(null);
    }
    
}
