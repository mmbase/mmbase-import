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
 * @version $Id: BasicStringDataType.java,v 1.6 2005-07-14 14:13:40 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.StringDataType
 * @since MMBase-1.8
 */
public class BasicStringDataType extends BasicBigDataType implements StringDataType {

    public static final String PROPERTY_PATTERN = "pattern";
    public static final String PROPERTY_PATTERN_DEFAULT = null;

    protected DataType.Property patternProperty = null;

    /**
     * Constructor for string field.
     */
    public BasicStringDataType(String name) {
        super(name, String.class);
        patternProperty = createProperty(PROPERTY_PATTERN, PROPERTY_PATTERN_DEFAULT);
    }

    public String getPattern() {
        return (String) getPatternProperty().getValue();
    }

    public DataType.Property getPatternProperty() {
        return patternProperty;
    }

    public DataType.Property setPattern(String value) {
        return setProperty(getPatternProperty(), value);
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

    public Object clone(String name) {
        BasicStringDataType clone = (BasicStringDataType)super.clone(name);
        clone.patternProperty = (DataTypeProperty)getPatternProperty().clone(clone);
        return clone;
    }

}
