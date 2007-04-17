package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.LocalContext;
import java.util.Hashtable;

/**
 * Listener thread, that accepts connection on port 25 (default) and 
 * delegates all work to its worker threads.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @version $Id: SMTPListener.java,v 1.6 2006-12-27 12:48:22 mmeeuwissen Exp $
 */
public class SMTPListener extends Thread {
    private static final Logger log = Logging.getLoggerInstance(SMTPListener.class);
    private boolean running = true;
    private java.net.ServerSocket ssocket;
    private final Hashtable properties;

    public SMTPListener(Hashtable properties) {
        this.properties = properties;
    }

    /**
     * Main run method; it will listen for new connections and spawn
     * new threads for incoming connections.
     */
    public void run() {
        Cloud cloud = null;
        try {
            cloud = LocalContext.getCloudContext().getCloud("mmbase");
        } catch (java.lang.ExceptionInInitializerError e) {
            // fail silently?
        }
        String portnr = (String)properties.get("port");
        int port = Integer.parseInt(portnr);

        try {
            ssocket = new java.net.ServerSocket(port);
        } catch (Exception e) {
            running = false;
            log.error("Cannot listen on port " + port);
        }
        log.info("SMTP listening on " + ssocket);

        while (running) {
            try {
                java.net.Socket socket = ssocket.accept();
                if (log.isDebugEnabled()) {
                    log.debug("Accepted connection: " + socket);
                }
                SMTPHandler handler = new SMTPHandler(socket, properties, cloud);
                handler.start();
            } catch (Exception e) {
                log.error("Exception while accepting connections: " + e);
                try {
                    Thread.sleep(1000);
                } catch (Exception ie) {return;}
            }
        }
    }

    public void interrupt() {
        // Interrupted; this only happens when we are shutting down
        log.info("Interrupt() called");
        if (ssocket != null) {
            try {
                ssocket.close();
            } catch (Exception e) {
            }
            ssocket = null;
        }
        running = false;
    }
}
