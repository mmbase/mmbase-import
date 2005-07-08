/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.datatypes.FloatDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicFloatDataType.java,v 1.3 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.FloatDataType
 * @since MMBase-1.8
 */
public class BasicFloatDataType extends AbstractDataType implements FloatDataType {

    protected Float minimum = null;
    protected boolean minimumInclusive = true;
    protected Float maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for Float field.
     */
    public BasicFloatDataType(String name) {
        super(name, Float.class);
    }

    /**
     * Create a Float field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicFloatDataType(String name, BasicFloatDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_FLOAT;
    }

    public Float getMin() {
        return minimum;
    }

    public boolean getMinInclusive() {
        return minimumInclusive;
    }

    public Float getMax() {
        return maximum;
    }

    public boolean getMaxInclusive() {
        return maximumInclusive;
    }

    public FloatDataType setMin(Float value) {
        edit();
        minimum = value;
        return this;
    }

    public FloatDataType setMinInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public FloatDataType setMin(Float value, boolean inclusive) {
        setMin(value);
        setMinInclusive(inclusive);
        return this;
    }

    public FloatDataType setMax(Float value) {
        edit();
        maximum = value;
        return this;
    }

    public FloatDataType setMaxInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public FloatDataType setMax(Float value, boolean inclusive) {
        setMax(value);
        setMaxInclusive(inclusive);
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        float floatValue = Casting.toFloat(value);
        if (minimum != null) {
            if (minimumInclusive) {
                if (minimum.floatValue() > floatValue) {
                    throw new IllegalArgumentException("The value "+floatValue+" may not be less than the minimum value "+minimum.floatValue());
                }
            } else {
                if (minimum.floatValue() >= floatValue) {
                    throw new IllegalArgumentException("The value "+floatValue+" may not be less than or equal to the minimum value "+minimum.floatValue());
                }
            }
        }
        if (maximum != null) {
            if (maximumInclusive) {
                if (maximum.floatValue() < floatValue) {
                    throw new IllegalArgumentException("The value "+floatValue+" may not be greater than the maximum value "+maximum.floatValue());
                }
            } else {
                if (maximum.floatValue() <= floatValue) {
                    throw new IllegalArgumentException("The value "+floatValue+" may not be greater than or equal to the maximum value "+maximum.floatValue());
                }
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicFloatDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        FloatDataType floatField = (FloatDataType)dataType;
        setMin(floatField.getMin());
        setMinInclusive(floatField.getMinInclusive());
        setMax(floatField.getMax());
        setMaxInclusive(floatField.getMaxInclusive());
    }

}
