/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * Comparable datatypes have values which are {@link java.lang.Comparable}, so can be ordered, and
 * therefore can have a minimum and a maximum value.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComparableDataType.java,v 1.17 2006-05-16 21:11:05 michiel Exp $
 * @since MMBase-1.8
 */
public abstract class ComparableDataType extends BasicDataType {

    private static final Logger log = Logging.getLoggerInstance(ComparableDataType.class);

    private static final long serialVersionUID = 1L; 

    protected MinRestriction minRestriction  = new MinRestriction(true);
    protected MaxRestriction maxRestriction  = new MaxRestriction(true);

    protected ComparableDataType(String name, Class classType) {
        super(name, classType);
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof ComparableDataType) {
            ComparableDataType dataType = (ComparableDataType) origin;
            minRestriction.inherit(dataType.minRestriction);
            maxRestriction.inherit(dataType.maxRestriction);
        }
    }
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof ComparableDataType) {
            ComparableDataType dataType = (ComparableDataType) origin;
            minRestriction  = new MinRestriction(dataType.minRestriction);
            maxRestriction  = new MaxRestriction(dataType.maxRestriction);
        }
    }


    /**
     */
    public DataType.Restriction getMinRestriction() {
        return minRestriction;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minRestriction.isInclusive();
    }

    /**
     */
    public DataType.Restriction getMaxRestriction() {
        return maxRestriction;
    }


    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxRestriction.isInclusive();
    }

    /**
     * @inheritDoc
     *
     * If the default value of comparable datatype is somewhy out the range, it will be truncated into it.
     */
    public final Object getDefaultValue() {
        Object def = super.getDefaultValue();
        if (! minRestriction.valid(def, null, null)) {
            def = minRestriction.getValue();
        } else if (! maxRestriction.valid(def, null, null)) {
            def = maxRestriction.getValue();
        }
        return def;
    }

    /**
     * Sets the minimum Date value for this data type.
     * @param value the minimum as an <code>Comparable</code> (and <code>Serializable</code>), or <code>null</code> if there is no minimum.
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MMBase)
     */
    public void setMin(Comparable value, boolean inclusive) {
        edit();
        checkType(value);
        if (inclusive != minRestriction.isInclusive()) minRestriction = new MinRestriction(inclusive);
        minRestriction.setValue((java.io.Serializable) value);
    }


    /**
     * Sets the maximum Date value for this data type.
     * @param value the maximum as an <code>Comparable</code> (and <code>Serializable</code>), or <code>null</code> if there is no maximum.
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MMBase)
     */
    public void setMax(Comparable value, boolean inclusive) {
        edit();
        checkType(value);
        if (inclusive != maxRestriction.isInclusive()) maxRestriction = new MaxRestriction(inclusive);
        maxRestriction.setValue((java.io.Serializable) value);
    }

    
    public void toXml(org.w3c.dom.Element parent) {
        super.toXml(parent);
        /* TODO
        if (minRestriction.isInclusive()) {
            getElement(parent, "min", "description,class,property,default,unique,required,minInclusive").setAttribute("value", Casting.toString(minRestriction.getValue()));
        } else {
            getElement(parent, "min", "description,class,property,default,unique,required,minExclusive").setAttribute("value", Casting.toString(minRestriction.getValue()));
        }
        if (maxRestriction.isInclusive()) {
            getElement(parent, "re", "description,class,property,default,unique,required,minInclusive").setAttribute("value", Casting.toString(maxRestriction.getValue()));
        } else {
            getElement(parent, "reed", "description,class,property,default,unique,required,minExclusive").setAttribute("value", Casting.toString(maxRestriction.getValue()));
        }
        */
    }


    protected Collection validateCastedValue(Collection errors, Object castedValue, Object value,  Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, value, node, field);
        errors = minRestriction.validate(errors, castedValue, node, field);
        errors = maxRestriction.validate(errors, castedValue, node, field);
        return errors;
    }

    public Object clone(String name) {
        ComparableDataType clone = (ComparableDataType) super.clone(name);
        return clone;
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        Object minValue = minRestriction.getValue();
        Object maxValue = maxRestriction.getValue();
        if (minValue != null) {
            buf.append(minRestriction.isInclusive() ? '[' : '<');
            buf.append(minValue);
            if (minValue instanceof Date) {
                // tss, the toString of Date object doesn't have BC in it if needed!
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) minValue);
                if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
                    buf.append(" BC");
                }
            }
            buf.append(minRestriction.enforceStrength == DataType.ENFORCE_NEVER ? "*" : "");
        }
        if (minValue != null || maxValue != null) {
            buf.append("...");
        }
        if (maxValue != null) {
            buf.append(maxValue);
            buf.append(maxRestriction.enforceStrength == DataType.ENFORCE_NEVER ? "*" : "");
            buf.append(maxRestriction.isInclusive() ? ']' : '>');
        }
        return buf;
    }

    protected class MinRestriction extends AbstractRestriction {
        private boolean inclusive;
        MinRestriction(MinRestriction source) {
            super(source);
            inclusive = source.inclusive;
        }
        MinRestriction(boolean inc) {
            super("min" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }

        protected boolean simpleValid(Object v, Node node, Field field) {
            if ((v == null) || (getValue() == null)) return true;
            Comparable comparable = (Comparable) v;
            Comparable minimum;
            try {
                minimum = (Comparable) ComparableDataType.this.castToValidate(getValue(), node, field);
            } catch (CastException ce) {
                log.error(ce); // probably config error.
                // invalid value, but not because of min-restriction
                return true;
            }
            if (inclusive && (comparable.equals(minimum))) return true;
            return comparable.compareTo(minimum) > 0;
        }
        public boolean isInclusive() {
            return inclusive;
        }
    }
    protected class MaxRestriction extends AbstractRestriction {
        private boolean inclusive;
        MaxRestriction(MaxRestriction source) {
            super(source);
            inclusive = source.inclusive;
        }
        MaxRestriction(boolean inc) {
            super("max" + (inc ? "Inclusive" : "Exclusive"), null);
            inclusive = inc;
        }
        protected boolean simpleValid(Object v, Node node, Field field) {
            if ((v == null) || (getValue() == null)) return true;
            Comparable comparable = (Comparable) v;
            Comparable maximum;
            try {
                maximum = (Comparable) ComparableDataType.this.castToValidate(getValue(), node, field);
            } catch (CastException ce) {
                log.error(ce); // probably config error.
                // invalid value, but not because of max-restriction
                return true;
            }
            if (inclusive && (comparable.equals(maximum))) return true;
            boolean res = comparable.compareTo(maximum) < 0;
            return res;
        }


        public boolean isInclusive() {
            return inclusive;
        }
    }


}
