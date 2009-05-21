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
import javax.mail.util.ByteArrayDataSource;
import javax.activation.*;
import java.util.regex.*;

import org.mmbase.module.smtp.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * Module providing mail functionality based on JavaMail, mail-resources.
 *
 * @author Case Roole
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since  MMBase-1.6
 * @version $Id$
 */
public class SendMail extends AbstractSendMail {
    private static final Logger log = Logging.getLoggerInstance(SendMail.class);

    public static final String DEFAULT_MAIL_ENCODING = "ISO-8859-1";

    public static String mailEncoding = DEFAULT_MAIL_ENCODING;

    public static long emailSent = 0;
    public static long emailFailed = 0;

    private static final Pattern MATCH_ALL = Pattern.compile(".*");

    private Pattern onlyToPattern = MATCH_ALL;

    private String typeField = "mailtype";

    public SendMail() {
        this(null);
    }
    public SendMail(String name) {
        super(name);
    }

    /**
     * Returns the domains that are te be consided 'local' domains.
     * If the SMTPModule is not active, then this retuns an empty set.
     * @since MMBase-1.9
     */
    protected Set<String> getDomains() {
        Set<String> domains = new HashSet<String>();
        //SMTPModule smtpModule = org.mmbase.module.Module.getModule(SMTPModule.class);
        SMTPModule smtpModule = (SMTPModule) org.mmbase.module.Module.getModule("smtp");
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
     * Delivers the mail represented by an MMBase node 'locally'. I.e. no actual mail is sent
     * (unless forwarded), but new objects and relations are created, to make this mail appear in
     * the mailbox of the recipients (which are represented by other MMBase nodes).
     *
     * The given 'body' of the node is _not_ parsed. MultiParts should be indicated by mmbase relations.
     *
     * @since MMBase-1.9
     */
    public void sendLocalMail(InternetAddress[] to, Node n) {
        if (log.isDebugEnabled()) {
            log.debug("sendLocalMail: Sending node {" + n + "} to addresses {" + Arrays.asList(to) + "}");
        }
        Cloud cloud = n.getCloud();
        NodeManager peopleManager = cloud.getNodeManager("people");
        getInitParameter("emailbuilder");
        NodeManager emailManager = cloud.getNodeManager("emails");
        NodeManager attachmentManager = cloud.getNodeManager("attachments");
        RelationManager relatedManager = cloud.getRelationManager("related");
        Set<String> failedUsers = new HashSet<String>();
        for (InternetAddress recipient : to) {
            log.debug("address " + recipient);
            String address = recipient.getAddress();
            int index = address.indexOf("@");
            String username = index > 0 ? address.substring(0, index) : address;

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
            for (int j = 0; j < mailboxes.size(); j++) {
                Node mailbox = mailboxes.getNode(j);
                if (mailbox.getIntValue(typeField) == EmailBuilder.TYPE_STATIC) {
                    log.debug("Found mailbox, adding mail now");
                    Node emailNode = emailManager.createNode();
                    emailNode.setValue("to", n.getValue("to"));
                    emailNode.setValue("from", n.getValue("from"));
                    emailNode.setValue("cc", n.getValue("cc"));
                    if (emailNode.getNodeManager().hasField("bcc")) {
                        emailNode.setValue("bcc", n.getValue("bcc"));
                    }
                    emailNode.setValue("subject", n.getValue("subject"));
                    if (emailNode.getNodeManager().hasField("date")) {
                        emailNode.setValue("date", n.getValue("date"));
                    }
                    emailNode.setValue("body", n.getValue("body"));
                    if (emailNode.getNodeManager().hasField("mimetype")) {
                        emailNode.setValue("mimetype", n.getValue("mimetype"));
                    }
                    emailNode.setIntValue(typeField, EmailBuilder.TYPE_RECEIVED);
                    emailNode.commit();
                    log.debug("Appending " + emailNode + " to " + mailbox.getNumber());
                    mailbox.createRelation(emailNode, relatedManager).commit();


                    // TODO attachments should not be copied! Or at least not always.
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
                        if (newAttachment.getNodeManager().hasField("date")) {
                            newAttachment.setValue("date", oldAttachment.getValue("date"));
                        }
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
                    error.setIntValue(typeField, EmailBuilder.TYPE_ONESHOT);
                    error.commit();
                    log.debug("Ready sending error mail about " + failedUsers);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        log.debug("Finished processing local mails");
    }

    private static final InternetAddress[] EMPTY = new InternetAddress[] {};
    /**
     * Like InternetAddress#parse but leaves out the addresses not matching 'onlyTo'.
     */
    protected InternetAddress[] parseOnly(String to) throws MessagingException {
        if (to != null && ! "".equals(to)) {
            List<InternetAddress> res = new ArrayList<InternetAddress>();
            InternetAddress[] parsed = InternetAddress.parse(to);
            for( InternetAddress a : parsed) {
                if (onlyToPattern.matcher(a.getAddress()).matches()) {
                    res.add(a);
                } else {
                    log.service("Skipping " + a + " because it does not match " + onlyToPattern);
                }

            }
            return res.toArray(EMPTY);
        } else {
            return EMPTY;
        }

    }


    /**
     * @return Always <code>true</code> Could in principle return <code>false</false> on
     * failure. But that would normally result an exception.
     */
    public boolean sendMultiPartMail(String from, String to, Map<String, String> headers, MimeMultipart mmpart) throws MessagingException {
        if (log.isServiceEnabled()) {
            log.service("Sending (multipart) mail from " + from + " to " + to);
            if (log.isDebugEnabled()) {
                log.debug("" + headers);
            }

        }
        InternetAddress[] onlyTo = parseOnly(to);
        if (onlyTo.length > 0)  {

            try {
                MimeMessage msg = constructMessage(from, onlyTo, headers);
                if (mmpart == null) throw new NullPointerException();
                msg.setContent(mmpart);

                Transport.send(msg);

                emailSent++;
                log.debug("JMimeSendMail done.");
                return true;
            } catch (javax.mail.MessagingException e) {
                emailFailed++;
                log.debug(e.getMessage(), e);
                throw e;
            }
        } else {
            log.service("Not sending mail to " + to + " because it does not match " + onlyToPattern);
            return true;
        }
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
        MMBase mmb = MMBase.getMMBase();
        mailEncoding = mmb.getEncoding();
        String encoding = getInitParameter("encoding");
        if (encoding != null && !encoding.equals("")) {
            mailEncoding = encoding;
        }


        String context = getInitParameter("context");
        if ("".equals(context)) context = null;
        String dataSource = getInitParameter("datasource");
        if ("".equals(dataSource)) dataSource = null;

        {
            String only = getInitParameter("onlyto");
            if (only != null && ! "".equals(only)) {
                onlyToPattern = Pattern.compile(only);
            }
        }

        {
            String i = getInitParameter("emailbuilder.typefield");
            if (i != null && ! "".equals(i)) {
                typeField = i;
            }
        }
        //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        session = null;
        if (dataSource != null) {
            try {

                if (context == null) {
                    context = "java:comp/env";
                    log.warn("The property 'context' is missing, taking default " + context);
                }

                Context initCtx = new InitialContext();
                Context envCtx = (Context)initCtx.lookup(context);
                Object o = envCtx.lookup(dataSource);
                if (o instanceof Session) {
                    session = (javax.mail.Session) o;
                } else {
                    log.error("Configured dataSource '" + dataSource + "' of context '" + context + "' is not a Session but " + (o == null ? "NULL" : "a " + o.getClass().getName()));
                    return;
                }
                log.info("Module SendMail started (datasource = " + dataSource + " -> " + session.getProperties() + ")");
            } catch (javax.naming.NamingException e) {
                log.error("SendMail failure: " + e.getMessage());
                log.debug(Logging.stackTrace(e));
            }
        }

        if (session == null) {
            String smtpHost = getInitParameter("mailhost");
            if (smtpHost != null && ! "".equals(smtpHost)) {
                if (context != null) {
                    log.error("It does not make sense to have both properties 'context' and 'mailhost' in email module");
                }
                if (dataSource != null) {
                    log.error("It does not make sense to have both properties 'datasource' and 'mailhost' in email module");
                }
                String smtpPort = getInitParameter("mailport");
                String userName  = getInitParameter("user");
                String password  = getInitParameter("password");

                log.service("EMail module is configured using 'mailhost' property. Consider using J2EE compliant 'context' and 'datasource' properties.");

                Properties prop = System.getProperties();
                prop.put("mail.transport.protocol", "smtp");
                prop.put("mail.smtp.starttls.enable","true");

                prop.put("mail.smtp.connectiontimeout", "10000");
                prop.put("mail.smtp.timeout", "10000");

                Map parameters = getInitParameters();
                for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String key = (String) entry.getKey();
                    if (key.startsWith("mail.")) {
                        prop.put(key, entry.getValue());
                    }
                }
                StringBuilder buf = new StringBuilder(smtpHost);
                prop.put("mail.smtp.host", smtpHost);

                if (smtpPort != null && smtpPort.trim().length() > 0) {
                    prop.put("mail.smtp.port", smtpPort);
                    buf.append(':').append(smtpPort);
                }
                // When username and password are specified, turn on smtp authentication.
                boolean smtpAuth = userName != null && userName.trim().length() != 0 && password != null;
                prop.setProperty("mail.smtp.auth", Boolean.toString(smtpAuth));
                if (smtpAuth) {
                    buf.insert(0, userName + "@");
                }

                session = Session.getInstance(prop, new SimpleAuthenticator(userName, password));
                Map<Object, Object> mailProps = new HashMap<Object, Object>();
                for (Map.Entry<Object, Object> e : prop.entrySet()) {
                    if (e.getKey().toString().startsWith("mail")) {
                        mailProps.put(e.getKey(), e.getValue());
                    }
                }
                log.info("Module SendMail started SMTP: " + buf + "(" + mailProps + ")");
            } else {
                log.fatal("Could not create Mail session");
            }
        }


    }

    protected static final Set<String> RECOGNIZED_HEADERS = new HashSet<String>(Arrays.asList(new String[] {"CC", "BCC", "Reply-To", "Subject", "Content-Type", "Mime-Version"}));

    /**
     * Utility method to do the generic job of creating a MimeMessage object and setting its recipients and 'from'.
     */
    protected final MimeMessage constructMessage(String from, InternetAddress[] to, Map<String, String> headers) throws MessagingException {
        // construct a message
        MimeMessage msg = new MimeMessage(session);
        if (from != null && !from.equals("")) {
            msg.addFrom(InternetAddress.parse(from));
        }

        msg.addRecipients(Message.RecipientType.TO, to);

        String cc = headers.get("CC");
        if (cc != null) {
            log.debug("Adding cc " + cc);
            msg.addRecipients(Message.RecipientType.CC, parseOnly(cc));
        }
        String bcc = headers.get("BCC");
        if (bcc != null) {
            log.debug("Adding bcc " + bcc);
            msg.addRecipients(Message.RecipientType.BCC, parseOnly(bcc));
        }

        String replyTo = headers.get("Reply-To");
        if (replyTo != null) {
            msg.setReplyTo(InternetAddress.parse(replyTo));
        }
        String sub = headers.get("Subject");
        if (sub == null || "".equals(sub)) sub = "<no subject>";
        msg.setSubject(sub);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (! RECOGNIZED_HEADERS.contains(header.getKey())) {
                String value = header.getValue();
                if (value != null) {
                    msg.addHeader(header.getKey(), header.getValue());
                } else {
                    log.warn("Got null " + header);
                }
            }
        }

        return msg;
    }

    /**
     * Send mail with headers, withouth using any explicit nodes.
     */
    public boolean sendMail(String from, String to, String data, Map<String, String> headers)  {
        if (log.isServiceEnabled()) {
            log.service("Sending mail to " + to + " Headers " + headers + " " + session);
        }
        try {

            InternetAddress[] onlyTo = parseOnly(to);
            if (onlyTo.length > 0) {
                MimeMessage msg = constructMessage(from, onlyTo, headers);
                msg.setText(data, mailEncoding);
                Transport.send(msg);
                log.debug("SendMail done.");
                return true;
            } else {
                log.service("not sending mail to " + to + " because it does not match " + onlyToPattern);
                return true;
            }
        } catch (MessagingException e) {
            log.error("SendMail failure: " + e.getClass() + " " + e.getMessage() + " from: " + from + " to: " + to + " " + (e.getCause() != null ? e.getCause() : ""));
            if (log.isDebugEnabled()) {
                log.debug("because: ", new Exception());
            }
        }
        return false;
    }




    /**
     *  @since MMBase-1.9
     */
    protected void sendRemoteMail(InternetAddress[] onlyto, Node n) {
        sendRemoteMail(onlyto, null, n);
    }

    /**
     * Sends an email which is represented by an MMBase node.
     *
     * The given 'body' of the node is _not_ parsed. MultiParts should be indicated by mmbase relations.
     *
     * @param sender If this is a forward, then you'd want to set the sender to the local address
     * (see rfc 822 4.4.4)
     *  @since MMBase-1.9
     */
    protected void sendRemoteMail(final InternetAddress[] onlyto, Address sender,  Node n) {
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
        if (mimeType == null || "".equals(mimeType)) mimeType = "text/html";

        try {

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
                msg.addRecipients(Message.RecipientType.TO, parseOnly(to));
            } catch (javax.mail.internet.AddressException ae) {
                log.warn(ae);
                errors.append("\nTo: " + to + ": " + ae.getMessage());
            }

            if (cc != null) {
                try {
                    msg.addRecipients(Message.RecipientType.CC, parseOnly(cc));
                } catch (javax.mail.internet.AddressException ae) {
                    log.warn(ae);
                    errors.append("\nCc: " + cc  + " " + ae.getMessage());
                }
            }

            if (bcc != null) {
                try {
                    msg.addRecipients(Message.RecipientType.BCC, parseOnly(bcc));
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
                    bodyPart.setContent(body, mimeType);
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
                    if (handle == null) handle = new byte[0];
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
            Address[] all = msg.getAllRecipients();
            if (all != null && all.length > 0) {
                if (log.isServiceEnabled()) {
                    log.service("JMSendMail sending mail to " + to + " cc:" + cc + " bcc:" + bcc + " (node " + n.getNumber() + ")" + " from " + from + " (mime-type " + mimeType + ") using " + session);
                }
                Transport.send(msg, onlyto);
            } else {
                log.debug("nothing to do");
            }

            log.debug("JMSendMail done.");
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
                errorNode.setStringValue("subject", "****");
                errorNode.setIntValue(typeField, EmailBuilder.TYPE_ONESHOT);
                errorNode.setStringValue("body", errors.toString());
                errorNode.commit();
                log.service("Sent node " + errorNode.getNumber());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    /**
     * Send mail with headers AND attachments
     *  @since MMBase-1.9
     */
    public boolean sendMail(Node n) {
        return sendMail(null, n);
    }

    protected boolean isLocal(InternetAddress recipient) {
        Set<String> domains = getDomains();
        String address = recipient.getAddress();
        int index = address.indexOf("@");
        String domain = index > 0 ? address.substring(index + 1).toLowerCase() : null;
        if (domain == null || domains.contains(domain)) {
            log.debug("Known domain [" + domain + "], processing internally");
            return true;
        } else {
            log.debug("Unknown domain [" + domain + "], processing externally");
            return false;
        }
    }

    /**
     * Send mail with headers AND attachments to the emailaddresses
     * specified in the 'to' and 'cc' fields. If these are null,
     * the values from the 'to' and 'cc' fields from the node
     * are used.
     *  @since MMBase-1.9
     */
    public boolean sendMail(String onlyto, Node n) {
        if (log.isDebugEnabled()) {
            log.debug("Start sendmail: " + onlyto + ", " + n);
        }

        List<InternetAddress> remoteRecipients = new ArrayList<InternetAddress>();
        List<InternetAddress>  localRecipients = new ArrayList<InternetAddress>();
        if (onlyto != null) {
            log.debug("Sending to " + onlyto);
            try {
                InternetAddress[] recipients = InternetAddress.parse(onlyto);
                for (InternetAddress recipient : recipients) {
                    if (isLocal(recipient)) {
                        localRecipients.add(recipient);
                    } else {
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            String to = n.getStringValue("to");
            try {
                log.debug("" + to);
                InternetAddress[] recipients = InternetAddress.parse(to);
                for (InternetAddress recipient : recipients) {
                    log.debug(recipient);
                    if (isLocal(recipient)) {
                        localRecipients.add(recipient);
                    } else {
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
                // whould we not send some notification about this?
            }

            String cc = n.getStringValue("cc");
            try {
                InternetAddress[] recipients = InternetAddress.parse(cc);
                for (InternetAddress recipient : recipients) {
                    if (isLocal(recipient)) {
                        localRecipients.add(recipient);
                    } else {
                        remoteRecipients.add(recipient);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
            String bcc     = n.getNodeManager().hasField("bcc") ? n.getStringValue("bcc") : null;
            if (bcc != null) {
                try {
                    InternetAddress[] recipients = InternetAddress.parse(bcc);
                    for (InternetAddress recipient : recipients) {
                        if (isLocal(recipient)) {
                            localRecipients.add(recipient);
                        } else {
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
            sendRemoteMail(remoteRecipients.toArray(new InternetAddress[]{}), n);
        }
        if (localRecipients.size() == 0 && remoteRecipients.size() == 0) {
            log.service("Not mailing " + n + " because no recipients");
        }

        return true;
    }

    public Session getSession() {
        return session;
    }

    public String getTypeField() {
        return typeField;
    }

    {
        addFunction(new AbstractFunction("verifyEmail", new Parameter[] { new Parameter("signature", String.class, true), Parameter.CLOUD }, ReturnType.NODE) {
                public Node getFunctionValue(Parameters parameters) {
                    Cloud cloud = (Cloud) parameters.get(Parameter.CLOUD);
                    String signature = parameters.getString("signature");
                    return org.mmbase.datatypes.VerifyEmailProcessor.validate(cloud, signature);
                }
            });
    }


}
