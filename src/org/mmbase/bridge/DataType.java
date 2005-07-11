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
 * @version $Id: DataType.java,v 1.7 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface DataType extends Comparable, Descriptor {

    // DataTypes for base MMBase field types
    public static final DataType INTEGER  = DataTypes.createFinalDataType("integer", Field.TYPE_INTEGER);
    public static final DataType LONG     = DataTypes.createFinalDataType("long", Field.TYPE_LONG);
    public static final DataType FLOAT    = DataTypes.createFinalDataType("float", Field.TYPE_FLOAT);
    public static final DataType DOUBLE   = DataTypes.createFinalDataType("double", Field.TYPE_DOUBLE);
    public static final DataType STRING   = DataTypes.createFinalDataType("string", Field.TYPE_STRING);
    public static final DataType XML      = DataTypes.createFinalDataType("xml", Field.TYPE_XML);
    public static final DataType DATETIME = DataTypes.createFinalDataType("datetime", Field.TYPE_DATETIME);
    public static final DataType BOOLEAN  = DataTypes.createFinalDataType("boolean", Field.TYPE_BOOLEAN);
    public static final DataType BINARY   = DataTypes.createFinalDataType("binary", Field.TYPE_BINARY);
    public static final DataType NODE     = DataTypes.createFinalDataType("node", Field.TYPE_NODE);

    public static final DataType LIST_INTEGER = DataTypes.createFinalListDataType("list[integer]", INTEGER);
    public static final DataType LIST_LONG = DataTypes.createFinalListDataType("list[long]", LONG);
    public static final DataType LIST_FLOAT = DataTypes.createFinalListDataType("list[float]", FLOAT);
    public static final DataType LIST_DOUBLE = DataTypes.createFinalListDataType("list[double]", DOUBLE);
    public static final DataType LIST_STRING = DataTypes.createFinalListDataType("list[string]", STRING);
    public static final DataType LIST_XML = DataTypes.createFinalListDataType("list[xml]", XML);
    public static final DataType LIST_DATETIME = DataTypes.createFinalListDataType("list[datetime]", DATETIME);
    public static final DataType LIST_BOOLEAN = DataTypes.createFinalListDataType("list[boolean]", BOOLEAN);
    public static final DataType LIST_NODE = DataTypes.createFinalListDataType("list[node]", NODE);

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
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype (can be <code>null</code>).
     */
    public DataType copy(String name);

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class getTypeAsClass();

    /**
     * Returns the identifier for the MMBase base type for this datatype.
     * This represents one of field type constants. This basic type determines how data is stored in MMBase.
     * Generally it only makes sense when the datatype is actually used by a field.
     * If a datatype cannot be translated, it returns {@link Field.TYPE_UNKNOWN}.
     * @return  an <code>int</code> which identifies the base type
     */
    public int getBaseType();

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
