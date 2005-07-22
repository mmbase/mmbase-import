/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: IntegerDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class IntegerDataType extends NumberDataType {

    /**
     * Constructor for integer field.
     */
    public IntegerDataType(String name) {
        super(name, Integer.class);
    }

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Integer</code>, or <code>null</code> if there is no minimum.
     */
    public Integer getMin() {
        Number min = getMinValue();
        if (min instanceof Integer) {
            return (Integer)min;
        } else {
            return new Integer(min.intValue());
        }
    }

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Integer</code>, or <code>null</code> if there is no maximum.
     */
    public Integer getMax() {
        Number max = getMaxValue();
        if (max instanceof Integer) {
            return (Integer)max;
        } else {
            return new Integer(max.intValue());
        }
    }

}
