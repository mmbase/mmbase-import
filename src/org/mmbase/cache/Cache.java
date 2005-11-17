/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.bridge.Cacheable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base class for all Caches. Extend this class for other caches.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Cache.java,v 1.31 2005-11-17 09:54:26 johannes Exp $
 */
abstract public class Cache extends AbstractMap implements SizeMeasurable, Map {

    private static final Logger log = Logging.getLoggerInstance(Cache.class);

    /**
     * All registered caches
     */
    private static Map caches = new ConcurrentHashMap();

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

        Iterator e =  xmlReader.getChildElements("caches", "cache");
        while (e.hasNext()) {
            Element cacheElement = (Element) e.next();
            String cacheName =  cacheElement.getAttribute("name");
            if (only != null && ! only.equals(cacheName)) {
                continue;
            }
            Cache cache = getCache(cacheName);
            if (cache == null) {
                log.service("No cache " + cacheName + " is present (perhaps not used yet?)");
            } else {
                String clazz = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.implementation.class"));
                if(!"".equals(clazz)) {
                    try {
                        Class clas = Class.forName(clazz);
                        if (cache.implementation == null || (! clas.equals(cache.implementation.getClass()))) {
                            cache.implementation = (CacheImplementationInterface) clas.newInstance();
                            Element cacheImpl = xmlReader.getElementByPath(cacheElement, "cache.implementation");
                            Iterator it = xmlReader.getChildElements(cacheImpl, "param");
                            HashMap configValues = new HashMap();
                            while (it.hasNext()) {
                                Element attrNode = (Element)it.next();
                                String paramName = xmlReader.getElementAttributeValue(attrNode, "name");
                                String paramValue = xmlReader.getElementValue(attrNode);
                                configValues.put(paramName, paramValue);
                            }
                            cache.implementation.config(configValues);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        log.error("For cache " + cache + " " + cnfe.getClass().getName() + ": " + cnfe.getMessage());
                    } catch (InstantiationException ie) {
                        log.error("For cache " + cache + " " + ie.getClass().getName() + ": " + ie.getMessage());
                    } catch (IllegalAccessException iae) {
                        log.error("For cache " + cache + " " + iae.getClass().getName() + ": " + iae.getMessage());
                    }
                }
                String status = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.status"));
                cache.setActive(status.equalsIgnoreCase("active"));
                try {
                    Integer size = new Integer(xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.size")));
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
                        log.debug("found a SearchQueryCache: " + cacheName);
                        //see if there are globally configured release strategies
                        List strategies = findReleaseStrategies(xmlReader, xmlReader.getElementByPath("caches"));
                        if(strategies != null){
                            log.debug("found "+strategies.size()+" globally configured strategies");
                            ((QueryResultCache)cache).addReleaseStrategies(strategies);
                        }

                        //see if there are strategies configured for this cache
                        strategies = findReleaseStrategies(xmlReader, cacheElement);
                        if(strategies != null){
                            log.debug("found "+strategies.size()+" strategies for cache "+cache.getName());
                            ((QueryResultCache)cache).addReleaseStrategies(strategies);
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
    private static List findReleaseStrategies(DocumentReader reader, Element parentElement) {
        List result = new ArrayList(5);
        Iterator strategyParentIterator = reader.getChildElements(parentElement, "releaseStrategies");
        if(!strategyParentIterator.hasNext()){
            return null;
        }else{
            parentElement = (Element) strategyParentIterator.next();

            //now find the strategies
            Iterator strategyIterator = reader.getChildElements(parentElement, "strategy");
            while(strategyIterator.hasNext()){
                String strategyClassName =
                    reader.getElementValue((Element)strategyIterator.next());
                log.debug("found strategy in configuration: "+ strategyClassName);
                try {
                    ReleaseStrategy releaseStrategy = getStrategyInstance(strategyClassName);
                    log.debug("still there after trying to get a strategy instance... Instance is " +
                            releaseStrategy==null ? "null" : "not null");

                    //check if we got something
                    if(releaseStrategy != null){

                        result.add(releaseStrategy);
                        log.info("Successfully created and added "+releaseStrategy.getName() + " instance");
                    }else{
                        log.error("release strategy instance is null.");
                    }

                } catch (CacheConfigurationException e1) {
                    // here we throw a runtime exception, becouse there is
                    // no way we can deal with this error.
                    log.error("Cache configuration error: " + e1.toString());
                    log.debug("strategy instantiation error: "+e1.toString());
                    throw new RuntimeException("Cache configuration error: " +
                            e1.toString());
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
                    configReader = new XMLBasicReader(ResourceLoader.getConfigurationRoot().getInputSource(resource), Cache.class);
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


    private boolean active = true;
    protected int   maxEntrySize = -1; // no maximum/ implementation does not support;

    /**
     * @since MMBase-1.8
     */
    private CacheImplementationInterface implementation;

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
        implementation = new LRUHashtable(size);
        log.service("Creating cache " + getName() + ": " + getDescription());
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

    /**
     * Puts a cache in the caches repository. This function will be
     * called in the static of childs, therefore it is protected.
     *
     * @param cache A cache.
     * @return The previous cache of the same type (stored under the same name)
     */
    protected static Cache putCache(Cache cache) {
        Cache old = (Cache) caches.put(cache.getName(), cache);
        configure(configReader, cache.getName());
        return old;
    }

    /**
     * Puts this cache in the caches repository.
     */

    public Cache putCache() {
        return putCache(this);
    }

    /**
     * Returns the Cache with a certain name. To be used in combination with getCaches(). If you
     * need a certain cache, you can just as well call the non-static 'getCache' which is normally
     * in cache singletons.
     *
     * @see #getCaches
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


    public Set entrySet() {
        if (! active) return new HashSet();
        return implementation.entrySet();
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
     *
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
        if (! active) implementation.clear();
        // inactive caches cannot contain anything
        // another option would be to override also the 'contains' methods (which you problable should not use any way)
    }

    public String toString() {
        return "Cache " + getName() + ", Ratio: " + getRatio() + " " + implementation.toString();
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
        Iterator i = entrySet().iterator();
        int len = 0;
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            len += sizeof.sizeof(entry.getKey());
            len += sizeof.sizeof(entry.getValue());
        }
        return len;
    }

    public static int getTotalByteSize() {
        Iterator i = caches.entrySet().iterator();
        int len = 0;
        SizeOf sizeof = new SizeOf();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            len += sizeof.sizeof(entry.getKey()) + sizeof.sizeof(entry.getValue());
        }
        return len;
    }

    public static void main(String args[]) {
        Cache mycache = new Cache(20) {
                public String getName()        { return "test cache"; }
                public String getDescription() { return ""; }
            };
        /*
          System.out.println("putting some strings in cache");
          mycache.put("aaa", "AAA"); // 6 bytes
          mycache.put("bbb", "BBB"); // 6 bytes

          System.out.println("putting an hashmap in cache");
          Map m = new HashMap();
          m.put("ccc", "CCCCCC");
          m.put("ddd", "DDD");
          m.put("abc", "EEE");
          mycache.put("eee", m);
        */

        MMObjectNode node = new MMObjectNode(new MMObjectBuilder(), false);
        node.setValue("hoi", "hoi");
        mycache.put("node", node);

        System.out.println("size of cache: " + mycache.getByteSize());

    }

}
