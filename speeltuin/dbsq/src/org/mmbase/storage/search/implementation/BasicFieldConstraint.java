package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The caseSensitive property defaults to <code>true</code>.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
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
            throw new IllegalArgumentException(
            "Invalid field value: " + field);
        }
        
        this.field = field;
    }
    
    /**
     * Sets caseSensitive property.
     * This has only effect when the associated field is of string type.
     *
     * @param caseSensitive The caseSensitive property value.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
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
                && field.getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName());
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (isInverse()? 0: 107)
        + (isCaseSensitive()? 0: 73)
        + 79 * field.getFieldName().hashCode()
        + 83 * field.getStep().getTableName().hashCode();
    }
}
