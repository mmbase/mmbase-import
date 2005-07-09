/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.bridge.implementation.datatypes.*;
import org.mmbase.module.core.MMObjectNode;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataTypes.java,v 1.5 2005-07-09 11:08:54 nklasens Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public class DataTypes {

    private static Map finalDataTypes = new HashMap();

    public static int classToBaseType(Class type) {
        if (type == null) {
            return Field.TYPE_UNKNOWN;
        } else if (type.isArray() && type.getComponentType() == Byte.TYPE) {
            return Field.TYPE_BINARY;
        } else if (type == Integer.class || type == Integer.TYPE) {
            return Field.TYPE_INTEGER;
        } else if (type == Long.class || type == Long.TYPE) {
            return Field.TYPE_LONG;
        } else if (type == Double.class || type == Double.TYPE) {
            return Field.TYPE_DOUBLE;
        } else if (type == Float.class || type == Float.TYPE) {
            return Field.TYPE_FLOAT;
        } else if (type == String.class) {
            return Field.TYPE_STRING;
        } else if (type == org.w3c.dom.Document.class) {
            return Field.TYPE_XML;
        } else if (type == Node.class || type == MMObjectNode.class) {
            return Field.TYPE_NODE;
        } else if (type == Date.class) {
            return Field.TYPE_DATETIME;
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            return Field.TYPE_BOOLEAN;
        } else if (type == List.class) {
            return Field.TYPE_LIST;
        } else {
            return Field.TYPE_UNKNOWN;
        }
    }

    public static Class baseTypeToClass(int type) {
        switch (type) {
        case Field.TYPE_STRING : return String.class;
        case Field.TYPE_INTEGER : return Integer.class;
        case Field.TYPE_BINARY: return byte[].class;
        case Field.TYPE_FLOAT: return Float.class;
        case Field.TYPE_DOUBLE: return Double.class;
        case Field.TYPE_LONG: return Long.class;
        case Field.TYPE_XML: return org.w3c.dom.Document.class;
        case Field.TYPE_NODE: return org.mmbase.module.core.MMObjectNode.class; // org.mmbase.bridge.Node.class;
        case Field.TYPE_DATETIME: return java.util.Date.class;
        case Field.TYPE_BOOLEAN: return Boolean.class;
        case Field.TYPE_LIST: return List.class;
        default: return null;
        }
    }

    /**
     * Create an instance of a dataType based on the MMBase type passed.
     */
    public static DataType createDataType(String name, int type) {
        DataType dataType = null;
        switch (type) {
        case Field.TYPE_BINARY : dataType = new BasicBinaryDataType(name); break;
        case Field.TYPE_INTEGER : dataType = new BasicIntegerDataType(name); break;
        case Field.TYPE_LONG : dataType = new BasicLongDataType(name); break;
        case Field.TYPE_DOUBLE : dataType = new BasicDoubleDataType(name); break;
        case Field.TYPE_FLOAT : dataType = new BasicFloatDataType(name); break;
        case Field.TYPE_STRING : dataType = new BasicStringDataType(name); break;
        case Field.TYPE_XML: dataType = new BasicXmlDataType(name); break;
        case Field.TYPE_NODE : dataType = new BasicNodeDataType(name); break;
        case Field.TYPE_DATETIME : dataType = new BasicDateTimeDataType(name); break;
        case Field.TYPE_BOOLEAN : dataType = new BasicBooleanDataType(name); break;
        case Field.TYPE_LIST : dataType = new BasicListDataType(name); break;
        default: {
            dataType = new BasicDataType(name);
        }
        }
        return dataType;
    }

    /**
     * Create an instance of a dataType based on the MMBase type passed.
     */
    public static DataType createDataType(String name, Class type) {
        DataType dataType = null;
        if (type == null) {
            dataType = new BasicDataType(name, type);
        } else if (type.isArray() && type.getComponentType() == Byte.TYPE) {
            dataType = new BasicBinaryDataType(name);
        } else if (type == Integer.class || type == Integer.TYPE) {
            dataType = new BasicIntegerDataType(name);
        } else if (type == Long.class || type == Long.TYPE) {
            dataType = new BasicLongDataType(name);
        } else if (type == Double.class || type == Double.TYPE) {
            dataType = new BasicDoubleDataType(name);
        } else if (type == Float.class || type == Float.TYPE) {
            dataType = new BasicFloatDataType(name);
        } else if (type == String.class) {
            dataType = new BasicStringDataType(name);
        } else if (type == org.w3c.dom.Document.class) {
            dataType = new BasicXmlDataType(name);
        } else if (type == Node.class) {
            dataType = new BasicNodeDataType(name);
        } else if (type == Date.class) {
            dataType = new BasicDateTimeDataType(name);
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            dataType = new BasicBooleanDataType(name);
        } else if (type == List.class) {
            dataType = new BasicListDataType(name);
        } else {
            dataType = new BasicDataType(name, type);
        }
        return dataType;
    }

    public static DataType finish(DataType dataType) {
        if (dataType instanceof AbstractDataType) {
            ((AbstractDataType)dataType).finish();
        }
        return dataType;
    }

    public static synchronized DataType createFinalDataType(String name, Class type) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        DataType dataType = createDataType(name, type);
        finish(dataType);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    public static synchronized DataType createFinalDataType(String name, int type) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        DataType dataType = createDataType(name, type);
        finish(dataType);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    /**
     * Create an instance of a dataType based on another data type
     */
    public static DataType createFinalDataType(String name, DataType baseDataType) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        DataType dataType = baseDataType.copy(name);
        finish(dataType);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    /**
     * Create an instance of a List dataType with a specified data type element
     */
    public static BasicListDataType createFinalListDataType(String name, DataType elementDataType) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        BasicListDataType dataType = new BasicListDataType(name);
        dataType.setListItemDataType(elementDataType);
        finish(dataType);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    public static synchronized DataType getDataType(String name) {
        return (DataType) finalDataTypes.get(name);
    }

}
