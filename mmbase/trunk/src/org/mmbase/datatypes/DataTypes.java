/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.net.*;
import java.util.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

import org.mmbase.bridge.Field;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.util.xml.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;

/**
 * This class contains various methods for manipulating DataType objects.
 * It contains a static set of named DataType objects, with which it is possible to craete a set
 * of datatypes that are accessable throught the MMBase application.
 * This set contains, at the very least, the basic datatypes (a DataType for every
 * 'MMBase' type, i.e. integer, string, etc).
 * There can be only one DataType in a set with a given name, so it is not possible to have multiple
 * registered datatypes with the same name.
 * <br />
 * A number of other methods in this class deal with conversion, creating datatypes, and 'finishing'
 * datatypes (locking a datatype to protect it form being changed).
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: DataTypes.java,v 1.3 2005-07-29 14:52:37 pierre Exp $
 */

public class DataTypes {

    private static final Logger log = Logging.getLoggerInstance(DataTypes.class);

    public static void initialize() {
        // read the XML
        try {
            ResourceWatcher watcher = new ResourceWatcher(ResourceLoader.getConfigurationRoot()) {
                    public void onChange(String resource) {
                        readDataTypes(getResourceLoader(), resource);
                    }
                };
            watcher.add("datatypes.xml");
            watcher.start();
            watcher.onChange("datatypes.xml");
        } catch (Throwable t) {
            log.error(t.getClass().getName() + ": " + Logging.stackTrace(t));
        }
    }

    /**
     * Initialize the type handlers defaultly supported by the system, plus those configured in WEB-INF/config.
     */
    private static void readDataTypes(ResourceLoader loader, String resource) {
        List resources = loader.getResourceList(resource);
        log.info("Using " + resources);
        ListIterator i = resources.listIterator();
        while (i.hasNext()) i.next();
        while (i.hasPrevious()) {
            try {
                URL u = (URL) i.previous();
                log.info("Reading " + u);
                URLConnection con = u.openConnection();
                if (con.getDoInput()) {
                    InputSource dataTypesSource = new InputSource(con.getInputStream());
                    log.service("Reading datatypes from " + dataTypesSource.getSystemId());
                    DocumentReader reader  = new DocumentReader(dataTypesSource, false, DataTypes.class);
                    Element dataTypesElement = reader.getRootElement(); // fieldtypedefinitons or datatypes element
                    DataTypeReader.readDataTypes(dataTypesElement, lockObject);
                }
            } catch (Exception e) {
                log.error(e);
                log.error(Logging.stackTrace(e));
            }
        }
        log.info(finalDataTypes.values().toString());
    }

    // the map containing named DataTypes for use throughout the application
    private static Map finalDataTypes = new HashMap();

    // object used to lock datatypes
    private static Object lockObject = new Object();

