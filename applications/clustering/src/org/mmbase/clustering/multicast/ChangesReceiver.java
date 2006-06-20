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
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesReceiver is a thread object that builds a MultiCast Thread
 * to receive changes from other MMBase Servers.
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id: ChangesReceiver.java,v 1.12 2006-06-20 08:05:53 michiel Exp $
 */
public class ChangesReceiver implements Runnable {

    /** MMbase logging system */
    private static final Logger log = Logging.getLoggerInstance(ChangesReceiver.class);

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
    ChangesReceiver(String multicastHost, int mport, int dpsize, Queue nodesToSpawn) {
        this.mport = mport;
        this.dpsize = dpsize;
        this.nodesToSpawn = nodesToSpawn;
        try {
            this.ia = InetAddress.getByName(multicastHost);
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        this.start();
    }
    private  void start() {
        if (kicker == null && ia != null) {
            try {
                ms = new MulticastSocket(mport);
                ms.joinGroup(ia);
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
            if (ms != null) {
                kicker = MMBaseContext.startThread(this, "MulticastReceiver");
                log.debug("MulticastReceiver started");
            }
        }
    }

    void stop() {
        MulticastSocket closingMS = ms; // for closing the socket while the thread stops
        ms = null; // the criteria for stopping thread
        try {
            closingMS.leaveGroup(ia);
            closingMS.close();
        } catch (Exception e) {
            // nothing
        }
        if (kicker != null) {
            kicker.setPriority(Thread.MIN_PRIORITY);
            kicker.interrupt();
            kicker = null;
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }


    public void run() {
        // create a datapackage to receive all messages
        byte[] buffer = new byte[dpsize];
        DatagramPacket dp = new DatagramPacket(buffer, dpsize);
        while (ms != null) {
            try {
                // reset datapackage buffer size for re-use
                dp.setLength(dpsize);
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
                // generally happens on shutdown (ms==null)
                // if not log it as an error
                if (ms != null) log.error(se.getMessage());
            } catch (Exception f) {
                log.error(f.getMessage(), f);
            }
        }
    }

}
