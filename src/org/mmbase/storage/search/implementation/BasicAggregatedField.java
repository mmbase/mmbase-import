/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The step alias is equal to the field name, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Id: BasicAggregatedField.java,v 1.8 2005-05-10 22:58:58 michiel Exp $
 * @since MMBase-1.7
 */
public class BasicAggregatedField extends BasicStepField implements AggregatedField {

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
    public BasicAggregatedField(Step step, CoreField fieldDefs, int aggregationType) {
        super(step, fieldDefs);
        setAggregationType(aggregationType);
    }

    /**
     * Sets the aggregation type.
     *
     * @param aggregationType The aggregation type.
     * @return This <code>BasicAggregatedField</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicAggregatedField setAggregationType(int aggregationType) {
        if (aggregationType < AggregatedField.AGGREGATION_TYPE_GROUP_BY
        || aggregationType > AggregatedField.AGGREGATION_TYPE_MAX) {
            throw new IllegalArgumentException(
            "Invalid aggregationType value: " + aggregationType);
        }
        this.aggregationType = aggregationType;
        return this;
    }

    /**
     * Gets the aggregation type.
     */
    public int getAggregationType() {
        return aggregationType;
    }

    /**
     * Gets the aggregation type.
     */
    public String getAggregationTypeDescription() {
        try {
            return AggregatedField.AGGREGATION_TYPE_DESCRIPTIONS[aggregationType];
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj instanceof AggregatedField) {
            AggregatedField field = (AggregatedField) obj;
            return BasicStepField.compareSteps(getStep(), field.getStep())
                && getFieldName().equals(field.getFieldName())
                && (getAlias() == null? true: getAlias().equals(field.getAlias()))
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
        StringBuffer sb = new StringBuffer("AggregatedField(step:");
        if (getStep() == null) {
            sb.append("null");
        } else {
            if (getStep().getAlias() == null) {
                sb.append(getStep().getTableName());
            } else {
                sb.append(getStep().getAlias());
            }
        }
        sb.append(", fieldname:").append(getFieldName()).
        append(", alias:").append(getAlias()).
        append(", aggregationtype:").append(getAggregationTypeDescription()).
        append(")");
        return sb.toString();
    }

}
