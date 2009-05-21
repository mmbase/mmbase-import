/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.mmbase.core.event.*;
import org.mmbase.core.util.DaemonThread;
import org.mmbase.module.core.*;
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
 * @author Ernst Bunders
 * @version $Id$
 */
public abstract class ClusterManager implements AllEventListener, Runnable {

    private static final Logger log = Logging.getLoggerInstance(ClusterManager.class);

    protected final Statistics receive = new Statistics();
    protected final Statistics send    = new Statistics();

    /** Queue with messages to send to other MMBase instances */
    protected BlockingQueue<byte[]> nodesToSend = new LinkedBlockingQueue<byte[]>(64);
    /** Queue with received messages from other MMBase instances */
    protected BlockingQueue<byte[]> nodesToSpawn = new LinkedBlockingQueue<byte[]>(64);

    /** Thread which processes the messages */
    protected Thread kicker = null;

    protected boolean spawnThreads = true;

    protected boolean compatible17 = false;

    public final void shutdown(){
        log.info("Shutting down clustering");
        stopCommunicationThreads();
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }

    protected void readConfiguration(Map<String,String> configuration) {
        String tmp = configuration.get("spawnthreads");
        if (tmp != null && !tmp.equals("")) {
            spawnThreads = !"false".equalsIgnoreCase(tmp);
        }
    }

    /**
     * Subclasses should start the communication threads in this method
     */
    protected abstract void startCommunicationThreads();

    /**
     * Subclasses should stop the communication threads in this method
     */
    protected abstract void stopCommunicationThreads();

    public void notify(Event event){
        //we only want to propagate the local events into the cluster
        if(event.getMachine().equals(MMBase.getMMBase().getMachineName())){
            byte[] message = createMessage(event);
            if (message != null) {
                if (message.length > 5000) {
                    log.warn("Sending large event to the cluster. Serialization of  " + event + " is " + message.length + " long!");
                } else {
                    log.debug("Sending an event to the cluster");
                }
                nodesToSend.offer(message);
            } else {
                log.debug("MEssage was null");
            }
        } else {
            log.trace("Ignoring remote event from " + event.getMachine() + " it will not be propagated");
        }
    }

