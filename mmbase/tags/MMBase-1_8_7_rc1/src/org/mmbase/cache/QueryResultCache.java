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

import org.mmbase.bridge.implementation.BasicQuery;

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
 * @version $Id$
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.SearchQuery
 */

abstract public class QueryResultCache extends Cache implements NodeEventListener, RelationEventListener {

    private static final Logger log = Logging.getLoggerInstance(QueryResultCache.class);

    /**
     * This map contains the possible counts of queries grouped by type in this cache.
     * A query with multiple steps (types) will increase all counters.
     * A relation role name is considered a type
     * This cache will not invalidate when an event does not mention one of these types
     * The cache will be evaluated when a parent type is in this map.
     */
    private Map typeCounters = new HashMap();


    /**
     * This is the default release strategy. Actually it is a container for any
     * number of 'real' release strategies
     *
     * @see ChainedReleaseStrategy
     */
    private final ChainedReleaseStrategy releaseStrategy;

    public QueryResultCache(int size) {
        super(size);
        releaseStrategy = new ChainedReleaseStrategy();
        log.debug("Instantiated a " + this.getClass().getName() + " (" + releaseStrategy + ")"); // should happen limited number of times
        MMBase.getMMBase().addNodeRelatedEventsListener("object", this);
    }

    /**
     * @param strategies
     */
    public void addReleaseStrategies(List strategies) {
        if (strategies != null) {
            for (Iterator iter = strategies.iterator(); iter.hasNext();) {
                ReleaseStrategy element = (ReleaseStrategy) iter.next();
                log.debug(("adding strategy " + element.getName() + " to cache " + getName()));
                addReleaseStrategy(element);
            }
        }
    }

    /**
     * This method lets you add a release strategy to the cache. It will in fact
     * be added to <code>ChainedReleaseStrategy</code>, which
     * is the default base release strategy.
     * @param releaseStrategy A releaseStrategy to add.
     */
    public void addReleaseStrategy(ReleaseStrategy releaseStrategy) {
        this.releaseStrategy.addReleaseStrategy(releaseStrategy);
    }

    /**
     * @return Returns the releaseStrategy.
     */
    public ChainedReleaseStrategy getReleaseStrategy() {
        return releaseStrategy;
    }

    /**
     * @throws ClassCastException if key not a SearchQuery or value not a List.
     */
    public Object put(Object key, Object value) {
        if (key instanceof BasicQuery) {
            return put(((BasicQuery) key).getQuery(), (List) value);
        }

        return put((SearchQuery) key, (List) value);
    }

    /**
     * Puts a search result in this cache.
     * @param query
     * @param queryResult
     */
    public Object put(SearchQuery query, List queryResult) {
        if (!checkCachePolicy(query)) return null;
        synchronized(lock) {
            increaseCounters(query, typeCounters);
            return super.put(query, queryResult);
        }
    }

    /**
     * @throws ClassCastException if key not a SearchQuery or value not a List.
     */
    public Object remove(Object key) {
        if (key instanceof BasicQuery) {
            return remove(((BasicQuery) key).getQuery());
        }

        return remove((SearchQuery) key);
    }


    /**
     * Removes an object from the cache. It alsos remove the watch from the
     * observers which are watching this entry.
     *
     * @param key A SearchQuery object.
     */
    public Object remove(SearchQuery query) {
        synchronized(lock) {
            Object result = super.remove(query);
            if (result != null) decreaseCounters(query, typeCounters);
            return result;
        }
    }

    private void increaseCounters(SearchQuery query, Map counters) {
        for (Iterator iter = query.getSteps().iterator(); iter.hasNext();) {
            Step step = (Step) iter.next();
            String stepName = step.getTableName();
            if (counters.containsKey(stepName)) {
                int count = ((Integer) counters.get(stepName)).intValue();
                counters.put(stepName, new Integer(count + 1));
            } else {
                counters.put(stepName, new Integer(1));
            }
        }
    }

    private void decreaseCounters(SearchQuery query, Map counters) {
        for (Iterator iter = query.getSteps().iterator(); iter.hasNext();) {
            Step step = (Step) iter.next();
            String stepName = step.getTableName();
            if (counters.containsKey(stepName)) {
                int count = ((Integer) counters.get(stepName)).intValue();
                if (count > 1) {
                    counters.put(stepName, new Integer(count - 1));
                } else {
                    counters.remove(stepName);
                }
            }
        }
    }


    public String toString() {
        return this.getClass().getName() + " " + getName();
    }

    /**
     * @see org.mmbase.core.event.RelationEventListener#notify(org.mmbase.core.event.RelationEvent)
     */
    public void notify(RelationEvent event) {
        if(containsType(event)) {
            nodeChanged(event);
        }
    }

