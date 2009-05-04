/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation.
 * <p>
 * <em>This constraint type is provided for the sole purpose of aligning
 * existing legacy code with the new search query framework, and will
 * eventually be phased out.</em>
 *
 * @author  Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicLegacyConstraint extends BasicConstraint
implements LegacyConstraint {

    /** Logger instance. */
    private static final Logger log = Logging.getLoggerInstance(BasicLegacyConstraint.class);

    /** The constraint. */
    private String constraint = null;

    /**
     * Constructor.
     *
     * @param constraint The non-null constraint as it appears in
     *        the where-clause.
     * @throws IllegalArgumentException When an invalid argument is supplied.
     */
    public BasicLegacyConstraint(String constraint) {
        setConstraint(constraint);
    }

    /**
     * Sets the constraint.
     *
     * @param constraint The non-null constraint as it appears in
     *                   the where-clause (may also not be empty string)
     * @return This <code>BasicLegacyConstraint</code> instance.
     * @throws IllegalArgumentException When an invalid argument is supplied.
     */
    public BasicLegacyConstraint setConstraint(String constraint) {
        if (log.isDebugEnabled()) {
            log.debug("Legacy constraint: " + constraint);
        }
        // Test constraint is not null or empty (empty goes wrong in composite constraints)
        if (constraint == null || constraint.equals("")) {
            throw new IllegalArgumentException("Invalid constraint value: " + constraint);
        }

        this.constraint = constraint;
        return this;
    }

    // javadoc is inherited
    public String getConstraint() {
        return constraint;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicLegacyConstraint lc = (BasicLegacyConstraint) obj;
            return isInverse() == lc.isInverse() && constraint.equals(lc.getConstraint());
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return 31 * (constraint.hashCode() + super.hashCode());
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("LegacyConstraint(inverse:").
        append(isInverse()).
        append(", constraint:").append(getConstraint()).
        append(")");
        return sb.toString();
    }
}
