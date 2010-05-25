/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constaint on the search results.
 * <p>
 * This corresponds to constraints in a WHERE-clause in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface Constraint {
    /**
     * Tests if the condition must be inverted.
     * <p>
     * This corresponds to the use of NOT in a WHERE-clause in SQL SELECT-syntax.
     */
    boolean isInverse();

    /**
     * Tests if this constraint is supported by the basic queryhandler.
     */
    int getBasicSupportLevel();

    /**
     * Compares this constraint to the specified object. The result is
     * <code>true</code> if and only if the argument is a non-null
     * Constraint object representing the same constraint(s).
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal,
     * <code>false</code> otherwise.
     */
    @Override
    boolean equals(Object obj);

    // javadoc is inherited
    @Override
    int hashCode();


    /**
     * @since MMBase-1.9.2
     */
    void setUnmodifiable();

}
