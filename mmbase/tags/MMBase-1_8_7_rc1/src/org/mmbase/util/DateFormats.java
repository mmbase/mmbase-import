/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import java.util.*;
import java.text.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Utility function to create DateFormat instances.
 * 
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7.1
 * @version $Id$
 */
public class DateFormats {

    private static final Logger log = Logging.getLoggerInstance(DateFormats.class);

    /**
     * Creates a DateFormat instance, based on a String.
     *
     * @param format The format defining the DateFormat. This can be constants like :FULL, :FULL.FULL, :LONG, :MEDIUM or :SHORT. 
     *               It can also be 'e' for weekday. Also 'RFC822' or 'rfc822' is possible then the 
     *               quite complicated http://www.faqs.org/rfcs/rfc822.html compliant date and time
     *               is made, which comes in handy when you need to create a rss feed f.e.
     *               Or none of those, then a SimpleDateFormat is instantiated.
     * @param timeZone A String describing the timeZone (see DateFormat#setTimeZone)
     * @param locale   Most DateFormat's need a Locale too.
     * @throws IllegalArgumentException
     */
    public static DateFormat getInstance(String format, String timeZone, Locale locale)  {
        DateFormat df;
        if (format.length() > 0 && format.charAt(0) == ':') {
            log.debug("found symbolic format");
            if (format.charAt(1) == '.') {
                df = DateFormat.getTimeInstance(getDateFormatStyle(format.substring(2)), locale);
            } else if (format.indexOf('.') == -1) {
                df = DateFormat.getDateInstance(getDateFormatStyle(format.substring(1)), locale);
            } else {
                int i = format.indexOf('.');
                df = DateFormat.getDateTimeInstance(getDateFormatStyle(format.substring(1, i)),
                                                            getDateFormatStyle(format.substring(i+1)), locale);
            }
        } else if (format.equals("e")) {
            df = new DayOfWeekDateFormat();
        } else if (format.equals("RFC822") || format.equals("rfc822")) {
            df = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        } else {
            df = new SimpleDateFormat(format, locale);
        }
        if (!( timeZone == null ||  timeZone.equals(""))) {
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            df.setTimeZone(org.mmbase.util.dateparser.DateParser.defaultTimeZone);
        }
        return df;

    }

    /**
     * Converts a string to a DateFormat constant.
     *
     * @param style A string describing the dateformat style (FULL, LONG, MEDIUM, SHORT)
     * @return A DateFormat style constant.
     * @see    java.text.DateFormat
     */
    private static int getDateFormatStyle(String style) {
        if ("FULL".equals(style)) {
            return DateFormat.FULL;
        } else if ("LONG".equals(style)) {
            return DateFormat.LONG;
        } else if ("MEDIUM".equals(style)) {
            return DateFormat.MEDIUM;
        } else if ("SHORT".equals(style)) {
            return DateFormat.SHORT;
        } else {
            throw new IllegalArgumentException("Unknown DateFormat Style " + style);
        }
    }

    /**
     * There is no DateFormat which can return the day of the week as a number available in
     * java.text package.  This provides one. 
     */

    protected static class DayOfWeekDateFormat extends DateFormat {
        private TimeZone zone = null;
        public Date parse(String source, ParsePosition pos) {
            Calendar calendar = Calendar.getInstance(zone != null ? zone :  org.mmbase.util.dateparser.DateParser.defaultTimeZone);
            int day = source.charAt(0) - '0';
            pos.setIndex(pos.getIndex() + 1);
            calendar.set(Calendar.DAY_OF_WEEK, day);
            return calendar.getTime();
        }
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
            Calendar calendar = Calendar.getInstance(zone != null ? zone :  org.mmbase.util.dateparser.DateParser.defaultTimeZone);
            calendar.setTime(date);
            // pos.setBeginIndex(0); pos.setEndIndex(1);
            toAppendTo.append(calendar.get(Calendar.DAY_OF_WEEK));
            return toAppendTo;
        }

        public void setTimeZone(TimeZone value) {
            zone = value;
        }

        
    }
}
