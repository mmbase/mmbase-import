/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.DynamicDate;
import org.mmbase.util.logging.*;

/**
 * The date-time datatype further describes {@link java.util.Date} objects. The date can be
 * restricted to a certain period (using {@link #setMin}, {@link #setMax}, and {@link
 * org.mmbase.util.Casting#toDate}. The presentation logic can be specified using a pattern, see
 * {@link #getPattern}.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class DateTimeDataType extends ComparableDataType<Date> {

    public static final Date MIN_VALUE = new Date(Long.MIN_VALUE);
    public static final Date MAX_VALUE = new Date(Long.MAX_VALUE);

    private static final Logger log = Logging.getLoggerInstance(DateTimeDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    // see javadoc of DateTimeFormat
    private boolean weakPattern = true; // means, may not be changed, must be cloned before changing something
    private DateTimePattern pattern = DateTimePattern.DEFAULT;

    /**
     * Constructor for DateTime field.
     */
    public DateTimeDataType(String name) {
        super(name, Date.class);
        setMin(MIN_VALUE, true);
        setMax(MAX_VALUE, true);
    }

    @Override
    protected void xmlValue(org.w3c.dom.Element el, Object value) {
        if (value instanceof DynamicDate) {
            el.setAttribute("value", ((DynamicDate) value).getFormat());
            el.setAttribute("eval",  Casting.toString(value));
        } else {
            super.xmlValue(el, value);
        }
    }

    @Override
    public void setDefaultValue(Object o) {
        log.debug("Setting default value " + o);
        //super.setDefaultValue(Casting.toDate(o));
        super.setDefaultValue(o == null ? null : Casting.toDate(o));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void inheritProperties(BasicDataType origin) {
        super.inheritProperties(origin);
        if (origin instanceof DateTimeDataType) {
            DateTimeDataType dataType = (DateTimeDataType)origin;
            if (weakPattern) {
                pattern      = dataType.pattern;
            }
        }
    }

    @Override
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        if (value == null) return null;
        try {
            return DynamicDate.eval(Casting.toDate(value));
        } catch (Throwable t) {
            throw new CastException(t);
        }
    }

    /**
     * @return the minimum value as an <code>Date</code>, or very very long ago if there is no minimum.
     */
    public Date getMin() {
        Object min =  getMinRestriction().getValue();
        return min == null ? MIN_VALUE : Casting.toDate(min);
    }

    /**
     * @return the maximum value as an <code>Date</code>, or a date very very far in the future if there is no maximum.
     */
    public Date getMax() {
        Object max = getMaxRestriction().getValue();
        return max == null ? MAX_VALUE : Casting.toDate(max);
    }


    /**
     * The 'pattern' of a 'DateTime' value gives a <code>DateTimePattern</code> object which can be used as an
     * indication for presentation.
     *
     * Basicly, this can indicate whether the objects present e.g. only a date, only a time and whether e.g. this time includes seconds or not.
     *
     * <code>DateTimePattern</code> is actually a wrapper arround a pattern, and that is used here.
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


    @Override
    public DateTimeDataType clone(String name) {
        DateTimeDataType clone = (DateTimeDataType) super.clone(name);
        clone.weakPattern = true;
        return clone;
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.append(" " + pattern);
        return buf;
    }
}
