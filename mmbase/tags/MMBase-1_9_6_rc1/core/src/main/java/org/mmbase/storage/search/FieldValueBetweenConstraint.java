/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constraint that restricts the value of a stepfield to be in a specified
 * range of values (numerical or string).
 * <p>
 * This corresponds to the use of "between ... and ..." in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface FieldValueBetweenConstraint extends FieldConstraint {

    /**
     * Gets the value of the lower limit of the range specified for this
     * constraint.
     */
    Object getLowerLimit();

    /**
     * Gets the value of the upper limit of the range specified for this
     * constraint.
     */
    Object getUpperLimit();

    /**
     * Returns a string representation of this FieldValueBetweenConstraint.
     * The string representation has the form
     * "FieldValueBetweenConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;,
     *  casesensitive:&lt;casesensitive&gt;,
     *  lower:&lt;lowerLimit&gt;, upper:&lt;upperLimit&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by
     *     <code>FieldConstraint#getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;lowerLimit&gt;</em> is the values returned by
     *     {@link #getLowerLimit getValues()}
     * <li><em>&lt;upperLimit&gt;</em> is the values returned by
     *     {@link #getUpperLimit getValues()}
     * </ul>
     *
     * @return A string representation of this FieldValueInConstraint.
     */
    @Override
    String toString();

}
