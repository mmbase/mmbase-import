package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.3 $
 */
public class BasicFieldValueConstraint extends BasicFieldCompareConstraint
implements FieldValueConstraint {
    
    /** The value. */
    private Object value = null;
    
    /**
     * Constructor.
     *
     * @param field The associated field.
     * @param value The non-null property value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueConstraint(StepField field, Object value) {
        super(field);
        setValue(value);
    }
    
    /**
     * Sets value property.
     *
     * @param value The non-null property value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setValue(Object value) {
        BasicStepField.testValue(value, getField());
        this.value = value;
    }
    
    // javadoc is inherited
    public Object getValue() {
        return value;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldValueConstraint constraint 
                = (BasicFieldValueConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && getField().getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName())
                && getOperator() == constraint.getOperator()
                && (value == null? constraint.value == null: value.equals(constraint.value));
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (isInverse()? 0: 107)
        + (isCaseSensitive()? 0: 73)
        + 79 * getField().getFieldName().hashCode()
        + 83 * getField().getStep().getTableName().hashCode()
        + 113 * getOperator()
        + (value == null? 0: value.hashCode());
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("FieldValueConstraint(field:");
        sb.append(getField().getAlias()).
        append(", casesensitive:").
        append(isCaseSensitive()).
        append(", operator:").
        append(getOperator()).
        append(", value:").
        append(getValue()).
        append(")");
        return sb.toString();
    }
}
