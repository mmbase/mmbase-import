/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import org.mmbase.clustering.Statistics;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.mmbase.util.ThreadPools;
import org.mmbase.module.builders.MMServers;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesSender is a thread object sending the nodes found in the
 * sending queue over unicast connections
 *
 * @author Nico Klasens
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class ChangesSender implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    private final Statistics send;

    private Future future = null;

    /** Queue with messages to send to other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSend;

    /** For the port on which the talking between nodes take place.*/
    private final Map<String,String> configuration;
    private final int defaultUnicastPort;

    /** Timeout of the connection.*/
    private final int unicastTimeout;

    /** last time the mmservers table was checked for active servers */
    private long lastServerChecked = -1;
    private List<MMObjectNode> activeServers = new ArrayList<MMObjectNode>();

    /** Interval of servers change their state */
    private long serverInterval;

    private int version = 1;
    private Iterable<OtherMachine> otherMachines = null;

    /**
     * Construct UniCast Sender
     * @param configuration configuration of unicast
     * @param unicastPort port of the unicast connections
     * @param unicastTimeout timeout on the connections
     * @param nodesToSend Queue of messages to send
     * @param send Statistics
     */
    public ChangesSender(Map<String,String> configuration, int unicastPort, int unicastTimeout, BlockingQueue<byte[]> nodesToSend, Statistics send, int version) {
        this.nodesToSend = nodesToSend;
        this.configuration = configuration;
        this.defaultUnicastPort = unicastPort;
        this.unicastTimeout = unicastTimeout;
        this.send = send;
        this.version = version;
        this.start();
    }

    /**
     * @since MMBase-2.0
     */
    protected void setOtherMachines(Iterable<OtherMachine> om) {
        this.otherMachines = om;
    }

    /**
     * @since MMBase-2.0
     */
    public void setOtherMachines(String s) {
        final List<OtherMachine> unicastSenders = new ArrayList<ChangesSender.OtherMachine>();
        String[] unicastHost = s.split(",");
        for (String unicastString : unicastHost) {
            if (unicastString.length() > 0) {
                String[] unicastSend = unicastString.split(":", 3);
                unicastSenders.add(new org.mmbase.clustering.unicast.ChangesSender.OtherMachine(unicastSend[0], unicastSend.length > 2 ? unicastSend[2] : null, Integer.parseInt(unicastSend[1]), 2));
            }
        }
        setOtherMachines(unicastSenders);
    }


    void start() {
        if (future == null) {
            future = ThreadPools.jobsExecutor.submit(this);
            ThreadPools.identify(future, "UnicastSender");
        }
    }

    void stop() {
        if (future != null) {
            try {
                future.cancel(true);
                future = null;
            } catch (Throwable t) {
            }
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }


    /**
     * @since MMBase-2.0
     */
    public static class OtherMachine {
        public final String host;
        public final String machineName;
        public final int unicastPort;
        public final int version;
        public OtherMachine(String host, String machineName, int unicastPort, int version) {
            this.host = host;
            this.machineName = machineName;
            this.unicastPort = unicastPort;
            this.version = version;

        }
        @Override
        public String toString() {
            return host + ":" + unicastPort + " (v:" + version + ")";
        }
    }

    // javadoc inherited
    public void run() {
        log.info("Unicast sending to " + getOtherMachines());
        while(true) {
            if (Thread.currentThread().isInterrupted()) break;
            try {
                List<byte[]> data = new ArrayList<byte[]>();
                data.add(nodesToSend.take()); // at least one
                if (version > 1) {
                    nodesToSend.drainTo(data);
                }
                long startTime = System.currentTimeMillis();
                log.debug("Send change to " + getOtherMachines());
                for (OtherMachine machine : getOtherMachines()) {
                    DataOutputStream os = null;
                    Socket socket = null;
                    try {
                        if (machine.version > 1) {
                            socket = new Socket();
                            socket.connect(new InetSocketAddress(machine.host, machine.unicastPort), unicastTimeout);
                            os = new DataOutputStream(socket.getOutputStream());
                            os.writeInt(data.size());
                            send.bytes += 4;
                            for (byte[] d : data) {
                                os.writeInt(d.length);
                                send.bytes += 4;
                                os.write(d, 0, d.length);
                                send.bytes += d.length;
                            }
                            os.flush();
                        } else {
                            for (byte[] d : data) {
                                socket = new Socket();
                                socket.connect(new InetSocketAddress(machine.host, machine.unicastPort), unicastTimeout);
                                os = new DataOutputStream(socket.getOutputStream());
                                os.write(d, 0, d.length);
                                send.bytes += d.length;
                                os.flush();
                            }
                        }

                        if (log.isDebugEnabled()) {
                            log.debug("SEND=>" + machine + " (" + data.size() + " events)");
                        }
                    } catch(SocketTimeoutException ste) {
                        int removed = remove(machine);
                        if (removed == 1) {
                            log.warn("Server timeout: " + machine + " " + ste + ". Removed from active server list.");
                        } else {
                            log.error("Server timeout: " + machine + " " + ste + ". Remove from active server list: " + removed);
                        }
                    } catch (ConnectException ce) {
                        log.warn("Connect exception: " + machine + " " + ce + ".");
                    } catch (IOException e) {
                        log.error("can't send message to " + machine + " " + e.getMessage() , e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e1) {
                            }
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e1) {
                            }
                        }
                    }
                }
                send.count++;
                send.cost += (System.currentTimeMillis() - startTime);

            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get Active server list
     * @return server list
     */
    private List<MMObjectNode> getActiveServers() {
        List<MMObjectNode> prevActiveServers = activeServers;
        if (serverInterval < 0) {
            MMBase mmbase = MMBase.getMMBase();
            MMServers mmservers = (MMServers) mmbase.getBuilder("mmservers");
            serverInterval = mmservers.getIntervalTime();
            activeServers = mmservers.getActiveServers();
            lastServerChecked = System.currentTimeMillis();
            log.info("Active servers: " + activeServers );
        } else {
            if (lastServerChecked + serverInterval < System.currentTimeMillis()) {
                MMBase mmbase = MMBase.getMMBase();
                MMServers mmservers = (MMServers) mmbase.getBuilder("mmservers");
                activeServers = mmservers.getActiveServers();
                lastServerChecked = System.currentTimeMillis();
                if (! activeServers.equals(prevActiveServers)) {
                    log.info("Active servers: " + activeServers + " " + prevActiveServers.size() + " -> " + activeServers.size());
                } else {
                    log.debug("Active servers: " + activeServers);
                }
            }
        }
        return activeServers;
    }

    /**
     * @since MMBase-2.0
     */
    protected int remove(OtherMachine remove) {
        if (otherMachines != null) {
        } else {
            Iterator<MMObjectNode> i = activeServers.iterator();
            while (i.hasNext()) {
                MMObjectNode node = i.next();
                String hostname    = node.getStringValue("host");
                String machinename = node.getStringValue("name");
                if (remove.host.equals(hostname) && remove.machineName.equals(machinename)) {
                    i.remove();
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * @since MMBase-2.0
     */
    protected Iterable<OtherMachine> getOtherMachines() {
        if (otherMachines != null) {
            return otherMachines;
        } else  {
            List<OtherMachine> result = new ArrayList<OtherMachine>();

            for (MMObjectNode node : getActiveServers()) {
                if (node != null) {
                    String hostname    = node.getStringValue("host");
                    String machinename = node.getStringValue("name");

                    int unicastPort = defaultUnicastPort;
                    int version = 1;
                    if (configuration != null) {
                        String specificPort = configuration.get(machinename + ".unicastport");
                        if (specificPort != null) {
                            unicastPort = Integer.parseInt(specificPort);
                        }
                        String specificVersion = configuration.get(machinename + ".version");
                        if (specificVersion != null) {
                            version = Integer.parseInt(specificVersion);
                        }
                    }
                    result.add(new OtherMachine(hostname, machinename, unicastPort, version));
                }
            }
            return result;
        }
    }
}
