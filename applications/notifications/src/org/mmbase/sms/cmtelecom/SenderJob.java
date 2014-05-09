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
 * If using cmtelecom for notification, then this class must be scheduled as an mmbase cronjob. It
 * queues to be-send SMS-messages and then communicates with ClubMessage when this Job is
 * scheduled. It should run regularly, e.g. every 5 minutes or so. Or every minute. This way only
 * one external connection to clubmessage every 5 or 1 minutes is made.
 *
 * If the implementation of SMS sending is <em>not</em> CMTelecomSender (bug e.g. {@link
 * org.mmbase.sms.EventSender}), this job will do nothing.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public class SenderJob implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(SenderJob.class);


    public void run() {
        try {
            Sender s = Sender.getInstance();
            if (s instanceof CMTelecomSender) {
                ((CMTelecomSender) s).trigger();
            } else {
                log.debug("No CMTelecom configured for SMS Sending");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }



}
