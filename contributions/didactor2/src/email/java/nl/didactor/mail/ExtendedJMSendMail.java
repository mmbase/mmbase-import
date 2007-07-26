package nl.didactor.mail;
import javax.mail.internet.*;
import javax.mail.*;
import javax.mail.util.ByteArrayDataSource;
import javax.activation.*;
import javax.naming.*;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.applications.email.SendMail;
import org.mmbase.module.smtp.SMTPModule;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;

/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 * Extended by Johannes Verelst to allow attachments to be sent.
 *
 * @todo Merge this code to org.mmbase.applications.email.SendMail.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since  MMBase-1.6
 * @version $Id: ExtendedJMSendMail.java,v 1.24 2007-07-26 14:39:03 michiel Exp $
 */

public class ExtendedJMSendMail extends SendMail {
    private static final Logger log = Logging.getLoggerInstance(ExtendedJMSendMail.class);

    /**
     * Send mail with headers AND attachments
     */
    public boolean sendMail(Node n) {
        return sendMail(null, n);
    }


    protected Set<String> getDomains() {
        Set<String> domains = new HashSet<String>();
        SMTPModule smtpModule = (SMTPModule)Module.getModule("smtpmodule");
        if (smtpModule != null) {
            String sdomains = smtpModule.getLocalEmailDomains();
            StringTokenizer st = new StringTokenizer(sdomains, ",");
            while (st.hasMoreTokens()) {
                domains.add(st.nextToken().toLowerCase());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Have domains: " + domains);
        }
        return domains;
    }

    /**
     * Send mail with headers AND attachments to the emailaddresses
     * specified in the 'to' and 'cc' fields. If these are null,
     * the values from the 'to' and 'cc' fields from the node
     * are used.
     */
    public boolean sendMail(String onlyto, Node n) {
        if (log.isDebugEnabled()) {
            log.debug("Start sendmail: " + onlyto + ", " + n);
        }
        Set domains = getDomains();

        List<InternetAddress> remoteRecipients = new ArrayList<InternetAddress>();
        List<InternetAddress> localRecipients = new ArrayList<InternetAddress>();
        if (onlyto != null) {
            try {
                for (InternetAddress recipient : InternetAddress.parse(onlyto)) {
                    String domain = recipient.getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.contains(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipient);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            String to = n.getStringValue("to");
            try {
                for (InternetAddress recipient : InternetAddress.parse(to)) {
                    String domain = recipient.getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.contains(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipient);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
                // whould we not send some notification about this?
            }

            String cc = n.getStringValue("cc");
            try {
                for (InternetAddress recipient : InternetAddress.parse(cc)) {
                    String domain = recipient.getAddress();
                    domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                    if (domains.contains(domain.toLowerCase())) {
                        log.debug("Known domain [" + domain + "], processing internally");
                        localRecipients.add(recipient);
                    } else {
                        log.debug("Unknown domain [" + domain + "], processing externally");
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
            String bcc     = n.getNodeManager().hasField("bcc") ? n.getStringValue("bcc") : null;
            if (bcc != null) {
                try {
                    for (InternetAddress recipient : InternetAddress.parse(bcc)) {
                        String domain = recipient.getAddress();
                        domain = domain.substring(domain.indexOf("@") + 1, domain.length());
                        if (domains.contains(domain.toLowerCase())) {
                            log.debug("Known domain [" + domain + "], processing internally");
                            localRecipients.add(recipient);
                        } else {
                            log.debug("Unknown domain [" + domain + "], processing externally");
                            remoteRecipients.add(recipient);
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Locals: " + localRecipients + ", remotes: " + remoteRecipients);
        }

        if (localRecipients.size() > 0) {
            sendLocalMail(localRecipients.toArray(new InternetAddress[]{}), n);
        }

        if (remoteRecipients.size() > 0) {
            log.service("Sending remote to " + remoteRecipients);
            sendRemoteMail(remoteRecipients.toArray(new InternetAddress[]{}), n);
        }

        return true;
    }

    public void sendLocalMail(InternetAddress[] to, Node n) {
        if (log.isDebugEnabled()) {
            log.debug("sendLocalMail: Sending node {" + n + "} to addresses {" + Arrays.asList(to) + "}");
        }
        Cloud cloud = n.getCloud();
        NodeManager peopleManager = cloud.getNodeManager("people");
        NodeManager emailManager = cloud.getNodeManager("emails");
        NodeManager attachmentManager = cloud.getNodeManager("attachments");
        RelationManager relatedManager = cloud.getRelationManager("related");
        Set failedUsers = new HashSet();
        for (InternetAddress recipient : to) {
            log.debug("address " + recipient);
            String domain = recipient.getAddress();
            String username = domain.substring(0, domain.indexOf("@"));
            domain = domain.substring(domain.indexOf("@") + 1, domain.length());

            NodeQuery nq = peopleManager.createQuery();
            nq.setConstraint(nq.createConstraint(nq.createStepField("username"), username));
            NodeList people = peopleManager.getList(nq);
            if (people.size() != 1) {
                log.warn("There are " + people.size() + " users with username '" + username + "', aborting for this user!");
                // should send a bounce.
                failedUsers.add(username);
                continue;
            }

            Node person = people.getNode(0);
            NodeList mailboxes = person.getRelatedNodes("mailboxes");
            for (int j=0; j < mailboxes.size(); j++) {
                Node mailbox = mailboxes.getNode(j);
                if (mailbox.getIntValue("type") == 0) {
                    log.debug("Found mailbox, adding mail now");
                    Node emailNode = emailManager.createNode();
                    emailNode.setValue("to", n.getValue("to"));
                    emailNode.setValue("from", n.getValue("from"));
                    emailNode.setValue("cc", n.getValue("cc"));
                    if (emailNode.getNodeManager().hasField("bcc")) {
                        emailNode.setValue("bcc", n.getValue("bcc"));
                    }
                    emailNode.setValue("subject", n.getValue("subject"));
                    emailNode.setValue("date", n.getValue("date"));
                    emailNode.setValue("body", n.getValue("body"));
                    if (emailNode.getNodeManager().hasField("mimetype")) {
                        emailNode.setValue("mimetype", n.getValue("mimetype"));
                    }
                    emailNode.setIntValue("type", 2);
                    emailNode.commit();
                    log.debug("Appending " + emailNode + " to " + mailbox.getNumber());
                    mailbox.createRelation(emailNode, relatedManager).commit();


                    NodeList attachments = n.getRelatedNodes("attachments");
                    for (int k = 0; k < attachments.size(); k++) {
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
                String mailadres = person.getStringValue("email").trim();
                if (! "".equals(mailadres)) {
                    if (log.isDebugEnabled()) {
                        log.debug("This user has email forwarding enabled. forwarding email to [" + mailadres + "]");
                    }
                    if (! "true".equals(getInitParameter("noforwarding"))) {
                        try {
                            /// should use Sender header here (in case of boucnes).
                            // and perhaps als Resent-From header.
                            Address localAddress = recipient;
                            sendRemoteMail(InternetAddress.parse(mailadres), localAddress, n);
                        } catch (Exception e) {
                            // MM: I think all exceptions are catched in sendRemoteMail itself already. So I
                            // doubt it'll ever come here.
                            log.warn("Exception when trying to forward email to [" + mailadres + "]: " + e.getMessage());
                        }
                    } else {
                        log.service("Forwarding disabled, not sending mail to " + mailadres);
                    }
                } else {
                    log.debug("This user has email-forwaring enabled, but did not set an external email-adress");
                }
            }
        }
        if (failedUsers.size() > 0) {
            String subject = n.getStringValue("subject");
            if (! subject.startsWith("Failed:")) { // should not happen, but _if_ if happens, it
                                                   // would otherwise become horribly recursive.
                log.debug("Unknown local users, send a notification about that back");
                try {
                    // sending information to sender
                    Node error = emailManager.createNode();
                    error.setValue("to", n.getValue("from"));
                    error.setValue("from", n.getValue("from"));
                    error.setValue("subject", "Failed: '" + subject + "'");
                    error.setValue("body", "Could not send mail to " + failedUsers + " (no such users)");
                    if (emailManager.hasField("mimetype")) {
                        error.setValue("mimetype", "text/plain");
                    }
                    error.setIntValue("type", 1);
                    error.commit();
                    log.debug("Ready sending error mail about " + failedUsers);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        log.debug("Finished processing local mails");
    }

    /**
     * @scope protected (not used elsewhere, not in interface)
     */
    public void sendRemoteMail(InternetAddress[] onlyto, Node n) {
        sendRemoteMail(onlyto, null, n);
    }

    /**
     * @param sender If this is a forward, then you'd want to set the sender to the local address
     * (see rfc 822 4.4.4
     */
    protected void sendRemoteMail(InternetAddress[] onlyto, Address sender,  Node n) {
        if (log.isDebugEnabled()) {
            log.debug("Sending node {" + n + "} to addresses {" + Arrays.asList(onlyto) + "}");
            log.trace("Because ", new Exception());
        }
        StringBuilder errors = new StringBuilder();
        String from    = n.getStringValue("from");
        String to      = n.getStringValue("to");
        String cc      = n.getStringValue("cc");
        String bcc     = n.getNodeManager().hasField("bcc") ? n.getStringValue("bcc") : null;
        String body    = n.getStringValue("body");
        String subject = n.getStringValue("subject");
        String mimeType = n.getNodeManager().hasField("mimetype") ? n.getStringValue("mimetype") : null;
        if (mimeType == null) mimeType = "text/html";

        try {

            if (log.isServiceEnabled()) {
                log.service("JMSendMail sending mail to " + to + " cc:" + cc + " bcc:" + bcc + " (node " + n.getNumber() + ")" + " from " + from + " (mime-type " + mimeType + ") using " + session);
            }
            // construct a message
            MimeMessage msg = new MimeMessage(session);
            // msg = super.constructMessage()....
            if (from != null && ! from.equals("")) {
                msg.setFrom(new InternetAddress(from));
            }
            if (sender != null) {
                msg.setSender(sender);
            }
            msg.setHeader("X-mmbase-node", n.getNodeManager().getName() + "/" + n.getNumber());
            try {
                InternetAddress[] toRecipients = InternetAddress.parse(to);
                msg.addRecipients(Message.RecipientType.TO, toRecipients);
            } catch (javax.mail.internet.AddressException ae) {
                log.warn(ae);
                errors.append("\nTo: " + to + ": " + ae.getMessage());
            }

            if (cc != null) {
                try {
                    InternetAddress[] ccRecipients = InternetAddress.parse(cc);
                    msg.addRecipients(Message.RecipientType.CC, ccRecipients);
                } catch (javax.mail.internet.AddressException ae) {
                    log.warn(ae);
                    errors.append("\nCc: " + cc  + " " + ae.getMessage());
                }
            }

            if (bcc != null) {
                try {
                    InternetAddress[] bccRecipients = InternetAddress.parse(bcc);
                    msg.addRecipients(Message.RecipientType.BCC, bccRecipients);
                } catch (javax.mail.internet.AddressException ae) {
                    log.warn(ae);
                    errors.append("\nBcc: " + bcc + " " + ae.getMessage());
                }
            }

            msg.setSubject(subject, "UTF-8");


            /* add attachments here */
            NodeList attachments = n.getRelatedNodes("attachments");
            if (log.isDebugEnabled()) {
                log.debug("Found attachments " + attachments.size() + " on node " + n.getNumber() + " " + n.getCloud().getUser());
            }
            if (attachments.size() != 0) {
                String subType;
                if (mimeType.startsWith("multipart/")) {
                    subType = mimeType.substring(10);
                    log.debug("found subype " + subType + " from " + mimeType);
                } else {
                    log.debug("Related attachments, but mimeType was " + mimeType + " using multipart/mixed");
                    subType ="mixed";
                }

                MimeMultipart mmp = new MimeMultipart(subType);

                if (body != null && ! "".equals("body")  && ! mimeType.startsWith("multipart/")) {
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    if (mimeType.startsWith("text")) {
                        bodyPart.setContent(body, mimeType + "; charset=UTF-8");
                    } else {
                        bodyPart.setContent(body, mimeType);
                    }
                    mmp.addBodyPart(bodyPart);
                }

                log.debug("Adding attachments to " + mmp);
                for (int i = 0; i < attachments.size(); i++) {
                    Node attachment = attachments.getNode(i);
                    String filename = attachment.getStringValue("filename");
                    if (filename == null || filename.equals("")) {
                        filename = "attached file";
                    }

                    String attachmentMimeType = attachment.getStringValue("mimetype");
                    if (attachmentMimeType == null || attachmentMimeType.equals("")) {
                        attachmentMimeType = "application/octet-stream";
                    }


                    byte[] handle = attachment.getByteValue("handle");
                    MimeBodyPart mbp = new MimeBodyPart();


                    log.debug("Found a part " + attachmentMimeType);
                    // If our attached file is text/html, we will create a new 'normal'
                    // bodypart. The email client will then show the HTML inline.
                    // Note that for this to work, you need a valid doctype definition
                    // in your body.
                    mbp.setDisposition(subType.equals("alternative") || subType.equals("related") ? Part.INLINE : Part.ATTACHMENT);

                    String desc = attachment.getStringValue("description");
                    if (subType.equals("related") && ! desc.equals("")) {
                        mbp.setContentID(desc);
                    }

                    if (attachmentMimeType.startsWith("text")) {
                        if (!filename.equals("attached file")) {
                            mbp.setFileName(filename);
                        }

                        log.debug("creating mbp with mimeType " + attachmentMimeType);
                        mbp.setDataHandler(new DataHandler(new String(handle, "UTF-8"), attachmentMimeType));//new ByteArrayDataSource(handle, mimeType)));
                    } else {

                        // if no ByteArrayDataSource used, then you get: javax.activation.UnsupportedDataTypeException: no object DCH for MIME type image/gif
                        mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(handle, attachmentMimeType)));
                        mbp.setFileName(filename);
                    }
                    if (mbp.getContentID() == null) {
                        mbp.setContentID("mmbase/" + attachment.getNodeManager().getName() + "/" + attachment.getNumber());
                    }
                    mmp.addBodyPart(mbp);
                }
                msg.setContent(mmp);
            } else {
                log.debug("Using message with body " + mimeType);
                if (mimeType.startsWith("text")) {
                    //String subType = mimeType.substring(5);
                    //log.info("Using " + subType);
                    // msg.setText(body, subType, "UTF-8"); // java mail 1.4...
                    msg.setContent(body, mimeType + "; charset=UTF-8");
                } else {
                    msg.setContent(body, mimeType);
                }
            }

            // just to test if errors may follow
            /*
            try {
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
                msg.writeTo(bos);
            } catch (Exception e) {
                log.error("Exception: " + e.getMessage(), e);
                errors.append("\nIO: " + e.getMessage());
            }
            */

            Transport.send(msg, onlyto);
            log.debug("JMSendMail done.");
        } catch (javax.mail.SendFailedException sfe) {
            log.warn("JMSendMail failure: " + sfe.getMessage());
        } catch (javax.mail.MessagingException e) {
            log.error("JMSendMail failure: " + e.getMessage(), e);
            errors.append("\nMessaging: " + e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                errors.append("\ncaused by: " + cause.getMessage());
                cause = e.getCause();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            errors.append(e.getClass() + ": " + e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                errors.append("\ncaused by: " + cause.getMessage());
                cause = e.getCause();
            }
        }
        if (errors.length() > 0 && ! n.getStringValue("to").equals(n.getStringValue("from"))) {
            log.service("Sending error mail to " + n.getStringValue("from") + " " + n.getNodeManager() + " " + errors);
            // if errors, and this is certainly not an error mail itself....
            try {
                Node errorNode = n.getNodeManager().createNode();
                errorNode.setStringValue("to", n.getStringValue("from"));
                errorNode.setStringValue("from", n.getStringValue("from"));
                errorNode.setStringValue("subject", "**** -> " +  n.getStringValue("to") + " (" + n.getStringValue("subject") + ")");
                errorNode.setIntValue("type", 1);
                if (n.getNodeManager().hasField("mimetype")) {
                    errorNode.setValue("mimetype", "text/plain");
                }
                errorNode.setStringValue("body", errors.toString());
                errorNode.commit();
                log.service("Sent node " + errorNode.getNumber());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public String getModuleInfo() {
        return "Sends mail through J2EE/JavaMail";
    }


}
