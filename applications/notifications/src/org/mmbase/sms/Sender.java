/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.bridge.*;
import org.mmbase.util.xml.UtilReader;
import java.util.*;
import java.util.concurrent.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The core of this class is {@link #offer(String, int, Message)} which offers an SMS message to a
 * queue. This queue is emptied and offered to {@link Handler}s which are configured in &lt;config
 * dir&gt;utils/sms_handlers.xml.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Sender.java,v 1.3 2007-11-12 18:34:20 michiel Exp $
 **/
public abstract class Sender  {
    private static final Logger log = Logging.getLoggerInstance(Sender.class);

    private static Sender sender = null;
    private static Map<String, String> config = new UtilReader("sms_sender.xml", new Runnable() { public void run() {sender = null;} }).getProperties();

    /**
     * Sends an SMS.
     */
    public abstract boolean send(SMS sms);
    /**
     * Offers an SMS for sending. It needs not do this immediately, but may collect some.
     */
    public  abstract boolean offer(SMS sms);


    public static Sender getInstance() {
        if (sender == null) {
            try {
                Class clazz = Class.forName(config.get("class"));
                sender = (Sender) clazz.newInstance();
                log.info("Using " + sender + " to send SMS");
            } catch (Exception e) {
                log.fatal(e.getMessage(), e);
            }
        }
        return sender;
    }




}
