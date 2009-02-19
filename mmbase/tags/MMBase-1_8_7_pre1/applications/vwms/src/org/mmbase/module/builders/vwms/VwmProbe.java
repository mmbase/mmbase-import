/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.builders.vwms;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @application VWMs
 * @author Daniel Ockeloen
 * @version $Id: VwmProbe.java,v 1.11 2004-10-08 10:57:57 pierre Exp $ current version $Id: VwmProbe.java,v 1.11 2004-10-08 10:57:57 pierre Exp $
 */
public class VwmProbe implements Runnable {

    // logging variable
    private static Logger log = Logging.getLoggerInstance(VwmProbe.class.getName());

    Thread kicker = null;
    VwmProbeInterface parent=null;
    SortedVector tasks= new SortedVector(new MMObjectCompare("wantedtime"));
    // Active Node
    MMObjectNode anode=null;
    PerformProbe pp;
    private final static int TASK_PICKUP_TIMEDIFFERENCE = 3;

    public VwmProbe(VwmProbeInterface parent) {
        this.parent=parent;
        init();
    }

    public void init() {
        this.start();
    }


    /**
     * Starts the admin Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"Vwmprobe");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.interrupt();
        kicker = null;
    }

    /**
     * blocked on the first task in the queue
     */
    public synchronized void run() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);
        log.info("Probe thread started, checking for tasks.");
        while (kicker!=null) {
            log.service("Tasks vector:"+tasks+" size:"+tasks.size());
            if (tasks.size()>0) {
                anode=(MMObjectNode)tasks.elementAt(0);
                log.service("Getting task at pos 0 in vector, number:"
                +anode.getIntValue("number")+" task:"+anode.getStringValue("task"));
            } else {
                anode=null;
            }
            try {
                if (anode==null) {
                    // so no task in the future wait a long time then
                    log.info("No tasks, anode=null, waiting 1 hour.");
                    wait(3600*1000);
                } else {
                    int curTime=(int)((System.currentTimeMillis()/1000));
                    int timeDifference = anode.getIntValue("wantedtime") - curTime;
                    if (timeDifference<TASK_PICKUP_TIMEDIFFERENCE) {
                        log.service("Difference between curtime and task starttime"
                        +" (curtime - starttime)"
                        +" is smaller than "+TASK_PICKUP_TIMEDIFFERENCE
                        +" seconds, it is "+timeDifference+" handle Task NOW");
                        try {
                            //							parent.performTask(anode);
                            pp=new PerformProbe(parent,anode);
                        } catch (RuntimeException e) {
                            log.error("performTask failed "+anode+" : "+e);
                            log.error(e.getMessage());
                            log.error(Logging.stackTrace(e));
                        }
                        log.service("Removing task number:"+anode.getIntValue("number")
                        +" task:"+anode.getStringValue("task"));
                        tasks.removeElement(anode);
                    } else {
                        log.service("Task starttime is still further than "
                        +TASK_PICKUP_TIMEDIFFERENCE+" seconds away, "
                        +"waiting "+timeDifference+" seconds, before getting task again");
                        wait(timeDifference*1000);
                    }
                }
            } catch (InterruptedException e){}
        }
    }

    /**
     * Puts a task node to the vector (sorted on task start time) of new tasks.
     * If the tasks vector already contains the node, it will be replaced.
     * @param node task node
     * @return true always.
     */
    public synchronized boolean putTask(MMObjectNode node) {
        boolean res;
        if (!containsTask(node)) {
            tasks.addSorted(node);
            res=true;
        } else {
            res=replaceTask(node);
        }

        if (tasks.size()==0) {
            // notiy when tasks size is 0 ?
            log.service("Tasks vector size is 0, calling notify()");
            notify();
        } else if (node==tasks.elementAt(0)) {
            // node is first in tasks vector.
            log.service("Node "+node.getIntValue("number")
            +" task "+node.getStringValue("task")+" calling notify()");
            notify();
        }
        // huh ?!#
        return true;
    }

    /**
     * Checks if a task node already exists in the task nodes vector.
     * @param node task node
     * @return true if the nodes objectnr matches a node objectnr
     * in the vector, false if tasks vector is empty or doesn't contain the node.
     */
    public boolean containsTask(MMObjectNode node) {
        int number = node.getIntValue("number");
        Enumeration e = tasks.elements();
        if (!e.hasMoreElements()) {
            log.info("Task nodes vector is empty.");
            return false;
        }
        for (MMObjectNode nodeFromTasksVec=null; e.hasMoreElements();) {
            nodeFromTasksVec = (MMObjectNode)e.nextElement();
            if (number==nodeFromTasksVec.getIntValue("number"))
                return true;
        }
        return false;
    }

    /**
     * Replaces a task node entry in the sorted task nodes vector with a new one.
     * @param node task node
     * @return true if task node was found and replaced,
     * false if tasks vector is empty or doesn't contain the node.
     */
    public boolean replaceTask(MMObjectNode node) {
        int number = node.getIntValue("number");
        Enumeration e = tasks.elements();
        if (!e.hasMoreElements()) {
            log.warn("Task nodes vector is empty.");
            return false;
        }
        for (MMObjectNode nodeFromTasksVec=null; e.hasMoreElements();) {
            nodeFromTasksVec = (MMObjectNode)e.nextElement();
            if (number==nodeFromTasksVec.getIntValue("number")) {
                tasks.removeElement(nodeFromTasksVec);
                tasks.addSorted(node);
                return true;
            }
        }
        return false;
    }
}