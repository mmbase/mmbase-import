/*
 * Created on 6-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.sms;

import org.mmbase.core.event.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Michiel Meeuwissen
 */
public class SMSEventListener implements EventListener {

    private static final Logger log = Logging.getLoggerInstance(SMSEventListener.class);

    private static final SMSEventListener instance = new SMSEventListener();

    public static SMSEventListener getInstance() {
        return instance;
    }
    private SMSEventListener() {

    }
    public void notify(SMSEvent event) {
        log.debug("Received " + event);
        if (event.isImmediate()) {
            Sender.getInstance().send(event.getSMS());
        } else {
            Sender.getInstance().offer(event.getSMS());
        }
    }
}
