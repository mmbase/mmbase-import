/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core.change;

import java.util.Map;

import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.UtilReader;


/**
 * @javadoc
 * 
 * @author Nico Klasens
 * @created 20-sep-2004
 * @version $Id: Unicast.java,v 1.1.2.1 2004-10-09 10:51:43 nico Exp $
 */
public class Unicast extends SharedStorage {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(Unicast.class.getName());
    
    public static final String CONFIG_FILE = "unicast.xml";
    
    /** Port on which the talking between nodes take place.*/
    private int unicastPort = 4243;
    
    /** Timeout of the connection.*/
    private int unicastTimeout = 10*1000;
    
    /** Sender which reads the nodesToSend Queue amd puts the message on the line */
    private UnicastChangesSender ucs;
    /** Receiver which reads the message from the line and puts message in the nodesToSpawn Queue */
    private UnicastChangesReceiver ucr;
    
    /**
     * @see org.mmbase.module.core.MMBaseChangeInterface#init(org.mmbase.module.core.MMBase)
     */
    public void init(MMBase mmb) {
        super.init(mmb);
        
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
     * @see org.mmbase.module.core.change.SharedStorage#startCommunicationThreads()
     */
    protected void startCommunicationThreads() {
        ucs = new UnicastChangesSender(unicastPort, unicastTimeout, nodesToSend, mmbase);
        ucr = new UnicastChangesReceiver(unicastPort, nodesToSpawn);
    }

    /**
     * @see org.mmbase.module.core.change.SharedStorage#stopCommunicationThreads()
     */
    protected void stopCommunicationThreads() {
        ucs.stop();
        ucr.stop(); 
    }

    /**
     * @see org.mmbase.module.core.MMBaseChangeInterface#changedNode(int, java.lang.String, java.lang.String)
     */
    public boolean changedNode(int nodenr, String tableName, String type) {
        String message = createMessage(nodenr, tableName, type);
        nodesToSend.append(message);
        //Multicast receives his own message. Unicast now too.
        nodesToSpawn.append(message);
        
        log.debug("message: " + message);
        
        return true;
    }
}
