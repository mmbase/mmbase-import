/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for splitting delimited values.
 * @author Pierre van Rooden
 * @author Kees Jongenburger
 * @version $Id: StringSplitter.java,v 1.2 2004-02-18 12:29:26 pierre Exp $
 */
public class StringSplitter {

    /**
     * Simple util method to split delimited values to a list. Useful for attributes.
     * Similar to <code>String.split()</code>, but returns a List instead of an array, and trims the values.
     * @param string the string to split
     * @param delimiter
     * @return a List containing the elements
     */
    static public List split(String attribute, String delimiter) {
        if (attribute == null) return new ArrayList();
        String[] values = attribute.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
        return Arrays.asList(values);
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
