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
 * 
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 * @author Daniel Ockeloen
 */
public class EmailBackgroundHandler implements Runnable {

    static private final Logger log = Logging.getLoggerInstance(EmailBackgroundHandler.class); 

    // email node
    private MMObjectNode node;


    /**
     * create a background thread with given email node
     */
    public EmailBackgroundHandler(MMObjectNode node) {
        this.node = node;
        Thread kicker = new Thread(this, "emailbackgroundhandler");
        //kicker.setDaemon(false); // mail should be sent before jvm can end
        kicker.start();
    }

    
    /**
     * Main run, exception protected
     */
    public void run () {
	// now we run in a new thread call
	// the email handler to start sending the node
        try {
            EmailHandler.sendMailNode(node);
        } catch(Exception e) {
            log.error("run(): ERROR: Exception in emailbackgroundhandler thread!");
            log.error(Logging.stackTrace(e));
        }
    }

}
