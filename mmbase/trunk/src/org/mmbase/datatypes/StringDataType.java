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
 * @version $Id: StringDataType.java,v 1.15 2005-10-02 16:55:03 michiel Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends BigDataType {

    public static final String   CONSTRAINT_PATTERN = "pattern";
    public static final Pattern CONSTRAINT_PATTERN_DEFAULT = Pattern.compile(".*");

    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected DataType.ValueConstraint patternConstraint;

    private boolean isPassword = false;

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
     * @return the pattern.
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
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MMBase)
     */
    public DataType.ValueConstraint setPattern(Pattern value) {
        return getPatternConstraint().setValue(value);
    }

    public boolean isPassword() {
        return isPassword;
    }
    public void setPassword(boolean pw) {
        isPassword = pw;
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

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getPattern() != null) {
            buf.append(" pattern:").append(getPattern()).append(" ");
        }
        return buf.toString();
    }

}
