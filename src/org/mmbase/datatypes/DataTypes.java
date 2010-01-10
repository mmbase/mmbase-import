/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.net.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.util.xml.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;

/**
 * <p>
 * This class contains various methods for manipulating DataType objects.
 * It contains a static set of named DataType objects, with which it is possible to craete a set
 * of datatypes that are accessable throught the MMBase application.
 * This set contains, at the very least, the basic datatypes (a DataType for every
 * 'MMBase' type, i.e. integer, string, etc).
 * There can be only one DataType in a set with a given name, so it is not possible to have multiple
 * registered datatypes with the same name.
 * </p>
 * <p>
 * A number of other methods in this class deal with conversion, creating datatypes, and 'finishing'
 * datatypes (locking a datatype to protect it form being changed).
 *</p>
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id$
 */

public class DataTypes {

    private static final Logger log = Logging.getLoggerInstance(DataTypes.class);

    // the datatype collector containing named DataTypes for use throughout the application
    private static final DataTypeCollector dataTypeCollector = DataTypeCollector.createSystemDataTypeCollector();

    public static void initialize() {
        // read the XML
        // Watching will ptobably not work properly,
        // as datatypes depend one ach other, and are are referred
        // throughout the system.
        // For the moment turn watching off.
        // Not sure if it is needed anyway - it won't actually happen that often
        log.trace("" + Constants.class); // make sure its static init is called, otherwise it goes horribly wrong.

        log.debug("Reading datatypes " + dataTypeCollector);
        readDataTypes(ResourceLoader.getConfigurationRoot(), "datatypes.xml");

        /*
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
        */

    }

    private static void readFailedDependencies(List failed) {
        ListIterator i = failed.listIterator();
        while(i.hasNext()) {
            DependencyException de = (DependencyException) i.next();
            if (de.retry()) {
                log.debug("Resolved " + de.getId() + " after all");
                i.remove();
            }
        }
    }

