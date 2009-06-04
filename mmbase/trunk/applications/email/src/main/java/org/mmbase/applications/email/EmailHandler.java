/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.mmbase.bridge.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is a helper class for EmailBuilder. It contains a lot of methods which deal with
 * Node of type 'email' (So actually one would expect those functions to be member of
 * EmailBuilder itself).
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @author Simon Groenewolt
 * @version $Id$
 * @since  MMBase-1.7
 */
public class EmailHandler {

    private static final Logger log = Logging.getLoggerInstance(EmailHandler.class);

    /**
     * Send the email node.
     * firsts finds all out all the related users and groups (or not)
     * then parse the content for subject and body
     * lastly mail it using the sendmail module
     */
    public static Node sendMailNode(final Node node, Object... messageFormatArguments) throws MessagingException {
        // get the sendmail module
        SendMail sendmail = EmailBuilder.getSendMail();
        if (sendmail == null) {
            log.error("sendmail module not active, cannot send email");
            //node.commit(); // why is the node committed here?
            return node; // STATE_FAILED ?
        }

        String from = node.getStringValue("from");
        Set<NodeRecipient>   toGroup = getAttachedGroups(node);

        // get Body of the mail (including url based)
        String body = node.getStringValue("body");

        Map<String, String> headers = getHeaders(node);

        if (toGroup.size() > 0) {
            // bulk-mailing
            Set<NodeRecipient> toUsers = new LinkedHashSet<NodeRecipient>(getTo(node));
            toUsers.addAll(toGroup);
            boolean success = true;
            // loop all the users we need to mail
            for (NodeRecipient to : toUsers) {
                if (! sendMail(node, from, to, body, headers, messageFormatArguments)) {
                    success = false;
                }

                // make sure that CC and BCC are only on first mail, otherwise those poor people get a lot of mail.
                headers.put("CC", null);
                headers.put("BCC", null);
            }
        } else {
            // one simple mail
            NodeRecipient to = new NodeRecipient(-1, node.getStringValue("to"));
            sendMail(node, from, to, body, headers, messageFormatArguments);
        }
        // set the new mailedtime, that can be used by admins
        // to see when it was mailed vs the requested mail
        // time
        node.setValue("mailedtime", (int)(System.currentTimeMillis()/1000));

        // commit the changes to the cloud
        if (! node.isNew()) {
            node.commit();
        }
        return node;
    }

    protected static String applyMessageFormat(String pattern, Object... messageFormatArguments) {
        if (messageFormatArguments == null) {
            return null;
        } else {
            return java.text.MessageFormat.format(pattern, messageFormatArguments);
        }
    }

    /**
     * Reads some fields from the given node and returns it as a Map with mail-headers.
     * The considered fields are replyto, cc, bcc and subject.
     */
    private static Map<String, String> getHeaders(Node node) {
        Map<String, String> headers = new HashMap<String, String>();

        NodeManager email = node.getNodeManager();
        // headers.put("From", node.getStringValue("from"));
        if (email.hasField("replyto")) {
            headers.put("Reply-To", unemptyString(node.getStringValue("replyto")));
        }
        if (email.hasField("cc")) {
            headers.put("CC",       unemptyString(node.getStringValue("cc")));
        }
        if (email.hasField("bcc")) {
            headers.put("BCC",      unemptyString(node.getStringValue("bcc")));
        }
        // subject field is obligatory
        headers.put("Subject",      unemptyString(node.getStringValue("subject")));

        headers.put("X-mmbase-node", node.getNodeManager().getName() + "/" + node.getNumber());
        return headers;
    }

    /**
     * Utility function.
     * @return null if empty string, string otherwise
     */

    private static String unemptyString(String string) {
        return "".equals(string) ? null : string;
    }


    /**
     * get the To header if its not set directly
     * try to obtain it from related objects.
     */
    private static Set<NodeRecipient> getTo(Node node, Object... messageFormatArguments) {
        Set<NodeRecipient> toUsers = new LinkedHashSet<NodeRecipient>();
        String to = node.getStringValue("to");
        if (to != null && !to.equals("")) {
            if (messageFormatArguments != null) {
                to = applyMessageFormat(to, messageFormatArguments);
            }
            toUsers.add(new NodeRecipient(-1, to));
        }
        return toUsers;
    }



    /**
     * Get the email addresses of related users, which are related to related groups.
     */
    private static Set<NodeRecipient> getAttachedGroups(Node node) {
        Set<NodeRecipient> toUsers = new LinkedHashSet<NodeRecipient>();
        if (node.getCloud().hasNodeManager(EmailBuilder.groupsBuilder)) { // never mind if groups builders does not exist
            NodeIterator rels = node.getRelatedNodes(EmailBuilder.groupsBuilder).nodeIterator();
            while(rels.hasNext()) {
                Node pnode = rels.nextNode();
                toUsers.addAll(getAttachedUsers(pnode));
            }
        }

        return toUsers;

    }

    /**
     * Get the email addresses of related users;
     */
    private static Set<NodeRecipient> getAttachedUsers(Node node) {
        Set<NodeRecipient> toUsers = new LinkedHashSet<NodeRecipient>();
        // try and find related users
        if (node.getCloud().hasNodeManager((EmailBuilder.usersBuilder))) { // never mind if users builders does not exist
            NodeIterator rels = node.getRelatedNodes(EmailBuilder.usersBuilder).nodeIterator();
            while (rels.hasNext()) {
                Node pnode = rels.nextNode();
                toUsers.add(new NodeRecipient(pnode.getNumber(), pnode.getStringValue(EmailBuilder.usersEmailField)));
            }
        }
        return toUsers;
    }



