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
 * @version $Id: NumberDataType.java,v 1.9 2005-09-06 21:11:30 michiel Exp $
 * @since MMBase-1.8
 */
abstract public class NumberDataType extends DataType {

    public static final String CONSTRAINT_MIN = "min";
    public static final Number CONSTRAINT_MIN_DEFAULT = null;

    public static final String CONSTRAINT_MAX = "max";
    public static final Number CONSTRAINT_MAX_DEFAULT = null;

    protected DataType.ValueConstraint minConstraint;
    protected boolean minInclusive;
    protected DataType.ValueConstraint maxConstraint;
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

        minConstraint = null;
        setMinInclusive(true);
        maxConstraint = null;
        setMaxInclusive(true);
    }


    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof NumberDataType) {
            NumberDataType dataType = (NumberDataType)origin;
            minConstraint = inheritConstraint(dataType.minConstraint);
            minInclusive = dataType.isMinInclusive();
            maxConstraint = inheritConstraint(dataType.maxConstraint);
            maxInclusive = dataType.isMaxInclusive();
        }
    }

    protected Number getMinValue() {
        if (minConstraint == null) {
            return CONSTRAINT_MIN_DEFAULT;
        } else {
            return (Number)minConstraint.getValue();
        }
    }

    /**
     * Returns the minimum value for this data type.
     * @return the property defining the minimum value
     */
    public DataType.ValueConstraint getMinConstraint() {
        if (minConstraint == null) minConstraint = new ValueConstraint(CONSTRAINT_MIN, CONSTRAINT_MIN_DEFAULT);
        // change the key for the property error description to match the inclusive status
        if (minInclusive) {
            minConstraint.getErrorDescription().setKey(minInclusiveErrorKey);
        } else {
            minConstraint.getErrorDescription().setKey(minExclusiveErrorKey);
        }
        return minConstraint;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minInclusive;
    }

    protected Number getMaxValue() {
        if (maxConstraint == null) {
            return CONSTRAINT_MAX_DEFAULT;
        } else {
            return (Number)maxConstraint.getValue();
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
            maxConstraint.getErrorDescription().setKey(maxInclusiveErrorKey);
        } else {
            maxConstraint.getErrorDescription().setKey(maxExclusiveErrorKey);
        }
        return maxConstraint;
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
    public DataType.ValueConstraint setMin(Number value) {
        return getMinConstraint().setValue(value);
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
    public DataType.ValueConstraint setMin(Number value, boolean inclusive) {
        edit();
        setMinInclusive(inclusive);
        return setMin(value);
    }

    /**
     * Sets the maximum Number value for this data type.
     * @param length the maximum as an <code>Number</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMax(Number value) {
        return getMaxConstraint().setValue(value);
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
    public DataType.ValueConstraint setMax(Number value, boolean inclusive) {
        edit();
        setMaxInclusive(inclusive);
        return setMax(value);
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        if (value != null) {
            double doubleValue = Casting.toDouble(value);
            Number minimum = getMinValue();
            if (minimum != null) {
                double minValue = minimum.doubleValue();
                if (minValue > doubleValue || (!minInclusive && minValue == doubleValue)) {
                    errors = addError(errors, getMinConstraint(), value);
                }
            }
            Number maximum = getMaxValue();
            if (maximum != null) {
                double maxValue = maximum.doubleValue();
                if (maxValue < doubleValue || (!maxInclusive && maxValue == doubleValue)) {
                    errors = addError(errors, getMaxConstraint(), value);
                }
            }
        }
        return errors;
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
