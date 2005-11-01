/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * A NumberDataType, but provides getMin and getMax as int.
 *
 * @author Pierre van Rooden
 * @version $Id: IntegerDataType.java,v 1.6 2005-11-01 23:41:26 michiel Exp $
 * @since MMBase-1.8
 */
public class IntegerDataType extends NumberDataType {

    /**
     * @param primitive indicate if a primitive type should be used
     */
    public IntegerDataType(String name, boolean primitive) {
        super(name, primitive ? Integer.TYPE : Integer.class);
        setMin(new Integer(Integer.MIN_VALUE), true);
        setMax(new Integer(Integer.MAX_VALUE), true);
    }

    /**
     * @return the minimum value as an <code>int</code>, or a very very small number if there is no minimum.
     */
    public int getMin() {
        Number min = (Number) getMinRestriction().getValue();
        return min == null ? Integer.MIN_VALUE : min.intValue();
    }

    /**
     * @return the maximum value as an <code>int</code>, or a very very big number if there is no maximum.
     */
    public int getMax() {
        Number max = (Number) getMaxRestriction().getValue();
        return max == null ? Integer.MAX_VALUE  : max.intValue();
    }

}
