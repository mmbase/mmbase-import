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
 * @version $Revision: 1.3 $
 * @since MMBase-1.7
 */
public class BasicFieldValueConstraint extends BasicFieldCompareConstraint
implements FieldValueConstraint {
    
    /** The value. */
    private Object value = null;
    
    /**
     * Constructor.
     * Depending on the field type, the value must be of type 
     * <code>String</code> or <code>Number</code>.
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
     * Depending on the field type, the value must be of type 
     * <code>String</code> or <code>Number</code>.
     *
     * @param value The non-null property value.
     * @return This <code>BasicFieldValueConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueConstraint setValue(Object value) {
        BasicStepField.testValue(value, getField());
        this.value = value;
        return this;
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
