package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.io.*;
import javax.mail.*;
import nl.didactor.mail.*;

/**
 * Listener thread, that accepts connection on port 25 (default) and
 * delegates all work to its worker threads. It is a minimum implementation,
 * it only implements commands listed in section 4.5.1 of RFC 2821.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class SMTPHandler extends Thread {
    private static final Logger log = Logging.getLoggerInstance(SMTPHandler.class);
    private boolean running = true;
    private final java.net.Socket socket;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private final Cloud cloud;
    private final Map properties;

    /** State indicating we sent our '220' initial session instantiation message, and are now waiting for a HELO */
    private final int STATE_HELO = 1;

    /** State indicating we received a HELO and we are now waiting for a 'MAIL FROM:' */
    private final int STATE_MAILFROM = 2;

    /** State indicating we received a MAIL FROM and we are now waiting for a 'RCPT TO:' */
    private final int STATE_RCPTTO = 3;

    /** State indicating we received a DATA and we are now processing the data */
    private final int STATE_DATA = 4;

    /** State indicating we received a QUIT, and that we may close the connection */
    private final int STATE_FINISHED = 5;

    /** The current state of this handler */
    private int state = 0;

    /** Vector containing Node objects for all mailboxes of the receipients */
    private final List mailboxes = new Vector(); // why is this synchronized

    /** Vector containing Node objects for all receipients (in some configurations this
     *  can equal the 'mailboxes' vector */
    private final List users = new Vector(); // why is this synchornized?

    /**
     * Public constructor. Set all data that is needed for this thread to run.
     */
    public SMTPHandler(java.net.Socket socket, Map properties, Cloud cloud) {
        this.socket = socket;
        this.properties = properties;
        this.cloud = cloud;
    }

    /**
     * The main run method of this thread. It will read data from the given
     * socket line by line, and it will call the parser for this data.
     */
    public void run() {
        // talk to the other party
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            writer = new BufferedWriter(new OutputStreamWriter(os));
        } catch (IOException e) {
            log.error("Exception while initializing inputstream to incoming SMTP connection: " + e);
            return;
        }

        try {
            writer.write("220 " + properties.get("hostname") + " Service ready\r\n");
            writer.flush();

            while (state < STATE_FINISHED) {
                String line = reader.readLine();
                parseLine(line);
            }
        } catch (IOException e) {
            log.warn("Caught IOException: " + e);
        }

        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            log.warn("Cannot cleanup my reader, writer or socket: " + e);
        }
    }

    /**
     * Parse input received from the client. This method has the following side-effects:
     * <ul>
     *  <li> It can alter the 'state' variable
     *  <li> It can read extra data from the 'reader'
     *  <li> It can write data to the 'writer'
     *  <li> It can add Nodes to the 'mailboxes' vector
     * </ul>
     */
    private void parseLine(String line) throws IOException {
        log.debug("SMTP INCOMING: " + line);
        if (line.toUpperCase().startsWith("QUIT")) {
            state = STATE_FINISHED;
            return;
        }

        if (line.toUpperCase().startsWith("RSET")) {
            state = STATE_MAILFROM;
            mailboxes.clear();
            users.clear();
            writer.write("250 Spontanious amnesia has struck me, I forgot everything!\r\n");
            writer.flush();
            return;
        }

        if (line.toUpperCase().startsWith("HELO")) {
            if (state > STATE_HELO) {
                writer.write("503 5.0.0 " + properties.get("hostname") + "Duplicate HELO/EHLO\r\n");
                writer.flush();
            } else {
                writer.write("250 " + properties.get("hostname") + " Good day [" + socket.getInetAddress().getHostAddress() + "], how are you today?\r\n");
                writer.flush();
                state = STATE_MAILFROM;
            }
            return;
        }

        if (line.toUpperCase().startsWith("MAIL FROM:")){
            if (state < STATE_MAILFROM) {
                writer.write("503 That's not nice! Polite people say HELO first\r\n");
                writer.flush();
            } else if (state > STATE_MAILFROM) {
                writer.write("503 You cannot specify MAIL FROM after a RCPT TO\r\n");
                writer.flush();
            } else {
                String address = line.substring(10, line.length());
                String sender[] = parseAddress(address);

                writer.write("250 That address looks okay, I'll allow you to send mail.\r\n");
                writer.flush();
                state = STATE_RCPTTO;
            }
            return;
        }

        if (line.toUpperCase().startsWith("RCPT TO:")) {
            log.debug("RCPT TO:");
            if (state < STATE_RCPTTO) {
                writer.write("503 You should say MAIL FROM first\r\n");
                writer.flush();
            } else if (state >= STATE_DATA) {
                writer.write("503 You cannot use RCPT TO: at this state\r\n");
                writer.flush();
            } else {
                String address = line.substring(8, line.length());
                String recepient[] = parseAddress(address);
                if (recepient.length != 2) {
                    log.info("Can't parse address "+address);
                    writer.write("553 This user format is unknown here\r\n");
                    writer.flush();
                    return;
                }
                String username = recepient[0];
                String domain = recepient[1];
                String domains = (String)properties.get("domains");
                log.info("Incoming mail for "+username+" @ "+domain);
                for (StringTokenizer st = new StringTokenizer(domains, ","); st.hasMoreTokens();) {
                    if (domain.toLowerCase().endsWith(st.nextToken().toLowerCase())) {
                        if (!addMailbox(username)) {
                            log.info("Mail for "+username+" rejected: no mailbox");
                            writer.write("550 User not found: " + username + "\r\n");
                            writer.flush();
                            return;
                        }
                        log.info("Mail for "+username+" accepted");
                        writer.write("250 Yeah, that user lives here. Bring on the data!\r\n");
                        writer.flush();
                        return;
                    }
                }
                log.info("Mail for domain "+domain+" not accepted");

                writer.write("553 We do not accept mail for domain '" + domain + "'\r\n");
                writer.flush();
            }
            return;
        }

        if (line.toUpperCase().startsWith("DATA")) {
            if (state < STATE_RCPTTO) {
                writer.write("503 You should issue an RCPT TO first\r\n");
                writer.flush();
            } else if (state != STATE_RCPTTO) {
                writer.write("503 Command not possible at this state\r\n");
                writer.flush();
            } else if (mailboxes.size() == 0) {
                writer.write("503 You should issue an RCPT TO first\r\n");
                writer.flush();
            } else {
                // start reading all the data, until the '.'
                writer.write("354 Enter mail, end with CRLF.CRLF\r\n");
                writer.flush();
                char[] endchars = {'\r', '\n', '.', '\r', '\n'};
                char[] last5chars = new char[endchars.length];
                int currentpos = 0;
                int c;
                StringBuffer data = new StringBuffer();
                boolean isreading = true;
                while (isreading) {
                    while ((c = reader.read()) == -1) {
                        try {
                            this.sleep(50);
                        } catch (InterruptedException e) {}
                    }
                    data.append((char)c);

                    for (int i=0; i<last5chars.length - 1; i++) {
                        last5chars[i] = last5chars[i + 1];
                    }
                    last5chars[last5chars.length - 1] = (char)c;

                    isreading = false;
                    for (int i=0; i<last5chars.length; i++) {
                        if (last5chars[i] != endchars[i]) {
                            isreading = true;
                            break;
                        }
                    }
                }

                // Copy everything but the '.\r\n' to the result
                String result = data.substring(0, data.length() - 3);
                if (handleData(result)) {
                    writer.write("250 Rejoice! We will deliver this email to the user.\r\n");
                    writer.flush();
                    state = STATE_MAILFROM;
                } else  {
                    writer.write("550 Message not accepted.\r\n");
                    writer.flush();
                }
            }
            return;
        }

        writer.write("503 Sorry, but I have no idea what you mean.\r\n");
        writer.flush();
    }

    /**
     * Interrupt method, is called only during shutdown
     */
    public void interrupt() {
        log.info("Interrupt() called");
    }

    /**
     * Parse a string of addresses, which are given in an RCPT TO: or MAIL FROM:
     * line by the client. This is a strict RFC implementation.
     * @return an array of strings, the first element contains the username, the second element is the domain
     */
    private String[] parseAddress(String address) {
        if (address == null)
            return new String[0];

        if (address.equals("<>"))
            return new String[0];

        int leftbracket = address.indexOf("<");
        int rightbracket = address.indexOf(">");
        int colon = address.indexOf(":");

        // If we have source routing, we must ignore everything before the colon
        if (colon > 0) {
            leftbracket = colon;
        }

        // if the left or right brackets are not supplied, we MAY bounce the message. We
        // however try to parse the address still

        if (leftbracket < 0) {
            leftbracket = 0;
        }
        if (rightbracket < 0) {
            rightbracket = address.length();
        }

        // Trim off any whitespace that may be left
        String finaladdress = address.substring(leftbracket + 1, rightbracket).trim();
        int atsign = finaladdress.indexOf("@");
        if (atsign < 0)
            return new String[0];

        String[] retval = new String[2];
        retval[0] = finaladdress.substring(0, atsign);
        retval[1] = finaladdress.substring(atsign + 1, finaladdress.length());
        return retval;
    }

    /**
     * Handle the data from the DATA command. This method does all the work: it creates
     * objects in mailboxes.
     */
    private boolean handleData(String data) {
        log.debug("Data: [" + data + "]");
        NodeManager emailbuilder = cloud.getNodeManager((String)properties.get("emailbuilder"));
        javax.mail.internet.MimeMessage message = null;
        try {
            message = new javax.mail.internet.MimeMessage(null, new ByteArrayInputStream(data.getBytes()));
        } catch (javax.mail.MessagingException e) {
            log.error("Cannot parse message data: [" + data + "]");
            return false;
        }
        int rnrn = data.indexOf("\r\n\r\n");
        String headers = "";
        String body = "";
        if (rnrn > 0) {
            headers = data.substring(0, rnrn);
            body = data.substring(rnrn + 4, data.length());
        } else {
            body = data;
        }

        for (int i=0; i<mailboxes.size(); i++) {

            Node mailbox = (Node)mailboxes.get(i);
            Node email = emailbuilder.createNode();
            if (properties.containsKey("emailbuilder.typefield")) {
                 email.setIntValue((String)properties.get("emailbuilder.typefield"), 2); // new unread mail
            }
            if (properties.containsKey("emailbuilder.headersfield")) {
                email.setStringValue((String)properties.get("emailbuilder.headersfield"), "" + headers);
            }
            if (properties.containsKey("emailbuilder.tofield")) {
                try {
                    String value = message.getHeader("To", ", ");
                    if (value == null) value = "";
                    email.setStringValue((String)properties.get("emailbuilder.tofield"), value);
                } catch (javax.mail.MessagingException e) {}
            }
            if (properties.containsKey("emailbuilder.ccfield")) {
                try {
                    String value = message.getHeader("CC", ", ");
                    if (value == null) value = "";
                    email.setStringValue((String)properties.get("emailbuilder.ccfield"), value);
                } catch (javax.mail.MessagingException e) {}
            }
            if (properties.containsKey("emailbuilder.fromfield")) {
                try {
                    String value = message.getHeader("From", ", ");
                    if (value == null) value = "";
                    email.setStringValue((String)properties.get("emailbuilder.fromfield"), value);
                } catch (javax.mail.MessagingException e) {}
            }
            if (properties.containsKey("emailbuilder.subjectfield")) {
                try {
                    String value = message.getSubject();
                    if (value == null) value = "(empty)";
                    email.setStringValue((String)properties.get("emailbuilder.subjectfield"), value);
                } catch (javax.mail.MessagingException e) {}
            }
            if (properties.containsKey("emailbuilder.datefield")) {
                try {
                    Date d = message.getSentDate();
                    if (d == null) {
                        d = new Date();
                    }
                    email.setIntValue((String)properties.get("emailbuilder.datefield"), (int)(d.getTime() / 1000));
                } catch (javax.mail.MessagingException e) {}
            }
            try {
                if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {
                    if (message.getContent() != null) {
                        email.setStringValue((String)properties.get("emailbuilder.bodyfield"), "" + message.getContent());
                    }
                    email.commit();
                } else {
                    // now parse the attachments
                    try {
                        List attachmentsVector = extractPart(message, new ArrayList(), email);
                        email.commit();

                        for (Iterator it = attachmentsVector.iterator(); it.hasNext();) {
                            Node attachment = (Node) it.next();
                            Relation rel = email.createRelation(attachment, cloud.getRelationManager("related"));
                            rel.commit();
                        }
                    } catch (Exception e) {
                        log.error("Exception while parsing attachments: " + e);
                    }
                }
            } catch (Exception e) {
                email.setStringValue((String)properties.get("emailbuilder.bodyfield"), "" + body);
                email.commit();
            }
            Relation rel = mailbox.createRelation(email, cloud.getRelationManager("related"));
            rel.commit();

            //TODO: send to this user if he wants to
            Node user = (Node)users.get(i);
            if (user.getBooleanValue("email-mayforward")) {
                String mailadres = user.getStringValue("email");
                ExtendedJMSendMail sendmail = (ExtendedJMSendMail)org.mmbase.module.Module.getModule("sendmail");
                sendmail.startModule();
                sendmail.sendMail(mailadres, email);
            }
        }
        return true;
    }

    /**
     * Extract all attachments from a Part of a MultiPart message.
     * @author Gerard van Enk
     * @param p Part object that is being dissected
     * @param attach Vector of parts that already extracted
     * @param mail Mail Node that describes the mail that is being dissected
     * @return Vector of attachments that includes the currently extracted ones
     **/
    private List extractPart(final Part p, final List attachments, final Node mail) throws Exception {
        if (p.isMimeType("multipart/*")) {
            log.debug("Found attachments with type: multipart/*");
            Multipart mp = (Multipart)p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                extractPart(mp.getBodyPart(i), attachments, mail);
            }
        } else if (p.isMimeType("message/rfc822")) {
            log.debug("Found attachments with type: message/rfc822");
            extractPart((Part)p.getContent(), attachments, mail);
        } else if (p.isMimeType("text/plain")) {
            // only adding text/plain - text/html will be stored as attachment!
            log.debug("Found attachments with type: some text/plain tomething");
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
            if (content != null) {
                if (mail.getStringValue((String)properties.get("emailbuilder.bodyfield")) == null) {
                    mail.setStringValue((String)properties.get("emailbuilder.bodyfield"), (String)content);
                } else {
                    String tempPartString = "";
                    tempPartString = mail.getStringValue((String)properties.get("emailbuilder.bodyfield")) +"\r\n\r\n"+ (String)content;
                    mail.setStringValue((String)properties.get("emailbuilder.bodyfield"), tempPartString);
                }
                Node tempAttachment = storeAttachment(p);
                if (tempAttachment != null) {
                    attachments.add(tempAttachment);
                }
            } else {
                log.warn("Failed all attempts to decode the MultiPart");
            }
        } else {
            log.debug("Found attachment with type: " + p.getContentType());
            Node tempAttachment = storeAttachment(p);
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
    private Node storeAttachment(Part p) throws javax.mail.MessagingException {
        String fileName = p.getFileName();
        if (fileName == null || fileName.equals("")) {
            fileName="unknown";
        }

        NodeManager attachmentManager = cloud.getNodeManager("attachments");

        if (attachmentManager == null) {
            log.error("Attachments builder not activated");
            return null;
        }

        Node attachmentNode = attachmentManager.createNode();

        attachmentNode.setStringValue("title", "privatemail Attachment");
        attachmentNode.setStringValue("mimetype",p.getContentType());
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
    }


    /**
     * This method returns a Node to which the email should be related.
     * This node can be the user object represented by the given string parameter,
     * or it can be another object that is related to this user. This behaviour
     * is defined in the config file for this module.
     * @return whether or not this succeeded
     */
    private boolean addMailbox(String user) {
        String usersbuilder = (String)properties.get("usersbuilder");
        NodeManager manager = cloud.getNodeManager(usersbuilder);
        NodeList nodelist = manager.getList(properties.get("usersbuilder.accountfield") + " = '" + user + "'", null, null);
        if (nodelist.size() != 1) {
            return false;
        }
        Node usernode = nodelist.getNode(0);
        if (properties.containsKey("mailboxbuilder")) {
            String where = null;
            String mailboxbuilder = (String)properties.get("mailboxbuilder");
            if (properties.containsKey("mailboxbuilder.where")) {
                where = (String)properties.get("mailboxbuilder.where");
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
                mailboxes.add(cloud.getNode(number));
                users.add(usernode);
                return true;
            } else if (nodelist.size() == 0) {
                String notfoundaction = "bounce";
                if (properties.containsKey("mailboxbuilder.notfound")) {
                    notfoundaction = (String)properties.get("mailboxbuilder.notfound");
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
            mailboxes.add(usernode);
            users.add(usernode);
        }
        return false;
    }
}
