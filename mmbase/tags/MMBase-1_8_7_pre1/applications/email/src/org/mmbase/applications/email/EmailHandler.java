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
 * @version $Id: EmailHandler.java,v 1.31 2008-08-08 08:05:48 michiel Exp $
 * @since  MMBase-1.7
 */
class EmailHandler {

    private static final Logger log = Logging.getLoggerInstance(EmailHandler.class);

    /**
     * Send the email node.
     * firsts finds all out all the related users and groups (or not)
     * then parse the content for subject and body
     * lastly mail it using the sendmail module
     */
    public static Node sendMailNode(final Node node) throws MessagingException {
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
                if (! sendMail(node, from, to, body, headers)) {
                    success = false;
                }

                // make sure that CC and BCC are only on first mail, otherwise those poor people get a lot of mail.
                headers.put("CC", null);
                headers.put("BCC", null);
            }
        } else {
            // one simple mail
            NodeRecipient to = new NodeRecipient(-1, node.getStringValue("to"));
            sendMail(node, from, to, body, headers);
        }
        // set the new mailedtime, that can be used by admins
        // to see when it was mailed vs the requested mail
        // time
        node.setValue("mailedtime", (int)(System.currentTimeMillis()/1000));

        // commit the changes to the cloud
        if (node.getNumber() > 0) {
            node.commit();
        }
        return node;
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
        // subject field is obligotary
        headers.put("Subject",  unemptyString(node.getStringValue("subject")));

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
    private static Set<NodeRecipient> getTo(Node node) {
        Set<NodeRecipient> toUsers = new LinkedHashSet<NodeRecipient>();
        String to = node.getStringValue("to");
        if (to != null && !to.equals("")) {
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

               StringTokenizer tokenizer = new StringTokenizer(contentType, "; ");
               while (tokenizer.hasMoreTokens()) {
                   String value = tokenizer.nextToken();
                   if (value.startsWith("charset=")) {
                       // charset overrides the defaults for the mimetypes
                       encoding = value.substring(8);
                   } else if (value.equals("text/xml")) {
                       // default encoding for text/xml
                       encoding = "utf-8";
                   }
                   // default encoding for text/html en text/plain is ISO-8859-1

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

    private static boolean sendMail(Node node, String from, NodeRecipient to,  String body, Map<String, String> headers) throws MessagingException {
        String obody = body;

        // WTF!

        // if the body starts with a url call that url
        if (obody.indexOf("http://") == 0) {
            body = getUrlExtern(obody, "", "" + to.nodeNumber);
            // convert html to plain text unless a the html tag is found
            if (body.indexOf("<html>") == -1 && body.indexOf("<HTML>") == -1) {
                //body=html2plain(body);
            }
        }

        String osubject = headers.get("Subject");

        // if the subject starts with a url call that url
        if (osubject != null && osubject.indexOf("http://") == 0) {
            String subject = getUrlExtern(osubject, "" , "" + to.nodeNumber);
            subject = stripToOneLine(subject);
            headers.put("Subject", subject);
        }


        // little trick if it seems valid html page set

        // Follow horrible hacks

        // the headers for html mail
        if (body.indexOf("<HTML>") != -1 && body.indexOf("</HTML>")!=-1) {
            headers.put("Mime-Version", "1.0");
            headers.put("Content-Type", "text/html; charset=\"ISO-8859-1\""); // oh no!
        }

        // is the don't mail tag set ? this allows
        // a generated body to signal it doesn't
        // want to be mailed since for some reason
        // invalid (for example there is no news for
            // you
        if (body.indexOf("<DONTMAIL>") == -1) {
            // if the subject contains 'fakemail'
            // perform all actions butt don't really
            // mail. This is done for testing
            String subject = headers.get("Subject");
            if (subject != null && subject.indexOf("fakemail")!=-1) {
                // add one to the sendmail counter
                // refix numberofmailsend++;
                log.info("Email -> fake send to " + to);
                return true;
            } else {

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
                    log.debug("Email -> mail failed");
                    node.setValue("mailstatus", EmailBuilder.STATE_FAILED);
                    // add one to the sendmail counter
                    // refix numberofmailsend++;
                } else {
                    // add one to the sendmail counter
                    // refix numberofmailsend++;
                    log.debug("Email -> mail send");
                    node.setValue("mailstatus", EmailBuilder.STATE_DELIVERED);
                }
                if (exception != null) throw exception;

                return true;
            }
        } else {
            log.debug("Don't mail tag found");
            return true;
        }
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