    private static String getUrlExtern(String absoluteUrl, String params, String usernumber) {
        try {
            if (usernumber != null) {
                params += "&usernumber=" + usernumber;
            }
            String prefix = "?";
            if (absoluteUrl.indexOf("?") != -1) {
                prefix = "&";
            }
            URL includeURL = new URL(absoluteUrl + prefix + params);
            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            String contentType = connection.getContentType();

            // Default encoding for http transport is iso-8859-1
            String encoding = "ISO-8859-1";

            // If a Content-Type header is present: get the encoding from there
            if (contentType != null) {
                String e = org.mmbase.util.GenericResponseWrapper.getEncoding(contentType);
                if (e != null) {
                    encoding = e;
                }
            }
            BufferedReader in = new BufferedReader(new InputStreamReader (connection.getInputStream(), encoding));
            int buffersize = 10240;
            char[] buffer = new char[buffersize];
            StringBuilder string = new StringBuilder();
            int len;
            while ((len = in.read(buffer, 0, buffersize)) != -1) {
                string.append(buffer, 0, len);
            }
            String result = string.toString();
            return result;

        } catch(Exception e) {
            log.warn(e.getMessage());
            return "";
        }

    }

    private static String stripToOneLine(String input) {
        StringBuilder result = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(input,",\n\r");
        while (tok.hasMoreTokens()) {
            result.append(tok.nextToken());
        }
        return result.toString();
    }


    /**
     * Sends one email. The body is a bit parsed. It can be an URL, in which case the result will be
     * fetched, and the body replaced.  It can also contain with &lt;DONTMAIL&gt; (why not at least
     * start??) in which case nothing happens. It can contain &lt;multipart (why not at least
     * starts) in which case the body will be considered a representation of a 'multipart' message
     * (in a kind of XML format).
     *
     * @return whether successful
     */

    private static boolean sendMail(Node node, String from, NodeRecipient to,  String body, Map<String, String> headers, Object[] messageFormatArguments) throws MessagingException {
        // if the body starts with a url call that url
        if (body.startsWith("http://")) {
            body = getUrlExtern(body, "", "" + to.nodeNumber);
        }

        {
            String subject = headers.get("Subject");
            // if the subject starts with a url call that url
            if (subject != null && subject.startsWith("http://")) {
                subject = getUrlExtern(subject, "" , "" + to.nodeNumber);
                subject = stripToOneLine(subject);
                headers.put("Subject", subject);
            }
        }


        // little trick if it seems valid html page set

        // Follow horrible hacks

        // the headers for html mail This is stupid.
        if (body.indexOf("<HTML>") != -1 && body.indexOf("</HTML>")!=-1) {
            headers.put("Mime-Version", "1.0");
            headers.put("Content-Type", "text/html; charset=\"ISO-8859-1\""); // oh no!
        }



        if (messageFormatArguments != null ) {
            body = applyMessageFormat(body, messageFormatArguments);
            from = applyMessageFormat(from, messageFormatArguments);
            String subj = headers.get("Subject");
            subj = applyMessageFormat(subj, messageFormatArguments);
            headers.put("Subject", subj);
        }


        boolean mailResult = false;
        MessagingException exception = null;
        try {
            // get mail text to see if we have a mime msg
            if (body.indexOf("<multipart") == -1) {
                mailResult =  EmailBuilder.getSendMail().sendMail(from, to.email, body, headers);
            } else {
                MimeMultipart mmpart = MimeMessageGenerator.getMimeMultipart(body, node);
                if (mmpart == null) throw new NullPointerException();
                mailResult =  EmailBuilder.getSendMail().sendMultiPartMail(from, to.email, headers, mmpart);
            }
        } catch (MessagingException me) {
            exception = me;

        }
        if (! mailResult) {
            log.warn("Mail to " + to.email + " failed", exception);
            if (node.getNodeManager().hasField("mailstatus")) {
                node.setValue("mailstatus", EmailBuilder.STATE_FAILED);
            }
            // add one to the sendmail counter
            // refix numberofmailsend++;
        } else {
            // add one to the sendmail counter
            // refix numberofmailsend++;
            log.debug("Email -> mail send");
            if (node.getNodeManager().hasField("mailstatus")) {
                node.setValue("mailstatus", EmailBuilder.STATE_DELIVERED);
            }
        }
        if (exception != null) throw exception;

        return true;

    }
    /**
     * Simple structure representing an email-adres which is associated with a node-number.
     */

    static class NodeRecipient {
        private final int nodeNumber;
        private final String email;
        NodeRecipient(int i, String s) {
            nodeNumber = i;
            email = s;
        }
        public boolean equals(Object o) {
            if (o instanceof NodeRecipient) {
                NodeRecipient other = (NodeRecipient) o;
                return other.nodeNumber == nodeNumber && other.email.equals(email);
            } else {
                return false;
            }
        }
        public int hashCode() {
            return email.hashCode() + nodeNumber;
        }
        public String toString() {
            return email;
        }
    }

}

