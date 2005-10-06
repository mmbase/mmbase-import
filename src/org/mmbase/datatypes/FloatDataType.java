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
 * @version $Id: FloatDataType.java,v 1.2 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class FloatDataType extends NumberDataType {

    /**
     */
    public FloatDataType(String name) {
        super(name, Float.class);
    }

    /**
     * @return the minimum value as a <code>float</code>, or {@link Float#NEGATIVE_INFINITY} if there is no minimum.
     */
    public float getMin() {
        Number min = (Number) getMinConstraint().getValue();
        return min == null ? Float.NEGATIVE_INFINITY : min.floatValue();
    }

    /**
     * @return the maximum value as a <code>float</code>, or {@link Float#POSITIVE_INFINITY} if there is no maximum.
     */
    public float getMax() {
        Number max = (Number) getMaxConstraint().getValue();
        return max == null ? Float.POSITIVE_INFINITY : max.floatValue();
    }
}
