/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicCompositeConstraint extends BasicConstraint implements CompositeConstraint {
    private static final Logger log = Logging.getLoggerInstance(BasicCompositeConstraint.class);

    private final List<Constraint> childs = new ArrayList<Constraint>();

    private final int logicalOperator;

    /**
     * Constructor.
     *
     * @param logicalOperator The logical operator.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicCompositeConstraint(int logicalOperator, Constraint... childs) {
        // Invalid argument, must be either LOGICAL_AND or LOGICAL_OR.
        if (logicalOperator != CompositeConstraint.LOGICAL_AND
        && logicalOperator != CompositeConstraint.LOGICAL_OR) {
            throw new IllegalArgumentException(
            ("Invalid argument: " + logicalOperator + ", must be either "
            + CompositeConstraint.LOGICAL_AND + " or "
            + CompositeConstraint.LOGICAL_OR));
        }
        this.logicalOperator = logicalOperator;
        for (Constraint child : childs) {
            addChild(child);
        }
    }

    public void setUnmodifiable() {
        super.setUnmodifiable();
        for (Constraint c : childs) {
            c.setUnmodifiable();
        }
    }

    /**
     * Adds new child constraint.
     *
     * @param child The child constraint.
     * @return This <code>BasicCompositeConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicCompositeConstraint addChild(Constraint child) {
        if (! modifiable) throw new IllegalStateException();
        if (child == null) {
            throw new IllegalArgumentException("Invalid child argument: " + child);
        }
        // Check constraint not added to itself.
        if (child == this) {
            throw new IllegalArgumentException("Trying to add constraint as child to itself: " + child);
        }
        childs.add(child);
        return this;
    }

    public BasicCompositeConstraint removeChild(Constraint child) {
        if (! modifiable) throw new IllegalStateException();
        if (! childs.remove(child)) {
            log.info("Tried to remove non existing child");
        }
        return this;
    }


    // javadoc is inherited
    public List<Constraint> getChilds() {
        // return a unmodifiable list
        return Collections.unmodifiableList(childs);
    }

    // javadoc is inherited
    public int getLogicalOperator() {
        return logicalOperator;
    }

    /**
     * Returns a description of the logical operator
     */
    public String getLogicalOperatorDescription() {
        try {
            return CompositeConstraint.LOGICAL_OPERATOR_DESCRIPTIONS[logicalOperator];
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    // javadoc is inherited
    public int getBasicSupportLevel() {
        // Calculate support as lowest value among childs.
        int result = SearchQueryHandler.SUPPORT_OPTIMAL;
        Iterator<Constraint> iChilds = childs.iterator();
        while (iChilds.hasNext()) {
            Constraint constraint = iChilds.next();
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
            // XXX should order of childs matter (it does now)?
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 109 * logicalOperator
        + 71 * childs.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("CompositeConstraint(inverse:").append(isInverse()).
        append(", operator:").append(getLogicalOperatorDescription()).
        append(", childs:").append(getChilds()).
        append(")");
        return sb.toString();
    }
}
