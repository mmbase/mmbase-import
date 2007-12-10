/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.bridge.*;
import org.mmbase.sms.*;
import org.mmbase.util.Casting;
import java.util.*;

/**
 * Implementation of an SMS message based on the 'notification' paradigm, ie. 2 mmbase nodes.
 */
public class NotificationSMS implements SMS {

    private final Node recipient;
    private final Node notifyable;
    private final Date date;
    public NotificationSMS(Node r, Node n, Date d) {
        recipient = r;
        notifyable = n;
        date = d;
    }

    public String getMessage() {
        return Notification.getMessage(notifyable, SMSNotification.class.getName());
    }
    public String getMobile() {
        return Casting.toString(recipient.getFunctionValue("phone", null));
    }
    public int getOperator() {
        return Casting.toInt(recipient.getFunctionValue("operator", null));
    }

    public Date getDate() {
        return date;
    }
    public String toString() {
        return notifyable.getFunctionValue("gui", null) + " -> " + getMobile();
    }
}
