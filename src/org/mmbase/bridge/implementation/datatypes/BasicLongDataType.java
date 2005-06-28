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
import org.mmbase.bridge.datatypes.LongDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicLongDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.LongDataType
 * @since MMBase-1.8
 */
public class BasicLongDataType extends Parameter implements LongDataType {

    protected Long minimum = null;
    protected boolean minimumInclusive = true;
    protected Long maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for long field.
     */
    public BasicLongDataType(String name) {
        super(name, MMBaseType.TYPE_LONG);
    }

    /**
     * Create a long field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicLongDataType(String name, BasicLongDataType dataType) {
        super(name,dataType);
    }

    public Long getMinimum() {
        return minimum;
    }

    public boolean getMinimumInclusive() {
        return minimumInclusive;
    }

    public Long getMaximum() {
        return maximum;
    }

    public boolean getMaximumInclusive() {
        return maximumInclusive;
    }

    public LongDataType setMinimum(Long value) {
        edit();
        minimum = value;
        return this;
    }

    public LongDataType setMinimumInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public LongDataType setMinimum(Long value, boolean inclusive) {
        setMinimum(value);
        setMinimumInclusive(inclusive);
        return this;
    }

    public LongDataType setMaximum(Long value) {
        edit();
        maximum = value;
        return this;
    }

    public LongDataType setMaximumInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public LongDataType setMaximum(Long value, boolean inclusive) {
        setMaximum(value);
        setMaximumInclusive(inclusive);
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        long longValue = Casting.toLong(value);
        if (minimum != null) {
            if (minimumInclusive) {
                if (minimum.longValue() > longValue) {
                    throw new IllegalArgumentException("The value "+longValue+" may not be less than the minimum value "+minimum.longValue());
                }
            } else {
                if (minimum.longValue() >= longValue) {
                    throw new IllegalArgumentException("The value "+longValue+" may not be less than or equal to the minimum value "+minimum.longValue());
                }
            }
        }
        if (maximum != null) {
            if (maximumInclusive) {
                if (maximum.longValue() < longValue) {
                    throw new IllegalArgumentException("The value "+longValue+" may not be greater than the maximum value "+maximum.longValue());
                }
            } else {
                if (maximum.longValue() <= longValue) {
                    throw new IllegalArgumentException("The value "+longValue+" may not be greater than or equal to the maximum value "+maximum.longValue());
                }
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicLongDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        LongDataType longField = (LongDataType)dataType;
        setMinimum(longField.getMinimum());
        setMinimumInclusive(longField.getMinimumInclusive());
        setMaximum(longField.getMaximum());
        setMaximumInclusive(longField.getMaximumInclusive());
    }

}
