/*
 * Created on 9-jul-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.*;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.*;

/**
 * <p>
 * This class is the base for all cache release strategies. You should extend
 * this to create your own. It will contain a number of usefull utility methods
 * to analyze query objecs and cached search results. Feel free to add those In
 * case you miss one developing your own strategies.
 * </p>
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id: ReleaseStrategy.java,v 1.4 2005-10-13 17:35:03 ernst Exp $
 */

public abstract class ReleaseStrategy {

    private int totalEvaluated = 0, totalPreserved = 0;

    private long totalEvalueationTimeInMillis = 0;

    private boolean isActive = true;

    protected String name;

    public ReleaseStrategy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public abstract String getDescription();

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#avgEvaluationTimeInMilis()
     */
    public int getAvgEvaluationTimeInMilis() {
        return (int) (totalEvalueationTimeInMillis / totalEvaluated);
    }

    public long getTotalEvaluationTimeMillis() {
        return totalEvalueationTimeInMillis;
    }

    /**
     * this method checks if evaluation should happen (active), keeps the time
     * of the operation and updates the statistics.<br/> to implement you own
     * strategy override
     * <code>doEvaluate(NodeEvent event, SearchQuery query, List cachedResult)</code>
     *
     */
    public final StrategyResult evaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        Timer timer = new Timer();
        if (isActive) {
            boolean shouldRelease = doEvaluate(event, query, cachedResult);
            totalEvaluated++;
            if (!shouldRelease) totalPreserved++;
            totalEvalueationTimeInMillis += timer.getTimeMillis();
            return new StrategyResult(shouldRelease, timer);
        } else {
            // if the cache is inactive it can not prevent the flush
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

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getTotalEvaluations()
     */
    public int getTotalEvaluated() {
        return totalEvaluated;
    }

    /**
     * implement this method to create your own strategy.
     *
     * @param event (could be a RelationEvent)
     * @param query
     * @param cachedResult
     * @return true if the cache entry should be released
     */
    protected abstract boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult);

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        if (isActive != enabled) {
            totalEvaluated = 0;
            totalPreserved = 0;
            totalEvalueationTimeInMillis = 0;
            isActive = enabled;
        }
    }
    
    public boolean isEnabled(){
       return isActive;
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
        String builderName  = event.getNode().getBuilder().getTableName();
        for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
            step = (Step) i.next();
            if (builderName.equals(step.getTableName())) {
                return step;
            }
        }
        return null;
    }

    /**
     * utility for specializations: get all the relation steps of a query
     * @param query
     * @return
     */
    protected   static List getRelationSteps(SearchQuery query) {
        List result = new ArrayList(10);
        for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
            Object step = i.next();
            if (step instanceof RelationStep) result.add(step);
        }
        return result;
    }
    
    protected static List getStepFields(SearchQuery query, Step step){
        List result = new ArrayList();
        for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
            StepField  field = (StepField) i.next();
            if(field.getStep().equals(step))result.add(field);
        }
        return result;
    }
    
    /**
     * utility for specializations: get all the relation steps of a query
     * @param query
     * @return
     */
    protected static List getNodeSteps(SearchQuery query, MMObjectBuilder type){
        List result = new ArrayList(10);
        for (Iterator i = query.getSteps().iterator(); i.hasNext();) {
            Step step = (Step) i.next();
            if (! (step instanceof RelationStep)) {
                if(type == null || step.getTableName().equals(type.getTableName()))
                    result.add(step);
            }
        }
        return result;
    }
    
 
    /**
     * @author Ernst Bunders This class is a bean containing shouldRelease of an
     *         event evaluation
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
         * The cost of a node event evaluation. XXX What is the cost?
         */
        public long getCost() {
            return cost;
        }

        /**
         * XXX What means this?
         */
        public boolean shouldRelease() {
            return shouldRelease;
        }

    }

    /**
     * @author Ernst Bunders This is a utility class to help timing the
     *         evaluation. Just create an instance before the evaluation and
     *         then use it to create the StrategyResult object
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
