/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.community.modules;

import java.util.*;

import org.mmbase.module.core.*;

/**
 * NodeBreaker stores temporary nodes with an expiration time.
 * After this has expired the node is removed.
 * <br />
 * @deprecated The NodeBreaker is a temporary solution (not the best code I ever wrote,
 * either, but it should not be too slow and will have to do for now).
 * It would be much better to incorporate 'expiration time' in the TemporaryNodeManager
 * or a temporary node cloud. As such, avoid using this class in the future.
 *
 * @author Dirk-Jan Hoekstra
 * @author Pierre van Rooden
 * @version $Id$
 */
public class NodeBreaker implements Runnable {

    // Lists of ids and expirationtimes temporary node manager
    // These are co-related: ids[x] and expirationtimes[x] belong to the same
    // objkect: ids[x] is the object number or key, expirationtimes[x] determines
    // when it is to be removed.
    // This is not brilliant coding, I know, but it is fast.
    private ArrayList<String> ids = new ArrayList<String>();
    private ArrayList<Long> expirationtimes = new ArrayList<Long>();
    // The interval at which the node breaker checks for expired relations
    private long checkInterval = 10 * 60 * 1000;
    // used to control starting and stopping the thread
    private Thread kicker = null;
    // The temporary node manager that holds the relations.
    private TemporaryNodeManager tmpNodeManager;

    /**
     * Creates a new node breaker.
     * Used by the Channel builder.
     * @param checkInterval the interval at which the relation breaker checks for expired relations
     * @param tmpNodeManager the temporary node manager that holds the relations.
     */
    public NodeBreaker(long checkInterval, TemporaryNodeManager tmpNodeManager) {
        this.checkInterval = checkInterval;
        this.tmpNodeManager = tmpNodeManager;
    }

    /**
     * Adds a node to be watched.
     * This method starts the breaker if it hasn't been done already.
     * @param id the id of the referred node
     * @param expireTime expiration time of the node
     */
    public synchronized void add(String id, long expireTime) {
        ids.add(id);
        expirationtimes.add(new Long(expireTime));
        if (kicker == null) {
            kicker = new Thread(this,"NodeBreaker");
            kicker.setDaemon(true);
            kicker.start();
        }
   }

    /**
     * Updates a node to be watched, preventing it from being removed prematurely.
     * @param id the id of the referred node
     * @param expireTime the new expiration time of the node
     */
    public synchronized boolean update(String id, long expireTime) {
        int i=ids.indexOf(id);
        if (i==-1) return false;
        expirationtimes.set(i,new Long(expireTime));
        return true;
    }

    /**
     * Removes a node.
     * This also removes the node from the temporary node manager cache.
     * @param id the id of the referred node
     */
    public synchronized void remove(String id) {
        int i = ids.indexOf(id);
        if (i >= 0) {
            remove(i);
        }
    }

    /**
     * Removes a node
     * This also removes the node from the temporary node manager cache.
     * @param i the iindex in the list of nodes
     */
    private synchronized void remove(int i) {
        String id=ids.remove(i);
        expirationtimes.remove(i);
        String owner = id.substring(0, id.indexOf("_"));
        String key = id.substring(id.indexOf("_") + 1);
        tmpNodeManager.deleteTmpNode(owner, key);
    }

    /**
     * Stop the breaker.
     */
    public synchronized void stop() {
        kicker = null;
        notify();
    }

    /**
     * Runs the thread that checks for expired relations.
     */
    public void run() {
        long currentTime;
        while (kicker!=null) {
            try {
                Thread.sleep(checkInterval);
            } catch(InterruptedException e) {}
            if (kicker==null) return;
            currentTime = System.currentTimeMillis();
            for (int i = expirationtimes.size()-1; i>=0; i--) {
                Long time = expirationtimes.get(i);
                if (time.longValue() < currentTime) {
                    remove(i);
                }
            }
        }
    }
}
