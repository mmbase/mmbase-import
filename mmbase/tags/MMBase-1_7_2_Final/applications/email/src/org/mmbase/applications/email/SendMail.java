/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.net.*;
import java.util.*;
import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


import org.mmbase.util.*;
import org.mmbase.util.logging.*;


/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @since  MMBase-1.6
 * @version $Id: SendMail.java,v 1.1.2.4 2004-06-01 13:51:24 michiel Exp $
 */
public class SendMail extends org.mmbase.module.JMSendMail implements SendMailInterface {
    private static final Logger log = Logging.getLoggerInstance(SendMail.class);

    public static final String DEFAULT_MAIL_ENCODING="ISO-8859-1";

    public static String mailEncoding = DEFAULT_MAIL_ENCODING;

    /**
     * {@inheritDoc}
     */
    public boolean sendMultiPartMail(String from, String to, Map headers, MimeMultipart mmpart) {
        try {

            MimeMessage msg = constructMessage(from, to, headers);

            msg.setContent(mmpart);

            Transport.send(msg);
            log.debug("JMimeSendMail done.");
            return true;
        } catch (javax.mail.MessagingException e) {
            log.error("JMimeSendMail failure: " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */

    public String getModuleInfo() {
        return("Sends mail through J2EE/JavaMail, supporting MultiPart");
    }



}
