/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.Vector;
import java.util.Enumeration;
import org.mmbase.util.StringTagger;

/**
 * This object subscribes itself to builder changes
 * @javadoc
 * @rename MultiLevelCacheEntry
 * @author Daniel Ockeloen
 * @version $Id: MultilevelCacheEntry.java,v 1.4 2002-03-22 13:12:46 pierre Exp $
 */
public class MultilevelCacheEntry {
    /**
     * @javadoc
     * @todo should be List
     */
    private Vector listeners=new Vector();
    /**
     * @javadoc
     */
    private MultilevelCacheHandler han;
    /**
     * @javadoc
     */
    private Object cachedobject;
    /**
     * @javadoc
     */
    private Object hash;
    /**
     * @javadoc
     * @deprecated tagger should be the 'key'
     */
    private StringTagger tagger;

    /**
     * @javadoc
     * @todo tagger passed should be the key, hash should be determined by entry (?)
     */
    public MultilevelCacheEntry(MultilevelCacheHandler han,Object hash,Object o ,StringTagger tagger) {
        this.han=han;
        this.hash=hash;
        this.cachedobject=o;
        this.tagger=tagger;
    }

    /**
     * @javadoc
     */
    public void addListener(MultilevelSubscribeNode parent) {
        listeners.addElement(parent);
    }

    /**
     * @javadoc
     * @scope package or protected
     * @todo remove itself should be handled by cache handler
     */
    public synchronized void clear() {
        Enumeration e=listeners.elements();
        while (e.hasMoreElements()) {
            MultilevelSubscribeNode l=(MultilevelSubscribeNode)e.nextElement();
            l.removeCacheEntry(this);
        }
        // now remove ourselfs from the cache
        han.callbackRemove(hash);
    }

    /**
     * @javadoc
     */
    public Object getObject() {
        return cachedobject;
    }

    /**
     * @javadoc
     */
    public Object getKey() {
        return hash;
    }

    /**
     * @javadoc
     * @deprecated tagger should be the 'key'
     */
    public StringTagger getTagger() {
        return tagger;
    }
}
