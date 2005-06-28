/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.*;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.bridge.implementation.datatypes.*;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataTypes.java,v 1.1 2005-06-28 14:01:41 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public class DataTypes {

    private static Map finalDataTypes = new HashMap();

    public static Class getTypeAsClass(int type) {
        switch (type) {
            case MMBaseType.TYPE_STRING : return String.class;
            case MMBaseType.TYPE_INTEGER : return Integer.class;
            case MMBaseType.TYPE_BINARY: return byte[].class;
            case MMBaseType.TYPE_FLOAT: return Float.class;
            case MMBaseType.TYPE_DOUBLE: return Double.class;
            case MMBaseType.TYPE_LONG: return Long.class;
            case MMBaseType.TYPE_XML: return org.w3c.dom.Document.class;
            case MMBaseType.TYPE_NODE: return org.mmbase.bridge.Node.class;
            case MMBaseType.TYPE_DATETIME: return java.util.Date.class;
            case MMBaseType.TYPE_BOOLEAN: return Boolean.class;
            case MMBaseType.TYPE_LIST: return List.class;
            default: return null;
        }
    }

    /**
     * Create an instance of a dataType based on the MMBase type passed.
     */
    public static AbstractDataType createDataType(String name, int type) {
        AbstractDataType dataType = null;
        switch (type) {
            case MMBaseType.TYPE_BINARY : dataType = new BasicBinaryDataType(name); break;
            case MMBaseType.TYPE_INTEGER : dataType = new BasicIntegerDataType(name); break;
            case MMBaseType.TYPE_LONG : dataType = new BasicLongDataType(name); break;
            case MMBaseType.TYPE_DOUBLE : dataType = new BasicDoubleDataType(name); break;
            case MMBaseType.TYPE_FLOAT : dataType = new BasicFloatDataType(name); break;
            case MMBaseType.TYPE_STRING : dataType = new BasicStringDataType(name); break;
            case MMBaseType.TYPE_NODE : dataType = new BasicNodeDataType(name); break;
            case MMBaseType.TYPE_DATETIME : dataType = new BasicDateTimeDataType(name); break;
            case MMBaseType.TYPE_BOOLEAN : dataType = new BasicBooleanDataType(name); break;
            case MMBaseType.TYPE_LIST : dataType = new BasicListDataType(name); break;
            default: {
                dataType = new BasicDataType(name);
            }
        }
        return dataType;
    }

    /**
     * Create an instance of a dataType based on the MMBase type passed.
     */
    public static AbstractDataType createDataType(String name, Class type) {
        AbstractDataType dataType = null;
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

    public static synchronized AbstractDataType createFinalDataType(String name, Class type) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        AbstractDataType dataType = createDataType(name, type);
        dataType.finish();
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    public static synchronized AbstractDataType createFinalDataType(String name, int type) {
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("Datatype with name " + name + " already exists as : " + finalDataTypes.get(name));
        }
        AbstractDataType dataType = createDataType(name, type);
        dataType.finish();
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    public static synchronized AbstractDataType getDataType(String name) {
        return (AbstractDataType) finalDataTypes.get(name);
    }

}
