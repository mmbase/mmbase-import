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
 * @author Michiel Meeuwissen
 * @version $Id: ListDataType.java,v 1.12 2005-10-12 00:01:04 michiel Exp $
 * @since MMBase-1.8
 */
public class ListDataType extends AbstractLengthDataType {

    protected ItemConstraint itemConstraint = new ItemConstraint(Constants.DATATYPE_UNKNOWN);

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
            itemConstraint = new ItemConstraint(dataType.itemConstraint);
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
        return itemConstraint.getItemDataType();
    }

    /**
     * Returns the 'itemDataType' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType.ValueConstraint}
     */
    public DataType.ValueConstraint getItemDataTypeConstraint() {
        return itemConstraint;
    }

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType.ValueConstraint setItemDataType(DataType value) {
        return itemConstraint.setValue(value);
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = itemConstraint.validate(errors, castedValue, node, field);
        return errors;
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        buf.append("items: " + getItemDataType());
        return buf;
    }

    protected class ItemConstraint extends AbstractValueConstraint {
        ItemConstraint(ItemConstraint me) {
            super(me);
        }
        ItemConstraint(DataType v) {
            super("itemDataType", v);
        }
        DataType getItemDataType() {
            return (DataType) value;
        }

        public boolean valid(Object v, Node node, Field field) {
            DataType itemDataType = getItemDataType();
            if (itemDataType == Constants.DATATYPE_UNKNOWN) return true;
            List listValue = Casting.toList(v);
            for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                try {
                    Collection col = itemDataType.validate(i.next());
                    if (col != VALID) return false;
                } catch (ClassCastException cce) {
                    return false;
                }
            }
            return true;
        }

        protected Collection validate(Collection errors, Object v, Node node, Field field) {
            if (! enforce(node, field)) {
                return errors;
            }
            DataType itemDataType = getItemDataType();
            if (itemDataType == Constants.DATATYPE_UNKNOWN) return errors;
            List listValue = Casting.toList(v);
            for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                Collection col = itemDataType.validate(i.next());
                try {
                    if (col != VALID) {
                        if (errors == VALID) errors = new ArrayList();
                        errors.addAll(col);
                    }
                } catch (ClassCastException cce) {
                    errors = addError(errors, v);
                }
            }
            return errors;
        }

    }

}
