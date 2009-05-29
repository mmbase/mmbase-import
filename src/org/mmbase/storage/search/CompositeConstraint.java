/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;

/**
 * A constraint combining several child constraints, using either logical AND or OR.
 * <p>
 * This corresponds to a AND- or OR-expression in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface CompositeConstraint extends Constraint {

    /** Logical operator 'and' */
    final static int LOGICAL_AND = 2;
    /** Logical operator 'or' */
    final static int LOGICAL_OR = 1;

    /**
     * Operator descriptions corresponding to the operator values:
     * {@link #LOGICAL_AND}, and {@link #LOGICAL_OR}
     */
    final static String[] LOGICAL_OPERATOR_DESCRIPTIONS = new String[]{
         null, // not specified
         "or",
         "and"
    };
    /**
     * Gets the child constraints.
     */
    List<Constraint> getChilds();

    /**
     * Gets the logical operator used to combine the child constraints. This must be either LOGICAL_AND or LOGICAL_OR.
     */
    int getLogicalOperator();

    /**
     * Compares this constraint to the specified object. The result is
     * <code>true</code> if and only if the argument is a non-null
     * CompositeConstraint object representing the same constraint(s).
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal,
     * <code>false</code> otherwise.
     */
    @Override
    boolean equals(Object obj);

    // javadoc is inherited
    @Override
    int hashCode();

    /**
     * Returns a string representation of this CompositeConstraint.
     * The string representation has the form
     * "CompositeConstraint(inverse:&lt:inverse&gt;, operator:&lt;operator&gt;,
     *  childs:&lt;childs&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;operator&gt;</em> is the value returned by
     *     {@link #getLogicalOperator getLogicalOperator()}
     * <li><em>&lt;childs&gt;</em> is the value returned by
     *     {@link #getChilds getChilds()}
     * </ul>
     *
     * @return A string representation of this CompositeConstraint.
     */
    @Override
    String toString();


}
