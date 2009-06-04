/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.core.event.EventManager;
import org.mmbase.util.xml.UtilReader;
import java.util.*;
import java.util.regex.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The core of this class are {@link #offer(SMS)} and {@link #send(SMS)}, which both send an SMS,
 * but the first one allows for some delay.
 *
 * This class is abstract and must be extended. The method {@link #getInstance} returns one instance
 * of an extension. Which class is instantiated is determined by &lt;config&gt;utils/sms_sender.xml
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public abstract class Sender {
    private static final Logger log = Logging.getLoggerInstance(Sender.class);

    private static Sender sender = null;
    private static boolean listening = false;
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



    /**
     * @todo Similar code in org.mmbase.module.lucene.Lucene, org.mmbase.notifications.Notifier Generalize this.
     */
    protected static boolean determinActive() {

        boolean active = true;

        String setting = config.get("active");
        while (setting != null && setting.startsWith("system:")) {
            setting = System.getProperty(setting.substring(7));
        }
        if (setting != null) {
            if (setting.startsWith("host:")) {
                Pattern host = Pattern.compile(setting.substring(5));
                try {
                    active =
                        host.matcher(java.net.InetAddress.getLocalHost().getHostName()).matches() ||
                        host.matcher((System.getProperty("catalina.base") + "@" + java.net.InetAddress.getLocalHost().getHostName())).matches();
                } catch (java.net.UnknownHostException uhe) {
                    log.error(uhe);
                }
            } else if (setting.startsWith("machinename:")) {
                Pattern machineName = Pattern.compile(setting.substring(12));
                active = machineName.matcher(org.mmbase.module.core.MMBase.getMMBase().getMachineName()).matches();
            } else {
                 active = "true".equals(setting);
            }
        }
        return active;
    }

    public static Sender getInstance() {
        if (sender == null) {
            try {
                if (determinActive()) {
                    Class<?> clazz = Class.forName(config.get("class"));
                    sender = (Sender) clazz.newInstance();
                    EventManager.getInstance().addEventListener(SMSEventListener.getInstance());
                    log.info("Using " + sender + " to send SMS. " + SMSEventListener.getInstance() + " is listening for remote SMSs");
                    listening = true;
                } else {
                    sender = new EventSender();
                    if (listening) EventManager.getInstance().removeEventListener(SMSEventListener.getInstance());
                    listening = false;
                    log.info("Using " + sender + " to send SMS");
                }
            } catch (Exception e) {
                log.fatal(e.getMessage(), e);
            }
        }
        return sender;
    }





}
