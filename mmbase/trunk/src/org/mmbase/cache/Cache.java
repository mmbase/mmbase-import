/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import org.mmbase.util.LRUHashtable;

/**
 * A base class for all Caches. Extend this class for other caches.  
 *
 * @author Michiel Meeuwissen
 * @version $Id: Cache.java,v 1.1 2002-03-29 20:07:07 michiel Exp $
 */
abstract public class Cache extends LRUHashtable {

    private static Map caches = new Hashtable();
    private boolean active = true;


    public Cache(int size) {
        super(size);
    }

    /**
     * Returns a name for this cache type. Default it is the class
     * name, but this normally will be overriden.
     */
    public String getName() {
        return getClass().getName();
    }
    /**
     * Gives a description for this cache type. This can be used in
     * cache overviews.
     */
    public String getDescription() {
        return "An all purpose Cache";
    }

    /**
     * Puts a cache in the caches repository. This function will be
     * called in the static of childs, therefore it is protected.
     *
     * @param A cache.
     * @return The previous cache of the same type.
     */
    protected static Cache putCache(Cache cache) {
        return (Cache) caches.put(cache.getName(), cache);                
    }

    /**
     * Returns the Cache with a certain name. To be used in combination with getCaches().
     *
     * @see getCaches
     */
    public static Cache getCache(String name) {
        return (Cache) caches.get(name);
    }

    /**
     * Returns the names of all caches.
     *
     * @return A Set containing the names of all caches.
     */
    public static Set getCaches() {
        return caches.keySet();
    }

    /**
     * Like 'get' of LRUHashtable but considers if the cache is active or not.
     *
     */
    public synchronized Object get(Object key) {
        if (! active) return null;
        return super.get(key);
    }

    /**
     * Like 'put' of LRUHashtable but considers if the cache is active or not.
     *
     */
    public synchronized Object put(Object key, Object value) {
        if (! active) return null;
        return super.put(key, value);
    }

    /**
     * Sets this cache to active or passive.
     */
    public void setActive(boolean a) {
        active = a;
    }

    /**
     * Wether this cache is active or not.
     */
    public boolean isActive() {
        return active;
    }
}
