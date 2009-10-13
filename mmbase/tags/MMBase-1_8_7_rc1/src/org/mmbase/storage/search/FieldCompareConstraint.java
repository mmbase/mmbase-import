/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constraint that compares a stepfield value with another value.
 * <p>
 * This corresponds with comparison operators <, =, > and LIKE in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface FieldCompareConstraint extends FieldConstraint {

    /** Operator 'less than' */
    public final static int LESS = 1;
    /** Operator 'less than or equal' */
    public final static int LESS_EQUAL = 2;
    /** Operator 'equal' */
    public final static int EQUAL = 3;
    /** Operator 'not equal' */
    public final static int NOT_EQUAL = 4;
    /** Operator 'greater than' */
    public final static int GREATER = 5;
    /** Operator 'greater than or equal' */
    public final static int GREATER_EQUAL = 6;
    /** Operator 'like' */
    public final static int LIKE = 7;

    /**
     * Operator descriptions corresponding to the operator values:
     * {@link #LESS}, {@link #LESS_EQUAL}, {@link #EQUAL}, {@link #NOT_EQUAL},
     * {@link #GREATER}, {@link #GREATER_EQUAL}, and {@link #LIKE}
     */
    public final static String[] OPERATOR_DESCRIPTIONS = new String[] {
        null, // not specified
        "less than", "less than or equal", "equal", "not equal",
         "greater than", "greater than or equal", "like"
    };

    /**
     * Gets the operator used to compare values.
     * This must be one of the values declared here.
     * The value <code>LIKE</code> is allowed only when the associated field
     * is of string type.
     */
    int getOperator();
}
