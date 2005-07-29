/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: ListDataType.java,v 1.2 2005-07-29 14:52:37 pierre Exp $
 * @since MMBase-1.8
 */
public class ListDataType extends DataType {

    public static final String PROPERTY_MINSIZE = "minSize";
    public static final Integer PROPERTY_MINSIZE_DEFAULT = new Integer(-1);

    public static final String PROPERTY_MAXSIZE = "maxSize";
    public static final Integer PROPERTY_MAXSIZE_DEFAULT = new Integer(-1);

    public static final String PROPERTY_ITEMDATATYPE = "itemDataType";
    public static final Integer PROPERTY_ITEMDATATYPE_DEFAULT = null;

    protected DataType.Property minSizeProperty = null;
    protected DataType.Property maxSizeProperty = null;
    protected DataType.Property itemDataTypeProperty = null;

    /**
     * Constructor for List field.
     */
    public ListDataType(String name) {
        super(name, List.class);
        minSizeProperty = createProperty(PROPERTY_MINSIZE, PROPERTY_MINSIZE_DEFAULT);
        maxSizeProperty = createProperty(PROPERTY_MAXSIZE, PROPERTY_MAXSIZE_DEFAULT);
        itemDataTypeProperty = createProperty(PROPERTY_ITEMDATATYPE, PROPERTY_ITEMDATATYPE_DEFAULT);
    }

    /**
     * Returns the minimum size for the list.
     * @return the minimum size as an <code>int</code>, or <code>-1</code> if there is no minimum.
     */
    public int getMinSize() {
        return Casting.toInt(getMinSizeProperty().getValue());
    }

    /**
     * Returns the 'minsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMinSizeProperty() {
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
        return Casting.toInt(getMaxSizeProperty().getValue());
    }

    /**
     * Returns the 'maxsize' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getMaxSizeProperty() {
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
        return (DataType)getItemDataTypeProperty().getValue();
    }

    /**
     * Returns the 'itemDataType' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getItemDataTypeProperty() {
        return itemDataTypeProperty;
    }

    /**
     * Sets the datatype of items in this list.
     * @param value the datatype as a DataType object, <code>null</code> if there are no restrictions
     */
    public DataType.Property setItemDataType(DataType value) {
        return setProperty(getItemDataTypeProperty(), value);
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
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
                        itemDataType.validate(i.next());
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



    public Object clone(String name) {
        ListDataType clone = (ListDataType)super.clone(name);
        clone.minSizeProperty = (DataType.Property)getMinSizeProperty().clone(clone);
        clone.maxSizeProperty = (DataType.Property)getMaxSizeProperty().clone(clone);
        clone.itemDataTypeProperty = (DataType.Property)getItemDataTypeProperty().clone(clone);
        return clone;
    }

}
