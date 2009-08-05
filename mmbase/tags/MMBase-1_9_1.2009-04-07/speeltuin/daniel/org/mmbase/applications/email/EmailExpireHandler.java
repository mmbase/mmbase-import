/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 *
 * expire handler, removes email nodes that are
 * need to be expired
 */
public class EmailExpireHandler implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(EmailExpireHandler.class.getName()); 

    // thread
    Thread kicker = null;

    // sleeptime between expire runs in seconds
    int sleeptime;

    // expire time (default 30min, set in the builder.xml), defined in seconds
    int expiretime;

    // parent builder needed for callbacks
    EmailBuilder parent;


    /**
    *  create a handler with sleeptime and expiretime
    */
    public EmailExpireHandler(EmailBuilder parent,int sleeptime,int expiretime) {
	this.parent=parent;
        this.sleeptime=sleeptime;
        this.expiretime=expiretime;
        init();
    }

    /**
    * init()
    */
    public void init() {
        this.start();    
    }


    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"emailexpireprobe");
            kicker.start();
        }
    }
    
    /**
     * Stops the main Thread.
     */
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
    public void run () {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  
        while (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                log.error("run(): ERROR: Exception in emailqueueprobe thread!");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Main work loop
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  

        while (kicker!=null) {
		// get the nodes we want to expire
        	Enumeration e=parent.getMailedOlderThen(expiretime);
   		while (e.hasMoreElements()) {
			// get next node 
			MMObjectNode expirenode=(MMObjectNode)e.nextElement();
	
			// remove all its relations
			expirenode.removeRelations();

			// remove the node itself, by asking its builder
			parent.removeNode(expirenode);
		}
            	try {Thread.sleep(sleeptime*1000);} catch (InterruptedException f){}
        }
    }

}
