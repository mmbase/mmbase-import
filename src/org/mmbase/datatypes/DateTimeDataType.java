/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.FieldValueDateConstraint;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: DateTimeDataType.java,v 1.17 2005-10-02 16:10:32 michiel Exp $
 * @since MMBase-1.8
 */
public class DateTimeDataType extends DataType {

    private static final Logger log = Logging.getLoggerInstance(DateTimeDataType.class);

    public static final String CONSTRAINT_MIN = "min";
    public static final Date CONSTRAINT_MIN_DEFAULT = null;
    public static final String ERROR_MIN_INCLUSIVE = "minInclusive";
    public static final String ERROR_MIN_EXCLUSIVE = "minExclusive";

    public static final String CONSTRAINT_MAX = "max";
    public static final Date CONSTRAINT_MAX_DEFAULT = null;
    public static final String ERROR_MAX_INCLUSIVE = "maxInclusive";
    public static final String ERROR_MAX_EXCLUSIVE = "maxExclusive";

    protected DataType.ValueConstraint minConstraint;
    protected int minPrecision;
    protected boolean minInclusive;

    protected DataType.ValueConstraint maxConstraint;
    protected int maxPrecision;
    protected boolean maxInclusive;

    // keys for use with error messages to retrive from the bundle
    private String minInclusiveErrorKey;
    private String minExclusiveErrorKey;
    private String maxInclusiveErrorKey;
    private String maxExclusiveErrorKey;


    // see javadoc of DateTimeFormat
    private boolean weakPattern = true; // means, may not be changed, must be cloned before changing something
    private DateTimePattern pattern = DateTimePattern.DEFAULT;

    /**
     * Constructor for DateTime field.
     */
    public DateTimeDataType(String name) {
        super(name, Date.class);
        // the default default value of a date time field is 'now'
        // That's a good default and is more or less backwards compatible, because unfilled
        // 'eventtime' object used to be displayed as now too.
        try {
            setDefaultValue(org.mmbase.util.DynamicDate.getInstance("now"));
        } catch (org.mmbase.util.dateparser.ParseException pe) {
            log.error(pe);
            // could not happen, 'now' should parse
        }
    }

    public DataType setDefaultValue(Object o) {
        return super.setDefaultValue(Casting.toDate(o));
    }

