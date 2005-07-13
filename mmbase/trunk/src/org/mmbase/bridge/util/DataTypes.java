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
import org.mmbase.core.util.Fields;
import org.mmbase.module.core.MMObjectNode;

/**
 * This class contains various methods for manipulating DataType objects.
 * It contains a static set of named DataType objects, with which it is possible to craete a set
 * of datatypes that are accessable throught the MMBase application.
 * This set contains, at the very least, the basic datatypes (a DataType for every
 * MMBase 'base' type, i.e. integer, string, etc).
 * There can be only one DataType in a set with a given name, so it is not possible to have multiple
 * registered datatypes with the same name (i.e. various 'integer' DataTypes for different basetypes).
 * <br />
 * A number of other methods in this class deal with conversion, creating datatypes, and 'finishing'
 * datatypes (locking a datatype to protect it form being changed).
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataTypes.java,v 1.8 2005-07-13 11:48:48 pierre Exp $
 */

public class DataTypes {

    // the map containing named DataTypes for use throughout the application
    private static Map finalDataTypes = new HashMap();

    /**
     * Determines the MMBase base type of a specified class. The MMBase base type determines, amonmg otehr things,
     * how the class would - ideally - be stored.
     * If the base type cannot be determined from the class, the value returned is {@link Field.TYPE_UNKNOWN}.
     * @param classType
     * @return an MMBase base type constant
     */
    public static int classToBaseType(Class classType) {
        if (classType == null) {
            return Field.TYPE_UNKNOWN;
        } else if (classType.isArray() && classType.getComponentType() == Byte.TYPE) {
            return Field.TYPE_BINARY;
        } else if (classType == Integer.class || classType == Integer.TYPE) {
            return Field.TYPE_INTEGER;
        } else if (classType == Long.class || classType == Long.TYPE) {
            return Field.TYPE_LONG;
        } else if (classType == Double.class || classType == Double.TYPE) {
            return Field.TYPE_DOUBLE;
        } else if (classType == Float.class || classType == Float.TYPE) {
            return Field.TYPE_FLOAT;
        } else if (classType == Boolean.class || classType == Boolean.TYPE) {
            return Field.TYPE_BOOLEAN;
        } else if (classType == String.class) {
            return Field.TYPE_STRING;
        } else if (org.w3c.dom.Node.class.isAssignableFrom(classType)) {
            return Field.TYPE_XML;
        } else if (Node.class.isAssignableFrom(classType) || MMObjectNode.class.isAssignableFrom(classType)) {
            return Field.TYPE_NODE;
        } else if (Date.class.isAssignableFrom(classType)) {
            return Field.TYPE_DATETIME;
        } else if (List.class.isAssignableFrom(classType)) {
            return Field.TYPE_LIST;
        } else {
            return Field.TYPE_UNKNOWN;
        }
    }

    /**
     * Determines the class for a specified MMBase base type.
     * If the class cannot be determined from the bas etype (i.e. the value is {@link Field.TYPE_UNKNOWN}),
     * the method returns <code>null</code>.
     * @param classType
     * @return an MMBase base type constant
     */
    public static Class baseTypeToClass(int type) {
        switch (type) {
        case Field.TYPE_STRING : return String.class;
        case Field.TYPE_INTEGER : return Integer.class;
        case Field.TYPE_BINARY: return byte[].class;
        case Field.TYPE_FLOAT: return Float.class;
        case Field.TYPE_DOUBLE: return Double.class;
        case Field.TYPE_LONG: return Long.class;
        case Field.TYPE_XML: return org.w3c.dom.Document.class;
        case Field.TYPE_NODE: return MMObjectNode.class; // org.mmbase.bridge.Node.class;
        case Field.TYPE_DATETIME: return java.util.Date.class;
        case Field.TYPE_BOOLEAN: return Boolean.class;
        case Field.TYPE_LIST: return List.class;
        default: return null;
        }
    }

