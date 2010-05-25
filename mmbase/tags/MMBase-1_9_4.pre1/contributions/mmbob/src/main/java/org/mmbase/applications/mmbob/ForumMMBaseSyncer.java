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
 * This object is responsible for calling {@link Node#commit}, of the MMBase nodes involved in
 * MMBob. It does that only to spare the MMBase database. It maintains a queue of 'dirty nodes', and
 * sometimes calls commit on one and cleans it up.
 * 
 * @author Daniel Ockeloen
 * @author Gerard van Enk
 * @version $Id$
 */
public class ForumMMBaseSyncer implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ForumMMBaseSyncer.class);

    // holds the fellow ForumMMBaseSyncers that are instantiated
    private static final List<ForumMMBaseSyncer> siblings = new ArrayList<ForumMMBaseSyncer>();

    // thread
    Thread kicker = null;
    final int sleepTime; // ms
    final int delayTime; // ms

    /**
     * The vector dirtyNodes is also referred to as "syncQueue"
     * it contains the nodes that needs to be synchronized
     * @TODO How about using an actual Queue here (DelayQueue or so)?
     */
    private Vector dirtyNodes = new Vector(){
        public String toString() {
            StringBuffer out = new StringBuffer("[");
            for (Enumeration e = elements(); e.hasMoreElements();) {
                out.append(((Node) e.nextElement()).getNumber() + ",");
            }
            out.append("]");
            return out.toString();
        }
    };

    /**
     * Contructor
     *
     * @param sleepTime  time to sleep
     * @param maxQueue   maximum number of nodes in the syncQueue (not implemented)
     * @param startDelay delay (not implemented?)
     */
    public ForumMMBaseSyncer(int sleepTime, int maxQueue, int startDelay) {
        this.sleepTime = sleepTime;
        //this.maxqueue = maxqueue;
        this.delayTime = startDelay;
        init();
    }

    /**
     * init()
     */
    public void init() {
        ForumMMBaseSyncerShutdown shutdownsyncer = new ForumMMBaseSyncerShutdown(this);
        Runtime.getRuntime().addShutdownHook(shutdownsyncer);
        log.debug("init syncer" + sleepTime);
        // add this syncer to the band of siblings
        siblings.add(this);
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
                log.error(e.getMessage(), e);
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
                    try {
                        NodeManager tm = node.getNodeManager();
                        if (tm != null) {
                            String tmn = tm.getName();
                            if (tmn.equals("forums") || tmn.equals("postthreads") || tmn.equals("postareas")) {
                                // check if the node was not deleted
                                Node on = node.getNodeValue("lastpostnumber");
                                if (on == null) {
                                    node.setValue("lastpostnumber", null);
                                }
                            }
                            log.debug("committing node with number: " + node.getNumber());
                            node.commit();
                            removeFromSiblings(node);
                        }
                    } catch (Exception e) {
                        log.error("NODE PROBLEM WITH : " + node.getNumber() + " " + e.getMessage(), e);
                    }
                    if (kicker.isInterrupted()) {
                        throw new InterruptedException();
                    }
                    Thread.sleep(delayTime); // this causes that mmbob can handle only 1 node per delaytime.
                }
                log.trace("going to sleep");
                if (kicker.isInterrupted()) {
                    throw new InterruptedException();
                }
                kicker.sleep(sleepTime);
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
                log.debug("removing node " + node.getNumber() + " from sync queue " + sleepTime);
                node.commit();
                removeFromSiblings(node);
            } 
        } catch (Exception ex) {
            log.fatal("something went wrong while shutting down Syncer " + ex.getMessage());
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
    private void removeFromSiblings(Node node) {
        for (ForumMMBaseSyncer sibling : siblings) {
            if (sibling != this) {
                if (log.isDebugEnabled()) {
                    log.debug("removing node " + node.getNumber() + " from sync queue "+ sibling);
                }
                sibling.nodeDeleted(node);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("won't remove node " + node.getNumber() +" from sync queue " + sibling + " because i probably just did");
                }
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
            log.debug("added node=" + node.getNumber() + " to sync queue " + this);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("refused node=" + node.getNumber() + " already in sync queue " + this);
                log.trace("sync queue " + dirtyNodes);
            }
        }
    }

    String printCurrentContent() {
        return dirtyNodes.toString();
    }
    public String toString() {
        return "SYNCER[delay=" + delayTime + " ms, sleep=" + sleepTime + " ms]";
    }

}
