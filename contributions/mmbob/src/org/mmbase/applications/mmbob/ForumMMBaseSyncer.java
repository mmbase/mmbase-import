/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 *
 */
public class ForumMMBaseSyncer implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(ForumMMBaseSyncer.class); 

    // thread
    Thread kicker = null;

    int sleeptime;

    int delaytime;

    int maxqueue;

    private Vector dirtyNodes=new Vector();

    /**
    */
    public ForumMMBaseSyncer(int sleeptime,int maxqueue,int startdelay) {
        this.sleeptime=sleeptime;
        this.maxqueue=maxqueue;
        this.delaytime=startdelay;
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
            kicker = new Thread(this,"forummmbasesyncer");
            kicker.start();
        }
    }
    
    /**
     * Stops the main Thread.
     */
    public void stop() {
        /* Stop thread */
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
                log.error("run(): ERROR: Exception in forummmbasesyncer thread!");
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
		try {
		while (dirtyNodes.size()>0) {
			Node node=(Node)dirtyNodes.elementAt(0);
			dirtyNodes.removeElementAt(0);
			node.commit();
			//log.info("synced node="+node.getNumber());
            		Thread.sleep(delaytime);
		}
            	Thread.sleep(sleeptime);
		} catch (InterruptedException f2){}
        }
    }

    public void nodeDeleted(Node node) {
	dirtyNodes.remove(node);
    }

    public void syncNode(Node node) {
	if (!dirtyNodes.contains(node)) {
		dirtyNodes.addElement(node);
		//log.info("added node="+node.getNumber()+" to sync queue "+sleeptime);
	} else {
		//log.info("refused node="+node.getNumber()+" allready in sync queue "+sleeptime);
	}
    }

}
