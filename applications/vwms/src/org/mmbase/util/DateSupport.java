/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import java.text.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some routines to support dates better<br /><br />
 *
 * The problem that generally occurs is with timezones. Therefore, we have made the following structure:<br />
 * <ul>
 * <li> If a date is stored in a database, it is in GMT
 * <li> If a date is displayed, it happens in the timezone of the machine that is calling.
 * </ul>
 * This means that some timezone conversions have to be made.
 * We assume nothing about timezones, we just read the value specified by the system (Timezone.getDefault() call).
 *
 * @deprecated use Calendar and java.util.DateFormat
 * @author Rico Jansen
 * @author Johannes Verelst
 * @version $Id$
 */
public class DateSupport {

    // logger
    private static Logger log = Logging.getLoggerInstance(DateSupport.class);

    /**
     * The offset for date conversions for the current zone, in seconds
     */
    static int offset = 0;
    /**
     * Whether to sue the offset.
     * Set to <code>true</code> when initialization of this class succeeds.
     */
    static boolean dooffset = true;

    /**
    * Return the number of days in the month in a specified year.
    * Leap years have to be taken into account
    *
    * @param year The year valid values 0..100 where 0 is y2k 2000  untill 89 => 2089 and 90 being the year 1990
    * @param month The month where 0 is januari
    */
    static public int daysInMonth(int year, int month) {
        int months[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int days = months[month];
        year = (year < 90) ? year + 2000 : year + 1900;

        // Make an exception for the intercalary day.
        if (month == 1) {
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
        }
        return days;
    }

    /**
     * Return the number of seconds that have elapsed from the beginning of the year to the given date
     *
     * @param d The date
     * @return The number of secods from January 1 to the given date
     * @see DateSupport#daysInMonth
     * @see DateSupport#dayInYear
     * @see DateSupport#weekInYear
     */
    static public int secondInYear(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.DAY_OF_YEAR) * (60 * 60 * 24) + c.get(Calendar.HOUR_OF_DAY) * (24 * 60) + c.get(Calendar.SECOND);
    }

    /**
     * Return the number of days that have elapsed from the beginning of the year to the given date
     *
     * @param d The date
     * @return The number of days from January 1 to the given date
     * @see DateSupport#daysInMonth
     * @see DateSupport#secondInYear
     * @see DateSupport#weekInYear
     */
    static public int dayInYear(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Return the number of weeks that have elapsed from the beginning of the year to the given date
     *
     * @param d The date
     * @return The number of weeks from January 1 to the given date
     * @see DateSupport#daysInMonth
     * @see DateSupport#secondInYear
     * @see DateSupport#dayInYear
     */
    static public int weekInYear(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Return the number milliseconds elapsed from 1-Jan-1970 to the beginning of the given week.
     *
     * @param year The year
     * @param week The number of the week
     * @return The number of milliseconds between 1-Jan-1970 and the begin of the given week.
     */
    static public long milliDate(int year, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 0);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        Date d = calendar.getTime();
        return d.getTime();
    }

    /**
     * Return a date, based on a year, a week and the day of that week  <br />
     * For instance: 1999, 40, 4 = The 4th day of the 40th week of 1999
     *
     * @param year The year
     * @param week The week
     * @param day The number of the day in the week
     * @return A date-object for the given date
     */
    static public Date Date(int year, int week, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, day);
        return cal.getTime();
    }