    /**
     * Create an instance of a DataType based on the class passed.
     * The DataType returned is, if possoble, a specialized DataType (such as {@link IntegerDataType})
     * based on the BaseType associated with the passed class. Otherwis, it is a generic DataType
     * specific for that class (with generally emans that it only supports basic functionality such as autocast).
     * @param name The name of the datatype to create. If <code>null</code> is passed, the class name is used.
     * @param classType The class of the datatype to create. If <code>null</code> is passed, the
     *          dataType returned is based on Object.class.
     */
    public static DataType createDataType(String name, Class classType) {
        int baseType = classToBaseType(classType);
        if (name == null && classType != null) {
            name = classType.getName();
        }
        if (baseType != Field.TYPE_UNKNOWN || classType == null) {
            return createDataType(name, baseType);
        } else {
            return new BasicDataType(name, classType);
        }
    }

    /**
     * Create an instance of a DataType based on the MMBase type passed.
     */
    private static DataType createDataType(String name, int type) {
        switch (type) {
        case Field.TYPE_BINARY: return new BasicBinaryDataType(name);
        case Field.TYPE_INTEGER : return new BasicIntegerDataType(name);
        case Field.TYPE_LONG: return new BasicLongDataType(name);
        case Field.TYPE_FLOAT: return new BasicFloatDataType(name);
        case Field.TYPE_DOUBLE: return new BasicDoubleDataType(name);
        case Field.TYPE_BOOLEAN: return new BasicBooleanDataType(name);
        case Field.TYPE_STRING : return new BasicStringDataType(name);
        case Field.TYPE_XML: return new BasicXmlDataType(name);
        case Field.TYPE_NODE: return new BasicNodeDataType(name);
        case Field.TYPE_DATETIME: return new BasicDateTimeDataType(name);
        case Field.TYPE_LIST: return new BasicListDataType(name);
        default: return new BasicDataType(name);
        }
    }

    /**
     * Finishes the passed DataType, indicated all validation rules on it have been set.
     * This prohibits any future changes to the datatype.
     * @param datatype the Datatype to finish
     * @return the datatype after being finished.
     */
    public static DataType finish(DataType dataType) {
        if (dataType instanceof AbstractDataType) {
            ((AbstractDataType)dataType).finish();
        }
        return dataType;
    }

