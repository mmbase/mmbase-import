/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.7 $
 * @since MMBase-1.7
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
                && getField().getStep().getAlias().equals(
                    constraint.getField().getStep().getAlias())
                && getOperator() == constraint.getOperator()
                && (value == null? constraint.value == null: value.equals(constraint.value));
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + (value == null? 0: value.hashCode());
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("FieldValueConstraint(inverse:").
        append(isInverse()).
        append(", field:").
        append(getField().getAlias()).
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
