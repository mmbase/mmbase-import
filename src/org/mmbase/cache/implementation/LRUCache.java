/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache.implementation;

import org.mmbase.cache.CacheImplementationInterface;
import java.util.*;

/**
 * A cache implementation backed by a {@link java.util.LinkedHashMap}, in access-order mode, and
 * restricted maximal size ('Least Recently Used' cache algorithm).
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @see    org.mmbase.cache.Cache
 * @since MMBase-1.8.6
 */
public class LRUCache implements CacheImplementationInterface {

    public int maxSize = 100;
    private final Map backing;

    public LRUCache() {
        this(100);
    }

    public LRUCache(int size) {
        maxSize = size;
        // caches can typically be accessed/modified by multipible thread, so we need to synchronize
        backing = Collections.synchronizedMap(new LinkedHashMap(size, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > LRUCache.this.maxSize;
                }
            });
    }

    public Object getLock() {
        return backing;
    }

    public int getCount(Object key) {
        return -1;
    }

    /**
     * Change the maximum size of the table.
     * This may result in removal of entries in the table.
     * @param size the new desired size
     */
    public void setMaxSize(int size) {
        if (size < 0 ) throw new IllegalArgumentException("Cannot set size to negative value");
        maxSize = size;
        while (size() > maxSize()) {
            try {
                Iterator i = entrySet().iterator();
                i.next();
                i.remove();
            } catch (Exception e) {
                // ConcurentModification?
            }
        }
    }


    public int maxSize() {
        return maxSize;
    }

    /**
     * Returns size, maxSize.
     */
    public String toString() {
        return "Size=" + size() + ", Max=" + maxSize;
    }


    public void config(Map map) {
        // needs no configuration.
    }

    // wrapping for synchronization
    public int size() { return backing.size(); }
    public boolean isEmpty() { return backing.isEmpty();}
    public boolean containsKey(Object key) { return backing.containsKey(key);}
    public boolean containsValue(Object value){ return backing.containsValue(value);}
    public Object get(Object key) { return backing.get(key);}
    public Object put(Object key, Object value) { return backing.put(key, value);}
    public Object remove(Object key) { return backing.remove(key);}
    public void putAll(Map map) { backing.putAll(map); }
    public void clear() { backing.clear(); }
    public Set keySet() { return backing.keySet(); }
    public Set entrySet() { return backing.entrySet(); }
    public Collection values() { return backing.values();}
}
