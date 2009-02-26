/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {

    /** Date pattern for showing the date, example: 01 January 1970 00:59 */
    private final static String TOTAL_DATE_FORMAT_WITH_TIME = "dd MMMM yyyy HH:mm";
    
    /** Date pattern for showing the date, example: 01 January 1970*/
    private final static String TOTAL_DATE_FORMAT = "EE dd MMMM yyyy";

    /** Date pattern for showing the date, example: 01 January 1970*/
    private final static String TOTAL_TIME_FORMAT = "HH:mm";
    
    /** Date pattern for showing the date, example: 01 January*/
    private final static String DAY_MONTH_FORMAT = "EE dd MMMM";
    
    /** Date pattern for showing the date, example: 01*/
    private final static String DAY_FORMAT = "EE dd";

   /** Date pattern for showing the date , example:2008-1-1 */
   private final static String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
   
   private static Log log = LogFactory.getLog(DateUtil.class);

    public static int getHour(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.MINUTE);
    }

    public static int getSecond(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.SECOND);
    }

    public static int getDay(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * Returns a String for displaying dates for the given locale
     * 
     * @param beginDate
     *            the begindate to process
     * @param endDate
     *            the enddate to process
     * @param locale
     *            the locale to process
     * @return String a start- and enddate as a certain datepattern
     */
    public static String displayDate(Long beginDate, Long endDate, Locale locale) {
       
       Calendar beginCal = Calendar.getInstance();
       Calendar endCal = Calendar.getInstance();
       beginCal.setTime(new Date(beginDate.longValue() * 1000));
       endCal.setTime(new Date(endDate.longValue() * 1000));
       
       String returnString = "";
       
       SimpleDateFormat dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT, locale);

       if (beginCal.get(Calendar.YEAR) < endCal.get(Calendar.YEAR)) {
          returnString = dateFormatter.format(beginCal.getTime()) + " - " + dateFormatter.format(endCal.getTime());
       }
       else if (beginCal.get(Calendar.MONTH) < endCal.get(Calendar.MONTH)) {
          dateFormatter = new SimpleDateFormat(DAY_MONTH_FORMAT,  locale);
          returnString = dateFormatter.format(beginCal.getTime()) + " - ";
          dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT, locale);
          returnString += dateFormatter.format(endCal.getTime());
       }
       else if (beginCal.get(Calendar.DATE) < endCal.get(Calendar.DATE)) {
          dateFormatter = new SimpleDateFormat(DAY_FORMAT,  locale);
          returnString = dateFormatter.format(beginCal.getTime()) + " - ";
          dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT, locale);
          returnString += dateFormatter.format(endCal.getTime());
       }
       else {
          dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT, locale);
          returnString = dateFormatter.format(endCal.getTime());
       }
       return returnString;
    }
    
    /**
     * Returns a String for displaying a date for the given locale
     * @param displayDate the date to process
     * @param locale the locale to process
     * @return String a date as a certain datepattern
     */
    public static String displayDate(Long displayDate, Locale locale) {
       return displayDate(new Date(displayDate.longValue()), locale, true);
    }

    /**
     * Returns a String for displaying a date for the given locale
     * @param displayDate the date to process
     * @param locale the locale to process
     * @param useSpecial Use special day strings for yersterdat, today and tomorrow
     * @return String a date as a certain datepattern
     */
    public static String displayDate(Date displayDate, Locale locale, boolean useSpecial) {
       if (useSpecial && isToday(displayDate)) {
           return specialDisplayDate("today", locale);
       }
       if (useSpecial && isYesterday(displayDate)) {
           return specialDisplayDate("yesterday", locale);
       }
       if (useSpecial && isTomorrow(displayDate)) {
           return specialDisplayDate("tomorrow", locale);
       }
       SimpleDateFormat dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT, locale);
       return dateFormatter.format(displayDate);
    }

    /**
     * Returns a String for displaying a date with the time for the given locale
     * @param displayDate the date to process
     * @param locale the locale to process
     * @return String a date as a certain datepattern
     */
    public static String displayDateWithTime(Long displayDate, Locale locale) {
        return displayDateWithTime(new Date(displayDate.longValue()), locale, true);
    }

    /**
     * Returns a String for displaying a date with the time for the given locale
     * @param displayDate the date to process
     * @param locale the locale to process
     * @param useSpecial Use special day strings for yersterdat, today and tomorrow
     * @return String a date as a certain datepattern
     */
    public static String displayDateWithTime(Date displayDate, Locale locale, boolean useSpecial) {
        if (useSpecial && isToday(displayDate)) {
            String time = displayTime(displayDate, locale);
            return specialDisplayDate("today", locale) + " " + time;
        }
        if (useSpecial && isYesterday(displayDate)) {
            String time = displayTime(displayDate, locale);
            return specialDisplayDate("yesterday", locale) + " " + time;
        }
        if (useSpecial && isTomorrow(displayDate)) {
            String time = displayTime(displayDate, locale);
            return specialDisplayDate("tomorrow", locale) + " " + time;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat(TOTAL_DATE_FORMAT_WITH_TIME, locale);
        return dateFormatter.format(displayDate);
    }

    /**
     * Returns a String for displaying a time for the given locale
     * @param displayDate the date to process
     * @param locale the locale to process
     * @return String a date as a certain datepattern
     */
    private static String displayTime(Date displayDate, Locale locale) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(TOTAL_TIME_FORMAT, locale);
        String time = dateFormatter.format(displayDate);
        return time;
    }

    private static String specialDisplayDate(String dayName, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(DateUtil.class.getName(), locale);
        return bundle.getString(dayName);
    }

    /**
     * This method checks if the parametered day is today
     * @param date check with today
     * @return <code>true</code> when date is today
     */
    public static boolean isToday(Date date) {
       Calendar now = Calendar.getInstance();
       return isSameDay(date, now);
    }

    /**
     * This method checks if the parametered day is yesterday
     * @param date check with yesterday
     * @return <code>true</code> when date is yesterday
     */
    public static boolean isYesterday(Date date) {
       Calendar yesterday = Calendar.getInstance();
       yesterday.add(Calendar.DATE, -1);
       return isSameDay(date, yesterday);
    }

    /**
     * This method checks if the parametered day is tomorrow
     * @param date check with tomorrow
     * @return <code>true</code> when date is tomorrow
     */
    public static boolean isTomorrow(Date date) {
       Calendar tomorrow = Calendar.getInstance();
       tomorrow.add(Calendar.DATE, 1);
       return isSameDay(date, tomorrow);
    }

    
    public static boolean isSameDay(Date date, Calendar other) {
        Calendar then = Calendar.getInstance();
        then.setTime(date);
        return (other.get(Calendar.YEAR) == then.get(Calendar.YEAR)
                && other.get(Calendar.MONTH) == then.get(Calendar.MONTH) && other
                .get(Calendar.DATE) == then.get(Calendar.DATE));
    }

    /**
     * builds a Date object from 3 Strings
     *
     * @param year a
     * @param month a
     * @param day a
     * @return the created date
     */
    public static Date buildDate(String year, String month, String day) {
       Calendar calendar = getEmptyCalendar();
       calendar.set(Integer.parseInt(year), 
                    Integer.parseInt(month) - 1,
                    Integer.parseInt(day), 0, 0, 0);
       return calendar.getTime();
    }

    /**
     * builds a Date object which represents the first day of the given week
     *
     * @param year a
     * @param week aS
     * @return the created date
     */
    public static Date buildBeginDateWeek(String year, String week) {
       Calendar calendar = getEmptyCalendar();
       calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
       calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
       calendar.set(Calendar.YEAR, Integer.parseInt(year));
       return calendar.getTime();
    }

    /** builds a Date object which represents the last day of the given week
     * @param year a
     * @param week a
     * @return the created date
     */
    public static Date buildEndDateWeek(String year, String week) {
       Calendar calendar = getEmptyCalendar();
       calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
       calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
       calendar.set(Calendar.YEAR, Integer.parseInt(year));
       return calendar.getTime();
    }
    
    /** builds a Date object which represents the first day of the given week
     * @return the created date
     * @param year a
     * @param month a
     */
    public static Date buildBeginDateMonth(String year, String month) {
       Calendar calendar = getEmptyCalendar();
       calendar.set(Calendar.DAY_OF_MONTH, 1);
       calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
       calendar.set(Calendar.YEAR, Integer.parseInt(year));
       return calendar.getTime();
    }

    /** builds a Date object which represents the last day of the given month
     * @return the created date
     * @param year a
     * @param month a
     */
    public static Date buildEndDateMonth(String year, String month) {
       Calendar calendar = getEmptyCalendar();
       calendar.set(Calendar.YEAR, Integer.parseInt(year));
       calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
       calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
       return calendar.getTime();
    }

    
    private static Calendar getEmptyCalendar() {
        Calendar calendar = Calendar.getInstance(new Locale("nl", "NL"));
        calendar.clear();
        return calendar;
     }

   /**
    * parser string("yyyy-MM-dd") to date object
    * 
    * @param raw
    *           string object
    * @return Date object
    * 
    */
   public static Date parser(String raw) {
      Date date = null;
      raw = raw.replaceAll("/", "-");
      if (StringUtils.isNotBlank(raw)) {
         SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
         try {
            date = format.parse(raw);
         } catch (ParseException e) {
            log.error(e);
         }
      }
      return date;
   }

   /**
    * parser date object to string ojbect("yyyy-MM-dd"),
    * 
    * @param date
    *           Date ojbect
    * @return a String object
    * 
    */
   public static String parser(Date date) {

      SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
      return format.format(date);
   }

   /**
    * calculate date ,
    * 
    * @param date
    *           Date ojbect
    * @param value
    *           the value used to operate at
    * @param mode
    *           m = month;w=week of year;d=date;
    * @return a Date object
    * 
    */
   public static Date calculateDateByDuration(Date date, int value, String mode) {
      Calendar calender = new GregorianCalendar();
      calender.setTime(date);

      if ("m".equals(mode)) {
         calender.add(Calendar.MONTH, value);
      }

      if ("w".equals(mode)) {
         calender.add(Calendar.WEEK_OF_YEAR, value);
      }

      if ("d".equals(mode)) {
         calender.add(Calendar.DATE, value);
      }

      return calender.getTime();
   }

   /**
    * 
    * get current date time by millis type
    * 
    */
   public static Date getCurrent() {
      return new Date(System.currentTimeMillis());
   }
    public static void main(String[] args) {
        System.out.println(displayDateWithTime(new Date(), new Locale("en"), true));
    }
    
}
