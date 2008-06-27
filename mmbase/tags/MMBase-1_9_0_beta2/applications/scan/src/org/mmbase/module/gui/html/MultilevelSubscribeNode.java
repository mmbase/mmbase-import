/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * This object subscribes itself to builder changes
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @deprecated-now This is an _excact copy_ of org.mmbase.cache.MultilevelSubscribeNode
 */
public class MultilevelSubscribeNode implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(MultilevelSubscribeNode.class);
    
    private MMBase mmb;
    String type;
    Vector<MultilevelCacheEntry> queue = new Vector<MultilevelCacheEntry>(50);

    public MultilevelSubscribeNode(MMBase mmb,String type) {
        this.mmb = mmb;
        this.type = type;
        MMObjectBuilder builder = mmb.getBuilder(type);
        if (builder != null) {
            builder.addLocalObserver(this);
            builder.addRemoteObserver(this);
        } else {
            log.error("ERROR: Can't find builder : " + type);
        }
    }

    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        clearEntrys();
        return(true);
    }

    public synchronized void clearEntrys() {
        Enumeration<MultilevelCacheEntry> e=queue.elements();
        while (e.hasMoreElements()) {
            MultilevelCacheEntry n=e.nextElement();
            // call the entry's clear that will remove all observers
            // too including myself !
            n.clear();
        }
    }

    public boolean nodeRemoteChanged(String machine, String number,String builder,String ctype) {
        return(nodeChanged(machine,number,builder,ctype));
    }

    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        return(nodeChanged(machine,number,builder,ctype));
    }

    public boolean removeCacheEntry(MultilevelCacheEntry entry) {
        if (queue.contains(entry)) {
            queue.remove(entry);
            return(true);
        } else {
            return(false);
        }
    }

    public boolean addCacheEntry(MultilevelCacheEntry entry) {
        // add myself to the entry so he can find me for remove
        entry.addListener(this);

        // add the entry to my queue
        if (!queue.contains(entry)) {
            queue.addElement(entry);
        }
        return(true);
    }
}
