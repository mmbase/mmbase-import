/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.community.modules;

import java.util.*;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * RelationBreaker stores relation numbers with an expiretime.
 * After the expiretime has expired the relation is removed.
 * Unfortunately, this class doesn't work. See docs for more info.
 *
 * @deprecated use NodeBreaker instead
 *
 * @author Dirk-Jan Hoekstra
 * @version $Id$
 */

public class RelationBreaker extends Thread {

    // logger
    private static Logger log = Logging.getLoggerInstance(RelationBreaker.class.getName());
    // List of RelationHolder objects, which reference relations in the
    // temporary node manager
    private Vector<RelationHolder> relations = new Vector<RelationHolder>();
    // The interval at which the relation breaker checks for expired relations
    private long checkInterval = 10 * 60 * 1000;
    // Reference to MMBase
    private MMBase mmb;
    // boolean to make sure the breaker is started when called for the first time,
    // and to stop the thread later on.
    private boolean shouldRun = false;
    // The temporary node manager that holds the relations.
    private TemporaryNodeManager tmpNodeManager;


    /**
     * Creates a new relation breaker.
     * Used by the Channel builder.
     * @param mmb reference to MMBase
     * @param checkInterval the interval at which the relation breaker checks for expired relations
     * @param tmpNodeManager the temporary node manager that holds the relations.
     */
    public RelationBreaker(MMBase mmb, long checkInterval, TemporaryNodeManager tmpNodeManager) {
        this.mmb = mmb;
        this.checkInterval = checkInterval;
        this.tmpNodeManager = tmpNodeManager;
    }

    /**
     * Adds a relation to be watched.
     * This method starts the breaker if it hasn't been done already.
     * @param id the id of the referred relation
     * @param expireTime expiration time of the relation
     */
    public synchronized void add(String id, long expireTime) {
        relations.add(new RelationHolder(id, expireTime));
        log.debug("add");
        if (!shouldRun) {
            shouldRun = true;
            start();
        }
    }

    /**
     * Updates a relation to be watched, preventing it from being removed prematurely.
     * @param id the id of the referred relation
     * @param expireTime the new expiration time of the relation
     */
    public synchronized boolean update(String id, long expireTime) {
        RelationHolder relationHolder = relations.elementAt(relations.indexOf(id));
        if (relationHolder != null)
        {    relationHolder.setExpireTime(expireTime);
            return true;
        }
        return false;
    }

    /**
     * Removes a relation.
     * This also removes the relation from the temporary node manager cache.
     * XXX: doesn't work
     * @param id the id of the referred relation
     */
    public synchronized void remove(String id) {
        String owner = id.substring(0, id.indexOf("_"));
        String key = id.substring(id.indexOf("_") + 1);
//      searching on id doesn't work!
        int i = relations.indexOf(id);
        if (i > 0) relations.remove(i);
        tmpNodeManager.deleteTmpNode(owner, key);
    }

    /**
     * Removes a relation.
     * This also removes the relation from the temporary node manager cache.
     * XXX: doesn't work
     * @param relationHolder the RelationHolder of the referred relation
     * @param i index of the holder in the internal list
     */
    public synchronized void remove(RelationHolder relationHolder, int i) {
        //relations.remove(i);
        log.debug(relationHolder.id);
        String owner = relationHolder.id.substring(0, relationHolder.id.indexOf("_"));
        String key = relationHolder.id.substring(relationHolder.id.indexOf("_") + 1);
        tmpNodeManager.deleteTmpNode(owner, key);
    }

    /**
     * Runs the thread that checks for expired relations.
     */
    public void run() {
        mmb.getInsRel();
        long currentTime;

        while (shouldRun) {
            try {
                sleep(checkInterval);
            } catch(Exception e) {
                log.error("run(): can't sleep.");
                shouldRun = false;
                return;
            }

            currentTime = System.currentTimeMillis();

            log.debug("search for expired");
            int i = 0;
            while (i < relations.size()) {
                RelationHolder relationHolder = relations.elementAt(i);
                if (relationHolder.getExpireTime() < currentTime)
                    remove(relationHolder, i);

                i++;
            }
        }
    }
}


/**
 * Holds a reference to a relation.
 */
class RelationHolder {
    // The id of the referred relation.
    public String id;
    // expiration time of the relation
    private long expireTime;

    /**
     * Creates a relation reference.
     * @param id the id of the referred relation
     * @param expireTime expiration time of the relation
     */
    public RelationHolder(String id, long expireTime) {
        this.id = id;
        this.expireTime = expireTime;
    }

    /**
     * Compares the relation references to another object.
     * In this specific case, the object should be equal to the id.
     */
    public synchronized boolean equals(Object anObject) {
        return (id.equals(anObject));
    }
    
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public synchronized int hashCode() {
        return id.hashCode();
    }

    /**
     * Sets the expiration time of the relation.
     */
    public synchronized void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * Retrieves the expiration time of the relation
     */
    public synchronized long getExpireTime() {
        return expireTime;
    }
}
