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
 * @version $Id: LongDataType.java,v 1.6 2005-11-23 12:11:25 michiel Exp $
 * @since MMBase-1.8
 */
public class LongDataType extends NumberDataType {

    /**
     * @param primitive indicate if a primitive type should be used
     */
    public LongDataType(String name, boolean primitive) {
        super(name, primitive ? Long.TYPE : Long.class);
        setMin(new Long(Long.MIN_VALUE), true);
        minRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
        setMax(new Long(Long.MAX_VALUE), true);
        maxRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
    }


    /**
     * @return the minimum value as an <code>long</code>, or {@link Long#MIN_VALUE} if there is no minimum.
     */
    public long getMin() {
        Number min = (Number) getMinRestriction().getValue();
        return min == null ? Long.MIN_VALUE : min.longValue();
    }

    /**
     * @return the maximum value as an <code>long</code>, or {@link Long#MAX_VALUE} if there is no maximum.
     */
    public long getMax() {
        Number max = (Number) getMaxRestriction().getValue();
        return max == null ? Long.MAX_VALUE : max.longValue();
    }

}
