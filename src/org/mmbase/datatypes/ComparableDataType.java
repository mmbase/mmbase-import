/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;

/**
 * Comparable datatypes have values which are Comparable, so can be ordered, and therefore can have
 * a minimum and a maximum value.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComparableDataType.java,v 1.1 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public abstract class ComparableDataType extends BasicDataType {

    private static final Logger log = Logging.getLoggerInstance(DateTimeDataType.class);

    protected MinConstraint minConstraint  = new MinConstraint(true);
    protected MaxConstraint maxConstraint  = new MaxConstraint(true);
    protected ComparableDataType(String name, Class classType) {
        super(name, classType);
    }

    public void inherit(BasicDataType origin) {
        super.inherit(origin);
        if (origin instanceof ComparableDataType) {
            ComparableDataType dataType = (ComparableDataType)origin;
            minConstraint  = new MinConstraint(dataType.minConstraint);
            maxConstraint  = new MaxConstraint(dataType.maxConstraint);
        }
    }

    /**
     */
    public DataType.ValueConstraint getMinConstraint() {
        return minConstraint;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minConstraint.isInclusive();
    }

    /**
     */
    public DataType.ValueConstraint getMaxConstraint() {
        return maxConstraint;
    }


    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxConstraint.isInclusive();
    }


    /**
     * Sets the minimum Date value for this data type.
     * @param value the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MMxbBase)
     */
    public DataType.ValueConstraint setMin(Comparable value, boolean inclusive) {
        edit();
        checkType(value);
        if (inclusive != minConstraint.isInclusive()) minConstraint = new MinConstraint(inclusive);
        return minConstraint.setValue(value);
    }


    /**
     * Sets the maximum Date value for this data type.
     * @param value the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setMax(Comparable value, boolean inclusive) {
        edit();
        checkType(value);
        if (inclusive != maxConstraint.isInclusive()) maxConstraint = new MaxConstraint(inclusive);
        return getMaxConstraint().setValue(value);
    }


    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        errors = minConstraint.validate(errors, value, node, field);
        errors = maxConstraint.validate(errors, value, node, field);
        return errors;
    }

    public Object clone(String name) {
        ComparableDataType clone = (ComparableDataType) super.clone(name);
        return clone;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getMinConstraint().getValue() != null) {
            buf.append(" min: " + getMinConstraint());
        }
        if (getMaxConstraint().getValue() != null) {
            buf.append(" max:" + getMaxConstraint());
        }
        return buf.toString();
    }

    private class MinConstraint extends AbstractValueConstraint {
        private boolean inclusive;
        MinConstraint(MinConstraint source) {
            super(source.getName(), null);
            inclusive = source.inclusive;
            inherit(source);
        }
        MinConstraint(boolean inc) {
            super("min" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }
        public boolean valid(Object value, Node node, Field field) {
            //Comparable v = Casting.toComparable(value);
            Comparable v = (Comparable) value;
            Comparable minimum = (Comparable) getValue();
            if (minimum == null) return true;
            if (inclusive && (v.equals(minimum))) return true;
            return v.compareTo(minimum) > 0;
        }
        public boolean isInclusive() {
            return inclusive;
        }
    }
    private class MaxConstraint extends AbstractValueConstraint {
        private boolean inclusive;
        MaxConstraint(MaxConstraint source) {
            super(source.getName(), null);
            inclusive = source.inclusive;
            inherit(source);
        }
        MaxConstraint(boolean inc) {
            super("max" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }
        public boolean valid(Object value, Node node, Field field) {
            //Comparable v = Casting.toComparable(value);
            Comparable v = (Comparable) value;
            Comparable maximum = (Comparable) getValue();
            if (maximum == null) return true;
            if (inclusive && (v.equals(maximum))) return true;
            return v.compareTo(maximum) < 0;
        }

        public boolean isInclusive() {
            return inclusive;
        }
    }


}
