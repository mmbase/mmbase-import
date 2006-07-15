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
 * @version $Id: FieldCompareConstraint.java,v 1.3 2003-03-10 11:50:45 pierre Exp $
 * @since MMBase-1.7
 */
public interface FieldCompareConstraint extends FieldConstraint {
    /**
     * Gets the operator used to compare values. 
     * This must be one of the values declared here.
     * The value <code>LIKE</code> is allowed only when the associated field 
     * is of string type.
     */
    int getOperator();

    int LESS = 1;
    int LESS_EQUAL = 2;
    int EQUAL = 3;
    int NOT_EQUAL = 4;
    int GREATER = 5;
    int GREATER_EQUAL = 6;
    int LIKE = 7;
}
