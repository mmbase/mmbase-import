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

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: NumberDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
abstract public class NumberDataType extends DataType {

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
    public NumberDataType(String name, Class classType) {
        super(name, classType);
        minProperty = createProperty(PROPERTY_MIN, PROPERTY_MIN_DEFAULT);
        maxProperty = createProperty(PROPERTY_MAX, PROPERTY_MAX_DEFAULT);
    }

    protected Number getMinValue() {
        return (Number)getMinProperty().getValue();
    }

    /**
     * Returns the minimum value for this data type.
     * @return the property defining the minimum value
     */
    public DataType.Property getMinProperty() {
        return minProperty;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minInclusive;
    }

    protected Number getMaxValue() {
        return (Number)getMaxProperty().getValue();
    }

    /**
     * Returns the maximum value for this data type.
     * @return the property defining the maximum value
     */
    public DataType.Property getMaxProperty() {
        return maxProperty;
    }

    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    /**
     * Sets the minimum Number value for this data type.
     * @param length the minimum as an <code>Number</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Number value) {
        return setProperty(getMinProperty(), value);
    }

    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    /**
     * Sets the minimum Number value for this data type.
     * @param length the minimum as an <code>Number</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Number value, boolean inclusive) {
        edit();
        setMinInclusive(inclusive);
        return setMin(value);
    }

    /**
     * Sets the maximum Number value for this data type.
     * @param length the maximum as an <code>Number</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMax(Number value) {
        return setProperty(getMaxProperty(), value);
    }

    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    /**
     * Sets the maximum Number value for this data type.
     * @param length the maximum as an <code>Number</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
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
        NumberDataType clone = (NumberDataType)super.clone(name);
        clone.minProperty = (DataType.Property)getMinProperty().clone(clone);
        clone.maxProperty = (DataType.Property)getMaxProperty().clone(clone);
        return clone;
    }

}
