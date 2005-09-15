/*
 * Created on 9-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.storage.search.SearchQuery;

/**
 * @author Ernst Bunders
 * 
 * This class will manage a collection of <code>AbstractReleaseStrategy</code>
 * instances, and call them hyrarchically. It is not really thread safe, but
 * i suppose the cost of synchronizing access to the list of strategies dous
 * not weigh up to the benefit. 
 */
public class ChainedReleaseStrategy extends AbstractReleaseStrategy {

	private Map cacheReleaseStrategies;
	private String basicStrategyName;



	public ChainedReleaseStrategy() {
	    super("Chained release strategy");
		BasicReleaseStrategy st = new BasicReleaseStrategy();
		basicStrategyName = st.getName();
		addReleaseStrategy(st);
		cacheReleaseStrategies = new HashMap(10);
	}

	/**
	 * Adds the strategy if it is not allerady there. Strategies should only
	 * occure once.
	 * @param strategy
	 */
	public void addReleaseStrategy(AbstractReleaseStrategy strategy){
		if(cacheReleaseStrategies.get(strategy.getName()) == null)
			cacheReleaseStrategies.put(strategy.getName(), strategy);
	}
	
	public void removeReleaseStrategy(AbstractReleaseStrategy strategy){
		if(! strategy.getName().equals(basicStrategyName)){
			cacheReleaseStrategies.remove(strategy.getName());
		}
	}

	/* (non-Javadoc)
	 * @see org.mmbase.cache.AbstractReleaseStrategy#getName()
	 */
	public String getName() {
		return "Multi Release Strategy";
	}

	/* (non-Javadoc)
	 * @see org.mmbase.cache.AbstractReleaseStrategy#getDescription()
	 */
	public String getDescription() {
		return "This is a wrapper for any number of strategies you would like to "+
		"combine. it is used as the base strategy for QueryResultCache subclasses."+
		"it will at lease contain a BasicReleaseStrategy, and leave the rest to the "+
		"user to configure.";
	}
	
	/**
	 * @return an iterator of present cache release strategies. This only contains
	 * the strategies added by the user.
	 */
	public Iterator iterator(){
		return cacheReleaseStrategies.values().iterator();
	}

	/* (non-Javadoc)
	 * @see org.mmbase.cache.AbstractReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent, org.mmbase.storage.search.SearchQuery, java.util.List)
	 */
	public boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
		//first do the 'basic' strategy that is allways there. (see constructor)
		Iterator i = cacheReleaseStrategies.values().iterator();
		StrategyResult result = ((AbstractReleaseStrategy) i.next()).evaluate(event, query, cachedResult);
		
		//while the outcome of getResult is true (the cache should be fluhed),
		//we have to keep trying.
		while( i.hasNext() && result.shouldRelease() == true ) {
			result = ((AbstractReleaseStrategy) i.next()).evaluate(event, query, cachedResult);
		}
		return result.shouldRelease();
	}
	

}