/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.DateTimeDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDateTimeDataType.java,v 1.6 2005-07-14 11:37:53 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.DateTimeDataType
 * @since MMBase-1.8
 */
public class BasicDateTimeDataType extends AbstractDataType implements DateTimeDataType {

    public static final String PROPERTY_MININCLUSIVE = "minInclusive";
    public static final String PROPERTY_MINEXCLUSIVE = "minExclusive";
    public static final Number PROPERTY_MIN_DEFAULT = null;

    public static final String PROPERTY_MAXINCLUSIVE = "minInclusive";
    public static final String PROPERTY_MAXEXCLUSIVE = "minExclusive";
    public static final Number PROPERTY_MAX_DEFAULT = null;

    protected int minPrecision = Calendar.SECOND;
    protected boolean minInclusive = true;
    protected int maxPrecision = Calendar.SECOND;
    protected boolean maxInclusive = true;

    /**
     * Constructor for DateTime field.
     */
    public BasicDateTimeDataType(String name) {
        super(name, Date.class);
    }

    public Date getMin() {
        return (Date)getMinProperty().getValue();
    }

    public DataType.Property getMinProperty() {
        if (minInclusive) {
            return getProperty(PROPERTY_MININCLUSIVE, PROPERTY_MIN_DEFAULT);
        } else {
            return getProperty(PROPERTY_MINEXCLUSIVE, PROPERTY_MIN_DEFAULT);
        }
    }

    public int getMinPrecision() {
        return minPrecision;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public Date getMax() {
        return (Date)getMaxProperty().getValue();
    }

    public DataType.Property getMaxProperty() {
        if (maxInclusive) {
            return getProperty(PROPERTY_MAXINCLUSIVE, PROPERTY_MAX_DEFAULT);
        } else {
            return getProperty(PROPERTY_MAXEXCLUSIVE, PROPERTY_MAX_DEFAULT);
        }
    }

    public int getMaxPrecision() {
        return maxPrecision;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public DataType.Property setMin(Date value) {
        return setProperty(PROPERTY_MININCLUSIVE, value);
    }

    public void setMinPrecision(int precision) {
        minPrecision = precision;
    }

    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    public DataType.Property setMin(Date value, int precision, boolean inclusive) {
        edit();
        setMinPrecision(precision);
        setMinInclusive(inclusive);
        return setMin(value);
    }

    public DataType.Property setMax(Date value) {
        return setProperty(PROPERTY_MAXINCLUSIVE, value);
    }

    public void setMaxPrecision(int precision) {
        maxPrecision = precision;
    }

    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    public DataType.Property setMax(Date value, int precision, boolean inclusive) {
        edit();
        setMaxPrecision(precision);
        setMaxInclusive(inclusive);
        return setMax(value);
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value != null) {
            Date dateValue = Casting.toDate(value);
            // Todo: check on mindate/max date, taking into account precision and inclusiveness
        }
    }

}
