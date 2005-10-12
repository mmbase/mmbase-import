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
 * @version $Id: StringDataType.java,v 1.19 2005-10-12 00:01:04 michiel Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends ComparableDataType implements LengthDataType {
    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected PatternConstraint patternConstraint = new PatternConstraint(Pattern.compile(".*"));
    private boolean isPassword = false;
    protected AbstractLengthDataType.MinConstraint minLengthConstraint = new AbstractLengthDataType.MinConstraint(this, 0);
    protected AbstractLengthDataType.MaxConstraint maxLengthConstraint = new AbstractLengthDataType.MaxConstraint(this, Long.MAX_VALUE);

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
            patternConstraint = new PatternConstraint(dataType.patternConstraint);
            isPassword = dataType.isPassword();
            minLengthConstraint = new AbstractLengthDataType.MinConstraint(this, dataType.getMinLengthConstraint());
            maxLengthConstraint = new AbstractLengthDataType.MaxConstraint(this, dataType.getMaxLengthConstraint());
        }
    }

    public long getLength(Object value) {
        return ((String) value).length();
    }
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
     * @inheritDoc
     */
    public DataType.ValueConstraint setMaxLength(long value) {
        return getMaxLengthConstraint().setValue(new Long(value));
    }



    /**
     * Returns the regular expression pattern used to validate values for this datatype.
     * @return the pattern.
     */
    public Pattern getPattern() {
        return patternConstraint.getPattern();
    }

    /**
     * Returns the 'pattern' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType.ValueConstraint}
     */
    public DataType.ValueConstraint getPatternConstraint() {
        return patternConstraint;
    }

    /**
     * Sets the regular expression pattern used to validate values for this datatype.
     * @param value the pattern as a <code>Pattern</code>, or <code>null</code> if no pattern should be applied.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MMBase)
     */
    public DataType.ValueConstraint setPattern(Pattern value) {
        return getPatternConstraint().setValue(value);
    }

    /**
     * Where or not not the data represents sensistive information, in which case e.g. input
     * interface may present asterixes in stead of letters.
     */
    public boolean isPassword() {
        return isPassword;
    }
    public void setPassword(boolean pw) {
        isPassword = pw;
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = patternConstraint.validate(errors, castedValue, node, field);
        return errors;
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        if (getPattern() != null) {
            buf.append(" pattern:").append(getPattern()).append(" ");
        }
        return buf;
    }

    protected class PatternConstraint extends AbstractValueConstraint {
        PatternConstraint(PatternConstraint source) {
            super(source);
        }
        PatternConstraint(Pattern v) {
            super("pattern", v);
        }
        Pattern getPattern() {
            return (Pattern) value;
        }
        public boolean valid(Object v, Node node, Field field) {
            String s = Casting.toString(v);
            return value == null ? true : getPattern().matcher(Casting.toString(v)).matches();
        }
    }

}
