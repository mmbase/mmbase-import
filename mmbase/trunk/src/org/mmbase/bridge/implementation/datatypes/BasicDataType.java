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
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.bridge.util.DataTypes;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDataType.java,v 1.3 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @since MMBase-1.8
 */
public class BasicDataType extends AbstractDataType {

    /**
     * Constructor for node field.
     */
    public BasicDataType(String name) {
        this(name, Object.class);
    }

    /**
     * Constructor for node field.
     */
    public BasicDataType(String name, Class type) {
        super(name, type);
    }

    /**
     * Create a node field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    public BasicDataType(String name, DataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return DataTypes.classToBaseType(getTypeAsClass());
    }

}
