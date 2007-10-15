/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications.mobile2you;

import org.mmbase.notifications.Notification;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A notification implementation which sends using the Mobile2You SMS-gateway. This is implemented
 * by offering it to the (static) queue of {@link SenderJob}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Mobile2YouNotification.java,v 1.1 2007-10-15 13:52:55 michiel Exp $
 **/
public  class Mobile2YouNotification extends Notification {

    private static final Logger log = Logging.getLoggerInstance(Mobile2YouNotification.class);

    public void send(Node recipient, Node notifyable) {
        SenderJob.offer(recipient, notifyable);

    }


}
