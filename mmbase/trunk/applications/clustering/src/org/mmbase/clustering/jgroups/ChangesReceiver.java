/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.jgroups;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.ExitEvent;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.SuspectEvent;
import org.jgroups.TimeoutException;
import org.jgroups.View;
import org.mmbase.util.Queue;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ChangesReceiver is a thread object that builds a MultiCast Thread
 * to receive changes from other MMBase Servers.
 *
 * This is the JGroups variant.
 *
 * @see org.mmbase.clustering.jgroups.Multicast
 * @see org.mmbase.clustering.jgroups.ChangesSender
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @author Costyn van Dongen
 * @author Ronald Wildenberg
 * @version $Id: ChangesReceiver.java,v 1.3 2006-03-30 11:23:53 pierre Exp $
 */
public class ChangesReceiver implements Runnable {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ChangesReceiver.class.getName());

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private Queue nodesToSpawn;

    /** JChannel: the multicast communication channel */
    JChannel channel;

    /**
     * Construct the MultiCast Receiver
     * @param channel channel on which to listen for and recieve messages.
     * @param nodesToSpawn Queue of received messages
     */
    public ChangesReceiver(JChannel channel, Queue nodesToSpawn) {
        this.channel=channel;
        this.nodesToSpawn=nodesToSpawn;
        this.start();
    }

    /**
     * Start thread
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this, "MulticastReceiver");
            kicker.setDaemon(true);
            kicker.start();
            log.debug("MulticastReceiver started");
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
     * @todo determine what encoding to use on receiving packages
     */
    public void doWork() {

      while (kicker != null) {

         /* Attempt to receive an object. */
         Object receivedObject = null;
         try {
            receivedObject = channel.receive(0); /* wait forever */
         }
         catch (ChannelNotConnectedException e) {
            /* This channel is not connected to a group.
             * This should never happen, since we never call disconnect on the channel. */
            log.error("Channel disconnected. This should never happen.");
            log.error(Logging.stackTrace(e));
         }
         catch (ChannelClosedException e) {
            log.warn("Channel closed.");
            log.warn(Logging.stackTrace());
         }
         catch (TimeoutException e) {
            log.error("A timeout occurred while receiving a message. This should never happen, since we wait indefinitely.");
            log.error(Logging.stackTrace());
         }

         /* Handle the received object. */
         if (receivedObject != null) {
            if (receivedObject instanceof Message) {
               Message message = (Message) receivedObject;
               log.debug("Received Message from: " + message.getSrc());
               log.debug("Message content:");
               Set headerKeySet = message.getHeaders().keySet();
               final Iterator headers = headerKeySet.iterator();
               while(headers.hasNext()) {
                   log.debug(new String(" " +  message.getHeaders().get(headers.next())));
               }

               log.debug("      " + new String(message.getBuffer()));
               try {
                  nodesToSpawn.append(message.getBuffer());
               } catch (Exception ex) {
                  log.error(Logging.stackTrace(ex));
               }
            } else if (receivedObject instanceof View) {
               View view = (View) receivedObject;
               log.info("Received View from: " + view.getCreator());
               log.info("Current members of group:");

               Vector members = view.getMembers() ;

               for ( int i = 0 ; i < members.size() ; i++ ) {
                  log.info("       " + members.elementAt(i) ) ;
               }
            } else if (receivedObject instanceof SuspectEvent) {
               log.warn("Received SuspectEvent for member: " + ((SuspectEvent) receivedObject).getMember());
            } else if (receivedObject instanceof ExitEvent) {
               /* If an ExitEvent occurs, this means the channel is no longer open.
                * Continuing to call JChannel.receive(0) inside this
                * loop will result in throwing an enormous amount of
                * ChannelClosedException's. Therefore, we wait until the channel is open again. */
               log.warn("Received an ExitEvent. Going to wait until we automatically reconnect to the channel.");
               log.info("Starting to wait at: " +
                        new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
               while (!(channel.isOpen() && channel.isConnected())) {
                  try {
                     Thread.sleep(10);
                  } catch (InterruptedException e) {
                     if (log.isServiceEnabled()) {
                        log.service("Thread " + Thread.currentThread() + " ");
                     }
                  }
               }
               log.info("Finished waiting at: " +
                        new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
               log.info("Channel open again. Current View:");
               View view = channel.getView() ;
               Vector members = view.getMembers() ;
               for ( int i = 0 ; i < members.size() ; i++ ) {
                  log.info("       " + members.elementAt(i) ) ;
               }
            } else {
               log.warn("Unkown object recieved: " + receivedObject.toString());
            }
         }
      }
   }
}
