/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote;

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
 * @version 12 Mar 1997
 */
public class Strip {

    /**
     * Strip nothing, a rather ineffecient form of a copy
     */
    public static final int NOTHING=0;

    /**
     * Strip leading, only characters at begin of string are checked
     */
    public static final int LEADING=1;

    /**
     * Strip trailing, only characters at end of string are checked
     */
    public static final int TRAILING=2;

    /**
     * Strip both, characters at begin and end of string are checked
     */
    public static final int BOTH=3;

    /**
     * Strip double quotes from beginning, end or both, only once.
     */
    public static String DoubleQuote(String str,int where) {
        return(Char(str,'"',where));
    }

    /**
     * Strip single quotes from beginning, end or both, only once.
     */
    public static String SingleQuote(String str,int where) {
        return(Char(str,'\'',where));
    }

    /**
     * Strip multiple whitespace characters from beginning, end or both, that
     * means keep on stripping util a non-whitespace character is found.
     */
    public static String Whitespace(String str,int where) {
        return(Chars(str," \t\n\r",where));
    }
    
    /**
     * Strip one character from beginning, end or both.
     */
    public static String Char(String str,char chr,int where) {
        if (str!=null && str.length()>0) {
            int lead=0;
            int trail=str.length()-1;

            switch(where) {
                case LEADING:
                    if (str.charAt(lead)==chr) lead++;
                    break;
                case TRAILING:
                    if (str.charAt(trail)==chr) trail--;
                    break;
                case BOTH:
                    if (str.charAt(lead)==chr) lead++;
                    if (str.charAt(trail)==chr) trail--;
                    break;
                default:
                    break;
            }
            str=str.substring(lead,trail+1);
        }
        return(str);
    }

    /**
     * Strip multiple characters contained in the set given as second parameter
     * until a non-set character. 
     */
    public static String Chars(String str,String chars,int where) {

        if (str!=null && str.length()>0) {
            int lead=0;
            int trail=str.length()-1;

            if (trail<1) {
                where=LEADING;
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
            if (lead<=trail) {
                str=str.substring(lead,trail+1);
            } else {
                str="";
            }
        }
        return(str);
    }

    /**
     * Test the class
     */
    public static void main(String args[]) {
        System.out.println("Double "+Strip.DoubleQuote("\"double\"",Strip.BOTH));
        System.out.println("Single "+Strip.SingleQuote("'single'",Strip.BOTH));
        System.out.println("White |"+Strip.Whitespace("   white         \n",Strip.BOTH)+"|");

    }
}
