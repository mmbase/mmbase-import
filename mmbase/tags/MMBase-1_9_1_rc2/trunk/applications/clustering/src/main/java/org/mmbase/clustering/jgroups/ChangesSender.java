/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.jgroups;

import java.util.concurrent.BlockingQueue;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;

import org.mmbase.clustering.Statistics;
import org.mmbase.core.util.DaemonThread;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'.
 *
 * This is the JGroups variant.
 *
 * @see org.mmbase.clustering.jgroups.Multicast
 * @see org.mmbase.clustering.jgroups.ChangesReceiver
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @author Costyn van Dongen
 * @version $Id$
 */
public class ChangesSender implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesSender.class);

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages to send to other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSend;

    /** Channel to send messages on */
    private final JChannel channel;

    private final Statistics send;

    /** Construct MultiCast Sender
     * @param channel Channel on which to send messages
     * @param nodesToSend Queue of messages to send
     * @param send Statistics
     */
    ChangesSender(JChannel channel, BlockingQueue<byte[]> nodesToSend, Statistics send) {
        this.send = send;
        this.channel = channel;
        this.nodesToSend = nodesToSend;
        this.start();
    }

    private void start() {
        if (kicker == null) {
            kicker = new DaemonThread(this, "MulticastSender");
            kicker.start();
            log.debug("MulticastSender started");
        }
    }

    void stop() {
        if (kicker != null) {
            kicker.interrupt();
            kicker.setPriority(Thread.MIN_PRIORITY);
            kicker = null;
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }

    /**
     * Take messages fromt the queeu nodesToSend and send them
     * on the JChannel. send() will throw an exception in the
     * cases that the channel is closed or that no channel
     * has been joined.
     *
     */
    public void run() {
        while(kicker != null) {
            try {
                if (channel == null || (! channel.isConnected())) {
                    log.warn("Channel " + channel + " not connected. Sleeping for 5 s.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                    }
                    continue;
                }

                byte[] message = nodesToSend.take();
                long startTime = System.currentTimeMillis();
                Message msg = new Message(null, null, message);
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("SEND=>" + message);
                    }
                    channel.send(msg);
                } catch (ChannelException e) {
                    log.error("Can't send message" + message + ": " + e.getMessage(), e);
                }
                send.count++;
                send.bytes += message.length;
                send.cost += (System.currentTimeMillis() - startTime);
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