    /**
     * Starts the Changer Thread.
     */
    protected void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new DaemonThread(this, "ClusterManager");
            kicker.start();
            try {
                kicker.setPriority(Thread.NORM_PRIORITY + 1);
            } catch (NullPointerException npe) {
                // MM:a NPE is thrown here sometimes if ThreadGroup of kicker is null.
                // which I saw happening on my jvm (ThreadGroep set to null by Thread.start).
                // I don't understand it, but it's not worth failing ClusterManager completely.
                log.warn("Could not set thread priority of Cluster Manager");
            }
            startCommunicationThreads();
        }
    }

    protected byte[] createMessage(Event event) {
        if (log.isDebugEnabled()) {
            log.debug("Serializing " + event);
        }
        try {
            long startTime = System.currentTimeMillis();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (compatible17) {
                if (event instanceof  NodeEvent || event instanceof RelationEvent) {
                    NodeEvent ne;
                    if (event instanceof RelationEvent) {
                        RelationEvent re = (RelationEvent) event;
                        ne = re.getNodeEvent();
                        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
                        byte[] rel1 = createMessage(re.getMachine(), re.getRelationSourceNumber(), re.getRelationSourceType(), "r").getBytes();
                        b1.write(rel1, 0, rel1.length);
                        b1.write(',');
                        b1.write(0);
                        nodesToSend.offer(b1.toByteArray());
                        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                        byte[] rel2 = createMessage(re.getMachine(), re.getRelationDestinationNumber(), re.getRelationDestinationType(), "r").getBytes();
                        b2.write(rel2, 0, rel2.length);
                        b2.write(',');
                        b2.write(0);
                        nodesToSend.offer(b2.toByteArray());
                    } else {
                        ne = (NodeEvent) event;
                    }
                    byte[] oldStyleEvent = createMessage(ne.getMachine(), ne.getNodeNumber(), ne.getBuilderName(), NodeEvent.newTypeToOldType(ne.getType())).getBytes();
                    bytes.write(oldStyleEvent, 0, oldStyleEvent.length);
                }
            }
            bytes.write(',');
            bytes.write(0);
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeObject(event);
            long cost = System.currentTimeMillis() - startTime;
            send.parseCost += cost;
            send.cost += cost;
            return bytes.toByteArray();
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            return null;
        }

    }

    /** Followup number of message */
    protected int follownr = 1;
    protected int lastRecievedMessage;

    /**
     * Creates MMBase 1.7 parseable message. This is simple String, which is prefixed before the actual 1.8 message.
     *
     * @param machine MMBase 'machine name'.
     * @param nodenr node number
     * @param tableName node type (tablename)
     * @param type command type
     * @return message
     */
    protected String createMessage(String machine, int nodenr, String tableName, String type) {
        return machine + ',' + (follownr++) + ',' + nodenr + ',' + tableName + ',' + type;
    }

    protected Event parseMessage(byte[] message) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(message);
            int c = 1;
            while (c > 0) {
                // ignore backwards compatibility message
                c = stream.read();
            }
            ObjectInputStream in = new ObjectInputStream(stream);
            Event event = (Event) in.readObject();
            if (log.isDebugEnabled()) {
                log.debug("Unserialized " + event);
            }
            return event;
        } catch (StreamCorruptedException scc) {
            // not sure that this can happen, now, because of the while(c>0) trick.
            log.debug(scc.getMessage() + ". Supposing old style message.");
            // Possibly, it is a message from an 1.7 system
            String mes = new String(message);
            NodeEvent event = parseMessageBackwardCompatible(mes);
            if (log.isDebugEnabled()) {
                log.debug("Old style message " + event);
            }
            return event;
        } catch (EOFException eofe) {
            // suppose that this is a 1.7 message
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
                String fnr = tok.nextToken();
                int newFollowNr = Integer.valueOf(fnr);
                int expectedFollowNr = lastRecievedMessage + 1;
                if (newFollowNr != expectedFollowNr) {
                    log.info("Expected message " + expectedFollowNr + ", but message " + newFollowNr + " was recieved ");
                }
                lastRecievedMessage = newFollowNr;
                if (tok.hasMoreTokens()) {
                    String id = tok.nextToken();
                    if (tok.hasMoreTokens()) {
                        String tb = tok.nextToken();
                        if (tok.hasMoreTokens()) {
                            String ctype = tok.nextToken();
                            if (!ctype.equals("s")) {
                                MMBase mmbase = MMBase.getMMBase();
                                MMObjectBuilder builder = mmbase.getBuilder(tb);
                                if (builder == null) builder = mmbase.getBuilder("object");
                                MMObjectNode node = builder.getNode(id);
                                if (node != null) {
                                    return new NodeEvent(machine, tb, node.getNumber(), node.getOldValues(), node.getValues(), NodeEvent.oldTypeToNewType(ctype));
                                } else {
                                    try {
                                        return new NodeEvent(machine, tb, Integer.valueOf(id).intValue(), null, null, NodeEvent.oldTypeToNewType(ctype));
                                    } catch (NumberFormatException nfe) {
                                        log.error(message + ": colud not parse " + id + " to a node number.");
                                    }
                                }
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
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while(kicker != null) {
            try {
                byte[] message = nodesToSpawn.take();
                if (message == null) continue;
                long startTime = System.currentTimeMillis();
                if (log.isDebugEnabled()) {
                    log.trace("RECEIVED =>" + message.length + " bytes");
                }
                receive.count++;
                receive.bytes += message.length;
                Event event = parseMessage(message);
                receive.parseCost += (System.currentTimeMillis() - startTime);
                if (event != null) {
                    handleEvent(event);
                } else {
                    log.warn("Could not handle event, it is null");
                }
                receive.cost += (System.currentTimeMillis() - startTime);
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            } catch(Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
    }

    /**
     * @javadoc
     * @param event
     */
    protected void handleEvent(final Event event) {
        // check if MMBase is 100% up and running, if not eat event
        MMBase mmbase = MMBase.getMMBase();
        if (mmbase == null || !mmbase.getState()) {
            if (log.isDebugEnabled()) {
                log.debug("Ignoring event " + event + ", mmbase is not up " + mmbase);
            }
            return;
        }
        if (mmbase.getMachineName().equals(event.getMachine())) {
            // ignore changes of ourselves
            if (log.isDebugEnabled()) {
                log.debug("Ignoring event " + event + " it is from this (" + event.getMachine() + ") mmbase");
            }
            return;
        }

        if (event instanceof NodeEvent) {
            MMObjectBuilder builder = mmbase.getBuilder(((NodeEvent) event).getBuilderName());
            if (builder != null && (! builder.broadcastChanges())) {
                log.info("Ignoring node-event for node type " + builder + " because broad cast changes is false");
                return;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Handling event " + event + " for " + event.getMachine());
        }

        if (spawnThreads) {
            Runnable job = new Runnable () {
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        EventManager.getInstance().propagateEvent(event);
                        receive.cost += (System.currentTimeMillis() - startTime);
                    }
                };
            org.mmbase.util.ThreadPools.jobsExecutor.execute(job);
        } else {
            try {
                EventManager.getInstance().propagateEvent(event);
            } catch (Throwable t) {
                log.error("Exception during propagation of event: " + event + ": " + t.getMessage(), t);
            }
        }
    }

}
