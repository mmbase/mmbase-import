/* Generated by Together */

package org.mmbase.storage.search;

/**
 * A constraint that compares a stepfield value with a fixed value.
 * <p>
 * This corresponds with comparison operators <, =, > and LIKE in SQL SELECT-syntax.
 */
public interface FieldValueConstraint extends FieldCompareConstraint {
    /**
     * Gets the value to compare with. If the associated field type is of string type, when used in combination with the operator <code>LIKE</code>,this may contain the following wildcard characters as well:
     * <ul>
     * <li>% for any string
     * <li>_ for a single character
     * </ul>
     */
    Object getValue();

    /**
     * Returns a string representation of this FieldValueConstraint. 
     * The string representation has the form 
     * "FieldValueConstraint(field:&lt;field&gt;, 
     *  casesensitive:&lt;casesensitive&gt;, operator:&lt;operator&gt;,
     *  value:&lt;value&gt;)"
     * where 
     * <ul>
     * <li><em>&lt;field&gt;</em> is the field alias returned by 
     *     <code>FieldConstraint#getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;operator&gt;</em> is the value returned by
     *     (@link FieldCompareConstraint#getOperator getOperator()}
     * <li><em>&lt;value&gt;</em> is the value returned by
     *     {@link #getValue getValue()}
     * </ul>
     *
     * @return A string representation of this FieldValueConstraint.
     */
    public String toString();

}
