package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.LocalContext;
import java.util.Hashtable;

/**
 * Listener thread, that accepts connection on port 25 (default) and 
 * delegates all work to its worker threads.
 */
public class SMTPListener extends Thread {
    private Logger log = Logging.getLoggerInstance(SMTPListener.class.getName());
    private boolean running = true;
    private java.net.ServerSocket ssocket;
    private Hashtable properties;
    
    public SMTPListener(Hashtable properties) {
        this.properties = properties;
    }

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

        while (running) {
            try {
                java.net.Socket socket = ssocket.accept();
                if (log.isDebugEnabled())
                    log.debug("Accepted connection: " + socket);
                SMTPHandler handler = new SMTPHandler(socket, properties, cloud);
                handler.start();
            } catch (Exception e) {
                log.error("Exception while accepting connections: " + e);
            }
        }
    }

    public void interrupt() {
        // Interrupted; this only happens when we are shutting down
        log.info("Interrupt() called");
        running = false;
        ssocket = null;
    }
}
