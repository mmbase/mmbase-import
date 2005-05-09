/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.DataType;
import org.mmbase.util.Casting;
import org.mmbase.util.LocalizedString;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen (MMFunctionParam)
 * @since  MMBase-1.8
 * @version $Id: AbstractDataType.java,v 1.2 2005-05-09 10:57:27 michiel Exp $
 */

abstract public class AbstractDataType implements DataType, Comparable {

    private static final Logger log = Logging.getLoggerInstance(AbstractDataType.class);
    

    // package for Parameters (direct access avoids function calls)
    private String key;
    private LocalizedString description;
    private Class type;


    protected AbstractDataType() {}

    /**
     * Create an data type object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected AbstractDataType(String name, Class type) {
        this.key = name;
        this.type = type;
        description = new LocalizedString(key);
    }

    /**
     * Returns the name or 'key' of this data type.
     * @return the name as a String
     */
    public String getName() {
        return key;
    }

    /**
     * Returns the description of this data type, for a certain Locale
     * @return the description as a String
     */
    public String getDescription(Locale locale) {        
        return description.get(locale);
    }


    /**
     * Sets the description of this data type.
     * @param description the description as a String
     */
    public void setDescription(String desc, Locale locale) {
        description.set(desc, locale);
    }

    public void setBundle(String b) {
        description.setBundle(b);
    }

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    abstract public Object getDefaultValue();

    /**
     * Sets the default value of this data type.
     * @param def the default value
     */
    abstract public void setDefaultValue(Object def);

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class getTypeAsClass() {
        return type;
    }

    /**
     * Returns whether the data type requires a value.
     * @return <code>true</code> if a value is required
     */
    abstract public boolean isRequired();

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this DataType),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    protected boolean isCorrectType(Object value) {
        return Casting.isType(type, value);
    }

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this DataType),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            throw new IllegalArgumentException("DataType of '" + value + "' for '" + getName() + "' must be of type " + type + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }

    /**
     * Tries to 'cast' an object for use with this data type. E.g. if value is a String, but this
     * data type is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this DataType.
     */
    public Object autoCast(Object value) {
        return Casting.toType(type, value);
    }

    public String toString() {
        return type.getName() + " " + key;
    }



    public int compareTo(Object o) {
        if (o instanceof DataType) {
            DataType a = (DataType) o;
            int compared = key.compareTo(a.getName());
            if (compared == 0) compared = type.getName().compareTo(a.getTypeAsClass().getName());
            return compared;
        } else {
            throw new ClassCastException("Object is not of type DataType");
        }
    }

    /**
     * Whether data type equals to other data type. Only key and type are consided. DefaultValue and
     * required propererties are only 'utilities'.
     * @return true if o is a DataType of which key and type equal to this' key and type.
     */
    public boolean equals(Object o) {
        if (o instanceof DataType) {
            DataType a = (DataType) o;
            return key.equals(a.getName()) && type.equals(a.getTypeAsClass());
        }
        return false;
    }

    public int hashCode() {
        return key.hashCode() * 13 + type.hashCode();
    }

}
