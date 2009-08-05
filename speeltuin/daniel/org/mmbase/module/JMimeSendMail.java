/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.module;

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
public class JMimeSendMail extends AbstractSendMail {
    private static Logger log = Logging.getLoggerInstance(JMimeSendMail.class.getName());
    private Session session;

    public void reload() {
        init();
    }

    public void init() {                     
        try {
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
                log.info("Module JMimeSendMail started (datasource = " + datasource +  ")");
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
                log.info("Module JMimeSendMail started (smtphost = " + smtphost +  ")");
            }                

        } catch (javax.naming.NamingException e) {
            log.fatal("JMimeSendMail failure: " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
    }

    /**
     * Send mail with headers 
     */
    public boolean sendMail(String from, String to, String data, Map headers) {
        try {

            if (log.isServiceEnabled()) log.service("JMimeSendMail sending mail to " + to);
            // construct a message
            MimeMessage msg = new MimeMessage(session);
            if (from != null && ! from.equals("")) {
                msg.setFrom(new InternetAddress(from));
            }

            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            if (headers.get("CC") != null) {                
                msg.addRecipient(Message.RecipientType.CC,new InternetAddress((String) headers.get("CC")));
            }
            if (headers.get("BCC") != null) {
                msg.addRecipient(Message.RecipientType.CC,new InternetAddress((String) headers.get("BCC")));
            }
            msg.setSubject((String) headers.get("Subject"));
            msg.setText(data);
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
     * Send mail with headers 
     */
    public boolean sendMultiPartMail(String from, String to, Map headers,MimeMultipart mmpart) {
        try {

            if (log.isServiceEnabled()) log.service("JMimeSendMail sending mail to " + to);
            // construct a message
            MimeMessage msg = new MimeMessage(session);
            if (from != null && ! from.equals("")) {
                msg.setFrom(new InternetAddress(from));
            }

            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            if (headers.get("CC") != null) {                
                msg.addRecipient(Message.RecipientType.CC,new InternetAddress((String) headers.get("CC")));
            }
            if (headers.get("BCC") != null) {
                msg.addRecipient(Message.RecipientType.CC,new InternetAddress((String) headers.get("BCC")));
            }
            msg.setSubject((String) headers.get("Subject"));

            //msg.setText(data);
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

    public String getModuleInfo() {
        return("Sends mail through J2EE/JavaMail");
    }


    public boolean sendMultiPartMail(Mail mail,MimeMultipart parts) {
        return sendMultiPartMail(mail.from, mail.to, mail.headers,parts);
    }
}
