/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;
import org.mmbase.module.core.*;

/**
 * This object subscribes itself to builder changes
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @deprecated-now This is an _excact copy_ of org.mmbase.cache.MultilevelSubscribeNode
 */
public class MultilevelSubscribeNode implements MMBaseObserver {

    private MMBase mmb;
    String type;
    Vector queue=new Vector(50);

    public MultilevelSubscribeNode(MMBase mmb,String type) {
        this.mmb=mmb;
        this.type=type;
        mmb.addLocalObserver(type,this);
        mmb.addRemoteObserver(type,this);
    }

    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        clearEntrys();
        return(true);
    }

    public synchronized void clearEntrys() {
        Enumeration e=queue.elements();
        while (e.hasMoreElements()) {
            MultilevelCacheEntry n=(MultilevelCacheEntry)e.nextElement();
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
