/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
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
 * @version $Id: SMTPFetcher.java,v 1.12 2008-02-03 17:42:06 nklasens Exp $
 */
public class SMTPFetcher extends MailFetcher implements Runnable {
    private static final Logger log = Logging.getLoggerInstance(SMTPFetcher.class);

    private final java.net.Socket socket;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    enum State {
        IDLE,
        /**
         * We sent our '220' initial session instantiation message, and are now waiting for a HELO
         */
        HELO,
        /**
         * We received a HELO and we are now waiting for a 'MAIL FROM:'
         */
        MAILFROM,
        /**
         * We received a MAIL FROM and we are now waiting for a 'RCPT TO:'
         */
         RCPTTO,

        /**
         * We received a DATA and we are now processing the data
         */
         DATA,
         /** We received a QUIT, and that we may close the connection */
         FINISHED
     }

    private int delivered = 0;
    /** The current state of this handler */
    private State state = State.IDLE;
    private MailHandler.Address fromAddress = null;
    private List<MailHandler.Address> recipients = new ArrayList<MailHandler.Address>();

    public MailHandler.Address getSender() {
        return fromAddress;
    }
    public List<MailHandler.Address> getRecipients() {
        return recipients;
    }
    private Map<String, String> properties;

    private final MailHandler handler;
    /**
     * Public constructor. Set all data that is needed for this thread to run.
     */
    public SMTPFetcher(java.net.Socket socket, Map<String, String> properties) {
        super();
        this.socket = socket;
        this.properties = properties;
        handler = getHandler();
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

            while (state != State.FINISHED) {
                String line = reader.readLine();
                try {
                    parseLine(line);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    writer.write("550 Exception: " + e.getMessage());
                    writer.flush();
                    state = State.FINISHED;
                    break;
                }
            }
            if (delivered == 0) {
                writer.write("550: Nothing to do");
                writer.flush();
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
    public static int getMaxAttachmentSize(Map<String, String> properties) {
        // 76 length of a base 64 encoded line
        int maxAttachmentSize = (5 * 1024 * 1024 / 76) * 76; // approx 5 Mb.
        String maxSize = properties.get("max_attachment_size");
        if (maxSize != null && ! "".equals(maxSize)) {
            try {
                maxAttachmentSize = (Integer.parseInt(maxSize) / 76) * 76;
            } catch (Exception e) {
                log.error(e);
            }
        }
        return maxAttachmentSize;
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
        if (log.isTraceEnabled()) {
            log.trace("SMTP INCOMING: " + line);
        }

        String uLine = line.toUpperCase();

        if (uLine.startsWith("QUIT")) {
            log.debug("Sending 221 Goodbye");
            state = State.FINISHED;
            writer.write("221 Goodbye. (" + delivered + ")\r\n");
            writer.flush();
            return;
        }

        if (uLine.startsWith("RSET")) {
            state = State.MAILFROM;
            handler.clearMailboxes();
            writer.write("250 Spontanious amnesia has struck me, I forgot everything!\r\n");
            writer.flush();
            return;
        }

        if (uLine.startsWith("HELO")) {
            if (state.compareTo(State.HELO) > 0) {
                writer.write("503 5.0.0 " + properties.get("hostname") + "Duplicate HELO/EHLO\r\n");
                writer.flush();
            } else {
                writer.write("250 " + properties.get("hostname") + " Good day [" + socket.getInetAddress().getHostAddress() + "], how are you today?\r\n");
                writer.flush();
                state = State.MAILFROM;
            }
            return;
        }

        if (uLine.startsWith("MAIL FROM:")){
            if (state.compareTo(State.MAILFROM) < 0) {
                writer.write("503 That's not nice! Polite people say HELO first\r\n");
                writer.flush();
            } else if (state.compareTo(State.MAILFROM) > 0) {
                writer.write("503 You cannot specify MAIL FROM after a RCPT TO\r\n");
                writer.flush();
            } else {
                fromAddress = parseAddress(line.substring(9, line.length()));
                if (fromAddress != null) {
                    writer.write("250 That address looks okay, I'll allow you to send mail.\r\n");
                    writer.flush();
                    state = State.RCPTTO;
                } else {
                    writer.write("550 No acceptable MAIL FROM address.\r\n");
                    writer.flush();
                }
            }
            return;
        }

        if (uLine.startsWith("RCPT TO:")) {
            log.debug(line);
            if (state.compareTo(State.RCPTTO) < 0) {
                writer.write("503 You should say MAIL FROM first\r\n");
                writer.flush();
            } else if (state.compareTo(State.DATA) >= 0) {
                writer.write("503 You cannot use RCPT TO: at this state\r\n");
                writer.flush();
            } else {
                String address = line.substring(7, line.length());
                MailHandler.Address recipient = parseAddress(address);
                String domains = properties.get("domains");
                log.service("Incoming mail for " + recipient);

                for (StringTokenizer st = new StringTokenizer(domains, ","); st.hasMoreTokens();) {
                    if (recipient.domain.toLowerCase().endsWith(st.nextToken().toLowerCase())) {
                        log.debug("Will accept");
                        MailHandler.MailBoxStatus status = handler.addMailbox(recipient.user, recipient.domain);
                        if (status != MailHandler.MailBoxStatus.OK) {
                            log.service("Mail for " + recipient.user + " rejected: no mailbox");
                            writer.write("550 User: " + recipient.user + ": " + status + "\r\n");
                            writer.flush();
                            return;
                        }
                        log.debug("Mail for " + recipient.user + " accepted");
                        writer.write("250 Yeah, that user lives here. Bring on the data!\r\n");
                        writer.flush();
                        recipients.add(recipient);
                        return;
                    }
                }
                log.service("Mail for domain " + recipient.domain + " not accepted, not one of " + domains);
                writer.write("553 We do not accept mail for domain '" + recipient.domain + "'\r\n");
                writer.flush();
            }
            return;
        }

        if (uLine.startsWith("DATA")) {
            log.debug("data");
            if (state.compareTo(State.RCPTTO) < 0) {
                writer.write("503 You should issue an RCPT TO first\r\n");
                writer.flush();
            } else if (state != State.RCPTTO) {
                writer.write("503 Command not possible at this state\r\n");
                writer.flush();
            } else if (handler.size() == 0) {
                writer.write("503 You should issue an RCPT TO first\r\n");
                writer.flush();
            } else {
                state = State.DATA;
                // start reading all the data, until the '.'
                log.debug("354 Enter mail, end with CRLF.CRLF");
                writer.write("354 Enter mail, end with CRLF.CRLF\r\n");
                writer.flush();
                char[] endchars = {'\r', '\n', '.', '\r', '\n'};
                char[] last5chars = new char[endchars.length];
                int c;
                long maxAttachmentSize = getMaxAttachmentSize(properties);

                StringBuilder data = new StringBuilder();
                boolean isreading = true;
                boolean tooBig = false;
                while (isreading) {
                    while ((c = reader.read()) == -1) {
                        try {
                            // why's this?
                            Thread.currentThread().sleep(50);
                        } catch (InterruptedException e) {}
                    }
                    if (data.length() < maxAttachmentSize * 1.5) {
                        data.append((char)c);
                    } else {
                        tooBig = true;
                    }

                    for (int i = 0; i < last5chars.length - 1; i++) {
                        last5chars[i] = last5chars[i + 1];
                    }
                    last5chars[last5chars.length - 1] = (char)c;

                    if (log.isTraceEnabled()) {
                        log.trace("" + last5chars);
                    }
                    isreading = false;
                    for (int i = 0; i < last5chars.length; i++) {
                        if (last5chars[i] != endchars[i]) {
                            isreading = true;
                            break;
                        }
                    }
                }
                Map<String, String> headers = null;
                if (tooBig) {
                    // could be perhaps reject the entire message?
                    log.warn("Attachment was too big, truncated to " + maxAttachmentSize);
                    headers = new HashMap<String, String>();
                    headers.put("X-Attachment-Truncated", "Attachment was too big, truncated to " + maxAttachmentSize);

                }
                // Copy everything but the '.\r\n' to the result
                String result = data.substring(0, data.length() - (tooBig ? 0 : 3));
                try {
                    log.debug("Now handling data " + result.length());
                    MailHandler.MessageStatus status = handleData(result, headers);
                    if (status == MailHandler.MessageStatus.DELIVERED  || status == MailHandler.MessageStatus.ERRORNEOUS_DELIVERED) {
                        if (status == MailHandler.MessageStatus.DELIVERED) {
                            writer.write("250 Rejoice! We will deliver this email to the user.\r\n");
                        } else {
                            writer.write("250 " + status + " We will deliver this email to the user.\r\n");
                        }
                        writer.flush();
                        delivered++;
                    } else  {
                        log.debug("550 Message not accepted.", new Exception());
                        writer.write("550 Message not accepted. " + status + ".\r\n");
                        writer.flush();
                    }
                } catch (Exception e) {
                    log.error("Exception during handling data '" + result + "' (" + headers + "): " + e.getMessage(), e);
                    writer.write("550 Message error " + e.getMessage() + "\r\n");
                    writer.flush();
                }
            }
            state = State.MAILFROM;
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
     * @return an array of strings, the first element contains the username, the second element is the domain
     */
    private MailHandler.Address parseAddress(String address) {
        if (address == null)
            return null;

        if (address.equals("<>")) {
            return null;
        }

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
            return null;
        }

        return new MailHandler.Address(finaladdress.substring(0, atsign), finaladdress.substring(atsign + 1, finaladdress.length()));
    }



    /**
     * Handle the data from the DATA command. This method does all the work: it creates
     * a MimeMessage, and dispatches that to the MailHandler(s).
     */
    private MailHandler.MessageStatus handleData(String data, Map<String, String> headers) {
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
            return MailHandler.MessageStatus.ERROR;
        } catch (MessagingException e) {
            log.error("Cannot parse message data: [" + data + "]");
            return MailHandler.MessageStatus.ERROR;
        } catch (RuntimeException t) {
            log.warn("Exception in MimeMessage instantiation " + t, t);
            throw t;
        }
        for (MailHandler.Address recipient : recipients) {
            try {
                message.addRecipients(Message.RecipientType.TO, recipient.toString());
            } catch (MessagingException e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    message.setHeader(entry.getKey(), entry.getValue());
                }
            }
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
        if (handler == null) throw new RuntimeException("No handler configured!");
        log.debug("Handling message with " + handler);
        MailHandler.MessageStatus s =  handler.handleMessage(message);
        return s;
    }


}
