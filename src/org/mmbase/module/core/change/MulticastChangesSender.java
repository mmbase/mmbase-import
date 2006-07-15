/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core.change;

import java.net.*;
import java.io.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MulticastChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: MulticastChangesSender.java,v 1.1.2.1 2004-10-09 10:51:44 nico Exp $
 */
public class MulticastChangesSender implements Runnable {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(MulticastChangesSender.class.getName());

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
    public MulticastChangesSender(String multicastHost, int mport, int mTTL, Queue nodesToSend) {
        this.mport = mport;
        this.mTTL = mTTL;
        this.nodesToSend = nodesToSend;
        try {
            this.ia = InetAddress.getByName(multicastHost);
        }
        catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        this.start();
    }

    /**
     * Start thread
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null && ia != null) {
            try {
                ms = new MulticastSocket();
                ms.joinGroup(ia);
                ms.setTimeToLive(mTTL);
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }

            kicker = new Thread(this, "MulticastSender");
            kicker.setDaemon(true);
            kicker.start();
            log.debug("MulticastSender started");
        }
    }

    /**
     * Stop thread
     */
    public void stop() {
        /* Stop thread */
        try {
            ms.leaveGroup(ia);
            ms.close();
        } catch (Exception e) {
            // nothing
        }
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
            String message = (String) nodesToSend.get();
            byte[] data = message.getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, ia, mport);
            try {
                if (log.isDebugEnabled()) {
                    log.debug("SEND=>" + message);
                }
                ms.send(dp);
            } catch (IOException e) {
                log.error("can't send message" + message);
                log.error(Logging.stackTrace(e));
            }
            outcount++;
        }
    }
}
