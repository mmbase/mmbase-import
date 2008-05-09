/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.multicast;

import org.mmbase.clustering.Statistics;

import java.net.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: ChangesSender.java,v 1.16 2008-05-09 11:33:54 nklasens Exp $
 */
public class ChangesSender implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    private final Statistics send;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages to send to other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSend;

    /** address to send the messages to */
    private final InetAddress ia;

    /** Socket to send the multicast packets */
    private MulticastSocket ms;

    /** Port for sending datapackets send by Multicast */
    private final int mport;
    /** Time To Live for datapackets send by Multicast */
    private final int mTTL;

    /**
     * Construct MultiCast Sender
     * @param multicastHost 'channel' of the multicast
     * @param mport port of the multicast
     * @param mTTL time-to-live of the multicast packet (0-255)
     * @param nodesToSend Queue of messages to send
     * @param send Statistics object in which to administer duration costs
     * @throws UnknownHostException when multicastHost is not found
     */
    ChangesSender(String multicastHost, int mport, int mTTL, BlockingQueue<byte[]> nodesToSend, Statistics send) throws UnknownHostException  {
        this.mport = mport;
        this.mTTL = mTTL;
        this.nodesToSend = nodesToSend;
        this.ia = InetAddress.getByName(multicastHost);
        this.send = send;
        this.start();
    }

    private void start() {
        if (kicker == null && ia != null) {
            try {
                ms = new MulticastSocket();
                ms.joinGroup(ia);
                ms.setTimeToLive(mTTL);
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }

            kicker = MMBaseContext.startThread(this, "MulticastSender");
            log.debug("MulticastSender started");
        }
    }

    void stop() {
        try {
            ms.leaveGroup(ia);
            ms.close();
        } catch (Exception e) {
            // nothing
        }
        ms = null;
        if (kicker != null) {
            kicker.interrupt();
            kicker.setPriority(Thread.MIN_PRIORITY);
            kicker = null;
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }

    public void run() {
        log.debug("Started sending");
        while(ms != null) {
            try {
                byte[] data = nodesToSend.take();
                long startTime = System.currentTimeMillis();
                DatagramPacket dp = new DatagramPacket(data, data.length, ia, mport);
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("SEND=> " + dp.getLength() + " bytes to " + dp.getAddress());
                    }
                    ms.send(dp);
                } catch (IOException e) {
                    log.error("can't send message " + dp + " (" + data.length + " long) to " + ia + ":" + mport);
                    log.error(e.getMessage(), e);
                }
                send.count++;
                send.bytes += data.length;
                send.cost += (System.currentTimeMillis() - startTime);
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.debug("Finished sending");
    }
}
