/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.*;

import org.mmbase.module.SendMailInterface;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @since  MMBase-1.6
 * @version $Id: SendMail.java,v 1.24 2007-01-08 14:00:25 michiel Exp $
 */
public class SendMail extends AbstractSendMail implements SendMailInterface {
    private static final Logger log = Logging.getLoggerInstance(SendMail.class);

    public static final String DEFAULT_MAIL_ENCODING = "ISO-8859-1";

    public static String mailEncoding = DEFAULT_MAIL_ENCODING;

    public static long emailSent = 0;
    public static long emailFailed = 0;

    /**
     */
    public boolean sendMultiPartMail(String from, String to, Map<String, String> headers, MimeMultipart mmpart) {
        if (log.isServiceEnabled()) {
            log.service("Sending (multipart) mail to " + to);
        }
        try {

            MimeMessage msg = constructMessage(from, to, headers);

            msg.setContent(mmpart);

            Transport.send(msg);

            emailSent++;
            log.debug("JMimeSendMail done.");
            return true;
        } catch (javax.mail.MessagingException e) {
            emailFailed++;
            log.error("JMimeSendMail failure: " + e.getMessage());
            log.debug(e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getModuleInfo() {
        return "Sends mail through J2EE/JavaMail, supporting MultiPart";
    }


    protected Session session;

    /**
     */
    public void reload() {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        try {
            MMBase mmb = MMBase.getMMBase();
            mailEncoding = mmb.getEncoding();
            String encoding = getInitParameter("encoding");
            if (encoding != null && !encoding.equals("")) {
                mailEncoding = encoding;
            }

            String smtpHost = getInitParameter("mailhost");
            String smtpPort = getInitParameter("mailport");

            String userName  = getInitParameter("user");
            String password  = getInitParameter("password");

            String context = getInitParameter("context");
            String dataSource = getInitParameter("datasource");
            session = null;
            if (smtpHost == null) {
                if (context == null) {
                    context = "java:comp/env";
                    log.warn("The property 'context' is missing, taking default " + context);
                }
                if (dataSource == null) {
                    dataSource = "mail/Session";
                    log.warn("The property 'datasource' is missing, taking default " + dataSource);
                }

                Context initCtx = new InitialContext();
                Context envCtx = (Context)initCtx.lookup(context);
                Object o = envCtx.lookup(dataSource);
                if (o instanceof Session) {
                    session = (javax.mail.Session) o;
                } else {
                    log.fatal("Configured dataSource '" + dataSource + "' of context '" + context + "' is not a Session but " + (o == null ? "NULL" : "a " + o.getClass().getName()));
                    return;
                }
                log.info("Module SendMail started (datasource = " + dataSource + " -> " + session.getProperties() + ")");
            } else {
                if (context != null) {
                    log.error("It does not make sense to have both properties 'context' and 'mailhost' in email module");
                }
                if (dataSource != null) {
                    log.error("It does not make sense to have both properties 'datasource' and 'mailhost' in email module");
                }
                log.service("EMail module is configured using 'mailhost' property. Consider using J2EE compliant 'context' and 'datasource' properties.");

                Properties prop = System.getProperties();
                StringBuilder buf = new StringBuilder(smtpHost);
                prop.put("mail.smtp.host", smtpHost);

                if (smtpPort != null && smtpPort.trim().length() > 0) {
                    prop.put("mail.smtp.port", smtpPort);
                    buf.append(':').append(smtpPort);
                }
                // When username and password are specified, turn on smtp authentication.
                boolean smtpAuth = userName != null && userName.trim().length() != 0 && password != null;
                prop.setProperty("mail.smtp.auth", Boolean.toString(smtpAuth));
                if (smtpAuth) buf.insert(0, userName + "@");

                session = Session.getInstance(prop, new SimpleAuthenticator(userName, password));

                log.info("Module SendMail started SMTP: " + buf);
            }

        } catch (javax.naming.NamingException e) {
            log.fatal("SendMail failure: " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
    }

    /**
     * Utility method to do the generic job of creating a MimeMessage object and setting its recipients and 'from'.
     */
    protected MimeMessage constructMessage(String from, String to, Map<String, String> headers) throws MessagingException {
        // construct a message
        MimeMessage msg = new MimeMessage(session);
        if (from != null && !from.equals("")) {
            msg.addFrom(InternetAddress.parse(from));
        }

        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        String cc = headers.get("CC");
        if (cc != null) {
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        String bcc = headers.get("BCC");
        if (bcc != null) {
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(bcc));
        }

        String replyTo = headers.get("Reply-To");
        if (replyTo != null) {
            msg.setReplyTo(InternetAddress.parse(replyTo));
        }
        String sub = (String)headers.get("Subject");
        if (sub == null || "".equals(sub)) sub = "<no subject>";
        msg.setSubject(headers.get("Subject"));

        return msg;
    }

    /**
     * Send mail with headers
     */
    public boolean sendMail(String from, String to, String data, Map<String, String> headers) {
        if (log.isServiceEnabled()) {
            log.service("Sending mail to " + to + " Headers " + headers);
        }
        try {
            MimeMessage msg = constructMessage(from, to, headers);
            msg.setText(data, mailEncoding);
            Transport.send(msg);
            log.debug("SendMail done.");
            return true;
        } catch (MessagingException e) {
            log.error("SendMail failure: " + e.getMessage() + " from: " + from + " to: " + to);
            log.debug(e);
        }
        return false;
    }
}
