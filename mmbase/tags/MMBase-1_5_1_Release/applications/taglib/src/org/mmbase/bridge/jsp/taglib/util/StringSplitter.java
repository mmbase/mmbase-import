/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * Simple util methods to split strings.
 *
 **/

public class StringSplitter {

    /**
     * Simple util method to split comma separated values
     * to a vector. Useful for attributes.
     * @param string the string to split
     * @param delimiter
     * @return a Vector containing the elements, the elements are also trimed     
     */

    static public Vector split(String attribute, String delimiter) {
        Vector retval = new Vector();
        StringTokenizer st = new StringTokenizer(attribute, delimiter);
        while(st.hasMoreTokens()){
            retval.addElement(st.nextToken().trim());
        }
        return retval;
    }

    static public Vector split(String string) {
        return split(string, ",");
    }

}
