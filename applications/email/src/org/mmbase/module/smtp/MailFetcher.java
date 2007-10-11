/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.io.*;
import org.mmbase.applications.crontab.*;
import javax.mail.*;
import javax.mail.search.*;
import javax.mail.event.*;
import javax.mail.internet.*;

/**
 * The abstraction of a class that somehow fetches mail. It is not defined how these fetchers are
 * used. Current implementations create Fetchers using crontab {@link PopFetcher} or are driven by a
 * seperate thread ({@link SMTPFetcher} is instantiated by {@link SMTPListener}), which in turn is
 * bootstrapped by a module {@link SMTPModule}.
 *
 * For convenience this abstract base implementation implements also {@link MailHandler}.
 *
 * @version $Id: MailFetcher.java,v 1.1 2007-10-11 17:47:50 michiel Exp $
 */
public abstract class MailFetcher implements MailHandler {
    private static final Logger log = Logging.getLoggerInstance(MailFetcher.class);


    protected final MailHandler handler;

    MailFetcher(MailHandler h) {
        handler  = h;
    }

    public final boolean handleMessage(Message message) {
        return handler.handleMessage(message);
    }
    public final boolean addMailbox(String user) {
        return handler.addMailbox(user);
    }
    public final void clearMailboxes() {
        handler.clearMailboxes();
    }
    public final int size() {
        return handler.size();
    }


}
