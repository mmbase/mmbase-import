/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.BigDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBigDataType.java,v 1.2 2005-07-12 15:03:35 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BigDataType
 * @since MMBase-1.8
 */
abstract public class BasicBigDataType extends AbstractDataType implements BigDataType {

    public static final String PROPERTY_MINLENGTH = "minLength";
    public static final Integer PROPERTY_MINLENGTH_DEFAULT = new Integer(-1);

    public static final String PROPERTY_MAXLENGTH = "maxLength";
    public static final Integer PROPERTY_MAXLENGTH_DEFAULT = new Integer(-1);

    /**
     * Constructor for bif type field.
     */
    public BasicBigDataType(String name, Class classType) {
        super(name, classType);
    }

    public int getMinLength() {
        return Casting.toInt(getMinLengthProperty().getValue());
    }

    public DataType.Property getMinLengthProperty() {
        return getProperty(PROPERTY_MINLENGTH, PROPERTY_MINLENGTH_DEFAULT);
    }

    public DataType.Property setMinLength(int value) {
        return setProperty(PROPERTY_MINLENGTH, new Integer(value));
    }

    public int getMaxLength() {
        return Casting.toInt(getMaxLengthProperty().getValue());
    }

    public DataType.Property getMaxLengthProperty() {
        return getProperty(PROPERTY_MAXLENGTH, PROPERTY_MAXLENGTH_DEFAULT);
    }

    public DataType.Property setMaxLength(int value) {
        return setProperty(PROPERTY_MINLENGTH, new Integer(value));
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value != null) {
            int size = -1;
            if (getBaseType() == Field.TYPE_STRING) {
                String stringValue = Casting.toString(value);
                size = stringValue.length();
            } else {
                byte[] binaryValue = Casting.toByte(value);
                size = binaryValue.length;
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

}
