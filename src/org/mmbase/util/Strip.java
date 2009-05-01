/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class to strip characters from the beginning and end of strings.
 *
 * <PRE>
 * Example1: Strip.Char("..dfld..",'.',Strip.TRAILING) yields "..dlfd."
 * Example2: Strip.Chars("..dfld..",".",Strip.TRAILING) yields "..dlfd"
 * Example3: Strip.Chars(". .. dfld. , .","., ",Strip.BOTH) yields "dfld"
 * </PRE>
 *
 * @author Rico Jansen
 * @version $Id$
 */
public class Strip {

    private static final Logger log = Logging.getLoggerInstance(Strip.class);

    /**
     * Strip nothing, a rather ineffecient form of a copy
     */
    public static final int NOTHING = 0;

    /**
     * Strip leading, only characters at begin of string are checked
     */
    public static final int LEADING = 1;

    /**
     * Strip trailing, only characters at end of string are checked
     */
    public static final int TRAILING = 2;

    /**
     * Strip both, characters at begin and end of string are checked
     */
    public static final int BOTH = 3;

    /**
     * Strip double quotes from beginning, end or both, only once.
     * @param str the string to strip
     * @param where one of {@link #NOTHING}, {@link #LEADING}, {@link #TRAILING}
     * or {@link #BOTH}
     * @return the stripped String
     */
    public static String doubleQuote(String str,int where) {
        return character(str, '"', where);
    }

    /**
     * Strip single quotes from beginning, end or both, only once.
     * @param str the string to strip
     * @param where one of {@link #NOTHING}, {@link #LEADING}, {@link #TRAILING}
     * or {@link #BOTH}
     * @return the stripped String
     */
    public static String singleQuote(String str,int where) {
        return character(str, '\'', where);
    }

    /**
     * Strip multiple whitespace characters from beginning, end or both, that
     * means keep on stripping util a non-whitespace character is found.
     * @param str the string to strip
     * @param where one of {@link #NOTHING}, {@link #LEADING}, {@link #TRAILING}
     * or {@link #BOTH}
     * @return the stripped String
     */
    public static String whitespace(String str, int where) {
        return chars(str, " \t\n\r", where);
    }

    /**
     * Strip all of the specified character from beginning, end or both.
     * @param str the string to strip
     * @param chr the character to strip from the string
     * @param where one of {@link #NOTHING}, {@link #LEADING}, {@link #TRAILING}
     * or {@link #BOTH}
     * @return the stripped String
     */
    public static String character(String str, char chr, int where) {
        if (str != null && str.length() > 0) {
            int lead = 0;
            int trail = str.length() - 1;

            switch(where) {
            case LEADING:
                if (str.charAt(lead) == chr) lead++;
                break;
            case TRAILING:
                if (str.charAt(trail) == chr) trail--;
                break;
            case BOTH:
                if (str.charAt(lead) == chr) lead++;
                if (str.charAt(trail) == chr) trail--;
                break;
            default:
                break;
            }
            str = str.substring(lead, trail + 1);
        }
        return str;
    }

    /**
     * Strip multiple characters contained in the set given as second parameter
     * until a non-set character.
     * @param str the string to strip
     * @param chars a string containing all characters to strip from the string
     * @param where one of {@link #NOTHING}, {@link #LEADING}, {@link #TRAILING}
     * or {@link #BOTH}
     * @return the stripped String
     */
    public static String chars(String str, String chars, int where) {

        if (str != null && str.length() > 0) {
            int lead = 0;
            int trail = str.length() - 1;

            if (trail < 1) {
                where = LEADING;
            } else {
                switch(where) {
                case LEADING:
                    while(chars.indexOf(str.charAt(lead))!=-1 && (lead<str.length()-1)) lead++;
                    break;
                case TRAILING:
                    while(chars.lastIndexOf(str.charAt(trail))!=-1 && trail>0) trail--;
                    break;
                case BOTH:
                    while(chars.indexOf(str.charAt(lead))!=-1 && lead<(str.length()-1)) lead++;
                    while(chars.lastIndexOf(str.charAt(trail))!=-1 && trail>=lead) trail--;
                    break;
                default:
                    break;
                }
            }
            if (lead <= trail) {
                str = str.substring(lead, trail + 1);
            } else {
                str = "";
            }
        }
        return str;
    }

    /**
     * Test the class
     */
    public static void main(String args[]) {
        log.info("Double " + Strip.doubleQuote("\"double\"", Strip.BOTH));
        log.info("Single " + Strip.singleQuote("'single'", Strip.BOTH));
        log.info("White |" + Strip.whitespace("   white         \n", Strip.BOTH) + "|");
    }
}
