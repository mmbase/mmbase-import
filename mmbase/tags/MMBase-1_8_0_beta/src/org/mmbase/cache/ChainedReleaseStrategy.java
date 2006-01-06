/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.core.event.*;
import org.mmbase.storage.search.SearchQuery;

/**
 * This class will manage a collection of <code>ReleaseStrategy</code>
 * instances, and call them hierarchically. It is not really thread safe, but I
 * suppose the cost of synchronizing access to the list of strategies does not
 * weigh up to the benefit.
 *
 * @since MMBase-1.8
 * @author Ernst Bunders
 * @version $Id: ChainedReleaseStrategy.java,v 1.10 2005-12-23 10:20:17 ernst Exp $
 */
public class ChainedReleaseStrategy extends ReleaseStrategy {

    private List cacheReleaseStrategies = new ArrayList(10);

    private String basicStrategyName;

    public ChainedReleaseStrategy() {
        //BetterStrategy st = new BetterStrategy();
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
        if (! cacheReleaseStrategies.contains(strategy)){
        	cacheReleaseStrategies.add(strategy);
        }
    }

    public void removeStrategy(ReleaseStrategy strategy) {
        if (!strategy.getName().equals(basicStrategyName)) {
            cacheReleaseStrategies.remove(strategy);
        }
    }
    
    /**
     * removes all strategies but the base one
     */
    public void removeAllStrategies(){
        for (Iterator i = iterator(); i.hasNext(); ){
            if( ! ((ReleaseStrategy)i.next()).getName().equals(basicStrategyName)) i.remove();
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
        return cacheReleaseStrategies.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent,
     *      org.mmbase.storage.search.SearchQuery, java.util.List)
     */
    protected boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        // first do the 'basic' strategy that is allways there. (see constructor)
        Iterator i = cacheReleaseStrategies.iterator();
        StrategyResult result = ((ReleaseStrategy) i.next()).evaluate(event, query, cachedResult);

        // while the outcome of getResult is true (the cache should be fluhed), we have to keep trying.
        while (i.hasNext() && result.shouldRelease() == true) {
            result = ((ReleaseStrategy) i.next()).evaluate(event, query, cachedResult);
        }
        return result.shouldRelease();
    }

    protected boolean doEvaluate(RelationEvent event, SearchQuery query, List cachedResult) {
        // first do the 'basic' strategy that is allways there. (see constructor)
        Iterator i = cacheReleaseStrategies.iterator();
        StrategyResult result = ((ReleaseStrategy) i.next()).evaluate(event, query, cachedResult);

        // while the outcome of getResult is true (the cache should be fluhed), we have to keep trying.
        while (i.hasNext() && result.shouldRelease() == true) {
            result = ((ReleaseStrategy) i.next()).evaluate(event, query, cachedResult);
        }
        return result.shouldRelease();
    }
    
    public void clear(){
        super.clear();
        for(Iterator i = iterator(); i.hasNext();){
            ReleaseStrategy rs = (ReleaseStrategy) i.next();
            rs.clear();
        }
    }

}
