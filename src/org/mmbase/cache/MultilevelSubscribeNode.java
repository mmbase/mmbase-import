/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.List;
import java.util.Vector;
import java.util.Enumeration;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseObserver;

/**
 * This object subscribes itself to builder changes
 * @rename MultiLevelSubscribeNode
 * @author Daniel Ockeloen
 * @version $Id: MultilevelSubscribeNode.java,v 1.5 2002-03-29 21:26:01 michiel Exp $
 */
class MultilevelSubscribeNode implements MMBaseObserver {

    /**
     * @javadoc
     */
    private MMBase mmb;
    /**
     * @javadoc
     */
    private String type;
    /**
     * @javadoc
     * @badliteral initial size should be configurable or java default?
     */
    private Vector queue=new Vector(50);

    /**
     * @javadoc
     */
    MultilevelSubscribeNode(MMBase mmb,String type) {
        this.mmb=mmb;
        this.type=type;
        mmb.addLocalObserver(type,this);
        mmb.addRemoteObserver(type,this);
    }

    /**
     * @javadoc
     */
    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        clearEntrys();
        return(true);
    }

    /**
     * @javadoc
     */
    public synchronized void clearEntrys() {
        // bit unsafe (?): gets an enumeration, then removes nodes form the enum's list
        for (Enumeration e=queue.elements(); e.hasMoreElements(); ) {
            MultilevelCacheEntry n=(MultilevelCacheEntry)e.nextElement();
            // call the entry's clear that will remove all observers
            // too including myself !
            n.clear();
        }
    }

    /**
     * @javadoc
     */
    public boolean nodeRemoteChanged(String machine, String number,String builder,String ctype) {
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @javadoc
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @javadoc
     */
    public boolean removeCacheEntry(MultilevelCacheEntry entry) {
        if (queue.contains(entry)) {
            queue.remove(entry);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @javadoc
     */
    public boolean addCacheEntry(MultilevelCacheEntry entry) {
        // add myself to the entry so he can find me for remove
        entry.addListener(this);

        // add the entry to my queue
        if (!queue.contains(entry)) {
            queue.add(entry);
        }
        return true;
    }
}
