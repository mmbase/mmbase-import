/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;
import org.mmbase.util.Casting;

/**
 *  * DataType associated with {@link java.lang.Long},  NumberDataType, but provides getMin and getMax as long.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class LongDataType extends NumberDataType {
    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)
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
        Object min = getMinRestriction().getValue(); 
        return min == null ? Long.MIN_VALUE : Casting.toLong(min); // casting, mainly to anticipate dates
    }

    /**
     * @return the maximum value as an <code>long</code>, or {@link Long#MAX_VALUE} if there is no maximum.
     */
    public long getMax() {
        Object max = getMaxRestriction().getValue(); 
        return max == null ? Long.MAX_VALUE : Casting.toLong(max); // casting, mainly to anticipate dates
    }

}
