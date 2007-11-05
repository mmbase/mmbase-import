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
 *
 * @author Michiel Meeuwissen
 * @version $Id: Receiver.java,v 1.3 2007-11-05 14:35:12 michiel Exp $
 **/
public  class Receiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(Receiver.class);

    private static Map<String, Thread> threads = new ConcurrentHashMap<String, Thread>();

    public static class SMS {
        public final String mobile;
        public final int operator;
        public final String message;
        public SMS(String mob, int o, String mes) {
            this.mobile = mob;
            this.operator = o;
            this.message  = mes;
        }
        public String toString() {
            return mobile + ":" + message;
        }

    }

    private static BlockingQueue<SMS> queue = new LinkedBlockingQueue<SMS>();


    protected static synchronized boolean offer(String config, String mobile, int operator, String message) {
        Thread thread = threads.get(config);
        if (thread == null) {
            thread = org.mmbase.module.core.MMBaseContext.startThread(new Receiver(config), Receiver.class.getName());
            threads.put(config, thread);
            log.info("Started " + thread);
        }
        SMS sms = new SMS(mobile, operator, message);
        boolean ok = queue.offer(sms);
        log.service("Offering " + sms + " to handlers of " + config + " " + queue.hashCode() + " " +  queue + " " + ok);
        return ok;

    }

    public static synchronized boolean offer(String mobile, int operator, String message) {
        return offer("sms_handlers.xml", mobile, operator, message);
    }

    private List<Handler> handlers = new ArrayList<Handler>();

    public Receiver(String configFile) {
        Map<String, Object> config = new UtilReader(configFile).getProperties();
        log.info("Found " + config);
        Collection<Map.Entry<String, String>> col = (Collection<Map.Entry<String, String>>) config.get("handlers");
        if (col == null) {
            log.error("No SMS-handlers found");
        } else {
            for (Map.Entry<String, String> entry : col) {
                String clazz = entry.getValue();
                try {
                    handlers.add((Handler) Class.forName(clazz).newInstance());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

    }


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
                log.warn("Could not handle SMS " + sms);
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
