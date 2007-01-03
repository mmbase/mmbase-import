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
 * @version $Id: QueryResultCache.java,v 1.39 2007-01-03 09:16:21 nklasens Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.SearchQuery
 */

abstract public class QueryResultCache extends Cache<SearchQuery, List<MMObjectNode>> implements NodeEventListener, RelationEventListener {

    private static final Logger log = Logging.getLoggerInstance(QueryResultCache.class);

    /**
     * This map contains the possible counts of queries grouped by type in this cache.
     * A query with multiple steps (types) will increase all counters. 
     * A relation role name is considered a type
     * This cache will not invalidate when an event does not mention one of these types
     * The cache will be evaluated when a parent type is in this map.
     */
    private Map<String, Integer> typeCounters = new HashMap<String, Integer>();
    

    /**
     * This is the default release strategy. Actually it is a container for any
     * number of 'real' release strategies
     *
     * @see ChainedReleaseStrategy
     */
    private final ChainedReleaseStrategy releaseStrategy;

    QueryResultCache(int size) {
        super(size);
        releaseStrategy = new ChainedReleaseStrategy();
        log.debug("Instantiated a " + this.getClass().getName() + " (" + releaseStrategy + ")"); // should happen limited number of times
        MMBase.getMMBase().addNodeRelatedEventsListener("object", this);
    }

    /**
     * @param strategies
     */
    public void addReleaseStrategies(List<ReleaseStrategy> strategies) {
        if (strategies != null) {
            for (ReleaseStrategy element : strategies) {
                if (log.isDebugEnabled()) {
                    log.debug(("adding strategy " + element.getName() + " to cache " + getName()));
                }
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
     * Puts a search result in this cache.
     */
    public synchronized List<MMObjectNode> put(SearchQuery query, List<MMObjectNode> queryResult) {
        if (!checkCachePolicy(query)) return null;
        if (query instanceof BasicQuery) {
            query = ((BasicQuery) query).getQuery();
        }
        increaseCounters(query, typeCounters);
        return super.put(query, queryResult);
    }

    /**
     * Removes an object from the cache. It alsos remove the watch from the
     * observers which are watching this entry.
     *
     * @param key A SearchQuery object.
     */
    public synchronized List<MMObjectNode> remove(SearchQuery key) {
        if (key instanceof BasicQuery) {
            key = ((BasicQuery) key).getQuery();
        }
        List<MMObjectNode> result = super.remove(key);
        decreaseCounters(key, typeCounters);
        return result;
    }

    private void increaseCounters(SearchQuery query, Map<String, Integer> counters) {
        for (Iterator iter = query.getSteps().iterator(); iter.hasNext();) {
            Step step = (Step) iter.next();
            String stepName = step.getTableName();
            if (counters.containsKey(stepName)) {
                int count = counters.get(stepName);
                counters.put(stepName, new Integer(count + 1));
            }
            else {
                counters.put(stepName, new Integer(1));
            }
        }
    }

    private void decreaseCounters(SearchQuery query, Map<String, Integer> counters) {
        for (Iterator iter = query.getSteps().iterator(); iter.hasNext();) {
            Step step = (Step) iter.next();
            String stepName = step.getTableName();
            if (counters.containsKey(stepName)) {
                int count = counters.get(stepName);
                if (count > 1) {
                    counters.put(stepName, new Integer(count - 1));
                }
                else {
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
        String roleName = mmb.getRelDef().getBuilderName(Integer.valueOf(event.getRole()));
        if (typeCounters.containsKey(roleName)) {
            return true;
        }
        MMObjectBuilder srcbuilder = mmb.getMMObject(event.getRelationSourceType());
        for (Iterator iter = srcbuilder.getAncestors().iterator(); iter.hasNext();) {
            MMObjectBuilder parent = (MMObjectBuilder) iter.next();
            if (typeCounters.containsKey(parent.getTableName())) {
                return true;
            }
        }
        MMObjectBuilder destbuilder = mmb.getMMObject(event.getRelationDestinationType());
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
        if (typeCounters.containsKey("object")) {
            return true;
        }
        if (typeCounters.containsKey(event.getBuilderName())) {
            return true;
        }
        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder destbuilder = mmb.getMMObject(event.getBuilderName());
        for (Iterator iter = destbuilder.getAncestors().iterator(); iter.hasNext();) {
            MMObjectBuilder parent = (MMObjectBuilder) iter.next();
            if (typeCounters.containsKey(parent.getTableName())) {
                return true;
            }
        }
        return false;
    }

    protected int nodeChanged(Event event) throws IllegalArgumentException{
        if (log.isDebugEnabled()) {
            log.debug("Considering " + event);
        }
        Set<SearchQuery> cacheKeys;
        Map<String, Integer> oldTypeCounters;
        synchronized(this) {
            cacheKeys = new HashSet<SearchQuery>(keySet());
            oldTypeCounters = new HashMap<String, Integer>(typeCounters);
        }

        Set<SearchQuery> removeKeys = new HashSet<SearchQuery>();
        Map<String, Integer> foundTypeCounters = new HashMap<String, Integer>();

        evaluate(event, cacheKeys, removeKeys, foundTypeCounters);

        synchronized(this) {
            Iterator removeIter = removeKeys.iterator();
            while(removeIter.hasNext()) {
                remove(removeIter.next());
            }
            
            // types in the oldTypesCounter which are not in the typeCounters are removed during the 
            // evaluation of the keys and are not relevant anymore.
            for (Iterator iter = typeCounters.keySet().iterator(); iter.hasNext();) {
                String type = (String) iter.next();
                if (foundTypeCounters.containsKey(type)) {
                    if (oldTypeCounters.containsKey(type)) {
                        // adjust counter
                        int oldValue = oldTypeCounters.get(type);
                        int guessedValue = typeCounters.get(type);
                        int foundValue = foundTypeCounters.get(type);
                        if (guessedValue - oldValue > 0) {
                            int newValue = foundValue + (guessedValue - oldValue);
                            foundTypeCounters.put(type, new Integer(newValue));
                        }
                    }
                    else {
                        int guessedValue = typeCounters.get(type);
                        int foundValue = foundTypeCounters.get(type);
                        int newValue = foundValue + guessedValue;
                        foundTypeCounters.put(type, new Integer(newValue));
                    }
                }
                else {
                    Integer guessedValue = typeCounters.get(type);
                    foundTypeCounters.put(type, guessedValue);
                }
            }
            typeCounters = foundTypeCounters;
        }
        return removeKeys.size();
    }

    private void evaluate(Event event, Set<SearchQuery> cacheKeys, Set<SearchQuery> removeKeys, Map<String, Integer> foundTypeCounters) {
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
            }
            else {
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
