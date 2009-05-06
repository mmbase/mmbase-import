package nl.teleacnot.example;

import java.util.*;
import java.util.concurrent.*;
import java.text.MessageFormat;
import org.mmbase.sms.*;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.util.PasswordGenerator;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * THIS IS AN EXAMPLE. THIS CODE IS COPIED FROM TELEAC/NOT AND PROBABLY IS SPECIFIC FOR THEM.
 *
 * The validator processor, is a set-processor for a mobile phone number field. The field will be
 * filled with the set phone number, but prefixed with a confirmation string, which must be sms'sed
 * back first. Confirmations are handled by {@link #Handler}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Validator.java,v 1.1 2007-12-07 10:15:47 michiel Exp $
 **/
public class Validator implements org.mmbase.datatypes.processors.Processor {

    private static final Logger log = Logging.getLoggerInstance(Validator.class);

    static final DelayQueue<Sender> queue = new DelayQueue<Sender>();
    static Map<Integer, Sender> queued = new ConcurrentHashMap<Integer, Sender>();
    static {
        // Runnable to send the 'queued' confirmations SMS's. See {@link #Sender}.
        org.mmbase.module.core.MMBaseContext.startThread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Sender sender = Validator.queue.take();
                            queued.remove(sender.getNodeNumber());
                            log.service("Using " + sender);
                            sender.run();
                        } catch (InterruptedException e) {
                            log.service(Thread.currentThread().getName() +" was interruped.");
                            continue;
                        } catch (RuntimeException rte) {
                            log.error(rte.getMessage(), rte);
                        }
                    }
                }
            }, Validator.class.getName());
    }
    /**
     * Sends a delayed confirmation SMS. It is delayed a few seconds, because the
     * 'setValue("mobile"..' can e.g. in a mm:form be called multiple time. Only one SMS must be
     * sent.`
     *
     * Perhaps a bit of a hack, but I couldn't come up with something better without heavy
     * modifications in mmbase itself.
     */
    static class Sender implements Runnable, Delayed {
        private final long endTime = System.currentTimeMillis() + 1000 * 5;
        private final Node node;
        private String confirmationString;
        private String value;
        Sender(Node n, String confString, String value) {
            node = n ;
            update(confString, value);
        }
        public void update(String confString, String value) {
            this.confirmationString = confString;
            this.value = value;
        }
        public int getNodeNumber() {
            return node.getNumber();
        }

        public void run() {
            if (log.isDebugEnabled()) {
                log.debug("Sending confirmation SMS to " + value + " because ", new Exception());
            }
            String prefix = "";
            for (org.mmbase.sms.Handler h : Receiver.getReceiver().getHandlers()) {
                if (h instanceof Handler) {
                    Handler th = (Handler) h;
                    prefix = th.getPrefix();

                }
            }
            log.debug("Found prefix " + prefix);
            ResourceBundle bundle = ResourceBundle.getBundle("nl.teleacnot.users", node.getCloud().getLocale());
            String message = MessageFormat.format(bundle.getString("sms_request"), prefix + "C" + confirmationString.toUpperCase());
            org.mmbase.sms.Sender.getInstance().send(new BasicSMS("" + value, message));
            log.service("Node is now " + node);

        }
        public long getDelay(TimeUnit unit) {
            return unit.convert(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        public int compareTo(Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Generates the confirmation codes
     */
    private static PasswordGenerator GENERATOR = new PasswordGenerator();

    protected static String getNumber(String v) {
        if (v == null) return v;
        int pos = v.indexOf(":");
        if (pos > 0) {
            return v.substring(pos + 1);
        } else {
            return v;
        }

    }

    public Object process(Node node, Field field, Object value) {
        String v = node.getStringValue(field.getName());
        if (value != null && ! "".equals(value)) {
            if (! getNumber(v).equals(getNumber("" + value))) {
                if (log.isDebugEnabled()) {
                    log.debug("node " + node);
                }
                String confirmationString = GENERATOR.getPassword("SSS");
                Sender s = queued.get(node.getNumber());
                if (s == null) {
                    // not yet queued for confirmation
                    s = new Sender(node, confirmationString, "" + value);
                    queue.offer(s);
                    queued.put(node.getNumber(), s);
                } else {
                    // already queued, avoid offering a second message.
                    s.update(confirmationString, "" + value);
                }
                return confirmationString + ":" + value;
            } else {
                log.service("Not sending confirmation SMS because number already set to " + getNumber(v));
                return v;
            }
        } else {
            log.service("Not sending confirmation SMS because setting to empty mobile number");
            node.setIntValue("mobile_operator", -1);
            return value;
        }
    }

}

