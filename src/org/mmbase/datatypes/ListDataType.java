/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: ListDataType.java,v 1.9 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class ListDataType extends AbstractLengthDataType {

    public static final DataType CONSTRAINT_ITEMDATATYPE_DEFAULT = null;
    public static final String CONSTRAINT_ITEMDATATYPE = "itemDataType";
    protected DataType.ValueConstraint itemDataTypeConstraint;

    /**
     * Constructor for List field.
     */
    public ListDataType(String name) {
        super(name, List.class);
    }


    public void inherit(BasicDataType origin) {
        super.inherit(origin);
        if (origin instanceof ListDataType) {
            ListDataType dataType = (ListDataType)origin;
            itemDataTypeConstraint = new AbstractValueConstraint(dataType.getItemDataTypeConstraint());
         }
    }

    public long getLength(Object value) {
        return ((Collection) value).size();
    }

    /**
     * Returns the datatype of items in this list.
     * @return the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType getItemDataType() {
        if (itemDataTypeConstraint == null) {
            return CONSTRAINT_ITEMDATATYPE_DEFAULT;
        } else {
            return (DataType)itemDataTypeConstraint.getValue();
        }
    }

    /**
     * Returns the 'itemDataType' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType.ValueConstraint}
     */
    public DataType.ValueConstraint getItemDataTypeConstraint() {
        if (itemDataTypeConstraint == null) itemDataTypeConstraint = new AbstractValueConstraint(CONSTRAINT_ITEMDATATYPE, CONSTRAINT_ITEMDATATYPE_DEFAULT);
        return itemDataTypeConstraint;
    }

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType.ValueConstraint setItemDataType(DataType value) {
        return getItemDataTypeConstraint().setValue(value);
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        if (value != null) {
            List listValue = Casting.toList(value);
            // test list item values
            DataType itemDataType = getItemDataType();
            if (itemDataType != null) {
                for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                    try {
                        Collection col = itemDataType.validate(i.next());
                        if (col != VALID) {
                            if (errors == VALID) errors = new ArrayList();
                            errors.addAll(col);
                        }
                    } catch (ClassCastException cce) {
                        errors = addError(errors, getItemDataTypeConstraint(), value);
                    }
                }
            }
        }
        return errors;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        return buf.toString();
    }

}
