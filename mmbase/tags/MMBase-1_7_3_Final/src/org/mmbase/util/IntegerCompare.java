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
 *
 * @author Rico Jansen
 * @version $Id: IntegerCompare.java,v 1.3 2001-04-13 15:13:17 pierre Exp $
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
    public int compare(Object thisone,Object other) {
        return ((Integer)thisone).intValue()-((Integer)other).intValue();
    }
}
