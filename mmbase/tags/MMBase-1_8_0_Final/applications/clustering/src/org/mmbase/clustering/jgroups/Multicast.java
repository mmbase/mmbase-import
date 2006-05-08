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
 * @version $Id: Multicast.java,v 1.4 2006-01-02 14:33:56 nklasens Exp $
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
     * eachother.  If there are different clouds in a network which should not
     * communicate, this name should be different for each group of clouds
     * communicating with eachother.
     * */
    private String channelName;

    /**
     * @see org.mmbase.module.core.MMBaseChangeInterface#init(org.mmbase.module.core.MMBase)
     */
    
    public Multicast() {
        
        UtilReader reader = new UtilReader(CONFIG_FILE);
        Map properties = reader.getProperties();

        String tmp = (String) properties.get("spawnthreads");
        if (tmp != null && !tmp.equals("")) {
            spawnThreads = !"false".equalsIgnoreCase(tmp);
        }

        tmp = (String) properties.get("channelproperties");
        if (tmp != null && !tmp.equals("")) {
            channelProperties = tmp;
        } else {
            log.error("No channel properties found"); 
        }

        tmp = (String) properties.get("channelname");
        if (tmp != null && !tmp.equals("")) {
            channelName = tmp;
        }

        /** 
         * We need to strip out white space characters from the
         * channelproperties string before we pass it onto new JChannel().
         */
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(channelProperties);
        channelProperties = m.replaceAll( "" );

        try {
            channel = new JChannel(channelProperties);
            channel.connect(channelName);
        } catch (ChannelException createChannelException) {
            log.error("JChannel: Unable to create or join multicast channel: " + Logging.stackTrace(createChannelException));
        }

        /**
         * Have the communication threads stop and disconnect when this object 
         * is garbage collected. There is no neat method of disconnecting otherwise.
         * */
        Runtime.getRuntime().addShutdownHook( 
            new Thread() {
                public void run() {
                    log.warn("Channel is going to be disconnected due to a virtual machine shutdown.");
                    channel.disconnect();
                }
            }
        );

        log.info("Joining channel: " + channelName);
        start();
    }
    
    /** Starts the ChangesSender and MulticastChangerReciever threads,
     * which handle the sending and recieving of messages on the channel in
     * seaparate threads.
     */
    protected void startCommunicationThreads() {
        mcs = new ChangesSender(channel, nodesToSend);
        mcr = new ChangesReceiver(channel, nodesToSpawn);
    }

    /** @TODO: This method _should_ be called when mmbase is shut down. This
     * method implements abstract method stopCommunicationThreads in
     * org.mmbase.clustering.jgroups.SharedStorage, but is never called during
     * shutdown. The channel.disconnect() is currently called during garbage
     * collection of this object. If in future versions of MMBase this method
     * is called, please remove the shutdownHook in the constructor.
     */
    protected void stopCommunicationThreads() {
        mcs.stop();
        mcr.stop();
        channel.disconnect();
    }
}
