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
 * @version $Id: ClusterManager.java,v 1.8 2005-09-20 19:31:27 michiel Exp $
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
    public void start() {
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
    public void stop() {
        stopCommunicationThreads();
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }
    
    
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
            // do do, need to somehow recognise message from MMBase < 1.8
            // and then call paseMessageBackwardCompatible
            NodeEvent event = (NodeEvent) in.readObject();
            if (log.isDebugEnabled()) {
                log.debug("Unserialized " + event);
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
        String chars=(String)nodesToSpawn.get();
        String machine,vnr,id,tb,ctype;
        StringTokenizer tok;
        
        if (log.isDebugEnabled()) {
            log.debug("RECEIVE=>" + chars);
        }
        tok=new StringTokenizer(chars,",");
        if (tok.hasMoreTokens()) {
            machine=tok.nextToken();
            if (tok.hasMoreTokens()) {
                    vnr=tok.nextToken();
                    if (tok.hasMoreTokens()) {
                        id=tok.nextToken();
                        if (tok.hasMoreTokens()) {
                            tb=tok.nextToken();
                            if (tok.hasMoreTokens()) {
                                ctype=tok.nextToken();
                                if (!ctype.equals("s")) {
                                    //return new NodeEvent();
                                    //handleMsg(machine,vnr,id,tb,ctype);
                                } else {
                                    if (tok.hasMoreTokens()) {
                                        String xml=tok.nextToken("");
                                        //return new NodeEvent();a
                                        //commitXML(machine,vnr,id,tb,ctype,xml);
                                    } else log.error("doWork("+chars+"): 'xml' could not be extracted from this string!");
                                }
                            } else log.error("doWork("+chars+"): 'ctype' could not be extracted from this string!");
                        } else log.error("doWork("+chars+"): 'tb' could not be extracted from this string!");
                    } else log.error("doWork("+chars+"): 'id' could not be extracted from this string!");
                } else log.error("doWork("+chars+"): 'vnr' could not be extracted from this string!");
        } else log.error("doWork("+chars+"): 'machine' could not be extracted from this string!");        
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
        try {
            kicker.setPriority(Thread.NORM_PRIORITY+1);
            doWork();
        } catch(Exception e) {
            log.error(e);
        }
    }
    
    /**
     * Let the thread do his work
     */
    private void doWork() {
        while(kicker != null) {
            byte[] message = (byte[]) nodesToSpawn.get();
            if (log.isDebugEnabled()) {
                log.debug("RECEIVE=>" + new String(message));
            }
            spawncount++;
            NodeEvent event = parseMessage(message);
            handleEvent(event);
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
