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
 *
 * @author Rob van Maris
 * @version $Id: BasicFieldValueBetweenConstraint.java,v 1.4 2003-03-10 11:50:55 pierre Exp $
 * @since MMBase-1.7
 */
public class BasicFieldValueBetweenConstraint extends BasicFieldConstraint 
implements FieldValueBetweenConstraint {
    
    /** The lower limit. */
    private String lowerLimit = null;
    
    /** The upper limit. */
    private String upperLimit = null;
    
    /**
     * Constructor.
     * <p>
     * Depending on the field type, the limit values must be of type 
     * <code>String</code> or <code>Number</code>.
     *
     * @param field The associated field.
     * @param lowerLimit The lower limit.
     * @param upperLimit The upper limit.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueBetweenConstraint(
        StepField field, Object lowerLimit, Object upperLimit) {
            
        super(field);
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }
    
    /**
     * Sets the lower limit property.
     * <p>
     * Depending on the field type, the value must be of type 
     * <code>String</code> or <code>Number</code>.
     *
     * @param lowerLimit The non-null lower limit property value.
     * @return This <code>BasicFieldValueBetweenConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueBetweenConstraint setLowerLimit(Object lowerLimit) {
        this.lowerLimit = convertValue(lowerLimit);
        return this;
    }
    
    /**
     * Sets the upper limit property.
     * <p>
     * Depending on the field type, the value must be of type 
     * <code>String</code> or <code>Number</code>.
     *
     * @param upperLimit The non-null upper limit property value.
     * @return This <code>BasicFieldValueBetweenConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueBetweenConstraint setUpperLimit(Object upperLimit) {
        this.upperLimit = convertValue(upperLimit);
        return this;
    }
    
    // javadoc is inherited
    public String getLowerLimit() {
        return lowerLimit;
    }
    
    // javadoc is inherited
    public String getUpperLimit() {
        return upperLimit;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldValueBetweenConstraint constraint = (BasicFieldValueBetweenConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep())
                && lowerLimit.equals(constraint.lowerLimit)
                && upperLimit.equals(constraint.upperLimit);
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return 101 * (lowerLimit.hashCode() 
            + 97 * (upperLimit.hashCode() 
                + 89 * super.hashCode()));
    }
    
    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("FieldValueBetweenConstraint(inverse:").
        append(isInverse()).
        append(", field:").
        append(getField().getAlias()). // TODO RvM: handle null alias.
        append(", casesensitive:").
        append(isCaseSensitive()).
        append(", lower:").
        append(getLowerLimit()).
        append(", upper:").
        append(getUpperLimit()).
        append(")");
        return sb.toString();
    }
    
    /**
     * Tests type of value, and converts it to a string as required by {@link 
     * #getLowerLimit() getLowerLimit()} and {@link #getUpperLimit() 
     * getUpperLimit()}
     *
     * @param value The value.
     * @return The resulting string.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @see org.mmbase.storage.FieldValueBetweenConstraint#getLowerLimit()
     * @see org.mmbase.storage.FieldValueBetweenConstraint#getUpperLimit()
     */
    private String convertValue(Object value) {
        String result = null;
        BasicStepField.testValue(value, getField());
        if (value instanceof Number) {
            // Add value as string. This facilitates comparison of
            // numerical values of different type.
            Number numberValue = (Number) value;
            // Represent integral value as integer, 
            // other values as floating point.
            if (numberValue.intValue() == numberValue.doubleValue()) {
                result = Integer.toString(numberValue.intValue());
            } else {
                result = numberValue.toString();
            }
        } else {
            result = value.toString();
        }
        return result;
    }
}
