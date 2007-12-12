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
 * The core of this class are {@link #offer(SMS)} and {@link #send(SMS)}, which both send an SMS,
 * but the first one allows for some delay.
 *
 * This class is abstract and must be extended. The method {@link getInstance} returns one instance
 * of an extension. Which class is instantiated is determined by &lt;config&gt;utils/sms_sender.xml
 *
 * @author Michiel Meeuwissen
 * @version $Id: Sender.java,v 1.5 2007-11-26 15:50:38 michiel Exp $
 **/
public abstract class Sender  {
    private static final Logger log = Logging.getLoggerInstance(Sender.class);

    private static Sender sender = null;
    private static Map<String, String> config = new UtilReader("sms_sender.xml", new Runnable() { public void run() {sender = null;} }).getProperties();

    /**
     * Sends an SMS, immediately.
     */
    public abstract boolean send(SMS sms);
    /**
     * Offers an SMS for sending. It needs not do this immediately, but may collect some, and offer
     * them in batch to an SMS gateway.
     */
    public abstract boolean offer(SMS sms);

    public abstract Collection<SMS> getQueue();


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
