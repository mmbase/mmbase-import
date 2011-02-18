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
import org.mmbase.util.LocalizedString;
import org.w3c.dom.Element;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A LengthDataType is a datatype that defines a length for its values ({@link #getLength(Object)}) ,
 * and restrictions on that (minimal an maximal length).
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
abstract public class AbstractLengthDataType<E> extends BasicDataType<E> implements LengthDataType<E> {

    private static final Logger log = Logging.getLoggerInstance(AbstractLengthDataType.class);

    protected MinRestriction minLengthRestriction = new MinRestriction(this, 0);
    protected MaxRestriction maxLengthRestriction = new MaxRestriction(this, Long.MAX_VALUE);

    /**
     * Constructor for big data field.
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    public AbstractLengthDataType(String name, Class<E> classType) {
        super(name, classType);
    }


    @Override
    protected void cloneRestrictions(BasicDataType<E> origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof AbstractLengthDataType) {
            AbstractLengthDataType<E> dataType = (AbstractLengthDataType<E>)origin;
            // make new instances because of this can be called from a clone .. We hate java.
            minLengthRestriction = new MinRestriction(this, dataType.minLengthRestriction);
            maxLengthRestriction = new MaxRestriction(this, dataType.maxLengthRestriction);
        }
    }

    @Override
    protected void inheritRestrictions(BasicDataType<E> origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof AbstractLengthDataType) {
            AbstractLengthDataType<E> dataType = (AbstractLengthDataType<E>)origin;
            // make new instances because of this can be called from a clone .. We hate java.
            minLengthRestriction.inherit(dataType.minLengthRestriction);
            maxLengthRestriction.inherit(dataType.maxLengthRestriction);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract long getLength(Object value);

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMinLength() {
        return minLengthRestriction.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType.Restriction<Long> getMinLengthRestriction() {
        return minLengthRestriction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinLength(long value) {
        getMinLengthRestriction().setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxLength() {
        return getMaxLengthRestriction().getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType.Restriction<Long> getMaxLengthRestriction() {
        return maxLengthRestriction;
    }

    /**
     * Sets the maximum length of binary values for this datatype.
     * @param value the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     */
    @Override
    public void setMaxLength(long value) {
        getMaxLengthRestriction().setValue(value);
    }

    @Override
    public int getEnforceStrength() {
        int enforceStrength = Math.max(super.getEnforceStrength(), minLengthRestriction.getEnforceStrength());
        return Math.max(enforceStrength, maxLengthRestriction.getEnforceStrength());
    }

    @Override
    protected Collection<LocalizedString> validateCastValueOrNull(Collection<LocalizedString> errors, Object castValue, Object value,  Node node, Field field) {
        errors = super.validateCastValueOrNull(errors, castValue, value,  node, field);
        errors = minLengthRestriction.validate(errors, castValue, node, field);
        return errors;

    }

    @Override
    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value,  Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        if (log.isDebugEnabled()) {
            log.debug("Validating with " + maxLengthRestriction + " " + castValue);
        }
        errors = maxLengthRestriction.validate(errors, castValue, node, field);
        return errors;
    }

    @Override
    public void toXml(Element parent) {
        super.toXml(parent);
        addRestriction(parent, "minLength",  "name,description,class,property,default,unique,required,(minInclusive|minExclusive),(maxInclusive|maxExclusive),minLength", minLengthRestriction);
        addRestriction(parent, "maxLength",  "name,description,class,property,default,unique,required,(minInclusive|minExclusive),(maxInclusive|maxExclusive),minLength,maxLength", maxLengthRestriction);

    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        if (getMinLength() > 0) {
            buf.append(" minLength:").append(getMinLength()).append(" ");
        }
        if (getMaxLength() < Long.MAX_VALUE) {
            buf.append(" maxLength:").append(getMaxLength()).append(" ");
        }
        return buf;
    }

    protected static class MinRestriction extends StaticAbstractRestriction<Long> {
        private static final long serialVersionUID = 4115513188533000959L;

        MinRestriction(BasicDataType<?> dt, MinRestriction source) {
            super(dt, source);
        }

        MinRestriction(BasicDataType<?> dt, long min) {
            super(dt, "minLength", min);
            setEnforceStrength(DataType.ENFORCE_ONCHANGE);
        }

        @Override
        protected boolean simpleValid(Object v, Node node, Field field) {
            if (v == null) return true; // depends on 'required'
            long min = Casting.toLong(getValue());
            return ((LengthDataType) parent).getLength(v) >= min;
        }
    }

    protected static class MaxRestriction extends StaticAbstractRestriction<Long> {
        private static final long serialVersionUID = -6308264648535016037L;
        MaxRestriction(BasicDataType<?> dt, MaxRestriction source) {
            super(dt, source);
        }

        MaxRestriction(BasicDataType<?> dt, long max) {
            super(dt, "maxLength", max);
            setEnforceStrength(DataType.ENFORCE_ONCHANGE);
        }

        @Override
        protected boolean simpleValid(Object v, Node node, Field field) {
            log.debug("Validation " + v);
            if (v == null) {
                return true; // depends on 'required'
            }
            long max = Casting.toLong(getValue());
            long length = ((LengthDataType) parent).getLength(v);
            log.debug("comparing " + length + " <= " + max);
            return length <= max;
        }
    }

}
