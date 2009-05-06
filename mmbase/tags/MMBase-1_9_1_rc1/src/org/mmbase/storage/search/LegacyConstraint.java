/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.storage.search;

/**
 * Constraint represented by a string, as it appears in the where-clause
 * of an SQL query.
 * <p>
 * <em>This constraint type is provided for the sole purpose of aligning
 * existing legacy code with the new search query framework, and will
 * eventually be phased out.</em>
 *
 * @author  Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface LegacyConstraint extends Constraint {

    /**
     * Gets the constraint.
     *
     * @return The constraint as it appears in the where-clause.
     */
    public String getConstraint();

    /**
     * Returns a string representation of this LegacyConstraint.
     * The string representation has the form
     * "LegacyConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;,
     *  constraint:&lt;constraint&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;constraint&gt;</em> is the value returned by
     *     {@link #getConstraint getConstraint()}
     * </ul>
     *
     * @return A string representation of this LegacyConstraint.
     */
    public String toString();
}
