/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import org.mmbase.core.event.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
 * @version $Id$
 */

public abstract class ReleaseStrategy {

    private int totalEvaluated = 0;
    private int totalPreserved = 0;

    private long totalEvaluationTimeInMillis = 0;

    private boolean isActive = true;

    private static final Logger log = Logging.getLoggerInstance(ReleaseStrategy.class);

    public ReleaseStrategy() {
    }

    public abstract String getName();

    public abstract String getDescription();

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#avgEvaluationTimeInMilis()
     */
    public int getAvgEvaluationTimeInMilis() {
        return (int) (totalEvaluationTimeInMillis / totalEvaluated);
    }

    public long getTotalEvaluationTimeMillis() {
        return totalEvaluationTimeInMillis;
    }

    /**
     * This method checks if evaluation should happen (active), keeps the time
     * of the operation and updates the statistics. To implement you own
     * strategy override
     * {@link #doEvaluate(NodeEvent event, SearchQuery query, List cachedResult)}.
     *
     */
    public final StrategyResult evaluate(final NodeEvent event, final SearchQuery query, final List cachedResult) {
        final Timer timer = new Timer();
        if (isActive) {
            boolean shouldRelease = doEvaluate(event, query, cachedResult);
            totalEvaluated++;
            if (!shouldRelease) totalPreserved++;
            long cost = timer.getTimeMillis();
            totalEvaluationTimeInMillis += cost;
            return new StrategyResult(shouldRelease, cost);
        } else {
            // if the cache is inactive it can not prevent the flush
            return new StrategyResult(true, timer.getTimeMillis());
        }
    }

    public final StrategyResult evaluate(RelationEvent event, SearchQuery query, List cachedResult) {
        Timer timer = new Timer();
        if (isActive) {
            boolean shouldRelease = doEvaluate(event, query, cachedResult);
            totalEvaluated++;
            if (!shouldRelease) totalPreserved++;
            long cost = timer.getTimeMillis();
            totalEvaluationTimeInMillis += cost;
            return new StrategyResult(shouldRelease, cost);
        } else {
            // if the cache is inactive it can not prevent the flush
            return new StrategyResult(true, timer.getTimeMillis());
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
     * @param event a node event
     * @param query
     * @param cachedResult
     * @return true if the cache entry should be released
     */
    protected abstract boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult);

    /**
     * implement this method to create your own strategy.
     *
     * @param event a relation event
     * @param query
     * @param cachedResult
     * @return true if the cache entry should be released
     */
    protected abstract boolean doEvaluate(RelationEvent event, SearchQuery query, List cachedResult);

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#setEnabled(boolean)
     */
    public void setEnabled(boolean newStatus) {
        if (isActive != newStatus) {
            clear();
            isActive = newStatus;
        }
    }

    public boolean isEnabled(){
       return isActive;
    }

    public void clear(){
        totalEvaluated = 0;
        totalPreserved = 0;
        totalEvaluationTimeInMillis = 0;
    }

    public boolean equals(Object ob){
        return ob instanceof ReleaseStrategy && this.getName().equals(((ReleaseStrategy)ob).getName());
    }

    public int hashCode(){
        return getName().hashCode();
    }

    public String toString() {
        return getName();
    }

    /**
     * utility for specializations: get all the constraints in the query that apply to
     * a certain field
     * TODO MM: This method is used like this:
     * <code> if(getConstraintsForField(fieldName, eventBuilder, constraint, query).size() > 0){  return false;}</code>
     * IOW, only the <em>size</em> of the return list is used, and then even wheter it is 0 or not. I think it is a waste to construct a complete new list, only for that.
     * Perhaps the method should return an Iterator?, and can be used with only 'hasNext()', constructing a longer list then necessary is avoided then.
     * @param fieldName
     * @param builder
     * @param constraint
     * @param query
     */
    protected static List getConstraintsForField(String  fieldName, final MMObjectBuilder builder, Constraint constraint, final SearchQuery query){
        if(constraint == null) constraint = query.getConstraint();
        if(constraint == null) return Collections.EMPTY_LIST;
        List result = new ArrayList();

        if(constraint instanceof CompositeConstraint) {
            log.debug("constraint is composite.");
            for (Iterator i = ((CompositeConstraint)constraint).getChilds().iterator(); i.hasNext();) {
                Constraint c = (Constraint) i.next();
                result.addAll(getConstraintsForField(fieldName, builder, c, query));
            }
        } else if (constraint instanceof LegacyConstraint) {
            log.debug("constraint is legacy.");
            if(query.getSteps().size() > 1) {
                // how about postfixing with numbers?
                fieldName = builder.getTableName() + "." + fieldName;
            }
            if(((LegacyConstraint)constraint).getConstraint().indexOf(fieldName) > -1){
                result.add(constraint);
                return result;
            }
        } else if (constraint instanceof FieldConstraint) {
            log.debug("constraint is field constraint.");
            StepField sf = ((FieldConstraint)constraint).getField();
            if(sf.getFieldName().equals(fieldName) && (sf.getStep().getTableName().equals(builder.getTableName()) ||
                                                       builder.isExtensionOf(MMBase.getMMBase().getBuilder(sf.getStep().getTableName()))
                                                       )
               ) {
                result.add(constraint);
                return result;
            }
        }
        return result;
    }

    /**
     * utility for specializations: get all the sortorders in the query that apply to
     * a certain field
     * TODO MM:  See remark at {@link #getConstraintsForField}

     * @param fieldName
     * @param builder
     * @param sortOrders
     * @param query
     */
    protected static List getSortordersForField(final String fieldName, final MMObjectBuilder builder, List sortOrders, final SearchQuery query) {
        if(sortOrders == null) sortOrders = query.getSortOrders();
        if(sortOrders == null) return Collections.EMPTY_LIST;
        List result = new ArrayList();
        for (Iterator iter = sortOrders.iterator(); iter.hasNext();) {
            SortOrder order = (SortOrder) iter.next();
            StepField sf = order.getField();
            String stepName = sf.getStep().getTableName();
            if(sf.getFieldName().equals(fieldName) && (stepName.equals(builder.getTableName()) ||
                                                       builder.isExtensionOf(MMBase.getMMBase().getBuilder(stepName))
                                                       )
               ) {
                result.add(order);
            }
        }
        return result;
    }


    /**
     * @author Ernst Bunders This class is a bean containing shouldRelease of an
     *         event evaluation
     */
    public static class StrategyResult {
        private final boolean shouldRelease;
        private final long cost;

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
         * Whether, according to this strategy, the query must be flushed.
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
    protected final static class Timer {
        private final long now;

        Timer() {
            now = System.currentTimeMillis();
        }

        public long getTimeMillis() {
            return System.currentTimeMillis() - now;
        }
    }


}
