package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * <p>
 * Listener thread, that accepts connection on port 25 (default) and
 * delegates all work to its worker threads. It is a minimum implementation,
 * it only implements commands listed in section 4.5.1 of RFC 2821.
 *</p>
 * <h1>How are multiparts dispatched to mmbase objects?</h2>
 * <p> 
 * If the mimetype of the message itself is not multipart, then the message can simply be stored in
 * a object of the type 'emails'. The mime-type of the mail can be sotred in the mime-type field of
 * the message.
 * </p>
 * <p>
 * If the mimetype of the orignal message is multipart/alternative, then only the 'best' part is
 * stored in the mmbase email node. Currently only text/plain and text/html alternatives are
 * recognized. text/html is supposed to be better.
 * </p>
 * <p>
 * If the mimetype of the orignal message is multipart/mixed, then the INLINE part can be stored
 * in the object. If no disposition given on a part, then it is considered INLINE if text/*. If the
 * disposition if ATTACHMENT, then those are stored as related attachment-objects.
 * </p>
 * TODO: What happens which attached mail-messages? Will those not cause a big mess?
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @version $Id: SMTPHandler.java,v 1.1 2007-08-06 12:04:50 michiel Exp $
 */
public class SMTPHandler extends MailHandler implements Runnable {
    private static final Logger log = Logging.getLoggerInstance(SMTPHandler.class);

    private boolean running = true;
    private final java.net.Socket socket;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

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

    private Map<String, String> properties;
    /**
     * Public constructor. Set all data that is needed for this thread to run.
     */
    public SMTPHandler(java.net.Socket socket, Map<String, String> properties) {
        super();
        this.socket = socket;
        this.properties = properties;
    }
    protected Map<String, String> getProperties() {
        return properties;
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
            reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            // acctualy, we should parse it as a stream, because e.g. also the constructor of MimeMessage want an inputstream.
            // but, we simply encode and uncode ISO-8859-1, trusting that that doesn't matter for the SMTP-protocol and that
            // the content is invariant in this process.

            writer = new BufferedWriter(new OutputStreamWriter(os, "US-ASCII"));
        } catch (UnsupportedEncodingException uee) {
            // should not happen. iso-8859-1 and us-ascii _are_ supported.
            log.fatal(uee);
            return;
        } catch (IOException e) {
            log.error("Exception while initializing inputstream to incoming SMTP connection: " + e.getMessage(), e);
            return;
        }

        try {
            writer.write("220 " + properties.get("hostname") + " Service ready\r\n"); // SMTP uses Windows-like newline conventions
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
        if (log.isDebugEnabled()) {
            log.debug("SMTP INCOMING: " + line);
        }

        String uLine = line.toUpperCase();

        if (uLine.startsWith("QUIT")) {
            state = STATE_FINISHED;
            writer.write("221 Goodbye.\r\n");
            writer.flush();
            return;
        }

        if (uLine.startsWith("RSET")) {
            state = STATE_MAILFROM;
            mailboxes.clear();
            writer.write("250 Spontanious amnesia has struck me, I forgot everything!\r\n");
            writer.flush();
            return;
        }

        if (uLine.startsWith("HELO")) {
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

        if (uLine.startsWith("MAIL FROM:")){
            if (state < STATE_MAILFROM) {
                writer.write("503 That's not nice! Polite people say HELO first\r\n");
                writer.flush();
            } else if (state > STATE_MAILFROM) {
                writer.write("503 You cannot specify MAIL FROM after a RCPT TO\r\n");
                writer.flush();
            } else {
                String address = line.substring(9, line.length());
                String sender[] = parseAddress(address);

                writer.write("250 That address looks okay, I'll allow you to send mail.\r\n");
                writer.flush();
                state = STATE_RCPTTO;
            }
            return;
        }

        if (uLine.startsWith("RCPT TO:")) {
            log.debug(line);
            if (state < STATE_RCPTTO) {
                writer.write("503 You should say MAIL FROM first\r\n");
                writer.flush();
            } else if (state >= STATE_DATA) {
                writer.write("503 You cannot use RCPT TO: at this state\r\n");
                writer.flush();
            } else {
                String address = line.substring(7, line.length());
                String recepient[] = parseAddress(address);
                if (recepient.length != 2) {
                    log.service("Can't parse address " + address);
                    writer.write("553 This user format is unknown here\r\n");// 
                    writer.flush();
                    return;
                }
                String username = recepient[0];
                String domain = recepient[1];
                String domains = properties.get("domains");
                log.service("Incoming mail for " + username + " @ "+domain);
                for (StringTokenizer st = new StringTokenizer(domains, ","); st.hasMoreTokens();) {
                    if (domain.toLowerCase().endsWith(st.nextToken().toLowerCase())) {
                        if (! addMailbox(username)) {
                            log.service("Mail for " + username + " rejected: no mailbox");
                            writer.write("550 User not found: " + username + "\r\n");
                            writer.flush();
                            return;
                        }
                        log.service("Mail for " + username + " accepted");
                        writer.write("250 Yeah, that user lives here. Bring on the data!\r\n");
                        writer.flush();
                        return;
                    } 
                }
                log.service("Mail for domain " + domain + " not accepted, not one of " + domains);
                writer.write("553 We do not accept mail for domain '" + domain + "'\r\n");
                writer.flush();
            }
            return;
        }

        if (uLine.startsWith("DATA")) {
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
                StringBuilder data = new StringBuilder();
                boolean isreading = true;
                while (isreading) {
                    while ((c = reader.read()) == -1) {
                        try {
                            // why's this?
                            Thread.currentThread().sleep(50);
                        } catch (InterruptedException e) {}
                    }
                    data.append((char)c);

                    for (int i = 0; i < last5chars.length - 1; i++) {
                        last5chars[i] = last5chars[i + 1];
                    }
                    last5chars[last5chars.length - 1] = (char)c;

                    isreading = false;
                    for (int i = 0; i < last5chars.length; i++) {
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
        log.info("Interrupt called");
    }

    /**
     * Parse a string of addresses, which are given in an RCPT TO: or MAIL FROM:
     * line by the client. This is a strict RFC implementation.
     * @todo Is this really ok? Why not {@link javax.mail.InternetAddress#parse}?
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
        if (atsign < 0) {
            return new String[0];
        }

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
        if (log.isTraceEnabled()) {
            log.trace("Data: [" + data + "]");
        }
        MimeMessage message = null;
        try {
            message = new MimeMessage(null, new ByteArrayInputStream(data.getBytes("ISO-8859-1")));
            /// which encoding?!
        } catch (UnsupportedEncodingException uee) {
            // should not happen. iso-8859-1  _is_ supported.
            log.fatal(uee);
            return false;
        } catch (MessagingException e) {
            log.error("Cannot parse message data: [" + data + "]");
            return false;
        }
        return handleMessage(message);
    }


}
