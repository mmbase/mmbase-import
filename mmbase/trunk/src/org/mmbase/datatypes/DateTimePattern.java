/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.text.*;
import java.util.*;
import org.mmbase.util.LocalizedString;

/**
 * This is a bit like SimpleDateFormat, because it accepts the same pattern String.  It can also
 * parse the String though (see {@link #getList}), which can be used to so something elso then 
 * for parsing or formatting (think: format an editor entry).
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: DateTimePattern.java,v 1.1 2005-08-30 19:40:47 michiel Exp $
 */

public class DateTimePattern implements Cloneable, java.io.Serializable {


    public static final DateTimePattern DEFAULT = new DateTimePattern("yyyy-MM-dd HH:mm:ss");
    protected  LocalizedString pattern;
    
    public DateTimePattern(String pattern) {
        this.pattern = new LocalizedString(pattern);
    }

    public void set(String pattern, Locale locale) {
        this.pattern.set(pattern, locale);
    }
    public void set(String pattern) {
        this.pattern.setKey(pattern);
    }

    /**
     * Returns a DateFormat object associated with this object. The locale is always US, because
     * this object is needed for generic formation, which is probably not dependent on the Locale.
     */
    public DateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(pattern.get(locale), locale);
    }


    /**
     * Returns the original pattern, which can e.g. be used to instantiate a SimpleDateFormat (but this is also done for you in {@link #getDateFormat}.
     */
    public LocalizedString getPattern() {
        return pattern;
    }

    private List parse(String p) {
        List parsed = new ArrayList();
        StringBuffer buf = new StringBuffer();
        boolean inString = true;
        boolean inQuote = false;
        char    nonStringChar = (char) -1;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z')) { // reserved
                if (inString) {
                    if (! inQuote) {
                        if (buf.length() > 0) {
                            parsed.add(buf.toString());
                            buf.setLength(0);
                        }
                        inString = false;
                        nonStringChar = c;
                            }
                } else {
                    if (nonStringChar != c) {
                        parsed.add(buf.toString());
                        buf.setLength(0);
                        nonStringChar = c;
                    }
                }
            } else {
                if (! inString) {
                    parsed.add(buf.toString());
                    buf.setLength(0);
                    buf.append("\'");
                    inQuote = false;
                    inString = true;
                } 
                if (inString) {
                    if (c == '\'') {
                        if (inQuote && i > 0 && p.charAt(i -1) == '\'') {
                            // enabling to escape '.
                        } else {
                            c = (char) -1;
                        }
                        inQuote = ! inQuote;
                        
                    }
                }
                
            }
            if (c != (char) -1) {
                buf.append(c);
            }
        }
        if (inQuote) {
            throw new IllegalArgumentException("Unterminated quote");
        }
        if (buf.length() > 0) {
            parsed.add(buf.toString());
        }
        return parsed;
    }
    

    /**
     * Returns the pattern 'parsed'. This means that is is a List of Strings. If the string is
     * introduces by a quote, then it is a literal string, otherwise it is a format-string,
     * consisting only of a number of the same letters (e.g. yyy). So by checking the first
     * charachter you can decide what to do with it. If for exmaple you are making an editor, and
     * the first char is an quote, you may decite to do either nothing, or to write it out (without
     * the quote). If the first charachter is e.g. 'y' you can make an input box for the year (you
     * could also attribute some meaning to the length of the string then).
     * 
     */
    public List getList(Locale locale) {
        String p = pattern.get(locale);
        return parse(p);
    }


    public Object clone() {
        try {
            DateTimePattern clone =  (DateTimePattern) super.clone();
            clone.pattern = (LocalizedString) pattern.clone();
            return clone;
        } catch (CloneNotSupportedException cns) {
            // should not happen
            throw new RuntimeException(cns);
        }
    }

    public String toString() {
        return pattern.toString();
    }

    public static void main(String argv[]) {
        String input;
        if (argv.length > 0) {
            input = argv[0];
        } else {
            input = "yyyy-MM-dd";
        }
        DateTimePattern df = new DateTimePattern(input);
        df.set("yyyy-MM-dd", Locale.FRANCE);

        DateTimePattern df2 = (DateTimePattern) df.clone();
        df2.set("HH:mm:ss");
        df2.set("yyyy;MM;dd", Locale.FRANCE);

        System.out.println("" + df.getList(Locale.FRANCE));
        System.out.println("" + df2.getList(Locale.FRANCE));

    }

}
