package org.mmbase.module.database.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicFieldCompareConstraint extends BasicFieldConstraint 
implements FieldCompareConstraint {
    
    /** The operator. */
    private int operator = 0;
    
    /**
     * Constructor.
     *
     * @param field The associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldCompareConstraint(StepField field) { 
        super(field);
        // Operator defaults to equal.
        operator = FieldValueConstraint.EQUAL;
    }
    
    /**
     * Sets operator.
     *
     * @param the operator value
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setOperator(int operator) {
        
        // Test for defined operator value.
        if (operator != FieldValueConstraint.LESS
        && operator != FieldValueConstraint.EQUAL
        && operator != FieldValueConstraint.GREATER
        && operator != FieldValueConstraint.LIKE) {
            throw new IllegalArgumentException(
            "Invalid operator value: " + operator );
        }
        
        // Test "LIKE" operator only used with string type field.
        if (operator == FieldValueConstraint.LIKE
        && getField().getType() != FieldDefs.TYPE_STRING
        && getField().getType() != FieldDefs.TYPE_XML) {
            throw new IllegalArgumentException(
            "LIKE operator not allowed for this field type: "
            + getField().getType());
        }
        
        this.operator = operator;
    }
    
    // javadoc is inherited
    public int getOperator() {
        return operator;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldCompareConstraint constraint 
                = (BasicFieldCompareConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && getField().getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName())
                && operator == constraint.operator;
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
        + 113 * operator;
    }
}
