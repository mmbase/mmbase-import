/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.util.logging.*;

import org.mmbase.storage.search.*;

/**
 * This cache handles query results from the bridge.  A SearchQuery object serves as key, so
 * ideally, this cache could simply work for <em>all</em> select queries done by MMBase (because the
 * should all be performed via a SearchQuery object).
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: QueryResultCache.java,v 1.1 2003-07-14 20:58:41 michiel Exp $
 * @since MMBase-1.7
 */


abstract public class QueryResultCache extends Cache {

    private static Logger log = Logging.getLoggerInstance(QueryResultCache.class);


    // Keep a map of the existing Observers, for each nodemanager one.
    private Map observers = new HashMap();


    QueryResultCache(int size) {
        super(size);
    }

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
     * Remove an object from the cache. It also remove the watch from
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
         * This list contains the types (as a string) which are to be invalidated.
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
         */
        protected boolean nodeChanged(String machine, String number, String builder, String ctype) {

            List removedKeys = new ArrayList();
            // clear the entries from the cache.
            synchronized(this) {
                for (Iterator i = cacheKeys.iterator(); i.hasNext(); ) {
                    removedKeys.add(i.next());
                }
                cacheKeys.clear();
            }
            // remove now from Cache (and from other Observers)
            Iterator i = removedKeys.iterator();
            while (i.hasNext()) {
                Object key = i.next();
                QueryResultCache.this.remove(key);
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
