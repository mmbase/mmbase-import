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
 * A cache for URLS requested in the MediaFragment builder.
 * This cache can easily be extended in such a way it can cache more requests.
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen
 */
public class URLCache extends Cache<String, String> {
    private static int cacheSize = 4 * 1024;    // Max size of the node cache
    private static URLCache cache;
    private static Logger log = Logging.getLoggerInstance(URLCache.class);

    private CacheExpire cacheExpire = new CacheExpire();
    private Observer observer = new Observer();

    public static URLCache getCache() {
        return cache;
    }

    static {
        cache = new URLCache(cacheSize);
        cache.putCache();
    }

    /**
     * creates a key based of the media fragment number and the user information
     * @param mediaFragment fragment to be cached
     * @param info user information to be cached
     * @return key to be cached
     */
    static public String toKey(MMObjectNode mediaFragment, Map<String, Object> info) {

        StringBuffer toKey = new StringBuffer("MediaFragmentNumber=").append(mediaFragment.getNumber());
        Iterator<Map.Entry<String, Object>> infoItems = info.entrySet().iterator();
        while (infoItems.hasNext()) {
            Map.Entry<String, Object> entry  = infoItems.next();
            toKey.append(',').append(entry.getKey()).append('=').append(entry.getValue());
        }
        if (log.isDebugEnabled()) {
            log.debug("Generated key=" + toKey);
        }
        return toKey.toString();
    }

    /**
     * put an entry in the cache
     * @param key cache key
     * @param result  cache result
     * @param objects the objects that can invalidate the cache
     */
    public synchronized void put(String key, String result, Set<MMObjectNode> objects) {
        cache.put(key, result);
        log.debug("Adding to cache, key="+key);
        if(objects!=null) {
            cacheExpire.put(objects,key);
        } else {
            log.debug("No objects are specified to expire the cache entries");
        }
    }

    public void clear() {
        super.clear();
        cacheExpire.clear();
    }

    /**
     * Invalidates cache entries based on the node that changes
     * @param nodeNumber number of node that changes
     */
    public synchronized void nodeChange(String nodeNumber) {
        log.debug("Node changed, number=" + nodeNumber);
        cacheExpire.remove(nodeNumber);
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
        private Cache<String, Vector<String>> objectNumber2Keys = new Cache<String, Vector<String>>(10000) {
            public String getName()        { return "Media objectnumber-to-keys cache"; }
            public String getDescription() { return "Contains information about which objects are used to create a certain cache entry."; }
        };

        public CacheExpire() {
            objectNumber2Keys.putCache();
        }

        public void clear() {
            objectNumber2Keys.clear();
        }

        /**
         * add objects that were needed for the creation of a cache entry
         * @param obj A vector with object numbers (Strings).
         * @param key The key of the cache entry to invalidate if an object changes.
         */
        private void put(Set<MMObjectNode> obj, String key) {
            for (MMObjectNode objectNode : obj) {
                put(objectNode, key);
            }
        }

        /**
         * add object that was needed for the creation of a cache entry
         * @param obj A object number.
         * @param key The key of the cache entry to invalidate if an object changes.
         */
        private void put(MMObjectNode node, String key) {
            if(node==null) {
                return;
            }
            Vector<String> keyList = null;
            String objectNumber = ""+node.getNumber();
            if(objectNumber2Keys.containsKey(objectNumber)) {
                keyList = objectNumber2Keys.get(objectNumber);
            } else {
                keyList = new Vector<String>(20);
                objectNumber2Keys.put(objectNumber,keyList);
            }
            keyList.add(key);
            observer.put(node);
        }

        /**
         * remove all entries form the cache that are invalidated by the change of the object.
         */
        private void remove(String nodeNumber) {
            if(objectNumber2Keys.containsKey(nodeNumber)) {
                Vector<String> keyList = objectNumber2Keys.get(nodeNumber);
                for (String key : keyList) {
                    cache.remove(key);
                    log.debug("Flusing key from cache, key="+key);
                }
            }
        }
    }

    /**
     * this observer will listen to all builders that are used while creating cache entries.
     * if an object changes (that is used during the creation of the cache entrie) that specific
     * cache entrie will be flushed.
     */
    private class Observer implements MMBaseObserver  {
        private Set<String> observingBuilders = new HashSet<String>(); // the builders in which 'this' was registered already.

        Observer() {
        }

        /**
         * start listning to node changes of this builder
         */
        private void addToObservingBuilder(MMObjectBuilder builder) {
            log.debug("Adding observer for builder = "+builder.getTableName());
            builder.addLocalObserver(this);
            builder.addRemoteObserver(this);
            observingBuilders.add(builder.getTableName());
        }

        /**
         * makes sure that the observer will listen to the builder of this node
         */
        synchronized void put(MMObjectNode node) {
            MMObjectBuilder bul = node.getBuilder();
            if (!observingBuilders.contains(bul.getTableName())) {
                addToObservingBuilder(bul);
            }
        }

        /**
         * If something changes this function is called, and the observer multilevel cache entries are removed.
         */
        protected boolean nodeChanged(String machine, String number, String builder, String ctype) {
            // d=delete, c=create
            if (ctype.equals("d") || ctype.equals("c")) {
                log.debug("Recieved a change, object number = "+number);
                nodeChange(number);
            }
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
