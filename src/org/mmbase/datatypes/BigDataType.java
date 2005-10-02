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
 * @version $Id: BigDataType.java,v 1.9 2005-10-02 16:10:32 michiel Exp $
 * @since MMBase-1.8
 */
abstract public class BigDataType extends DataType {

    public static final String CONSTRAINT_MINLENGTH = "minLength";
    public static final Integer CONSTRAINT_MINLENGTH_DEFAULT = new Integer(-1);

    public static final String CONSTRAINT_MAXLENGTH = "maxLength";
    public static final Integer CONSTRAINT_MAXLENGTH_DEFAULT = new Integer(-1);

    private static final Logger log = Logging.getLoggerInstance(DataType.class);

    protected DataType.ValueConstraint minLengthConstraint;
    protected DataType.ValueConstraint maxLengthConstraint;


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
        minLengthConstraint = null;
        maxLengthConstraint = null;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof BigDataType) {
            BigDataType dataType = (BigDataType)origin;
            minLengthConstraint = inheritConstraint(dataType.minLengthConstraint);
            maxLengthConstraint = inheritConstraint(dataType.maxLengthConstraint);
        }
    }

    /**
     * Returns the minimum length of binary values for this datatype.
     * @return the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     */
    public int getMinLength() {
        if (minLengthConstraint == null) {
            return CONSTRAINT_MINLENGTH_DEFAULT.intValue();
        } else {
            return Casting.toInt(minLengthConstraint.getValue());
        }
    }

    /**
     * Returns the 'minLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType.ValueConstraint}
     */
    public DataType.ValueConstraint getMinLengthConstraint() {
        if (minLengthConstraint == null) minLengthConstraint = new ValueConstraint(CONSTRAINT_MINLENGTH, CONSTRAINT_MINLENGTH_DEFAULT);
        return minLengthConstraint;
    }

    /**
     * Sets the minimum length of binary values for this datatype.
     * @param value the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.ValueConstraint setMinLength(int value) {
        return getMinLengthConstraint().setValue(new Integer(value));
    }

    /**
     * Returns the maximum length of binary values for this datatype.
     * @return the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     */
    public int getMaxLength() {
        if (maxLengthConstraint == null) {
            return CONSTRAINT_MAXLENGTH_DEFAULT.intValue();
        } else {
            return Casting.toInt(getMaxLengthConstraint().getValue());
        }
    }

    /**
     * Returns the 'maxLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType.ValueConstraint}
     */
    public DataType.ValueConstraint getMaxLengthConstraint() {
        if (maxLengthConstraint == null) maxLengthConstraint = new ValueConstraint(CONSTRAINT_MAXLENGTH, CONSTRAINT_MAXLENGTH_DEFAULT);
        return maxLengthConstraint;
    }

    /**
     * Sets the maximum length of binary values for this datatype.
     * @param value the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.ValueConstraint setMaxLength(int value) {
        return getMaxLengthConstraint().setValue(new Integer(value));
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
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
                    errors = addError(errors, getMinLengthConstraint(), value);
                }
            }
            int maxLength = getMaxLength();
            if (maxLength > 0) {
                if (size > maxLength) {
                    errors = addError(errors, getMaxLengthConstraint(), value);
                }
            }
        }
        return errors;
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