    /**
     * Create date strings in the form yyyy-mm-dd for a given Date object
     * <br />This format is used in several database (dbm's)
     * @param da The date input
     * @return A string in the form yyyy-mm-dd
     * @see DateSupport#parsedbmdate
     */
    public static String makedbmdate(Date da) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return f.format(da);
    }

    /**
     * Parse date strings in the form yyyy-mm-dd
     *  <br />This format is used in several database (dbm's)
     * @param wh The string representing the date in 'yyyy-mm-dd' format
     * @return A Date object for the given date
     * @see DateSupport#makedbmdate
     */
    public static Date parsedbmdate(String wh) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        //for bw comp
        if (wh.indexOf('/') != -1) {
            f = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        }
        try {
            return f.parse(wh);
        } catch (ParseException e) {
            //idem for bw comp
            return new Date();
        }
    }

    /**
     * Puts a colon between a time of RFC-1223 format
     *
     * @param time A string in RFC-1223 format
     * @return A string with an extra colon
     */
    public static String colontime(String time) {
        if (time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2, 4);
        }
        return time;
    }

    /**
     * Returns the number of seconds from 1-Jan-1970 to a given date
     *
     * @param sDate String in the form 'yyyyMMdd'
     * @return Number of seconds from 1-Jan-1970
     * @see DateSupport#parsetime
     * @see DateSupport#parsedatetime
     */
    public static int parsedate(String sDate) {
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        TimeZone tz;
        df.applyLocalizedPattern("yyyyMMdd");

        tz = TimeZone.getDefault();
        df.setTimeZone(tz);

        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (java.text.ParseException e) {
            log.error(e.toString());
        }

        if (date != null)
            return (int) ((date.getTime() - getMilliOffset()) / 1000);
        else
            return -1;
    }

    /**
     * Returns the number of seconds from 00:00:00 to a given time
     *
     * @param wh Time in the form 'hhmmss'
     * @return Number of seconds from 00:00:00 to the given time
     * @see DateSupport#parsedate
     * @see DateSupport#parsedatetime
     */
    public static int parsetime(String wh) {
        int h = 0, m = 0, s = 0;
        try {
            h = Integer.parseInt(wh.substring(0, 2));
            m = Integer.parseInt(wh.substring(2, 4));
            s = Integer.parseInt(wh.substring(4, 6));
        } catch (Exception e) {
            log.error("DateSupport: maketime (" + wh + ")");
        }
        return s + 60 * (m + 60 * h);
    }

    /**
     * Returns the number of seconds from 1-Jan-1970 00:00:00 to a given time
     *
     * @param wh Date in the form 'yyyymmddhhmmss'
     * @return Number of seconds from 1-Jan-1970 00:00:00 to the given time
     * @see DateSupport#parsedate
     * @see DateSupport#parsetime
     */
    public static int parsedatetime(String wh) {
        return parsedate(wh.substring(0, 8)) + parsetime(wh.substring(8, 14));
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the time as a string
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return String in the form 'hhmm' for the given time
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getTime(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        return f.format(v);
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the time as a string
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return String in the form 'hhmmss' for the given time
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getTimeSec(int val) {
        Date v;
        if (val == -1) {
            // WHY? This behaviour leads to incorrect displaying of MMEvents!!
            v = new Date();
        } else {
            if (dooffset) {
                val += offset;
            }
            v = new Date((long) val * 1000);
        }
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        return f.format(v);
    }

    /**
     * Takes an integer representing the number of seconds from 00:00:00 and returns the time as a string
     *
     * @param val Number of seconds from 00:00:00
     * @return String in the form 'hhmmss' for the given time
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getTimeSecLen(int val) {
        Date date = new Date((long) val * 1000);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
        return f.format(date);
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the day in the month
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return String containing the day of the month (1 to 31)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getMonthDay(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        SimpleDateFormat f = new SimpleDateFormat("dd");
        return f.format(v);
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the number of the month
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return String containing the number of the month (1 to 12)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getMonth(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        SimpleDateFormat f = new SimpleDateFormat("MM");
        return f.format(v);
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the year
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return String containing the year (1900 to ....)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static String getYear(int val) {
        //log.debug(val);
        if (dooffset) {
            val += offset;
        }
        Date v = new Date(((long) val) * 1000);
        Calendar c = Calendar.getInstance();
        c.setTime(v);

        return Integer.toString(c.get(Calendar.YEAR));
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00 and returns the month as an integer
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return Integer containing the value of the month (1 to 12)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getWeekDayInt
     * @see DateSupport#getDayInt
     */
    public static int getMonthInt(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        Calendar c = Calendar.getInstance();
        c.setTime(v);
        return c.get(Calendar.MONTH);
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00
     * and returns the number of the day in the week as an integer
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return Integer containing the number of the day in the week (0 to 6)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getDayInt
     */
    public static int getWeekDayInt(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        Calendar c = Calendar.getInstance();
        c.setTime(v);
        return c.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * Takes an integer representing the number of seconds from 1-Jan-1970 00:00:00
     * and returns the number of the day in the month as an integer
     *
     * @param val Number of seconds from 1-Jan-1970 00:00:00
     * @return Integer containing the number of the day in the month (1 to 31)
     * @see DateSupport#getTime
     * @see DateSupport#getTimeSec
     * @see DateSupport#getTimeSecLen
     * @see DateSupport#getMonthDay
     * @see DateSupport#getMonth
     * @see DateSupport#getYear
     * @see DateSupport#getMonthInt
     * @see DateSupport#getWeekDayInt
     */
    public static int getDayInt(int val) {
        if (dooffset) {
            val += offset;
        }
        Date v = new Date((long) val * 1000);
        Calendar c = Calendar.getInstance();
        c.setTime(v);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Return the time-difference between our timezone and GMT
     *
     * @return Integer containing the number of milliseconds representing the time-difference between us and GMT
     */
    public static long getMilliOffset() {
        if (!dooffset) {
            // Do not worry about the code below, since it will never be called

            TimeZone tz1, tz2;
            int off1, off2;
            Date d = new Date();

            tz1 = TimeZone.getDefault(); // This is MET but they think it's the Middle East
            tz2 = TimeZone.getTimeZone("ECT"); //Apparently we live there ?
            off1 = tz1.getRawOffset();
            off2 = tz2.getRawOffset();
            if (tz1.inDaylightTime(d)) {
                off1 += (3600 * 1000); // Activate before sunday morning
            }

            if (tz2.inDaylightTime(d)) {
                off2 += (3600 * 1000);
            }
            return off1 - off2;
        } else {
            return (long) offset * 1000;
        }
    }

    /**
     * Return the current time in milliseconds (for the current-timezone!!)
     *
     * @return Integer containing the number of milliseconds representing the current time
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis() - getMilliOffset();
    }

    /**
     * Convert a string (like "12:42:15 1/2/97") to milliseconds from 1970
     * The timezone used is 'GMT'
     * @param date String which contains the date and time in the format "hour:minutes:sec day/month/year"
     * @return the elapsed milliseconds since 1970 from this date
     */
    public static long convertDateToLong(String date) {
        // Next line was the old code:
        // return (convertStringToLong(date));
        log.debug("Converting " + date);
        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();

        cal.setTimeZone(tz);
        cal = parseDate(cal, date);

        Date d = cal.getTime();
        long l = d.getTime();

        return l;
    }

    /*
     * ----- private functions used by convertDateToLong --------
     */


    /**
     * Parse a string containing a date and put it in a calendar
     * @param cal Calander object that is used for storing the parsed date
     * @param date String in the form:  hour:minute:second day/month/year
     * @return Calander object representing the parsed date
     * @see DateSupport parseDateRev
     */
    public static Calendar parseDate(Calendar cal, String date) {
        StringTokenizer tok = new StringTokenizer(date, "-\n\r:/ ");
        String token = null;

        cal.clear(Calendar.HOUR_OF_DAY);

        token = tok.nextToken();
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.MINUTE, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.SECOND, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.MONTH, Integer.valueOf(token).intValue() - 1);
        token = tok.nextToken();
        cal.set(Calendar.YEAR, Integer.valueOf(token).intValue());
        return (cal);
    }

    /**
     * Parse a string containing a date and put it in a calendar, the string is in reversed order
     * @param cal Calander object that is used for storing the parsed date
     * @param date String in the form:  year/month/day hour:minute:second
     * @return Calander object representing the parsed date
     * @see DateSupport parseDate
     */
    public static Calendar parseDateRev(Calendar cal, String date) {
        StringTokenizer tok = new StringTokenizer(date, "-\n\r:/ ");
        String token = null;

        cal.clear(Calendar.HOUR_OF_DAY);

        token = tok.nextToken();
        cal.set(Calendar.YEAR, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.MONTH, Integer.valueOf(token).intValue() - 1);
        token = tok.nextToken();
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.MINUTE, Integer.valueOf(token).intValue());
        token = tok.nextToken();
        cal.set(Calendar.SECOND, Integer.valueOf(token).intValue());
        return (cal);
    }

    /**
     * Return a string for a given date
     * @param time Integer representing the time in seconds since 1-Jan-1970 00:00:00
     * @return String in the form 'hhmmss day/month/year'
     * @see DateSupport#date2day
     * @see DateSupport#date2date
     */
    public static String date2string(int time) {
        return getTimeSec(time) + " " + getMonthDay(time) + "/" + getMonth(time) + "/" + getYear(time);
    }

    /**
     * Return a string for a given date
     * @param time Integer representing the time in seconds since 1-Jan-1970 00:00:00
     * @return String in the form 'year-month-day'
     * @see DateSupport#date2string
     * @see DateSupport#date2date
     */
    public static String date2day(int time) {
        return getYear(time) + "-" + getMonth(time) + "-" + getMonthDay(time);
    }

    /**
     * Return a string for a given date
     * @param time Integer representing the time in seconds since 1-Jan-1970 00:00:00
     * @return String in the form 'year-month-day hhmmss'
     * @see DateSupport#date2string
     * @see DateSupport#date2day
     */
    public static String date2date(int time) {
        return getYear(time) + "-" + getMonth(time) + "-" + getMonthDay(time) + " " + getTimeSec(time);
    }

    /**
     * Dump a date as string
     * @param time Integer representing the time in seconds since 1-Jan-1970 00:00:00
     * @return String with a date
     */
    private static String dumpdate(int d) {
        Date dd = new Date((long) d * 1000);
        return dd.toString();
    }

    /**
     * Main method used for testing purposes
     * @param args Array of arguments
     */
    public static void main(String args[]) {
        log.info("Date (without corr)" + date2string((int) (System.currentTimeMillis() / 1000)) + " " + System.currentTimeMillis() / 1000);
        log.info("Date (with corr)" + date2string((int) (DateSupport.currentTimeMillis() / 1000)) + " : " + DateSupport.currentTimeMillis() / 1000);
        log.info("Date " + args[0] + " " + date2string(Integer.parseInt(args[0])));
        log.info("Date " + args[0] + " " + dumpdate(Integer.parseInt(args[0])));
        String ID = System.getProperty("user.timezone", "GMT");
        log.info("ID " + ID + " : " + getMilliOffset());
        log.info("ParseDate " + parsedate(args[1]));
    }
}
