/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import java.util.*;
import org.mmbase.util.Casting;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.1 2004-12-06 15:25:19 pierre Exp $
 * @see Parameter
 */

public interface DataType {

    /**
     * An empty Parameter array.
     */
    public static final DataType[] EMPTY  = new DataType[0];

    /**
     * Returns the name or 'key' of this data type, or <code>null</code> if not appliable.
     * @return the name as a String
     */
    public String getName ();

    /**
     * Returns the description of this data type.
     * @return the description as a String
     */
    public String getDescription();

    /**
     * Sets the description of this data type.
     * @param description the description as a String
     */
    public void setDescription(String description);

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    public Object getDefaultValue();

    /**
     * Sets the default value of this data type.
     * @param def the default value
     */
    public void setDefaultValue(Object def);

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class getType();

    /**
     * Returns whether the data type requires a value.
     * @return <code>true</code> if a value is required
     */
    public boolean isRequired();

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
