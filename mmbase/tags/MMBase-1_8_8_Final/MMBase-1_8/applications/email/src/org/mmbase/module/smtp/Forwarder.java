/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.xml.UtilReader;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.mmbase.applications.email.SendMail;

/**
 * Forwarding MailHandler. Fill {@link #forwards} to make this actually do something.
 *
 * @version $Id$
 */
public class Forwarder implements MailHandler {
    private static final Logger log = Logging.getLoggerInstance(Forwarder.class);

    private static UtilReader.PropertiesMap f = new UtilReader("forwards.xml", new Runnable() { public void run() {readForwards();}}).getMaps();
    protected static final Map<String, InternetAddress[]> forwards = new HashMap<String, InternetAddress[]>();

    protected static void readForwards() {
        List<Map.Entry<String, String>> classes = (List<Map.Entry<String, String>>) f.get("forwards");
        for (Map.Entry<String, String> c : classes) {
            try {
                forwards.put(c.getKey(), InternetAddress.parse(c.getValue()));
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
    static {
        readForwards();
    }

    protected final Set<String> mailboxes = new HashSet<String>();


    public MessageStatus handleMessage(Message message) {

        if (mailboxes.size() == 0) return MessageStatus.IGNORED;

        try {
            SendMail sendmail = (SendMail)org.mmbase.module.Module.getModule("sendmail");
            //forwarded.setSender(message.getFrom());
            for (String to : mailboxes) {
                InternetAddress[] recipients = forwards.get(to);
                Message forward = new MimeMessage(sendmail.getSession());
                // Fill in header
                forward.setSubject(message.getSubject());
                forward.setFrom(message.getFrom()[0]);
                forward.setRecipients(Message.RecipientType.TO, recipients);

                forward.setDataHandler(message.getDataHandler());

                log.info("Forwarding mail to " + Arrays.asList(recipients));

                Transport.send(forward);

            }
            return MessageStatus.DELIVERED;
        } catch (Exception e) {
            log.error(e);
            return MessageStatus.ERROR;
        }
    }



    public void clearMailboxes() {
        mailboxes.clear();
    }

    public int size() {
        return mailboxes.size();
    }

    public MailBoxStatus addMailbox(String user, String domain) {
        String key = user + "@" + domain;
        if(forwards.containsKey(key)) {
            mailboxes.add(user + "@" + domain);
            return MailBoxStatus.OK;
        } else {
            return MailBoxStatus.NO_INBOX;
        }

    }
}
