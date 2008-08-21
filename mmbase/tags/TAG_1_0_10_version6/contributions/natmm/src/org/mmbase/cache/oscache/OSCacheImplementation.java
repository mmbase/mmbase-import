package org.mmbase.cache.oscache;
import org.mmbase.cache.CacheImplementationInterface;
import org.mmbase.util.SizeOf;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.base.Config;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * Implementation of the MMBase 'CacheImplementationInterface' interface, which
 * delegates all calls to an OSCache implementation.
 * This implementation accepts the following configuration properties:
 * <ul>
 *   <li>path</li>
 * </ul>
 * To use this implementation, you can use a construction like this in your 'caches.xml'
 * configuration file:
 * <pre>
 * &lt;cache name="NodeListCache"&gt;
 *   &lt;status&gt;active&lt;/status&gt;
 *   &lt;size&gt;3&lt;/size&gt;
 *   &lt;implementation&gt;
 *     &lt;class&gt;org.mmbase.cache.oscache.OSCacheImplementation&lt;/class&gt;
 *     &lt;param name="path"&gt;/tmp&lt;/param&gt;
 *   &lt;/implementation&gt;
 * &lt;/cache&gt;
 * </pre>
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class OSCacheImplementation implements CacheImplementationInterface  {
    private AbstractConcurrentReadCache cacheImpl;
    private static final String classname = "com.opensymphony.oscache.base.algorithm.LRUCache";
    private static final String persistanceclass = "com.opensymphony.oscache.plugins.diskpersistence.DiskPersistenceListener";
    private static final Logger log = Logging.getLoggerInstance(OSCacheImplementation.class);

    public OSCacheImplementation() {
    }

    /**
     * This method is called by MMBase to configure the cache using the given 
     * map of configuration parameters.
     */
    public void config(Map config) {
       try {
           Class c = Class.forName(classname);
           if (c != null) {
               cacheImpl = (AbstractConcurrentReadCache)c.newInstance();
           }

           c = Class.forName(persistanceclass);
           if (c != null) {
               PersistenceListener pl = (PersistenceListener)c.newInstance();
               Config osconfig = new Config();
               osconfig.set("cache.path", config.get("path"));
               pl.configure(osconfig);
               cacheImpl.setPersistenceListener(pl);
               cacheImpl.setMemoryCaching(true);
               cacheImpl.setUnlimitedDiskCache(true);
               cacheImpl.setOverflowPersistence(true);
            }
        } catch (Exception e) {
            log.error("Exception while initializing cache: " + e);
        }
    }

    /**
     * Wrapper around the setMaxEntries() method of the cache implementation.
     */
    public void setMaxSize(int size) {
        cacheImpl.setMaxEntries(size);
    }

    /**
     * Wrapper around the getMaxEntries() method of the cache implementation.
     */
    public int maxSize() {
        return cacheImpl.getMaxEntries();
    }

    /**
     * Wrapper around the getCount() method of the cache implementation. (not implemented)
     */
    public int getCount(Object key) {
        return -1; //not implemented
    }

    /**
     * Wrapper around the clear() method of the cache implementation.
     */
    public void clear() {
        cacheImpl.clear();
    }

    /**
     * Wrapper around the containsKey() method of the cache implementation.
     */
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return cacheImpl.containsKey(key);
        } else {
            return cacheImpl.containsKey(computeKey(key));
        }
    }

    /**
     * Wrapper around the containsValue() method of the cache implementation.
     */
    public boolean containsValue(Object value) {
        return cacheImpl.containsValue(value);
    }

    /**
     * Wrapper around the entrySet() method of the cache implementation.
     */
    public Set entrySet() {
        return cacheImpl.entrySet();
    }

    /**
     * Wrapper around the equals() method of the cache implementation.
     */
    public boolean equals(Object o) {
        return cacheImpl.equals(o);
    }

    /**
     * Wrapper around the get() method of the cache implementation.
     */
    public Object get(Object key) {
        if (key instanceof String) {
            return cacheImpl.get(key);
        } else {
            return cacheImpl.get(computeKey(key));
        }
    }

    /**
     * Wrapper around the hashCode() method of the cache implementation.
     */
    public int hashCode() {
        return cacheImpl.hashCode();
    }
    
    /**
     * Wrapper around the isEmpty() method of the cache implementation.
     */
    public boolean isEmpty() {
        return cacheImpl.isEmpty();
    }

    /**
     * Wrapper around the keySet() method of the cache implementation.
     */
    public Set keySet() {
        return cacheImpl.keySet();
    }

    /**
     * Wrapper around the put() method of the cache implementation.
     */
    public Object put(Object key, Object value) {
        if (key instanceof String) {
            return cacheImpl.put(key, value);
        } else {
            return cacheImpl.put(computeKey(key), value);
        }
    }

    /**
     * Wrapper around the putAll() method of the cache implementation.
     */
    public void putAll(Map t) {
        cacheImpl.putAll(t);
    }

    /**
     * Wrapper around the remove() method of the cache implementation.
     */
    public Object remove(Object key) {
        if (key instanceof String) {
            return cacheImpl.remove(key);
        } else {
            return cacheImpl.remove(computeKey(key));
        }
    }

    /**
     * Wrapper around the size() method of the cache implementation.
     */
    public int size() {
        return cacheImpl.size();
    }

    /**
     * Wrapper around the values() method of the cache implementation.
     */
    public Collection values() {
        return cacheImpl.values();
    }
    
    /**
     * Calculate an unique string key for an object that is not a 'String'.
     * The 'toString()' will not suffice, partly because it doesn't guarantee
     * a unique value, and also because the toString() method may generate 
     * keys that are too long. 
     * This method will concatenate the classname and the hashcode of the object,
     * this combination should be unique.
     */
    private static String computeKey(Object o) {
        return o.getClass().getName() + "_" + o.hashCode();
    }

    /**
     * @see org.mmbase.util.SizeMeasurable#getByteSize()
     */
    public int getByteSize() {
        throw new UnsupportedOperationException("Size is not available for OSCache");
    }

    /**
     * @see org.mmbase.util.SizeMeasurable#getByteSize(org.mmbase.util.SizeOf)
     */
    public int getByteSize(SizeOf sizeof) {
        throw new UnsupportedOperationException("Size is not available for OSCache");
    }
}
