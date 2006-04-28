/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * RowVectorCompare compares a given row element in two Vectors.
 */
public class RowVectorCompare implements CompareInterface {
    int comparePos=1;

    /**
     * Creates a RowVectorCompare.
     * @param pos the position (row number) at which to compare two vectors
     */
    public RowVectorCompare(int pos) {
        comparePos = pos;
    }

    /**
     * Make the comparison.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Vector</code>.
     * @param other the second object to compare. should be a <code>Vector</code>.
     * @return the result of the comparison
     */
    public int compare(Object thisOne, Object other) {
        Object object1;
        Object object2;
        int result = 0;

        object1 = ((Vector)thisOne).elementAt(comparePos);
        object2 = ((Vector)other).elementAt(comparePos);

        if(object1 instanceof String)
            result = internalStringCompare(object1, object2);
        else if(object1 instanceof Integer)
            result = internalIntCompare(object1, object2);
        return result;
    }


    /**
     * Make the comparison between two Integer objects.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Integer</code>.
     * @param other the second object to compare. should be a <code>Integer</code>.
     * @return the result of the comparison
     */
    int internalIntCompare(Object thisOne, Object other) {
        return ((Integer)thisOne).intValue()-((Integer)other).intValue();
    }

    /**
     * Make the comparison between two String objects.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>String</code>.
     * @param other the second object to compare. should be a <code>String</code>.
     * @return the result of the comparison
     */
    int internalStringCompare(Object thisOne, Object other) {
        return ((String)thisOne).compareTo((String)other);
    }
}
