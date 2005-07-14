/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;
import org.mmbase.bridge.util.DataTypes;
import org.mmbase.util.LocalizedString;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.9 2005-07-14 11:37:53 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface DataType extends Comparable, Descriptor, Cloneable {

    // DataTypes for base MMBase field types
    public static final DataType INTEGER  = DataTypes.getDataType(Field.TYPE_INTEGER);
    public static final DataType LONG     = DataTypes.getDataType(Field.TYPE_LONG);
    public static final DataType FLOAT    = DataTypes.getDataType(Field.TYPE_FLOAT);
    public static final DataType DOUBLE   = DataTypes.getDataType(Field.TYPE_DOUBLE);
    public static final DataType STRING   = DataTypes.getDataType(Field.TYPE_STRING);
    public static final DataType XML      = DataTypes.getDataType(Field.TYPE_XML);
    public static final DataType DATETIME = DataTypes.getDataType(Field.TYPE_DATETIME);
    public static final DataType BOOLEAN  = DataTypes.getDataType(Field.TYPE_BOOLEAN);
    public static final DataType BINARY   = DataTypes.getDataType(Field.TYPE_BINARY);
    public static final DataType NODE     = DataTypes.getDataType(Field.TYPE_NODE);
    public static final DataType UNKNOWN  = DataTypes.getDataType(Field.TYPE_UNKNOWN);

    public static final DataType LIST_UNKNOWN = DataTypes.getListDataType(Field.TYPE_UNKNOWN);
    public static final DataType LIST_INTEGER = DataTypes.getListDataType(Field.TYPE_INTEGER);
    public static final DataType LIST_LONG = DataTypes.getListDataType(Field.TYPE_LONG);
    public static final DataType LIST_FLOAT = DataTypes.getListDataType(Field.TYPE_FLOAT);
    public static final DataType LIST_DOUBLE = DataTypes.getListDataType(Field.TYPE_DOUBLE);
    public static final DataType LIST_STRING = DataTypes.getListDataType(Field.TYPE_STRING);
    public static final DataType LIST_XML = DataTypes.getListDataType(Field.TYPE_XML);
    public static final DataType LIST_DATETIME = DataTypes.getListDataType(Field.TYPE_DATETIME);
    public static final DataType LIST_BOOLEAN = DataTypes.getListDataType(Field.TYPE_BOOLEAN);
    public static final DataType LIST_NODE = DataTypes.getListDataType(Field.TYPE_NODE);

    /**
     * An empty Parameter array.
     */
    public static final DataType[] EMPTY  = new DataType[0];

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    public Object getDefaultValue();

    /**
     * Sets the default value of this data type.
     * @param def the default value
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return this datatype
     */
    public DataType setDefaultValue(Object def);

    /**
     * Returns whether this field is required (should have content).
     * Note that MMBase does not generally enforce required fields to be filled -
     * If not provided, a default value (generally an empty string or the integer value -1)
     * is filled in by the system.
     * As such, isRequired will mostly be used as an indicator for (generic) editors.
     *
     * @return  <code>true</code> if the field is required
     * @since  MMBase-1.6
     */
    public boolean isRequired();

    /**
     * Sets whether the data type requires a value.
     * @param required <code>true</code> if a value is required
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return the datatype property that was just set
     */
    public DataType.Property setRequired(boolean required);

    /**
     * Returns the 'required' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getRequiredProperty();

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException if it doesn't.
     * @param value the value to validate
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value);

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException with a lozalized message (dependent on the cloud) if it doesn't.
     * @param value the value to validate
     * @param cloud the cloud used to determine the locale for the error message when validation fails
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value, Cloud cloud);

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class getTypeAsClass();

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this data type),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value the value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value);

    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this Parameter.
     */
    public Object autoCast(Object value);

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Similar to calling clone(), but changes the data type name if one is provided.
     * @param name the new name of the copied datatype (can be <code>null</code>, in which case the name is not changed).
     */
    public Object clone(String name);

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Unlike the original datatype though, the cloned copy is declared unfinished even if the original
     * was finished. This means that the cloned datatype can be changed.
     */
    public Object clone();

    static public interface Property {
        public String getName();
        public Object getValue();
        public void setValue(Object value);
        public LocalizedString getLocalizedErrorDescription();
        public String getErrorDescription(Locale locale);
        public String getErrorDescription();
        public void setLocalizedErrorDescription(LocalizedString errorDescription);
        public boolean isFixed();
        public void setFixed(boolean fixed);
    }

}
