package nl.didactor.mail;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
import javax.naming.*;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.JMSendMail;
import org.mmbase.module.smtp.SMTPModule;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;

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
        log.debug("Start sendmail: " + onlyto + ", " + n);
        SMTPModule smtpModule = (SMTPModule)Module.getModule("smtpmodule");
        HashMap domains = new HashMap();

        if (smtpModule != null) {
            String sdomains = smtpModule.getLocalEmailDomains();
            StringTokenizer st = new StringTokenizer(sdomains, ",");
            while (st.hasMoreTokens()) {
                domains.put(st.nextToken().toLowerCase(), "1");
            }
        }
        log.debug("Have domains: " + domains);

        Vector remoteRecipients = new Vector();
        Vector localRecipients = new Vector();
        if (onlyto != null) {
            try {
                InternetAddress[] recipients = InternetAddress.parse(onlyto);
                for (int i=0; i<recipients.length; i++) {
                    String domain = recipients[i].getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.containsKey(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipients[i]);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipients[i]);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            String to = n.getStringValue("to");
            try {
                InternetAddress[] recipients = InternetAddress.parse(to);
                for (int i=0; i<recipients.length; i++) {
                    String domain = recipients[i].getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.containsKey(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipients[i]);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipients[i]);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }

            String cc = n.getStringValue("cc");
            try {
                InternetAddress[] recipients = InternetAddress.parse(cc);
                for (int i=0; i<recipients.length; i++) {
                    String domain = recipients[i].getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.containsKey(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipients[i]);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipients[i]);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        log.debug("Locals: " + localRecipients + ", remotes: " + remoteRecipients);

        if (localRecipients.size() > 0) {
            InternetAddress[] ia = new InternetAddress[localRecipients.size()];
            for (int i=0; i<localRecipients.size(); i++) {
                ia[i] = (InternetAddress)localRecipients.get(i);
            }
            sendLocalMail(ia, n);
        }

        if (remoteRecipients.size() > 0) {
            InternetAddress[] ia = new InternetAddress[remoteRecipients.size()];
            for (int i=0; i<remoteRecipients.size(); i++) {
                ia[i] = (InternetAddress)remoteRecipients.get(i);
            }
            sendRemoteMail(ia, n);
        }

        return true;
    }

    public void sendLocalMail(InternetAddress[] to, Node n) {
        log.debug("sendLocalMail: Sending node {" + n + "} to addresses {" + to + "}");
        Cloud cloud = n.getCloud();
        NodeManager peopleManager = cloud.getNodeManager("people");
        NodeManager emailManager = cloud.getNodeManager("emails");
        NodeManager attachmentManager = cloud.getNodeManager("attachments");
        RelationManager relatedManager = cloud.getRelationManager("related");

        for (int i=0; i<to.length; i++) {
            String domain = to[i].getAddress();
            String username = domain.substring(0, domain.indexOf("@"));
            domain = domain.substring(domain.indexOf("@") + 1, domain.length());

            NodeQuery nq = peopleManager.createQuery();
            nq.setConstraint(nq.createConstraint(nq.createStepField("username"), username));
            NodeList people = peopleManager.getList(nq);
            if (people.size() != 1) {
                log.error("There are " + people.size() + " users with username '" + username + "', aborting for this user!");
                continue;
            }

            Node person = people.getNode(0);
            NodeList mailboxes = person.getRelatedNodes("mailboxes");
            for (int j=0; j<mailboxes.size(); j++) {
                Node mailbox = mailboxes.getNode(j);
                if (mailbox.getIntValue("type") == 0) {
                    log.debug("Found mailbox, adding mail now");
                    Node emailNode = emailManager.createNode();
                    emailNode.setValue("to", n.getValue("to"));
                    emailNode.setValue("from", n.getValue("from"));
                    emailNode.setValue("cc", n.getValue("cc"));
                    emailNode.setValue("subject", n.getValue("subject"));
                    emailNode.setValue("date", n.getValue("date"));
                    emailNode.setValue("body", n.getValue("body"));
                    emailNode.setIntValue("type", 2);
                    emailNode.commit();
                    mailbox.createRelation(emailNode, relatedManager).commit();

                    NodeList attachments = n.getRelatedNodes("attachments");
                    for (int k=0; k<attachments.size(); k++) {
                        log.debug("Adding attachment(" + k + ")");
                        Node oldAttachment = attachments.getNode(k);
                        Node newAttachment = attachmentManager.createNode();
                        newAttachment.setValue("title", oldAttachment.getValue("title"));
                        newAttachment.setValue("description", oldAttachment.getValue("description"));
                        newAttachment.setValue("mimetype", oldAttachment.getValue("mimetype"));
                        newAttachment.setValue("filename", oldAttachment.getValue("filename"));
                        newAttachment.setValue("size", oldAttachment.getValue("size"));
                        newAttachment.setValue("handle", oldAttachment.getValue("handle"));
                        newAttachment.setValue("date", oldAttachment.getValue("date"));
                        newAttachment.setValue("showtitle", oldAttachment.getValue("showtitle"));
                        newAttachment.commit();
                        emailNode.createRelation(newAttachment, relatedManager).commit();
                    }
                }
            }

            // If this person has mail forwarding enabled, we have to forward it to his local email address
            if (person.getBooleanValue("email-mayforward")) {
                String mailadres = person.getStringValue("email");
                log.debug("This user has email forwarding enabled .. forwarding email to [" + mailadres + "]");
                try {
                    sendRemoteMail(InternetAddress.parse(mailadres), n);
                } catch (Exception e) {
                    log.warn("Exception when trying to forward email to [" + mailadres + "]: " + e.getMessage());
                }
            }
        }
        log.debug("Finished processing local mails");
    }

    public void sendRemoteMail(InternetAddress[] onlyto, Node n) {
        log.debug("Sending node {" + n + "} to addresses {" + onlyto + "}");
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

            InternetAddress[] toRecipients = InternetAddress.parse(to);
            for (int i=0; i<toRecipients.length; i++) {
                msg.addRecipient(Message.RecipientType.TO, toRecipients[i]);
            }

            InternetAddress[] ccRecipients = InternetAddress.parse(cc);
            for (int i=0; i<ccRecipients.length; i++) {
                msg.addRecipient(Message.RecipientType.CC, ccRecipients[i]);
            }

            msg.setSubject(subject, "UTF-8");

            /* add attachments here */
            NodeList attachments = n.getRelatedNodes("attachments");
            if (attachments.size() != 0) {
                MimeBodyPart bodypart = new MimeBodyPart();
                MimeMultipart mmp = new MimeMultipart("mixed");

                bodypart.setContent(body, "text/html; charset=UTF-8");
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
            } else {
              msg.setContent(body, "text/html; charset=UTF-8");
            }
            
            try {
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();

                msg.writeTo(bos);
            } catch (java.io.IOException e) {
                log.error("Exception: " + e);
            }

            Transport.send(msg, onlyto);
            log.debug("JMSendMail done.");
        } catch (javax.mail.MessagingException e) {
            log.error("JMSendMail failure: " + e.getMessage());
            log.error(Logging.stackTrace(e));
        }
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
