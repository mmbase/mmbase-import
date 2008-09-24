/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.util.Map;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.clustering.ClusterManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.UtilReader;


/**
 * @javadoc
 *
 * @author Nico Klasens
 * @version $Id: Unicast.java,v 1.9.2.2 2008-07-28 11:19:45 michiel Exp $
 */
public class Unicast extends ClusterManager {

    private static final Logger log = Logging.getLoggerInstance(Unicast.class);

    public static final String CONFIG_FILE = "unicast.xml";

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

    protected synchronized void readConfiguration(Map configuration) {
        super.readConfiguration(configuration);

        String tmp = (String) configuration.get("unicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }
        tmp = (String) configuration.get(org.mmbase.module.core.MMBase.getMMBase().getMachineName() + ".unicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        tmp = (String) configuration.get("unicasttimeout");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastTimeout = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        log.info("unicast port: "    + unicastPort);
        log.info("unicast timeout: " + unicastTimeout);

    }

    /**
     * @see org.mmbase.clustering.ClusterManager#startCommunicationThreads()
     */
    protected synchronized void startCommunicationThreads() {
        if (unicastPort == -1) {
            log.service("Not starting multicast threads because port number configured to be -1");
        } else {
            ucs = new ChangesSender(reader.getProperties(), unicastPort, unicastTimeout, nodesToSend, send);
            try {
                ucr = new ChangesReceiver(unicastPort, nodesToSpawn);
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

    // javadoc inherited
    public void changedNode(NodeEvent event) {
        byte[] message = createMessage(event);
        nodesToSend.append(message);
        //Multicast receives his own message. Unicast now too.
        nodesToSpawn.append(message);
        if (log.isDebugEnabled()) {
            log.debug("message: " + event);
        }
        return;
    }

}
