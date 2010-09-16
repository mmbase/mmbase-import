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

import org.mmbase.module.builders.MMServers;
import org.mmbase.module.core.*;
import org.mmbase.util.ThreadPools;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesSender is a runnable object sending the nodes found in the sending queue over unicast connections. Using
 * {@link #start} (and {@link #stop}) it can run itself in a dedicated thread.
 *
 * @author Nico Klasens
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see Unicast
 */
public class ChangesSender implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    private final Statistics send;

    private Thread kicker = null;

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
    private long serverInterval = -1;

    private int version = 1;
    private Iterable<OtherMachine> otherMachines = null;

    private int collectTime = 0;
    private int collectCount = 100;

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
    }

    /**
     * @since MMBase-2.0
     */
    protected void setOtherMachines(Iterable<OtherMachine> om) {
        this.otherMachines = om;
    }

    /**
     * Sets the other machines (the 'peers') using a string to be parsed
     <pre>
     &lt;hostname&gt;:&lt;portnumber&gt;[:&lt;name&gt;[:&lt;version&gt;]],[&lt;hostname&gt;:&lt;portnumber&gt;[:&lt;name&gt;[:&lt;version&gt;]]...]
     </pre>
     * @since MMBase-2.0
     */
    public void setOtherMachines(String s) {
        final List<OtherMachine> unicastSenders = new ArrayList<ChangesSender.OtherMachine>();
        String[] unicastHost = s.split(",");
        for (String unicastString : unicastHost) {
            if (unicastString.length() > 0) {
                String[] unicastSend = unicastString.split(":", 4);
                unicastSenders.add(new org.mmbase.clustering.unicast.ChangesSender.OtherMachine(unicastSend[0],
                                                                                                unicastSend.length > 2 ? unicastSend[2] : null,
                                                                                                Integer.parseInt(unicastSend[1]),
                                                                                                unicastSend.length > 3 ? Integer.parseInt(unicastSend[3]) : 2));
            }
        }
        setOtherMachines(unicastSenders);
    }

    /**
     * The maximum time (in second) to collect events before starting
     * to send (only if version >= 2)
     * @since MMBase-2.0
     */
    public void setCollectTime(int ct) {
        collectTime = ct;
    }
    /**
     * The maximum number of events to collect before starting
     * to send (only if version >= 2)
     * @since MMBase-2.0
     */
    public void setCollectCount(int cc) {
        collectCount = cc;
    }



    public void start() {
        if (kicker == null) {
            kicker = new Thread(ThreadPools.threadGroup, this, "UnicastSender");
            kicker.setDaemon(true);
            kicker.start();
        }
        log.trace("Submitted " + kicker);

    }

    void stop() {
        if (kicker != null) {
            try {
                kicker.interrupt();
                kicker.setPriority(Thread.MIN_PRIORITY);
                kicker = null;
            } catch (Throwable t) {
            }
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }


    /**
     * On object to represent a peer in unicast.
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


    /**
     * Writes a number of messages to an output stream, according to 'version 2'protocol of unicast (supporting multiple
     * messages per session)
     * The encoding is as such. using a DataOutputStream:
     <pre>
     &lt;number of message&gt;&lt;size of first message&gt;&lt;first message&gt;&lt;size second message&gt;&lt;second message&gt;....
     </pre>
     * @param out The stream to write the messages to
     * @param data The messages to write.
     * @since MMBase-2.0
    */
    protected void writeVersion2(java.io.OutputStream out, Collection<byte[]> data) throws IOException {
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(out);
            os.writeInt(data.size());
            send.bytes += 4;
            for (byte[] d : data) {
                os.writeInt(d.length);
                send.bytes += 4;
                os.write(d, 0, d.length);
                send.bytes += d.length;
            }
            os.flush();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * Sends a collection of messages to the given address. So, it opens a socket and uses {@link #writeVersion2} to
     * send the messages
     * @param address The address to send to.
     * @param data The message to send
     * @since MMBase-2.0
     */
    protected void sendVersion2(InetSocketAddress address, Collection<byte[]> data)  throws IOException {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(address, unicastTimeout);
            writeVersion2(socket.getOutputStream(), data);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    /**
     * Simply writes one message to an output stream. Version 1 does not support multiple messages.
     * @since MMBase-2.0
     */
    protected void writeVersion1(java.io.OutputStream out, byte[] d) throws IOException {
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(out);
            os.write(d, 0, d.length);
            send.bytes += d.length;
            os.flush();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    /**
     * Sends a collection of messages to the given address. It uses {@link #writeVersion1} to
     * send the messages. The version 1 protocol supports only one message per session, so this method opens a new
     * connection for every message in the given collection.
     *
     * @param address The address to send to.
     * @param data The message to send
     * @since MMBase-2.0
     */
    protected void sendVersion1(InetSocketAddress address, Collection<byte[]> data) throws IOException {
        for (byte[] d : data) {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(address, unicastTimeout);
                writeVersion1(socket.getOutputStream(), d);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        log.info("Unicast sending to " + getOtherMachines());
        long lastTime = System.currentTimeMillis();
        while(true) {
            if (Thread.currentThread().isInterrupted()) break;

            try {
                List<byte[]> data = new ArrayList<byte[]>();

                data.add(nodesToSend.take()); // at least one
                if (version > 1) {
                    // we can collect more messages
                    if (collectTime > 0) {
                        // wait a bit until more become available
                        long leftTime = lastTime + (1000L * collectTime) - System.currentTimeMillis();
                        while (leftTime > 0 && data.size() < collectCount) {
                            byte[] next = nodesToSend.poll(collectTime, TimeUnit.MILLISECONDS);
                            if (next != null) {
                                data.add(next);
                            }
                            leftTime = lastTime + (1000L * collectTime) - System.currentTimeMillis();
                        }
                    }
                    // collect everthing what's left too (if collectTime was indicated, that would probably be none)
                    nodesToSend.drainTo(data);
                }
                long startTime = System.currentTimeMillis();
                lastTime = startTime;
                if (log.isTraceEnabled()) {
                    log.trace("Send " + data.size() + " changes to " + getOtherMachines());
                }
                // Now send all messages to all peers
                for (OtherMachine machine : getOtherMachines()) {
                    try {
                        final InetSocketAddress address = new InetSocketAddress(machine.host, machine.unicastPort);
                        if (machine.version > 1) {
                            sendVersion2(address, data);
                        } else {
                            sendVersion1(address, data);
                        }

                        if (log.isDebugEnabled()) {
                            log.debug("unicast(v" + version + ") SEND=>" + machine + " (" + data.size() + " events, " + send.bytes + " byte)");
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
                    }
                }
                send.count++;
                send.cost += (System.currentTimeMillis() - startTime);
                if (log.isTraceEnabled()) {
                    log.trace("Send statistics: " + send);
                }
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get active server list. It looks in the 'mmservers' table for every server that is 'active', and returns all
     * those nodes. The result is cached during a 'serverInterval' period of milliseconds (see {@link
     * MMServers#getIntervalTime}) (so this is a property of the mmservers builder).
     *
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
     * Returns the peers to which must be connected. This can be a fixed list (using the 'peers' setting), or it can be
     * actively determined using the database (see {@link #getActiveServers}).
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
