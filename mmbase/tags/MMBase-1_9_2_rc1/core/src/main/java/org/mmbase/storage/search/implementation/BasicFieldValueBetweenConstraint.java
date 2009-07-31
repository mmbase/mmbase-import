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
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicFieldValueBetweenConstraint extends BasicFieldConstraint
implements FieldValueBetweenConstraint {

    /** The lower limit. */
    private Object lowerLimit = null;

    /** The upper limit. */
    private Object upperLimit = null;

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
        if (! modifiable) throw new IllegalStateException();
        this.lowerLimit = lowerLimit;
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
        if (! modifiable) throw new IllegalStateException();
        this.upperLimit = upperLimit;
        return this;
    }

    // javadoc is inherited
    public Object getLowerLimit() {
        return lowerLimit;
    }

    // javadoc is inherited
    public Object getUpperLimit() {
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
        StringBuilder sb = new StringBuilder("FieldValueBetweenConstraint(inverse:").append(isInverse()).
        append(", field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(", lower:").append(getLowerLimit()).
        append(", upper:").append(getUpperLimit()).
        append(")");
        return sb.toString();
    }
}
