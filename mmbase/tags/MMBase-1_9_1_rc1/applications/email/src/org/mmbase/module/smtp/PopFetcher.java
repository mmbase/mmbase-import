/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/


package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import org.mmbase.util.ResourceLoader;
import java.util.*;
import org.mmbase.applications.crontab.*;
import javax.mail.*;
import javax.mail.search.*;

/**
 * A mail fetcher that does not smtp-listen but periodically pops from a server. Implemented as a cronjob
 *
 *
 * @version $Id: PopFetcher.java,v 1.6 2008-12-30 11:06:12 michiel Exp $
 */
public class PopFetcher extends MailFetcher implements CronJob {
    private static final Logger log = Logging.getLoggerInstance(PopFetcher.class);

    private static final String LASTRUN_ALIAS = PopFetcher.class.getName() + ".lastrun";
    // alias of mmevents node which remembers time of last run.

    private CronEntry entry;

    public void init(CronEntry cronEntry) {
        entry = cronEntry;
    }

    public void stop() {
    }

    public CronEntry getEntry() {
        return entry;
    }
    /**
     * Public constructor. Set all data that is needed for this thread to run.
     */
    public PopFetcher() {
    }

    /**
     * Pops the mail, and maintains an mmevents object, to administer which mails were handled
     * (based on received date)
     */
    public  void run() {
        Properties props = System.getProperties();
        try {
            Cloud cloud = CloudMailHandler.getCloud();
            Node lastRun = null;
            try {
                lastRun = cloud.getNode(LASTRUN_ALIAS);
            } catch (Exception e) {
            }
            if (lastRun == null) {
                log.info("Missing node " + LASTRUN_ALIAS + " now creating");
                lastRun = cloud.getNodeManager("mmevents").createNode();
                lastRun.commit();
                lastRun.createAlias(LASTRUN_ALIAS);
                lastRun.commit();
            }
            Date lastDate = new Date(lastRun.getLongValue("start") * 1000);

            SearchTerm searchTerm = new DateTerm(ComparisonTerm.GT, lastDate) {
                    public boolean match(Message message) {
                        try {
                            if (message == null) {
                                return false;
                            }
                            Date messageDate = message.getReceivedDate();
                            if (messageDate == null) messageDate = message.getSentDate();
                            if (messageDate == null) return true;
                            log.debug("Comparing message " + message.getSubject() + " " + messageDate + " with " + date);
                            boolean result = match(messageDate);
                            return result;
                        } catch (Exception e) {
                            log.warn(e.getMessage() + " " + Logging.stackTrace(e));
                        }
                        return false;
                    }
                };


            String[] configuration = entry.getConfiguration().split(",");
            String protocol = configuration[0];
            String host     = configuration[1];
            String userName = configuration[2];
            String password = configuration[3];
            int port = -1;
            if (configuration.length > 4 && configuration[4].length() > 0) {
                port = Integer.parseInt(configuration[4]);
            }
            // Get a Properties object

            if (configuration.length > 5) {
                props = new Properties();
                props.load(ResourceLoader.getConfigurationRoot().getResourceAsStream(configuration[5]));
            }
            // Get a Session object
            Session session = Session.getInstance(props, null);


            Store store = session.getStore(protocol);
            log.service("Connecting to " + userName + "@" + host + (port == -1 ? "" : ":" + port) + " using " + protocol + ". Using cloud of " + cloud.getUser() + " (" + cloud.getUser().getRank() + "). Previous mail:" + lastDate);

            store.connect(host, port, userName, password);
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            // try to open read/write and if that fails try read-only
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                log.debug("Opening READ_ONLY");
                folder.open(Folder.READ_ONLY);
            }
            if (log.isDebugEnabled()) {
                int newMessages    = folder.getNewMessageCount();
                int unreadMessages = folder.getUnreadMessageCount();
                int totalMessages  = folder.getMessageCount();
                log.debug("New messages = " + newMessages);
                log.debug("Unread messages = " + unreadMessages);
                log.debug("Total messages = " + totalMessages);
            }
            // Attributes & Flags for all messages ..
            Message[] msgs = folder.search(searchTerm);

            // Use a suitable FetchProfile
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(msgs, fp);
            int total = 0;
            for (Message message :  msgs) {
                Date date = message.getReceivedDate();
                if (date == null) date = message.getSentDate();
                log.debug("MESSAGE #" + message.getMessageNumber() + ":" + message.getContentType() + " of " + date + "from " + Arrays.asList(message.getFrom()));
                getHandler().handleMessage(message);

                if (date != null && date.getTime() > lastDate.getTime()) {
                    log.debug("setting last date to " + date);
                    lastDate = date;
                }

            }
            folder.close(false);
            store.close();
            if (lastRun.getLongValue("start") * 1000 != lastDate.getTime()) {
                lastRun.setLongValue("start", lastDate.getTime() / 1000);
                lastRun.commit();
                log.service("Handled " + total + " email messages. Set last run in " + lastRun.getNumber() + " to " + new Date(lastRun.getLongValue("start") * 1000));
            } else {
                log.debug("No newer emails found");
            }
        } catch (Exception e) {
            log.error(e.getMessage() + "props: " + props, e);
        }
    }



}
