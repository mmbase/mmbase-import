/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Id: BasicSearchQuery.java,v 1.15 2003-12-02 13:54:01 michiel Exp $
 * @since MMBase-1.7
 */
public class BasicSearchQuery implements SearchQuery, Cloneable {
    private static final Logger log = Logging.getLoggerInstance(BasicSearchQuery.class);
    
    /** Distinct property. */
    private boolean distinct = false;
    
    /** MaxNumber property. */
    private int maxNumber = SearchQuery.DEFAULT_MAX_NUMBER;
    
    /** Offset property. */
    private int offset = SearchQuery.DEFAULT_OFFSET;
    
    /** Step list. */
    private List steps = new ArrayList();
    
    /** StepField list. */
    protected List fields = new ArrayList();
    
    /** SortOrder list. */
    private List sortOrders = new ArrayList();
    
    /** Constraint.. */
    private Constraint constraint = null;
    
    /** Aggragating property. */
    private boolean aggregating = false;
    
    /**
     * Constructor.
     *
     * @param aggregating True for an aggregating query, false otherwise.
     */
    public BasicSearchQuery(boolean aggregating) {
        this.aggregating = aggregating;
    }
    
    /**
     * Constructor, constructs non-aggragating query.
     */
    public BasicSearchQuery() {
        this(false);
    }

    /**
     * A deep copy, but sets also aggregating, and clear fields if aggregating is true then.
     */

    public BasicSearchQuery(SearchQuery q, boolean aggregating) {
        this(q);
        this.aggregating = aggregating;
        if (aggregating) {
            fields.clear();
            sortOrders.clear();
            offset = DEFAULT_OFFSET;
            maxNumber = DEFAULT_MAX_NUMBER;
        }
    }


    /**
     * A deep copy. Needed if you want to do multiple queries (and change the query between them).
     * Used by bridge.Query#clone (so it will be decided that that is not needed, ths can be removed too)
     * @see org.mmbase.bridge.Query#clone 
     */
    public BasicSearchQuery(SearchQuery q) {
        distinct  = q.isDistinct();
        maxNumber = q.getMaxNumber();
        offset    = q.getOffset();

        copySteps(q);
        copyFields(q);
        copySortOrders(q);

        Constraint c = q.getConstraint();
        if (c != null) {
            setConstraint(copyConstraint(q, c));
        }
    }


