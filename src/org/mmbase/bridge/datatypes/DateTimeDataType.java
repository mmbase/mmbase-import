/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.datatypes;

import java.util.Date;
import org.mmbase.bridge.DataType;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DateTimeDataType.java,v 1.2 2005-07-08 08:02:17 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface DateTimeDataType extends DataType {

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Date</code>, or <code>null</code> if there is no minimum.
     */
    public Date getMin();

    /**
     * Returns the precision for comparing the minimum value for this datatype.
     * @return the minimum value as an <code>Date</code>, or <code>null</code> if there is no minimum.
     */
    public int getMinPrecision();

    /**
     * Returns whether the minimum value for this datatype is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMinInclusive();

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Date</code>, or <code>null</code> if there is no maximum.
     */
    public Date getMax();

    /**
     * Returns whether the maximum value for this datatype is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean getMaxInclusive();

    /**
     * Returns the precision for comparing the maximum value for this datatype.
     * @return the minimum value as an <code>Date</code>, or <code>null</code> if there is no minimum.
     */
    public int getMaxPrecision();

    /**
     * Sets the minimum Date value for this datatype.
     * @param length the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMin(Date value);

    /**
     * Sets the precision for the minimum value for this datatype.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMinPrecision(int precision);

    /**
     * Sets whether the minimum value for this datatype is inclusive or not.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    DateTimeDataType setMinInclusive(boolean inclusive);

    /**
     * Sets the minimum Date value for this datatype.
     * @param length the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMin(Date value, int precision, boolean inclusive);

    /**
     * Sets the maximum Date value for this datatype.
     * @param length the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMax(Date value);

    /**
     * Sets the precision for the maximum value for this datatype.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMaxPrecision(int precision);

    /**
     * Sets whether the maximum value for this datatype is inclusive or not.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMaxInclusive(boolean inclusive);

    /**
     * Sets the maximum Date value for this datatype.
     * @param length the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DateTimeDataType setMax(Date value, int precision, boolean inclusive);

}
