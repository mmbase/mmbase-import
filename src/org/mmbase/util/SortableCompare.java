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
 * @author Rico Jansen
 * @version $Id: SortableCompare.java,v 1.5 2003-03-10 11:51:12 pierre Exp $
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
    public int compare(Object thisone,Object other) {
        return ((Sortable)thisone).compare((Sortable)other);
    }
}
