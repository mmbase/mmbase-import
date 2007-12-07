/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import java.util.*;
import java.util.regex.*;

import org.mmbase.core.event.EventManager;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * SMS Sender implementation, which only wraps the message in an {@link #SMSEvent} and offers it to
 * the Event Manager.
 *
 * @author Michiel Meeuwissen
 * @version $Id: EventSender.java,v 1.1 2007-12-07 13:06:43 michiel Exp $
 **/
public class EventSender extends Sender {
    private static final Logger log = Logging.getLoggerInstance(EventSender.class);

    @Override
    public boolean send(SMS sms) {
        log.service("Sending remote SMS to " + sms.getMobile());
        EventManager.getInstance().propagateEvent(new SMSEvent(sms, true));
        return true;
    }

    @Override
    public boolean offer(SMS sms) {
        log.service("Sending remote SMS to " + sms.getMobile());
        EventManager.getInstance().propagateEvent(new SMSEvent(sms, false));
        return true;
    }

    @Override
    public Collection<SMS> getQueue() {
        return Collections.emptyList();
    }



}
