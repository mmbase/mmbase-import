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
import org.mmbase.bridge.datatypes.IntegerDataType;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicIntegerDataType.java,v 1.4 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.IntegerDataType
 * @since MMBase-1.8
 */
public class BasicIntegerDataType extends BasicNumberDataType implements IntegerDataType {

    /**
     * Constructor for integer field.
     */
    public BasicIntegerDataType(String name) {
        super(name, Integer.class);
    }

    /**
     * Create a integer field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    public BasicIntegerDataType(String name, DataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_INTEGER;
    }

    public Integer getMin() {
        Number min = getMinValue();
        if (min instanceof Integer) {
            return (Integer)min;
        } else {
            return new Integer(min.intValue());
        }
    }

    public Integer getMax() {
        Number max = getMaxValue();
        if (max instanceof Integer) {
            return (Integer)max;
        } else {
            return new Integer(max.intValue());
        }
    }

}
