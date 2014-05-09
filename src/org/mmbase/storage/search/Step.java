/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;

/**
 * A step refers to a table in a search request. Several steps may refer to the same table, therefore each step has an unique alias to identify it.
 * <p>
 * This corresponds to a table name and alias in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface Step {
    /**
     * Gets the name of the table referred to by this step.
     * <p>
     * This corresponds to a table name in SQL SELECT-syntax.
     */
    String getTableName();

    /**
     * Gets the alias associated with this step.
     * <p>
     * This corresponds to a table alias in SQL SELECT-syntax.
     */
    String getAlias();

    /**
     * Gets nodenumbers for nodes that must be included in this step.
     * A null value indicates that no such constraint is applied.
     * <p>
     * This corresponds to a "number IN (....)" constraint in SQL SELECT syntax.
     * <p>
     * Note that this can also be achieved by using a FieldValueInConstraint on the "number" field.
     */
    SortedSet getNodes();

    /**
     * Compares this step to the specified object. The result is 
     * <code>true</code> if and only if the argument is a non-null 
     * Step, but not RelationStep, object associated with the same tablename, 
     * using the same alias and including the same nodes.
     * 
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal, 
     * <code>false</code> otherwise.
     * @see RelationStep#equals
     */
    public boolean equals(Object obj);
    
    // javadoc is inherited
    public int hashCode();
    
    /**
     * Returns a string representation of this Step. 
     * The string representation has the form 
     * "Step(tablename:&lt;tablename&gt;, alias:&lt;alias&gt;, nodes:&lt;nodes&gt;)"
     * where 
     * <ul>
     * <li><em>&lt;tablename&gt;</em> is the tablename returnedby 
     *     {@link #getTableName getTableName()}
     * <li><em>&lt;alias&gt;</em> is the alias returned by {@link #getAlias getAlias()}
     * <li><em>&lt;nodes&gt;</em> is the string representation of the ordered list 
     * of nodenumbers returned by {@link #getNodes getNodes()}
     * </ul>
     *
     * @return A string representation of this Step.
     */
    public String toString();

    /** @link dependency 
     * @clientRole **/
    /*#SearchQuery lnkSearchQuery;*/
}
