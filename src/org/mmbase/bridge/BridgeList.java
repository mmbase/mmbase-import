/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.List;
import java.util.Comparator;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: BridgeList.java,v 1.4 2004-06-08 08:45:16 michiel Exp $
 * @since  MMBase-1.6
 */
public interface BridgeList extends List {

    /**
     * Retrieves a property previously set for this list.
     * Use this to store and retrieve metadata on whow teh listw as created
     * (such as what sort-order was specified)
     * @param key the key of the property
     * @return the property value
     */
    public Object getProperty(Object key);

    /**
     * Sets a property for this list.
     * Use this to store and retrieve metadata on whow teh listw as created
     * (such as what sort-order was specified)
     * @param key the key of the property
     * @param value the property value
     */
    public void setProperty(Object key, Object value);

    /**
     * Sorts this list according to a default sort order.
     */
    public void sort();

    /**
     * Sorts this list according to a specified sort order
     *
     * @param comparator the comparator defining the sort order
     */
    public void sort(Comparator comparator);
}
