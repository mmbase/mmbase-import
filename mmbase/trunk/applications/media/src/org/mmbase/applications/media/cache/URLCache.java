/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.media.cache;

import org.mmbase.cache.Cache;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMObjectNode;

import java.util.*;

/**
 * A cache for URLS requested in the MediaFragment builder.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class URLCache extends Cache {
    private static int cacheSize = 4 * 1024;    // Max size of the node cache
    private static URLCache cache;
    private CacheExpire cacheExpire = new CacheExpire();
    private static Logger log = Logging.getLoggerInstance(URLCache.class);

    public static URLCache getCache() {
        return cache;
    }

    static {
        cache = new URLCache(cacheSize);
        putCache(cache);
    }

    /**
     * creates a key based of the media fragment number and the user information
     * @return key to be cached
     */
    static public String toKey(MMObjectNode mediafragment, Map info) {
        String toKey = "MediaFragmentNumber="+mediafragment.getNumber();
	for(Iterator infoItems=info.keySet().iterator();infoItems.hasNext();) {
            String key = (String)infoItems.next();
            toKey+=","+key+"="+info.get(key);
	}
	log.debug("Generated key="+toKey);
	return toKey;
    }

    /**
     * put an entry in the cache
     * @param objects The objects that can invalidate the cache 
     */
    public synchronized void put(String key, String result, Vector objects) {
	cache.put(key, result);
	if(objects!=null) {
		cacheExpire.put(objects,key);	
	} else {
		log.debug("No objects are specified to expire the cache entries");
	}
    }

    /**
     * signal if a node change to see if cache entries have to be invalidated.
     * @param objects The object that can invalidate the cache
     */
    public synchronized void nodeChange(String object) {
	cacheExpire.remove(object);
    } 

    public String getName() {
        return "MediaURLS";
    }
    public String getDescription() {
        return "This cache contains the evaluated urls of media fragments (with user information)";
    }

    /**
     * Creates the Cache
     */
    private URLCache(int size) {
        super(size);
    }

	/**	
	 * Contains information about which objects are used to create a certain cache entry.
	 * If an object changes it is a good idea to assume that the cache entry is invalid.
	 */
	class CacheExpire {
		private Hashtable objectnumber2key = new Hashtable(10000);
	
		/**
	 	 * add objects that were needed for the creation of a cache entry
		 * @param obj A vector with object numbers (Strings).
		 * @param key The key of the cache entry to invalidate if an object changes.
		 */	
		private void put(Vector obj, String key) {
			for(Iterator objects = obj.iterator();objects.hasNext();) {
				put((String)objects.next(), key);
			}
		}

		/**
	 	 * add object that was needed for the creation of a cache entry
		 * @param obj A object number.
		 * @param key The key of the cache entry to invalidate if an object changes.
		 */	
		private void put(String object, String key) {
			Vector keyList = null;
			if(objectnumber2key.contains(object)) {
				keyList = (Vector)objectnumber2key.get(object);
			} else {
				keyList = (Vector)objectnumber2key.put(object,new Vector(20));
			}
			keyList.add(key);
		}

		/**
		 * remove all entries form the cache that are invalidated by the change of the object.
		 * @param object the object that changes
		 */
		private void remove(String object) {
			if(objectnumber2key.contains(object)) {
				Vector keyList = (Vector)objectnumber2key.get(object);
				for(Iterator items = keyList.iterator(); items.hasNext();) {
					cache.remove((String)items.next());
				}
			} 
		}
	}
}
