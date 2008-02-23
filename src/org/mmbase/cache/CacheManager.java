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
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Cache manager manages the static methods of {@link Cache}. If you prefer you can call them on this in stead.
 *
 * @since MMBase-1.8
 * @version $Id: CacheManager.java,v 1.23 2008-02-23 13:37:16 michiel Exp $
 */
public class CacheManager {

    private static final Logger log = Logging.getLoggerInstance(CacheManager.class);

    /**
     * All registered caches
     */
    //private static final NavigableMap<String, Cache<?,?>> caches = new ConcurrentSkipListMap<String, Cache<?,?>>();
    private static final Map<String, Cache<?,?>> caches = new ConcurrentHashMap<String, Cache<?,?>>();

    /**
     * Returns the Cache with a certain name. To be used in combination with getCaches(). If you
     * need a certain cache, you can just as well call the non-static 'getCache' which is normally
     * in cache singletons.
     *
     * @see #getCaches
     */
    public static Cache getCache(String name) {
        return caches.get(name);
    }

    /**
     * Returns a cache wrapped in a 'Bean', so it is not a Map any more. This makes it easier
     * accesible by tools which want that (like EL).
     * @since MMBase-1.9
     */
    public static Bean getBean(String name) {
        return new Bean(getCache(name));
    }
    public static Set<Bean> getCaches(String className) {
        Set<Bean> result = new HashSet<Bean>();
        for (Cache c : caches.values()) {
            try {
                if (className != null && Class.forName(className).isInstance(c)) {
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
        return Collections.unmodifiableSet(caches.keySet());
    }

    /**
     * @since MMBase-1.8.6
     */
    public static Map<String, Cache<?, ?>> getMap() {
        return Collections.unmodifiableMap(caches);
    }


    /**
     * Puts a cache in the caches repository. This function will be
     * called in the static of childs, therefore it is protected.
     *
     * @param cache A cache.
     * @return The previous cache of the same type (stored under the same name)
     */
    public static <K,V> Cache<K,V> putCache(Cache<K,V> cache) {
        Cache old = caches.put(cache.getName(), cache);
        configure(configReader, cache.getName());
        return old;
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
            // TODO: fix again when everybody runs 1.5.0_08, because of
            // generics bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4916620
            Cache cache = getCache(cacheName);
            if (cache == null) {
                log.service("No cache " + cacheName + " is present (perhaps not used yet?)");
            } else {
                String clazz = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.implementation.class"));
                if(!"".equals(clazz)) {
                    Element cacheImpl = xmlReader.getElementByPath(cacheElement, "cache.implementation");
                    Map<String,String> configValues = new HashMap<String,String>();
                    for (Element attrNode: xmlReader.getChildElements(cacheImpl, "param")) {
                        String paramName = xmlReader.getElementAttributeValue(attrNode, "name");
                        String paramValue = xmlReader.getElementValue(attrNode);
                        configValues.put(paramName, paramValue);
                    }
                    cache.setImplementation(clazz, configValues);
                }
                String status = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.status"));
                cache.setActive(status.equalsIgnoreCase("active"));
                try {
                    Integer size = Integer.valueOf(xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.size")));
                    cache.setMaxSize(size.intValue());
                    log.service("Setting " + cacheName + " " + status + " with size " + size);
                } catch (NumberFormatException nfe) {
                    log.error("Could not configure cache " + cacheName + " because the size was wrong: " + nfe.toString());
                }
                String maxSize = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.maxEntrySize"));
                if (!"".equals(maxSize)) {
                    try {
                        cache.maxEntrySize = Integer.parseInt(maxSize);
                        log.service("Setting maximum entry size on " + cacheName + ": " + cache.maxEntrySize + " bytes ");
                    } catch (NumberFormatException nfe2) {
                        log.error("Could not set max entry size cache  of " + cacheName + " because " + nfe2.toString());
                    }
                } else {
                    if (cache.getDefaultMaxEntrySize() > 0) {
                        log.service("No max entry size specified for this cache taking default " + cache.getDefaultMaxEntrySize() + " bytes");
                    }
                    cache.maxEntrySize = cache.getDefaultMaxEntrySize();
                    //now see if we have to load cache release strategies for this lovely cache...
                    if(cache instanceof QueryResultCache){
                        QueryResultCache queryCache = (QueryResultCache) cache;
                        //first remove all present strategies (this might be a reconfiguration)
                        queryCache.getReleaseStrategy().removeAllStrategies();
                        log.debug("found a SearchQueryCache: " + cacheName);
                        //see if there are globally configured release strategies
                        List<ReleaseStrategy> strategies = findReleaseStrategies(xmlReader, xmlReader.getElementByPath("caches"));
                        if(strategies != null){
                            log.debug("found " + strategies.size() + " globally configured strategies");
                            queryCache.addReleaseStrategies(strategies);
                        }

                        //see if there are strategies configured for this cache
                        strategies = findReleaseStrategies(xmlReader, cacheElement);
                        if(strategies != null){
                            log.debug("found " + strategies.size() + " strategies for cache " + cache.getName());
                            queryCache.addReleaseStrategies(strategies);
                        }
                        if (queryCache.getReleaseStrategy().size() == 0) {
                            log.warn("No release-strategies configured for cache " + queryCache + " (nor globally configured); falling back to basic release strategy");
                            queryCache.addReleaseStrategy(new BasicReleaseStrategy());
                        }
                    }
                }
            }
        }
    }

    /**
     * @param reader xml document reader instance
     * @param parentElement the parent of the releaseStrategies element
     * @return List of ReleaseStrategy instances
     * @since 1.8
     */
    private static List<ReleaseStrategy> findReleaseStrategies(DocumentReader reader, Element parentElement) {
        List<ReleaseStrategy> result = new ArrayList<ReleaseStrategy>();

        List<Element> strategyParentIterator = reader.getChildElements(parentElement, "releaseStrategies");
        if(strategyParentIterator.size() == 0){
            return null;
        } else{
            parentElement = strategyParentIterator.get(0);

            //now find the strategies
            for (Element strategy: reader.getChildElements(parentElement, "strategy")) {
                String strategyClassName = reader.getElementValue(strategy);
                log.debug("found strategy in configuration: "+ strategyClassName);
                try {
                    ReleaseStrategy releaseStrategy = getStrategyInstance(strategyClassName);
                    log.debug("still there after trying to get a strategy instance... Instance is " + releaseStrategy==null ? "null" : "not null");

                    //check if we got something
                    if(releaseStrategy != null){
                        result.add(releaseStrategy);
                        log.debug("Successfully created and added "+releaseStrategy.getName() + " instance");
                    } else {
                        log.error("release strategy instance is null.");
                    }

                } catch (CacheConfigurationException e1) {
                    // here we throw a runtime exception, because there is
                    // no way we can deal with this error.
                    throw new RuntimeException("Cache configuration error: " + e1.toString(), e1);
                }
            }
        }
        return result;
    }

    /**
     * I moved this code away from <code>configure()</code> just to
     * clean up a little, and keep the code readable
     * XXX: Who is I?
     * @param strategyClassName
     * @since 1.8
     */
    private static ReleaseStrategy getStrategyInstance(String strategyClassName) throws CacheConfigurationException {
        log.debug("getStrategyInstance()");
        Class strategyClass;
        ReleaseStrategy strategy = null;
        try {
            strategyClass = Class.forName(strategyClassName);
            strategy = (ReleaseStrategy) strategyClass.newInstance();
            log.debug("created strategy instance: "+strategyClassName);

        } catch (ClassCastException e){
            log.debug(strategyClassName + " can not be cast to strategy");
            throw new CacheConfigurationException(strategyClassName + " can not be cast to strategy");
        } catch (ClassNotFoundException e) {
            log.debug("exception getStrategyInstance()");
            throw new CacheConfigurationException("Class "+strategyClassName +
                    "was not found");
        } catch (InstantiationException e) {
            log.debug("exception getStrategyInstance()");
            throw new CacheConfigurationException("A new instance of " + strategyClassName +
                    "could not be created: " + e.toString());
        } catch (IllegalAccessException e) {
            log.debug("exception getStrategyInstance()");
            throw new CacheConfigurationException("A new instance of " + strategyClassName +
                    "could not be created: " + e.toString());
        }
        log.debug("exit getStrategyInstance()");
        return strategy;
    }

    /**
     * The caches can be configured with an XML file, this file can
     * be changed which causes the caches to be reconfigured automaticly.
     */
    private static ResourceWatcher configWatcher = new ResourceWatcher () {
            public void onChange(String resource) {
                try {
                    configReader = new DocumentReader(ResourceLoader.getConfigurationRoot().getInputSource(resource), Cache.class);
                } catch (Exception e) {
                    log.error(e);
                    return;
                }
                configure(configReader);
            }
        };

    static { // configure
        log.debug("Static init of Caches");
        configWatcher.add("caches.xml");
        configWatcher.onChange("caches.xml");
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();

    }


    public static int getTotalByteSize() {
        int len = 0;
        SizeOf sizeof = new SizeOf();
        for (Map.Entry entry : caches.entrySet()) {
            len += sizeof.sizeof(entry.getKey()) + sizeof.sizeof(entry.getValue());
        }
        return len;
    }

    /**
     * Clears and dereferences all caches. To be used on shutdown of MMBase.
     * @since MMBase-1.8.1
     */
    public static void shutdown() {
        log.info("Clearing all caches");
        for(Cache<?,?> cache : caches.values()) {
            cache.clear();
        }
        caches.clear();
    }


    /**
     * Used in config/functions/caches.xml
     */
    public Object remove(String name, Object key) {
        Cache cache = getCache(name);
        if (cache == null) throw new IllegalArgumentException();
        return cache.remove(key);
    }

    public static class Bean<K, V> {
        /* private final Cache<K, V> cache; // this line prevents building in Java 1.5.0_07 probably because of http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4916620 */
        private final Cache cache;
        public Bean(Cache<K, V> c) {
            cache = c;
        }
        public String getName() { return cache.getName(); }
        public String getDescription() { return cache.getDescription(); }
        public int getMaxEntrySize() { return cache.getMaxEntrySize(); }
        public Set<Map.Entry<K, V>> getEntrySet() { return new HashSet<Map.Entry<K, V>>(cache.entrySet()); }
        public Set<K> getKeySet() { return new HashSet<K>(cache.keySet()); }
        public long getHits() { return cache.getHits(); }
        public long  getMisses() { return cache.getMisses(); }
        public long getPuts() { return cache.getPuts(); }
        public  int getMaxSize() { return cache.maxSize(); }
        public  int getSize() { return cache.size(); }
        public double getRatio() { return cache.getRatio(); }
        public String getStats() { return cache.getStats(); }
        public String toString() { return cache.toString(); }
        public boolean isActive() { return cache.isActive(); }
        public int getByteSize() { return cache.getByteSize(); }
        public int getCheapByteSize() { return cache.getCheapByteSize(); }
        public boolean isEmpty() { return cache.isEmpty(); }
        public ReleaseStrategy getReleaseStrategy() { return cache instanceof QueryResultCache ? ((QueryResultCache) cache).getReleaseStrategy() : null;}
        public Map<K, V> getMap() {  return cache; }
        public Map<K, Integer> getCounts() {
            return new AbstractMap<K, Integer>() {
                public Set<Map.Entry<K, Integer>> entrySet() {
                    return new AbstractSet<Map.Entry<K, Integer>>() {
                        public int size() {
                            return cache.size();
                        }
                        public Iterator<Map.Entry<K, Integer>> iterator() {
                            return new Iterator<Map.Entry<K, Integer>>() {
                                private Iterator<K> iterator = new HashSet<K>(cache.keySet()).iterator();
                                public boolean hasNext() {
                                    return iterator.hasNext();
                                }
                                public Map.Entry<K, Integer> next() {
                                    K key = iterator.next();
                                    return new org.mmbase.util.Entry<K, Integer>(key, cache.getCount(key));
                                }
                                public void remove() {
                                    throw new UnsupportedOperationException();
                                }
                            };
                        }
                    };
                }
            };
        }
        public boolean equals(Object o) {
            return  o instanceof Bean && ((Bean) o).cache.equals(cache);
        }
        public int hashCode() {
            return cache.hashCode();
        }
    }
}
