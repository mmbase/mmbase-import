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
import java.lang.reflect.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The core of this class is {@link #offer(String, int, Message)} which offers an SMS message to a
 * queue. This queue is emptied and offered to {@link Handler}s which are configured in &lt;config
 * dir&gt;utils/sms_handlers.xml.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Receiver.java,v 1.8 2007-11-19 14:15:27 michiel Exp $
 **/
public  class Receiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(Receiver.class);

    private static Map<String, Thread> threads = new ConcurrentHashMap<String, Thread>();

    private static BlockingQueue<SMS> queue = new LinkedBlockingQueue<SMS>();


    protected static synchronized boolean offer(String config, String mobile, int operator, String message) {
        Thread thread = threads.get(config);
        if (thread == null) {
            thread = org.mmbase.module.core.MMBaseContext.startThread(new Receiver(config), Receiver.class.getName());
            threads.put(config, thread);
            log.info("Started " + thread);
        }
        SMS sms = new BasicSMS(mobile, operator, message);
        boolean ok = queue.offer(sms);
        log.service("Offering " + sms + " to handlers of " + config + " " + queue.hashCode() + " " +  queue + " " + ok);
        return ok;

    }

    /**
     * Offers a SMS message for 'handling' by the SMS Handlers.
     */
    public static synchronized boolean offer(String mobile, int operator, String message) {
        return offer("sms_handlers.xml", mobile, operator, message);
    }

    private List<Handler> handlers = new ArrayList<Handler>();

    protected Receiver(String configFile) {
        Map<String, ?> config = new UtilReader(configFile).getProperties();
        log.info("Found " + config);
        if (config.size() == 0) {
            log.error("No SMS-handlers found");
        } else {
            for (Map.Entry<String, ?> entry : config.entrySet()) {
                String clazz = entry.getKey();
                try {
                    Class claz = Class.forName(clazz);
                    Handler handler = (Handler) claz.newInstance();
                    Collection<Map.Entry<String, String>> properties = (Collection<Map.Entry<String, String>>) entry.getValue();
                    for (Map.Entry<String, String> property : properties) {
                        String key   = property.getKey();
                        String value = property.getValue();
                        Method method = claz.getMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1), String.class);
                        method.invoke(handler, value);
                    }
                    handlers.add(handler);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
    }

    /**
     * Looks in the queue. If something found, creates a cloud (using class security) and adds SMS
     * objects to the {@link Handler}s.
     */
    public void run() {
        MAIN:
        while (true) {
            try {
                log.service("Wating for new entry in queue " + queue.hashCode());
                SMS sms = queue.take();
                log.service("Received SMS " + sms);
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                for (Handler handler : handlers) {
                    boolean handled = handler.handle(cloud, sms);
                    if (handled) continue MAIN;
                }
                log.warn("Could not handle SMS " + sms + "None of " + handlers + " took it");
            } catch (InterruptedException ie) {
                log.warn(ie);
                threads.clear();
                break MAIN;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("End of Receiver Thread. Still in queue: " + queue);

    }

}
