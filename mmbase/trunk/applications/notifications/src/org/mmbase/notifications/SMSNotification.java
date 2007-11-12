/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.sms.Sender;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * A notification implementation which uses SMS.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SMSNotification.java,v 1.1 2007-11-12 17:44:06 michiel Exp $
 **/
public  class SMSNotification extends Notification {


    public void send(Node recipient, Node notifyable, Date date) {
        Sender.getInstance().offer(new NotificationSMS(recipient, notifyable, date));
    }


}
