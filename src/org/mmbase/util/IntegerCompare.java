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
 * @version $Id: IntegerCompare.java,v 1.2 2001-04-12 15:00:31 pierre Exp $
 */
public class IntegerCompare implements CompareInterface {

    /**
     * The compare function called by SortedVector to sort things
     * @see org.mmbase.util.SortedVector
     * @see org.mmbase.util.CompareInterface
     */
    public int compare(Object thisone,Object other) {
        return ((Integer)thisone).intValue()-((Integer)other).intValue();
    }
}
