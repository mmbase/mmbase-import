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
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 */
public class EmailBackgroundHandler implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(EmailBackgroundHandler.class.getName()); 

    // Thread
    Thread kicker = null;

    // email node
    MMObjectNode node;

    /**
    * create a background thread with given email node
    */
    public EmailBackgroundHandler(MMObjectNode node) {
        this.node=node;
        init();
    }

    /**
    * init the thread
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
            kicker = new Thread(this,"emailbackgroundhandler");
            kicker.start();
        }
    }
    
    /**
     * Main run, exception protected
     */
    public void run () {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  
        try {
            doWork();
        } catch(Exception e) {
            log.error("run(): ERROR: Exception in emailbackgroundhandler thread!");
            log.error(Logging.stackTrace(e));
         }
    }

    /**
     * Main work handelr
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  

	// now we run in a new thread call
	// the email handler to start sending the node
        EmailHandler.sendMailNode(node);
    }

}
