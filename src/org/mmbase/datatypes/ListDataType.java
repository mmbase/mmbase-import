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
 * @version $Id: ListDataType.java,v 1.6 2005-08-18 12:21:51 pierre Exp $
 * @since MMBase-1.8
 */
public class ListDataType extends DataType {

    public static final String PROPERTY_MINSIZE = "minSize";
    public static final Integer PROPERTY_MINSIZE_DEFAULT = new Integer(-1);

    public static final String PROPERTY_MAXSIZE = "maxSize";
    public static final Integer PROPERTY_MAXSIZE_DEFAULT = new Integer(-1);

    public static final String PROPERTY_ITEMDATATYPE = "itemDataType";
    public static final DataType PROPERTY_ITEMDATATYPE_DEFAULT = null;

    protected DataType.Property minSizeProperty;
    protected DataType.Property maxSizeProperty;
    protected DataType.Property itemDataTypeProperty;

    /**
     * Constructor for List field.
     */
    public ListDataType(String name) {
        super(name, List.class);
    }

    public void erase() {
        super.erase();
        minSizeProperty = null;
        maxSizeProperty = null;
        itemDataTypeProperty = null;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof ListDataType) {
            ListDataType dataType = (ListDataType)origin;
            minSizeProperty = inheritProperty(dataType.minSizeProperty);
            maxSizeProperty = inheritProperty(dataType.maxSizeProperty);
            itemDataTypeProperty = inheritProperty(dataType.itemDataTypeProperty);
         }
    }

    /**
     * Returns the minimum size for the list.
     * @return the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public int getMinSize() {
        if (minSizeProperty == null) {
            return PROPERTY_MINSIZE_DEFAULT.intValue();
        } else {
            return Casting.toInt(minSizeProperty.getValue());
        }
    }

    /**
     * Returns the 'minsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMinSizeProperty() {
        if (minSizeProperty == null) minSizeProperty = createProperty(PROPERTY_MINSIZE, PROPERTY_MINSIZE_DEFAULT);
        return minSizeProperty;
    }

    /**
     * Sets the minimum size for the list.
     * @param value the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public DataType.Property setMinSize(int value) {
        return setProperty(getMinSizeProperty(), new Integer(value));
    }

    /**
     * Returns the maximum size for the list.
     * @return the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public int getMaxSize() {
        if (maxSizeProperty == null) {
            return PROPERTY_MAXSIZE_DEFAULT.intValue();
        } else {
            return Casting.toInt(maxSizeProperty.getValue());
        }
    }

    /**
     * Returns the 'maxsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMaxSizeProperty() {
        if (maxSizeProperty == null) maxSizeProperty = createProperty(PROPERTY_MAXSIZE, PROPERTY_MAXSIZE_DEFAULT);
        return maxSizeProperty;
    }

    /**
     * Sets the maximum size for the list.
     * @param value the maximum size as an <code>int</code>, or <code>-1</code> if there is no maximum.
     */
    public DataType.Property setMaxSize(int value) {
        return setProperty(getMaxSizeProperty(), new Integer(value));
    }

    /**
     * Returns the datatype of items in this list.
     * @return the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType getItemDataType() {
        if (itemDataTypeProperty == null) {
            return PROPERTY_ITEMDATATYPE_DEFAULT;
        } else {
            return (DataType)itemDataTypeProperty.getValue();
        }
    }

    /**
     * Returns the 'itemDataType' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getItemDataTypeProperty() {
        if (itemDataTypeProperty == null) itemDataTypeProperty = createProperty(PROPERTY_ITEMDATATYPE, PROPERTY_ITEMDATATYPE_DEFAULT);
        return itemDataTypeProperty;
    }

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType.Property setItemDataType(DataType value) {
        return setProperty(getItemDataTypeProperty(), value);
    }

    public void validate(Object value, Node node, Field field, Cloud cloud) {
        super.validate(value, node, field, cloud);
        if (value !=null) {
            List listValue = Casting.toList(value);
            int minSize = getMinSize();
            if (minSize > 0) {
                if (listValue.size() < minSize) {
                    failOnValidate(getMinSizeProperty(), value, cloud);
                }
            }
            int maxSize = getMaxSize();
            if (maxSize > -1) {
                if (listValue.size() > maxSize) {
                    failOnValidate(getMaxSizeProperty(), value, cloud);
                }
            }
            // test list item values
            DataType itemDataType = getItemDataType();
            if (itemDataType != null) {
                for (Iterator i = listValue.iterator(); i.hasNext(); ) {
                    try {
                        itemDataType.validate(i.next(), cloud);
                    } catch (ClassCastException cce) {
                        failOnValidate(getItemDataTypeProperty(), value, cloud);
                    }
                }
            }
        }
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
