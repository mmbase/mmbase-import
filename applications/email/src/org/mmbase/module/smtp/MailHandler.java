package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.mmbase.applications.email.SendMail;

/**

 * @version $Id: MailHandler.java,v 1.5 2007-08-28 11:06:34 michiel Exp $
 */
public abstract class MailHandler {
    private static final Logger log = Logging.getLoggerInstance(MailHandler.class);


    private class MailBox {
        public final Node box;  // mailbox object (mails are related to this node) Can be the user
                                // node itself.
        public final Node user;
        MailBox(Node b, Node u) {
            box = b; user = u;
        }
    }

    protected Cloud getCloud() {
        return LocalContext.getCloudContext().getCloud("mmbase", "class", null);
    }

    protected Map<String, String> getProperties() {
        return org.mmbase.module.core.MMBase.getMMBase().getModule("sendmail").getInitParameters();
    }


    /**
     * List containing Node objects for all mailboxes of the receipients

     */
    protected final List<MailBox> mailboxes = new ArrayList<MailBox>();



    protected void nodeSetHeader(Node node, String fieldName, Address[] values) {
        Field field = node.getNodeManager().getField(fieldName);
        long maxLength = ((org.mmbase.datatypes.StringDataType) field.getDataType()).getMaxLength();
        StringBuilder buf = new StringBuilder();
        if (values != null) {
            log.debug("Using " + values.length + " values");
            for (int i = 0 ; i < values.length; i++) {
                Address value = values[i];
                if (buf.length() + value.toString().length() + 2 < maxLength) {
                    buf.append(value.toString());
                    if (i < values.length) {
                        buf.append(", ");
                    }
                } else {
                    log.warn("Could not store " + value + " for field '" + fieldName + "' of email node because field can maximully contain " + maxLength + " chars");
                }
            }
        }
        nodeSetHeader(node, fieldName, buf.toString());
    }
    protected void nodeSetHeader(Node node, String fieldName, String value) {
        Field field = node.getNodeManager().getField(fieldName);
        int maxLength = field.getMaxLength();
        log.trace("max length for " + fieldName + " is " + maxLength);
        if (value.length() >= maxLength) {
            log.warn("Truncating field " + fieldName + " for node " + node + " (" + value.length() + " > " + maxLength + ")");
            value = value.substring(0, maxLength - 1);
        } else {
            log.trace(value.length() + " < " + maxLength);
        }
        node.setStringValue(fieldName, value);
    }
    protected static String getMimeType(String contentType) {
        if (contentType == null) return null;
        int pos = contentType.indexOf(';');
        String mimeType;
        if (pos > 0) {
            mimeType = contentType.substring(0, pos);
        } else {
            mimeType = contentType;
        }
        return mimeType;
    }


    /**
     * @return 1 if mt1 bettern then mt2, -1 if mt2 is better, 0 if they are equal. -1 if unknown.
     */

    protected static int compareMimeTypes(String mt1, String mt2) {
        if (mt1.equals(mt2)) return 0;
        if (mt1.equals("text/html") && mt2.equals("text/plain")) {
            return 1;
        }
        if (mt2.equals("text/html") && mt1.equals("text/plain")) {
            return -1;
        }
        return -1;
    }


