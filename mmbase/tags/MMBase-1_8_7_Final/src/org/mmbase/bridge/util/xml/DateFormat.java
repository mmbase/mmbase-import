/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml;

import org.apache.xpath.XPathAPI;
import java.util.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Dates are stored as integer in mmbase. If you want to show these dates
 * in a nice format in the XSL transformation.
 * it is necessary to use this Xalan extension.
 * The XSLT looks like this then:
 *
 * <pre>
 *  &lt;xsl:stylesheet  version = "1.0"
 *    xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
 *    xmlns:date ="org.mmbase.bridge.util.xml.DateFormat"
 *  &gt;
 * </pre>
 *
 * @author Nico Klasens
 * @author Martijn Houtman
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.7
 */
public class DateFormat {

    private static final Logger log = Logging.getLoggerInstance(DateFormat.class);

    /**
     * TimeZone null or blank means server timezone. If timeZone is not
     * recognized it will fall back to GMT.
     * @param timeZone String representation of a timezine
     * @return TimeZone object or the default timezone when id does not exist
     */
    private static TimeZone getTimeZone(String timeZone) {
        if (timeZone == null || "".equals(timeZone.trim())) {
            return TimeZone.getDefault();
        } else {
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            if (!tz.getID().equals(timeZone)) {
                tz = TimeZone.getDefault();
            }
            return tz;
        }
    }

    /**
     * Formats a node's field value with the date pattern
     * @param number the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @param timeZone  timezone of the field value
     * @param language Language of the field value
     * @param country country  of the field value
     * @return the formatted string
     */
    public static String format(String number, String fieldName, String pattern, String timeZone, String language, String country) {
        return format("mmbase", number, fieldName, pattern, timeZone, language, country);
    }


    /**
     * Formats a node's field value with the date pattern
     * @param cloudName the name of the cloud in which to find the node
     * @param number the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @param timeZone  timezone of the field value
     * @param language Language of the field value
     * @param country country  of the field value
     * @return the formatted string
     */
    public static String format(String cloudName, String number, String fieldName, String pattern, String timeZone, String language, String country) {
        try {
            Cloud cloud = LocalContext.getCloudContext().getCloud(cloudName);
            cloud.setLocale(new Locale(language, country));
            return format(cloud, number, fieldName, pattern, timeZone);
        } catch (BridgeException e) {
            return "could not find '" + fieldName + "' on node '" + number + "' (" + e.toString() + ")";
        }
    }


    /**
     * Formats a node's field value with the date pattern
     * @param cloud the cloud in which to find the node
     * @param number the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @param timeZone  timezone of the field value
     * @return the formatted string
     */
    public static String format(Cloud cloud, String number, String fieldName, String pattern, String timeZone) {
        try {
            Node node = cloud.getNode(number);
            String fieldValue = "" + node.getIntValue(fieldName);
            return format(fieldValue, pattern, 1000, timeZone,  cloud.getLocale());
        } catch (BridgeException e) {
            if (log.isDebugEnabled()) {
                log.debug("could not find '" + fieldName + "' on node '" + number + "'");
                log.trace(Logging.stackTrace(e));
            }
            return "could not find " + fieldName + " on node " + number + "(" + e.toString() + ")";
        }
    }

    /**
     * Formats the fieldvalue to a date pattern
     * 
     * @param fieldValue  time-stamp in seconds
     * @param pattern   the date pattern (i.e. 'dd-MM-yyyy')
     * @param timeZone  timezone of the field value
     * @param language Language of the field value
     * @param country country  of the field value
     * @return the formatted string
     */
    public static String format(String fieldValue, String pattern, String timeZone, String language, String country) {
        return format(fieldValue, pattern, 1000, timeZone, language, country);
    }

    /** Formats the fieldvalue to a date pattern
     *
     * @param fieldValue  time-stamp
     * @param pattern   the date pattern (i.e. 'dd-MM-yyyy')
     * @param factor    Factor to multiply fieldvalue to make milliseconds. Should be 1000 normally (so field in seconds)
     * @param timeZone  Timezone. Null or blank means server timezone. If not recognized it will fall back to GMT.
     * @param language  The language for which the date must be formatted.
     * @param country   The country for which the date must be formatted.
     * @return the formatted string
     */
    public static String format(String fieldValue, String pattern, int factor, String timeZone, String language, String country) {
        return format(fieldValue, pattern, factor, timeZone, new Locale(language, country));
    }

