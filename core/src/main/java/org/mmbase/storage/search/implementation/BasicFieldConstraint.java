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
 * The caseSensitive property defaults to <code>true</code>.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */

// this class would logically be abstract, but test-cases are instantiating it.
public class BasicFieldConstraint extends BasicConstraint implements FieldConstraint {

    /** The associated field. */
    private StepField field;

    /** The caseSensitive property. */
    private boolean caseSensitive = true;

    /**
     * Constructor.
     * Protected, so only subclasses can be instantiated.
     *
     * @param field The associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    protected BasicFieldConstraint(StepField field) {
        // Test for non-null value.
        if (field == null) {
            throw new IllegalArgumentException("Invalid field value: " + field);
        }
        this.field = field;
    }

    /**
     * Sets caseSensitive property.
     * This has only effect when the associated field is of string type.
     *
     * @return This <code>BasicFieldConstraint</code> instance.
     * @param caseSensitive The caseSensitive property value.
     */
    public BasicFieldConstraint setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    // javadoc is inherited
    public StepField getField() {
        return field;
    }

    // javadoc is inheritied
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldConstraint constraint = (BasicFieldConstraint) obj;
            return isInverse() == constraint.isInverse()
                && caseSensitive == constraint.isCaseSensitive()
                && field.getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep());
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + (isCaseSensitive()? 0: 73)
        + 79 * field.getFieldName().hashCode()
        + (field.getStep().getAlias() == null?
            87 * field.getStep().getTableName().hashCode():
            83 * field.getStep().getAlias().hashCode());
    }

    /**
     * Returns the main field's fieldname, possibly extended with the step'sname if known.
     * May return null or partial fieldnames if not all data is available (for use in debugging).
     */
    public String getFieldName() {
        return BasicStepField.getFieldName(getField());
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("BasicFieldConstraint(inverse:").append(isInverse()).
        append("field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(")");
        return sb.toString();
    }

}
