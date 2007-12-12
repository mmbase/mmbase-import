/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.sms.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A notification implementation which uses SMS.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SMSNotification.java,v 1.2 2007-11-26 16:15:48 michiel Exp $
 **/
public  class SMSNotification extends Notification {

    private static final Logger log = Logging.getLoggerInstance(SMSNotification.class);

    public void send(Node recipient, Node notifyable, Date date) {
        SMS sms = new NotificationSMS(recipient, notifyable, date);
        if (sms.getOperator() < 0) {
            log.service("Ignoring sms " + sms + " because recipient's phone number not yet validated");
        } else {
            Sender.getInstance().offer(sms);
        }
    }


}
