/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Interface for comparing objects so trhey can get sorted.
 * Used by SortedVector.
 * @see SortedVector
 * @deprecated This is the same as java.util.Comparator
 * @version $Id$
 */

public interface CompareInterface {
    /**
     * The compare function called by SortedVector to sort things
     */
    public abstract int compare(Object thisone,Object other);
}
