/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.sms.*;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A notification implementation which uses SMS.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public  class SMSNotification extends Notification {

    private static final Logger log = Logging.getLoggerInstance(SMSNotification.class);

    @Override
    public void send(Relation notification, Date date) {
        Node recipient = notification.getSource();
        int operator = Casting.toInt(recipient.getFunctionValue("operator", null));
        if (operator < 0) {
            log.service("Ignoring sms to " + recipient + " because recipient's phone number not yet validated");
        } else {
            Node notifyable = notification.getDestination();
            String message = format(notification, getMessage(notification), getFormatParameters(notification, date));
            String mobile = Casting.toString(recipient.getFunctionValue("phone", null));
            SMS sms = new BasicSMS(mobile, operator, message);
            Sender.getInstance().offer(sms);
        }
    }


}