    /**
     * Determines the MMBase type of a specified class. The MMBase base type is sue dby teh storage layer to
     * determine how to store a field.
     * If the base type cannot be determined from the class, the value returned is {@link Field.TYPE_UNKNOWN}.
     * @param classType
     * @return an MMBase base type constant
     */
    public static int classToType(Class classType) {
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
        } else if (org.mmbase.bridge.Node.class.isAssignableFrom(classType) || MMObjectNode.class.isAssignableFrom(classType)) {
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
     * If the value is {@link Field.TYPE_UNKNOWN}), the method returns <code>null</code>.
     * @param classType
     * @return an MMBase base type constant
     */
    public static Class typeToClass(int type) {
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
     * The DataType returned is, if possible, a specialized DataType (such as {@link IntegerDataType})
     * based on the MMBase Type that most closely matches the passed class. Otherwise, it is a generic DataType
     * specific for that class (with generally means that it only supports basic functionality such as autocast).
     * @param name The name of the datatype to create. If <code>null</code> is passed, the class name is used.
     * @param classType The class of the datatype to create. If <code>null</code> is passed, the
     *          dataType returned is based on Object.class.
     */
    public static DataType createDataType(String name, Class classType) {
        int type = classToType(classType);
        if (name == null && classType != null) {
            name = classType.getName();
        }
        if (type != Field.TYPE_UNKNOWN || classType == null) {
            return createDataType(name, type);
        } else {
            return new DataType(name, classType);
        }
    }

    /**
     * Create an instance of a DataType based on the MMBase type passed.
     */
    private static DataType createDataType(String name, int type) {
        switch (type) {
        case Field.TYPE_BINARY: return new BinaryDataType(name);
        case Field.TYPE_INTEGER : return new IntegerDataType(name);
        case Field.TYPE_LONG: return new LongDataType(name);
        case Field.TYPE_FLOAT: return new FloatDataType(name);
        case Field.TYPE_DOUBLE: return new DoubleDataType(name);
        case Field.TYPE_BOOLEAN: return new BooleanDataType(name);
        case Field.TYPE_STRING : return new StringDataType(name);
        case Field.TYPE_XML: return new XmlDataType(name);
        case Field.TYPE_NODE: return new NodeDataType(name);
        case Field.TYPE_DATETIME: return new DateTimeDataType(name);
        case Field.TYPE_LIST: return new ListDataType(name);
        default: return new DataType(name);
        }
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
        dataType.finish(lockObject);
        finalDataTypes.put(name, dataType);
        return dataType;
    }

    /**
     * Returns a DataType from the the available set of datatypes accessible throughout the application,
     * or <code>null</code> if that type does not exist.
     * @param name the name of the DataType to look for
     * @return A DataType instance or <code>null</code> if none can be found
     */
    public static synchronized DataType getDataType(String name) {
        return (DataType) finalDataTypes.get(name);
    }

    /**
     * Returns a DataType instance.
     * The system first tries to obtain a data type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name exists, a clone of that DataType is returned.
     * Otherwise, a clone of the base DataType passed is returned.
     * if the DataType with the passed name does not exist, and the value passed for the baseDataType is <code>null</code>,
     * the method returns <code>null</code>.
     * @param name the name of the DataType to look for
     * @param baseDataType the dataType to match against. Can be <code>null</code>.
     * @return A DataType instance or <code>null</code> if none can be instantiated
     */
    public static synchronized DataType getDataTypeInstance(String name, DataType baseDataType) {
        DataType dataType = (DataType) finalDataTypes.get(name);
        if (dataType == null && baseDataType == null) {
            return null;
        } else if (dataType == null) {
            return (DataType)baseDataType.clone(name);
        } else {
            return (DataType)dataType.clone();
        }
    }

    /**
     * Returns a DataType instance.
     * The system first tries to obtain a type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name exists,
     * a clone of that DataType is returned. Otherwise, an instance of a DataType based
     * on the base type is returned.
     * @param name the name of the DataType to look for
     * @param type the base type to use for a default datatype instance
     * @return A DataType instance
     */
    public static synchronized DataType getDataTypeInstance(String name, int type) {
        return getDataTypeInstance(name, getDataType(type));
    }

    /**
     * Returns a ListDataType instance.
     * The system first tries to obtain a type from the available set of datatypes
     * accessible throughout the application. If a DataType of the passed name exists,
     * a clone of that DataType is returned. Otherwise, an instance of a ListDataType based
     * on the list item type is returned.
     * @param name the name of the DataType to look for
     * @param listItemType the base type to use for a default listdatatype instance
     *        (this type determines the type of the list elements)
     * @return A ListDataType instance
     */
    public static synchronized ListDataType getListDataTypeInstance(String name, int listItemType) {
        return (ListDataType)getDataTypeInstance(name, getListDataType(listItemType));
    }

    /**
     * Returns the basic DataType that matches the passed type.
     * The datatype is retrieved from the available set of datatypes accessible throughout the application.
     * If this datatype does not (yet) exists, an instance is automatically created and added.
     * The datatype returned by this method is only useful for matching or cloning -  it cannot be changed.
     * @param type the base type whose DataType to return
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
                dataType.finish(lockObject);
                finalDataTypes.put(name, dataType);
            }
        }
        return dataType;
    }

    /**
     * Returns the basic ListDataType whose item's DataType matches the passed type.
     * The datatype is retrieved from the available set of datatypes accessible throughout the application.
     * If this datatype does not (yet) exists, an instance is automatically created and added.
     * The datatype returned by this method is only useful for matching or cloning -  it cannot be changed.
     * @param listItemType the base type whose ListDataType to return
     * @return the ListDataType instance
     */
    public static ListDataType getListDataType(int listItemType) {
        String name = Fields.getTypeDescription(Field.TYPE_LIST).toLowerCase() +
                      "[" +  Fields.getTypeDescription(listItemType).toLowerCase() + "]";
        ListDataType dataType = (ListDataType)finalDataTypes.get(name);
        if (dataType == null) {
            dataType = (ListDataType)createDataType(name, Field.TYPE_LIST);
            dataType.setItemDataType(getDataType(listItemType));
            dataType.finish(lockObject);
            finalDataTypes.put(name, dataType);
        }
        return dataType;
    }

}
