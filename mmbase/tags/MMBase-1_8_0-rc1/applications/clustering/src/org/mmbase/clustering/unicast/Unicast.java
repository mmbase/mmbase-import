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
 * @version $Id: Unicast.java,v 1.6 2006-01-02 14:33:37 nklasens Exp $
 */
public class Unicast extends ClusterManager {

    private static final Logger log = Logging.getLoggerInstance(Unicast.class);

    public static final String CONFIG_FILE = "unicast.xml";

    /** Port on which the talking between nodes take place.*/
    private int unicastPort = 4243;

    /** Timeout of the connection.*/
    private int unicastTimeout = 10*1000;

    /** Sender which reads the nodesToSend Queue amd puts the message on the line */
    private ChangesSender ucs;
    /** Receiver which reads the message from the line and puts message in the nodesToSpawn Queue */
    private ChangesReceiver ucr;


    public Unicast(){
        
        UtilReader reader = new UtilReader(CONFIG_FILE);
        Map properties = reader.getProperties();

        String tmp = (String) properties.get("spawnthreads");
        if (tmp != null && !tmp.equals("")) {
            spawnThreads = !"false".equalsIgnoreCase(tmp);
        }

        tmp = (String) properties.get("unicastport");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastPort = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        tmp = (String) properties.get("unicasttimeout");
        if (tmp != null && !tmp.equals("")) {
            try {
                unicastTimeout = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }

        log.info("unicastport: " + unicastPort);
        log.info("unicasttimeout: " + unicastTimeout);

        start();
    }

    /**
     * @see org.mmbase.clustering.ClusterManager#startCommunicationThreads()
     */
    protected void startCommunicationThreads() {
        ucs = new ChangesSender(unicastPort, unicastTimeout, nodesToSend);
        ucr = new ChangesReceiver(unicastPort, nodesToSpawn);
    }

    /**
     * @see org.mmbase.clustering.ClusterManager#stopCommunicationThreads()
     */
    protected void stopCommunicationThreads() {
        ucs.stop();
        ucr.stop();
    }

    // javadoc inherited
    public void changedNode(NodeEvent event) {
        byte[] message = createMessage(event);
        nodesToSend.append(message);
        //Multicast receives his own message. Unicast now too.
        nodesToSpawn.append(message);

        log.debug("message: " + event);
        return;
    }

}
