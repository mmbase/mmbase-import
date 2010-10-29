/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.dateparser.*;
import java.util.*;

/**
 * A DynamicDate is a Date object that has no fixed value, like 'now'. It is unmodifiable, so all
 * set-methods throw exceptions. There is no public constructor, but a public static {@link #getInstance}.
 *
 * Sadly, the Date object of Sun is implemented using private static methods which use private
 * fields of the Date object, so not everything could be overridden perfectly. So, if e.g. a dynamic
 * date could be an argument of an 'after' or 'before' method, it is better to wrap it with {@link
 * DynamicDate#eval} first.
 *
 * Basicly the following dynamic dates are possible:
 * <ul>
 * <li>&lt;an integer number&gt;: seconds since january 1 1970</li>
 * <li>&lt;integer&gt;-&lt;integer&gt;: year-daynumber</li>
 * <li>&lt;integer&gt;-W&lt;integer&gt;: year-weeknumber</li>
 * <li>&lt;integer&gt;-&lt;integer&gt;-&lt;integer&gt;
 * [&lt;integer&gt;:&lt;:integer&gt;:&lt;:float&gt;: year-month-day [hour:minute:seconds]</li>
 * <li>today, yesterday, tomonth, etc. : many strings are recognized</li>
 * </ul>
 * The then found (absolute) date/time can then be offsetted with aditions like "- 1 minute", "+ 5
 * week". I.e. one of the binary operators '-' or '+' an integer and a time unit (in the
 * singular). This process can be repeated so things like 'now + 5 minute + 3 second' will be
 * correctly parsed.
 *
 * {@link #getDemo} returns a list of several strings which can be parsed.
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DynamicDate extends Date {

    private final static long serialVersionUID = 0L;
    /**
     * Parses a format string and returns Date instance, possibly a 'dynamic one'. Not necessary a new one, which
     * does not matter, because these objects are unmodifiable anyway.
     *
     * If the request date is not dynamic, but absolutely defined, a normal Date object is returned.
     */
    public static Date getInstance(final String format) throws ParseException {
        if (format.equals("null")) return null;
        DateParser parser = new DateParser(new java.io.StringReader(format));
        try {
            parser.start();
            if (parser.dynamic()) {
                return new DynamicDate(format);
            } else {
                return parser.toDate();
            }
        } catch (ParseException pe) {
            ParseException p = new ParseException("In " + format + " " + pe.getMessage());
            p.initCause(pe);
            throw p;
        }

    }

    /**
     * This calls {@link #getInstance}, then {@link #eval} and catches the parse-exception (in which
     * case it will return -1).
     * This boils down to that this is a utility method to get a new Date object by String in just
     * one call.
     *
     * @since MMBase-1.8.7
     */
    public static Date eval(final String format) {
        try {
            return eval(getInstance(format));
        } catch (ParseException e) {
            return new Date(-1);
        }
    }

    /**
     *  Makes sure the argument 'date' is no DynamicDate any more. So this returns a fixed date
     *  object when the argument is a DynamicDate and simply the argument if it is not.
     */
    public static Date eval(final Date date) {
        if (date instanceof DynamicDate) {
            return ((DynamicDate) date).evalDate();
        } else {
            return date;
        }
    }

    /**
     * The original string by which this instance was gotten.
     */
    protected final String date;

    protected DynamicDate(String d) {
        date = d;
    }

    public String getFormat() {
        return date;
    }

    /**
     * This produces a normal Date object, and is called everytime when that is needed. Users can
     * call it too, if they want to fixate that Date. You can also use {@link #eval(Date)}, which
     * will work on a normal Date object too.
     */
    protected  Date evalDate() {
        DateParser parser = new DateParser(new java.io.StringReader(date));
        try {
            parser.start();
            return parser.toDate();
        } catch (org.mmbase.util.dateparser.ParseException pe) {
            return new Date();
        }
    }


    // all methods of Date itself are simply wrapped..

    @Override
    public boolean after(Date when) {
        return evalDate().after(when);
    }

    @Override
    public boolean  before(Date when) {
        return evalDate().before(when);
    }

    @Override
    public Object clone() {
        try {
            return getInstance(date);
        } catch (org.mmbase.util.dateparser.ParseException pe) {
            return new Date();
        }
    }
    @Override
    public int  compareTo(Date anotherDate) {
        return evalDate().compareTo(anotherDate);
    }

    @Override
    public boolean  equals(Object obj) {
        if (obj instanceof DynamicDate) {
            return date.equals(((DynamicDate)obj).date);
        } else {
            return false;
        }
    }
    @Deprecated@Override
    public int getDate() {
        return evalDate().getDate();
    }
    @Deprecated@Override
    public int getDay() {
        return evalDate().getDay();
    }
    @Deprecated@Override
    public int getHours() {
        return evalDate().getHours();
    }
    @Deprecated@Override
    public int getMinutes() {
        return evalDate().getMinutes();
    }
    @Deprecated@Override
    public int getMonth() {
        return evalDate().getMonth();
    }

    @Deprecated@Override
    public int getSeconds() {
        return evalDate().getSeconds();
    }
    @Override
    public long  getTime() {
        return evalDate().getTime();
    }
    @Deprecated@Override
    public int getTimezoneOffset() {
        return evalDate().getTimezoneOffset();
    }
    @Deprecated@Override
    public int getYear() {
        return evalDate().getYear();
    }
    @Override
    public  int  hashCode() {
        return date.hashCode();
    }
    @Deprecated@Override
    public void setDate(int date) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public void setHours(int hours) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public void setMinutes(int minutes) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public void setMonth(int month) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }

    @Deprecated@Override
    public void setSeconds(int seconds) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public void setTime(long time) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public void setYear(int year) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    @Deprecated@Override
    public String toGMTString() {
        return evalDate().toGMTString();
    }
    @Deprecated@Override
    public String toLocaleString() {
        return evalDate().toLocaleString();
    }

    @Override
    public String  toString() {
        return date + ": " + evalDate().toString();
    }

    /**
     * Returns an arrays of example Strings that can be parsed by DynamicDate. Most features are
     * tested here.
     */
    public static String[] getDemo() {
        return new String[] {
            "0", "10000", "-10000", "+1000", // just numbers a bit after 1970, a bit before
            "1973-05-03", "2006-05-09", "-3-12-25", // absolute dates
            "2000-01-01 16:00", "TZUTC 2001-01-01 16:00","today 12:34:56.789",
            "now", "today", "tomorrow", "now + 10 minute", "today + 5 day",
            "now this year", "next august", "today + 6 month next august", "tomonth", "borreltijd", "today + 5 dayish", "yesteryear", "mondayish",
            "duration + 5 minute", "duration + 100 year",
            "TZUTC today noon", "TZEurope/Amsterdam today noon", "TZUTC today", "TZEurope/Amsterdam today",
            "TZ UTC today noon", "TZ Europe/Amsterdam today noon", "TZ UTC today", "TZ Europe/Amsterdam today",
            "TZ Europe/Amsterdam -1000",
            "today 6 oclock", "today 23 oclock", "today 43 oclock",
            "tosecond", "tominute", "tohour", "today", "previous monday", "tomonth", "toyear", "tocentury", "tocentury_pedantic", "toera", "toweek",
            "now this second", "now this minute", "now this hour", "now this day", "today previous monday", "now this month", "now this year", "now this century", "now this era",
            "now - 15 year this century", "now - 20 year this century_pedantic", "today + 2 century", "toera - 1 minute",
            "this july", "previous july", "next july", "this sunday", "previous sunday", "next sunday",
            "2009-W01-01", "2009-W53-7", "2006-123",
            "2005-01-01 this monday"
        };
    }


    public static void main(String argv[]) throws java.text.ParseException, ParseException {

        //System.out.println("" + Arrays.asList(TimeZone.getAvailableIDs()));
        //System.out.println(TimeZone.getDefault());
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("GGGG yyyy-MM-dd HH:mm:ss.SSS zzz E");
        if (argv.length == 0) {
            String[] demo = getDemo();
            for (String element : demo) {
                try {
                    Date d1 = getInstance(element);
                    System.out.print(formatter.format(d1) + "\t");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(element);

            }
            System.out.println("This was demo, you can also call with an argument, to try it yourself");
            System.out.println("Also try with different values for -Duser.timezone=");
        } else {
            Date d1 = getInstance(argv[0]);
            if (argv.length > 1) {
                java.text.DateFormat my = new java.text.SimpleDateFormat(argv[1]);
                System.out.println(my.format(d1));
            } else {
                System.out.println(formatter.format(d1) + " " + d1.getTime());
            }
            //Date d2 = Casting.ISO_8601_UTC.parse(argv[0]);
            //Date d3 = new Date(Long.MIN_VALUE);

            //System.out.println("" + d2 + " " + d2.getTime());
            //System.out.println("" + d3 + " " + d3.getTime());
        }
    }

}



