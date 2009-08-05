/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Class to compare two strings, used by SortedVector.
 * @see org.mmbase.util.SortedVector
 * @see org.mmbase.util.CompareInterface
 * @deprecated Should implement java.util.Comparator, or should not exist, because this is java.lang.Integer's 'natural' order.
 *
 * @author Rico Jansen
 * @version $Id: IntegerCompare.java,v 1.1 2007-06-03 12:21:45 nklasens Exp $
 */
public class IntegerCompare implements CompareInterface {

    /**
     * Make the comparison.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Integer</code>.
     * @param other the second object to compare. should be a <code>Integer</code>.
     * @return the result of the comparison
     */
    public int compare(Object thisOne, Object other) {
        return ((Integer)thisOne).intValue() - ((Integer)other).intValue();
    }
}
