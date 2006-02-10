/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.mmbase.util.Queue;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesReceiver is a thread object that builds a MultiCast Thread 
 * to receive changes from other MMBase Servers.
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: ChangesReceiver.java,v 1.7 2006-02-10 13:42:24 michiel Exp $
 */
public class ChangesReceiver implements Runnable {

    /** MMbase logging system */
    private static final Logger log = Logging.getLoggerInstance(ChangesReceiver.class);

    /** counter of incoming messages */
    private int incount = 0;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private Queue nodesToSpawn;
    
    /** address to send the messages to */
    private InetAddress ia;
    
    /** Socket to send the multicast packets */
    private MulticastSocket ms;

    /** Port for sending datapackets send by Multicast */
    private int mport = 4243;

    /** Datapacket receive size */
    private int dpsize = 64*1024;

    /**
     * Construct the MultiCast Receiver
     * @param multicastHost 'channel' of the multicast
     * @param mport port of the multicast
     * @param dpsize datapacket receive size
     * @param nodesToSpawn Queue of received messages
     */
    public ChangesReceiver(String multicastHost, int mport, int dpsize, Queue nodesToSpawn) {
        this.mport = mport;
        this.dpsize = dpsize;
        this.nodesToSpawn = nodesToSpawn;
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
                ms = new MulticastSocket(mport);
                ms.joinGroup(ia);
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
            if (ms != null) {
                kicker = new Thread(this, "MulticastReceiver");
                kicker.setDaemon(true);
                kicker.start();
                log.debug("MulticastReceiver started");
            }
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
     * @todo determine what encoding to use on receiving packages
     */
    public void doWork() {
        while (kicker != null) {
            DatagramPacket dp = new DatagramPacket(new byte[dpsize], dpsize);
            try {
                ms.receive(dp);
                byte[] message = new byte[dp.getLength()];

                // the dp.getData array always has dpsize length. 
                // That's not what we want. Especially when falling back to legacy, this is translated to a String.
                // which otherwise gets dpsize length (64k!)
                System.arraycopy(dp.getData(), 0, message, 0, dp.getLength());
                if (log.isDebugEnabled()) {
                    log.debug("RECEIVED=> " + dp.getLength() + " bytes from " + dp.getAddress());
                }
                nodesToSpawn.append(message);
            } catch (java.net.SocketException se) {
                // happens on shutdown
                log.service(se.getMessage());
            } catch (Exception f) {
                log.error(Logging.stackTrace(f));
            }
            incount++;
        }
    }

}
