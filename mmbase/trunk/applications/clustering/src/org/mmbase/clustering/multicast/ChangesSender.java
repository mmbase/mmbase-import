/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.multicast;

import java.net.*;
import java.io.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: ChangesSender.java,v 1.8 2006-06-19 16:20:31 michiel Exp $
 */
public class ChangesSender implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    /** counter of send messages */
    private int outcount = 0;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages to send to other MMBase instances */
    private Queue nodesToSend;

    /** address to send the messages to */
    private InetAddress ia;

    /** Socket to send the multicast packets */
    private MulticastSocket ms;

    /** Port for sending datapackets send by Multicast */
    private int mport=4243;
    /** Time To Live for datapackets send by Multicast */
    private int mTTL=1;

    /** Construct MultiCast Sender
     * @param multicastHost 'channel' of the multicast
     * @param mport port of the multicast
     * @param mTTL time-to-live of the multicast packet (0-255)
     * @param nodesToSend Queue of messages to send
     */
    public ChangesSender(String multicastHost, int mport, int mTTL, Queue nodesToSend) {
        this.mport = mport;
        this.mTTL = mTTL;
        this.nodesToSend = nodesToSend;
        try {
            this.ia = InetAddress.getByName(multicastHost);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        this.start();
    }

    /**
     * Start thread
     */
    protected void start() {
        /* Start up the main thread */
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

    public void stop() {
        /* Stop thread */
        try {
            ms.leaveGroup(ia);
            ms.close();
        } catch (Exception e) {
            // nothing
        }
        ms = null;
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
        log.debug("Started sending");
        while(ms != null) {
            try {
                byte[] data = (byte[]) nodesToSend.get();
                DatagramPacket dp = new DatagramPacket(data, data.length, ia, mport);
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("SEND=> " + dp.getLength() + " bytes to " + dp.getAddress());
                    }
                    ms.send(dp);
                } catch (IOException e) {
                    log.error("can't send message" + dp + " to " + ia + ":" + mport);
                    log.error(e.getMessage(), e);
                }
                outcount++;
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            }
        }
        log.debug("Finished sending");
    }
}
