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

import org.mmbase.module.core.*;

import java.util.*;

/**
 * A cache for URL's requested in the MediaFragment builder.
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen
 */
public class URLCache extends Cache {
    private static final int CACHE_SIZE = 4 * 1024;
    private static URLCache cache;
    private static Logger log = Logging.getLoggerInstance(URLCache.class);

    public static URLCache getCache() {
        return cache;
    }

    static {
        cache = new URLCache();
        cache.putCache();
    }

    private Observer observer = new Observer();

    /**
     * creates a key based of the media fragment number and the user information
     * @return key to be cached
     */
    static public String toKey(MMObjectNode mediaFragment, Map info) {
        
        StringBuffer toKey = new StringBuffer("MediaFragmentNumber=").append(mediaFragment.getNumber());
        Iterator infoItems = info.entrySet().iterator();
	while (infoItems.hasNext()) {
            Map.Entry entry  = (Map.Entry)infoItems.next();
            toKey.append(',').append(entry.getKey()).append(',').append(entry.getValue());
	}
        if (log.isDebugEnabled()) {
            log.debug("Generated key=" + toKey);
        }
	return toKey.toString();
    }

    /**
     * put an entry in the cache
     * @param objects The objects that can invalidate the cache 
     */
    public synchronized void put(String key, String result, List objects) {
	put(key, result);
	if(objects != null) {
            observer.put(objects, key);	
	} else {
            log.debug("No objects are specified to expire the cache entries");
	}
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
    private URLCache() {
        super(CACHE_SIZE);
    }

    /**	
     * Contains information about which objects are used to create a certain cache entry.
     * If an object changes it is a good idea to assume that the cache entry is invalid.
     */
    private class Observer implements MMBaseObserver {
        private Map objectNumber2Keys = new HashMap(10000);
	
        private Set observingBuilders = new HashSet(); // the builders in which 'this' was registered already.

        private Observer() { 
        }


        /**
         * add objects that were needed for the creation of a cache entry
         * @param obj A vector with object numbers (Strings).
         * @param key The key of the cache entry to invalidate if an object changes.
         */	
        synchronized void put(List obj, String key) {
            for(Iterator objects = obj.iterator(); objects.hasNext();) {
                put((MMObjectNode)objects.next(), key);
            }
        }
        
        private void addToObservingBuilder(MMObjectBuilder bul) {
            bul.addLocalObserver(this);
            bul.addRemoteObserver(this);
            observingBuilders.add(bul.getTableName());
        }

        /**
         * add object that was needed for the creation of a cache entry
         * @param obj A object number.
         * @param key The key of the cache entry to invalidate if an object changes.
         */	
        synchronized void put(MMObjectNode object, String key) {
            MMObjectBuilder bul = object.parent;
            if (! observingBuilders.contains(bul.getTableName())) {
                addToObservingBuilder(bul);                
            }

            String objectNumber = "" + object.getNumber();
            List keys = null;
            if(objectNumber2Keys.containsKey(objectNumber)) {
                keys = (List)objectNumber2Keys.get(objectNumber);
            } else {
                keys = (List)objectNumber2Keys.put(objectNumber, new ArrayList(20));
            }
            keys.add(key);

        }
        
        /**
         * remove all entries form the cache that are invalidated by the change of the object.
         * @param object the object that changes
         */
        synchronized void remove(String object) {
            if(objectNumber2Keys.containsKey(object)) {
                List keyList = (List)objectNumber2Keys.get(object);
                Iterator i = keyList.iterator();
                while (i.hasNext()) {
                    URLCache.this.remove((String)i.next());
                    i.remove();
                }
            } 
        }


        /**
         * If something changes this function is called, and the observer multilevel cache entries are removed.
         */
        protected boolean nodeChanged(String machine, String number, String builder, String ctype) {
            remove(number);
            return true;
        }

        // javadoc inherited (from MMBaseObserver)
        public boolean nodeRemoteChanged(String machine, String number,String builder,String ctype) {
            return nodeChanged(machine, number, builder, ctype);
        }

        // javadoc inherited (from MMBaseObserver)
        public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
            return nodeChanged(machine, number, builder, ctype);
        }



    }
}
