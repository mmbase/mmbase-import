/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;
import java.util.*;
import java.text.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A notification implementation which sends using mmbase-email.jar
 *
 * @author Michiel Meeuwissen
 * @version $Id: EmailNotification.java,v 1.5 2007-12-10 18:15:05 michiel Exp $
 **/
public  class EmailNotification extends Notification {

    private static final Logger log = Logging.getLoggerInstance(EmailNotification.class);

    @Override
    public void send(Relation notification, Date date) {
        Node recipient = notification.getSource();
        String address = recipient.getFunctionValue("email", null).toString();
        log.service("Sending notification email to " + address);
        NodeManager emails = recipient.getCloud().getNodeManager("email");
        Node email = emails.createNode();
        email.setStringValue("to", address);
        Node notifyable = notification.getDestination();
        Object[] parameters = getFormatParameters(notification, date);
        email.setStringValue("subject",  format(notification, notifyable.getStringValue("title"), parameters));
        email.setStringValue("body",  format(notification, getMessage(notification), parameters));
        email.commit();
        email.getFunctionValue("startmail", null);

    }


}
