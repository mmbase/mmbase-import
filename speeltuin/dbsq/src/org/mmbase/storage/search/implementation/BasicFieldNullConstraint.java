package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
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
                && getField().getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName());
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (isInverse()? 0: 107)
        + (isCaseSensitive()? 0: 73)
        + 79 * getField().getFieldName().hashCode()
        + 83 * getField().getStep().getTableName().hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("FieldNullConstraint(field:");
        sb.append(getField().getAlias()).
        append(", casesensitive:").
        append(isCaseSensitive()).
        append(")");
        return sb.toString();
    }
}
