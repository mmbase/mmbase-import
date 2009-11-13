/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.text.MessageFormat;

/**
 * The TypeMapping class helps translating MMBase types to storage-specific type descriptions.
 * Examples of type mappings are mappings that convert to database field types.
 * I.e., a STRING with size 0-255 could be configured to translate to 'varchar({0})', '{0}, in this case,.
 * being the size of the actual field.
 * <br />
 * TypeMapping is a comparable class, which allows it to be used in a sorted map, set or list.
 * However, Typemapping needs fuzzy matching so it is easy to locate the appropriate type-mapping for a field.
 * As such, it's natural ordering is NOT consistent with equals. A typemapping may be considered 'equal' while still having
 * a different ordering position in the class.
 * This allows for an easy search on a sorted list of TypeMappings: By using 'IndexOf' you can quickly find a TypeMapping in a list,
 * even if the min or max sizes for the type do not completely match.
 * You typically use this if you searxh for a TypeMapping whose size you set with 'getSize()', rather than setting a specific range
 * (using the minSize/maxSize properties).
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.7
 */
public class TypeMapping implements Comparable<TypeMapping> {

    /**
     * The expression this type should translate to.
     * You can access this property directly, but you can use {@link #getType(Object...)} to obtain an expanded expression.
     */
    public String type;
    /**
     * The name of the MMBase type to map
     */
    public String name;
    /**
     * The minimum size of the MMBase type to map.
     * A value of -1 indicates no mimimum.
     */
    public long minSize;
    /**
     * The maximum size of the MMBase type to map.
     * A value of -1 indicates no maximum.
     */
    public long maxSize;

    public TypeMapping() {
    }

    /**
     * Sets a fixed size for this TypeMapping. Effectively, this sets the minimum and maximum size
     * of the type mapping to the specified value, ensuring this TypeMapping object is equal to all
     * TypeMappings which minimum size is equal to or smaller than the size, and the maximum size is
     * equal to or greater that this size.
     *
     * @param size the size to set
     */
    public void setFixedSize(long size) {
        minSize = size;
        maxSize = size;
    }

    // javadoc inherited
    public int compareTo(TypeMapping o) {
        TypeMapping t = o;
        if (!name.equals(t.name)) {
            return name.compareTo(t.name);
        } else if (minSize != t.minSize) {
            long diff = t.minSize - minSize;
            if (diff == 0) return 0;
            return diff < 0 ? -1 : 1;
        } else if (maxSize == -1) {
            if (t.maxSize == -1) {
                return 0;
            } else {
                return -1;
            }
        } else {
            long diff = t.maxSize - maxSize;
            if (diff == 0) return 0;
            return diff < 0 ? -1 : 1;
        }
    }

    // javadoc inherited
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof TypeMapping) {
            TypeMapping tm = (TypeMapping) o;
            // A typemapping equals another type-mapping when the one contains the other.
            // Because of this the 'fixed' size type-mappings (created DatabaseStorageManager) are found by indexOf of the typeMappings Collection of DatabaseStorageManager.
            // In this typeMappings Collection there are normally only 'ranged' of sizes (as defined in the XML)
            return (name == null ? tm.name == null : name.equals(tm.name)) &&
               (
                ( (minSize >= tm.minSize || (tm.minSize <= 0)) && (maxSize <= tm.maxSize || (tm.maxSize <= 0)) )  // contained by.
                ||
                ( (tm.minSize >= minSize || (minSize <= 0))    && (tm.maxSize <= maxSize || (maxSize <= 0)) )     // containing
                )
                ;
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // because of the complicated equals implementation minSize and maxSize cannot be present in hashCode
        return name == null ? 0 : name.hashCode();
    }

    /**
     * Returns the mappings type.
     * @param messageFormatArguments Argument to use as arugment for the used {@link
     * MessageFormat#format(String, Object...)}.
     * @return mappings type
     */
    public String getType(Object... messageFormatArguments) {
        return MessageFormat.format(type, messageFormatArguments);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name + " (" + minSize + "," + maxSize+ " )+>" + type;
    }

}
