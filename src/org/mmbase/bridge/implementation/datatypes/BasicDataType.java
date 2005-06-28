/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.MMBaseType;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @since MMBase-1.8
 */
public class BasicDataType extends Parameter {

    /**
     * Constructor for node field.
     */
    public BasicDataType(String name) {
        super(name, MMBaseType.TYPE_UNKNOWN);
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
    protected BasicDataType(String name, BasicDataType dataType) {
        super(name,dataType);
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicDataType(name,this);
    }

}