    public Object clone() {
        try {
            BasicSearchQuery clone = (BasicSearchQuery) super.clone();
            clone.copySteps(this);
            clone.copyFields(this);
            clone.copySortOrders(this);
            Constraint c = getConstraint();
            if (c != null) {
                clone.setConstraint(copyConstraint(this, c));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }

    protected void copySteps(SearchQuery q) {
        MMBase mmb = MMBase.getMMBase();
        steps = new ArrayList();
        Iterator i = q.getSteps().iterator();
        while (i.hasNext()) {            
            Step step = (Step) i.next();
            if (step instanceof RelationStep) {
                RelationStep relationStep = (RelationStep) step;               
                MMObjectBuilder dest   = mmb.getBuilder(relationStep.getNext().getTableName());
                InsRel         insrel  = (InsRel) mmb.getBuilder(relationStep.getTableName());
                BasicRelationStep newRelationStep = addRelationStep(insrel, dest);
                newRelationStep.setDirectionality(relationStep.getDirectionality());
                newRelationStep.setCheckedDirectionality(relationStep.getCheckedDirectionality());
                newRelationStep.setRole(relationStep.getRole());
                newRelationStep.setAlias(relationStep.getAlias());
                Iterator j = relationStep.getNodes().iterator();
                while (j.hasNext()) {
                    newRelationStep.addNode(((Integer) j.next()).intValue());
                }
                BasicStep next    = (BasicStep) relationStep.getNext();
                BasicStep newNext = (BasicStep) newRelationStep.getNext();
                newNext.setAlias(next.getAlias());
                j = next.getNodes().iterator();
                while (j.hasNext()) {
                    newNext.addNode(((Integer) j.next()).intValue());
                }
                i.next(); // dealt with that already
                                                                    
            } else {
                BasicStep newStep = addStep(mmb.getBuilder(step.getTableName()));
                newStep.setAlias(step.getAlias());
                Iterator j = step.getNodes().iterator();
                while (j.hasNext()) {
                    newStep.addNode(((Integer) j.next()).intValue());
                }
            }
        }
        //log.info("copied steps " + q.getSteps() + " became " + steps);

    }
    protected void copyFields(SearchQuery q) {        
        fields = new ArrayList();
        MMBase mmb = MMBase.getMMBase();
        Iterator i = q.getFields().iterator();
        while (i.hasNext()) {
            StepField field = (StepField) i.next();
            Step step = field.getStep();
            MMObjectBuilder bul = mmb.getBuilder(step.getTableName());
            int j = q.getSteps().indexOf(step);
            if (j == -1) {
                throw new  RuntimeException("Step " + step + " could not be found in " + q.getSteps());
            }
            Step newStep = (Step) steps.get(j);
            BasicStepField newField = addField(newStep, bul.getField(field.getFieldName()));
            newField.setAlias(field.getAlias());                                        
        }
        //log.info("copied fields " + q.getFields() + " became " + fields);
    }
    protected void copySortOrders(SearchQuery q) {
        sortOrders = new ArrayList();
        MMBase mmb = MMBase.getMMBase();
        Iterator i = q.getSortOrders().iterator();
        while (i.hasNext()) {
            SortOrder sortOrder = (SortOrder) i.next();
            StepField field = sortOrder.getField();
            int j = q.getFields().indexOf(field);
            StepField newField;
            if (j == -1) { // not sorting on field of field list.
                Step step = field.getStep();
                MMObjectBuilder bul = mmb.getBuilder(step.getTableName());
                newField = new BasicStepField(field.getStep(), bul.getField(field.getFieldName()));
            } else {
                newField = (StepField) fields.get(j);
            }
            BasicSortOrder newSortOrder = addSortOrder(newField);
            newSortOrder.setDirection(sortOrder.getDirection());
        }

                

    }

    /**
     * Creates a new StepField like f for query q.
     */
    protected StepField createNewStepField(SearchQuery q, StepField f) {
        Step fstep = f.getStep();
        // find existing step.
        List steps = q.getSteps();
        Step step = (Step) steps.get(steps.indexOf(fstep));
        MMObjectBuilder bul = MMBase.getMMBase().getBuilder(step.getTableName());
        return new BasicStepField(step, bul.getField(f.getFieldName()));        
    }
    

    /**
     * Used by copy-constructor. Constraints have to be done recursively.
     */
    protected Constraint copyConstraint(SearchQuery q, Constraint c) {        
        if (c instanceof CompositeConstraint) {
            CompositeConstraint constraint = (CompositeConstraint) c;
            BasicCompositeConstraint newConstraint = new BasicCompositeConstraint(constraint.getLogicalOperator());
            Iterator i = constraint.getChilds().iterator();
            while (i.hasNext()) {
                Constraint cons = (Constraint) i.next();
                newConstraint.addChild(copyConstraint(q, cons));
            }
            newConstraint.setInverse(constraint.isInverse());
            return newConstraint;
        } else if (c instanceof CompareFieldsConstraint) {
            CompareFieldsConstraint constraint = (CompareFieldsConstraint) c;       
            BasicCompareFieldsConstraint newConstraint = new BasicCompareFieldsConstraint(createNewStepField(q, constraint.getField()), createNewStepField(q, constraint.getField2()));
            newConstraint.setOperator(constraint.getOperator());
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;
        } else if (c instanceof FieldValueConstraint) {
            FieldValueConstraint constraint = (FieldValueConstraint) c;
            Object value = constraint.getValue();
            BasicFieldValueConstraint newConstraint = new BasicFieldValueConstraint(createNewStepField(q, constraint.getField()), value);
            newConstraint.setOperator(constraint.getOperator());
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;            
        } else if (c instanceof FieldNullConstraint) {
            FieldNullConstraint constraint = (FieldNullConstraint) c;
            BasicFieldNullConstraint newConstraint = new BasicFieldNullConstraint(createNewStepField(q, constraint.getField()));
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;            
        } else if (c instanceof FieldValueBetweenConstraint) {
            FieldValueBetweenConstraint constraint = (FieldValueBetweenConstraint) c;
            BasicFieldValueBetweenConstraint newConstraint;
            try {
                newConstraint = new BasicFieldValueBetweenConstraint(createNewStepField(q, constraint.getField()), new Integer(constraint.getLowerLimit()), new Integer(constraint.getUpperLimit()));
            } catch (NumberFormatException e) {
                newConstraint = new BasicFieldValueBetweenConstraint(createNewStepField(q, constraint.getField()), new Double(constraint.getLowerLimit()), new Double(constraint.getUpperLimit()));
            }
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;            
        } else if (c instanceof FieldValueInConstraint) {
            FieldValueInConstraint constraint = (FieldValueInConstraint) c;
            BasicFieldValueInConstraint newConstraint = new BasicFieldValueInConstraint(createNewStepField(q, constraint.getField()));
            int type =  constraint.getField().getType();

            Iterator k = constraint.getValues().iterator();
            while (k.hasNext()) {
                Object value = k.next();
                switch(type) {
                case FieldDefs.TYPE_INTEGER:
                case FieldDefs.TYPE_LONG:
                case FieldDefs.TYPE_NODE:
                    value = new Long((String) value);
                    break;
                case FieldDefs.TYPE_FLOAT:
                case FieldDefs.TYPE_DOUBLE:
                    value = new Double((String) value);
                    break;
                }
                newConstraint.addValue(value);
            }
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;            
        } else if (c instanceof LegacyConstraint) {
            LegacyConstraint constraint = (LegacyConstraint) c;
            BasicLegacyConstraint newConstraint = new BasicLegacyConstraint(constraint.getConstraint());
            return newConstraint;
        }
        throw new RuntimeException("Could not copy constraint " + c);
    }

    /**
     * Sets distinct.
     *
     * @param distinct The distinct value.
     * @return This <code>BasicSearchQuery</code> instance.
    */
    public BasicSearchQuery setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }
    
    /**
     * Sets maxNumber.
     *
     * @param maxNumber The maxNumber value.
     * @return This <code>BasicSearchQuery</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSearchQuery setMaxNumber(int maxNumber) {
        if (maxNumber < -1) {
            throw new IllegalArgumentException( "Invalid maxNumber value: " + maxNumber);
        }
        this.maxNumber = maxNumber;
        return this;
    }
    
    /**
     * Sets offset.
     *
     * @param offset The offset value.
     * @return This <code>BasicSearchQuery</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSearchQuery setOffset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException(
            "Invalid offset value: " + offset);
        }
        this.offset = offset;
        return this;
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
     * </code> on the relationstep, and cast to {@link BasicStep BasicStep}.
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
     * @throws UnsupportedOperationException when called 
     *         on an aggregating query.
     */
    public BasicStepField addField(Step step, FieldDefs fieldDefs) {
        if (aggregating) {
            throw new UnsupportedOperationException("Adding non-aggregated field to aggregating query.");
        }
        BasicStepField field = new BasicStepField(step, fieldDefs);
        fields.add(field);
        return field;
    }


    // MM //only sensible for NodeSearchQuery
    protected void mapField(FieldDefs field, StepField stepField) {
        
    }
    // MM
    public void  addFields(Step step) {
        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder builder = mmb.getBuilder(step.getTableName());
        Iterator iFields = builder.getFields().iterator();
        while (iFields.hasNext()) {
            FieldDefs field = (FieldDefs) iFields.next();
	    if ( field.getDBType() != FieldDefs.TYPE_BYTE 
		 && ( field.getDBState() == FieldDefs.DBSTATE_PERSISTENT 
	      || field.getDBState() == FieldDefs.DBSTATE_SYSTEM
		    )
		) {
		BasicStepField stepField = addField(step, field);
                mapField(field, stepField);
	    }
        }

    }
    // MM
    public void removeFields() {
        fields.clear();
    }
    
    /**
     * Adds new aggregated field to this SearchQuery.
     *
     * @param step The associated step.
     * @param fieldDefs The associated fieldDefs.
     * @param aggregatinType The aggregation type.
     * @return The new field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws UnsupportedOperationException when called 
     *         on an non-aggregating query.
     */
    public BasicAggregatedField addAggregatedField(Step step, FieldDefs fielDefs,
    int aggregationType) {
        if (!aggregating) {
            throw new UnsupportedOperationException(
            "Adding aggregated field to non-aggregating query.");
        }
        BasicAggregatedField field = new BasicAggregatedField(step, fielDefs, aggregationType);
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
                && (constraint == null?
                    query.getConstraint() == null:
                    constraint.equals(query.getConstraint()));
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
    
    // javadoc is inherited
    public String toString() {
        return "SearchQuery(distinct:" + isDistinct()
        + ", steps:" + getSteps()
        + ", fields:" + getFields()
        + ", constraint:" + getConstraint()
        + ", sortorders:" + getSortOrders()
        + ", max:" + getMaxNumber()
        + ", offset:" + getOffset() + ")";
    }
    
}
