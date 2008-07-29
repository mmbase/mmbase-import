/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.multicast;

import java.util.Map;

import org.mmbase.clustering.ClusterManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.UtilReader;

/**
 * Multicast is a thread object that reads the receive queue
 * and spawns them to call the objects (listeners) who need to know.
 * The Multicast start two threads to handle the sending and receiving of
 * multicast messages.
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: Multicast.java,v 1.15 2008-07-29 20:56:18 michiel Exp $
 */
public class Multicast extends ClusterManager {

    private static final Logger log = Logging.getLoggerInstance(Multicast.class);

    public static final String CONFIG_FILE = "multicast.xml";

    /**
     * Defines what 'channel' we are talking to when using multicast.
     */
    private String multicastHost = "ALL-SYSTEMS.MCAST.NET";

    /**
     * Determines on what port does this multicast talking between nodes take place.
     * This can be set to any port but check if something else on
     * your network is allready using multicast when you have problems.
     */
    private int multicastPort = 4243;

    /** Determines the Time To Live for a multicast datapacket */
    private int multicastTTL = 1;

    /** Datapacket receive size */
    private int dpsize = 64 * 1024;

    /** Sender which reads the nodesToSend Queue amd puts the message on the line */
    private ChangesSender mcs;
    /** Receiver which reads the message from the line and puts message in the nodesToSpawn Queue */
    private ChangesReceiver mcr;

    /**
     * @since MMBase-1.8.1
     */
    private final UtilReader reader = new UtilReader(CONFIG_FILE,
                                                            new Runnable() {
                                                                public void run() {
                                                                    synchronized(Multicast.this) {
                                                                        stopCommunicationThreads();
                                                                        readConfiguration(reader.getProperties());
                                                                        startCommunicationThreads();
                                                                    }
                                                                }
                                                            });

    /**
     */
    public Multicast() {
        readConfiguration(reader.getProperties());
        start();
    }

    /**
     * Read configuration settings
     * @param configuration read from config resource
     * @since MMBase-1.8.1
     */
    protected synchronized void readConfiguration(Map<String,String> configuration) {
        super.readConfiguration(configuration);

        String tmp = configuration.get("multicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                multicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        tmp = configuration.get("multicasthost");
        if (tmp != null && !tmp.equals("")) {
            multicastHost = tmp;
        }

        tmp = configuration.get("multicastTTL");
        if (tmp != null && !tmp.equals("")) {
            try {
                multicastTTL = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        tmp = configuration.get("dpsize");
        if (tmp != null && !tmp.equals("")) {
            try {
                dpsize = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        log.info("multicast host: " + multicastHost +
                 ", port: " + multicastPort +
                 ", TTL: " + multicastTTL +
                 ", datapacketsize: " + dpsize);
    }

    protected synchronized void startCommunicationThreads() {
        if (multicastPort == -1) {
            log.service("Not starting multicast threads because port number configured to be -1");
        } else {
            try {
                mcs = new ChangesSender(multicastHost, multicastPort, multicastTTL, nodesToSend, send);
            } catch (java.net.UnknownHostException e) {
                log.error(e);
            }
            try {
                mcr = new ChangesReceiver(multicastHost, multicastPort, dpsize, nodesToSpawn);
            } catch (java.net.UnknownHostException e) {
                log.error(e);
            }
        }
    }

    protected synchronized void stopCommunicationThreads() {
        if (mcs != null) {
            mcs.stop();
            log.service("Stopped communication sender " + mcs);
            mcs = null;
        }
        if (mcr != null) {
            mcr.stop();
            log.service("Stopped communication receiver " + mcr);
            mcr = null;
        }
    }

    public String toString() {
        return "MultiCast ClusterManager";
    }

}
