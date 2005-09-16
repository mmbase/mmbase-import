/*
 * Created on 9-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.NodeList;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.implementation.database.BasicSqlHandler;

/**
 * @author Ernst Bunders
 * 
 * <p>
 * This class is the base for all cache release strategies. You should extend
 * this to create your own. It will contain a number of usefull utility methods
 * to analyze query objecs and cached search results. Feel free to add those In
 * case you miss one developing your own strategies.
 * </p>
 */

public abstract class AbstractReleaseStrategy {
	
	private int totalEvaluated = 0, totalPreserved =0;
	private long totalEvalueationTimeInMillis = 0;
	private boolean isActive = true;
	protected String name;

	public AbstractReleaseStrategy(String name){
	    this.name = name;
	}
	
	public String getName(){
	    return name;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#avgEvaluationTimeInMilis()
	 */
	public int getAvgEvaluationTimeInMilis() {
		return (int) (totalEvalueationTimeInMillis / totalEvaluated);
	}
	
	public long getTotalEvaluationTimeMillis(){
	    return totalEvalueationTimeInMillis;
	}

	/**
	 * this method checks if evaluation should happen (active), keeps the 
	 * time of the operation and updates the statistics.<br/>
	 * to implement you own strategy override <code>doEvaluate(NodeEvent event, SearchQuery query, List cachedResult)</code>
	 * @see QueryResultCacheReleaseStrategy#evaluate(NodeEvent, SearchQuery,
	 *      NodeList)
	 */
	public final StrategyResult evaluate(NodeEvent event,
			SearchQuery query, List cachedResult){
		Timer timer = new Timer();
		if(isActive){
			boolean shouldRelease = doEvaluate(event, query,cachedResult);
			totalEvaluated ++;
			if(!shouldRelease)totalPreserved ++;
			totalEvalueationTimeInMillis += timer.getTimeMillis();
			return new StrategyResult(shouldRelease, timer);
		}else{
			//if the cache is inactive it can not prevent the flush
			return new StrategyResult(true, timer);
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getTotalPreserved()
     */
    public int getTotalPreserved() {
    	return totalPreserved;
    }

    /* (non-Javadoc)
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getTotalEvaluations()
     */
    public int getTotalEvaluated() {
    	return totalEvaluated;
    }
	
	/**
	 * implement this method to create your own strategy.
	 * @param event (could be a RelationEvent)
	 * @param query
	 * @param cachedResult
	 * @return true if the cache entry should be released
	 */
	public abstract boolean doEvaluate(NodeEvent event,
	SearchQuery query, List cachedResult);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		if (isActive != enabled){
			totalEvaluated = 0;
			totalPreserved = 0;
			totalEvalueationTimeInMillis = 0;
			isActive = enabled;
		}
	}

	/**
	 * Utility method to get step from a query that the event relates to
	 * 
	 * @param event
	 * @param query
	 * @return a Step instance or null if the event object type was not found in
	 *         the query
	 */
	protected Step getStepForEvent(NodeEvent event, SearchQuery query) {
		Step step;
		for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
			step = (Step) i.next();
			if (event.getBuilderName().equals(step.getTableName())) {
				return step;
			}
		}
		return null;
	}

	protected List getRelationSteps(SearchQuery query) {
		List result = new ArrayList(10);
		for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
			Object step = i.next();
			if (step instanceof RelationStep)
				result.add(step);
		}
		return result;
	}

	/**
	 * @author Ernst Bunders
	 * 
	 * This class is a bean containing shouldRelease of an event evaluation
	 */
	public static class StrategyResult {
		private boolean shouldRelease;
	
		private long cost;
	
		StrategyResult(boolean shouldRelease, Timer cost) {
			this.shouldRelease = shouldRelease;
			this.cost = cost.getTimeMillis();
		}
	
		StrategyResult(boolean shouldRelease, long cost) {
			this.shouldRelease = shouldRelease;
			this.cost = cost;
		}
	
		/**
		 * @param cost
		 *            The cost of a node event evaluation
		 */
		public long getCost() {
			return cost;
		}
	
		/**
		 * @param shouldRelease
		 *            of a node event evaluation
		 */
		public boolean shouldRelease() {
			return shouldRelease;
		}
	
	}

	/**
	 * @author Ernst Bunders
	 * 
	 * This is a utility class to help timing the evaluation. Just create an
	 * instance before the evaluation and then use it to create the
	 * StrategyResult object
	 */
	protected static class Timer {
		private long now;

		Timer() {
			now = System.currentTimeMillis();
		}

		public long getTimeMillis() {
			return System.currentTimeMillis() - now;
		}
	}

}
