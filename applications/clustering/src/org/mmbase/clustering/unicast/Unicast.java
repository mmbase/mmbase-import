/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.util.Map;

import org.mmbase.clustering.ClusterManager;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.UtilReader;


/**
 * Unicast implementation for the mmbase cluster. This implemenetation
 * opens a direct network connection to each host mentioned in the mmservers
 * as active.
 *
 * @author Nico Klasens
 * @version $Id: Unicast.java,v 1.14 2008-07-29 20:56:18 michiel Exp $
 */
public class Unicast extends ClusterManager {

    private static final Logger log = Logging.getLoggerInstance(Unicast.class);

    public static final String CONFIG_FILE = "unicast.xml";

    /** Host on which the server socket is bound.*/
    private String unicastHost;

    /** Port on which the talking between nodes take place.*/
    private int unicastPort = 4243;

    /** Timeout of the connection.*/
    private int unicastTimeout = 10 * 1000;


    /** Sender which reads the nodesToSend Queue amd puts the message on the line */
    private ChangesSender ucs;
    /** Receiver which reads the message from the line and puts message in the nodesToSpawn Queue */
    private ChangesReceiver ucr;

    /**
     * @since MMBase-1.8.1
     */
    private final UtilReader reader = new UtilReader(CONFIG_FILE,
                                                     new Runnable() {
                                                         public void run() {
                                                             synchronized(Unicast.this) {
                                                                 stopCommunicationThreads();
                                                                 readConfiguration(reader.getProperties());
                                                                 startCommunicationThreads();
                                                             }
                                                         }
                                                     });



    public Unicast(){
        readConfiguration(reader.getProperties());
        start();
    }

    protected synchronized void readConfiguration(Map<String,String> configuration) {
        super.readConfiguration(configuration);

        String tmp = configuration.get("unicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }
        tmp = configuration.get(org.mmbase.module.core.MMBase.getMMBase().getMachineName() + ".unicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        unicastHost = MMBase.getMMBase().getHost();

        String tmpHost = configuration.get("unicasthost");
        if (tmpHost != null && !tmpHost.equals("")) {
            unicastHost = tmpHost;
        }
        tmpHost = configuration.get(org.mmbase.module.core.MMBase.getMMBase().getMachineName() + ".unicasthost");
        if (tmpHost != null && !tmpHost.equals("")) {
            unicastHost = tmpHost;
        }

        String tmpTimeout = configuration.get("unicasttimeout");
        if (tmpTimeout != null && !tmpTimeout.equals("")) {
            try {
                unicastTimeout = Integer.parseInt(tmpTimeout);
            } catch (Exception e) {}
        }

        log.info("unicast host: "    + unicastHost);
        log.info("unicast port: "    + unicastPort);
        log.info("unicast timeout: " + unicastTimeout);

    }

    /**
     * @see org.mmbase.clustering.ClusterManager#startCommunicationThreads()
     */
    protected synchronized void startCommunicationThreads() {
        if (unicastPort == -1) {
            log.service("Not starting unicast threads because port number configured to be -1");
        } else {
            ucs = new ChangesSender(reader.getProperties(), unicastPort, unicastTimeout, nodesToSend, send);
            try {
                ucr = new ChangesReceiver(unicastHost, unicastPort, nodesToSpawn);
            } catch (java.io.IOException ioe) {
                log.error(ioe);
            }
        }
    }

    /**
     * @see org.mmbase.clustering.ClusterManager#stopCommunicationThreads()
     */
    protected synchronized void stopCommunicationThreads() {
        if (ucs != null) {
            ucs.stop();
            log.service("Stopped communication sender " + ucs);
            ucs = null;
        }
        if (ucr != null) {
            ucr.stop();
            log.service("Stopped communication receiver " + ucr);
            ucr = null;
        }
    }


    public String toString() {
        return "UniCast ClusterManager";
    }

}
