/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import java.io.File;
import org.mmbase.util.LRUHashtable;
import org.mmbase.module.core.*; // MMBaseContext;

import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.SizeMeasurable;
import org.mmbase.util.SizeOf;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A base class for all Caches. Extend this class for other caches.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Cache.java,v 1.19 2003-07-17 17:01:16 michiel Exp $
 */
abstract public class Cache extends LRUHashtable implements SizeMeasurable  {

    private static Logger log = Logging.getLoggerInstance(Cache.class);

    /**
     * All registered caches
     */
    private static Map caches = new Hashtable();

    /**
     * Configures the caches using a config File. There is only one
     * config file now so the argument is a little overdone, but it
     * doesn't harm.
     */

    private static void configure(XMLBasicReader file) {
        configure(file, null);
    }

    private static XMLBasicReader configReader = null;

    /**
     * As configure, but it only changes the configuration of the cache 'only'.
     * This is called on first use of a cache.
     */
    private static void configure(XMLBasicReader xmlReader, String only) {
        if (xmlReader == null) {
            return; // nothing can be done...
        }

        if (only == null) {
            log.service("Configuring caches with " + xmlReader.getFileName());
        } else {
            if (log.isDebugEnabled()) log.debug("Configuring cache " + only + " with file " + xmlReader.getFileName());
        }

        Enumeration e =  xmlReader.getChildElements("caches", "cache");
        while (e.hasMoreElements()) {
            Element cacheElement = (Element) e.nextElement();
            String cacheName =  cacheElement.getAttribute("name");
            if (only != null && ! only.equals(cacheName)) {
                continue;
            }
            Cache cache = getCache(cacheName);
            if (cache == null) {
                log.service("No cache " + cacheName + " is present (perhaps not used yet?)");
            } else {
                String status = xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.status"));
                cache.setActive(status.equalsIgnoreCase("active"));
                try {
                    Integer size = new Integer(xmlReader.getElementValue(xmlReader.getElementByPath(cacheElement, "cache.size")));
                    cache.setSize(size.intValue());
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
                }
            }
        }
    }


    /**
    * The caches can be configured with an XML file, this file can
    * be changed which causes the caches to be reconfigured automaticly.
    */
    private static FileWatcher configWatcher = new FileWatcher (true) {
            protected void onChange(File file) {
                configReader = new XMLBasicReader(file.getAbsolutePath(), Cache.class);
                configure(configReader);
            }
        };

    static { // configure
        log.debug("Static init of Caches");
        File configFile = new File(MMBaseContext.getConfigPath() + File.separator + "caches.xml");
        if (configFile.exists()) {
            configWatcher.add(configFile);
            configReader = new XMLBasicReader(configFile.getAbsolutePath(), Cache.class);
            // configure(configReader); never mind, no cache are present on start up
        } else {
            log.warn("No cache configuration file " + configFile + " found");
        }
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();

    }


    private boolean active = true;
    protected int   maxEntrySize = -1; // no maximum/ implementation does not support;



    public Cache(int size) {
        super(size);
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
     * cache-type support it (default no), then no values bigger then
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
     * @param A cache.
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
     * TODO: Writing back to caches.xml if necessary (if this call was nog caused by change of caches.xml itself)
     */
    public void setActive(boolean a) {
        active = a;
        if (! active) clear();
        // inactive caches cannot contain anything
        // another option would be to override also the 'contains' methods (which you problable should not use any way)
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

    MMObjectNode node = new MMObjectNode(new MMObjectBuilder());
    node.setValue("hoi", "hoi");
    mycache.put("node", node);

    System.out.println("size of cache: " + mycache.getByteSize());

}

}
