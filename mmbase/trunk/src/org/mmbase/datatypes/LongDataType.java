/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * A NumberDataType, but provides getMin and getMax as long.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: LongDataType.java,v 1.2 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class LongDataType extends NumberDataType {

    /**
     */
    public LongDataType(String name) {
        super(name, Long.class);
    }

    /**
     * @return the minimum value as an <code>long</code>, or {@link Long#MIN_VALUE} if there is no minimum.
     */
    public long getMin() {
        Number min = (Number) getMinConstraint().getValue();
        return min == null ? Long.MIN_VALUE : min.longValue();
    }

    /**
     * @return the maximum value as an <code>long</code>, or {@link Long#MAX_VALUE} if there is no maximum.
     */
    public long getMax() {
        Number max = (Number) getMaxConstraint().getValue();
        return max == null ? Long.MAX_VALUE : max.longValue();
    }

}
