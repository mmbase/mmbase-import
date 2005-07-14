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
 * @version $Id: BasicNumberDataType.java,v 1.3 2005-07-14 14:13:40 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.NumberDataType
 * @since MMBase-1.8
 */
abstract public class BasicNumberDataType extends AbstractDataType implements NumberDataType {

    public static final String PROPERTY_MIN = "min";
    public static final Number PROPERTY_MIN_DEFAULT = null;

    public static final String PROPERTY_MAX = "min";
    public static final Number PROPERTY_MAX_DEFAULT = null;

    protected DataType.Property minProperty = null;
    protected boolean minInclusive = true;
    protected DataType.Property maxProperty = null;
    protected boolean maxInclusive = true;

    /**
     * Constructor for Number field.
     */
    public BasicNumberDataType(String name, Class classType) {
        super(name, classType);
        minProperty = createProperty(PROPERTY_MIN, PROPERTY_MIN_DEFAULT);
        maxProperty = createProperty(PROPERTY_MAX, PROPERTY_MAX_DEFAULT);
    }

    protected Number getMinValue() {
        return (Number)getMinProperty().getValue();
    }

    public DataType.Property getMinProperty() {
        return minProperty;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    protected Number getMaxValue() {
        return (Number)getMaxProperty().getValue();
    }

    public DataType.Property getMaxProperty() {
        return maxProperty;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public DataType.Property setMin(Number value) {
        return setProperty(getMinProperty(), value);
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
        return setProperty(getMaxProperty(), value);
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

    public Object clone(String name) {
        BasicNumberDataType clone = (BasicNumberDataType)super.clone(name);
        clone.minProperty = (DataTypeProperty)getMinProperty().clone(clone);
        clone.maxProperty = (DataTypeProperty)getMaxProperty().clone(clone);
        return clone;
    }

}
