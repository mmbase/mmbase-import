/* Generated by Together */

package org.mmbase.storage.search;

/**
 * A constraint that tests if a stepfield value is null.
 * <p>
 * This corresponds to IS NULL in SQL SELECT-syntax.
 */
public interface FieldNullConstraint extends FieldConstraint {

    /**
     * Returns a string representation of this FieldNullConstraint. 
     * The string representation has the form 
     * "FieldNullConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;, 
     *  casesensitive:&lt;casesensitive&gt;)"
     * where 
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by 
     *     <code>FieldConstraint#getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * </ul>
     *
     * @return A string representation of this FieldNullConstraint.
     */
    public String toString();

}
