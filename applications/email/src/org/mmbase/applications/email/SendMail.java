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
 */
public class SendMail extends org.mmbase.module.AbstractSendMail implements SendMailInterface {
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


    /**
     * =========================================================================================================
     * BELOW THIS IS COPIED . DONT CHANGE ANYTHING HERE WITHOUT DOING IT IN JMSENDMAIL AS WELL
     * from JMSendMail 1.7.1. overriden to be compitable with mmbase 1.7.0, can be removed later (and then _extend_ from JMSendMail)
     */

    private Session session;

    /**
     * {@inheritDoc}
     */
    public void reload() {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        try {
            String encoding = getInitParameter("encoding");
            if (encoding == null) mailEncoding = encoding;

            String smtphost   = getInitParameter("mailhost");
            String context    = getInitParameter("context");
            String datasource = getInitParameter("datasource");
            session = null;
            if (smtphost == null) {
                if (context == null) {
                    context = "java:comp/env";
                    log.warn("The property 'context' is missing, taking default " + context);
                }
                if (datasource == null) {
                    datasource = "mail/Session";
                    log.warn("The property 'datasource' is missing, taking default " + datasource);
                }

                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup(context);
                session = (Session) envCtx.lookup(datasource);
                log.info("Module JMSendMail started (datasource = " + datasource +  ")");
            } else {
                if (context != null) {
                    log.error("It does not make sense to have both properties 'context' and 'mailhost' in email module");
                }
                if (datasource != null) {
                    log.error("It does not make sense to have both properties 'datasource' and 'mailhost' in email module");
                }
                log.info("EMail module is configured using 'mailhost' proprerty.\n" +
                         "Consider using J2EE compliant 'context' and 'datasource'\n" +
                         "Which means to put something like this in your web.xml:\n" +
                         "  <resource-ref>\n" +
                         "     <description>Email module mail resource</description>\n" +
                         "     <res-ref-name>mail/MMBase</res-ref-name>\n" +
                         "     <res-type>javax.mail.Session</res-type>\n" +
                         "     <res-auth>Container</res-auth>\n" +
                         "  </resource-ref>\n" +
                         " + some app-server specific configuration (e.g. in orion the 'mail-session' entry in the application XML)"
                         );

                Properties prop = System.getProperties();
                prop.put("mail.smtp.host", smtphost);
                session = Session.getInstance(prop, null);
                log.info("Module JMSendMail started (smtphost = " + smtphost +  ")");
            }

        } catch (javax.naming.NamingException e) {
            log.fatal("JMSendMail failure: " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
    }

    /**
     * Utility method to do the generic job of creating a MimeMessage object and setting its recipients and 'from'.
     */

    protected MimeMessage constructMessage(String from, String to, Map headers) throws MessagingException {
        if (log.isServiceEnabled()) {
            log.service("JMSendMail sending mail to " + to);
        }
        // construct a message
        MimeMessage msg = new MimeMessage(session);
        if (from != null && ! from.equals("")) {
            msg.addFrom(InternetAddress.parse(from));
        }

        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        if (headers.get("CC") != null) {
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse((String) headers.get("CC")));
        }
        if (headers.get("BCC") != null) {
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse((String) headers.get("BCC")));
        }

        if (headers.get("Reply-To") != null) {
            msg.setReplyTo(InternetAddress.parse((String) headers.get("Reply-To")));
        }

        msg.setSubject((String) headers.get("Subject"));

        return msg;
    }

    /**
     * Send mail with headers
     */
    public boolean sendMail(String from, String to, String data, Map headers) {
        try {
            MimeMessage msg = constructMessage(from, to, headers);

            msg.setText(data,mailEncoding);
            Transport.send(msg);
            log.debug("JMSendMail done.");
            return true;
        } catch (MessagingException e) {
            log.error("JMSendMail failure: " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
        return false;
    }





}
