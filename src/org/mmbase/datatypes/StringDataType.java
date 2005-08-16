/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: StringDataType.java,v 1.8 2005-08-16 14:43:50 pierre Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends BigDataType {

    public static final Integer WHITESPACE_PRESERVE = new Integer(0);
    public static final Integer WHITESPACE_REPLACE = new Integer(1);
    public static final Integer WHITESPACE_COLLAPSE = new Integer(2);

    public static final String PROPERTY_PATTERN = "pattern";
    public static final String PROPERTY_PATTERN_DEFAULT = null;

    public static final String PROPERTY_WHITESPACE = "whiteSpace";
    public static final Integer PROPERTY_WHITESPACE_DEFAULT = WHITESPACE_PRESERVE;

    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected DataType.Property patternProperty;
    protected DataType.Property whiteSpaceProperty;

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public StringDataType(String name) {
        super(name, String.class);
    }

    public void erase() {
        super.erase();
        patternProperty = createProperty(PROPERTY_PATTERN, PROPERTY_PATTERN_DEFAULT);
        whiteSpaceProperty = createProperty(PROPERTY_WHITESPACE, PROPERTY_WHITESPACE_DEFAULT);
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof StringDataType) {
            StringDataType dataType = (StringDataType)origin;
            patternProperty = (DataType.Property)dataType.getPatternProperty().clone(this);
            whiteSpaceProperty = (DataType.Property)dataType.getWhiteSpaceProperty().clone(this);
        }
    }

    /**
     * Returns the regular expression pattern used to validate values for this datatype.
     * @return the pattern as a <code>String</code>, or <code>null</code> if there is no pattern.
     */
    public String getPattern() {
        return (String) getPatternProperty().getValue();
    }

    /**
     * Returns the 'pattern' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getPatternProperty() {
        return patternProperty;
    }

    /**
     * Sets the regular expression pattern used to validate values for this datatype.
     * @param pattern the pattern as a <code>String</code>, or <code>null</code> if no pattern should be applied.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DataType.Property setPattern(String value) {
        return setProperty(getPatternProperty(), value);
    }

    /**
     * Returns the whitespace value.
     * @return one of the constants {@link #WHITESPACE_PRESERVE}, {@link #WHITESPACE_REPLACE},  or {@link #WHITESPACE_COLLAPSE}
     */
    public Integer getWhiteSpace() {
        return (Integer) getWhiteSpaceProperty().getValue();
    }

    /**
     * Returns the 'whitespace' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getWhiteSpaceProperty() {
        return whiteSpaceProperty;
    }

    /**
     * Sets the whitspeace value used to validate values for this datatype.
     * @param whitespace one of the constants {@link #WHITESPACE_PRESERVE}, {@link #WHITESPACE_REPLACE}, or {@link #WHITESPACE_COLLAPSE}
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public DataType.Property setWhiteSpace(Integer value) {
        if (WHITESPACE_COLLAPSE.equals(value) || WHITESPACE_PRESERVE.equals(value) || WHITESPACE_REPLACE.equals(value)) {
            return setProperty(getWhiteSpaceProperty(), value);
        } else {
            throw new IllegalArgumentException("value should be either WHITESPACE_PRESERVE, WHITESPACE_REPLACE or WHITESPACE_COLLAPSE");
        }
    }

    public void validate(Object value, Field field, Cloud cloud) {
        super.validate(value, field, cloud);
        if (value != null) {
            String stringValue = Casting.toString(value);
            String pattern = getPattern();
            if (pattern != null) {
                if (!stringValue.matches(pattern)) {
                    failOnValidate(getPatternProperty(), value, cloud);
                }
            }
        }
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
