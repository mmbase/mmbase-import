/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import org.mmbase.module.builders.MMServers;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.Queue;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesSender is a thread object sending the nodes found in the
 * sending queue over unicast connections
 *
 * @author Nico Klasens
 * @version $Id: ChangesSender.java,v 1.4 2006-01-02 14:33:37 nklasens Exp $
 */
public class ChangesSender implements Runnable {

    /** MMbase logging system */
    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    /** counter of send messages */
    private int outcount = 0;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages to send to other MMBase instances */
    private Queue nodesToSend;

    /** Port on which the talking between nodes take place.*/
    private int unicastPort = 4243;

    /** Timeout of the connection.*/
    private int unicastTimeout = 10*1000;

    /** List of active MMBase servers */
    private List activeServers = null;
    /** last time the mmservers table was checked for active servers */
    private long lastServerChecked = -1;
    /** Interval of servers change their state */
    private long serverInterval = -1;

    /**
     * Construct UniCast Sender
     * @param unicastPort port of the unicast connections
     * @param unicastTimeout timeout on the connections
     * @param nodesToSend Queue of messages to send
     * @param mmbase MMBase instance
     */
    public ChangesSender(int unicastPort, int unicastTimeout, Queue nodesToSend) {
        this.nodesToSend = nodesToSend;
        this.unicastPort = unicastPort;
        this.unicastTimeout = unicastTimeout;
        this.start();
    }

    /**
     * Start thread
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = MMBaseContext.startThread(this, "UnicastSender");
            log.debug("UnicastSender started");
        }
    }

    /**
     * Stop thread
     */
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }

    /**
     * Run thread
     */
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Let the thread do his work
     *
     * @todo check what encoding to sue for getBytes()
     */
    private void doWork() {
        while(kicker != null) {
            try {
                String message = (String) nodesToSend.get();

                List servers = getActiveServers();
                for (int i = 0; i < servers.size(); i++) {
                    MMObjectNode node = (MMObjectNode) servers.get(i);
                    if (node != null) {
                        String hostname = node.getStringValue("host");

                        Socket socket = null;
                        DataOutputStream os = null;
                        try {
                            socket = new Socket();
                            socket.connect(new InetSocketAddress(hostname, unicastPort), unicastTimeout);
                            os = new DataOutputStream(socket.getOutputStream());
                            os.writeBytes(message);
                            os.flush();
                            if (log.isDebugEnabled()) {
                                log.debug("SEND=>" + message);
                            }
                        } catch(SocketTimeoutException ste) {
                            log.warn("Server timeout: " + hostname);
                            servers.remove(i);
                        } catch (IOException e) {
                            log.error("can't send message" + message);
                            log.error(Logging.stackTrace(e));
                        }
                        finally {
                            if (os != null) {
                                try {
                                    os.close();
                                }
                                catch (IOException e1) {
                                }
                            }
                            if (socket != null) {
                                try {
                                    socket.close();
                                }
                                catch (IOException e1) {
                                }
                            }
                        }
                        outcount++;
                    }
                }
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            }
        }
    }

    /**
     * Get Active server list
     * @return server list
     */
    private List getActiveServers() {
        MMBase mmbase = MMBase.getMMBase();
        if (serverInterval < 0) {
            MMServers mmservers = (MMServers) mmbase.getMMObject("mmservers");
            serverInterval = mmservers.getIntervalTime();
            activeServers = mmservers.getActiveServers();
            lastServerChecked = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug("active servers: " + activeServers);
            }
        }
        else {
            if (lastServerChecked + serverInterval < System.currentTimeMillis()) {
                MMServers mmservers = (MMServers) mmbase.getMMObject("mmservers");
                activeServers = mmservers.getActiveServers();
                lastServerChecked = System.currentTimeMillis();
                if (log.isDebugEnabled()) {
                    log.debug("active servers: " + activeServers);
                }
            }
        }

        return activeServers;
    }


}
