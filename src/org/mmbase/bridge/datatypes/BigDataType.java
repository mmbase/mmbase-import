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
 * @version $Id: BigDataType.java,v 1.1 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface BigDataType extends DataType {

    /**
     * Returns the minimum length of binary values for this datatype.
     * @return the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     */
    public int getMinLength();

    /**
     * Returns the 'minLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMinLengthProperty();
    /**
     * Returns the maximum length of binary values for this datatype.
     * @return the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     */
    public int getMaxLength();

    /**
     * Returns the 'maxLength' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMaxLengthProperty();

    /**
     * Sets the minimum length of binary values for this datatype.
     * @param length the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.Property setMinLength(int length);

    /**
     * Sets the maximum length of binary values for this datatype.
     * @param length the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.Property setMaxLength(int length);

}
