/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;


import java.util.*;
import java.io.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.util.Queue;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



/**
 * ClusterManager is a thread object that reads the receive queue
 * and calls the objects (listeners) who need to know.
 * The ClusterManager starts communication threads to handle the sending 
 * and receiving of messages.
 *  
 * @author Nico Klasens
 * @author Michiel Meeuwissen
 * @version $Id: ClusterManager.java,v 1.10 2005-09-26 11:46:37 michiel Exp $
 */
public abstract class ClusterManager implements Runnable, MMBaseChangeInterface {

    private static final Logger log = Logging.getLoggerInstance(ClusterManager.class);

    /** Followup number of message */
    protected int follownr = 1;
    /** Number of processed messages */
    protected int spawncount = 0;

    /** Collections of nodes a thread is waiting on for change 
     * @todo should be Set.
     */
    protected Vector waitingNodes = new Vector(); 
    /** Queue with messages to send to other MMBase instances */
    protected Queue nodesToSend = new Queue(64);
    /** Queue with received messages from other MMBase instances */
    protected Queue nodesToSpawn = new Queue(64);

    /** Thread which processes the messages */
    private Thread kicker = null;

    /** MMBase instance */
    protected MMBase mmbase = null;

    protected boolean spawnThreads = true; 
    
    /**
     * @javadoc
     */
    public void init(MMBase mmb) {
        this.mmbase = mmb;
    }
    
    public MMBase getMMBase() {
        return mmbase;
    }
    /**
     * Subclasses should start the communication threads in this method
     */
    protected abstract void startCommunicationThreads();
    /**
     * Subclasses should stop the communication threads in this method
     */
    protected abstract void stopCommunicationThreads();

    
    /**
     * Starts the Changer Thread.
     */
    protected void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this, "ClusterManager");
            kicker.setDaemon(true);
            kicker.start();
            startCommunicationThreads();
        }
    }
    /**
     * Stops the ClusterManager.
     */
    /*
    public void stop() {
        stopCommunicationThreads();
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }
    */
    
    
    // javadoc inherited
    public void changedNode(NodeEvent event) {
        byte[] message = createMessage(event);
        nodesToSend.append(message);
        return;
    }

    protected byte[] createMessage(NodeEvent nodeEvent) {
        if (log.isDebugEnabled()) {
            log.debug("Serializing " + nodeEvent);
        }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeObject(nodeEvent);
            return bytes.toByteArray();
        } catch (IOException ioe) {
            log.error(ioe);
            return null;
        }
        
    }
    protected NodeEvent parseMessage(byte[] message) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(message));
            NodeEvent event = (NodeEvent) in.readObject();
            if (log.isDebugEnabled()) {
                log.debug("Unserialized " + event);
            }
            return event;
        } catch (StreamCorruptedException scc) {
            log.debug(scc.getMessage() + ". Supposing old style message.");
            // Possibly, it is a message from an 1.7 system
            String mes = new String(message);
            NodeEvent event = parseMessageBackwardCompatible(mes);
            if (log.isDebugEnabled()) {
                log.debug("Old style message " + event);
            }
            return event;
        } catch (IOException ioe) {
            log.error(ioe);
            return null;
        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            return null;
        }
    }

    protected NodeEvent parseMessageBackwardCompatible(String message) {
        if (log.isDebugEnabled()) {
            log.debug("RECEIVE=>" + message);
        }
        StringTokenizer tok = new StringTokenizer(message,",");
        if (tok.hasMoreTokens()) {
            String machine = tok.nextToken();
            if (tok.hasMoreTokens()) {
                String vnr = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String id = tok.nextToken();
                    if (tok.hasMoreTokens()) {
                        String tb = tok.nextToken();
                        if (tok.hasMoreTokens()) {
                            String ctype=tok.nextToken();
                            if (!ctype.equals("s")) {
                                MMObjectBuilder builder = mmbase.getBuilder(tb);
                                MMObjectNode    node    = builder.getNode(id);
                                return new NodeEvent(node, NodeEvent.oldTypeToNewType(ctype),machine);
                            } else {
                                /// XXXX should we?
                                log.error("XML messages not suppported any more");
                            }
                        } else log.error(message + ": 'ctype' could not be extracted from this string!");
                    } else log.error(message + ": 'tb' could not be extracted from this string!");
                } else log.error(message + ": 'id' could not be extracted from this string!");
            } else log.error(message + ": 'vnr' could not be extracted from this string!");
        } else log.error(message + ": 'machine' could not be extracted from this string!");        
        return null;
    }

    
    /**
     * @javadoc
     */
    public boolean waitUntilNodeChanged(MMObjectNode node) {
        try {
            WaitNode wnode = new WaitNode(node);
            waitingNodes.add(wnode);
            wnode.doWait(60*1000);
            waitingNodes.remove(wnode);
        } catch(Exception e) {
            log.error(e);
        }
        return true;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        kicker.setPriority(Thread.NORM_PRIORITY+1);
        doWork();
    }
    
    /**
     * Let the thread do his work
     */
    private void doWork() {
        while(kicker != null) {
            try {
                byte[] message = (byte[]) nodesToSpawn.get();
                if (log.isDebugEnabled()) {
                    log.debug("RECEIVED =>" + message.length + " bytes");
                }
                spawncount++;
                NodeEvent event = parseMessage(message);
                if (event != null) {
                    handleEvent(event);
                } else {
                    log.warn("Could not handle message, it is null");
                }
            } catch(Throwable t) {
                log.error(t);
            }
        }

    }

    /**
     * Handle message
     * 
     * @param event NodeEvent
     */
    protected void handleEvent(NodeEvent event) {
        // check if MMBase is 100% up and running, if not eat event
        if (!mmbase.getState()) return;

        boolean remote = ! mmbase.getMachineName().equals(event.getMachine());
        MessageProbe probe = new MessageProbe(this, event, remote);
        if (spawnThreads || ! remote) {
            probe.run();
        } else {
            org.mmbase.util.ThreadPools.jobsExecutor.execute(probe);
        }
    }
    
    /**
     * Check collection of waiting nodes for the changed node
     * @param snumber changed node number
     */
    public void checkWaitingNodes(String snumber) {
        try {
            int number = Integer.parseInt(snumber);
            for (Enumeration e=waitingNodes.elements();e.hasMoreElements();) {
                WaitNode n = (WaitNode) e.nextElement();
                if (n.doNotifyCheck(number)) {
                    waitingNodes.removeElement(n);
                    log.debug("waitingNodes size=" + waitingNodes.size());
                }
            }
        } catch(Exception e) {
            log.error("not a valid number " + snumber);
        }
    }
    
}
