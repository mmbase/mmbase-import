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
 * Utilities for date-parsing. More complicated then by java.text.* classes. Most noticable is the
 * support for <em>relative</em> dates, like for example 'next week', which is 7 days later relative to the 
 * time 'now'.
 *
 * Parsing is not done relative to a Locale, it only parses english.
 * 
 * The idea is that this is used in configuration files (e.g. to specify default values of datetime
 * fields), or e.g. when a hard time is to be specified in a jsp (and taglib).
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DateParser {

    private static final int DAY = 1000 * 60 * 60 * 24;


    /**
     * Fast way to find the day number of a day
     */
    static  Map days = new HashMap();
    /**
     * Fast way to find the month number of a month
     */
    static private Map months = new HashMap();

    static {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String[] dayarray = dfs.getWeekdays();

        for (int i = 0; i < dayarray.length; i++) {
            final int nextday = i;
            days.put(dayarray[i].toLowerCase(), new Integer(i));
        }
        
        String[] montharray = dfs.getMonths();

        for (int i = 0; i < montharray.length; i++) {
            months.put(montharray[i].toLowerCase(), new Integer(i));
        }
    }

    /**
     * A Date formatter that creates a date based on a ISO 8601 date and a ISO 8601 time.
     * I.e. 2004-12-01 14:30:00.
     * It is NOT 100% ISO 8601, as opposed to {@link #ISO_8601_UTC}, as the standard actually requires
     * a 'T' to be placed between the date and the time.
     * The date given is the date for the local (server) time. Use this formatter if you want to display
     * user-friendly dates in local time.
     */
    public final static DateFormat ISO_8601_LOOSE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * A Date formatter that creates a ISO 8601 datetime according to UTC/GMT.
     * I.e. 2004-12-01T14:30:00Z.
     * This is 100% ISO 8601, as opposed to {@link #ISO_8601_LOOSE}.
     * Use this formatter if you want to export dates.
     */
    public final static DateFormat ISO_8601_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    {
        ISO_8601_UTC.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    public final static DateFormat ISO_8601_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat ISO_8601_TIME = new SimpleDateFormat("HH:mm:ss");



    /**
     * Finds the start of the day.
     * @param date A date.
     * @return The beginning of the day of the given Date
     */
    static Date getBeginOfDay(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return getBeginOfDay(cal);
    }
    static Date getBeginOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    static Date getDay(final Date relative, final int day) {
        // Find out which day it is.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(relative);
        int today = calendar.get(Calendar.DAY_OF_WEEK);                       
        // Calc how many days futher
        int diff = (7 + day - today) % 7;
        // Go to the correct day
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return getBeginOfDay(calendar);
    }



    public static Date getInstance(String format) {
        int pos = format.indexOf(" relative to ");
        if (pos > 0) {
            String f = format.substring(0, pos);
            Date rest = getInstance(format.substring(pos + 13));
            return getInstance(f, rest);
        }
        try {
            return  ISO_8601_UTC.parse(format);
        } catch (ParseException pe) {}        
        try {
            return ISO_8601_LOOSE.parse(format);
        } catch (ParseException pe2) {}        
        try {
            return  ISO_8601_DATE.parse(format);
        } catch (ParseException pe3) {}
        try {
            return  ISO_8601_TIME.parse(format);
        } catch (ParseException pe4) {}
        try {
            return DynamicDate.getInstance(format);
        } catch (IllegalArgumentException iae) {
            return new Date(-1);
        }
    }

    public static Date getInstance(final String format, final Date relative) {
        if (relative == null) return getInstance(format);
        if (format.equals(DynamicDate.NOW.date)) {
            return relative;
        } else if (format.equals(DynamicDate.TOMORROW.date)) {
            if (relative instanceof DynamicDate) {
                return DynamicDate.getInstance(relative, DAY);
            } else {
                return new Date(relative.getTime() + DAY);
            }
        } else if (format.equals(DynamicDate.YESTERDAY.date)) {
            if (relative instanceof DynamicDate) {
                return DynamicDate.getInstance(relative, -1 * DAY);
            } else {
                return new Date(relative.getTime() - DAY);
            }
        } else if (days.containsKey(format)) {
            int day = ((Integer) days.get(format)).intValue();
            if (relative instanceof DynamicDate) {
                return DynamicDate.getDay(relative, day);
            } else {
                return getDay(relative, day);
            }
        }
        return null;
    }


    /** 
     * Just for testing.
     */
    public static void main(String argv[]) {
        System.out.println("" + getInstance("now"));
        System.out.println("" + getInstance("tomorrow"));
        System.out.println("" + getInstance("tomorrow relative to today"));
        System.out.println("" + getInstance("tomorrow relative to now"));
        System.out.println("" + getInstance("yesterday relative to yesterday"));
        System.out.println("" + getInstance("tomorrow relative to 2005-08-31"));
        System.out.println("" + getInstance("friday"));
        System.out.println("" + getInstance("friday relative to 2005-08-31"));
    }


}
