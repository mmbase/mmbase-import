/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;
import java.util.ResourceBundle;
import org.mmbase.bridge.util.DataTypes;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.3 2005-06-28 14:01:40 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface DataType extends MMBaseType, Comparable, Descriptor {

    public static final DataType LANGUAGE = DataTypes.createFinalDataType("language", String.class);
    public static final DataType LOCALE   = DataTypes.createFinalDataType("locale", Locale.class);
    public static final DataType USER     = DataTypes.createFinalDataType("user", org.mmbase.security.UserContext.class);
    public static final DataType RESPONSE = DataTypes.createFinalDataType("response", javax.servlet.http.HttpServletResponse.class);
    public static final DataType REQUEST  = DataTypes.createFinalDataType("request", javax.servlet.http.HttpServletRequest.class);
    public static final DataType CLOUD    = DataTypes.createFinalDataType("cloud", org.mmbase.bridge.Cloud.class);
    // should this be a bridge node ???
    public static final DataType NODE     = DataTypes.createFinalDataType("node", org.mmbase.module.core.MMObjectNode.class);

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
     * @return this datatype
     */
    public DataType setRequired(boolean required);

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException if it doesn't.
     * @param value the value to validate
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value);

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

}
