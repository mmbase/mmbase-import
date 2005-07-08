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
import org.mmbase.bridge.datatypes.BinaryDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBinaryDataType.java,v 1.2 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BinaryDataType
 * @since MMBase-1.8
 */
public class BasicBinaryDataType extends AbstractDataType implements BinaryDataType {

    protected int maxLength = -1;
    protected int minLength = -1;

    /**
     * Constructor for binary field.
     */
    public BasicBinaryDataType(String name) {
        super(name, byte[].class);
    }

    /**
     * Create a binary field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicBinaryDataType(String name, BasicBinaryDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_BINARY;
    }

    public int getMinLength() {
        return minLength;
    }

    public BinaryDataType setMinLength(int value) {
        edit();
        minLength = value;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public BinaryDataType setMaxLength(int value) {
        edit();
        maxLength = value;
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        byte[] binaryValue = Casting.toByte(value);
        if (minLength > 0) {
            if (binaryValue == null || binaryValue.length < minLength) {
                throw new IllegalArgumentException("The value must be longer than " + minLength + " bytes.");
            }
        }
        if (maxLength > 0) {
            if (binaryValue != null && binaryValue.length > maxLength) {
                throw new IllegalArgumentException("The value must be smaller than " + maxLength + " bytes.");
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicBinaryDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        BinaryDataType binaryField = (BinaryDataType)dataType;
        setMinLength(binaryField.getMinLength());
        setMaxLength(binaryField.getMaxLength());
    }

}
