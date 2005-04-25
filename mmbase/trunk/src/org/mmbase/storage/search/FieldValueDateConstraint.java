/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * @javadoc
 * @author Michiel Meeuwissen
 * @version $Id: FieldValueDateConstraint.java,v 1.3 2005-04-25 14:56:57 pierre Exp $
 * @since MMBase-1.8
 */
public interface FieldValueDateConstraint extends FieldValueConstraint {

    /** Date part: 'century' */
    static final int CENTURY      = 0;
    /** Date part: 'year' */
    static final int YEAR         = 1;
    /** Date part: 'month' */
    static final int MONTH        = 2;
    /** Date part: 'quarter' */
    static final int QUARTER      = 3;
    /** Date part: 'week' */
    static final int WEEK         = 4;
    /** Date part: 'day of year' */
    static final int DAY_OF_YEAR  = 5;
    /** Date part: 'day of month' */
    static final int DAY_OF_MONTH = 6;
    /** Date part: 'day of week' */
    static final int DAY_OF_WEEK  = 7;

    /** Time part: 'hour' */
    static final int HOUR         = 8;
    /** Time part: 'minute' */
    static final int MINUTE       = 9;
    /** Time part: 'second' */
    static final int SECOND       = 10;

    /**
     * Part descriptions corresponding to the date and time part values:
     * {@link #CENTURY}, {@link #YEAR}, {@link #MONTH}, {@link #QUARTER},
     * {@link #WEEK}, {@link #DAY_OF_YEAR}, {@link #DAY_OF_MONTH}, {@link #DAY_OF_WEEK},
     * {@link #HOUR}, {@link #MINUTE}, and {@link #SECOND}
     */
    public final static String[] PART_DESCRIPTIONS = new String[] {
         "century", "year", "month", "quarter",
         "weel", "day of year", "day of month", "day of week",
         "hour", "minute", "second"
    };

    /**
     * Returns the part of the date-field wich is to be compared.
     */
    int getPart();

}
