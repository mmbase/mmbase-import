/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import java.util.regex.Pattern;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * The datatype for String fields. Strings can be constrained by a regular expression, and have a
 * property 'password' which indicates that the contents should not be shown.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: StringDataType.java,v 1.22 2005-10-21 16:41:53 michiel Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends ComparableDataType implements LengthDataType {
    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected PatternRestriction patternRestriction = new PatternRestriction(Pattern.compile(".*"));
    private boolean isPassword = false;
    protected AbstractLengthDataType.MinRestriction minLengthRestriction = new AbstractLengthDataType.MinRestriction(this, 0);
    protected AbstractLengthDataType.MaxRestriction maxLengthRestriction = new AbstractLengthDataType.MaxRestriction(this, Long.MAX_VALUE);

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public StringDataType(String name) {
        super(name, String.class);
    }

    public void inherit(BasicDataType origin) {
        super.inherit(origin);
        if (origin instanceof StringDataType) {
            StringDataType dataType = (StringDataType)origin;
            patternRestriction = new PatternRestriction(dataType.patternRestriction);
            isPassword = dataType.isPassword();
            minLengthRestriction = new AbstractLengthDataType.MinRestriction(this, dataType.getMinLengthRestriction());
            maxLengthRestriction = new AbstractLengthDataType.MaxRestriction(this, dataType.getMaxLengthRestriction());
        }
    }

    public long getLength(Object value) {
        return ((String) value).length();
    }
    /**
     * {@inheritDoc}
     */
    public long getMinLength() {
        return Casting.toLong(minLengthRestriction.getValue());
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction getMinLengthRestriction() {
        return minLengthRestriction;
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction setMinLength(long value) {
        return getMinLengthRestriction().setValue(new Long(value));
    }

    /**
     * {@inheritDoc}
     */
    public long getMaxLength() {
        return Casting.toLong(getMaxLengthRestriction().getValue());
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction getMaxLengthRestriction() {
        return maxLengthRestriction;
    }
    /**
     * {@inheritDoc}
     */
    public DataType.Restriction setMaxLength(long value) {
        return getMaxLengthRestriction().setValue(new Long(value));
    }



    /**
     * Returns the regular expression pattern used to validate values for this datatype.
     * @return the pattern.
     */
    public Pattern getPattern() {
        return patternRestriction.getPattern();
    }

    /**
     * Returns the 'pattern' restriction, containing the value, error messages, and fixed status of this attribute.
     * @return the restriction as a {@link DataType.Restriction}
     */
    public DataType.Restriction getPatternRestriction() {
        return patternRestriction;
    }

    /**
     * Sets the regular expression pattern used to validate values for this datatype.
     * @param value the pattern as a <code>Pattern</code>, or <code>null</code> if no pattern should be applied.
     * @throws java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MMBase)
     */
    public DataType.Restriction setPattern(Pattern value) {
        return getPatternRestriction().setValue(value);
    }

    /**
     * Whether or not the data represents sensitive information, in which case e.g. an input
     * interface may present asterisks in stead of letters.
     */
    public boolean isPassword() {
        return isPassword;
    }
    public void setPassword(boolean pw) {
        isPassword = pw;
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = patternRestriction.validate(errors, castedValue, node, field);
        return errors;
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        if (getPattern() != null) {
            buf.append(" pattern:").append(getPattern()).append(" ");
        }
        return buf;
    }

    protected class PatternRestriction extends AbstractRestriction {
        PatternRestriction(PatternRestriction source) {
            super(source);
        }
        PatternRestriction(Pattern v) {
            super("pattern", v);
        }
        Pattern getPattern() {
            return (Pattern) value;
        }
        public boolean valid(Object v, Node node, Field field) {
            String s = Casting.toString(v);
            return value == null ? true : getPattern().matcher(s).matches();
        }
    }

}
