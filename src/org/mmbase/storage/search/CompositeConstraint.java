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
 * @version $Id: CompositeConstraint.java,v 1.2 2003-03-10 11:50:44 pierre Exp $
 * @since MMBase-1.7
 */
public interface CompositeConstraint extends Constraint {
    int LOGICAL_AND = 2;
    int LOGICAL_OR = 1;

    /**
     * Gets the child constraints.
     */
    List getChilds();

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
    public boolean equals(Object obj);
    
    // javadoc is inherited
    public int hashCode();

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
    public String toString();

    /** @link dependency 
     * @label child
     * @supplierRole **/
    /*#Constraint lnkConstraint;*/
}
