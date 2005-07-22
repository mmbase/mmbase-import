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
 * @version $Id: LongDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class LongDataType extends NumberDataType {

    /**
     * Constructor for long field.
     */
    public LongDataType(String name) {
        super(name, Long.class);
    }

    /**
     * Returns the minimum value for this datatype.
     * @return the minimum value as an <code>Long</code>, or <code>null</code> if there is no minimum.
     */
    public Long getMin() {
        Number min = getMinValue();
        if (min instanceof Long) {
            return (Long)min;
        } else {
            return new Long(min.longValue());
        }
    }

    /**
     * Returns the maximum value for this datatype.
     * @return the maximum value as an <code>Long</code>, or <code>null</code> if there is no maximum.
     */
    public Long getMax() {
        Number max = getMaxValue();
        if (max instanceof Long) {
            return (Long)max;
        } else {
            return new Long(max.longValue());
        }
    }

}
