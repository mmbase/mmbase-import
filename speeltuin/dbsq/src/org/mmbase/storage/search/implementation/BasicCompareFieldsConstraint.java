package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.6 $
 */
public class BasicCompareFieldsConstraint extends BasicFieldCompareConstraint 
implements CompareFieldsConstraint {
    
    /** The second associated field. */
    private StepField field2 = null;
    
    /**
     * Constructor.
     *
     * @param field The associated field.
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
                && getField().getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName())
                && getOperator() == constraint.getOperator()
                && field2.getFieldName().equals(constraint.getField2().getFieldName())
                && field2.getStep().getAlias().equals(
                    constraint.getField2().getStep().getAlias());
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 93 * field2.getFieldName().hashCode()
        + 97 * field2.getStep().getAlias().hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("CompareFieldsConstraint(inverse:").
        append(isInverse()).
        append(", field:").
        append(getField().getAlias()).
        append(", casesensitive:").
        append(isCaseSensitive()).
        append(", operator:").
        append(getOperator()).
        append(", field2:").
        append(getField2().getAlias()).
        append(")");
        return sb.toString();
    }
}
