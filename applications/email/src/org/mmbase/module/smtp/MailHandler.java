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

/**
 * A MailHandler handles <em>one</em> mail. So you must create a new one for every received message
 * (You can use {@link Factory}).
 * @version $Id: MailHandler.java,v 1.14 2008-12-30 11:06:12 michiel Exp $
 */
public interface  MailHandler {

    enum MailBoxStatus {
        OK,
        NO_SUCH_USER,
        NO_INBOX,
        CANT_CREATE_INBOX,
        TOO_MANY_INBOXES,
        UNDEFINED
    }
    enum MessageStatus {
        DELIVERED,
        ERRORNEOUS_DELIVERED,
        ERROR,
        IGNORED,
        TOO_BIG,
        UNDEFINED
    }

    static class Address {
        public final String user;
        public final String domain;
        Address(String u, String d) {
            user = u; domain = d;
        }
        public String toString() {
            return user + "@" + domain;
        }
    }
    /**
     *
     */
    MailBoxStatus addMailbox(String user, String domain);
    MessageStatus handleMessage(Message message);

    void clearMailboxes();
    int size();
    //List<Address> getRecipients();


    public static class Factory {
        private static final Logger log = Logging.getLoggerInstance(Factory.class);

        static UtilReader.PropertiesMap  mailHandlers =
            new UtilReader("mailhandlers.xml").getMaps();

        public static MailHandler getInstance() {
            List<MailHandler> mh = new ArrayList<MailHandler>();
            List<Map.Entry<String, String>> classes = (List<Map.Entry<String, String>>) mailHandlers.get("classes");
            if (classes == null) {
                log.warn("No mail handlers found in " + mailHandlers);
            } else {
                for (Map.Entry<String, String> c : classes) {
                    String cn = c.getKey();
                    try {
                        mh.add((MailHandler) Class.forName(cn).newInstance());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            MailHandler instance;
            if (mh.size() == 1) {
                instance = mh.get(0);
            } else {
                instance = new ChainedMailHandler(mh.toArray(new MailHandler[0]));
            }
            log.debug("MailHandler: " + instance);

            return instance;
        }
    }
}
