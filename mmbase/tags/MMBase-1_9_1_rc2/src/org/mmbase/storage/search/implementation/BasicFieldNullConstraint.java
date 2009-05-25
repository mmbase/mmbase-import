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
public class BasicFieldNullConstraint extends BasicFieldConstraint
implements FieldNullConstraint {

    /**
     * Constructor.
     *
     * @param field The associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldNullConstraint(StepField field) {
        super(field);
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldNullConstraint constraint = (BasicFieldNullConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep());
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("FieldNullConstraint(inverse:").append(isInverse()).
        append(", field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(")");
        return sb.toString();
    }
}
