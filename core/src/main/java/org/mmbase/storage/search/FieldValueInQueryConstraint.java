/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;

/**
 * A constraint that restricts the value of a stepfield to be in a specfied selection.
 * <p>
 * This corresponds to the use of "in (select ..)" in SQL SELECT-syntax.
 * </p>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public interface FieldValueInQueryConstraint extends FieldConstraint {

    SearchQuery getInQuery();

    /**
     * Returns a string representation of this FieldValueInConstraint.
     * The string representation has the form
     * "FieldValueInConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;,
     *  casesensitive:&lt;casesensitive&gt;, values:&lt;values&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by
     *     <code>FieldConstraint#getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;values&gt;</em> is the values returned by
     *     {@link #getInQuery toString()}
     * </ul>
     *
     * @return A string representation of this FieldValueInConstraint.
     */
    public String toString();

}
