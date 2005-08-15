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
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: StringDataType.java,v 1.5 2005-08-15 16:38:20 pierre Exp $
 * @since MMBase-1.8
 */
public class StringDataType extends BigDataType {

    public static final String PROPERTY_PATTERN = "pattern";
    public static final String PROPERTY_PATTERN_DEFAULT = null;

    private static final Logger log = Logging.getLoggerInstance(StringDataType.class);

    protected DataType.Property patternProperty;

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
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof StringDataType) {
            StringDataType dataType = (StringDataType)origin;
            patternProperty = (DataType.Property)dataType.getPatternProperty().clone(this);
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

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (getPattern() != null) {
            buf.append("pattern:").append(getPattern()).append("\n");
        }
        return buf.toString();
    }

}
