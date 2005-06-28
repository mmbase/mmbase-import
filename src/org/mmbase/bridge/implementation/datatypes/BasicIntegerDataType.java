/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.MMBaseType;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.datatypes.IntegerDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicIntegerDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.IntegerDataType
 * @since MMBase-1.8
 */
public class BasicIntegerDataType extends Parameter implements IntegerDataType {

    protected Integer minimum = null;
    protected boolean minimumInclusive = true;
    protected Integer maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for integer field.
     */
    public BasicIntegerDataType(String name) {
        super(name, MMBaseType.TYPE_INTEGER);
    }

    /**
     * Create a integer field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicIntegerDataType(String name, BasicIntegerDataType dataType) {
        super(name,dataType);
    }

    public Integer getMinimum() {
        return minimum;
    }

    public boolean getMinimumInclusive() {
        return minimumInclusive;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public boolean getMaximumInclusive() {
        return maximumInclusive;
    }

    public IntegerDataType setMinimum(Integer value) {
        edit();
        minimum = value;
        return this;
    }

    public IntegerDataType setMinimumInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public IntegerDataType setMinimum(Integer value, boolean inclusive) {
        setMinimum(value);
        setMinimumInclusive(inclusive);
        return this;
    }

    public IntegerDataType setMaximum(Integer value) {
        edit();
        maximum = value;
        return this;
    }

    public IntegerDataType setMaximumInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public IntegerDataType setMaximum(Integer value, boolean inclusive) {
        setMaximum(value);
        setMaximumInclusive(inclusive);
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
        setMinimum(integerField.getMinimum());
        setMinimumInclusive(integerField.getMinimumInclusive());
        setMaximum(integerField.getMaximum());
        setMaximumInclusive(integerField.getMaximumInclusive());
    }

}
