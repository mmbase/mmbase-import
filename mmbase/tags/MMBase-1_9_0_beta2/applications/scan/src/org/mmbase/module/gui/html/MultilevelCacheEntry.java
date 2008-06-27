/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;
import org.mmbase.util.*;

/**
 * This object subscribes itself to builder changes
 *
 * @application SCAN
 * @deprecated-now This is an _excact copy_ of org.mmbase.cache.MultilevelCacheEntry
 * @author Daniel Ockeloen
 * @version $Id: MultilevelCacheEntry.java,v 1.6 2007-06-21 15:50:23 nklasens Exp $
 */
public class MultilevelCacheEntry {
    // callbacks to my 'parents' who
    // listen for me to signals
    Vector<MultilevelSubscribeNode> listeners=new Vector<MultilevelSubscribeNode>();
    MultilevelCacheHandler han;
    Object cachedobject;
    public Object hash;
    StringTagger tagger;

    public MultilevelCacheEntry(MultilevelCacheHandler han,Object hash,Object o,StringTagger tagger) {
        this.han=han;
        this.hash=hash;
        this.cachedobject=o;
        this.tagger=tagger;
    }

    public void addListener(MultilevelSubscribeNode parent) {
        listeners.addElement(parent);
    }

    public synchronized void clear() {
        Enumeration<MultilevelSubscribeNode> e=listeners.elements();
        while (e.hasMoreElements()) {
            MultilevelSubscribeNode l=e.nextElement();
            l.removeCacheEntry(this);
        }
        // now remove ourselfs from the cache
        han.callbackRemove(hash);
    }

    public Object getObject() {
        return(cachedobject);
    }

    public Object getKey() {
        return(hash);
    }

    public StringTagger getTagger() {
        return(tagger);
    }
}
