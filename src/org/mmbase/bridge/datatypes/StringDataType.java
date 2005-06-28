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
 * @version $Id: StringDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface StringDataType extends DataType {

    /**
     * Returns the regular expression pattern used to validate values for this datatype.
     * @return the pattern as a <code>String</code>, or <code>null</code> if there is no pattern.
     */
    public String getPattern();

    /**
     * Sets the regular expression pattern used to validate values for this datatype.
     * @param pattern the pattern as a <code>String</code>, or <code>null</code> if no pattern should be applied.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public StringDataType setPattern(String pattern);

    /**
     * Returns the minimum length of string values for this datatype.
     * @return the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     */
    public int getMinLength();

    /**
     * Returns the maximum length of string values for this datatype.
     * @return the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     */
    public int getMaxLength();

    /**
     * Sets the minimum length of string values for this datatype.
     * @param length the minimum length as an <code>int</code>, or -1 if there is no minimum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public StringDataType setMinLength(int length);

    /**
     * Sets the maximum length of string values for this datatype.
     * @param length the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public StringDataType setMaxLength(int length);

}
