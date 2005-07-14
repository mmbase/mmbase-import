/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.StringDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicStringDataType.java,v 1.5 2005-07-14 11:37:53 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.StringDataType
 * @since MMBase-1.8
 */
public class BasicStringDataType extends BasicBigDataType implements StringDataType {

    public static final String PROPERTY_PATTERN = "pattern";
    public static final String PROPERTY_PATTERN_DEFAULT = null;

    /**
     * Constructor for string field.
     */
    public BasicStringDataType(String name) {
        super(name, String.class);
    }

    public String getPattern() {
        return (String) getPatternProperty().getValue();
    }

    public DataType.Property getPatternProperty() {
        return getProperty(PROPERTY_PATTERN, PROPERTY_PATTERN_DEFAULT);
    }

    public DataType.Property setPattern(String value) {
        return setProperty(PROPERTY_PATTERN, value);
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
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

}
