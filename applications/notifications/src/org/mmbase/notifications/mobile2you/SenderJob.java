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
import java.text.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.mmbase.util.xml.XmlWriter;
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
 * @version $Id: SenderJob.java,v 1.2 2007-10-22 12:51:18 michiel Exp $
 **/
public class SenderJob  extends AbstractCronJob {

    /**
     * Representation of one SMS message. Currently simply wraps the two nodes in given in the
     * constructor. But it also could be changed to store more of an actual SMS-message.
     */
    public static class SMS {
        final Node recipient;
        final Node notifyable;
        final Date date;
        public SMS(Node r, Node n, Date d) {
            recipient = r;
            notifyable = n;
            date = d;
        }

        public String m2uId() {
            return MessageFormat.format("{0,number,########}.{1,number,####}.{2,number,####}.{3,number,####}.{4,number,############}",
                                        0, 0,
                                        recipient.getNumber(), notifyable.getNumber(), date.getTime());

        }
        public void add(XmlWriter w) throws SAXException {
            AttributesImpl a = new AttributesImpl();
            a.addAttribute("", "messageVersion", "", "CDATA", "2.0");
            w.startElement("", "m2u_message", "", a);
            w.startElement("header");
            w.endElement("header");
            w.startElement("body");
            w.endElement("body");
            w.endElement("m2u_message");
        }
    }

    private static Queue<SMS> queue = new LinkedBlockingQueue<SMS>();

    public static void offer(Node recipient, Node notifyable, Date date) {
        queue.offer(new SMS(recipient, notifyable, date));
    }

    private static final Logger log = Logging.getLoggerInstance(SenderJob.class);


    // TODO
    public void run() {
        try {
            URL url = new URL(cronEntry.getConfiguration());
            URLConnection con = url.openConnection();
            OutputStream out = con.getOutputStream();
            InputStream in = con.getInputStream();
            send(out);
            // handle response too;
        } catch (MalformedURLException mfue) {
            log.error(mfue);
        } catch (SAXException se) {
            log.error(se);
        } catch (IOException ioe) {
            log.error(ioe);
        }

    }

    protected void send(OutputStream out) throws SAXException, IOException {
        Writer writer = new OutputStreamWriter(out);
        XmlWriter w = new XmlWriter(writer);
        int drain = queue.size();
        w.startDocument();
        w.startElement("m2u_envelop");
        for (int i = 0; i < drain; i++) {
            SMS sms = queue.poll();
            /// add to xml

        }
        w.endElement("m2u_envelop");
        w.endDocument();
        out.close();
    }

    /**
     * Main for testing only
     */
    public static void main(String[] argv) throws Exception {
        SenderJob sender = new SenderJob();
        sender.send(System.out);
    }


}
