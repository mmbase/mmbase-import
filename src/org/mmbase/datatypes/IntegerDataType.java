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
public class IntegerDataType extends NumberDataType {
    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    /**
     * @param primitive indicate if a primitive type should be used
     */
    public IntegerDataType(String name, boolean primitive) {
        super(name, primitive ? Integer.TYPE : Integer.class);
        setMin(new Integer(Integer.MIN_VALUE), true);
        minRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
        setMax(new Integer(Integer.MAX_VALUE), true);
        maxRestriction.setEnforceStrength(ENFORCE_ABSOLUTE);
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof BooleanDataType) {
            setMin(new Integer(0), true);
            setMax(new Integer(1), true);
        }
    }


    /**
     * @return the minimum value as an <code>int</code>, or a very very small number if there is no minimum.
     */
    public int getMin() {
        Object min = getMinRestriction().getValue();
        return min == null ? Integer.MIN_VALUE : Casting.toInt(min);
    }

    /**
     * @return the maximum value as an <code>int</code>, or a very very big number if there is no maximum.
     */
    public int getMax() {
        Object max = getMaxRestriction().getValue();
        return max == null ? Integer.MAX_VALUE : Casting.toInt(max);
    }

}
