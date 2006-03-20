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

        parser.start();
        if (parser.dynamic()) {
            return new DynamicDate(format);
        } else {
            return parser.toDate();
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

    public static void main(String argv[]) throws java.text.ParseException, ParseException {
        //System.out.println("" + Arrays.asList(TimeZone.getAvailableIDs()));
        //System.out.println(TimeZone.getDefault());
        Date d1 = getInstance(argv[0]);
        Date d2 = Casting.ISO_8601_UTC.parse(argv[0]);
        Date d3 = new Date(0);
        System.out.println("" + d1 + " " + d1.getTime());
        System.out.println("" + d2 + " " + d2.getTime());
        System.out.println("" + d3 + " " + d3.getTime());
    }

}



