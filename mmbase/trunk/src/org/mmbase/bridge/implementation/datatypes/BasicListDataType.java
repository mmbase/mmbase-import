/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.ListDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicListDataType.java,v 1.6 2005-07-14 14:13:40 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.ListDataType
 * @since MMBase-1.8
 */
public class BasicListDataType extends AbstractDataType implements ListDataType {

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
    public BasicListDataType(String name) {
        super(name, List.class);
        minSizeProperty = createProperty(PROPERTY_MINSIZE, PROPERTY_MINSIZE_DEFAULT);
        maxSizeProperty = createProperty(PROPERTY_MAXSIZE, PROPERTY_MAXSIZE_DEFAULT);
        itemDataTypeProperty = createProperty(PROPERTY_ITEMDATATYPE, PROPERTY_ITEMDATATYPE_DEFAULT);
    }

    public int getMinSize() {
        return Casting.toInt(getMinSizeProperty().getValue());
    }

    public DataType.Property getMinSizeProperty() {
        return minSizeProperty;
    }

    public DataType.Property setMinSize(int value) {
        return setProperty(getMinSizeProperty(), new Integer(value));
    }

    public int getMaxSize() {
        return Casting.toInt(getMaxSizeProperty().getValue());
    }

    public DataType.Property getMaxSizeProperty() {
        return maxSizeProperty;
    }

    public DataType.Property setMaxSize(int value) {
        return setProperty(getMaxSizeProperty(), new Integer(value));
    }

    public DataType getItemDataType() {
        return (DataType)getItemDataTypeProperty().getValue();
    }

    public DataType.Property getItemDataTypeProperty() {
        return itemDataTypeProperty;
    }

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

    public Object clone(String name) {
        BasicListDataType clone = (BasicListDataType)super.clone(name);
        clone.minSizeProperty = (DataTypeProperty)getMinSizeProperty().clone(clone);
        clone.maxSizeProperty = (DataTypeProperty)getMaxSizeProperty().clone(clone);
        clone.itemDataTypeProperty = (DataTypeProperty)getItemDataTypeProperty().clone(clone);
        return clone;
    }

}
