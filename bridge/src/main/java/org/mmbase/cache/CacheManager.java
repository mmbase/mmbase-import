/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import java.util.regex.*;

import org.mmbase.core.event.EventManager;
import org.mmbase.core.event.SystemEvent;
import org.mmbase.core.event.SystemEventListener;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

import java.util.concurrent.*;
import java.lang.management.*;
import javax.management.*;

import static org.mmbase.core.event.EventManager.*;


/**
 * Cache manager manages the static methods of {@link Cache}. If you prefer you can call them on
 * this in stead.
 *
 * Since 1.9.1 this class represents a singleton. Actually most methods should more logically not be
 * static any more.
 *
 * @since MMBase-1.8
 * @version $Id$
 */
public class CacheManager implements CacheManagerMBean, SystemEventListener {

    private static final Logger log = Logging.getLoggerInstance(CacheManager.class);

    /**
     * All registered caches
     */
    //private static final NavigableMap<String, Cache<?,?>> caches = new ConcurrentSkipListMap<String, Cache<?,?>>();
    private final Map<String, Cache<?,?>> caches = new ConcurrentHashMap<String, Cache<?,?>>();

    private static CacheManager instance = null;
    private String machineName;

    private CacheManager() {
        // singleton
    }

    private  String getMachineName() {
        return machineName;
    }

    /**
     * @since MMBase-1.9.1
     */

