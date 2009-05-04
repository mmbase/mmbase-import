/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Cacheable;
import org.mmbase.cache.implementation.LRUCache;

/**
 * A base class for all Caches. Extend this class for other caches.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
abstract public class Cache implements SizeMeasurable, Map {

    private static final Logger log = Logging.getLoggerInstance(Cache.class);

    private boolean active = true;
    protected int maxEntrySize = -1; // no maximum/ implementation does not support;

    /**
     * @since MMBase-1.8
     */
    private CacheImplementationInterface implementation;
    /**
     * @since MMBase-1.8.6
     */
    protected Object lock;

    /**
     * The number of times an element was succesfully retrieved from this cache.
     */
    private int hits = 0;

    /**
     * The number of times an element could not be retrieved from this cache.
     */
    private int misses = 0;

    /**
     * The number of times an element was committed to this cache.
     */
    private int puts = 0;

    public Cache(int size) {
        implementation = new LRUCache(size);
        lock           = implementation.getLock();

        log.service("Creating cache " + getName() + ": " + getDescription());
    }

    void setImplementation(String clazz, Map configValues) {
        try {
            Class clas = Class.forName(clazz);
            if (implementation == null || (! clas.equals(implementation.getClass()))) {
                implementation = (CacheImplementationInterface) clas.newInstance();
                implementation.config(configValues);
            }
        } catch (ClassNotFoundException cnfe) {
            log.error("For cache " + this + " " + cnfe.getClass().getName() + ": " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            log.error("For cache " + this + " " + ie.getClass().getName() + ": " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            log.error("For cache " + this + " " + iae.getClass().getName() + ": " + iae.getMessage());
        }
    }


    /**
     * @since MMBase-1.8.6
     */
    public Object getLock() {
        return lock;
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
     * Return the maximum entry size for the cache in bytes.  If the
     * cache-type supports it (default no), then no values bigger then
     * this will be stored in the cache.
     */
    public int getMaxEntrySize() {
        if (getDefaultMaxEntrySize() > 0) {
            return maxEntrySize;
        } else {
            return -1;
        }
    }

    /**
     * This has to be overridden by Caches which support max entry size.
     */

    protected int getDefaultMaxEntrySize() {
        return -1;
    }

    public Set entrySet() {
        if (! active) return new HashSet();
        return implementation.entrySet();
    }

    /**
     * @since MMBase-1.8.6
     */
    public Class getImplementation() {
        return implementation.getClass();
    }

    /**
     * Checks whether the key object should be cached.
     * This method returns <code>false</code> if either the current cache is inactive, or the object to cache
     * has a cache policy associated that prohibits caching of the object.
     * @param key the object to be cached
     * @return <code>true</code> if the object can be cached
     * @since MMBase-1.8
     */
    protected boolean checkCachePolicy(Object key) {
        CachePolicy policy = null;
        if (active) {
            if (key instanceof Cacheable) {
                policy = ((Cacheable)key).getCachePolicy();
                if (policy != null) {
                    return policy.checkPolicy(key);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Like 'get' of Maps but considers if the cache is active or not,  and the cache policy of the key.
     */
    public  Object get(Object key) {
        if (!checkCachePolicy(key)) {
            return null;
        }
        Object res = implementation.get(key);
        if (res != null) {
            hits++;
        } else {
            misses++;
        }
        return res;
    }

    /**
     * Like 'put' of LRUHashtable but considers if the cache is active or not.
     *
     */
    public Object put(Object key, Object value) {
        if (!checkCachePolicy(key)) {
            return null;
        }
        puts++;
        return implementation.put(key, value);
    }

    /**
     * Returns the number of times an element was succesfully retrieved
     * from the table.
     */
    public int getHits() {
        return hits;
    }

    /**
     * Returns the number of times an element cpould not be retrieved
     * from the table.
     */
    public int getMisses() {
        return misses;
    }

    /**
     * Returns the number of times an element was committed to the table.
     */
    public int getPuts() {
        return puts;
    }

    public  void setMaxSize(int size) {
        implementation.setMaxSize(size);
    }
    public  int maxSize() {
        return implementation.maxSize();
    }

    /**
     * @see java.util.Map#size()
     */
    public  int size() {
        return implementation.size();
    }
    public  boolean contains(Object key) {
        return implementation.containsKey(key);
    }

    public int getCount(Object key) {
        return implementation.getCount(key);
    }

    /**
     * Returns the ratio of hits and misses.
     * The higher the ratio, the more succesfull the table retrieval is.
     * A value of 1 means every attempt to retrieve data is succesfull,
     * while a value nearing 0 means most times the object requested it is
     * not available.
     * Generally a high ratio means the table can be shrunk, while a low ratio
     * means its size needs to be increased.
     *
     * @return A double between 0 and 1 or NaN.
     */
    public double getRatio() {
        return ((double) hits) / (  hits + misses );
    }


    /**
     * Returns statistics on this table.
     * The information shown includes number of accesses, ratio of misses and hits,
     * current size, and number of puts.
     */
    public String getStats() {
        return "Access "+ (hits + misses) + " Ratio " + getRatio() + " Size " + size() + " Puts " + puts;
    }


    /**
     * Sets this cache to active or passive.
     * TODO: Writing back to caches.xml if necessary (if this call was nog caused by change of caches.xml itself)
     */
    public void setActive(boolean a) {
        active = a;
        if (! active) {
            implementation.clear();
        }
        // inactive caches cannot contain anything
        // another option would be to override also the 'contains' methods (which you problable should not use any way)
    }

    public String toString() {
        return "Cache " + getName() + ", Ratio: " + getRatio() + " " + implementation;
    }

    /**
     * Wether this cache is active or not.
     */
    public final boolean isActive() {
        return active;
    }

    public int getByteSize() {
        return getByteSize(new SizeOf());
    }

    public int getByteSize(SizeOf sizeof) {
        int size = 26;
        if (implementation instanceof SizeMeasurable) {
            size += ((SizeMeasurable) implementation).getByteSize(sizeof);
        } else {
            // sizeof.sizeof(implementation) does not work because this.equals(implementation)
            Iterator i = implementation.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                size += sizeof.sizeof(entry.getKey());
                size += sizeof.sizeof(entry.getValue());
            }
        }
        return size;
    }

    /**
     * Returns the sum of bytesizes of every key and value. This may count too much, because objects
     * (like Nodes) may occur in more then one value, but this is considerably cheaper then {@link
     * #getByteSize()}, which has to keep a Collection of every counted object.
     * @since MMBase-1.8
     */
    public int getCheapByteSize() {
        int size = 0;
        SizeOf sizeof = new SizeOf();
        Iterator i = implementation.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            size += sizeof.sizeof(entry.getKey());
            size += sizeof.sizeof(entry.getValue());
            sizeof.clear();
        }
        return size;
    }


    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        implementation.clear();
    }


    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return implementation.containsKey(key);
    }


    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return implementation.containsValue(value);
    }


    /**
     * @see java.util.Map#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        // odd, but this is accordinding to javadoc of Map.
        if (o == this)
            return true;

        if (!(o instanceof Cache))
            return false;
        Cache c = (Cache) o;
        if (!c.getName().equals(getName()))
            return false;;
        return implementation.equals(o);
    }


    /**
     * @see java.util.Map#hashCode()
     */
    public int hashCode() {
        int hash = getName().hashCode();
        hash = HashCodeUtil.hashCode(hash, implementation.hashCode());
        return hash;
    }


    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return implementation.isEmpty();
    }


    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return implementation.keySet();
    }


    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t) {
        implementation.putAll(t);
    }


    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return implementation.remove(key);
    }


    /**
     * @see java.util.Map#values()
     */
    public Collection values() {
        return implementation.values();
    }


    /**
     * Puts this cache in the caches repository.
     * @see CacheManager#putCache(Cache)
     */

    public Cache putCache() {
        return CacheManager.putCache(this);
    }

    /**
     * @see CacheManager#getCache(String)
     */
    protected static Cache putCache(Cache cache) {
        return CacheManager.putCache(cache);
    }

    /**
     * @see CacheManager#getCache(String)
     */
    public static Cache getCache(String name) {
        return CacheManager.getCache(name);
    }

    /**
     * @see CacheManager#getCaches
     */
    public static Set getCaches() {
        return CacheManager.getCaches();
    }

    /**
     * @see CacheManager#getTotalByteSize
     */
    public static int getTotalByteSize() {
        return CacheManager.getTotalByteSize();
    }


    public static void main(String args[]) {
        Cache mycache = new Cache(20000000) {
                public String getName()        { return "test cache"; }
                public String getDescription() { return ""; }
            };
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long usedBefore = rt.totalMemory() - rt.freeMemory();

        System.out.println("putting some strings in cache");
        mycache.put("aaa", "AAA"); // 6 bytes
        mycache.put("bbb", "BBB"); // 6 bytes

        System.out.println("putting an hashmap in cache");
        Map m = new HashMap();
        m.put("ccc", "CCCCCC"); // 9
        m.put("ddd", "DDD");    // 6
        m.put("abc", "EEE");    // 6
        mycache.put("eee", m);  // 3


        //String[] list = new String[1000000];
        //ArrayList list = new ArrayList();
        // should cause 16M of char
        for (int i = 1000000; i < 2000000; i++) {
            mycache.put("a" + 1000000 + i, "b" + 1000000 + i);
            //list[i - 1000000] = "a" + i + "b" + i;
            //list.add("a" + i + "b" + i);
            //list.add(new String( new byte[] {}).intern());
        }
        rt.gc();

        long usedAfter = rt.totalMemory() - rt.freeMemory();

        System.out.println("1M of String costs "  + (usedAfter - usedBefore) + " bytes");
        System.out.println("Sizeof reports " + SizeOf.getByteSize(mycache));
        System.out.println("size of cache: " + mycache.getByteSize() + " ");

    }

}
