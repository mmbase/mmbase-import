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
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicCompareFieldsConstraint extends BasicFieldCompareConstraint
implements CompareFieldsConstraint {

    /** The second associated field. */
    private StepField field2 = null;

    /**
     * Constructor.
     *
     * @param field1 The associated field.
     * @param field2 The second associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicCompareFieldsConstraint(StepField field1, StepField field2) {
        super(field1);

        // Test for non-null value.
        if (field2 == null) {
            throw new IllegalArgumentException(
            "Invalid field2 value: " + field2);
        }

        // Test for matching fieldtype.
        if (field1.getType() != field2.getType()) {
            throw new IllegalArgumentException(
            "Fieldtypes do not match: " + field1.getType()
            + " and " + field2.getType());
        }
        this.field2 = field2;
    }

    // javadoc is inherited
    public StepField getField2() {
        return field2;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicCompareFieldsConstraint constraint
                = (BasicCompareFieldsConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep())
                && getOperator() == constraint.getOperator()
                && field2.getFieldName().equals(constraint.getField2().getFieldName())
                && BasicStepField.compareSteps(field2.getStep(),
                    constraint.getField2().getStep());
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 93 * field2.getFieldName().hashCode()
        + (field2.getStep().getAlias() == null?
            101 * field2.getStep().getTableName().hashCode():
            97 * field2.getStep().getAlias().hashCode());
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("CompareFieldsConstraint(inverse:").
        append(isInverse()).
        append(", field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(", operator:").append(getOperatorDescription()).
        append(", field2:").append(BasicStepField.getFieldName(getField2())).
        append(")");
        return sb.toString();
    }
}
