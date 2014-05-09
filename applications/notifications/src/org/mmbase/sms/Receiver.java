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
 * The core of this class is {@link #offer(SMS)} which offers an SMS message to a
 * queue. This queue is emptied and offered to {@link Handler}s which are configured in &lt;config
 * dir&gt;utils/sms_handlers.xml.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public  class Receiver implements Runnable {

    public static final String DEFAULT_CONFIG_FILE = "sms_handlers.xml";

    private static final Logger log = Logging.getLoggerInstance(Receiver.class);

    private static Map<String, Receiver> threads = new ConcurrentHashMap<String, Receiver>();

    private static BlockingQueue<SMS> queue = new LinkedBlockingQueue<SMS>();


    protected static Receiver getReceiver(String config) {
        Receiver thread = threads.get(config);
        if (thread == null) {
            thread = new Receiver(config);
            Thread t = org.mmbase.module.core.MMBaseContext.startThread(thread, Receiver.class.getName());
            threads.put(config, thread);
            log.info("Started " + thread);
        }
        return thread;
    }
    public static Receiver getReceiver() {
        return getReceiver(DEFAULT_CONFIG_FILE);
    }

    protected static synchronized boolean offer(String config, SMS sms) {
        getReceiver(config);
        boolean ok = queue.offer(sms);
        log.service("Offering " + sms + " to handlers of " + config + " " + queue.hashCode() + " " +  queue + " " + ok);
        return ok;

    }

    /**
     * Offers a SMS message for 'handling' by the SMS Handlers.
     */
    public static synchronized boolean offer(SMS sms) {
        return offer(DEFAULT_CONFIG_FILE, sms);
    }

    private List<Handler> handlers = new ArrayList<Handler>();

    protected Receiver(String configFile) {
        Map<String, ?> config = new UtilReader(configFile).getMaps();
        log.info("Found " + config);
        if (config.size() == 0) {
            log.error("No SMS-handlers found in " + configFile );
        } else {
            for (Map.Entry<String, ?> entry : config.entrySet()) {
                String clazz = entry.getKey();
                try {
                    Class claz = Class.forName(clazz);
                    Handler handler = (Handler) claz.newInstance();
                    Collection<Map.Entry<String, String>> properties = (Collection<Map.Entry<String, String>>) entry.getValue();
                    log.info("Setting properties " + properties + " on " + handler);
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

    public List<Handler> getHandlers() {
        return Collections.unmodifiableList(handlers);
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
