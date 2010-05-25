/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.applications.email;

import java.util.*;
import org.mmbase.core.event.*;
import javax.mail.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.9.3
 * @version $Id: TransactionEvent.java 41369 2010-03-15 20:54:45Z michiel $
 */
public abstract class EmailEvent extends Event {
    private static final Logger log = Logging.getLoggerInstance(EmailEvent.class);



    public static class Sent extends EmailEvent {
        private static final long serialVersionUID = 1L;

        private final Address[] to;
        public Sent(Message mess) throws MessagingException {
            to = mess.getRecipients(Message.RecipientType.TO);
        }
        @Override
        public String toString() {
            return "sent:" + Arrays.asList(to);
        }
    }

}
