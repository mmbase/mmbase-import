/*
 * This software is OSI Certified Open Source Software. OSI Certified is a
 * certification mark of the Open Source Initiative. The license (Mozilla
 * version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.cache;

import java.util.*;

import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.core.event.RelationEventListener;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

import org.mmbase.storage.search.*;

/**
 * This cache provides a base implementation to cache the result of
 * SearchQuery's. Such a cache links a SearchQuery object to a list of
 * MMObjectNodes. A cache entry is automaticly invalidated if arbitrary node of
 * one of the types present in the SearchQuery is changed (,created or deleted).
 * This mechanism is not very subtle but it is garanteed to be correct. It means
 * though that your cache can be considerably less effective for queries
 * containing node types from which often node are edited.
 * 
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @author Bunst Eunders
 * @version $Id: QueryResultCache.java,v 1.14 2005-09-20 11:51:19 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.SearchQuery
 */

abstract public class QueryResultCache extends Cache {

    private static final Logger log = Logging.getLoggerInstance(QueryResultCache.class);

    /**
     * Need reference to all existing these caches, to be able to invalidate
     * them.
     */
    private static final Map queryCaches = new HashMap();

    /**
     * This is the default release strategy. Actually it is a container for any
     * number of 'real' release strategies
     * 
     * @see ChainedReleaseStrategy
     */
    private ChainedReleaseStrategy releaseStrategy;

    /**
     * Explicitely invalidates all Query caches for a certain builder. This is
     * used in MMObjectBuilder for 'local' changes, to ensure that imediate
     * select after update always works.
     * 
     * @return number of entries invalidated
     */
    /*
    public static int invalidateAll(MMObjectNode node, int eventType) { 
        int result = 0; 
        MMObjectBuilder builder = node.getBuilder();
        while (builder != null) { 
            String tn = builder.getTableName(); 
            Iterator i = queryCaches.entrySet().iterator(); 
            while (i.hasNext()) { 
                Map.Entry entry = (Map.Entry) i.next(); 
                QueryResultCache cache = (QueryResultCache) entry.getValue(); 
                // get the Observers for the builder: 
                Observer observer = (Observer) cache.observers.get(tn); 
                if (observer != null) { 
                    result += observer.nodeChanged(new NodeEvent(node, eventType));
                } 
            } 
            builder = builder.getParentBuilder(); 
        } 
        return result; 
    }
    */

    // Keep a map of the existing Observers, for each nodemanager one.
    // @todo I think it can be done with one Observer instance too, (in which
    // case we can as well
    // let QueryResultCache implement MMBaseObserver itself)
    private Map observers = new HashMap();

    QueryResultCache(int size) {
        super(size);
        releaseStrategy = new ChainedReleaseStrategy();
        log.info("Instantiated a " + this.getClass().getName()); // should happen limited number of times
        if (queryCaches.put(this.getName(), this) != null) {
            log.error("" + queryCaches + "already containing " + this + "!!");
        }
    }

    /**
     * @param strategies
     */
    public void addReleaseStrategies(List strategies) {
        if (strategies != null) {
            for (Iterator iter = strategies.iterator(); iter.hasNext();) {
                AbstractReleaseStrategy element = (AbstractReleaseStrategy) iter
                    .next();
                log.debug(("adding strategy " + element.getName()
                    + " to cache " + getName()));
                addReleaseStrategy(element);
            }
        }
    }

    /**
     * This method lets you add a release strategy to the cache. It will in fact
     * be added to <code>ChainedReleaseStrategy</codde>, which
     * is the default base release strategy.
     * @param releaseStrategy A releaseStrategy to add.
     */
    public void addReleaseStrategy(AbstractReleaseStrategy releaseStrategy) {
        ((ChainedReleaseStrategy) this.releaseStrategy)
            .addReleaseStrategy(releaseStrategy);
    }

    /**
     * @return Returns the releaseStrategy.
     */
    AbstractReleaseStrategy getReleaseStrategy() {
        return releaseStrategy;
    }

    /**
     * @throws ClassCastException if key not a SearchQuery or value not a List.
     */
    public synchronized Object put(Object key, Object value) {
        return put((SearchQuery) key, (List) value);
    }

    /**
     * Puts a search result in this cache.
     */
    public synchronized Object put(SearchQuery query, List queryResult) {
        if (!checkCachePolicy(query)) return null;

        List n = (List) super.get(query);
        if (n == null) {
            addObservers(query);
        }
        return super.put(query, queryResult);
    }

