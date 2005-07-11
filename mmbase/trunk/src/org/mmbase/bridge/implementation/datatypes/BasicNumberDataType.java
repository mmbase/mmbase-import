/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.NumberDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicNumberDataType.java,v 1.1 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.NumberDataType
 * @since MMBase-1.8
 */
abstract public class BasicNumberDataType extends AbstractDataType implements NumberDataType {

    public static final String PROPERTY_MININCLUSIVE = "minInclusive";
    public static final String PROPERTY_MINEXCLUSIVE = "minExclusive";
    public static final Number PROPERTY_MIN_DEFAULT = null;

    public static final String PROPERTY_MAXINCLUSIVE = "minInclusive";
    public static final String PROPERTY_MAXEXCLUSIVE = "minExclusive";
    public static final Number PROPERTY_MAX_DEFAULT = null;

    protected boolean minInclusive = true;
    protected boolean maxInclusive = true;

    /**
     * Constructor for Number field.
     */
    public BasicNumberDataType(String name, Class classType) {
        super(name, classType);
    }

    /**
     * Create a Number field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    public BasicNumberDataType(String name, DataType dataType) {
        super(name,dataType);
    }

    protected Number getMinValue() {
        return (Number)getMinProperty().getValue();
    }

    public DataType.Property getMinProperty() {
        if (minInclusive) {
            return getProperty(PROPERTY_MININCLUSIVE, PROPERTY_MIN_DEFAULT);
        } else {
            return getProperty(PROPERTY_MINEXCLUSIVE, PROPERTY_MIN_DEFAULT);
        }
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    protected Number getMaxValue() {
        return (Number)getMaxProperty().getValue();
    }

    public DataType.Property getMaxProperty() {
        if (maxInclusive) {
            return getProperty(PROPERTY_MAXINCLUSIVE, PROPERTY_MAX_DEFAULT);
        } else {
            return getProperty(PROPERTY_MAXEXCLUSIVE, PROPERTY_MAX_DEFAULT);
        }
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public DataType.Property setMin(Number value) {
        if (minInclusive) {
            return setProperty(PROPERTY_MININCLUSIVE, value);
        } else {
            return setProperty(PROPERTY_MINEXCLUSIVE, value);
        }
    }

    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    public DataType.Property setMin(Number value, boolean inclusive) {
        edit();
        setMinInclusive(inclusive);
        return setMin(value);
    }

    public DataType.Property setMax(Number value) {
        if (maxInclusive) {
            return setProperty(PROPERTY_MAXINCLUSIVE, value);
        } else {
            return setProperty(PROPERTY_MAXEXCLUSIVE, value);
        }
    }

    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    public DataType.Property setMax(Number value, boolean inclusive) {
        edit();
        setMaxInclusive(inclusive);
        return setMax(value);
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value == null) {
            double doubleValue = Casting.toDouble(value);
            Number minimum = getMinValue();
            if (minimum != null) {
                double minValue = minimum.doubleValue();
                if (minValue > doubleValue || (!minInclusive && minValue == doubleValue)) {
                    failOnValidate(getMinProperty(), value, cloud);
                }
            }
            Number maximum = getMaxValue();
            if (maximum != null) {
                double maxValue = maximum.doubleValue();
                if (maxValue > doubleValue || (!minInclusive && maxValue == doubleValue)) {
                    failOnValidate(getMaxProperty(), value, cloud);
                }
            }
        }
    }

}
