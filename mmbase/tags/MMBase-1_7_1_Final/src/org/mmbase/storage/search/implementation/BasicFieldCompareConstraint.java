/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Id: BasicFieldCompareConstraint.java,v 1.5 2003-03-10 11:50:54 pierre Exp $
 * @since MMBase-1.7
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
     * @return This <code>BasicFieldCompareConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldCompareConstraint setOperator(int operator) {
        
        // Test for defined operator value.
        if (operator < FieldValueConstraint.LESS
        || operator > FieldValueConstraint.LIKE) {
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
        return this;
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
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep())
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
