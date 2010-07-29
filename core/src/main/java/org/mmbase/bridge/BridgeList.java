/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @param <E> Type of elements
 * @since  MMBase-1.6
 */
public interface BridgeList<E> extends List<E> {

    /**
     * Retrieves a property previously set for this list.
     * Use this to store and retrieve meta data on how the list was created
     * (such as what sort-order was specified)
     * @param key the key of the property
     * @return the property value
     */
    Object getProperty(Object key);

    /**
     * Sets a property for this list.
     * Use this to store and retrieve meta data on how the list was created
     * (such as what sort-order was specified)
     * @param key the key of the property
     * @param value the property value
     */
    void setProperty(Object key, Object value);


    /**
     * Returns an (unmodifiable) view on all properties of this list (See {@link #getProperty}).
     * @since MMBase-1.9.1
     */
    Map<Object, Object> getProperties();

    /**
     * Sorts this list according to a default sort order.
     */
    void sort();

    /**
     * Sorts this list according to a specified sort order
     *
     * @param comparator the comparator defining the sort order
     */
    void sort(Comparator<? super E> comparator); // ?

    BridgeList<E> subList(int fromIndex, int toIndex);

}
