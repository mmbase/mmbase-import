/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

import org.mmbase.storage.search.*;

/**
 * This cache provides a base implementation to cache the result of SearchQuery's. Such a cache
 * links a SearchQuery object to a list of MMObjectNodes.
 *
 * A cache entry is automaticly invalidated if arbitrary node of one of the types present in the
 * SearchQuery is changed (,created or deleted). This mechanism is not very subtle but it is
 * garanteed to be correct. It means though that your cache can be considerably less effective for
 * queries containing node types from which often node are edited.
 *
 * @author  Daniel Ockeloen
 * @author  Michiel Meeuwissen
 * @version $Id: QueryResultCache.java,v 1.4 2004-02-20 18:24:19 michiel Exp $
 * @since   MMBase-1.7
 * @see org.mmbase.storage.search.SearchQuery
 */


abstract public class QueryResultCache extends Cache {

    private static Logger log = Logging.getLoggerInstance(QueryResultCache.class);


    /**
     * Need reference to all existing these caches, to be able to invalidate them.
     */
    private static Set queryCaches = new HashSet();

    /**
     * Explicitely invalidates all Query caches for a certain builder. This is used in
     * MMObjectBuilder for 'local' changes, to ensure that imediate select after update always
     * works.
     * 
     * @return number of entries invalidated
     */
    public static  int invalidateAll(MMObjectBuilder builder) {
   
        int result = 0;
        while (builder != null) {
            String tn = builder.getTableName();
            Iterator i = queryCaches.iterator();
            while (i.hasNext()) {
                QueryResultCache cache = (QueryResultCache) i.next();
                    Observer observer = (Observer) cache.observers.get(tn);
                    if (observer != null) {
                        result += observer.nodeChanged();
                    }
            }
            builder = builder.getParentBuilder();
        }
        return result;
    }


    // Keep a map of the existing Observers, for each nodemanager one.
    // @todo I think it can be done with one Observer instance too, (in which case we can as well
    // let QueryResultCache implement MMBaseObserver itself)
    private Map observers = new HashMap();




     QueryResultCache(int size) {
        super(size);
        log.info("Instantiated a " + this.getClass().getName()); // should happend limited number of times
        queryCaches.add(this);
    }
  


    /**
     *
     * @throws ClassCastException if key not a SearchQuery or value not a List.
     */
    public synchronized Object put(Object key, Object value) {
        return put((SearchQuery) key, (List) value);
    }

    /**
     * Puts a  search result in this cache.
     */
    public synchronized Object put(SearchQuery query, List queryResult) { 
        if (! isActive()) return null;
        
        List n =  (List) super.get(query);
        if (n == null) {
            n = queryResult;
            addObservers(query);
        }
        return super.put(query, queryResult);        
    }
    
    /**
     * Removes an object from the cache. It alsos remove the watch from
     * the observers which are watching this entry.
     * 
     * @param key A SearchQuery object.
     */
    public synchronized Object remove(Object key) {
        Object result = super.remove(key);
        
        if (result != null) { // remove the key also from the observers.
            Iterator i = observers.values().iterator();
            while (i.hasNext()) {
                Observer o = (Observer) i.next();
                o.stopObserving(key);
            }
        }
        return result;
    }

    /**
     * Adds observers on the entry
     */
    private synchronized void addObservers(SearchQuery query) {
        Iterator i = query.getSteps().iterator();
        while (i.hasNext()) {
            Step step = (Step) i.next();
            String type = step.getTableName();
            Observer o;
            o = (Observer) observers.get(type);
            if (o == null) {
                o = new Observer(type);
                observers.put(type, o);
            }
            o.observe(query);
        }
    }


    /**
     * This observer subscribes itself to builder changes, and
     * invalidates the multilevel cache entries which are dependent on
     * that specific builder.
     */

    private class Observer implements MMBaseObserver {        
        /**
         * This set contains the types (as a string) which are to be invalidated.
         *
         */
        private Set cacheKeys = new HashSet(); // using java default for initial size. Someone tried 50.
        
        /**
         * Creates a multilevel cache observer for the speficied type
         * @param type Name of the builder which is to be observed.
         */
        private Observer(String type) {
            MMBase mmb = MMBase.getMMBase();
            // when the type is a role, we need to subscribe
            // the builder it belongs to..
            if(mmb.getMMObject(type) == null) {
                int builderNumber  = mmb.getRelDef().getNumberByName(type);
                String newType = mmb.getRelDef().getBuilder(builderNumber).getTableName();
                if (log.isDebugEnabled()) {
                    log.debug("replaced the type: " + type + " with type:" + newType);
                }
                type = newType;            
            }
            mmb.addLocalObserver (type, this);
            mmb.addRemoteObserver(type, this);
        }




        /**
         * If something changes this function is called, and the observer multilevel cache entries are removed.
         * @return number of keys invalidated
         */
        protected int nodeChanged() {

            List removedKeys = new ArrayList();
            // clear the entries from the cache.
            synchronized(this) {
                for (Iterator i = cacheKeys.iterator(); i.hasNext(); ) {
                    removedKeys.add(i.next());
                }
                cacheKeys.clear();
            }
            int result = removedKeys.size();
            if (result == 0) return 0;

            // remove now from Cache (and from other Observers)
            Iterator i = removedKeys.iterator();
            while (i.hasNext()) {
                Object key = i.next();
                QueryResultCache.this.remove(key);
            }

            return result;
        }
        

        // javadoc inherited (from MMBaseObserver)
        public boolean nodeRemoteChanged(String machine, String number,String builder,String ctype) {
            return nodeChanged() > 0; //machine, number, builder, ctype);
        }

        // javadoc inherited (from MMBaseObserver)
        public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
            // local changes are solved in MMObjectBuilder itself.
            // return nodeChanged() > 0; //machine, number, builder, ctype);
            return true;

        }
        
        /**
         * Start watching the entry with the specified key of this MultilevelCache (for this type).
         * @return true if it already was observing this entry.
         */
        protected synchronized boolean observe(Object key) {   
            // assert(MultilevelCache.this.containsKey(key));
            return cacheKeys.add(key);
        }

        /**
         * Stop observing this key of multilevelcache
         */
        protected synchronized boolean stopObserving(Object key) {            
            return cacheKeys.remove(key);
        }
    }
        
}
