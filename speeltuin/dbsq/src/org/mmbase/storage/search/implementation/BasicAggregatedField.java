package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The step alias is equal to the field name, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicAggregatedField extends BasicStepField
implements AggregatedField {
    
    /** he aggregation type. */
    private int aggregationType = 0;
    
    /**
     * Constructor.
     *
     * @param step The associated step.
     * @param fieldDefs The associated fieldDefs.
     * @param aggregationType The aggregation type.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicAggregatedField(Step step, FieldDefs fieldDefs, 
    int aggregationType) {
        super(step, fieldDefs);
        setAggregationType(aggregationType);
    }
    
    /**
     * Sets the aggregation type.
     *
     * @param aggregationType The aggregation type.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setAggregationType(int aggregationType) {
        if (aggregationType < AggregatedField.AGGREGATION_TYPE_GROUP_BY
        || aggregationType > AggregatedField.AGGREGATION_TYPE_MAX) {
            throw new IllegalArgumentException(
            "Invalid aggregationType value: " + aggregationType);
        }
        this.aggregationType = aggregationType;
    }        
    
    /**
     * Gets the aggregation type.
     */
    public int getAggregationType() {
        return aggregationType;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj instanceof AggregatedField) {
            AggregatedField field = (AggregatedField) obj;
            return getStep().getAlias().equals(field.getStep().getAlias())
                && getFieldName().equals(field.getFieldName())
                && getAlias().equals(field.getAlias())
                && aggregationType == field.getAggregationType();
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 149 * aggregationType;
    }

    // javadoc is inherited
    public String toString() {
        return "AggregatedField(step:" + getStep().getAlias()
        + ", fieldname:" + getFieldName()
        + ", alias:" + getAlias() 
        + ", aggregationtype:" + aggregationType + ")";
    }
    
}
