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
 * @author Pierre van Rooden
 * @author Kees Jongenburger
 * @version $Id: StringSplitter.java,v 1.7 2006-06-26 18:15:22 johannes Exp $
 */
public class StringSplitter {

    /**
     * Simple util method to split delimited values to a list. Useful for attributes.
     * Similar to <code>String.split()</code>, but returns a List instead of an array, and trims the values.
     * @param string the string to split
     * @param delimiter
     * @return a (modifiable) List containing the elements
     */
    static public List split(String string, String delimiter) {
        List result = new ArrayList();
        if (string == null) return result;
        String[] values = string.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].trim());
        }
        return result;
    }


    /**
     * Simple util method to split comma separated values.
     * @see #split(String, String)
     * @param string the string to split
     * @return a List containing the elements
     */
    static public List split(String string) {
        return split(string, ",");
    }

    /**
     * Splits up a String, (using comma delimiter), but takes into account brackets. So
     * a(b,c,d),e,f(g) will be split up in a(b,c,d) and e and f(g).
     * @since MMBase-1.8
     */
    static public List splitFunctions(CharSequence attribute) {
        int commaPos =  0;
        int nested   =  0;
        List  result = new ArrayList();
        int i;
        int length   =  attribute.length();
        for(i = 0; i < length; i++) {
            char c = attribute.charAt(i);
            if ((c == ',') || (c == ';')){
                if(nested == 0) {
                    result.add(attribute.subSequence(commaPos, i).toString().trim());
                    commaPos = i + 1;
                }
            } else if (c == '(') {
                nested++;
            } else if (c == ')') {
                nested--;
            }
        }
        if (i > 0) {
            result.add(attribute.toString().substring(commaPos).trim());
        }
        return result;
    }
    

}
