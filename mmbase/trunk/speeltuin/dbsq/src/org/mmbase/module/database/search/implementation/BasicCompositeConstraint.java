package org.mmbase.module.database.search.implementation;

import java.util.*;
import org.mmbase.module.database.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicCompositeConstraint extends BasicConstraint
implements CompositeConstraint {
    
    /** The child constraints. */
    private List childs = new ArrayList();
    
    /** The logical operator. */
    private int logicalOperator = 0;
    
    /**
     * Constructor.
     *
     * @param logicalOperator The logical operator.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicCompositeConstraint(int logicalOperator) {
        // Invalid argument, must be either LOGICAL_AND or LOGICAL_OR.
        if (logicalOperator != CompositeConstraint.LOGICAL_AND
        && logicalOperator != CompositeConstraint.LOGICAL_OR) {
            throw new IllegalArgumentException(
            ("Invalid argument: " + logicalOperator + ", must be either "
            + CompositeConstraint.LOGICAL_AND + " or "
            + CompositeConstraint.LOGICAL_OR));
        }
        this.logicalOperator = logicalOperator;
    }
    
    /**
     * Adds new child constraint.
     *
     * @param child The child constraint.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void addChild(Constraint child) {
        if (child == null) {
            throw new IllegalArgumentException(
            "Invalid child argument: " + child);
        }
        // Check constraint not added to itself.
        if (child == this) {
            throw new IllegalArgumentException(
            "Trying to add constraint as child to itself: " + child);
        }
        childs.add(child);
    }
    
    // javadoc is inherited
    public List getChilds() {
        // return a unmodifiable list
        return Collections.unmodifiableList(childs);
    }
    
    // javadoc is inherited
    public int getLogicalOperator() {
        return logicalOperator;
    }
    
    // javadoc is inherited
    public int getBasicSupportLevel() {
        // Calculate support as lowest value among childs.
        int result = SearchQueryHandler.SUPPORT_OPTIMAL;
        Iterator iChilds = childs.iterator();
        while (iChilds.hasNext()) {
            Constraint constraint = (Constraint) iChilds.next();
            int support = constraint.getBasicSupportLevel();
            if (support < result) {
                result = support;
                // Stop iteration when a not supported child constraint is found.
                if (result == SearchQueryHandler.SUPPORT_NONE) {
                    break;
                }
            }
        }
        return result;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CompositeConstraint) {
            CompositeConstraint constraint = (CompositeConstraint) obj;
            return isInverse() == constraint.isInverse()
                && logicalOperator == constraint.getLogicalOperator()
                && childs.equals(constraint.getChilds());
            // TODO: should order of childs matter (it does now)?
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (isInverse()? 0: 107) 
        + 109 * logicalOperator
        + 71 * childs.hashCode();
    }
}
