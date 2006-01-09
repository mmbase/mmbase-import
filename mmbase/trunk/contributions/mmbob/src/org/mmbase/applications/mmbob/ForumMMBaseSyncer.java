/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * The syncer for Nodes used in MMBob. There can be different types of syncing mechanisms:
 * slow for things like statistics and fast for really important things like postings, userinfo, etc
 * 
 * @author Daniel Ockeloen
 * @author Gerard van Enk
 * @version $Id: ForumMMBaseSyncer.java,v 1.6 2006-01-09 09:01:22 daniel Exp $
 */
public class ForumMMBaseSyncer implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(ForumMMBaseSyncer.class);

    // holds the fellow ForumMMBaseSyncers that are instantiated
    static ArrayList brothers = new ArrayList();

    // thread
    Thread kicker = null;
    int sleeptime;
    int delaytime;
    int maxqueue;

    /**
     * The vector dirtyNodes is also referred to as "syncQueue"
     * it contains the nodes that needs to be synchronized
     */
    private Vector dirtyNodes = new Vector();

    /**
     * Contructor
     *
     * @param sleeptime  time to sleep
     * @param maxqueue   maximum number of nodes in the syncQueue (not implemented?)
     * @param startdelay delay (not implemented?)
     */
    public ForumMMBaseSyncer(int sleeptime, int maxqueue, int startdelay) {
        this.sleeptime = sleeptime;
        this.maxqueue = maxqueue;
        this.delaytime = startdelay;

        init();
    }

    /**
     * init()
     */
    public void init() {
        ForumMMBaseSyncerShutdown shutdownsyncer = new ForumMMBaseSyncerShutdown(this);
        Runtime.getRuntime().addShutdownHook(shutdownsyncer);
        log.debug("init syncer" + sleeptime);
        // add this syncer to the band of brothers
        brothers.add(this);
        this.start();
    }

    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this, "forummmbasesyncer");
            kicker.start();
        }
    }

    /**
     * Stops the main Thread.
     **/
    //public void stop() {
        /* Stop thread */
    //  kicker = null;
    //}

    /**
     * Main loop, exception protected
     */
    public void run() {
        kicker.setPriority(Thread.MIN_PRIORITY + 1);
        while (kicker != null) {
            try {
                doWork();
            } catch (Exception e) {
                log.error("run(): ERROR: Exception in forummmbasesyncer thread!");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Main work loop
     * Commit the nodes in the syncQueue to the database
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY + 1);
        log.debug("going to do some work");
        while (kicker != null) {
            try {
                while (dirtyNodes.size() > 0) {
                    Node node = (Node) dirtyNodes.elementAt(0);
                    dirtyNodes.removeElementAt(0);

                    node.commit();
                    removeFromBrothers(node);
                    if (kicker.isInterrupted()) throw new InterruptedException();
                    Thread.sleep(delaytime);
                }
                log.debug("going to sleep");
                if (kicker.isInterrupted()) throw new InterruptedException();
                kicker.sleep(sleeptime);
            } catch (InterruptedException f2) {
                shutdownSync();
            }
        }
    }

    public void shutdownSync() {
                //let's try to commit the nodes before exit
                log.service("Shut down ForumSyncer, trying to commit changes");
                try {
                    while (dirtyNodes.size() > 0) {
                        Node node = (Node) dirtyNodes.elementAt(0);
                        dirtyNodes.removeElementAt(0);
                        log.debug("removing node " + node.getNumber() +" from sync queue "+sleeptime);
                        node.commit();
                        removeFromBrothers(node);
                    } 
                } catch (Exception ex) {
                    log.fatal("something went wrong while shutting down Syncer");
                }
    }


    /**
     * remove the given node from the syncQueue
     *
     * @param node node that has to be removed from the syncQueue
     */
    public void nodeDeleted(Node node) {
        dirtyNodes.remove(node);
    }

    /**
     * Remove the given node also from the brother syncers
     * @param node
     */
    private void removeFromBrothers(Node node) {
        for (int i = 0; i<brothers.size();i++) {
            if (((ForumMMBaseSyncer)brothers.get(i)) != this) {
                log.debug("removing node " + node.getNumber() +" from sync queue "+((ForumMMBaseSyncer)brothers.get(i)).sleeptime);
                ((ForumMMBaseSyncer)brothers.get(i)).nodeDeleted(node);
            } else {
                //log.debug("won't remove node " + node.getNumber() +" from sync queue "+((ForumMMBaseSyncer)brothers.get(i)).sleeptime+" because i probably just did");
            }
        }
    }

    /**
     * add the given node to the syncQueue, to be synchronized at synchronization-time
     *
     * @param node the node that must added to the syncQueue
     */
    public void syncNode(Node node) {
        if (!dirtyNodes.contains(node)) {
            dirtyNodes.addElement(node);
            //log.info("added node="+node.getNumber()+" to sync queue "+sleeptime);
        } else {
            //log.info("refused node="+node.getNumber()+" allready in sync queue "+sleeptime);
        }
    }
}
