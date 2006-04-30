/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.jgroups;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.Queue;
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
 * @version $Id: ChangesSender.java,v 1.3 2005-11-30 15:58:03 pierre Exp $
 */
public class ChangesSender implements Runnable {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ChangesSender.class.getName());

    /** counter of send messages */
    private int outcount = 0;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages to send to other MMBase instances */
    private Queue nodesToSend;

    /** Channel to send messages on */
    private JChannel channel = null;

    /** Construct MultiCast Sender
     * @param channel Channel on which to send messages
     * @param nodesToSend Queue of messages to send
     */
    public ChangesSender(JChannel channel, Queue nodesToSend) {
        this.channel = channel;
        this.nodesToSend = nodesToSend;
        this.start();
    }

    /**
     * Start thread
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = MMBaseContext.startThread(this, "MulticastSender");
            log.debug("MulticastSender started");
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
     * Take messages fromt the queeu nodesToSend and send them
     * on the JChannel. send() will throw an exception in the
     * cases that the channel is closed or that no channel
     * has been joined.
     *
     * @todo check what encoding to use for getBytes()
     */
    private void doWork() {

        Message msg = null;
        byte[] message = null;

        while(kicker != null) {
            try {
                message = (byte[]) nodesToSend.get();
                msg = new Message(null, null, message);
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("SEND=>" + message);
                    }
                    channel.send(msg);
                } catch (ChannelException e) {
                    log.error("Can't send message" + message);
                    log.error(Logging.stackTrace(e));
                }
                outcount++;
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            }
        }
    }
}
