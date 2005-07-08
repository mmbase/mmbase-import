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
import org.mmbase.bridge.datatypes.LongDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicLongDataType.java,v 1.3 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.LongDataType
 * @since MMBase-1.8
 */
public class BasicLongDataType extends AbstractDataType implements LongDataType {

    protected Long minimum = null;
    protected boolean minimumInclusive = true;
    protected Long maximum = null;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for long field.
     */
    public BasicLongDataType(String name) {
        super(name, Long.class);
    }

    /**
     * Create a long field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicLongDataType(String name, BasicLongDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_LONG;
    }

    public Long getMin() {
        return minimum;
    }

    public boolean getMinInclusive() {
        return minimumInclusive;
    }

    public Long getMax() {
        return maximum;
    }

    public boolean getMaxInclusive() {
        return maximumInclusive;
    }

    public LongDataType setMin(Long value) {
        edit();
        minimum = value;
        return this;
    }

    public LongDataType setMinInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public LongDataType setMin(Long value, boolean inclusive) {
        setMin(value);
        setMinInclusive(inclusive);
        return this;
    }

    public LongDataType setMax(Long value) {
        edit();
        maximum = value;
        return this;
    }

    public LongDataType setMaxInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public LongDataType setMax(Long value, boolean inclusive) {
        setMax(value);
        setMaxInclusive(inclusive);
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
        setMin(longField.getMin());
        setMinInclusive(longField.getMinInclusive());
        setMax(longField.getMax());
        setMaxInclusive(longField.getMaxInclusive());
    }

}
