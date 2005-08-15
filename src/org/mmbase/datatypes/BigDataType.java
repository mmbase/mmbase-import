/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BigDataType.java,v 1.5 2005-08-15 16:38:20 pierre Exp $
 * @since MMBase-1.8
 */
abstract public class BigDataType extends DataType {

    public static final String PROPERTY_MINLENGTH = "minLength";
    public static final Integer PROPERTY_MINLENGTH_DEFAULT = new Integer(-1);

    public static final String PROPERTY_MAXLENGTH = "maxLength";
    public static final Integer PROPERTY_MAXLENGTH_DEFAULT = new Integer(-1);

    private static final Logger log = Logging.getLoggerInstance(DataType.class);

    protected DataType.Property minLengthProperty;
    protected DataType.Property maxLengthProperty;


    /**
     * Constructor for big data field.
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    public BigDataType(String name, Class classType) {
        super(name, classType);
    }

    public void erase() {
        super.erase();
        minLengthProperty = createProperty(PROPERTY_MINLENGTH, PROPERTY_MINLENGTH_DEFAULT);
        maxLengthProperty = createProperty(PROPERTY_MAXLENGTH, PROPERTY_MAXLENGTH_DEFAULT);
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof BigDataType) {
            BigDataType dataType = (BigDataType)origin;
            minLengthProperty = (DataType.Property)dataType.getMinLengthProperty().clone(this);
            maxLengthProperty = (DataType.Property)dataType.getMaxLengthProperty().clone(this);
        }
    }

    /**
     * Returns the minimum length of binary values for this datatype.
     * @return the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     */
    public int getMinLength() {
        return Casting.toInt(getMinLengthProperty().getValue());
    }

    /**
     * Returns the 'minLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMinLengthProperty() {
        return minLengthProperty;
    }

    /**
     * Sets the minimum length of binary values for this datatype.
     * @param length the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.Property setMinLength(int value) {
        return setProperty(getMinLengthProperty(), new Integer(value));
    }

    /**
     * Returns the maximum length of binary values for this datatype.
     * @return the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     */
    public int getMaxLength() {
        return Casting.toInt(getMaxLengthProperty().getValue());
    }

    /**
     * Returns the 'maxLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMaxLengthProperty() {
        return maxLengthProperty;
    }

    /**
     * Sets the maximum length of binary values for this datatype.
     * @param length the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.Property setMaxLength(int value) {
        return setProperty(getMaxLengthProperty(), new Integer(value));
    }

    public void validate(Object value, Field field, Cloud cloud) {
        super.validate(value, field, cloud);
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


    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getMinLength() > -1) {
            buf.append("minLength:" + getMinLength() + "\n");
        }
        if (getMaxLength() > -1) {
            buf.append("minLength:" + getMaxLength() + "\n");
        }
        return buf.toString();
    }

}
