/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms.cmtelecom;

import org.mmbase.sms.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If using cmtelecom for notification, then this class must be scheduled as an mmbas cronjob. It
 * queues to be-send SMS-messages and then communicates with ClubMessage when this Job is
 * scheduled. It should run regularly, e.g. every 5 minutes or so. Or every minute. This way only
 * one external connection to mobile2you every 5 or 1 minutes is made.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: SenderJob.java,v 1.1 2007-11-12 17:44:06 michiel Exp $
 **/
public class SenderJob  implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(SenderJob.class);


    public void run() {
        Sender s = Sender.getInstance();
        if (s instanceof CMTelecomSender) {
            ((CMTelecomSender) s).trigger();
        } else {
            log.debug("No CMTelecom configured for SMS Sending");
        }
    }







}
