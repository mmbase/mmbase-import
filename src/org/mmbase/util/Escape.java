/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Class for escaping single quotes in a string, so that they can be safely
 * included in a SQL statement.
 *
 * @deprecated Use org.mmbase.util.Encode
 * @version $Id$
 */
public class Escape {

    /**
     * Escapes single quotes in a string.
     * Escaping is done by doubling any quotes encountered.
     * Strings that are rendered in such way can more easily be included
     * in a SQL query.
     * @param in the string to escape
     * @return the escaped string
     * @duplicate use {@link Encode} Encode encoder = new Encode("ESCAPE_SINGLE_QUOTE");<BR>encoder.encode("MMBase it's escaping quotes");
     */
    static public String singlequote(String in) {
        if (in == null || in.indexOf('\'') == -1) return in;

        StringBuffer sb = new StringBuffer();
        char[] data = in.toCharArray();
        for (char element : data) {
            if (element == '\'') sb.append("''");
            else sb.append(element);
        }
        return sb.toString();
    }
}
