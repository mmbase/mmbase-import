/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A sortorder specifies sorting of a single field.
 * <p>
 * This corresponds to use of ORDER BY in SQL SELECT-syntax. 
 *
 * @author Rob van Maris
 * @version $Id: SortOrder.java,v 1.2 2003-03-10 11:50:51 pierre Exp $
 * @since MMBase-1.7
 */
public interface SortOrder {

    int ORDER_ASCENDING = 1;
    int ORDER_DESCENDING = 2;

    /**
     * Gets the associated field.
     * <p>
     * This corresponds to a fieldname in a "ORDER BY" clause in SQL SELECT-syntax.
     */
    StepField getField();

    /**
     * Gets the sort direction. This is be either ORDER_ASCENDING or ORDER_DESCENDING.
     * <p>
     * This corresponds to the use of ASC and DESC in SQL SELECT-syntax.
     */
    int getDirection();

    /**
     * Compares this sortorder to the specified object. The result is 
     * <code>true</code> if and only if the argument is a non-null 
     * SortOrder object associated with the same field, using the same
     * sort direction.
     * 
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal, 
     * <code>false</code> otherwise.
     */
    public boolean equals(Object obj);
    
    // javadoc is inherited
    public int hashCode();

    /**
     * Returns a string representation of this SortOrder. 
     * The string representation has the form 
     * "SortOrder(field:&lt;field&gt;, dir:&lt;dir&gt;)"
     * where 
     * <ul>
     * <li><em>&lt;field&gt;</em> is the field alias returned by 
     *     <code>getField().getAlias()</code>
     * <li><em>&lt;dir&gt;</em> is the direction returned by
     *     {@link #getDirection getDirection()}
     * </ul>
     *
     * @return A string representation of this SortOrder.
     */
    public String toString();

    /** @link dependency 
     * @clientRole 0-1*/
    /*#StepField lnkStepField;*/

    /** @link dependency 
     * @clientRole **/
    /*#SearchQuery lnkSearchQuery;*/
}
