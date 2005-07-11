/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.datatypes.FloatDataType;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicFloatDataType.java,v 1.4 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.FloatDataType
 * @since MMBase-1.8
 */
public class BasicFloatDataType extends BasicNumberDataType implements FloatDataType {

    /**
     * Constructor for Float field.
     */
    public BasicFloatDataType(String name) {
        super(name, Float.class);
    }

    /**
     * Create a Float field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    public BasicFloatDataType(String name, DataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_FLOAT;
    }

    public Float getMin() {
        Number min = getMinValue();
        if (min instanceof Float) {
            return (Float)min;
        } else {
            return new Float(min.floatValue());
        }
    }

    public Float getMax() {
        Number max = getMaxValue();
        if (max instanceof Float) {
            return (Float)max;
        } else {
            return new Float(max.floatValue());
        }
    }
}
