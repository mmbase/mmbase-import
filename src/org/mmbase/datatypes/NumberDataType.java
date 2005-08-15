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
 * @version $Id: NumberDataType.java,v 1.5 2005-08-15 16:38:20 pierre Exp $
 * @since MMBase-1.8
 */
abstract public class NumberDataType extends DataType {

    public static final String PROPERTY_MIN = "min";
    public static final Number PROPERTY_MIN_DEFAULT = null;

    public static final String PROPERTY_MAX = "max";
    public static final Number PROPERTY_MAX_DEFAULT = null;

    protected DataType.Property minProperty;
    protected boolean minInclusive;
    protected DataType.Property maxProperty;
    protected boolean maxInclusive;

    // keys for use with error messages to retrive from the bundle
    private String minInclusiveErrorKey;
    private String minExclusiveErrorKey;
    private String maxInclusiveErrorKey;
    private String maxExclusiveErrorKey;

    /**
     * Constructor for Number field.
     */
    public NumberDataType(String name, Class classType) {
        super(name, classType);
    }

    public void erase() {
        super.erase();
        // Determine the key to retrieve an error message from a property's bundle
        minInclusiveErrorKey = getBaseTypeIdentifier() + ".minInclusive.error";
        minExclusiveErrorKey = getBaseTypeIdentifier() + ".minExclusive.error";
        maxInclusiveErrorKey = getBaseTypeIdentifier() + ".maxInclusive.error";
        maxExclusiveErrorKey = getBaseTypeIdentifier() + ".maxExclusive.error";

        minProperty = createProperty(PROPERTY_MIN, PROPERTY_MIN_DEFAULT);
        setMinInclusive(true);
        maxProperty = createProperty(PROPERTY_MAX, PROPERTY_MAX_DEFAULT);
        setMaxInclusive(true);
    }


    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof NumberDataType) {
            NumberDataType dataType = (NumberDataType)origin;
            minProperty = (DataType.Property)dataType.getMinProperty().clone(this);
            minInclusive = dataType.isMinInclusive();
            maxProperty = (DataType.Property)dataType.getMaxProperty().clone(this);
            maxInclusive = dataType.isMaxInclusive();
        }
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
        // change the key for the property error description to match the inclusive status
        if (minInclusive) {
            minProperty.getLocalizedErrorDescription().setKey(minInclusiveErrorKey);
        } else {
            minProperty.getLocalizedErrorDescription().setKey(minExclusiveErrorKey);
        }
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
        // change the key for the property error description to match the inclusive status
        if (maxInclusive) {
            maxProperty.getLocalizedErrorDescription().setKey(maxInclusiveErrorKey);
        } else {
            maxProperty.getLocalizedErrorDescription().setKey(maxExclusiveErrorKey);
        }
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

    public void validate(Object value, Field field, Cloud cloud) {
        super.validate(value, field, cloud);
        if (value != null) {
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
                if (maxValue < doubleValue || (!maxInclusive && maxValue == doubleValue)) {
                    failOnValidate(getMaxProperty(), value, cloud);
                }
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getMinValue() != null) {
            buf.append("min:" + getMinValue() + " ").append(isMinInclusive() ? " inclusive" : " exclusive").append("\n");
        }
        if (getMaxValue() != null) {
          buf.append("max:" + getMaxValue() + " ").append(isMaxInclusive() ? " inclusive" : " exclusive").append("\n");
        }
        return buf.toString();
    }

}