    protected boolean handleMessage(Message message) {
        Cloud cloud = getCloud();
        Map<String, String> properties = getProperties();
        NodeManager emailbuilder = cloud.getNodeManager(properties.get("emailbuilder"));


        for (MailBox mailbox : mailboxes) {
            log.debug("Delivering to mailbox node " + mailbox.box.getNumber());
            Node email = emailbuilder.createNode();
            if (properties.containsKey("emailbuilder.typefield")) {
                 email.setIntValue(properties.get("emailbuilder.typefield"), 2); // new unread mail
            }
            if (properties.containsKey("emailbuilder.headersfield")) {
                StringBuilder headers = new StringBuilder();
                try {
                    Enumeration e = message.getAllHeaders();
                    while (e.hasMoreElements()) {
                        Header header = (Header) e.nextElement();
                        headers.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
                    }
                    log.debug("Using headers " + headers);
                    nodeSetHeader(email, properties.get("emailbuilder.headersfield"), headers.toString());
                } catch (MessagingException me) {
                    log.warn(me);
                    nodeSetHeader(email, properties.get("emailbuilder.headersfield"), headers.toString());
                }
            }
            if (properties.containsKey("emailbuilder.tofield")) {
                try {
                    Address[] value = message.getRecipients(Message.RecipientType.TO);
                    nodeSetHeader(email, properties.get("emailbuilder.tofield"), value);
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (properties.containsKey("emailbuilder.ccfield")) {
                try {
                    Address[] value = message.getRecipients(Message.RecipientType.CC);
                    nodeSetHeader(email, properties.get("emailbuilder.ccfield"), value);
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (properties.containsKey("emailbuilder.bccfield")) {
                try {
                    Address[] value = message.getRecipients(Message.RecipientType.BCC);
                    nodeSetHeader(email, properties.get("emailbuilder.bccfield"), value);
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (properties.containsKey("emailbuilder.fromfield")) {
                try {
                    Address[] value = message.getFrom();
                    nodeSetHeader(email, properties.get("emailbuilder.fromfield"), value);
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (properties.containsKey("emailbuilder.subjectfield")) {
                try {
                    String value = message.getSubject();
                    if (value == null) value = "(empty)";
                    nodeSetHeader(email, properties.get("emailbuilder.subjectfield"), value);
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (properties.containsKey("emailbuilder.datefield")) {
                try {
                    Date d = message.getSentDate();
                    if (d == null) {
                        d = new Date();
                    }
                    email.setIntValue(properties.get("emailbuilder.datefield"), (int)(d.getTime() / 1000));
                } catch (MessagingException e) {
                    log.service(e);
                }
            }
            if (email.getNodeManager().hasField("mimetype")) {
                try {
                    String contentType = message.getContentType();
                    if (contentType != null) {
                        nodeSetHeader(email, "mimetype", getMimeType(contentType));
                    }
                } catch (MessagingException me) {
                    log.warn(me);
                }
            }
            try {
                if (! message.isMimeType("multipart/*")) {
                    if (log.isDebugEnabled()) {
                        log.debug("Non multipart mail, simply filling the body of the mail node with " + message);
                    }
                    if (message.getContent() != null) {
                        nodeSetHeader(email, properties.get("emailbuilder.bodyfield"), "" + message.getContent());
                    }
                    try {
                        email.commit();
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    // now parse the attachments
                    try {
                        log.debug("Extracting parts for message with mimetype " + message.getContentType());
                        List<Node> attachmentsVector = extractPart(message, new ArrayList<Node>(), email);
                        email.commit();
                        for (Node attachment : attachmentsVector) {
                            Relation rel = email.createRelation(attachment, cloud.getRelationManager("related"));
                            rel.commit();
                        }
                    } catch (Exception e) {
                        log.error("Exception while parsing attachments: " + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                try {
                    nodeSetHeader(email, properties.get("emailbuilder.bodyfield"), "" + message);
                    email.commit();
                } catch (Exception ee) {
                    log.error(ee);
                }
            }
            log.debug("Creating relation with mailbox " + mailbox);
            Relation rel = cloud.getNode(mailbox.box.getNumber()).createRelation(email, cloud.getRelationManager("related"));
            rel.commit();

            //TODO: send to this user if he wants to
            Node user = mailbox.user;
            if (user.getBooleanValue("email-mayforward")) {
                try {
                    String mailadres = user.getStringValue("email");
                    log.service("Forwarding " + email + " to " + mailadres);
                    SendMail sendmail = (SendMail)org.mmbase.module.Module.getModule("sendmail");
                    sendmail.startModule();
                    sendmail.sendMail(mailadres, email);
                } catch (Throwable e) {
                    log.warn("Exception in forward " + e.getMessage(), e);
                    return false;
                }
            }
        }
        return true;
    }


    protected String getContent(Part p) throws Exception {
        Object content = null;
        try {
            content = p.getContent();
        } catch (UnsupportedEncodingException e) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                p.writeTo(bos);
                content = bos.toString("ISO-8859-1");
            } catch (IOException e2) {}
        }
        if (content instanceof String) {
            return (String) content;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            p.writeTo(bos);
            return  bos.toString("ISO-8859-1");
        }
    }

    /**
     * Extract all attachments from a Part of a MultiPart message.
     * @author Gerard van Enk
     * @param p Part object that is being dissected
     * @param attachments List of parts that already extracted
     * @param mail Mail Node that describes the mail that is being dissected
     * @return The given attachments List, but wihth the currently extracted ones added.
     **/
    private List<Node> extractPart(final Part p, final List<Node> attachments, final Node mail) throws Exception {
        Map<String, String> properties = getProperties();
        String disposition = p.getDisposition();
        if (disposition == null) {
            // according to RFC 2183
            // Content-Disposition is an optional header field. In its absence, the
            // MUA may use whatever presentation method it deems suitable.

            // this, I deem suitable:

            disposition = p.isMimeType("text/*") || p.isMimeType("message/*")  ? Part.INLINE : Part.ATTACHMENT;
        }
        if (log.isDebugEnabled()) {
            log.debug("Extracting attachments from " + p + " (" + p.getContentType() + ", disposition " + p.getDisposition() + ") for node " + mail);
        }

        if (Part.INLINE.equals(disposition)) {
            // only adding text/plain - text/html will be stored as attachment!
            // should used somehow multipart/alternative

            // MM I think this goes wrong if the original mimeType was multipart/alternative
            // when forwarding:
            // 2006-12-28 10:32:38,758 ERROR nl.didactor.mail.ExtendedJMSendMail sendRemoteMail.373  - JMSendMail failure: MIME part of type "multipart/alternative" contains object of type java.lang.String instead of MimeMultipart
            // and in the web-interface it still shows 2 attachments, while it should show none.
            String mimeType = getMimeType(p.getContentType());
            String mailMimeType = mail.getStringValue("mimetype");



            log.debug("Found attachments with type: text/plain");
            String content = getContent(p);

            String bodyField = properties.get("emailbuilder.bodyfield");
            if (content != null) {
                int compareMimeType = compareMimeTypes(mimeType, mailMimeType);
                String currentBody = mail.getStringValue(bodyField);
                if (currentBody == null || "".equals(currentBody) || compareMimeType > 0) {
                    mail.setStringValue(bodyField, content);
                    mail.setStringValue("mimetype", mimeType);
                } else if (compareMimeType == 0) {
                    mail.setStringValue(bodyField, currentBody +"\r\n\r\n" + content);
                } else {
                    if (p.isMimeType("text/*")) {
                        log.debug("Ignoring part with mimeType " + mimeType + " (not better than already stored part with mimeType " + mailMimeType);
                    } else {
                        log.debug("Found a non-text alternative inline part, cannot handle that, treat it as an ordinary attachment");
                        Node tempAttachment = storeAttachment(p, mail.getCloud());
                        if (tempAttachment != null) {
                            attachments.add(tempAttachment);
                        }
                    }
                }
            } else {
                log.debug("Content of part is null, ignored");
            }
        } else if (p.isMimeType("multipart/*")) {

            Multipart mp = (Multipart)p.getContent();
            int count = mp.getCount();
            log.debug("Found attachment with type: " + p.getContentType() + " it has " + count + " parts, will add those recursively");
            for (int i = 0; i < count; i++) {
                extractPart(mp.getBodyPart(i), attachments, mail);
            }
        } else if (p.isMimeType("message/*")) {
            log.debug("Found attachment with type: " + p.getContentType());
            extractPart((Part) p.getContent(), attachments, mail);
        } else {
            log.debug("Found attachment with type: " + p.getContentType());
            Node tempAttachment = storeAttachment(p, mail.getCloud());
            if (tempAttachment != null) {
                attachments.add(tempAttachment);
            }
        }
        return attachments;
    }

    /**
     * Store an attachment (contained in a Part) in the MMBase object cloud.
     * @param p
     * @return Node in the MMBase object cloud
     */
    private Node storeAttachment(Part p, Cloud cloud) throws MessagingException {
        NodeManager attachmentManager = cloud.getNodeManager("attachments");
        try {
            String fileName = p.getFileName();

            if (attachmentManager == null) {
                log.error("Attachments builder not activated");
                return null;
            }

            Node attachmentNode = attachmentManager.createNode();


            if (p instanceof MimeBodyPart) {
                MimeBodyPart mbp = (MimeBodyPart) p;
                String contentId = mbp.getContentID();
                if (contentId != null) {
                    // a bit of misuse, of course.
                    // targeting at working of multipart/related messages.
                    attachmentNode.setStringValue("description", contentId);
                }
            }
            String mimeType = getMimeType(p.getContentType());

            String title = fileName;

            if (title == null || "".equals(title)) {
                title = "attachment " + mimeType;
            }
            attachmentNode.setStringValue("title", title);

            attachmentNode.setStringValue("mimetype", mimeType);
            attachmentNode.setStringValue("filename", fileName);
            attachmentNode.setIntValue("size", p.getSize());


            try {
                attachmentNode.setInputStreamValue("handle", p.getInputStream(), p.getSize());
            } catch (Exception ex) {
                log.error("Caught exception while trying to read attachment data: " + ex);
            }

            attachmentNode.commit();
            log.debug("committed attachment to MMBase");

            return attachmentNode;
        } catch (Throwable e) {
            log.service(e);
            try {
                Node attachmentNode = attachmentManager.createNode();
                String fileName = p.getFileName();
                attachmentNode.setStringValue("title", fileName + ": " + e.getMessage());

                attachmentNode.setStringValue("mimetype", "text/plain");
                attachmentNode.setStringValue("filename", "message.txt");
                attachmentNode.setStringValue("handle", Logging.stackTrace(e));
                attachmentNode.commit();
                return attachmentNode;
            } catch (Exception ew) {
                log.error(ew.getMessage(), ew);
                return null;
            }
        }
    }




    /**
     * This method returns a Node to which the email should be related.
     * This node can be the user object represented by the given string parameter,
     * or it can be another object that is related to this user. This behaviour
     * is defined in the config file for this module.
     * @return whether or not this succeeded
     */
    protected boolean addMailbox(String user) {
        Cloud cloud = getCloud();
        Map<String, String> properties = getProperties();
        String usersbuilder = properties.get("usersbuilder");
        NodeManager manager = cloud.getNodeManager(usersbuilder);
        NodeList nodelist = manager.getList(properties.get("usersbuilder.accountfield") + " = '" + user + "'", null, null);
        if (nodelist.size() != 1) {
            return false;
        }
        Node usernode = nodelist.getNode(0);
        if (properties.containsKey("mailboxbuilder")) {
            String where = null;
            String mailboxbuilder = properties.get("mailboxbuilder");
            if (properties.containsKey("mailboxbuilder.where")) {
                where = properties.get("mailboxbuilder.where");
            }
            nodelist = cloud.getList(
                "" + usernode.getNumber(),              //startnodes
                usersbuilder + "," + mailboxbuilder,    //path
                mailboxbuilder + ".number",             //fields
                where,                                  //constraints
                null,                                   //orderby
                null,                                   //directions
                null,                                   //searchdir
                true                                    //distinct
            );
            if (nodelist.size() == 1) {
                String number = nodelist.getNode(0).getStringValue(mailboxbuilder + ".number");
                mailboxes.add(new MailBox(cloud.getNode(number), usernode));
                return true;
            } else if (nodelist.size() == 0) {
                String notfoundaction = "bounce";
                if (properties.containsKey("mailboxbuilder.notfound")) {
                    notfoundaction = properties.get("mailboxbuilder.notfound");
                }
                if ("bounce".equals(notfoundaction)) {
                    return false;
                }
                /* this needs to be implemented
                if ("create".equals(notfoundaction)) {

                }
                */
            } else {
                log.error("Too many mailboxes for user '" + user + "'");
                return false;
            }
        } else {
            mailboxes.add(new MailBox(usernode, usernode));
        }
        return false;
    }
}