    public static CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
            instance.register();
            EventManager.getInstance().addEventListener(instance);
        }
        return instance;
    }

    private void register() {
        machineName = MMBaseContext.getMachineName();
        ObjectName on;
        @SuppressWarnings("UseOfObsoleteCollectionType")
        final Hashtable<String, String> props = new Hashtable<String, String>();

        try {
            props.put("type", "Caches");
            try {
                String machineName = getMachineName();

                if (machineName != null) {
                    props.put("type", machineName);
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
            on = new ObjectName("org.mmbase", props);
        } catch (MalformedObjectNameException mfone) {
            log.warn("" + props + " " + mfone);
            return;
        }
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(instance, on);
            log.service("Registered " + on);
        } catch (JMException jmo) {
            log.warn("" + on + " " + jmo.getClass() + " " + jmo.getMessage());
        } catch (Throwable t) {
            log.error("" + on + " " + t.getClass() + " " + t.getMessage());
        }

    }



    /**
     * Returns the Cache with a certain name. To be used in combination with getCaches(). If you
     * need a certain cache, you can just as well call the non-static 'getCache' which is normally
     * in cache singletons.
     *
     * @see #getCaches
     */
    public static Cache getCache(String name) {
        return getInstance().caches.get(name);
    }

    /**
     * Returns a cache wrapped in a 'Bean', so it is not a Map any more. This makes it easier
     * accessible by tools which want that (like EL).
     * @since MMBase-1.9
     */
    public static Bean getBean(String name) {
        return new Bean(getCache(name));
    }
    public static Set<Bean> getCaches(String className) {
        SortedSet<Bean> result = new TreeSet<Bean>();
        for (Cache c : getInstance().caches.values()) {
            try {
                if (className == null || "".equals(className) || Class.forName(className).isInstance(c)) {
                    result.add(new Bean(c));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * Returns the names of all caches.
     *
     * @return A Set containing the names of all caches.
     */
    public static Set<String> getCaches() {
        return Collections.unmodifiableSet(getInstance().caches.keySet());
    }

    /**
     * @since MMBase-1.8.6
     */
    public static Map<String, Cache<?, ?>> getMap() {
        return Collections.unmodifiableMap(getInstance().caches);
    }

    /**
     * Puts a cache in the caches repository.
     *
     * @param cache A cache.
     * @return The previous cache of the same type (stored under the same name)
     */
    public static <K,V> Cache<K,V> putCache(final Cache<K,V> cache) {
        Cache old = getInstance().caches.put(cache.getName(), cache);
        try {
            configure(configReader, cache.getName());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        getInstance().register(cache);
        return old;
    }

    private void register(Cache cache) {
        ObjectName name = getObjectName(cache);
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(cache, name);
        } catch (JMException jmo) {
            log.warn("" + name + " " + jmo.getClass() + " " + jmo.getMessage());
        } catch (Throwable t) {
            log.error("" + name + " " + t.getClass() + " " + t.getMessage());
        }
    }

    /**
     * @since MMBase-1.9
     */
    private ObjectName getObjectName(Cache cache) {
        // Not using the Constructor with Hashtable, because you can't influence the order of keys
        // with that. Which is relevant, e.g. when presented in a tree by jconsole.
        StringBuilder buf = new StringBuilder("org.mmbase:");
        try {
            buf.append("type=Caches");
            org.mmbase.util.transformers.CharTransformer identifier = new org.mmbase.util.transformers.Identifier();
            String machineName = getMachineName();
            if (machineName != null) {
                buf.append(",mmb=").append(machineName);
            } else {
            }
            if (cache != null) {
                buf.append(",name=").append(identifier.transform(cache.getName()));
            } else {
                //props.put("name", "*"); // WTF, this does not work in java 5.
            }
            return new ObjectName(buf.toString());
        } catch (MalformedObjectNameException mfone) {
            log.warn("" + buf + " " + mfone);
            return null;
        }
    }

    /**
     * Configures the caches using a config File. There is only one
     * config file now so the argument is a little overdone, but it
     * doesn't harm.
     */

    private static void configure(DocumentReader file) {
        configure(file, null);
    }

    private static DocumentReader configReader = null;

    /**
     * As configure, but it only changes the configuration of the cache 'only'.
     * This is called on first use of a cache.
     */
    private static void configure(DocumentReader xmlReader, String only) {
        if (xmlReader == null) {
            return; // nothing can be done...
        }

        if (only == null) {
            log.service("Configuring caches with " + xmlReader.getSystemId());
        } else {
            if (log.isDebugEnabled()) log.debug("Configuring cache " + only + " with file " + xmlReader.getSystemId());
        }

        for (Element cacheElement: xmlReader.getChildElements("caches", "cache")) {
            String cacheName =  cacheElement.getAttribute("name");
            if (only != null && ! only.equals(cacheName)) {
                continue;
            }
            Cache<?, ?> cache = getCache(cacheName);
            if (cache == null) {
                log.service("No cache " + cacheName + " is present (perhaps not used yet?)");
            } else {
                cache.configure(cacheElement);

            }
        }
    }



    /**
     * The caches can be configured with an XML file, this file can
     * be changed which causes the caches to be reconfigured automaticly.
     */
    private static final ResourceWatcher configWatcher = new ResourceWatcher () {
        @Override
        public void onChange(String resource) {
            try {
                org.xml.sax.InputSource is = ResourceLoader.getConfigurationRoot().getInputSource(resource);
                if (is == null) {
                    log.warn("Not found " + resource + " in " + ResourceLoader.getConfigurationRoot());
                    return;
                } else {
                    log.service("Reading " + is.getSystemId());
                    configReader = new DocumentReader(is, Cache.class);
                }
            } catch (Exception e) {
                log.warn(e.getClass() + " " + e.getMessage(), e);
                return;
            }
            configure(configReader);
        }
    };

    static { // configure
        try {
            log.debug("Static init of Caches");
            configWatcher.add("caches.xml");
            configWatcher.onChange("caches.xml");
            configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
            configWatcher.start();
        } catch (Throwable t) {
            log.error(t);
        }


    }


    public static int getTotalByteSize() {
        int len = 0;
        SizeOf sizeof = new SizeOf();
        for (Map.Entry<String, Cache<?, ?>> entry : getInstance().caches.entrySet()) {
            len += sizeof.sizeof(entry.getKey()) + sizeof.sizeof(entry.getValue());
        }
        return len;
    }


    private void unRegister() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        log.info("Clearing and unregistering all caches");
        log.debug(mbs.queryNames(getObjectName(null), null));
        for(Cache<?,?> cache : caches.values()) {
            cache.clear();
            ObjectName name = getObjectName(cache);
            if (mbs.isRegistered(name)) {
                try {
                    mbs.unregisterMBean(name);
                } catch (JMException jmo) {
                    log.warn("" + name + " " + jmo.getClass() + " " + jmo.getMessage() + " " + mbs.queryNames(null, null));
                }
            }
        }
        {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            final Hashtable<String, String> props = new Hashtable<String, String>();
            props.put("type", "Caches");
            String machineName = MMBaseContext.getMachineName();
            if (machineName != null) {
                props.put("type", machineName);
            }
            try {
                ObjectName name = new ObjectName("org.mmbase", props);
                if (mbs.isRegistered(name)) {
                    mbs.unregisterMBean(name);
                }
            } catch (JMException jmo) {

            }
        }

        if(mbs.queryNames(getObjectName(null), null).size() > 0) {
            log.warn("Didn't unregister all caches" + mbs.queryNames(getObjectName(null), null));
        }
    }

    /**
     * Clears and dereferences all caches. To be used on shutdown of MMBase.
     * @since MMBase-1.8.1
     */
    public static void shutdown() {

        getInstance().unRegister();

        getInstance().caches.clear();
        instance = null;
    }


    /**
     * Used in config/functions/caches.xml
     */
    public static Object remove(String name, Object key) {
        Cache cache = getCache(name);
        if (cache == null) {
            throw new IllegalArgumentException();
        }
        log.service("Removing " + key + " from " + cache);
        return cache.remove(key);
    }

    /**
     * @since MMBase-1.9.1
     */
    @Override
    public String clear(String pattern) {
        if (pattern == null) pattern = ".*";
        StringBuilder buf = new StringBuilder();
        Pattern p = Pattern.compile(pattern);
        for (Map.Entry<String, Cache<?, ?>> entry : caches.entrySet()) {
            if (p.matcher(entry.getKey()).matches()) {
                buf.append("Clearing ").append(entry.getValue()).append("\n");
                entry.getValue().clear();
            }
        }
        if (buf.length() == 0) buf.append("The regular expression '").append(pattern).append("' matched no cache at all");
        return buf.toString();
    }
    /**
     * @since MMBase-1.9.1
     */
    @Override
    public String enable(String pattern) {
        if (pattern == null) pattern = ".*";
        StringBuilder buf = new StringBuilder();
        Pattern p = Pattern.compile(pattern);
        for (Map.Entry<String, Cache<?, ?>> entry : caches.entrySet()) {
            if (p.matcher(entry.getKey()).matches()) {
                Cache c = entry.getValue();
                if(c.isActive()) {
                    buf.append("Already active ").append(c).append("\n");
                } else {
                    c.setActive(true);
                    buf.append("Making active ").append(c).append("\n");
                }

            }
        }
        if (buf.length() == 0) buf.append("The regular expression '").append(pattern).append("' matched no cache at all");
        return buf.toString();
    }
    /**
     * @since MMBase-1.9.1
     */
    @Override
    public String disable(String pattern) {
        if (pattern == null) pattern = ".*";
        StringBuilder buf = new StringBuilder();
        Pattern p = Pattern.compile(pattern);
        for (Map.Entry<String, Cache<?, ?>> entry : caches.entrySet()) {
            if (p.matcher(entry.getKey()).matches()) {
                Cache c = entry.getValue();
                if(c.isActive()) {
                    c.setActive(false);
                    buf.append("Making inactive ").append(c).append("\n");
                } else {
                    buf.append("Already inactive ").append(c).append("\n");
                }

            }
        }
        if (buf.length() == 0) {
            buf.append("The regular expression '").append(pattern).append("' matched no cache at all");
        }
        return buf.toString();
    }
    /**
     * @since MMBase-1.9.1
     */
    @Override
    public String readConfiguration() {
        configWatcher.onChange("caches.xml");
        return "Read " + ResourceLoader.getConfigurationRoot().getResource("caches.xml");
    }

    @Override
    public void notify(SystemEvent event) {
        if (event instanceof SystemEvent.MachineName) {
            SystemEvent.MachineName mn = (SystemEvent.MachineName) event;
            synchronized(caches) {
                unRegister();
                machineName = mn.getMachine();
                register();
                for (Cache c : caches.values()) {
                    register(c);
                }
            }
        }
    }

    @Override
    public int getWeight() {
        return 0;
    }


    public static class Bean<K, V> implements Comparable<Bean<?, ?>> {
        /* private final Cache<K, V> cache; // this line prevents building in Java 1.5.0_07 probably because of http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4916620 */
        private final Cache cache;
        public Bean(Cache<K, V> c) {
            cache = c;
        }
        public String getName() { return cache.getName(); }
        public String getDescription() { return cache.getDescription(); }
        public int getMaxEntrySize() { return cache.getMaxEntrySize(); }
        public Set<Map.Entry<K, V>> getEntrySet() {
            synchronized (cache.getLock()) {
                return new HashSet<Map.Entry<K, V>>(cache.entrySet());
            }
        }
        public Set<K> getKeySet() {
            synchronized (cache.getLock()) {
                return new HashSet<K>(cache.keySet());
            }
        }
        public long getHits() { return cache.getHits(); }
        public long  getMisses() { return cache.getMisses(); }
        public long getPuts() { return cache.getPuts(); }
        public  int getMaxSize() { return cache.maxSize(); }
        public  int getSize() { return cache.size(); }
        public double getRatio() { return cache.getRatio(); }
        public String getStats() { return cache.getStats(); }
        @Override
        public String toString() { return cache.toString(); }
        public boolean isActive() { return cache.isActive(); }
        public int getByteSize() { return cache.getByteSize(); }
        public int getCheapByteSize() { return cache.getCheapByteSize(); }
        public boolean isEmpty() { return cache.isEmpty(); }
        /*
        public ReleaseStrategy getReleaseStrategy() {
            return cache instanceof QueryResultCache ? ((QueryResultCache) cache).getReleaseStrategy() : null;
        }
        */
        public Map<K, V> getMap() {  return cache; }
        public Map<K, Integer> getCounts() {
            return new AbstractMap<K, Integer>() {
                @Override
                public Set<Map.Entry<K, Integer>> entrySet() {
                    return new AbstractSet<Map.Entry<K, Integer>>() {
                        @Override
                        public int size() {
                            return cache.size();
                        }
                        @Override
                        public Iterator<Map.Entry<K, Integer>> iterator() {
                            return new Iterator<Map.Entry<K, Integer>>() {
                                private Iterator<K> iterator = Bean.this.getKeySet().iterator();
                                @Override
                                public boolean hasNext() {
                                    return iterator.hasNext();
                                }
                                @Override
                                public Map.Entry<K, Integer> next() {
                                    K key = iterator.next();
                                    return new org.mmbase.util.Entry<K, Integer>(key, cache.getCount(key));
                                }
                                @Override
                                public void remove() {
                                    throw new UnsupportedOperationException();
                                }
                            };
                        }
                    };
                }
            };
        }
        @Override
        public boolean equals(Object o) {
            return  o instanceof Bean && ((Bean) o).cache.equals(cache);
        }
        @Override
        public int hashCode() {
            return cache.hashCode();
        }
        @Override
        public int compareTo(Bean<?, ?> bean) {
            return getName().compareTo(bean.getName());
        }
    }
}
