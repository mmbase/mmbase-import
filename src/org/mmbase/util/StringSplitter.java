/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for splitting delimited values.
 *
 * @deprecated better use String.split()
 * @author Pierre van Rooden
 * @author Kees Jongenburger
 * @version $Id: StringSplitter.java,v 1.4 2005-01-03 21:58:45 michiel Exp $
 */
public class StringSplitter {

    /**
     * Simple util method to split delimited values to a list. Useful for attributes.
     * Similar to <code>String.split()</code>, but returns a List instead of an array, and trims the values.
     * @param string the string to split
     * @param delimiter
     * @return a (modifiable) List containing the elements
     */
    static public List split(String attribute, String delimiter) {
        List result = new ArrayList();
        if (attribute == null) return result;
        String[] values = attribute.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].trim());
        }
        return result;
    }

    /**
     * Simple util method to split comma separated values.
     * @see #split(String, String)
     * @param string the string to split
     * @param delimiter
     * @return a List containing the elements
     */
    static public List split(String string) {
        return split(string, ",");
    }

}
