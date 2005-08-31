/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;

/**
 * A DynamicDate is a Date object that has no fixed value, like 'now'.
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.8
 */
public abstract class DynamicDate extends Date {

    public static DynamicDate getInstance(final String format) {
        if (format.equals("now")) {
            return new DynamicDate(format) {
                    protected Date evalDate() {
                        return new Date();
                    }
                };
        } else if (format.equals("tomorrow")) {
            return new DynamicDate(format) {
                    protected Date evalDate() {
                        return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);                        
                    }
                };
        } else {
            throw new IllegalArgumentException("" + format + " could not be parsed");
        }

    }

    private String date;
    
    protected DynamicDate(String d) {
        date = d;
    }

    protected abstract Date evalDate();


    public boolean after(Date when) {
        return evalDate().after(when);
    }

    public boolean 	before(Date when) {
        return evalDate().before(when);
    }
    
    public Object clone() {
        return getInstance(date);
    }
    public int 	compareTo(Date anotherDate) {
        return evalDate().compareTo(anotherDate);
    }
    public int 	compareTo(Object o) {
        return evalDate().compareTo(o);
    }
    public boolean 	equals(Object obj) {
        return false;
    }
    
    public int 	getDate() {
        return evalDate().getDate();
    }
    public int 	getDay() {
        return evalDate().getDay();
    }
    public int getHours() {
        return evalDate().getHours();
    }
    public int getMinutes() {
        return evalDate().getMinutes();
    }
    public int 	getMonth() {
        return evalDate().getMonth();
    }
    
    public int 	getSeconds() {
        return evalDate().getSeconds();
    }
    public long 	getTime() {
        return evalDate().getTime();
    }
    public int 	getTimezoneOffset() {
        return evalDate().getTimezoneOffset();
    }
    public int 	getYear() {
        return evalDate().getYear();
    }
    public  int 	hashCode() {
        return date.hashCode();
    }
    public void 	setDate(int date) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void 	setHours(int hours) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void 	setMinutes(int minutes) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void 	setMonth(int month) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    
    public void 	setSeconds(int seconds) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public  void 	setTime(long time) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public void 	setYear(int year) {
        throw new UnsupportedOperationException("Cannot set date in dynamic date");
    }
    public String 	toGMTString() {
        return evalDate().toGMTString();
    }
    public String 	toLocaleString() {
        return evalDate().toLocaleString();
    }

    public String 	toString() {
        return date + ":" + evalDate().toString();
    }


    /** 
     * Just for testing.
     */
    public static void main(String argv[]) {
        Date now      = getInstance("now");
        Date tomorrow = getInstance("tomorrow");
        System.out.println("" + now);
        System.out.println("" + tomorrow);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {}
        System.out.println("" + now);
        System.out.println("" + tomorrow);        
    }

}



