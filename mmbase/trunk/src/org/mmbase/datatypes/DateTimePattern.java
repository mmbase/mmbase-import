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
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * This is a bit like SimpleDateFormat, because it accepts the same pattern String.  It can also
 * parse the String though (see {@link #getList}), which can be used to do something else
 * for parsing or formatting (think: format an editor entry).
 *
 * This utility class is of course used in the implementation of {@link DateTimeDataType}.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: DateTimePattern.java,v 1.7 2006-01-06 17:19:21 michiel Exp $
 */

public class DateTimePattern implements Cloneable, java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(DateTimePattern.class);

    public static final DateTimePattern DEFAULT = new DateTimePattern("yyyy-MM-dd HH:mm:ss");

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

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
     * Returns a DateFormat object associated with this object. 
     */
    public DateFormat getDateFormat(Locale locale) {
        if (locale == null) locale = LocalizedString.getDefault();
        return new SimpleDateFormat(pattern.get(locale), locale);        
    }


    /**
     * Returns the original pattern, which can e.g. be used to instantiate a SimpleDateFormat (but this is also done for you in {@link #getDateFormat}.
     */
    public LocalizedString getPattern() {
        return pattern;
    }

    private static final char DONTAPPEND = (char) -1;
    private List parse(String p) {
        List parsed = new ArrayList();
        StringBuffer buf = new StringBuffer();
        boolean inString = false;
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
                    if (i != 0) {
                        parsed.add(buf.toString());
                        buf.setLength(0);
                    }
                    buf.append("\'");
                    inQuote = false;
                    inString = true;
                }
                if (inString) {
                    if (c == '\'') {
                        if (inQuote && i > 0 && p.charAt(i -1) == '\'') {
                            // enabling escape of '.
                        } else {
                            c = DONTAPPEND;
                        }
                        inQuote = ! inQuote;

                    }
                }

            }
            if (c != DONTAPPEND) {
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
     * introduced by a quote, then it is a literal string, otherwise it is a format-string,
     * consisting only of a number of the same letters (e.g. yyy). So by checking the first
     * character you can decide what to do with it. If for example you are making an editor, and
     * the first char is an quote, you may decide to do either nothing, or to write it out (without
     * the quote). If the first character is e.g. 'y' you can make an input box for the year (you
     * could also attribute some meaning to the length of the string then).
     *
     */
    public List getList(Locale locale) {
        String p = pattern.get(locale);
        return parse(p);
    }

    private static final Element HOUR_OF_DAY   = new Element("hour", Calendar.HOUR_OF_DAY, 0, 23);
    private static final Element HOUR          = new Element("hourinampm", Calendar.HOUR, 0, 11);
    private static final Element HOUR_OF_DAY_1 = new Element("hour", Calendar.HOUR_OF_DAY, 1, 24, 1);
    private static final Element HOUR_1        = new Element("hour", Calendar.HOUR, 1, 12, 1);
    private static final Element MINUTE        = new Element("minute", Calendar.MINUTE, 0, 59);
    private static final Element SECOND        = new Element("second", Calendar.SECOND, 0, 59);
    private static final Element DAY_OF_WEEK   = new Element("dayofweek", Calendar.DAY_OF_WEEK, 1, 7) {
            public String toString(int value, Locale locale, int length) {
                SimpleDateFormat format = new SimpleDateFormat("EEEEEEEEEEEEE".substring(0, length), locale);
                Calendar help = Calendar.getInstance();
                help.set(field, value);
                return format.format(help.getTime());
            }
        };
    private static final Element WEEK_OF_YEAR  = new Element("weekinyear", Calendar.WEEK_OF_YEAR, 1, 53);
    private static final Element DAY_OF_YEAR   = new Element("dayinyear", Calendar.DAY_OF_YEAR, 1, 366);
    private static final Element DAY_OF_WEEK_IN_MONTH  = new Element("dayofweekinmonth", Calendar.DAY_OF_WEEK_IN_MONTH, 1, 5) {
            public String toString(int value, Locale locale, int length) {
                if (length > 0) {
                    ResourceBundle bundle = ResourceBundle.getBundle("org.mmbase.datatypes.resources.ordinals", locale);
                    return bundle.getString("" + value);
                } else {
                    return super.toString(value, locale, length);
                }
            }
        };
    private static final Element ERA           = new Element("era", Calendar.ERA, 0, //BC
                                                             1 //AD
                                                             ) {
            public String toString(int value, Locale locale, int length) {
                SimpleDateFormat format = new SimpleDateFormat("GGGGGGGGGGGG".substring(0, length), locale);
                Calendar help = Calendar.getInstance();
                help.set(field, value);
                return format.format(help.getTime());
            }
            
        };
    private static final Element AM_PM         = new Element("am_pm", Calendar.AM_PM, 0, //AM
                                                             1 //PM
                                                             ) {
            public String toString(int value, Locale locale, int length) {
                SimpleDateFormat format = new SimpleDateFormat("aaaaaaaaaaaaaa".substring(0, length), locale);
                Calendar help = Calendar.getInstance();
                help.set(field, value);
                return format.format(help.getTime());
            }
        };
    private static final Element MILLISECOND   = new Element("millisecond", Calendar.MILLISECOND, 0, 9999);

    public static Element getElement(char c, Calendar minDate, Calendar maxDate) {
        switch(c) {
        case 'y': {
            int startYear = minDate.get(Calendar.YEAR);
            int   endYear = maxDate.get(Calendar.YEAR);
            return new Element("year",  Calendar.YEAR,  startYear, endYear) {
                    public int getNullValue() {
                        return Integer.MAX_VALUE;
                    }
                };
        }
        case 'M': {
            int startYear = minDate.get(Calendar.YEAR);
            int   endYear = maxDate.get(Calendar.YEAR);
            int min, max;
            if (startYear == endYear) {
                min = minDate.get(Calendar.MONTH) + 1;
                max = maxDate.get(Calendar.MONTH) + 1;
            } else {
                min = 1;
                max = 12;
            }
            return new Element("month", Calendar.MONTH, min, max, 1) {
                    public String toString(int value, Locale locale, int length) {
                        SimpleDateFormat format = new SimpleDateFormat("MMMMMMMMMMMMMMMMMM".substring(0, length), locale);
                        Calendar help = Calendar.getInstance();
                        help.set(field, value);
                        return format.format(help.getTime());
                    }
                };
        }
        case 'd': {
            int startYear = minDate.get(Calendar.YEAR);
            int   endYear = maxDate.get(Calendar.YEAR);
            int min, max;
            if (startYear == endYear) {
                int minMonth = minDate.get(Calendar.MONTH) + 1;
                int maxMonth = maxDate.get(Calendar.MONTH) + 1;
                if (minMonth == maxMonth) {
                    min = minDate.get(Calendar.DAY_OF_MONTH);
                    max = maxDate.get(Calendar.DAY_OF_MONTH);
                } else {
                    min = 1;
                    max = 31;
                }
            } else {
                min = 1;
                max = 31;
            }
            return new Element("day", Calendar.DAY_OF_MONTH, min, max);            
        }
        case 'H': return HOUR_OF_DAY;
        case 'K': return HOUR;
        case 'h': return HOUR_OF_DAY_1;
        case 'k': return HOUR_1;
        case 'm': return MINUTE;
        case 's': return SECOND;
        case 'E': return DAY_OF_WEEK;
        case 'w': return WEEK_OF_YEAR;
        case 'D': return DAY_OF_YEAR;
        case 'F': return DAY_OF_WEEK_IN_MONTH;
        case 'G': return ERA;
        case 'a': return AM_PM;
        case 'S': return MILLISECOND;
        default: 
            log.warn("Unknown pattern " + c);
            return null;
        }
    }

    public static class Element {
        private final String name;
        final int field;
        private final int min;
        private final int max;
        private final int offset;
        Element(String n, int field, int min, int max) {
            this(n, field, min, max, 0);
        }
        Element(String n, int field, int min, int max, int offset) {
            name = n;
            this.field = field;
            this.min = min;
            this.max = max;
            this.offset = offset;
        }
        public final String getName() {
            return name;
        }
        public final int getField() {
            return field;
        }
        public final int getMin() {
            return min;
        }
        public final int getMax() {
            return max;
        }
        public final int getOffset() {
            return offset;
        }
        public String toString(int value, Locale locale, int length) {
            StringBuffer buf = new StringBuffer("" + value);
            while(buf.length() < length) {
                buf.insert(0, "0");
            }
            return buf.toString();
        }
        /**
         * The int-value representing <code>null</code>. Some otherwise impossible value for the field.
         */
        public int getNullValue() {
            return -1;
        }
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
