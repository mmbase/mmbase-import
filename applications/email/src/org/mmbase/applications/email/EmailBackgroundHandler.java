/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * 
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 */
public class EmailBackgroundHandler implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(EmailBackgroundHandler.class);

    // email node
    MMObjectNode node;

    /**
    * create a background thread with given email node
    */
    public EmailBackgroundHandler(MMObjectNode node) {
        this.node = node;
        Thread kicker = new Thread(this, "emailbackgroundhandler");
        kicker.setDaemon(true);
        kicker.start();

    }

    public void run() {
        try {
            EmailHandler.sendMailNode(node);
        } catch (Exception e) {
            log.error("run(): ERROR: Exception in emailbackgroundhandler thread!");
            log.error(Logging.stackTrace(e));
        }
    }
}
