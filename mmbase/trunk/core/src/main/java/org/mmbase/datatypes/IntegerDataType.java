/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;
import org.mmbase.util.Casting;

/**
 * DataType associated with {@link java.lang.Integer}, a NumberDataType, but provides getMin and getMax as int.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 */
public class IntegerDataType extends NumberDataType<Integer> {
    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    /**
     * @param primitive indicate if a primitive type should be used
     */
    public IntegerDataType(String name, boolean primitive) {
        super(name, primitive ? Integer.TYPE : Integer.class);
        setMin(Integer.valueOf(Integer.MIN_VALUE), true);
        minRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
        setMax(Integer.valueOf(Integer.MAX_VALUE), true);
        maxRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof BooleanDataType) {
            setMin(Integer.valueOf(0), true);
            setMax(Integer.valueOf(1), true);
        }
    }


    /**
     * @return the minimum value as an <code>int</code>, or a very very small number if there is no minimum.
     */
    public int getMin() {
        Integer min = getMinRestriction().getValue();
        return min == null ? Integer.MIN_VALUE : Casting.toInt(min);
    }

    /**
     * @return the maximum value as an <code>int</code>, or a very very big number if there is no maximum.
     */
    public int getMax() {
        Integer max = getMaxRestriction().getValue();
        return max == null ? Integer.MAX_VALUE : Casting.toInt(max);
    }

}
