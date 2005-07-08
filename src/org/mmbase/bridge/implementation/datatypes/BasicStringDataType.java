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
import org.mmbase.bridge.datatypes.StringDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicStringDataType.java,v 1.2 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.StringDataType
 * @since MMBase-1.8
 */
public class BasicStringDataType extends AbstractDataType implements StringDataType {

    protected int minLength = -1;
    protected int maxLength = -1;
    protected String pattern = null;

    /**
     * Constructor for string field.
     */
    public BasicStringDataType(String name) {
        super(name, String.class);
    }

    /**
     * Create a string field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicStringDataType(String name, StringDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_STRING;
    }

    public String getPattern() {
        return pattern;
    }

    public StringDataType setPattern(String value) {
        edit();
        pattern = value;
        return this;
    }

    public int getMinLength() {
        return minLength;
    }

    public StringDataType setMinLength(int value) {
        edit();
        minLength = value;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public StringDataType setMaxLength(int value) {
        edit();
        maxLength = value;
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        String stringValue = Casting.toString(value);
        if (minLength > 0) {
            if (stringValue == null || stringValue.length() < minLength) {
                throw new IllegalArgumentException("The value '"+stringValue+"' must be longer than " + minLength + " characters.");
            }
        }
        if (maxLength > 0) {
            if (stringValue != null && stringValue.length() > maxLength) {
                throw new IllegalArgumentException("The value '"+stringValue+"' must be smaller than " + maxLength + " characters.");
            }
        }
        if (pattern != null) {
            if (stringValue == null || !stringValue.matches(pattern)) {
                throw new IllegalArgumentException("The value '"+stringValue+"' should follow the regular expression '" + pattern + "'.");
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicStringDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        StringDataType stringField = (StringDataType)dataType;
        setMinLength(stringField.getMinLength());
        setMaxLength(stringField.getMaxLength());
        setPattern(stringField.getPattern());
    }

}
