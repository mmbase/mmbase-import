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
 * fields, of the Date object so not everything could be overridden perfectly. So, if e.g. a dynamic
 * date could be an argument of a 'after' or 'before' method, it is better to wrap it with {@link
 * DynamicDate#eval} first.
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DynamicDate extends Date {


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
     * This produces a normal Date object, and is called everytime when that is needed. Users can call it too, if they want to fixated
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

    public boolean after(Date when) {
        return evalDate().after(when);
    }

    public boolean  before(Date when) {
        return evalDate().before(when);
    }

    public Object clone() {
        try {
            return getInstance(date);
        } catch (org.mmbase.util.dateparser.ParseException pe) {
            return new Date();
        }
    }
    public int  compareTo(Date anotherDate) {
        return evalDate().compareTo(anotherDate);
    }

    public boolean  equals(Object obj) {
        if (obj instanceof DynamicDate) {
            return date.equals(((DynamicDate)obj).date);
        } else {
            return false;
        }
    }
    public int  getDate() {
        return evalDate().getDate();
    }
    public int  getDay() {
        return evalDate().getDay();
    }
    public int getHours() {
        return evalDate().getHours();
    }
    public int getMinutes() {
        return evalDate().getMinutes();
    }
    public int  getMonth() {
        return evalDate().getMonth();
    }

    public int  getSeconds() {
        return evalDate().getSeconds();
    }
    public long  getTime() {
        return evalDate().getTime();
    }
    public int  getTimezoneOffset() {
        return evalDate().getTimezoneOffset();
    }
    public int  getYear() {
        return evalDate().getYear();
    }
    public  int  hashCode() {
        return date.hashCode();
    }
    public void  setDate(int date) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void  setHours(int hours) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void  setMinutes(int minutes) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void  setMonth(int month) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }

    public void  setSeconds(int seconds) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public  void  setTime(long time) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void  setYear(int year) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public String  toGMTString() {
        return evalDate().toGMTString();
    }
    public String  toLocaleString() {
        return evalDate().toLocaleString();
    }

    public String  toString() {
        return date + ": " + evalDate().toString();
    }

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
            "2009-W01-01", "2009-W53-7", "2006-123"
        };
    }


    public static void main(String argv[]) throws java.text.ParseException, ParseException {

        //System.out.println("" + Arrays.asList(TimeZone.getAvailableIDs()));
        //System.out.println(TimeZone.getDefault());
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("GGGG yyyy-MM-dd HH:mm:ss.SSS zzz E");
        if (argv.length == 0) {
            String[] demo = getDemo();
            for (int i = 0; i < demo.length; i++) {
                try {
                    Date d1 = getInstance(demo[i]);
                    System.out.print(formatter.format(d1) + "\t");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(demo[i]);

            }
            System.out.println("This was demo, you can also call with an argument, to try it yourself");
            System.out.println("Also try with different values for -Duser.timezone=");
        } else {
            Date d1 = getInstance(argv[0]);
            //Date d2 = Casting.ISO_8601_UTC.parse(argv[0]);
            //Date d3 = new Date(Long.MIN_VALUE);
            System.out.println(formatter.format(d1) + " " + d1.getTime());
            //System.out.println("" + d2 + " " + d2.getTime());
            //System.out.println("" + d3 + " " + d3.getTime());
        }
    }

}



