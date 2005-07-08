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
import org.mmbase.bridge.datatypes.ListDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicListDataType.java,v 1.2 2005-07-08 12:23:45 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.ListDataType
 * @since MMBase-1.8
 */
public class BasicListDataType extends AbstractDataType implements ListDataType {

    protected int minSize = -1;
    protected int maxSize = -1;
    protected DataType itemDataType = null;

    /**
     * Constructor for List field.
     */
    public BasicListDataType(String name) {
        super(name, List.class);
    }

    /**
     * Create a List field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicListDataType(String name, BasicListDataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_LIST;
    }

    public int getMinSize() {
        return minSize;
    }

    public ListDataType setMinSize(int value) {
        edit();
        minSize = value;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ListDataType setMaxSize(int value) {
        edit();
        maxSize = value;
        return this;
    }

    public DataType getListItemDataType() {
        return itemDataType;
    }

    public ListDataType setListItemDataType(DataType value) {
        edit();
        itemDataType = value;
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        List listValue = Casting.toList(value);
        if (minSize > 0) {
            if (listValue == null || listValue.size() < minSize) {
                throw new IllegalArgumentException("The list may not be smaller than  "+minSize + " items.");
            }
        }
        if (maxSize > -1) {
            if (listValue != null && listValue.size() > maxSize) {
                throw new IllegalArgumentException("The list may not be larger than  "+maxSize + " items.");
            }
        }
        // test list item values
        if (itemDataType != null && listValue != null) {
            for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                itemDataType.validate(i.next());
            }
        }
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicListDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        ListDataType listField = (ListDataType)dataType;
        setMinSize(listField.getMinSize());
        setMaxSize(listField.getMaxSize());
        DataType itemDataType = listField.getListItemDataType();
        if (itemDataType != null) {
            itemDataType = itemDataType.copy(key+".item");
        }
        setListItemDataType(itemDataType);
    }

}
