/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import org.mmbase.module.*;
import org.mmbase.util.*;
import java.util.*;
import javax.mail.internet.*;

/**
 * This module provides mail functionality
 *
 * @application Mail
 * @author Michiel Meeuwissen
 */
abstract public class AbstractSendMail extends WatchedReloadableModule {

    public AbstractSendMail(String name) {
        super(name);
    }

    /**
     * Send mail without extra headers
     */
    public boolean sendMail(String from, String to, String data) {
        return sendMail(from, to, data, null);
    }

    /**
     * Send mail
     */
    public boolean sendMail(Mail mail) {
        return sendMail(mail.from, mail.to, mail.text, mail.headers);
    }

    /**
     * @javadoc
     */
    public abstract boolean sendMail(String from, String to, String data, Map<String, String> headers);


    /**
     * Sends a 'multipart' mail.
     *
     */

    public abstract boolean sendMultiPartMail(String from, String to, Map<String, String> headers, MimeMultipart mmpart) throws javax.mail.MessagingException;


}
