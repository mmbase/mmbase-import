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
import org.mmbase.module.core.MMBaseContext;

import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.lang.reflect.*; // necessary for SizeOf

/**
 * A base class for all Caches. Extend this class for other caches.  
 *
 * @author Michiel Meeuwissen
 * @version $Id: Cache.java,v 1.8 2002-08-09 21:00:53 michiel Exp $
 */
abstract public class Cache extends LRUHashtable {

    private static Logger log = Logging.getLoggerInstance(Cache.class.getName());
    private static Map caches = new Hashtable();
   
    /**
     * Configures the caches using a config File. There is only one
     * config file now so the argument is al little overdone, but it
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
            log.info("Configuring caches with " + xmlReader.getFileName());
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
            }
        }        
    }


    /** 
    * The caches can be configured with an XML file, this file can
    * be changed which causes the caches to be reconfigured automaticly.
    */
    private static FileWatcher configWatcher = new FileWatcher (true) {
            protected void onChange(File file) {
                configReader = new XMLBasicReader(file.getAbsolutePath());
                configure(configReader);
            }
        };

    static { // configure
        log.debug("Static init of Caches");
        File configFile = new File(MMBaseContext.getConfigPath() + File.separator + "caches.xml");
        if (configFile.exists()) {
            configWatcher.add(configFile);
            configReader = new XMLBasicReader(configFile.getAbsolutePath());
            // configure(configReader); never mind, no cache are present on start up
        } else {
            log.warn("No cache configuration file " + configFile + " found");
        }
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();

    }


    private boolean active = true;


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
     * TODO: Writing back to caches.xml if necessary (if this call was nog caused by change of caches.xml itself)
     */
    public void setActive(boolean a) {
        active = a;
    }

    /**
     * Wether this cache is active or not.
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Returns the (approximate) bytesize of this cache
     * EXPERIMENTAL
     */

    public int getByteSize() { 
        return SizeOf.sizeof(this);
    }

    /**
     * Implementation of sizeof
     */

    static private class SizeOf {
        
        private static final int SZ_REF = 4;               
        private static int size_prim(Class t) { 
            if      (t == Boolean.TYPE)   return 1;
            else if (t == Byte.TYPE)      return 1;
            else if (t == Character.TYPE) return 2;
            else if (t == Short.TYPE)     return 2;
            else if (t == Integer.TYPE)   return 4;
            else if (t == Long.TYPE)      return 8;
            else if (t == Float.TYPE)     return 4;
            else if (t == Double.TYPE)    return 8;
            else if (t == Void.TYPE)      return 0;
            else return SZ_REF;
        }
        
        public static int sizeof(boolean b) { return 1; }        
        public static int sizeof(byte b)    { return 1; }        
        public static int sizeof(char c)    { return 2; }
        public static int sizeof(short s)   { return 2; }        
        public static int sizeof(int i)     { return 4; }
        public static int sizeof(long l)    { return 8; }        
        public static int sizeof(float f)   { return 4; }        
        public static int sizeof(double d)  { return 8; }
        public static int sizeof(Object obj) {
            System.out.println("sizeof object");
            if (obj == null) {
                return 0;
            }
            
            Class c = obj.getClass();
            
            if (c.isArray()) {
                System.out.println("an array");
                return size_arr(obj, c);
            } else {
                System.out.println("an object");
                if (obj instanceof Map)  return sizeof((Map) obj);
                return size_inst(obj, c);
            }
        }
        
        public static int sizeof(Map m) {
            System.out.println("sizeof Map");
            int len = size_inst(m, m.getClass());
            Iterator i = m.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                len += sizeof(entry.getKey());
                len += sizeof(entry.getValue());
            }
            return len;
        }
        

        private static int size_inst(Object obj, Class c) {
            Field flds[] = c.getDeclaredFields();
            int sz = 0;
            
            for (int i = 0; i < flds.length; i++) {
                Field f = flds[i];
                if (!c.isInterface() &&  (f.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                sz += size_prim(f.getType());
                try {
                    System.out.println("found a field " + f);
                    sz += sizeof(f.get(obj)); // recursion
                } catch (java.lang.IllegalAccessException e) {
                    // well...
                    System.out.println(e);

                }
            }
            
            if (c.getSuperclass() != null) {
                sz += size_inst(obj, c.getSuperclass());
            }
            
            Class cv[] = c.getInterfaces();
            for (int i = 0; i < cv.length; i++) {
                sz += size_inst(obj, cv[i]);
            }
            
            return sz;
        }
        
        private static int size_arr(Object obj, Class c) {
            Class ct = c.getComponentType();
            int len = Array.getLength(obj);
            
            if (ct.isPrimitive()) {
                return len * size_prim(ct);
            }
            else {
                int sz = 0;
                for (int i = 0; i < len; i++) {
                    sz += SZ_REF;
                    Object obj2 = Array.get(obj, i);
                    if (obj2 == null)
                        continue;
                    Class c2 = obj2.getClass();
                    if (!c2.isArray())
                        continue;
                    sz += size_arr(obj2, c2);
                }
                return sz;
            }
        }
       
    }
    public static void main(String args[]) {
        Cache mycache = new Cache(20) {  
                public String getName()        { return "test cache"; }
                public String getDescription() { return ""; }
            };
        System.out.println("putting some strings in cache");
        mycache.put("aaa", "AAA"); // 6 bytes
        mycache.put("bbb", "BBB"); // 6 bytes

        System.out.println("putting an hashmap in cache");
        Map m = new HashMap();
        m.put("ccc", "CCC");      
        m.put("ddd", "DDD"); 
        
        mycache.put("eee", m);   // 24 bytes

        mycache.put("fff", mycache); 

        System.out.println("size of cache: " + mycache.getByteSize());

    }
}
