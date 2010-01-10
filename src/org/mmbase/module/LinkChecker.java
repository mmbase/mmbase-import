/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.List;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * The linkChecker module detects broken urls in the urls builder and the jumpers builder.
 * If the linkchecker module is active it will at start up (5 minutes after the MMBase initialisation)
 * and start perfoming checks.
 * This wil only happend once every time time MMBase has been started.<br />
 * For the LinckChecker to work the sendmail modules has to be configured and has to be active.
 *
 * @author Rob vermeulen
 * @author Kees Jongenburger
 * @version $Id$
 **/

public class LinkChecker extends ProcessorModule implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(LinkChecker.class);

    private static long INITIAL_WAIT_TIME = 5 * 60 * 1000; // initial wait time after startup, default 5 minutes
    private static long WAIT_TIME = 0; // wait time between runs, default 0 (don't wait but terminate)
    private static long WAIT_TIME_BETWEEN_CHECKS = 5 * 1000; // wait time bewteen individual checks, default 5 seconds

    private MMBase mmbase;

    public void init() {
        super.init();
        String value  = getInitParameter("waittime");
        if (value!=null) {
            try {
                WAIT_TIME = Long.parseLong(value);
            } catch (NumberFormatException nfe) {
                log.warn("The value for property 'waittime'  (" + value + ") is not a valid number.");
            }
        }
        value  = getInitParameter("initialwaittime");
        if (value!=null) {
            try {
                INITIAL_WAIT_TIME = Long.parseLong(value);
            } catch (NumberFormatException nfe) {
                log.warn("The value for property 'initialwaittime'  (" + value + ") is not a valid number.");
            }
        }
        value  = getInitParameter("waittimebetweenchecks");
        if (value!=null) {
            try {
                WAIT_TIME_BETWEEN_CHECKS = Long.parseLong(value);
            } catch (NumberFormatException nfe) {
                log.warn("The value for property 'waittimebetweenchecks'  (" + value + ") is not a valid number.");
            }
        }

        mmbase = MMBase.getMMBase();
        log.info("Module LinkChecker started");
        MMBaseContext.startThread(this, "LinkChecker");
    }

    public String getModuleInfo() {
        return "This module checks all urls in the urls and jumpers builders";
    }

    public void run() {
        long waitTime = INITIAL_WAIT_TIME;  // wait a certain time after startup (default 5 minutes)
        while (!mmbase.isShutdown()) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ie) {
                return;
            }
            log.service("LinkChecker starting to check all Jumpers and Urls");

            StringBuffer data = new StringBuffer();
            checkUrls("urls", "url", data);
            checkUrls("jumpers", "url", data);

            try {
                SendMailInterface sendmail = (SendMailInterface) getModule("sendmail");
                if (sendmail != null) {
                    // init variables for mail.
                    String from = getInitParameter("from");
                    String to = getInitParameter("to");
                    String subject = getInitParameter("subject");
                    if (subject == null || subject.equals("")) {
                        subject = "List of incorrect urls and jumpers";
                    }
                    // Send Email if needed.
                    if (!data.toString().equals("")) {
                        Mail mail = new Mail(to, from);
                        mail.setSubject(subject);
                        mail.setText(data.toString());
                        sendmail.sendMail(mail);
                    }
                } else {
                    log.warn("LinkChecker requires the sendmail module to be active");
                }
            } catch (RuntimeException re) {
                log.error("LinkChecker -> Error in Run() " + re);
            }
            // determine how log to wait next time 'round
            // if 0, terminate
            waitTime = WAIT_TIME;
            if (waitTime == 0) {
                log.debug("LinkChecker finished, thread terminated.");
                return;
            }
       }
    }

    /**
     * Checks if the urls in a specified builder exist.
     * @param builderName the builder to check
     * @param fieldName the fieldname of the url to check
     * @param data the StringBuffer to append error information to
     * @since MMBase-1.7
     */
    protected void checkUrls(String builderName, String fieldName, StringBuffer data) {
        // Get all urls.
        MMObjectBuilder urls = mmbase.getBuilder(builderName);
        if (urls != null) {
            List nodes = urls.getNodes();
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                MMObjectNode node = (MMObjectNode) iter.next();
                long number = node.getNumber();
                String url = "" + node.getValue(fieldName);
                // Check if an url is correct.
                try {
                    if (!checkUrl(url)) {
                        data.append("Incorrect url: " + url + " (objectnumber: " + number + ")\n");
                    }
                } catch (MalformedURLException mue) {
                    data.append("Error in url (malformed): " + url + " (objectnumber: " + number + ")\n");
                } catch (IOException ioe) {
                    data.append("Error in url (IO failure): " + url + " (objectnumber: " + number + ")\n");
                } catch (RuntimeException re) {
                    data.append("Error in url (unknown): " + url + " (objectnumber: " + number + ", error: "+re.getMessage()+")\n");
                }
                try {
                    Thread.sleep(WAIT_TIME_BETWEEN_CHECKS);
                } catch (InterruptedException ie) {} //wait 5 seconds
            }
        }
    }

    /**
     * Checks if an url exists.
     * @param url the url to check
     * @return <code>false</code> if the url does not exist, <code>true</code> if the url exists
     */
    protected boolean checkUrl(String url) throws MalformedURLException, IOException {
        if (url.indexOf("http") == 0 || url.indexOf("ftp") == 0) {
            URL urlToCheck = new URL(url);
            URLConnection uc = urlToCheck.openConnection();
            String header = uc.getHeaderField(0);
            return header.indexOf("404") == -1;
        } else {
            // I don't know if the url is wrong, so lets say it's correct.
            return true;
        }
    }
}
