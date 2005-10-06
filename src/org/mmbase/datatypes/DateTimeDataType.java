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
 * @version $Id: DateTimeDataType.java,v 1.20 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class DateTimeDataType extends ComparableDataType {

    public static final Date MIN_VALUE = new Date(Long.MIN_VALUE);
    public static final Date MAX_VALUE = new Date(Long.MAX_VALUE);

    private static final Logger log = Logging.getLoggerInstance(DateTimeDataType.class);

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

    public void inherit(BasicDataType origin) {
        super.inherit(origin);
        if (origin instanceof DateTimeDataType) {
            DateTimeDataType dataType = (DateTimeDataType)origin;
            if (weakPattern) {
                pattern      = dataType.pattern;
            }
        }
    }


    /**
     * @return the minimum value as an <code>Date</code>, or very very long ago if there is no minimum.
     */
    public Date getMin() {
        Date min = (Date) getMinConstraint().getValue();
        return min == null ? MIN_VALUE : min;
    }

    /**
     * @return the maximum value as an <code>Date</code>, or a very very in the future if there is no maximum.
     */
    public Date getMax() {
        Date max = (Date) getMaxConstraint().getValue();
        return max == null ? MAX_VALUE : max;
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


    public Object clone(String name) {
        DateTimeDataType clone = (DateTimeDataType) super.clone(name);
        clone.weakPattern = true;
        return clone;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append(" " + pattern);

        return buf.toString();
    }
}
