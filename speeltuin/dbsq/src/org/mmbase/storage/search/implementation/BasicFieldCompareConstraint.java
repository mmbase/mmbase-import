package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.5 $
 */
public class BasicFieldCompareConstraint extends BasicFieldConstraint 
implements FieldCompareConstraint {
    
    /** The operator. */
    private int operator = FieldValueConstraint.EQUAL;
    
    /**
     * Constructor.
     * Protected, so only subclasses can be instantiated.
     *
     * @param field The associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    protected BasicFieldCompareConstraint(StepField field) { 
        super(field);
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
                && getField().getStep().getAlias().equals(
                    constraint.getField().getStep().getAlias())
                && operator == constraint.operator;
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 113 * operator;
    }
}
