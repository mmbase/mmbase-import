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
 * @version $Id: ListDataType.java,v 1.8 2005-09-06 21:11:30 michiel Exp $
 * @since MMBase-1.8
 */
public class ListDataType extends DataType {

    public static final String CONSTRAINT_MINSIZE = "minSize";
    public static final Integer CONSTRAINT_MINSIZE_DEFAULT = new Integer(-1);

    public static final String CONSTRAINT_MAXSIZE = "maxSize";
    public static final Integer CONSTRAINT_MAXSIZE_DEFAULT = new Integer(-1);

    public static final String CONSTRAINT_ITEMDATATYPE = "itemDataType";
    public static final DataType CONSTRAINT_ITEMDATATYPE_DEFAULT = null;

    protected DataType.ValueConstraint minSizeConstraint;
    protected DataType.ValueConstraint maxSizeConstraint;
    protected DataType.ValueConstraint itemDataTypeConstraint;

    /**
     * Constructor for List field.
     */
    public ListDataType(String name) {
        super(name, List.class);
    }

    public void erase() {
        super.erase();
        minSizeConstraint = null;
        maxSizeConstraint = null;
        itemDataTypeConstraint = null;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof ListDataType) {
            ListDataType dataType = (ListDataType)origin;
            minSizeConstraint = inheritConstraint(dataType.minSizeConstraint);
            maxSizeConstraint = inheritConstraint(dataType.maxSizeConstraint);
            itemDataTypeConstraint = inheritConstraint(dataType.itemDataTypeConstraint);
         }
    }

    /**
     * Returns the minimum size for the list.
     * @return the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public int getMinSize() {
        if (minSizeConstraint == null) {
            return CONSTRAINT_MINSIZE_DEFAULT.intValue();
        } else {
            return Casting.toInt(minSizeConstraint.getValue());
        }
    }

    /**
     * Returns the 'minsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getMinSizeConstraint() {
        if (minSizeConstraint == null) minSizeConstraint = new ValueConstraint(CONSTRAINT_MINSIZE, CONSTRAINT_MINSIZE_DEFAULT);
        return minSizeConstraint;
    }

    /**
     * Sets the minimum size for the list.
     * @param value the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public DataType.ValueConstraint setMinSize(int value) {
        return getMinSizeConstraint().setValue(new Integer(value));
    }

    /**
     * Returns the maximum size for the list.
     * @return the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public int getMaxSize() {
        if (maxSizeConstraint == null) {
            return CONSTRAINT_MAXSIZE_DEFAULT.intValue();
        } else {
            return Casting.toInt(maxSizeConstraint.getValue());
        }
    }

    /**
     * Returns the 'maxsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getMaxSizeConstraint() {
        if (maxSizeConstraint == null) maxSizeConstraint = new ValueConstraint(CONSTRAINT_MAXSIZE, CONSTRAINT_MAXSIZE_DEFAULT);
        return maxSizeConstraint;
    }

    /**
     * Sets the maximum size for the list.
     * @param value the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public DataType.ValueConstraint setMaxSize(int value) {
        return getMaxSizeConstraint().setValue(new Integer(value));
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
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getItemDataTypeConstraint() {
        if (itemDataTypeConstraint == null) itemDataTypeConstraint = new ValueConstraint(CONSTRAINT_ITEMDATATYPE, CONSTRAINT_ITEMDATATYPE_DEFAULT);
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
            int minSize = getMinSize();
            if (minSize > 0) {
                if (listValue.size() < minSize) {
                    errors = addError(errors, getMinSizeConstraint(), value);
                }
            }
            int maxSize = getMaxSize();
            if (maxSize > -1) {
                if (listValue.size() > maxSize) {
                    errors = addError(errors, getMaxSizeConstraint(), value);
                }
            }
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
        if (getMinSize() > -1) {
            buf.append("minSize:" + getMinSize() + "\n");
        }
        if (getMaxSize() > -1) {
            buf.append("maxSize:" + getMaxSize() + "\n");
        }
        return buf.toString();
    }

}
