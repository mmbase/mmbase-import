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
 * @version $Id: DoubleDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class DoubleDataType extends NumberDataType {

    /**
     * Constructor for Double field.
     */
    public DoubleDataType(String name) {
        super(name, Double.class);
    }

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Double</code>, or <code>null</code> if there is no minimum.
     */
    public Double getMin() {
        Number min = getMinValue();
        if (min instanceof Double) {
            return (Double)min;
        } else {
            return new Double(min.doubleValue());
        }
    }

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Double</code>, or <code>null</code> if there is no maximum.
     */
    public Double getMax() {
        Number max = getMaxValue();
        if (max instanceof Double) {
            return (Double)max;
        } else {
            return new Double(max.doubleValue());
        }
    }

}
