/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * MultiColCompare compares two Vectors on a given list of column numbers
 * @application SCAN
 * @author vpro
 * @version $Id: MultiColCompare.java,v 1.1 2007-06-03 12:21:45 nklasens Exp $
 */
public class MultiColCompare implements CompareInterface {

    /**
     * Array with column numbers to compare
     */
    int[] comparePosArray;

    /**
     * Creates a MultiColCompare.
     * @param posArray a list of column numbers
     */
    public MultiColCompare(int[] posArray) {
        comparePosArray = posArray;
    }

    /**
     * Constructor for comparing using multiple columns, where column position order is significant.
     * First compare is done using first colposnr, if same then 2nd colposnr is compared etc...
     * Column position delimiter character is ':'.
     * @param posList - a String with colpositions syntax "x:y:..." eg. "6:4".
     */
    public MultiColCompare(String posList) {
        int count = 1;
        for (int i=0; i<posList.length(); i++)
            if (posList.charAt(i)==':') count++;
        comparePosArray = new int[count];
        String s;
        for (int i=0; i<count; i++) {
            int pos = posList.indexOf(':');
            if (pos==-1) s=posList;
            else {
                s=posList.substring(0,pos);
                if (pos<posList.length()-1) posList = posList.substring(pos+1);
                else posList = "";
            }
            comparePosArray[i] = Integer.parseInt(s);
        }
    }

    /**
     * Make the comparison between to Vectors over all columns specified in the
     * postiton array, starting from a specified index in that array.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Vector</code>.
     * @param other the second object to compare. should be a <code>Vector</code>.
     * @param comparePosIndex the index in the positionarray where to start comparing
     * @return the result of the comparison
     */
    public int compareCol(Object thisOne, Object other, int comparePosIndex) {
        // log.debug("compareCol: thisOne:"+thisOne+", other:"+other+", compPosIndex:"+comparePosIndex);
        Object object1;
        Object object2;
        int result = 0;
        int comparePos = comparePosArray[comparePosIndex];

        object1 = ((Vector)thisOne).elementAt(comparePos);
        object2 = ((Vector)other).elementAt(comparePos);

        if(object1 instanceof String)
            result = internalStringCompare(object1, object2);
        else if(object1 instanceof Integer)
            result = internalIntCompare(object1, object2);

        if (result==0) {
            comparePosIndex++;
            if (comparePosIndex<comparePosArray.length)
                result = compareCol(thisOne, other, comparePosIndex);
        }
        return result;
    }

    /**
     * Make the comparison between to Vectors over all colums specified in the
     * postiton array.
     * The result is a negative value if the first object is 'smaller' than the second,
     * a positive value if it is 'larger', and 0 if both objects are 'equal'.
     * @param thisOne the first object to compare. should be a <code>Vector</code>.
     * @param other the second object to compare. should be a <code>Vector</code>.
     * @return the result of the comparison
     */
    public int compare(Object thisOne, Object other) {
        return compareCol(thisOne, other, 0);
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
