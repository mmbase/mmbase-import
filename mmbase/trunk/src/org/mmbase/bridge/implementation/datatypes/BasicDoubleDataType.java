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
import org.mmbase.bridge.datatypes.DoubleDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDoubleDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.DoubleDataType
 * @since MMBase-1.8
 */
public class BasicDoubleDataType extends Parameter implements DoubleDataType {

    protected Double minimum = null;
    protected boolean minimumInclusive = true;
    protected Double maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for Double field.
     */
    public BasicDoubleDataType(String name) {
        super(name, MMBaseType.TYPE_DOUBLE);
    }

    /**
     * Create a Double field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicDoubleDataType(String name, BasicDoubleDataType dataType) {
        super(name,dataType);
    }

    public Double getMinimum() {
        return minimum;
    }

    public boolean getMinimumInclusive() {
        return minimumInclusive;
    }

    public Double getMaximum() {
        return maximum;
    }

    public boolean getMaximumInclusive() {
        return maximumInclusive;
    }

    public DoubleDataType setMinimum(Double value) {
        edit();
        minimum = value;
        return this;
    }

    public DoubleDataType setMinimumInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public DoubleDataType setMinimum(Double value, boolean inclusive) {
        setMinimum(value);
        setMinimumInclusive(inclusive);
        return this;
    }

    public DoubleDataType setMaximum(Double value) {
        edit();
        maximum = value;
        return this;
    }

    public DoubleDataType setMaximumInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public DoubleDataType setMaximum(Double value, boolean inclusive) {
        setMaximum(value);
        setMaximumInclusive(inclusive);
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        double doubleValue = Casting.toDouble(value);
        if (minimum != null) {
            if (minimumInclusive) {
                if (minimum.doubleValue() > doubleValue) {
                    throw new IllegalArgumentException("The value "+doubleValue+" may not be less than the minimum value "+minimum.doubleValue());
                }
            } else {
                if (minimum.doubleValue() >= doubleValue) {
                    throw new IllegalArgumentException("The value "+doubleValue+" may not be less than or equal to the minimum value "+minimum.doubleValue());
                }
            }
        }
        if (maximum != null) {
            if (maximumInclusive) {
                if (maximum.doubleValue() < doubleValue) {
                    throw new IllegalArgumentException("The value "+doubleValue+" may not be greater than the maximum value "+maximum.doubleValue());
                }
            } else {
                if (maximum.doubleValue() <= doubleValue) {
                    throw new IllegalArgumentException("The value "+doubleValue+" may not be greater than or equal to the maximum value "+maximum.doubleValue());
                }
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicDoubleDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        DoubleDataType doubleField = (DoubleDataType)dataType;
        setMinimum(doubleField.getMinimum());
        setMinimumInclusive(doubleField.getMinimumInclusive());
        setMaximum(doubleField.getMaximum());
        setMaximumInclusive(doubleField.getMaximumInclusive());
    }

}
