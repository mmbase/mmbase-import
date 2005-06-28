/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.datatypes;

import org.mmbase.bridge.*;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: ListDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface ListDataType extends DataType {

    /**
     * Returns the minimum size for the list.
     * @return the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public int getMinSize();

    /**
     * Sets the minimum size for the list.
     * @param value the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public ListDataType setMinSize(int value);

    /**
     * Returns the maximum size for the list.
     * @return the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public int getMaxSize();

    /**
     * Sets the maximum size for the list.
     * @param value the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public ListDataType setMaxSize(int value);

    /**
     * Returns the datatype of items in this list.
     * @return the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType getListItemDataType();

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public ListDataType setListItemDataType(DataType value);


}
