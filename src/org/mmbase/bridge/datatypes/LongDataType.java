/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.datatypes;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: LongDataType.java,v 1.3 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface LongDataType extends NumberDataType {

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Long</code>, or <code>null</code> if there is no minimum.
     */
    public Long getMin();

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Long</code>, or <code>null</code> if there is no maximum.
     */
    public Long getMax();


}
