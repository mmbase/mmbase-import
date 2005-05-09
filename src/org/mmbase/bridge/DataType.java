/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.2 2005-05-09 21:42:39 michiel Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface DataType {

    /**
     * An empty Parameter array.
     */
    static final DataType[] EMPTY  = new DataType[0];

    /**
     * Returns the name or 'key' of this data type, or <code>null</code> if not applicable.
     * @return the name as a String
     */
    String getName ();

    /**
     * Returns the description of this data type.
     * @param locale The locale for which this must be returned, or <code>null</code> for a default locale.
     *               If no fitting description for the given locale is available, getName() can be returned.
     * @return the description as a String
     */
    String getDescription(Locale locale);

    /**
     * Sets the description of this data type.
     * @param locale The locale for which this is valid, or <code>null</code> for a default locale.
     * @param description the description as a String
     */
    void setDescription(String description, Locale locale);

    /**
     * Associates a resource-bundle with the description of this DataType. The description can be
     * looked up using this resource-bundle if no description for a Locale was specified explicitely.
     */
    void setBundle(String bundle);

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    Object getDefaultValue();
    /**
     * Sets the default value of this data type.
     * @param def the default value
     */
    void setDefaultValue(Object def);

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    Class getTypeAsClass();

    /**
     * Returns whether the data type requires a value.
     * @return <code>true</code> if a value is required
     */
    boolean isRequired();

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this data type),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value the value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    void checkType(Object value);

    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this Parameter.
     */
    Object autoCast(Object value);

}
