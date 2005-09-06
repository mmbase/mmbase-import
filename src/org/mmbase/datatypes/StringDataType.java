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
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: StringDataType.java,v 1.12 2005-09-06 21:11:30 michiel Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends BigDataType {

    public static final Integer WHITESPACE_PRESERVE = new Integer(0);
    public static final Integer WHITESPACE_REPLACE = new Integer(1);
    public static final Integer WHITESPACE_COLLAPSE = new Integer(2);

    public static final String CONSTRAINT_PATTERN = "pattern";
    public static final Pattern CONSTRAINT_PATTERN_DEFAULT = null;

    public static final String CONSTRAINT_WHITESPACE = "whiteSpace";
    public static final Integer CONSTRAINT_WHITESPACE_DEFAULT = WHITESPACE_PRESERVE;

    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected DataType.ValueConstraint patternConstraint;
    protected DataType.ValueConstraint whiteSpaceConstraint;

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public StringDataType(String name) {
        super(name, String.class);
    }

    public void erase() {
        super.erase();
        patternConstraint = null;
        whiteSpaceConstraint = null;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof StringDataType) {
            StringDataType dataType = (StringDataType)origin;
            patternConstraint = inheritConstraint(dataType.patternConstraint);
        }
    }

    /**
     * Returns the regular expression pattern used to validate values for this datatype.
     * @return the pattern as a <code>String</code>, or <code>null</code> if there is no pattern.
     */
    public Pattern getPattern() {
        if (patternConstraint == null) {
            return CONSTRAINT_PATTERN_DEFAULT;
        } else {
            return (Pattern) patternConstraint.getValue();
        }
    }

    /**
     * Returns the 'pattern' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getPatternConstraint() {
        if (patternConstraint == null) patternConstraint = new ValueConstraint(CONSTRAINT_PATTERN, CONSTRAINT_PATTERN_DEFAULT);
        return patternConstraint;
    }

    /**
     * Sets the regular expression pattern used to validate values for this datatype.
     * @param pattern the pattern as a <code>Pattern</code>, or <code>null</code> if no pattern should be applied.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setPattern(Pattern value) {
        return getPatternConstraint().setValue(value);
    }

    /**
     * Returns the whitespace value.
     * @return one of the constants {@link #WHITESPACE_PRESERVE}, {@link #WHITESPACE_REPLACE},  or {@link #WHITESPACE_COLLAPSE}
     */
    public Integer getWhiteSpace() {
        if (whiteSpaceConstraint == null) {
            return CONSTRAINT_WHITESPACE_DEFAULT;
        } else {
            return (Integer) whiteSpaceConstraint.getValue();
        }
    }

    /**
     * Returns the 'whitespace' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getWhiteSpaceConstraint() {
        if (whiteSpaceConstraint == null) whiteSpaceConstraint = new ValueConstraint(CONSTRAINT_WHITESPACE, CONSTRAINT_WHITESPACE_DEFAULT);
        return whiteSpaceConstraint;
    }

    /**
     * Sets the whitspeace value used to validate values for this datatype.
     * @param whitespace one of the constants {@link #WHITESPACE_PRESERVE}, {@link #WHITESPACE_REPLACE}, or {@link #WHITESPACE_COLLAPSE}
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DataType.ValueConstraint setWhiteSpace(Integer value) {
        if (WHITESPACE_COLLAPSE.equals(value) || WHITESPACE_PRESERVE.equals(value) || WHITESPACE_REPLACE.equals(value)) {
            return getWhiteSpaceConstraint().setValue(value);
        } else {
            throw new IllegalArgumentException("value should be either WHITESPACE_PRESERVE, WHITESPACE_REPLACE or WHITESPACE_COLLAPSE");
        }
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        if (value != null) {
            String stringValue = Casting.toString(value);
            Pattern pattern = getPattern();
            if (pattern != null) {
                if (! pattern.matcher(stringValue).matches()) {
                    errors = addError(errors, getPatternConstraint(), value);
                }
            }
        }
        return errors;
    }

    public Object process(int action, Node node, Field field, Object value, int processingType) {
        value = super.process(action, node, field, value, processingType);
        if (value instanceof String && action == PROCESS_SET) {
            Integer whiteSpace = getWhiteSpace();
            if (whiteSpace.equals(WHITESPACE_REPLACE)) {
                // replace all whitespace
                value = ((String)value).replaceAll("\\s"," ");
            } else if (whiteSpace.equals(WHITESPACE_COLLAPSE)) {
                // collapse all whitespace
                value = ((String)value).replaceAll("\\s+"," ").trim();
            }
        }
        return value;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getPattern() != null) {
            buf.append("pattern:").append(getPattern()).append("\n");
        }
        Integer whiteSpace = getWhiteSpace();
        if (whiteSpace.equals(WHITESPACE_REPLACE)) {
            buf.append("whitespace: replace\n");
        } else if (whiteSpace.equals(WHITESPACE_COLLAPSE)) {
            buf.append("whitespace: collapse\n");
        }
        return buf.toString();
    }

}
