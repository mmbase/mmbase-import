/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.datatypes;

import org.mmbase.bridge.DataType;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: NumberDataType.java,v 1.1 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface NumberDataType extends DataType {

    /**
     * Returns the minimum value for this data type.
     * @return the property defining the minimum value
     */
    public DataType.Property getMinProperty();

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive();

    /**
     * Returns the maximum value for this data type.
     * @return the property defining the maximum value
     */
    public DataType.Property getMaxProperty();

    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive();

    /**
     * Sets the minimum Number value for this data type.
     * @param length the minimum as an <code>Number</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Number value);

    /**
     * Sets the minimum Number value for this data type.
     * @param length the minimum as an <code>Number</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Number value, boolean inclusive);

    /**
     * Sets the maximum Number value for this data type.
     * @param length the maximum as an <code>Number</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMax(Number value);

    /**
     * Sets the maximum Number value for this data type.
     * @param length the maximum as an <code>Number</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMax(Number value, boolean inclusive);

}
