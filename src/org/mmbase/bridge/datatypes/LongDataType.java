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
 * @version $Id: LongDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface LongDataType extends DataType {

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Long</code>, or <code>null</code> if there is no minimum.
     */
    public Long getMinimum();

    /**
     * Returns whether the minimum value for this datatype is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMinimumInclusive();

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Long</code>, or <code>null</code> if there is no maximum.
     */
    public Long getMaximum();

    /**
     * Returns whether the maximum value for this datatype is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMaximumInclusive();

    /**
     * Sets the minimum Long value for this datatype.
     * @param length the minimum as an <code>Long</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMinimum(Long value);

    /**
     * Sets whether the minimum value for this datatype is inclusive or not.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMinimumInclusive(boolean inclusive);

    /**
     * Sets the minimum Long value for this datatype.
     * @param length the minimum as an <code>Long</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMinimum(Long value, boolean inclusive);

    /**
     * Sets the maximum Long value for this datatype.
     * @param length the maximum as an <code>Long</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMaximum(Long value);

    /**
     * Sets whether the maximum value for this datatype is inclusive or not.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMaximumInclusive(boolean inclusive);

    /**
     * Sets the maximum Long value for this datatype.
     * @param length the maximum as an <code>Long</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public LongDataType setMaximum(Long value, boolean inclusive);

}
