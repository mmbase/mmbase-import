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
import org.mmbase.util.DynamicDate
;
import org.mmbase.util.logging.*;

/**
 * The date-time datatype further describes <code>java.util.Date</code> objects. The date can be
 * constrainted to a certain period (using {@link #setMin}, {@link #setMax}, and {@link
 * org.mmbase.util.Casting#toDate}. The presentation logic can be specified using a pattern, see {@link #getPattern}.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: DateTimeDataType.java,v 1.19 2005-10-04 17:17:34 michiel Exp $
 * @since MMBase-1.8
 */
public class DateTimeDataType extends DataType {

    private static final Logger log = Logging.getLoggerInstance(DateTimeDataType.class);

    protected MinConstraint minConstraint  = new MinConstraint(true);
    protected MaxConstraint maxConstraint  = new MaxConstraint(true);

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

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof DateTimeDataType) {
            DateTimeDataType dataType = (DateTimeDataType)origin;
            minConstraint  = new MinConstraint(dataType.minConstraint);
            maxConstraint  = new MaxConstraint(dataType.maxConstraint);
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
        return (Date)minConstraint.getValue();
    }

    /**
     * Returns the minimum value for this data type.
     * @return the minimum value as an <code>Number</code>, or <code>null</code> if there is no minimum.
     */
    public MinConstraint getMinConstraint() {
        return minConstraint;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minConstraint.isInclusive();
    }

    /**
     * Returns the maximum value for this data type.
     * @return the maximum value as an <code>Date</code>, or <code>null</code> if there is no maximum.
     */
    public Date getMax() {
        return (Date)maxConstraint.getValue();
    }

    /**
     * Returns the maximum value for this data type.
     * @return the property defining the maximum value
     */
    public MaxConstraint getMaxConstraint() {
        return maxConstraint;
    }


    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxConstraint.isInclusive();
    }


    /**
     * Sets the minimum Date value for this data type.
     * @param value the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMin(Date value, boolean inclusive) {
        edit();
        if (inclusive != minConstraint.isInclusive()) minConstraint = new MinConstraint(inclusive);
        return minConstraint.setValue(value);
    }


    /**
     * Sets the maximum Date value for this data type.
     * @param value the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMax(Date value, boolean inclusive) {
        edit();
        if (inclusive != maxConstraint.isInclusive()) maxConstraint = new MaxConstraint(inclusive);
        return getMaxConstraint().setValue(value);
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
    /**
     * Set the pattern for a certain Locale.
     * See also {@link #getPattern}.
     */
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


    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        errors = minConstraint.validate(errors, value, node, field);
        errors = maxConstraint.validate(errors, value, node, field);
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
            buf.append(" min: " + getMinConstraint());
        }
        if (getMax() != null) {
            buf.append(" max:" + getMaxConstraint());
        }

        buf.append(" " + pattern);

        return buf.toString();
    }

    private class MinConstraint extends ValueConstraint {
        private boolean inclusive;
        MinConstraint(MinConstraint source) {
            super(source.getName());
            inclusive = source.inclusive;
            inherit(source);            
        }
        MinConstraint(boolean inc) {
            super("min" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }
        public boolean valid(Object value, Node node, Field field) {
            Date date = Casting.toDate(value);
            Date minimum = DynamicDate.eval((Date) getValue());
            if (minimum == null) return true;
            if (inclusive && (date.equals(minimum))) return true;
            return date.after(minimum);
        }
        public boolean isInclusive() {
            return inclusive;
        }
    }
    private class MaxConstraint extends ValueConstraint {
        private boolean inclusive;
        MaxConstraint(MaxConstraint source) {
            super(source.getName());
            inclusive = source.inclusive;
            inherit(source);            
        }
        MaxConstraint(boolean inc) {
            super("max" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }
        public boolean valid(Object value, Node node, Field field) {
            Date date = Casting.toDate(value);
            Date maximum = DynamicDate.eval((Date) getValue());
            if (maximum == null) return true;
            if (inclusive && (date.equals(maximum))) return true;           
            return date.before(maximum);
        }
        
        public boolean isInclusive() {
            return inclusive;
        }
    }
    

}