    /**
     * Initialize the type handlers defaultly supported by the system, plus those configured in WEB-INF/config.
     */
    private static void readDataTypes(ResourceLoader loader, String resource) {
        List resources = loader.getResourceList(resource);
        if (log.isDebugEnabled()) log.debug("Using " + resources);
        ListIterator i = resources.listIterator();
        List failed = new ArrayList();
        while (i.hasNext()) i.next();
        while (i.hasPrevious()) {
            try {
                URL u = (URL) i.previous();
                URLConnection con = u.openConnection();
                if (con.getDoInput()) {
                    InputSource dataTypesSource = new InputSource(con.getInputStream());
                    dataTypesSource.setSystemId(u.toExternalForm());
                    DocumentBuilder db = DocumentReader.getDocumentBuilder(true, true, new XMLErrorHandler(), new XMLEntityResolver(true, DataTypeReader.class));
                    Document doc = db.parse(dataTypesSource);
                    Element dataTypesElement = doc.getDocumentElement(); // fieldtypedefinitons or datatypes element
                    failed.addAll(DataTypeReader.readDataTypes(dataTypesElement, dataTypeCollector));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        int previousFailedSize = -1;
        while (failed.size() > 0 && failed.size() > previousFailedSize) {
            previousFailedSize = failed.size();
            log.debug(failed);
            readFailedDependencies(failed);
        }
        if (failed.size() > 0) {
            log.error("Failed " + failed);
        }
        if (log.isDebugEnabled()) log.debug(dataTypeCollector.toString());
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
    public static BasicDataType createDataType(String name, Class classType) {
        int type = Fields.classToType(classType);
        if (name == null && classType != null) {
            name = classType.getName();
        }
        if (type != Field.TYPE_UNKNOWN || classType == null) {
            return createDataType(name, type, classType.isPrimitive());
        } else {
            return new BasicDataType(name, classType);
        }
    }

    /**
     * Create an instance of a DataType based on the MMBase type passed.
     *
     * @param primitive in case of integer, long, float, double, boolean this
     *        parameter determines whether a primitive type or the wrapper class
     *        should be used
     */
    private static BasicDataType createDataType(String name, int type, boolean primitive) {
        switch (type) {
        case Field.TYPE_BINARY: return new BinaryDataType(name);
        case Field.TYPE_INTEGER : return new IntegerDataType(name, primitive);
        case Field.TYPE_LONG: return new LongDataType(name, primitive);
        case Field.TYPE_FLOAT: return new FloatDataType(name, primitive);
        case Field.TYPE_DOUBLE: return new DoubleDataType(name, primitive);
        case Field.TYPE_BOOLEAN: return new BooleanDataType(name, primitive);
        case Field.TYPE_STRING : return new StringDataType(name);
        case Field.TYPE_XML: return new XmlDataType(name);
        case Field.TYPE_NODE: return new NodeDataType(name);
        case Field.TYPE_DATETIME: return new DateTimeDataType(name);
        case Field.TYPE_LIST: return new ListDataType(name);
        default: return new BasicDataType(name);
        }
    }
    /**
     * Create an instance of a DataType based on the MMBase type passed. In case
     * a type is used that has both a primitive and a wrapper class the wrapped
     * version will be used.
     */
    private static BasicDataType createDataType(String name, int type) {
        return createDataType(name, type, false);
    }

    /**
     * Add an instance of a DataType to the set of data types that are available thoughout the application.
     * The datatype should have a proper name, and not occur already in the set.
     * Note that the datatype is finished when added (if it wasn't already), and can thereafter not be changed.
     * @param dataType the datatype to add
     * @return the dataType added.
     * @throws IllegalArgumentException if the datatype does not have a name or already occurs in the set
     */
    public static DataType addFinalDataType(BasicDataType dataType) {
        String name = dataType.getName();
        if (name == null) {
            throw new IllegalArgumentException("Passed datatype " + dataType + " does not have a name assigned.");
        }
        if (dataTypeCollector.contains(name)) {
            throw new IllegalArgumentException("The datatype " + dataType + " was passed, but a type with the same name occurs as : " +
                                               getDataType(name));
        }
        dataTypeCollector.finish(dataType);
        dataTypeCollector.addDataType(dataType);
        return dataType;
    }

    /**
     * Returns a DataType from the the available set of datatypes accessible throughout the application,
     * or <code>null</code> if that type does not exist.
     * @param name the name of the DataType to look for
     * @return A DataType instance or <code>null</code> if none can be found
     */
    public static synchronized BasicDataType getDataType(String name) {
        return  dataTypeCollector.getDataType(name);
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
    public static synchronized BasicDataType getDataTypeInstance(String name, BasicDataType baseDataType) {
        return dataTypeCollector.getDataTypeInstance(name, baseDataType);
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
    public static synchronized BasicDataType getDataTypeInstance(String name, int type) {
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
    public static synchronized BasicDataType getDataType(int type) {
        String name = Fields.getTypeDescription(type).toLowerCase();
        BasicDataType dataType = getDataType(name);
        if (dataType == null) {
            if (type == Field.TYPE_LIST) {
                dataType = getListDataType(Field.TYPE_UNKNOWN);
            } else {
                dataType = createDataType(name, type);
                // dataTypeCollector.finish(dataType); // will be finished when reading the XML.
                dataTypeCollector.addDataType(dataType);
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
        ListDataType dataType = (ListDataType) getDataType(name);
        if (dataType == null) {
            dataType = (ListDataType)createDataType(name, Field.TYPE_LIST);
            dataType.setItemDataType(getDataType(listItemType));
            dataTypeCollector.finish(dataType);
            dataTypeCollector.addDataType(dataType);
        }
        return dataType;
    }

    public static  DataTypeCollector getSystemCollector() {
        return dataTypeCollector;
    }


    /**
     * Returns a new XML completely describing the given DataType.
     * This means that the XML will <em>not</em> have a base attribute.
     */
    public static Document toXml(DataType dataType) {
        // create an inheritance stack
        List stack = new ArrayList();
        stack.add(dataType);
        while (dataType.getOrigin() != null) {
            dataType = dataType.getOrigin();
            stack.add(0, dataType);
        }

        // new XML
        Document doc = DocumentReader.getDocumentBuilder().newDocument();

        // iterate the stack to completely resolve everything.
        Iterator i = stack.iterator();
        dataType = (DataType) i.next();
        Element e = (Element) doc.importNode(dataType.toXml(), true);
        doc.appendChild(e);
        dataType.toXml(e);
        while (i.hasNext()) {
            dataType = (DataType) i.next();
            dataType.toXml(e);
        }
        DocumentReader.setPrefix(doc, DataType.XMLNS, "dt");
        return doc;
    }

}
