/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.jgroups;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.mmbase.clustering.ClusterManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.UtilReader;

import org.jgroups.*;

/**
 * Multicast is a thread object that reads the receive queue and spawns them to
 * call the objects (listeners) who need to know. The Multicast start two
 * threads to handle the sending and receiving of multicast messages.  This is
 * the version that uses JavaGroups (JGroups) to ensure reliable delivery of
 * multicast messages.
 *
 * See <a href="http://www.jgroups.org/">http://www.jgroups.org/</a> for more
 * information on JGroups.
 *
 * @see org.mmbase.clustering.jgroups.ChangesSender
 * @see org.mmbase.clustering.jgroups.ChangesReceiver
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @author Costyn van Dongen
 * @author Ronald Wildenberg
 * @version $Id: Multicast.java,v 1.12 2008-07-29 20:56:18 michiel Exp $
 */
public class Multicast extends ClusterManager {

    private static final Logger log = Logging.getLoggerInstance(Multicast.class);

    /**
     * Field containing the configuration file with the various options that
     * can be specified for configuring the JGroups channel
     * */
    public static final String CONFIG_FILE = "multicastJG.xml";

    /**
     * Sender which reads the nodesToSend Queue amd puts the message on the
     * line
     * */
    private ChangesSender mcs;

    /**
     * Receiver which reads the message from the line and puts message in the
     * nodesToSpawn Queue
     * */
    private ChangesReceiver mcr;

    /**
     * JChannel which the ChangesReceiver and ChangesSender
     * use to communicate with other instances
     * */
    private JChannel channel;

    /**
     * channelproperties A string specifying the properties of the JChannel
     * protocol stack.
     * */
    private String channelProperties;

    /**
     * Name which the various MMBase instances use to communicate with
     * each other.  If there are different clouds in a network which should not
     * communicate, this name should be different for each group of clouds
     * communicating with each other.
     * */
    private String channelName;

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

        String tmp = configuration.get("channelproperties");
        if (tmp != null && !tmp.equals("")) {
            channelProperties = tmp;
        } else {
            log.error("No channel properties found");
        }

        tmp = configuration.get("channelname");
        if (tmp != null && !tmp.equals("")) {
            channelName = tmp;
        }

        /**
         * We need to strip out white space characters from the
         * channelproperties string before we pass it onto new JChannel().
         */
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(channelProperties);
        channelProperties = m.replaceAll("");

        try {
            if (channel != null) channel.disconnect();
            channel = new JChannel(channelProperties);
            channel.connect(channelName);
        } catch (ChannelException createChannelException) {
            log.error("JChannel: Unable to create or join multicast channel: " + createChannelException.getMessage(), createChannelException);
            if (channel == null) {
                return;
            }
        }

        if (channel.isConnected()) {
            log.info("Joining channel: " + channel.toString(true));
        } else {
            log.warn("Could not connect channel: " + channel.toString(true));
        }
    }

    /**
     * Starts the ChangesSender and MulticastChangerReciever threads,
     * which handle the sending and recieving of messages on the channel in
     * seaparate threads.
     */
    protected  synchronized void startCommunicationThreads() {
        mcs = new ChangesSender(channel, nodesToSend, send);
        log.service("Started communication sender " + mcs);
        mcr = new ChangesReceiver(channel, nodesToSpawn);
        log.service("Started communication receiver " + mcr);
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
        if (channel != null) {
            log.service("Disconnecting jgroup channel " + channel.toString(true));
            channel.disconnect();
            channel = null;
        }
    }

    public String toString() {
        return "JGroups ClusterManager";
    }
}