    /**
     * Add an instance of a DataType to the set of data types that are available thoughout the application.
     * The datatype should have a proper name, and not occur already in the set.
     * Note that the datatype is finished when added (if it wasn't already), and can thereafter not be changed.
     * @param dataType the datatype to add
     * @return the dataType added.
     * @throws IllegalArgumentException if the datatype does not have a name or already occurs in the set
     */
    public static DataType addFinalDataType(DataType dataType) {
        String name = dataType.getName();
        if (name == null) {
            throw new IllegalArgumentException("Passed datatype " + dataType + " does not have a name assigned.");
        }
        if (finalDataTypes.containsKey(name)) {
            throw new IllegalArgumentException("The datatype " + dataType + " was passed, but a type with the same name occurs as : " +
                                               finalDataTypes.get(name));
        }
        finish(dataType);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    /**
     * Returns a DataType instance that is assignment compatible with the passed base DataType.
     * Assignment compatible in this regard means that the MMBase base type of the DataType matches,
     * (and in the case of a list, the base type of the elements as well). Note that a base Datatype that is of type
     * {@link Field.TYPE_UNKNOWN} matches with anything.
     * The system first tries to obtain a assignment-compatible type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name exists, and if it is
     * assignent-compatible to the base DataType, a clone of that DataType is returned.
     * Otherwise, a clone of the base DataType is returned.
     * if the DataType with the passed name does not exist, and the value passed for the baseDataType is <code>null</code>,
     * the method returns <code>null</code>.
     * @param name the name of the DataType to look for
     * @param baseDataType the dataType to match against. Can be <code>null</code>, in which case no matching takes place.
     * @return A DataType instance or <code>null</code> if none can be instantiated
     */
    public static synchronized DataType getDataTypeInstance(String name, DataType baseDataType) {
        DataType dataType = (DataType) finalDataTypes.get(name);
        int baseType = baseDataType != null ? baseDataType.getBaseType() : Field.TYPE_UNKNOWN;
        if (dataType == null && baseDataType == null) {
            return null;
        } else if (dataType == null || (baseType != Field.TYPE_UNKNOWN && dataType.getBaseType() != baseType)) {
            return (DataType)baseDataType.clone(name);
        } else if (baseType == Field.TYPE_LIST) {
            DataType baseItemDataType = ((ListDataType) baseDataType).getItemDataType();
            int itemBaseType = baseItemDataType != null ? baseItemDataType.getBaseType() : Field.TYPE_UNKNOWN;
            if (itemBaseType != Field.TYPE_UNKNOWN) {
                DataType itemDataType = ((ListDataType) dataType).getItemDataType();
                if (itemDataType == null || itemDataType.getBaseType() != itemBaseType) {
                    return (DataType)baseDataType.clone(name);
                }
            }
        }
        return (DataType)dataType.clone();
    }

    /**
     * Returns a DataType instance that matches the passed basetype.
     * Note that a base type{@link Field.TYPE_UNKNOWN} matches with anything.
     * The system first tries to obtain a type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name (and appropriate type) exists,
     * a clone of that DataType is returned. Otherwise, an instance of a DataType based on the base type is returned.
     * @param name the name of the DataType to look for
     * @param baseType the base type  to match against.
     * @return A DataType instance
     */
    public static synchronized DataType getDataTypeInstance(String name, int baseType) {
        return getDataTypeInstance(name, getDataType(baseType));
    }

    /**
     * Returns a ListDataType instance whose itemDataType matches the passed basetype.
     * Note that a base type{@link Field.TYPE_UNKNOWN} matches with anything.
     * The system first tries to obtain a type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name (and appropriate type) exists,
     * a clone of that DataType is returned. Otherwise, an instance of a DataType based on the base type is returned.
     * @param name the name of the DataType to look for
     * @param baseType the base type to match against
     * @return A ListDataType instance
     */
    public static synchronized ListDataType getListDataTypeInstance(String name, int subBaseType) {
        return (ListDataType)getDataTypeInstance(name, getListDataType(subBaseType));
    }

    /**
     * Returns the basic DataType that matches the passed basetype.
     * The datatype is retrieved from the available set of datatypes accessible throughout the application.
     * If this datatype does not (yet) exists, an instance is automatically created and added.
     * The datatype returned by this method is only useful for matching or cloning -  it cannot be changed.
     * @param baseType the base type whose DataType to return
     * @return the DataType instance
     */
    public static synchronized DataType getDataType(int type) {
        String name = Fields.getTypeDescription(type).toLowerCase();
        DataType dataType = (DataType) finalDataTypes.get(name);
        if (dataType == null) {
            if (type == Field.TYPE_LIST) {
                dataType = getListDataType(Field.TYPE_UNKNOWN);
            } else {
                dataType = createDataType(name, type);
                finish(dataType);
                finalDataTypes.put(name, dataType);
            }
        }
        return dataType;
    }

    /**
     * Returns the basic ListDataType whose item's DataType matches the passed basetype.
     * The datatype is retrieved from the available set of datatypes accessible throughout the application.
     * If this datatype does not (yet) exists, an instance is automatically created and added.
     * The datatype returned by this method is only useful for matching or cloning -  it cannot be changed.
     * @param subBaseType the base type whose ListDataType to return
     * @return the ListDataType instance
     */
    public static ListDataType getListDataType(int subBaseType) {
        String name = Fields.getTypeDescription(Field.TYPE_LIST).toLowerCase() +
                      "[" +  Fields.getTypeDescription(subBaseType).toLowerCase() + "]";
        ListDataType dataType = (ListDataType)finalDataTypes.get(name);
        if (dataType == null) {
            dataType = (ListDataType)createDataType(name, Field.TYPE_LIST);
            dataType.setItemDataType(getDataType(subBaseType));
            finish(dataType);
            finalDataTypes.put(name, dataType);
        }
        return dataType;
    }

    /**
     * Returns a basic DataType extracted from the passed DataType.
     * A basic DataType does not contain any validation rules other than type casting, andd cannot be changed.
     * This method is useful if you want to make an assignment compatible DataType, but do not wish to
     * copy all the validation rules.
     * @param  dataType the type to extract the basic DataType from
     * @return the basic DataType
     */
    public static DataType getBaseDataType(DataType dataType) {
        int baseType = dataType.getBaseType();
        DataType baseDataType;
        if (baseType == Field.TYPE_LIST) {
            int subType = Field.TYPE_UNKNOWN;
            DataType itemDataType = ((ListDataType)dataType).getItemDataType();
            if (itemDataType != null) {
                subType = itemDataType.getBaseType();
            }
            baseDataType = DataTypes.getListDataType(subType);
        } else {
            baseDataType = DataTypes.getDataType(baseType);
        }
        return baseDataType;
    }

}
