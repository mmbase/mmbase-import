/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Class to compare two objects, used by SortedVector.
 * This one is to sort objects supporting the Sortable interface
 * @see org.mmbase.util.Sortable
 * @see org.mmbase.util.SortedVector
 * @see org.mmbase.util.CompareInterface
 *
 * @deprecated Use java.util.Comparator
 * @author Rico Jansen
 * @version $Id$
 */
public class SortableCompare implements CompareInterface {

    /**
     * Make the comparison.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Comparable</code>.
     * @param other the second object to compare. should be a <code>Comparable</code>.
     * @return the result of the comparison
     */
    public int compare(Object thisOne,Object other) {
        return ((Sortable)thisOne).compare((Sortable)other);
    }
}
