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
import org.mmbase.util.LocalizedString;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class ListDataType extends AbstractLengthDataType<List> {
    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected ItemRestriction itemRestriction = new ItemRestriction(Constants.DATATYPE_UNKNOWN);

    /**
     * Constructor for List field.
     */
    public ListDataType(String name) {
        super(name, List.class);
    }


    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof ListDataType) {
            ListDataType dataType = (ListDataType)origin;
            itemRestriction.inherit(dataType.itemRestriction);
         }
    }
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof ListDataType) {
            ListDataType dataType = (ListDataType)origin;
            itemRestriction = new ItemRestriction(dataType.itemRestriction);
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
        return itemRestriction.getItemDataType();
    }

    /**
     * Returns the 'itemDataType' restriction, containing the value, errormessages, and fixed status of this attribute.
     * @return the restriction as a {@link DataType.Restriction}
     */
    public DataType.Restriction getItemDataTypeRestriction() {
        return itemRestriction;
    }

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public void setItemDataType(DataType value) {
        itemRestriction.setValue(value);
    }

    public int getEnforceStrength() {
        return Math.max(super.getEnforceStrength(), itemRestriction.getEnforceStrength());
    }

    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value, node, field);
        errors = itemRestriction.validate(errors, castValue, node, field);
        return errors;
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.append("items: " + getItemDataType());
        return buf;
    }

    protected class ItemRestriction extends AbstractRestriction {
        ItemRestriction(ItemRestriction me) {
            super(me);
        }
        ItemRestriction(DataType v) {
            super("itemDataType", v);
        }
        DataType getItemDataType() {
            return (DataType) value;
        }

        protected boolean simpleValid(Object v, Node node, Field field) {
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
            if (! enforce(v, node, field)) {
                return errors;
            }
            DataType itemDataType = getItemDataType();
            if (itemDataType == Constants.DATATYPE_UNKNOWN) return errors;
            List listValue = Casting.toList(v);
            for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                Collection col = itemDataType.validate(i.next());
                try {
                    if (col != VALID) {
                        if (errors == VALID) errors = new ArrayList<LocalizedString>();
                        errors.addAll(col);
                    }
                } catch (ClassCastException cce) {
                    errors = addError(errors, v, node, field);
                }
            }
            return errors;
        }

    }

}
