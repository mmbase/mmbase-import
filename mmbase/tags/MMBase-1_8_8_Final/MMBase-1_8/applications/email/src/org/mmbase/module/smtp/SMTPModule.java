/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.functions.*;
import java.util.*;

/**
 * RFC 2821 partial-compliant SMTP server. All commands
 * needed for a compliant SMTP server are implemented,
 * but no more than these.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @version $Id$
 */
public class SMTPModule extends org.mmbase.module.WatchedReloadableModule {
    private static final Logger log = Logging.getLoggerInstance(SMTPModule.class);
    SMTPListener listener;
    Map<String, String> properties;

    private String[] mandatoryProperties = {"hostname", "port", "domains",
                                            "usersbuilder", "usersbuilder.accountfield"
    };
    private String[] optionalProperties = {
        "mailboxbuilder", "mailboxbuilder.where", "mailboxbuilder.notfound"
    };

    public void init() {
        init(getInitParameters());
    }
    public void reload() {
        log.service("Reloading smtp");
        CloudMailHandler.props = null;
        if (listener != null) {
            listener.interrupt();
        }
        init();
    }

    /**
     * Initialize the SMTP engine. Creates a listening thread that can
     * initiate worker threads.
     */
    private void init(Map<String, String> properties) {
        log.info("Initializing SMTP module");
        this.properties = properties;

        if (checkProperties()) {
            listener = new SMTPListener(properties);
            listener.start();
        } else {
            log.error("SMTP module not started due to errors");
        }
    }

    /**
     * Check if all the mandatory properties are set.
     */
    private boolean checkProperties() {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        boolean result = true;
        for (int i = 0; i < mandatoryProperties.length; i++) {
            if (!properties.containsKey(mandatoryProperties[i])) {
                log.error("Mandatory property '" + mandatoryProperties[i] + "' not defined!");
                result = false;
            }
        }

        Map<String, String> allproperties = new HashMap<String, String>();
        for (int i = 0; i < mandatoryProperties.length; i++) {
            allproperties.put(mandatoryProperties[i], "yes");
        }
        for (int i = 0; i < optionalProperties.length; i++) {
            allproperties.put(optionalProperties[i], "yes");
        }

        for (Iterator<String> e = properties.keySet().iterator(); e.hasNext(); ) {
            String key = e.next();
            if (!allproperties.containsKey(key)) {
                log.warn("Property '" + key + "' unknown, ignoring");
            }
        }
        return result;
    }

    public String getLocalEmailDomains() {
        return properties.get("domains");
    }

    /**
     * Shutdown method. Cleanly shut down all current threads
     */
    public void shutdown() {
        log.info("Shutting down SMTP module");
        if (listener != null) listener.interrupt();
    }

    {
        addFunction(new AbstractFunction/*<SMTPListener>*/("listener", Parameter.EMPTY , new ReturnType/*<SMTPListener>*/(SMTPListener.class, "")) {
            public SMTPListener getFunctionValue(Parameters arguments) {
                return SMTPModule.this.listener;
            }
            });
    }

    /**
     * Useful for debugging
     */
    public static void main(String args[]) {
        Map<String, String> h = new HashMap<String, String>();
        h.put("hostname", "localhost");
        h.put("port", "1026");
        h.put("domains", "*");
        h.put("emailbuilder", "emails");
        h.put("emailbuilder.bodyfield", "body");
        h.put("usersbuilder", "people");
        h.put("usersbuilder.accountfield", "account");

        SMTPModule mod = new SMTPModule();
        mod.init(h);
        while (true) {

        }
    }
}
