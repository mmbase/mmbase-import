package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicSearchQuery implements SearchQuery {
    
    /** Query handler used to produce queries in ANSI SQL 92 format. */
    private final static BasicSqlGenerator sql92Generator
    = new BasicSqlGenerator(new HashMap(0), "");
    
    /** Distinct property. */
    private boolean distinct = false;
    
    /** MaxNumber property. */
    private int maxNumber = -1;
    
    /** Offset property. */
    private int offset = 0;
    
    /** Step list. */
    private List steps = new ArrayList();
    
    /** StepField list. */
    private List fields = new ArrayList();
    
    /** SortOrder list. */
    private List sortOrders = new ArrayList();
    
    /** Constraint.. */
    private Constraint constraint = null;
    
    /**
     * Default costructor.
     */
    public BasicSearchQuery() {}
    
    /**
     * Sets distinct.
     *
     * @param distinct The distinct value.
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    
    /**
     * Sets maxNumber.
     *
     * @param maxNumber The maxNumber value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setMaxNumber(int maxNumber) {
        if (maxNumber < -1) {
            throw new IllegalArgumentException(
            "Invalid maxNumber value: " + maxNumber);
        }
        this.maxNumber = maxNumber;
    }
    
    /**
     * Sets offset.
     *
     * @param offset The offset value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setOffset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException(
            "Invalid offset value: " + offset);
        }
        this.offset = offset;
    }
    
    /**
     * Adds new step to this SearchQuery.
     *
     * @param builder The builder associated with the step.
     * @return The new step.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStep addStep(MMObjectBuilder builder) {
        BasicStep step = new BasicStep(builder);
        steps.add(step);
        return step;
    }
    
    /**
     * Adds new relationstep to this SearchQuery.
     * This adds the next step as well, it can be retrieved by calling <code>
     * {@link org.mmbase.storage.search.RelationStep#getNext getNext()}
     * </code> on the relationstep.
     *
     * @param builder The builder associated with the relation step.
     * @param nextBuilder The builder associated with the next step.
     * @return The new relationstep.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    public BasicRelationStep addRelationStep(
    InsRel builder, MMObjectBuilder nextBuilder) {
        int nrOfSteps = steps.size();
        if (nrOfSteps == 0) {
            throw new IllegalStateException(
            "No previous step.");
        }
        BasicStep previous = (BasicStep) steps.get(nrOfSteps - 1);
        BasicStep next = new BasicStep(nextBuilder);
        BasicRelationStep relationStep
        = new BasicRelationStep(builder, previous, next);
        steps.add(relationStep);
        steps.add(next);
        return relationStep;
    }
    
    /**
     * Adds new field to this SearchQuery.
     *
     * @param step The associated step.
     * @param fieldDefs The associated fieldDefs.
     * @return The new field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStepField addField(Step step, FieldDefs fieldDefs) {
        BasicStepField field = new BasicStepField(step, fieldDefs);
        fields.add(field);
        return field;
    }
    
    /**
     * Creates sortorder for this SearchQuery.
     *
     * @param field The associated stepfield.
     * @return The new sortOrder
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSortOrder addSortOrder(StepField field) {
        BasicSortOrder sortOrder =  new BasicSortOrder(field);
        sortOrders.add(sortOrder);
        return sortOrder;
    }
    
    /**
     * Sets constraint.
     *
     * @param constraint The constraint.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }
    
    /**
     * Represents this query as a string in ANSI SQL 92 format.
     *
     * @return ANSI SQL 92 representation of this query.
     * @throws SearchQueryException
     * @throws IllegalStateException when the query is not complete.
     */
    public static String toSQL92(SearchQuery query) 
    throws SearchQueryException {
        return sql92Generator.toSql(query);
    }
    
    // javadoc is inherited
    public boolean isDistinct() {
        return distinct;
    }
    
    // javadoc is inherited
    public List getSortOrders() {
        // return as unmodifiable list
        return Collections.unmodifiableList(sortOrders);
    }
    
    // javadoc is inherited
    public List getSteps() {
        // return as unmodifiable list
        return Collections.unmodifiableList(steps);
    }
    
    
    // javadoc is inherited
    public List getFields() {
        // return as unmodifiable list
        return Collections.unmodifiableList(fields);
    }
    
    // javadoc is inherited
    public Constraint getConstraint() {
        return constraint;
    }
    
    // javadoc is inherited
    public int getMaxNumber() {
        return maxNumber;
    }
    
    //javadoc is inherited
    public int getOffset() {
        return offset;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SearchQuery) {
            SearchQuery query = (SearchQuery) obj;
            return distinct == query.isDistinct()
                && maxNumber == query.getMaxNumber()
                && offset == query.getOffset()
                && steps.equals(query.getSteps())
                && fields.equals(query.getFields())
                && sortOrders.equals(query.getSortOrders())
                && constraint.equals(query.getConstraint());
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (distinct? 0: 101)
        + maxNumber * 17 + offset * 19
        + 23 * steps.hashCode()
        + 29 * fields.hashCode()
        + 31 * sortOrders.hashCode()
        + 37 * (constraint == null? 0: constraint.hashCode());
    }
    
}
