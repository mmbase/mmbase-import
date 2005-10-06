/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * A NumberDataType, but provides getMin and getMax as double.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: DoubleDataType.java,v 1.2 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */

public class DoubleDataType extends NumberDataType {

    /**
     */
    public DoubleDataType(String name) {
        super(name, Double.class);
    }

    /**
     * @return the minimum value as a <code>double</code>, or {@link Double#NEGATIVE_INFINITY} if there is no minimum.
     */
    public double getMin() {
        Number min = (Number) getMinConstraint().getValue();
        return min == null ? Double.NEGATIVE_INFINITY : min.doubleValue();
    }

    /**
     * @return the maximum value as a <code>double</code>, or {@link Double#POSITIVE_INFINITY} if there is no maximum.
     */
    public double getMax() {
        Number max = (Number) getMaxConstraint().getValue();
        return max == null ? Double.POSITIVE_INFINITY : max.doubleValue();
    }

}
