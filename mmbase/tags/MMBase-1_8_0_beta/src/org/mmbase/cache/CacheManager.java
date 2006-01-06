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

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;



/**
 * Cache manager manages the static methods of {@link Cache}. If you prefer you can call them on this in stead.
 *
 * @since MMBase-1.8
 * @version $Id: CacheManager.java,v 1.3 2005-12-17 19:59:13 michiel Exp $
 */
public class CacheManager {

    private static final Logger log = Logging.getLoggerInstance(CacheManager.class);
    
    /**
     * All registered caches
     */
    private static Map caches = new ConcurrentHashMap();

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
                String clazz = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.implementation"));
                if(!"".equals(clazz)) {
                    Element cacheImpl = xmlReader.getElementByPath(cacheElement, "cache.implementation");
                    Iterator it = xmlReader.getChildElements(cacheImpl, "param");
                    Map configValues = new HashMap();
                    while (it.hasNext()) {
                        Element attrNode = (Element)it.next();
                        String paramName = xmlReader.getElementAttributeValue(attrNode, "name");
                        String paramValue = xmlReader.getElementValue(attrNode);
                        configValues.put(paramName, paramValue);
                    }
                    cache.setImplementation(clazz, configValues);
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
                        //first remove all present strategies (this might be a reconfiguration)
                        ((QueryResultCache)cache).getReleaseStrategy().removeAllStrategies();
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
        Iterator i = caches.entrySet().iterator();
        int len = 0;
        SizeOf sizeof = new SizeOf();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            len += sizeof.sizeof(entry.getKey()) + sizeof.sizeof(entry.getValue());
        }
        return len;
    }

}