    // javadoc inherited
    public void erase() {
        super.erase();
        // Determine the key to retrieve an error message from a property's bundle
        minInclusiveErrorKey = getBaseTypeIdentifier() + ".minInclusive.error";
        minExclusiveErrorKey = getBaseTypeIdentifier() + ".minExclusive.error";
        maxInclusiveErrorKey = getBaseTypeIdentifier() + ".maxInclusive.error";
        maxExclusiveErrorKey = getBaseTypeIdentifier() + ".maxExclusive.error";

        minConstraint = null;
        minInclusive = true;
        minPrecision = Calendar.MILLISECOND;
        maxConstraint = null;
        minInclusive = true;
        maxPrecision = Calendar.MILLISECOND;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof DateTimeDataType) {
            DateTimeDataType dataType = (DateTimeDataType)origin;
            minConstraint  = inheritConstraint(dataType.minConstraint);
            minInclusive = dataType.isMinInclusive();
            minPrecision = dataType.getMinPrecision();
            maxConstraint  = inheritConstraint(dataType.maxConstraint);
            maxInclusive = dataType.isMaxInclusive();
            maxPrecision = dataType.getMaxPrecision();
            if (weakPattern) {
                pattern      = dataType.pattern;
            }
        }
    }

    /**
     * Returns the minimum value for this data type.
     * @return the property defining the minimum value
     */
    public Date getMin() {
        if (minConstraint == null) {
            return CONSTRAINT_MIN_DEFAULT;
        } else {
            return (Date)minConstraint.getValue();
        }
    }

    /**
     * Returns the minimum value for this data type.
     * @return the minimum value as an <code>Number</code>, or <code>null</code> if there is no minimum.
     */
    public DataType.ValueConstraint getMinConstraint() {
        if (minConstraint == null) minConstraint = new ValueConstraint(CONSTRAINT_MIN, CONSTRAINT_MIN_DEFAULT);
        // change the key for the property error description to match the inclusive status
        if (minInclusive) {
            minConstraint.getErrorDescription().setKey(ERROR_MIN_INCLUSIVE);
        } else {
            minConstraint.getErrorDescription().setKey(ERROR_MIN_EXCLUSIVE);
        }
        return minConstraint;
    }

    /**
     * Returns the precision for comparing the minimum value for this data type.
     * @return the precision value, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     */
    public int getMinPrecision() {
        return minPrecision;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minInclusive;
    }

    /**
     * Returns the maximum value for this data type.
     * @return the maximum value as an <code>Date</code>, or <code>null</code> if there is no maximum.
     */
    public Date getMax() {
        if (maxConstraint == null) {
            return CONSTRAINT_MAX_DEFAULT;
        } else {
            return (Date)maxConstraint.getValue();
        }
    }

    /**
     * Returns the maximum value for this data type.
     * @return the property defining the maximum value
     */
    public DataType.ValueConstraint getMaxConstraint() {
        if (maxConstraint == null) maxConstraint = new ValueConstraint(CONSTRAINT_MAX, CONSTRAINT_MAX_DEFAULT);
        // change the key for the property error description to match the inclusive status
        if (maxInclusive) {
            maxConstraint.getErrorDescription().setKey(ERROR_MAX_INCLUSIVE);
        } else {
            maxConstraint.getErrorDescription().setKey(ERROR_MAX_EXCLUSIVE);
        }
        return maxConstraint;
    }

    /**
     * Returns the precision for comparing the maximum value for this data type.
     * @return the precision value, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     */
    public int getMaxPrecision() {
        return maxPrecision;
    }

    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    /**
     * Sets the minimum Date value for this data type.
     * @param value the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMin(Date value) {
        return getMinConstraint().setValue(value);
    }

    /**
     * Sets the precision for comparing the minimum value for this data type.
     * @param precision the precision value, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     */
    public void setMinPrecision(int precision) {
        minPrecision = precision;
    }

    /**
     * Sets whether the maximum value is inclusive.
     * @param inclusive <code>true</code> if the value is inclusive
     */
    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    /**
     * Sets the minimum Date value for this data type.
     * @param value the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMin(Date value, int precision, boolean inclusive) {
        edit();
        setMinPrecision(precision);
        setMinInclusive(inclusive);
        return setMin(value);
    }

    /**
     * Sets the maximum Date value for this data type.
     * @param valuethe maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMax(Date value) {
        return getMaxConstraint().setValue(value);
    }

    /**
     * Sets the precision for comparing the maximum value for this data type.
     * @param precision the precision value, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     */
    public void setMaxPrecision(int precision) {
        maxPrecision = precision;
    }

    /**
     * Sets whether the maximum value is inclusive.
     * @param inclusive <code>true</code> if the value is inclusive
     */
    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    /**
     * Sets the maximum Date value for this data type.
     * @param valuethe maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMax(Date value, int precision, boolean inclusive) {
        edit();
        setMaxPrecision(precision);
        setMaxInclusive(inclusive);
        return setMax(value);
    }


    /**
     * The 'pattern' of a 'DateTime' value gives a SimpleDateFormat object which can be used as an
     * indication for presentation.
     *
     * Basicly, this should indicate whether the objects present e.g. only a date, only a time and wheter e.g. this time includes seconds or not.
     *
     * SimpleDateFormat is actually a wrapper arround a pattern, and that is used here.
     *
     */
    public DateTimePattern getPattern() {
        return pattern;
    }
    public void setPattern(String p, Locale locale) {
        if (weakPattern) {
            pattern = new DateTimePattern(p);
            weakPattern = false;
        }  else {
            if (locale == null || locale.equals(Locale.US)) {
                pattern.set(p);
            }
        }
        pattern.set(p, locale);
    }


    /**
     * Returns a long value representing the date in milliseconds since 1/1/1970,
     * adjusted for the precision given.
     * @param date the date to obtain the value from
     * @param precision the precision, similar to the <code>java.util.Calendar</code> constants, or the
     *        constants from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @return the date as a <code>long</code>
     */
    protected long getDateLongValue(Date date, int precision) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (precision) {
        // 'CENTURY' does not exist in Calendar, but does in FieldValueDateConstraint
        case FieldValueDateConstraint.CENTURY : {
            int year = calendar.get(Calendar.YEAR);
            year %= 100;
            calendar.set(Calendar.YEAR, year);
        }
        case FieldValueDateConstraint.YEAR: calendar.clear(Calendar.MONTH);
            // 'Quarter' does not exist in Calendar, but does in FieldValueDateConstraint
        case FieldValueDateConstraint.QUARTER : {
            int month = calendar.get(Calendar.MONTH);
            month %= 4;
            calendar.set(Calendar.MONTH, month);
        }
        case FieldValueDateConstraint.MONTH : ;
        case FieldValueDateConstraint.WEEK : calendar.clear(Calendar.DAY_OF_MONTH);
        case FieldValueDateConstraint.DAY_OF_MONTH : ;
        case FieldValueDateConstraint.DAY_OF_YEAR : ;
        case FieldValueDateConstraint.DAY_OF_WEEK : calendar.clear(Calendar.HOUR);
        case FieldValueDateConstraint.HOUR : calendar.clear(Calendar.MINUTE);
        case FieldValueDateConstraint.MINUTE : calendar.clear(Calendar.SECOND);
        case FieldValueDateConstraint.SECOND : calendar.clear(Calendar.MILLISECOND);
        }
        return calendar.getTimeInMillis();
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        if (value != null) {
            Date date = Casting.toDate(value);
            Date minimum = getMin();
            if (minimum != null) {
                long minValue = getDateLongValue(minimum, minPrecision);
                long dateValue = getDateLongValue(date, minPrecision);
                if (minValue > dateValue || (!minInclusive && minValue == dateValue)) {
                    errors = addError(errors, getMinConstraint(), value);
                }
            }
            Date maximum = getMax();
            if (maximum != null) {
                long maxValue = getDateLongValue(maximum, maxPrecision);
                long dateValue = getDateLongValue(date, maxPrecision);
                if (maxValue < dateValue || (!maxInclusive && maxValue == dateValue)) {
                    errors = addError(errors, getMaxConstraint(), value);
                }
            }
        }
        return errors;
    }

    public Object clone(String name) {
        DateTimeDataType clone = (DateTimeDataType) super.clone(name);
        clone.weakPattern = true;
        return clone;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getMin() != null) {
            buf.append(" min:" + getMin() + " " + getMinPrecision()).append(isMinInclusive() ? " inclusive" : " exclusive");
        }
        if (getMax() != null) {
            buf.append(" max:" + getMax() + " " + getMaxPrecision()).append(isMaxInclusive() ? " inclusive" : " exclusive");
        }

        buf.append(" " + pattern);

        return buf.toString();
    }

}
