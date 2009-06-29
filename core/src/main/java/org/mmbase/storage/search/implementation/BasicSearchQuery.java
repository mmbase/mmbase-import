/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.cache.CachePolicy;
import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicSearchQuery implements SearchQuery, org.mmbase.util.PublicCloneable<BasicSearchQuery> {
    private static final Logger log = Logging.getLoggerInstance(BasicSearchQuery.class);

    private boolean distinct = false;
    private int maxNumber = SearchQuery.DEFAULT_MAX_NUMBER;
    private int offset = SearchQuery.DEFAULT_OFFSET;

    // Following fields would must logically be final, but that is incompatible with
    // clone(). Perhaps we should drop usage of clone() altogether. clone() sucks.

    private   List<Step> steps             = new ArrayList<Step>();
    private   List<Step> unmodifiableSteps = Collections.unmodifiableList(steps); // getSteps is called very  very often
    protected List<StepField> fields       = new ArrayList<StepField>();
    private   List<SortOrder> sortOrders   = new ArrayList<SortOrder>();

    private Constraint constraint = null;

    private boolean aggregating = false;

    private int savedHashcode = -1;

    /**
     * Whether this Query is cacheable.
     */
    private CachePolicy cachePolicy = CachePolicy.ALWAYS;

    private boolean modifiable = true;


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


    public final static int COPY_NORMAL = 0;
    public final static int COPY_AGGREGATING = 1;
    public final static int COPY_WITHOUTFIELDS = 2;

    /**
     * A deep copy, but sets also aggregating, and clear fields if aggregating is true then.
     */

    public BasicSearchQuery(SearchQuery q, int copyMethod) {
        distinct  = q.isDistinct();
        copySteps(q);
        Constraint c = q.getConstraint();
        if (c != null) {
            setConstraint(copyConstraint(q, c));
        }
        switch(copyMethod) {
        case COPY_NORMAL:
            copyFields(q);
        case COPY_WITHOUTFIELDS:
            copySortOrders(q);
            maxNumber = q.getMaxNumber();
            offset    = q.getOffset();
            aggregating = false;
            break;
        case COPY_AGGREGATING:
            aggregating = true;
            break;
        default:
            log.debug("Unknown copy method " + copyMethod);
            break;
        }
    }


    /**
     * A deep copy. Needed if you want to do multiple queries (and change the query between them).
     * Used by bridge.Query#clone (so it will be decided that that is not needed, ths can be removed too)
     * @see org.mmbase.bridge.Query#clone
     */
    public BasicSearchQuery(SearchQuery q) {
        this(q, COPY_NORMAL);
    }


    public BasicSearchQuery clone() {
        try {
            BasicSearchQuery clone = (BasicSearchQuery) super.clone();
            clone.modifiable = true;
            clone.copySteps(this);
            clone.copyFields(this);
            clone.copySortOrders(this);
            Constraint c = getConstraint();
            if (c != null) {
                clone.setConstraint(copyConstraint(clone, c));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }

    protected void copySteps(SearchQuery q) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        MMBase mmb = MMBase.getMMBase();
        steps = new ArrayList<Step>(q.getSteps().size());
        Iterator<Step> i = q.getSteps().iterator();
        while(i.hasNext()) {
            Step step = i.next();
            if (step instanceof RelationStep) {
                RelationStep relationStep = (RelationStep) step;
                MMObjectBuilder dest   = mmb.getBuilder(relationStep.getNext().getTableName());
                InsRel         insrel  = (InsRel) mmb.getBuilder(relationStep.getTableName());
                BasicRelationStep newRelationStep = addRelationStep(insrel, dest);
                newRelationStep.setDirectionality(relationStep.getDirectionality());
                newRelationStep.setCheckedDirectionality(relationStep.getCheckedDirectionality());
                newRelationStep.setRole(relationStep.getRole());
                newRelationStep.setAlias(relationStep.getAlias());
                if (relationStep.getNodes() != null) {
                    for (Integer j : relationStep.getNodes()) {
                        newRelationStep.addNode(j);
                    }
                }
                BasicStep next    = (BasicStep) relationStep.getNext();
                BasicStep newNext = (BasicStep) newRelationStep.getNext();
                newNext.setAlias(next.getAlias());
                if (next.getNodes() != null) {
                    for (Integer j : next.getNodes()) {
                        newNext.addNode(j);
                    }
                }
                i.next(); // dealt with that already

            } else {
                BasicStep newStep = addStep(mmb.getBuilder(step.getTableName()));
                newStep.setAlias(step.getAlias());
                if (step.getNodes() != null) {
                    for (Integer j : step.getNodes()) {
                        newStep.addNode(j);
                    }
                }
            }
        }
        //log.info("copied steps " + q.getSteps() + " became " + steps);
        unmodifiableSteps = Collections.unmodifiableList(steps);
    }
    protected void copyFields(SearchQuery q) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        fields = new ArrayList<StepField>(q.getFields().size());
        MMBase mmb = MMBase.getMMBase();
        for (StepField field : q.getFields()) {
            Step step = field.getStep();
            MMObjectBuilder bul = mmb.getBuilder(step.getTableName());
            int j = q.getSteps().indexOf(step);
            if (j == -1) {
                throw new  RuntimeException("Step " + step + " could not be found in " + q.getSteps());
            }
            Step newStep = steps.get(j);
            BasicStepField newField = addField(newStep, bul.getField(field.getFieldName()));
            newField.setAlias(field.getAlias());
        }
        //log.info("copied fields " + q.getFields() + " became " + fields);
    }
    protected void copySortOrders(SearchQuery q) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        sortOrders = new ArrayList<SortOrder>(q.getSortOrders().size());
        MMBase mmb = MMBase.getMMBase();
        for (SortOrder sortOrder : q.getSortOrders()) {
            StepField field = sortOrder.getField();
            int j = q.getFields().indexOf(field);
            StepField newField;
            if (j == -1 || j >= fields.size()) { // not sorting on field of field list.
                Step step = field.getStep();
                MMObjectBuilder bul = mmb.getBuilder(step.getTableName());
                newField = new BasicStepField(field.getStep(), bul.getField(field.getFieldName()));
            } else {
                newField = fields.get(j);
            }
            BasicSortOrder newSortOrder = addSortOrder(newField);
            newSortOrder.setDirection(sortOrder.getDirection());
        }
    }

    /**
     * Creates a new StepField like f for query q.
     */
    protected static StepField createNewStepField(SearchQuery q, StepField f) {
        Step fstep = f.getStep();
        // find existing step.
        List<Step> steps = q.getSteps();
        Step step = steps.get(steps.indexOf(fstep));
        MMObjectBuilder bul = MMBase.getMMBase().getBuilder(step.getTableName());
        CoreField field = bul.getField(f.getFieldName());
        if (field == null) {
            throw new IllegalStateException("Did not find field " + f.getFieldName() + " in builder " + step.getTableName() + " " + bul.getFields());
        }
        return new BasicStepField(step, field);
    }


    /**
     * Used by copy-constructor. Constraints have to be done recursively.
     */
    protected static Constraint copyConstraint(SearchQuery q, Constraint c) {
        if (c instanceof CompositeConstraint) {
            CompositeConstraint constraint = (CompositeConstraint) c;
            BasicCompositeConstraint newConstraint = new BasicCompositeConstraint(constraint.getLogicalOperator());
            for (Constraint cons : constraint.getChilds()) {
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
        } else if (c instanceof FieldValueDateConstraint) {
            FieldValueDateConstraint constraint = (FieldValueDateConstraint) c;
            Object value = constraint.getValue();
            BasicFieldValueDateConstraint newConstraint = new BasicFieldValueDateConstraint(createNewStepField(q, constraint.getField()), value, constraint.getPart());
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
                newConstraint = new BasicFieldValueBetweenConstraint(createNewStepField(q, constraint.getField()), constraint.getLowerLimit(), constraint.getUpperLimit());
            } catch (NumberFormatException e) {
                newConstraint = new BasicFieldValueBetweenConstraint(createNewStepField(q, constraint.getField()), constraint.getLowerLimit(), constraint.getUpperLimit());
            }
            newConstraint.setInverse(constraint.isInverse());
            newConstraint.setCaseSensitive(constraint.isCaseSensitive());
            return newConstraint;
        } else if (c instanceof FieldValueInConstraint) {
            FieldValueInConstraint constraint = (FieldValueInConstraint) c;
            BasicFieldValueInConstraint newConstraint = new BasicFieldValueInConstraint(createNewStepField(q, constraint.getField()));

            for (Object value : constraint.getValues()) {
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
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
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
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
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
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
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
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
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
    public BasicRelationStep addRelationStep(InsRel builder, MMObjectBuilder nextBuilder) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        int nrOfSteps = steps.size();
        if (nrOfSteps == 0) {
           throw new IllegalStateException("No previous step.");
        }
        BasicStep previous = (BasicStep) steps.get(nrOfSteps - 1);
        BasicStep next = new BasicStep(nextBuilder);
        BasicRelationStep relationStep = new BasicRelationStep(builder, previous, next);
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
    public BasicStepField addField(Step step, CoreField fieldDefs) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        if (aggregating) {
            throw new UnsupportedOperationException("Adding non-aggregated field to aggregating query.");
        }
        BasicStepField field = new BasicStepField(step, fieldDefs);
        assert ! fields.contains(field) : "" + field + " is already one of " + fields;
        fields.add(field);
        return field;
    }

    /**
     * @since MMBase-1.8.2
     */
    public BasicStepField addFieldUnlessPresent(Step step, CoreField fieldDefs) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        if (aggregating) {
            throw new UnsupportedOperationException("Adding non-aggregated field to aggregating query.");
        }
        BasicStepField field = new BasicStepField(step, fieldDefs);
        int i = fields.indexOf(field);
        if (i == -1) {
            fields.add(field);
        } else {
            field = (BasicStepField) fields.get(i);
        }
        return field;
    }

    // only sensible for NodeSearchQuery
    protected void mapField(CoreField field, BasicStepField stepField) {

    }

    // MM
    public void  addFields(Step step) {
        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder builder = mmb.getBuilder(step.getTableName());
        addFields(step, builder);
    }
    /**
     * Add all fields of given step
     */
    protected void  addFields(Step step, MMObjectBuilder builder) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        // http://www.mmbase.org/jira/browse/MMB-1435,
        // Using fields with "ORDER_CREATE" only returns fields actually in storage, and also in the
        // right order, which is important for microsoft JDBC.

        for (CoreField field : builder.getFields(NodeManager.ORDER_CREATE)) {
            if (field.inStorage()) {
                BasicStepField stepField = addField(step, field);
                mapField(field, stepField);
            }
        }
    }

    public void removeFields() {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        fields.clear();
    }

    /**
     * Adds new aggregated field to this SearchQuery.
     *
     * @param step The associated step.
     * @param field The associated Field.
     * @param aggregationType The aggregation type.
     * @return The new field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws UnsupportedOperationException when called
     *         on an non-aggregating query.
     */
    public BasicAggregatedField addAggregatedField(Step step, CoreField field, int aggregationType) {
        if (! modifiable) {
            throw new IllegalStateException("Unmodifiable");
        }
        if (!aggregating) {
            throw new UnsupportedOperationException("Adding aggregated field to non-aggregating query.");
        }
        BasicAggregatedField stepField = new BasicAggregatedField(step, field, aggregationType);
        fields.add(stepField);
        return stepField;
    }

    /**
     * Creates sortorder for this SearchQuery.
     *
     * @param field The associated stepfield.
     * @return The new sortOrder
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSortOrder addSortOrder(StepField field) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        if (field == null) throw new IllegalArgumentException();
        BasicSortOrder sortOrder;
        if (field.getType() ==  Field.TYPE_DATETIME) {
            sortOrder = new BasicDateSortOrder(field);
        } else {
            sortOrder = new BasicSortOrder(field);
        }
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
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        this.constraint = constraint;
    }

    // javadoc is inherited
    public boolean isDistinct() {
        return distinct;
    }

    // javadoc is inherited
    public boolean isAggregating() {
        return aggregating;
    }

    // javadoc is inherited
    public List<SortOrder> getSortOrders() {
        // return as unmodifiable list
        return Collections.unmodifiableList(sortOrders);
    }

    // javadoc is inherited
    public List<Step> getSteps() {
        return unmodifiableSteps;
    }


    // javadoc is inherited
    public List<StepField> getFields() {
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

    public CachePolicy getCachePolicy() {
        return cachePolicy;
    }

    public void setCachePolicy(CachePolicy policy) {
        if (! modifiable) throw new IllegalStateException("Unmodifiable");
        this.cachePolicy = policy;
    }

    /**
     * @since MMBase-1.9.1
     */
    public void setModifiable(boolean b) {
        if (! modifiable && b) throw new IllegalStateException("Unmodifiable");
        modifiable = b;
    }

    /**
     * @since MMBase-1.9.2
     */
    public boolean markUsed() {
        boolean wasModifiable = modifiable;
        modifiable = false;
        if (constraint != null) {
            constraint.setUnmodifiable();
        }
        for (SortOrder so : sortOrders) {
            so.setUnmodifiable();
        }
        return ! wasModifiable;
    }

    @Override
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

    protected int calculateHashCode() {
         return (distinct? 0: 101)
             + maxNumber * 17 + offset * 19
             + 23 * steps.hashCode()
             + 29 * fields.hashCode()
             + 31 * sortOrders.hashCode()
             + 37 * (constraint == null? 0: constraint.hashCode());
    }

    @Override
    public int hashCode() {
        if (modifiable) {
            return calculateHashCode();
        } else {
            if (savedHashcode == -1) {
                savedHashcode = calculateHashCode();
            }
            return savedHashcode;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SearchQuery(distinct:").append(isDistinct()).
        append(", steps:" + getSteps()).
        append(", fields:").append(getFields()).
        append(", constraint:").append(getConstraint()).
        append(", sortorders:").append(getSortOrders()).
        append(", max:").append(getMaxNumber()).
        append(", offset:").append(getOffset()).
        append(")");
        return sb.toString();
    }

}
