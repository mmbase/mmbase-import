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
 * @version $Id: FloatDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class FloatDataType extends NumberDataType {

    /**
     * Constructor for Float field.
     */
    public FloatDataType(String name) {
        super(name, Float.class);
    }

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Float</code>, or <code>null</code> if there is no minimum.
     */
    public Float getMin() {
        Number min = getMinValue();
        if (min instanceof Float) {
            return (Float)min;
        } else {
            return new Float(min.floatValue());
        }
    }

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Float</code>, or <code>null</code> if there is no maximum.
     */
    public Float getMax() {
        Number max = getMaxValue();
        if (max instanceof Float) {
            return (Float)max;
        } else {
            return new Float(max.floatValue());
        }
    }
}
