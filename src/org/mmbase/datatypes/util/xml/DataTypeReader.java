/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;

import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;

/**
 * This class contains static methods used for reading a 'datatypes' XML into a DataTypeCollector.
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeReader.java,v 1.6 2005-08-30 19:36:21 michiel Exp $
 * @since MMBase-1.8
 **/
public class DataTypeReader {

    private static final Logger log = Logging.getLoggerInstance(DataTypeReader.class);

    public static final String XSD_DATATYPES_1_0 = "datatypes.xsd";
    public static final String NAMESPACE_DATATYPES_1_0 = "http://www.mmbase.org/xmlns/datatypes";

    /** enumeration query (expansion of standard searchquery)  */
    public static final String XSD_ENUMERATIONQUERY_1_0 = "enumerationquery.xsd";
    public static final String NAMESPACE_ENUMERATIONQUERY_1_0 = "http://www.mmbase.org/xmlns/enumerationquery";

    /**  most recent version */
    public static final String NAMESPACE_DATATYPES = NAMESPACE_DATATYPES_1_0;
    public static final String NAMESPACE_ENUMERATIONQUERY = NAMESPACE_ENUMERATIONQUERY_1_0;

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by XMLEntityResolver.
     */
    public static void registerSystemIDs() {
        XMLEntityResolver.registerSystemID(NAMESPACE_DATATYPES_1_0 + ".xsd", XSD_DATATYPES_1_0, DataTypeReader.class);
        XMLEntityResolver.registerSystemID(NAMESPACE_ENUMERATIONQUERY_1_0 + ".xsd", XSD_ENUMERATIONQUERY_1_0, DataTypeReader.class);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * searchquery namespace
     */
    static private String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element, NAMESPACE_DATATYPES_1_0, localName);
    }


    /**
     * Initialize the data types default supported by the system.
     */
    public static void readDataTypes(Element dataTypesElement, DataTypeCollector collector) {
        readDataTypes(dataTypesElement, collector, null);
    }

    /**
     * Initialize the data types default supported by the system.
     */
    public static void readDataTypes(Element dataTypesElement, DataTypeCollector collector, DataType baseDataType) {
        NodeList childNodes = dataTypesElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                String localName = childElement.getLocalName();
                if (log.isDebugEnabled()) log.debug("Found child " + childElement.getTagName());
                if ("fieldtype".equals(localName) ||  // backward compatibility
                    "specialization".equals(localName) ||  // backward compatibility
                    "datatype".equals(localName)) {
                    DataType dataType = readDataType(childElement, baseDataType, collector).dataType;

                    collector.addDataType(dataType);
                    if (log.isServiceEnabled()) {
                        log.service("Created " + dataType + " based on " + baseDataType);
                        if (log.isDebugEnabled()) {
                            log.trace("Now " + collector);
                        }
                    }

                    readDataTypes(childElement, collector, dataType);
                }
            }
        }
    }

    /**
     * Read a datatype
     */
    protected static DataTypeDefinition readDataType(Element typeElement, DataType baseDataType, DataTypeCollector collector) {
        DataTypeDefinition definition = new DataTypeDefinition(collector);
        definition.configure(typeElement, baseDataType);
        collector.finish(definition.dataType);
        return definition;
    }

}


