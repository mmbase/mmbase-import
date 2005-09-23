/*
 * Created on 9-jul-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.storage.search.SearchQuery;

/**
 * This class will manage a collection of <code>ReleaseStrategy</code>
 * instances, and call them hierarchically. It is not really thread safe, but I
 * suppose the cost of synchronizing access to the list of strategies does not
 * weigh up to the benefit.
 *
 * @since MMBase-1.8
 * @author Ernst Bunders
 * @version $Id: ChainedReleaseStrategy.java,v 1.4 2005-09-23 13:59:26 pierre Exp $
 */
public class ChainedReleaseStrategy extends ReleaseStrategy {

    private Map cacheReleaseStrategies = new HashMap(10);

    private String basicStrategyName;

    public ChainedReleaseStrategy() {
        super("Chained release strategy");
        BasicReleaseStrategy st = new BasicReleaseStrategy();
        basicStrategyName = st.getName();
        addReleaseStrategy(st);
    }

    /**
     * Adds the strategy if it is not allerady there. Strategies should only
     * occure once.
     *
     * @param strategy
     */
    public void addReleaseStrategy(ReleaseStrategy strategy) {
        if (cacheReleaseStrategies.get(strategy.getName()) == null)
            cacheReleaseStrategies.put(strategy.getName(), strategy);
    }

    public void removeReleaseStrategy(ReleaseStrategy strategy) {
        if (!strategy.getName().equals(basicStrategyName)) {
            cacheReleaseStrategies.remove(strategy.getName());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.ReleaseStrategy#getName()
     */
    public String getName() {
        return "Multi Release Strategy";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.ReleaseStrategy#getDescription()
     */
    public String getDescription() {
        return "This is a wrapper for any number of strategies you would like to "
            + "combine. it is used as the base strategy for QueryResultCache subclasses."
            + "it will at lease contain a BasicReleaseStrategy, and leave the rest to the "
            + "user to configure.";
    }

    /**
     * @return an iterator of present cache release strategies. This only
     *         contains the strategies added by the user.
     */
    public Iterator iterator() {
        return cacheReleaseStrategies.values().iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent,
     *      org.mmbase.storage.search.SearchQuery, java.util.List)
     */
    public boolean doEvaluate(NodeEvent event, SearchQuery query,
            List cachedResult) {
        // first do the 'basic' strategy that is allways there. (see
        // constructor)
        Iterator i = cacheReleaseStrategies.values().iterator();
        StrategyResult result = ((ReleaseStrategy) i.next()).evaluate(
            event, query, cachedResult);

        // while the outcome of getResult is true (the cache should be fluhed),
        // we have to keep trying.
        while (i.hasNext() && result.shouldRelease() == true) {
            result = ((ReleaseStrategy) i.next()).evaluate(event,
                query, cachedResult);
        }
        return result.shouldRelease();
    }

}
