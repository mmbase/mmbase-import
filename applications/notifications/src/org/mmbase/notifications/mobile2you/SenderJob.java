/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications.mobile2you;
import org.mmbase.applications.crontab.AbstractCronJob;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If using mobile2you for notification, then this class must be scheduled as an mmbas cronjob. It
 * queues to be-send SMS-messages and then communicates with Mobile2You when this Job is
 * scheduled. It should run regularly, e.g. every 5 minutes or so. Or every minute. This way only
 * one external connection to mobile2you every 5 or 1 minutes is made.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: SenderJob.java,v 1.1 2007-10-15 13:52:55 michiel Exp $
 **/
public class SenderJob  extends AbstractCronJob {

    /**
     * Representation of one SMS message. Currently simply wraps the two nodes in given in the
     * constructor. But it also could be changed to store more of an actual SMS-message.
     */
    public static class SMS {
        final Node recipient;
        final Node notifyable;
        public SMS(Node r, Node n) {
            recipient = r;
            notifyable = n;
        }
    }

    private static Queue<SMS> queue = new LinkedBlockingQueue<SMS>();

    public static void offer(Node recipient, Node notifyable) {
        queue.offer(new SMS(recipient, notifyable));
    }

    private static final Logger log = Logging.getLoggerInstance(SenderJob.class);


    // TODO
    public void run() {
        try {
            URL url = new URL(cronEntry.getConfiguration());
            URLConnection con = url.openConnection();
            OutputStream out = con.getOutputStream();
            InputStream in = con.getInputStream();
            Writer writer = new OutputStreamWriter(out);
            int drain = queue.size();
            writer.write("<?xml >");
            // Perhaps use something like http://xml-writer.cvs.sourceforge.net/xml-writer/xml-writer/java/src/com/megginson/sax/XMLWriter.java?view=markup
            for (int i = 0; i < drain; i++) {
                SMS sms = queue.poll();
                /// add to xml

            }
            out.close();
            // handle response too;
        } catch (MalformedURLException mfue) {
            log.error(mfue);
        } catch (IOException ioe) {
            log.error(ioe);
        }

    }


}
