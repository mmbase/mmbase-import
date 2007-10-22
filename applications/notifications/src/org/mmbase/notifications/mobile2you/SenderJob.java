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
import org.mmbase.util.Casting;
import org.mmbase.util.xml.UtilReader;
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
 * @version $Id: SenderJob.java,v 1.4 2007-10-22 17:20:58 michiel Exp $
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

        public String body() {
            return notifyable.getStringValue("message");
        }
        public String phone() {
            return Casting.toString(recipient.getFunctionValue("phone", null));
        }

        public void add(XmlWriter w, Map<String, String> config) throws SAXException {
            w.startElement("MSG");
            w.startElement("FROM");
            w.characters(config.get("from"));
            w.endElement("FROM");
            {
                AttributesImpl a = new AttributesImpl();
                a.addAttribute("", "TYPE", "", "CDATA", "TEXT");
                w.startElement("", "BODY", "", a);
                w.characters(body());
                w.endElement("BODY");
            }
            {
                AttributesImpl a = new AttributesImpl();
                a.addAttribute("", "OPERATOR", "", "CDATA", config.get("operator"));
                w.startElement("", "TO", "", a);
                w.characters(phone());
                w.endElement("TO");
            }
            w.endElement("MSG");
        }
    }

    private static Queue<SMS> queue = new LinkedBlockingQueue<SMS>();

    public static void offer(Node recipient, Node notifyable, Date date) {
        queue.offer(new SMS(recipient, notifyable, date));
    }

    private static final Logger log = Logging.getLoggerInstance(SenderJob.class);


    public void run() {
        try {
            send(cronEntry.getConfiguration());
        } catch (MalformedURLException mfue) {
            log.error(mfue);
        } catch (SAXException se) {
            log.error(se);
        } catch (IOException ioe) {
            log.error(ioe);
        }

    }

    protected void send(String configFile)  throws SAXException, IOException {
        Map<String, String> config = new UtilReader(configFile).getProperties();
        String u = config.get("url");
        log.debug("Using '" + u + "'");
        URL url = new URL(config.get("url"));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        send(out, config);

        try {
            final InputStream in = con.getInputStream();
            BufferedReader is = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = is.readLine()) != null) {
                log.debug(line);
            }
            is.close();
        } catch (Exception e) {
            log.error(e);
        }

        {
            final InputStream error = con.getErrorStream();
            if (error != null) {
                BufferedReader es = new BufferedReader(new InputStreamReader(error));
                String line;
                while ((line = es.readLine()) != null) {
                    log.error(line);
                }
                es.close();
            }
        }
        out.close();


    }

    protected void send(OutputStream out, Map<String, String> config) throws SAXException, IOException {
        Writer writer = new OutputStreamWriter(out);
        XmlWriter w = new XmlWriter(writer);
        w.setSystemId("http://www.clubmessage.biz/DTD/bundles/messages.dtd");
        w.startDocument();
        {
            AttributesImpl a = new AttributesImpl();
            a.addAttribute("", "PID", "", "CDATA", "25");
            w.startElement("", "MESSAGES", "", a);
        }
        {
            AttributesImpl a = new AttributesImpl();
            a.addAttribute("", "ID", "", "CDATA", config.get("customerId"));
            w.emptyElement("", "CUSTOMER",  "", a);
        }
        {
            AttributesImpl a = new AttributesImpl();
            a.addAttribute("", "LOGIN", "", "CDATA", config.get("userLogin"));
            a.addAttribute("", "PASSWORD", "", "CDATA", config.get("userPassword"));
            w.emptyElement("", "USER",  "", a);
        }
        //w.emptyElement("ADMIN_EMAIL");
        w.startElement("TARIFF");
        w.characters("0");
        w.endElement("TARIFF");
        //w.emptyElement("REFERENCE");

        int drain = queue.size();
        for (int i = 0; i < drain; i++) {
            SMS sms = queue.poll();
            sms.add(w, config);

        }
        w.endElement("MESSAGES");
        w.endDocument();
        w.flush();
    }

    /**
     * Main for testing only
     */
    public static void main(final String[] argv) throws Exception {
        SenderJob sender = new SenderJob();
        if (argv.length == 0) {
            System.out.println("Use tel-number as argument");
            sender.send(System.out, new HashMap<String, String>());
        } else {
            sender.queue.offer(new SMS(null, null, null) {
                    public String body() {
                        return "Test test " + new Date();
                    }
                    public String phone() {
                        return argv[0];
                    }
                });
            //sender.send(System.out);
            sender.send("sms_sender.xml");
        }
    }


}
