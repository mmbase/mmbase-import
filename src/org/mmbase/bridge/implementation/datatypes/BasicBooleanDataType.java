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
import org.mmbase.bridge.datatypes.BooleanDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBooleanDataType.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BooleanDataType
 * @since MMBase-1.8
 */
public class BasicBooleanDataType extends Parameter implements BooleanDataType {

    /**
     * Constructor for boolean field.
     */
    public BasicBooleanDataType(String name) {
        super(name, MMBaseType.TYPE_BOOLEAN);
    }

    /**
     * Create a boolean field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicBooleanDataType(String name, BasicBooleanDataType dataType) {
        super(name,dataType);
    }

    public void validate(Object value) {
        super.validate(value);
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicBooleanDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
    }

}
