/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.io.File;

/**
 * Class to compare two Files on their modification time, used by SortedVector.
 * @see SortedVector
 * @see CompareInterface
 *
 * @author David V van Zeventer
 * @version 12 November 1998
 */
public class FileCompare implements CompareInterface {

    /**
     * The compare function called by SortedVector to sort things
     */
    public int compare(Object thisone,Object other) {
        return (int) (((File)thisone).lastModified()-((File)other).lastModified());
    }
}
