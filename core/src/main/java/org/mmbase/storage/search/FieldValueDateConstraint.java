/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.Calendar;

/**
 * @javadoc
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public interface FieldValueDateConstraint extends FieldValueConstraint {

    // Try to match the part constants available in most datetime constraints to the Calendar constants.
    // This does not work everywhere, as some values are not supported by Calendar, and vice versa.

    /** Date part: 'century' */
    static final int CENTURY      = 0;                     // note: does not exist in Calendar
                                                           // (0 is Calendar.ERA)
    /** Date part: 'year' */
    static final int YEAR         = Calendar.YEAR;         // 1
    /** Date part: 'month' */
    static final int MONTH        = Calendar.MONTH;        // 2
    /** Date part: 'week' */
    static final int WEEK         = Calendar.WEEK_OF_YEAR; // 3
    /** Date part: 'quarter' */
    static final int QUARTER      = 4;                     // note: does not exist in Calendar
                                                           // (4 is Calendar.WEEK_OF_MONTH)
    /** Date part: 'day of month' */
    static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH; // 5
    /** Date part: 'day of year' */
    static final int DAY_OF_YEAR  = Calendar.DAY_OF_YEAR;  // 6
    /** Date part: 'day of week' */
    static final int DAY_OF_WEEK  = Calendar.DAY_OF_WEEK;  // 7

    // 8 (Calendar.DAY_OF_WEEK_IN_MONTH) and 9 (Calendar.AM_PM) are not used

    /** Time part: 'hour' */
    static final int HOUR         = Calendar.HOUR;         // 10

    // 11 (Calendar.HOUR_OF_DAY) is not used

    /** Time part: 'minute' */
    static final int MINUTE       = Calendar.MINUTE;       // 12
    /** Time part: 'second' */
    static final int SECOND       = Calendar.SECOND;       // 13
    /** Time part: 'millisecond' */
    static final int MILLISECOND  = Calendar.MILLISECOND;  // 14

    /**
     * Part descriptions corresponding to the date and time part values:
     * {@link #CENTURY}, {@link #YEAR}, {@link #MONTH}, {@link #QUARTER},
     * {@link #WEEK}, {@link #DAY_OF_YEAR}, {@link #DAY_OF_MONTH}, {@link #DAY_OF_WEEK},
     * {@link #HOUR}, {@link #MINUTE}, {@link #SECOND} and {@link #MILLISECOND}
     */
    public final static String[] PART_DESCRIPTIONS = new String[] {
         "century", "year", "month", "week",
         "quarter",
         "day of month", "day of year", "day of week",
         "","",
         "hour",
         "",
         "minute", "second", "millisecond"
    };

    /**
     * Returns the part of the date-field wich is to be compared.
     */
    int getPart();

}
