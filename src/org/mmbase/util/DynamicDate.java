/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;
import java.text.*;

/**
 * A DynamicDate is a Date object that has no fixed value, like 'now'. It is unmodifiable, so all
 * set-methods throw exceptions. There is no public constructor, but a public static {@link #getInstance}.
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.8
 */
public abstract class DynamicDate extends Date {

    private static final int DAY = 1000 * 60 * 60 * 24;


    public static final DynamicDate NOW = new DynamicDate("now") {
            protected Date evalDate() {
                return new Date();
            }
        };
    public static final DynamicDate TOMORROW = new DynamicDate("tomorrow") {
            protected Date evalDate() {
                return DateParser.getBeginOfDay(System.currentTimeMillis() + DAY); 
            }
        };
    public static final DynamicDate YESTERDAY = new DynamicDate("yesterday") {
            protected Date evalDate() {
                return DateParser.getBeginOfDay(System.currentTimeMillis() - DAY);
            }
        };
    public static final DynamicDate TODAY = new DynamicDate("today") {
            protected Date evalDate() {
                return DateParser.getBeginOfDay(System.currentTimeMillis());
            }
        };

    

    /**
     * Parses a format string and returns a DynamicDate instance. Not necessary a new one, which
     * does not matter, because these objects are unmodifiable anyway.
     */
    static DynamicDate getInstance(final String format) {
        if (format.equals(NOW.date)) {
            return NOW;
        } else if (format.equals(TOMORROW.date)) {
            return TOMORROW;
        } else if (format.equals(YESTERDAY.date)) {
            return YESTERDAY;
        } else if (format.equals(TODAY.date)) {
            return TODAY;
        } 
        if (DateParser.days.containsKey(format)) {
            int day = ((Integer) DateParser.days.get(format)).intValue();
            return getDay(new Date(), day);
        } 
        return null;
    }

    static DynamicDate getInstance(final Date d, final long offset) {
        return new DynamicDate(d.toString() + " + " + offset) {
                public Date evalDate() {
                    return new Date(d.getTime() + offset);
                }
            };
    }

    static DynamicDate getDay(final Date relative, final int day) {
        return new DynamicDate("day" + day) {
                protected Date evalDate() {
                    return DateParser.getDay(relative, day);
                }
            };
    }


    /**
     * The original string by which this instance was gotten.
     */
    protected final String date;
    
    protected DynamicDate(String d) {
        date = d;
    }

    /**
     * This produces a normal Date object, and is called everytime when that is needed. Users can call it too, if they want to fixated 
     */
    protected abstract Date evalDate();


    // all methods of Date itself are simply wrapped..

    public boolean after(Date when) {
        return evalDate().after(when);
    }

    public boolean  before(Date when) {
        return evalDate().before(when);
    }
    
    public Object clone() {
        return getInstance(date);
    }
    public int  compareTo(Date anotherDate) {
        return evalDate().compareTo(anotherDate);
    }
    public int  compareTo(Object o) {
        return evalDate().compareTo(o);
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



}



