/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications.cmtelecom;

import org.mmbase.notifications.Notification;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A notification implementation which sends using CMTelecom SMS-gateway. This is implemented
 * by offering it to the (static) queue of {@link SenderJob}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CMTelecomNotification.java,v 1.3 2007-11-12 16:02:48 michiel Exp $
 **/
public  class CMTelecomNotification extends Notification {

    private static final Logger log = Logging.getLoggerInstance(CMTelecomNotification.class);

    public void send(Node recipient, Node notifyable, Date date) {
        SenderJob.offer(recipient, notifyable, date);

    }


}
