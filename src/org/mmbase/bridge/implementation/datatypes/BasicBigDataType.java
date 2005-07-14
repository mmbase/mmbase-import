/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.*;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBigDataType.java,v 1.4 2005-07-14 14:13:40 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BigDataType
 * @since MMBase-1.8
 */
abstract public class BasicBigDataType extends AbstractDataType implements BigDataType {

    public static final String PROPERTY_MINLENGTH = "minLength";
    public static final Integer PROPERTY_MINLENGTH_DEFAULT = new Integer(-1);

    public static final String PROPERTY_MAXLENGTH = "maxLength";
    public static final Integer PROPERTY_MAXLENGTH_DEFAULT = new Integer(-1);

    protected DataType.Property minLengthProperty = null;
    protected DataType.Property maxLengthProperty = null;


    /**
     * Constructor for bif type field.
     */
    public BasicBigDataType(String name, Class classType) {
        super(name, classType);
        minLengthProperty = createProperty(PROPERTY_MINLENGTH, PROPERTY_MINLENGTH_DEFAULT);
        maxLengthProperty = createProperty(PROPERTY_MAXLENGTH, PROPERTY_MAXLENGTH_DEFAULT);
    }

    public int getMinLength() {
        return Casting.toInt(getMinLengthProperty().getValue());
    }

    public DataType.Property getMinLengthProperty() {
        return minLengthProperty;
    }

    public DataType.Property setMinLength(int value) {
        return setProperty(getMinLengthProperty(), new Integer(value));
    }

    public int getMaxLength() {
        return Casting.toInt(getMaxLengthProperty().getValue());
    }

    public DataType.Property getMaxLengthProperty() {
        return maxLengthProperty;
    }

    public DataType.Property setMaxLength(int value) {
        return setProperty(getMaxLengthProperty(), new Integer(value));
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value != null) {
            int size = -1;
            if (this instanceof BinaryDataType) {
                byte[] binaryValue = Casting.toByte(value);
                size = binaryValue.length;
            } else {
                String stringValue = Casting.toString(value);
                size = stringValue.length();
            }
            int minLength = getMinLength();
            if (minLength > 0) {
                if (size < minLength) {
                    failOnValidate(getMinLengthProperty(), value, cloud);
                }
            }
            int maxLength = getMaxLength();
            if (maxLength > 0) {
                if (size > maxLength) {
                    failOnValidate(getMaxLengthProperty(), value, cloud);
                }
            }
        }
    }

    public Object clone(String name) {
        BasicBigDataType clone = (BasicBigDataType)super.clone(name);
        clone.minLengthProperty = (DataTypeProperty)getMinLengthProperty().clone(clone);
        clone.maxLengthProperty = (DataTypeProperty)getMaxLengthProperty().clone(clone);
        return clone;
    }

}