    private boolean containsType(RelationEvent event) {
        if (typeCounters.containsKey("object")) {
            return true;
        }
        if (typeCounters.containsKey(event.getRelationSourceType())
                || typeCounters.containsKey(event.getRelationDestinationType())) {
            return true;
        }
        MMBase mmb = MMBase.getMMBase();
        String roleName = mmb.getRelDef().getBuilderName(new Integer(event.getRole()));
        if (typeCounters.containsKey(roleName)) {
            return true;
        }
        MMObjectBuilder srcbuilder = mmb.getBuilder(event.getRelationSourceType());
        if (srcbuilder == null) {
            return false;
        }
        for (Iterator iter = srcbuilder.getAncestors().iterator(); iter.hasNext();) {
            MMObjectBuilder parent = (MMObjectBuilder) iter.next();
            if (typeCounters.containsKey(parent.getTableName())) {
                return true;
            }
        }
        MMObjectBuilder destbuilder = mmb.getBuilder(event.getRelationDestinationType());
        if (destbuilder == null) {
            return false;
        }
        for (Iterator iter = destbuilder.getAncestors().iterator(); iter.hasNext();) {
            MMObjectBuilder parent = (MMObjectBuilder) iter.next();
            if (typeCounters.containsKey(parent.getTableName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.mmbase.core.event.NodeEventListener#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (containsType(event)) {
            nodeChanged(event);
        }
    }

    private boolean containsType(NodeEvent event) {
        synchronized(lock) {
            if (typeCounters.containsKey("object")) {
                return true;
            }
            if (typeCounters.containsKey(event.getBuilderName())) {
                return true;
            }

            MMBase mmb = MMBase.getMMBase();
            MMObjectBuilder destBuilder = mmb.getBuilder(event.getBuilderName());
            if (destBuilder == null) {  // builder is not even available
                return false;
            }
            for (Iterator iter = destBuilder.getAncestors().iterator(); iter.hasNext();) {
                MMObjectBuilder parent = (MMObjectBuilder) iter.next();
                if (typeCounters.containsKey(parent.getTableName())) {
                    return true;
                }
            }
            return false;
        }
    }

    protected int nodeChanged(Event event) throws IllegalArgumentException{
        if (log.isDebugEnabled()) {
            log.debug("Considering " + event);
        }
        Set cacheKeys;
        Map oldTypeCounters;
        synchronized(lock) {
            cacheKeys = new HashSet(keySet());
            oldTypeCounters = new HashMap(typeCounters);
        }

        Set removeKeys = new HashSet();
        Map foundTypeCounters = new HashMap();

        evaluate(event, cacheKeys, removeKeys, foundTypeCounters);

        Iterator removeIter = removeKeys.iterator();
        while(removeIter.hasNext()) {
            remove(removeIter.next());
        }

        synchronized(lock) {
            // types in the oldTypesCounter which are not in the typeCounters are removed during the
            // evaluation of the keys and are not relevant anymore.
            for (Iterator iter = typeCounters.keySet().iterator(); iter.hasNext();) {
                String type = (String) iter.next();
                if (foundTypeCounters.containsKey(type)) {
                    if (oldTypeCounters.containsKey(type)) {
                        // adjust counter
                        int oldValue = ((Integer) oldTypeCounters.get(type)).intValue();
                        int guessedValue = ((Integer) typeCounters.get(type)).intValue();
                        int foundValue = ((Integer) foundTypeCounters.get(type)).intValue();
                        if (guessedValue - oldValue > 0) {
                            int newValue = foundValue + (guessedValue - oldValue);
                            foundTypeCounters.put(type, new Integer(newValue));
                        }
                    } else {
                        int guessedValue = ((Integer) typeCounters.get(type)).intValue();
                        int foundValue = ((Integer) foundTypeCounters.get(type)).intValue();
                        int newValue = foundValue + guessedValue;
                        foundTypeCounters.put(type, new Integer(newValue));
                    }
                } else {
                    Integer guessedValue = (Integer) typeCounters.get(type);
                    foundTypeCounters.put(type, guessedValue);
                }
            }
            typeCounters = foundTypeCounters;
        }
        return removeKeys.size();
    }

    // if I
    private void evaluate(Event event, Set cacheKeys, Set removeKeys, Map foundTypeCounters) {
        int evaluatedResults = cacheKeys.size();
        long startTime = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("Considering " + cacheKeys.size() + " objects in " + QueryResultCache.this.getName() + " for flush because of " + event);
        }
        Iterator i = cacheKeys.iterator();
        while(i.hasNext()) {
            SearchQuery key = (SearchQuery) i.next();

            boolean shouldRelease;
            if(releaseStrategy.isEnabled()){
                if(event instanceof NodeEvent){
                    shouldRelease = releaseStrategy.evaluate((NodeEvent)event, key, (List) get(key)).shouldRelease();
                } else if (event instanceof RelationEvent){
                    shouldRelease = releaseStrategy.evaluate((RelationEvent)event, key, (List) get(key)).shouldRelease();
                } else {
                    log.error("event " + event.getClass() + " " + event + " is of unsupported type");
                    shouldRelease = false;
                }
            } else {
                shouldRelease = true;
            }

            if (shouldRelease) {
                removeKeys.add(key);
            } else {
                increaseCounters(key, foundTypeCounters);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(QueryResultCache.this.getName() + ": event analyzed in " + (System.currentTimeMillis() - startTime)  + " milisecs. evaluating " + evaluatedResults + ". Flushed " + removeKeys.size());
        }
    }

    public void clear(){
        super.clear();
        releaseStrategy.clear();
    }
}
