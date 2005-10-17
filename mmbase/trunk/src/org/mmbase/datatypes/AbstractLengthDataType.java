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
 * A LengthDataType is a datatype that defines a length for its values ({@link #getLength(Object)}) ,
 * and constraints on that (minimal an maximal length).
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: AbstractLengthDataType.java,v 1.4 2005-10-17 15:28:13 michiel Exp $
 * @since MMBase-1.8
 */
abstract public class AbstractLengthDataType extends BasicDataType implements LengthDataType {
    private static final Logger log = Logging.getLoggerInstance(LengthDataType.class);

    protected MinConstraint minLengthConstraint = new MinConstraint(this, 0);
    protected MaxConstraint maxLengthConstraint = new MaxConstraint(this, Long.MAX_VALUE);

    /**
     * Constructor for big data field.
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    public AbstractLengthDataType(String name, Class classType) {
        super(name, classType);
    }


    public void inherit(BasicDataType origin) {
        super.inherit(origin);
        if (origin instanceof LengthDataType) {
            LengthDataType dataType = (LengthDataType)origin;
            // make new instances because of this can be called from a clone .. We hate java.
            minLengthConstraint = new MinConstraint(this, dataType.getMinLengthConstraint());
            maxLengthConstraint = new MaxConstraint(this, dataType.getMaxLengthConstraint());
        }
    }

    /**
     * @inheritDoc
     */
    public abstract long getLength(Object value);


    /**
     * @inheritDoc
     */
    public long getMinLength() {
        return Casting.toLong(minLengthConstraint.getValue());
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint getMinLengthConstraint() {
        return minLengthConstraint;
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint setMinLength(long value) {
        return getMinLengthConstraint().setValue(new Long(value));
    }

    /**
     * @inheritDoc
     */
    public long getMaxLength() {
        return Casting.toLong(getMaxLengthConstraint().getValue());
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint getMaxLengthConstraint() {
        return maxLengthConstraint;
    }

    /**
     * Sets the maximum length of binary values for this datatype.
     * @param value the maximum length as an <code>int</code>, or -1 if there is no maximum length.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is finished
     * @return the datatype property that was just set
     */
    public DataType.ValueConstraint setMaxLength(long value) {
        return getMaxLengthConstraint().setValue(new Long(value));
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = minLengthConstraint.validate(errors, castedValue, node, field);
        errors = maxLengthConstraint.validate(errors, castedValue, node, field);
        return errors;
    }


    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        if (getMinLength() > 0) {
            buf.append("minLength:" + getMinLength() + "\n");
        }
        if (getMaxLength() < Long.MAX_VALUE) {
            buf.append("maxLength:" + getMaxLength() + "\n");
        }
        return buf;
    }

    static class MinConstraint extends StaticAbstractValueConstraint {
        MinConstraint(BasicDataType dt, DataType.ValueConstraint source) {
            super(dt, source);
        }
        MinConstraint(BasicDataType dt, long min) {
            super(dt, "minLength", new Long(min));
        }
        public boolean valid(Object v, Node node, Field field) {
            long min = Casting.toLong(getValue());
            return ((LengthDataType) parent).getLength(v) >= min;
        }
    }
    static class MaxConstraint extends StaticAbstractValueConstraint {
        MaxConstraint(BasicDataType dt, DataType.ValueConstraint source) {
            super(dt, source);
        }
        MaxConstraint(BasicDataType dt, long max) {
            super(dt, "maxLength", new Long(max));
        }
        public boolean valid(Object v, Node node, Field field) {
            long max = Casting.toLong(getValue());
            long length = ((LengthDataType) parent).getLength(v);
            return length <= max;
        }
    }

}
