package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicFieldValueInConstraint extends BasicFieldConstraint implements FieldValueInConstraint {
    
    /** The values. */
    private SortedSet values = new TreeSet();
    
    /**
     * Constructor.
     *
     * @param field The associated field.
     */
    public BasicFieldValueInConstraint(StepField field) {
        super(field);
    }
    
    /**
     * Adds value.
     *
     * @param value The value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void addValue(Object value) {
        // Add value as string. This facilitates comparison of
        // numerical values of different type.
        BasicStepField.testValue(value, getField());
        values.add(value.toString());
    }
    
    // javadoc is inherited
    public SortedSet getValues() {
        return Collections.unmodifiableSortedSet(values);
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldValueInConstraint constraint = (BasicFieldValueInConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && getField().getStep().getTableName().equals(
                    constraint.getField().getStep().getTableName())
                && values.equals(constraint.values);
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
        + 89 * values.hashCode();
    }
    
    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("FieldValueInConstraint(field:");
        sb.append(getField().getAlias()).
        append(", casesensitive:").
        append(isCaseSensitive()).
        append(", values:").
        append(getValues()).
        append(")");
        return sb.toString();
    }
}
