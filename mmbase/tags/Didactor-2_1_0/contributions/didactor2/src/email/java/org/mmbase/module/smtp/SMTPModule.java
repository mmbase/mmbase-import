package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * RFC 2821 partial-compliant SMTP server. All commands
 * needed for a compliant SMTP server are implemented,
 * but no more than these.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class SMTPModule extends org.mmbase.module.Module {
    private Logger log = Logging.getLoggerInstance(SMTPModule.class.getName());
    SMTPListener listener;
    Hashtable properties;

    private String[] mandatoryProperties = {"hostname", "port", "domains",
            "emailbuilder", "emailbuilder.bodyfield", 
            "usersbuilder", "usersbuilder.accountfield"
    };
    private String[] optionalProperties = {
            "mailboxbuilder", "mailboxbuilder.where", "mailboxbuilder.notfound",
            "emailbuilder.tofield", "emailbuilder.fromfield", "emailbuilder.subjectfield",
            "emailbuilder.headersfield", "emailbuilder.datefield", "emailbuilder.ccfield",
            "emailbuilder.typefield"
    };
    
    public void init() {
        init(getInitParameters());
    }

    /**
     * Initialize the SMTP engine. Creates a listening thread that can
     * initiate worker threads.
     */
    private void init(Hashtable properties) {
        log.info("Initializing SMTP module");
        this.properties = properties;
        
        if (checkProperties()) {
            listener = new SMTPListener(properties);
            listener.start();
            log.info("SMTP module listening on port " + properties.get("port"));
        } else {
            log.error("SMTP module not started due to errors");
        }
    }

    /**
     * Check if all the mandatory properties are set.
     */
    private boolean checkProperties() {
        if (properties == null) {
            properties = new Hashtable();
        }
        boolean result = true;
        for (int i=0; i<mandatoryProperties.length; i++) {
            if (!properties.containsKey(mandatoryProperties[i])) {
                log.error("Mandatory property '" + mandatoryProperties[i] + "' not defined!");
                result = false;
            }
        }

        Hashtable allproperties = new Hashtable();
        for (int i=0; i<mandatoryProperties.length; i++) {
            allproperties.put(mandatoryProperties[i], "yes");
        }
        for (int i=0; i<optionalProperties.length; i++) {
            allproperties.put(optionalProperties[i], "yes");
        }

        for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            if (!allproperties.containsKey(key)) {
                log.warn("Property '" + key + "' unknown, ignoring");
            }
        }
        return result;
    }

    public void onload() {
    }
    /**
     * Shutdown method. Cleanly shut down all current threads
     */
    public void shutdown() {
        log.info("Shutting down SMTP module");
        listener.interrupt();
    }

    /**
     * Useful for debugging
     */
    public static void main(String args[]) {
        Hashtable h = new Hashtable();
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