    /**
     * Removes an object from the cache. It alsos remove the watch from the
     * observers which are watching this entry.
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
        MMBase.getMMBase();

        Iterator i = query.getSteps().iterator();
        while (i.hasNext()) {
            Step step = (Step) i.next();
            if (step instanceof RelationStep) {
                continue;
            }
            String type = step.getTableName();

            Observer o = (Observer) observers.get(type);
            if (o == null) {
                o = new Observer(type);
                observers.put(type, o);
            }
            o.observe(query);
        }
    }

    public String toString() {
        return this.getClass().getName() + " " + getName();
    }

    /**
     * This observer subscribes itself to builder changes, and invalidates the
     * multilevel cache entries which are dependent on that specific builder.
     */

    private class Observer implements NodeEventListener, RelationEventListener {
        /**
         * This set contains the types (as a string) which are to be
         * invalidated.
         */
        private Set cacheKeys = new HashSet(); // using java default for
                                                // initial size. Someone tried
                                                // 50.

        private String type;

        /**
         * Creates a multilevel cache observer for the speficied type
         * 
         * @param type Name of the builder which is to be observed.
         */
        private Observer(String type) {
            this.type = type;
            MMBase mmb = MMBase.getMMBase();
            // when the type is a role, we need to subscribe
            // the builder it belongs to..
            if (mmb.getMMObject(type) == null) {
                int builderNumber = mmb.getRelDef().getNumberByName(type);
                String newType = mmb.getRelDef().getBuilder(builderNumber).getTableName();
                if (log.isDebugEnabled()) {
                    log.debug("replaced the type: " + type + " with type:" + newType);
                }
                type = newType;
            }
            mmb.addNodeRelatedEventsListener(type, this);
        }

        /**
         * Start watching the entry with the specified key of this
         * MultilevelCache (for this type).
         * 
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

        public String toString() {
            return "Observer  " + super.toString() + " watching " + cacheKeys.size() + " keys";
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mmbase.core.event.RelationEventListener#notify(org.mmbase.core.event.RelationEvent)
         */
        public void notify(RelationEvent event) {
            // let's test if this event should be handeled. The event is
            // being propagated by a mmObjectBuilder to it's ancestors.
            // I don't think we want to haldle these events as well.
            if (event.getRelationSourceType().equals(type)
                || event.getRelationDestinationType().equals(type)) {
                nodeChanged(event);
            } else {
                log.debug("node event was deflected by Observer for type: " + type);
                log.debug(event.toString());
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mmbase.core.event.EventListener#getConstraintsForEvent(org.mmbase.core.event.Event)
         */
        public Properties getConstraintsForEvent(Event event) {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mmbase.core.event.NodeEventListener#notify(org.mmbase.core.event.NodeEvent)
         */
        public void notify(NodeEvent event) {
            // let's test if this event should be handeled. The event is
            // being propagated by a mmObjectBuilder to it's ancestors.
            // I don't think we want to haldle these events as well.
            if (event.getBuilderName().equals(type)) {
                nodeChanged(event);
            } else {
                log.debug("node event was deflected by Observer for type: " + type);
                log.debug(event.toString());
            }

        }

        protected int nodeChanged(NodeEvent event) {
            int evaluatedResults = cacheKeys.size();
            Set removeKeys = new HashSet();
            long totalEvaluationTime = 0;
            synchronized (this) {
                for (Iterator i = cacheKeys.iterator(); i.hasNext();) {
                    SearchQuery key = (SearchQuery) i.next();
                    AbstractReleaseStrategy.StrategyResult result = releaseStrategy.evaluate(event, key, (List) get(key));
                    if (result.shouldRelease()) {
                        removeKeys.add(key);
                        i.remove();
                    }
                    totalEvaluationTime += result.getCost();
                }
                
                // ernst: why is this in a separate loop?
                // why not chuck em out in the first one?

                for (Iterator i = removeKeys.iterator(); i.hasNext();) {
                    QueryResultCache.this.remove(i.next());
                }
            }
            if (log.isDebugEnabled()) {
                log.debug(getName() + ": event analyzed in " + totalEvaluationTime + " milisecs. evaluating " + evaluatedResults + ". Flushed " + removeKeys.size());
            }
            return removeKeys.size();
        }
    }

}
