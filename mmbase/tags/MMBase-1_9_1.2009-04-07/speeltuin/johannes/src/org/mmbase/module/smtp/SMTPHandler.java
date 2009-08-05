package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;
import java.util.StringTokenizer;
import java.io.*;

/**
 * Listener thread, that accepts connection on port 25 (default) and 
 * delegates all work to its worker threads. It is a minimum implementation,
 * it only implements commands listed in section 4.5.1 of RFC 2821.
 */
public class SMTPHandler extends Thread {
    private Logger log = Logging.getLoggerInstance(SMTPHandler.class.getName());
    private boolean running = true;
    private java.net.Socket socket;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private Cloud cloud;
    private Hashtable properties;
    
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
    private Vector mailboxes = new Vector();

    /**
     * Public constructor. Set all data that is needed for this thread to run.
     */
    public SMTPHandler(java.net.Socket socket, Hashtable properties, Cloud cloud) {
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
        if (line.toUpperCase().startsWith("QUIT")) {
            state = STATE_FINISHED;
            return;
        }

        if (line.toUpperCase().startsWith("RSET")) {
            state = STATE_MAILFROM;
            writer.write("250 Spontanious amnesia has struck me, I forgot everything!\r\n");
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
                writer.write("503 That's not nice! Polite people say HELO first\n");
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
                    writer.write("553 This user format is unknown here\r\n");
                    writer.flush();
                    return;
                }
                String username = recepient[0];
                String domain = recepient[1];
                String domains = (String)properties.get("domains");
                for (StringTokenizer st = new StringTokenizer(domains, ","); st.hasMoreTokens();) {
                    if (domain.toLowerCase().endsWith(st.nextToken().toLowerCase())) {
                        Node mailbox = getMailbox(username);
                        if (mailbox == null) {
                            writer.write("550 User not found: " + username + "\r\n");
                            writer.flush();
                            return;
                        }
                        mailboxes.add(mailbox);
                        writer.write("250 Yeah, that user lives here. Bring on the data!\r\n");
                        writer.flush();
                        return;
                    }
                }

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
        if (colon > 0) 
            leftbracket = colon;

        // if the left or right brackets are not supplied, we MAY bounce the message. We
        // however try to parse the address still
        
        if (leftbracket < 0) 
            leftbracket = 0;
        if (rightbracket < 0) 
            rightbracket = address.length();

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
            email.setStringValue((String)properties.get("emailbuilder.bodyfield"), "" + body);
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
            email.commit();
            Relation rel = mailbox.createRelation(email, cloud.getRelationManager("related"));
            rel.commit();
        }
        return true;
    }
            
    /**
     * This method returns a Node to which the email should be related. 
     * This node can be the user object represented by the given string parameter,
     * or it can be another object that is related to this user. This behaviour
     * is defined in the config file for this module.
     * @return a node in case the user could be found, null in case the user does
     * not exist or has no valid mailbox related to him.
     */
    private Node getMailbox(String user) {
        String usersbuilder = (String)properties.get("usersbuilder");
        NodeManager manager = cloud.getNodeManager(usersbuilder);
        NodeList nodelist = manager.getList(properties.get("usersbuilder.accountfield") + " = '" + user + "'", null, null);
        if (nodelist.size() != 1) {
            return null;
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
                return cloud.getNode(number);
            } else if (nodelist.size() == 0) {
                String notfoundaction = "bounce";
                if (properties.containsKey("mailboxbuilder.notfound")) {
                    notfoundaction = (String)properties.get("mailboxbuilder.notfound");
                }
                if ("bounce".equals(notfoundaction)) {
                    return null;
                }
                /* this needs to be implemented
                if ("create".equals(notfoundaction)) {
                    
                }
                */
            } else {
                log.error("Too many mailboxes for user '" + user + "'");
                return null;
            }
        } else {
            return usernode;
        }
        return null;
    }
}
