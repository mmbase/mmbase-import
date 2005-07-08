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
import org.mmbase.bridge.datatypes.IntegerDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicIntegerDataType.java,v 1.3 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.IntegerDataType
 * @since MMBase-1.8
 */
public class BasicIntegerDataType extends AbstractDataType implements IntegerDataType {

    protected Integer minimum = null;
    protected boolean minimumInclusive = true;
    protected Integer maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for integer field.
     */
    public BasicIntegerDataType(String name) {
        super(name, Integer.class);
    }

    /**
     * Create a integer field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicIntegerDataType(String name, BasicIntegerDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_INTEGER;
    }

    public Integer getMin() {
        return minimum;
    }

    public boolean getMinInclusive() {
        return minimumInclusive;
    }

    public Integer getMax() {
        return maximum;
    }

    public boolean getMaxInclusive() {
        return maximumInclusive;
    }

    public IntegerDataType setMin(Integer value) {
        edit();
        minimum = value;
        return this;
    }

    public IntegerDataType setMinInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public IntegerDataType setMin(Integer value, boolean inclusive) {
        setMin(value);
        setMinInclusive(inclusive);
        return this;
    }

    public IntegerDataType setMax(Integer value) {
        edit();
        maximum = value;
        return this;
    }

    public IntegerDataType setMaxInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public IntegerDataType setMax(Integer value, boolean inclusive) {
        setMax(value);
        setMaxInclusive(inclusive);
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        int intValue = Casting.toInt(value);
        if (minimum != null) {
            if (minimumInclusive) {
                if (minimum.intValue() > intValue) {
                    throw new IllegalArgumentException("The value "+intValue+" may not be less than the minimum value "+minimum.intValue());
                }
            } else {
                if (minimum.intValue() >= intValue) {
                    throw new IllegalArgumentException("The value "+intValue+" may not be less than or equal to the minimum value "+minimum.intValue());
                }
            }
        }
        if (maximum != null) {
            if (maximumInclusive) {
                if (maximum.intValue() < intValue) {
                    throw new IllegalArgumentException("The value "+intValue+" may not be greater than the maximum value "+maximum.intValue());
                }
            } else {
                if (maximum.intValue() <= intValue) {
                    throw new IllegalArgumentException("The value "+intValue+" may not be greater than or equal to the maximum value "+maximum.intValue());
                }
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicIntegerDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        IntegerDataType integerField = (IntegerDataType)dataType;
        setMin(integerField.getMin());
        setMinInclusive(integerField.getMinInclusive());
        setMax(integerField.getMax());
        setMaxInclusive(integerField.getMaxInclusive());
    }

}
