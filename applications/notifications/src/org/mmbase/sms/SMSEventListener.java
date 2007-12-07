/*
 * Created on 6-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.sms;

import org.mmbase.core.event.*;

/**
 * @author Michiel Meeuwissen
 */
public class SMSEventListener implements EventListener {

    private static final SMSEventListener instance = new SMSEventListener();

    public static SMSEventListener getInstance() {
        return instance;
    }
    private SMSEventListener() {

    }
    public void notify(SMSEvent event) {
        if (event.isImmediate()) {
            Sender.getInstance().send(event.getSMS());
        } else {
            Sender.getInstance().offer(event.getSMS());
        }
    }
}
