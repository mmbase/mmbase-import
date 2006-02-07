package nl.didactor.mail;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
import javax.naming.*;
import java.util.Properties;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.JMSendMail;

/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 * Extended by Johannes Verelst to allow attachments to be sent.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since  MMBase-1.6
 */

public class ExtendedJMSendMail extends JMSendMail {
    private static Logger log=Logging.getLoggerInstance(ExtendedJMSendMail.class.getName());
    private Session session;

    /**
     * Send mail with headers AND attachments
     */
    public boolean sendMail(Node n) {
        return sendMail(null, n);
    }

    /**
     * Send mail with headers AND attachments to the emailaddresses
     * specified in the 'to' and 'cc' fields. If these are null, 
     * the values from the 'to' and 'cc' fields from the node
     * are used.
     */
    public boolean sendMail(String onlyto, Node n) {
        try {
            String from = n.getStringValue("from");
            String to = n.getStringValue("to");
            String cc = n.getStringValue("cc");

            String body = n.getStringValue("body");
            String subject = n.getStringValue("subject");

            if (log.isServiceEnabled()) log.service("JMSendMail sending mail to " + to);
            // construct a message
            MimeMessage msg = new MimeMessage(session);
            if (from != null && ! from.equals("")) {
                msg.setFrom(new InternetAddress(from));
            }

            InternetAddress[] recipients = InternetAddress.parse(to);
            for (int i=0; i<recipients.length; i++) {
                msg.addRecipient(Message.RecipientType.TO, recipients[i]);
            }

            recipients = InternetAddress.parse(cc);
            for (int i=0; i<recipients.length; i++) {
                msg.addRecipient(Message.RecipientType.CC, recipients[i]);
            }

            msg.setSubject(subject);

            /* add attachments here */
            NodeList attachments = n.getRelatedNodes("attachments");
            if (attachments.size() != 0) {
                MimeBodyPart bodypart = new MimeBodyPart();
                MimeMultipart mmp = new MimeMultipart("mixed");

                bodypart.setText(body, "UTF-8");
                mmp.addBodyPart(bodypart);

                for (int i=0; i<attachments.size(); i++) {
                    String filename = attachments.getNode(i).getStringValue("filename");
                    if (filename == null || filename.equals(""))
                        filename = "attached file";

                    String mimetype = attachments.getNode(i).getStringValue("mimetype");
                    if (mimetype == null || mimetype.equals(""))
                        mimetype = "application/octet-stream";

                    byte[] handle = attachments.getNode(i).getByteValue("handle");
                    MimeBodyPart mbp = new MimeBodyPart();

                    mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(handle))); 
                    mbp.setHeader("Content-Type", mimetype);

                    // If our attached file is text/html, we will create a new 'normal'
                    // bodypart. The email client will then show the HTML inline.
                    // Note that for this to work, you need a valid doctype definition
                    // in your body.
                    if (mimetype.equals("text/html")) {
                        if (!filename.equals("attached file")) {
                            mbp.setFileName(filename);
                        }
                    } else {
                        mbp.setFileName(filename); 
                        mbp.setDisposition(Part.ATTACHMENT);
                    }
                    
                    mmp.addBodyPart(mbp); 
                }
                msg.setContent(mmp);
            }else if( body.toLowerCase().indexOf( "<html>" ) != -1 ){
              msg.setContent(body, "text/html; charset=UTF-8");
            } else {
              msg.setText(body, "UTF-8");
            }
            
            try {
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();

                msg.writeTo(bos);
            } catch (java.io.IOException e) {
                log.error("Exception: " + e);
            }
            if (onlyto == null) {
                Transport.send(msg);
            } else {
                Transport.send(msg, InternetAddress.parse(onlyto));
            }
            log.debug("JMSendMail done.");
            return true;
        } catch (javax.mail.MessagingException e) {
            log.error("JMSendMail failure: " + e.getMessage());
            log.error(Logging.stackTrace(e));
        }
        return false;
    }

    public String getModuleInfo() {
        return("Sends mail through J2EE/JavaMail");
    }

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

    private class ByteArrayDataSource implements DataSource {

        private byte[] buffer;

        public ByteArrayDataSource(byte[] buffer) {
            this.buffer = buffer;
        }

        public java.lang.String getContentType() {
            return "application/octet-stream";
        }

        public java.io.InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(buffer);
        }
    
        public java.lang.String getName() {
            return "Bytearray datasource";
        }
    
        public java.io.OutputStream getOutputStream()  {
            return null;
        }
    }
}