    /**
     * @param fieldValue  time-stamp
     * @param pattern   the date pattern (i.e. 'dd-MM-yyyy')
     * @param factor    Factor to multiply fieldvalue to make milliseconds. Should be 1000 normally (so field in seconds)
     * @param timeZone  Timezone. Null or blank means server timezone. If not recognized it will fall back to GMT.
     * @param locale    The locale for which the date must be formatted.
     * @return the formatted String
     */
    protected static String format(String fieldValue, String pattern, int factor, String timeZone, Locale locale) {
        if (fieldValue == null || "".equals(fieldValue)) {
           return "";
        }
        java.text.DateFormat df = org.mmbase.util.DateFormats.getInstance(pattern, timeZone, locale);
        long seconds = Long.valueOf(fieldValue).longValue();
        return df.format(new Date(seconds * factor));
    }

    /**
     * Formats a node's field value with the date pattern.
     * This version requires you to supply a DOM node. It will search for a tag of the form
     * &lt;field name='number' &gt; and uses it's contents to retrieve the node.
     * @deprecated not sure where this is used?
     * @param cloud the cloud in which to find the node
     * @param node A DOM node (xml) containing the node's fields as subtags
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @param timeZone The timezone of the field value
     * @return the formatted string
     * @throws javax.xml.transform.TransformerException if something went wrong while searching the DOM Node
     */
    public static String format(Cloud cloud, org.w3c.dom.Node node, String fieldName, String pattern, String timeZone) throws javax.xml.transform.TransformerException {
        log.debug("calling with dom node");
        // bit of a waste to use an xpath here?
        String number = XPathAPI.eval(node, "./field[@name='number']").toString();
        return format(cloud, number, fieldName, pattern, timeZone);
    }

    /** Returns the year part of the date
     *
     * @param fieldValue  time-stamp
     * @return year part
     */
    public static int getYear(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.YEAR, "");
    }

    /** Returns the month part of the date
     *
     * @param fieldValue  time-stamp
     * @return month part
     */
    public static int getMonth(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.MONTH, "") + 1;
    }
    
    /** Returns the day of the month part of the date
     *
     * @param fieldValue  time-stamp
     * @return day of the month part
     */
    public static int getDay(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.DAY_OF_MONTH, "");
    }
    
    /** Returns the hours part of the date
     *
     * @param fieldValue  time-stamp
     * @return hours part
     */
    public static int getHours(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.HOUR_OF_DAY, "");
    }
    
    /** Returns the minutes part of the date
     *
     * @param fieldValue  time-stamp
     * @return minutes part
     */
    public static int getMinutes(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.MINUTE, "");
    }
    
    /** Returns the seconds part of the date
     *
     * @param fieldValue  time-stamp
     * @return seconds part
     */
    public static int getSeconds(String fieldValue) {
        return getDatePart(fieldValue, 1000, Calendar.SECOND, "");
    }

    /** Returns the year part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return year part
     */
    public static int getYear(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.YEAR, timeZone);
    }

    /** Returns the month part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return month part
     */
    public static int getMonth(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.MONTH, timeZone) + 1;
    }
    
    /** Returns the day of the month part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return day of the month part
     */
    public static int getDay(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.DAY_OF_MONTH, timeZone);
    }
    
    /** Returns the hours part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return hours part
     */
    public static int getHours(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.HOUR_OF_DAY, timeZone);
    }
    
    /** Returns the minutes part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return minutes part
     */
    public static int getMinutes(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.MINUTE, timeZone);
    }
    
    /** Returns the seconds part of the date
     *
     * @param fieldValue  time-stamp
     * @param timeZone  timezone
     * @return seconds part
     */
    public static int getSeconds(String fieldValue, String timeZone) {
        return getDatePart(fieldValue, 1000, Calendar.SECOND, timeZone);
    }

    /** Returns the a part of the date
     *
     * @param fieldValue  time-stamp
     * @param factor    Factor to multiply fieldvalue to make milliseconds. Should be 1000 normally (so field in seconds)
     * @param datePart  which part of the date should be returned. These are Calendar constants
     * @return a part
     */
    public static int getDatePart(String fieldValue, int factor, int datePart) {
        return getDatePart(fieldValue, factor, datePart, "");
    }

    /** Returns the a part of the date
     *
     * @param fieldValue  time-stamp
     * @param factor    Factor to multiply fieldvalue to make milliseconds. Should be 1000 normally (so field in seconds)
     * @param datePart  which part of the date should be returned. These are Calendar constants
     * @param timeZone  Timezone. Null or blank means server timezone. If not recognized it will fall back to GMT.
     * @return a part
     */
    public static int getDatePart(String fieldValue, int factor, int datePart, String timeZone) {
        if (fieldValue != null && !"".equals(fieldValue.trim())) {
            Calendar cal = Calendar.getInstance(getTimeZone(timeZone));
            long seconds = Long.valueOf(fieldValue).longValue();
            cal.setTimeInMillis(seconds * factor);
            return cal.get(datePart);
        } else {
            return -1;
        }
    }

}
