/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Simple util methods to split strings.
 *
 **/

public class StringSplitter {

    /**
     * Simple util method to split comma separated values
     * to a list. Useful for attributes.
     * @param string the string to split
     * @param delimiter
     * @return a List containing the elements, the elements are also trimed     
     */

    static public List split(String attribute, String delimiter) {
        List retval = new ArrayList();
        if (attribute == null) return retval;
        StringTokenizer st = new StringTokenizer(attribute, delimiter);
        while(st.hasMoreTokens()){
            retval.add(st.nextToken().trim());
        }
        return retval;
    }

    static public List split(String string) {
        return split(string, ",");
    }

}
