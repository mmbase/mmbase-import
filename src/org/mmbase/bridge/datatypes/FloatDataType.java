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
 * @version $Id: FloatDataType.java,v 1.2 2005-07-08 08:02:17 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface FloatDataType extends DataType {

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Float</code>, or <code>null</code> if there is no minimum.
     */
    public Float getMin();

    /**
     * Returns whether the minimum value for this datatype is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMinInclusive();

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Float</code>, or <code>null</code> if there is no maximum.
     */
    public Float getMax();

    /**
     * Returns whether the maximum value for this datatype is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMaxInclusive();

    /**
     * Sets the minimum Float value for this datatype.
     * @param length the minimum as an <code>Float</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMin(Float value);

    /**
     * Sets whether the minimum value for this datatype is inclusive or not.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMinInclusive(boolean inclusive);

    /**
     * Sets the minimum Float value for this datatype.
     * @param length the minimum as an <code>Float</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMin(Float value, boolean inclusive);

    /**
     * Sets the maximum Float value for this datatype.
     * @param length the maximum as an <code>Float</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMax(Float value);

    /**
     * Sets whether the maximum value for this datatype is inclusive or not.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMaxInclusive(boolean inclusive);

    /**
     * Sets the maximum Float value for this datatype.
     * @param length the maximum as an <code>Float</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public FloatDataType setMax(Float value, boolean inclusive);

}
