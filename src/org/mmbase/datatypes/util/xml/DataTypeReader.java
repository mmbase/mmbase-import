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
 * @version $Id$
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
    static  {
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
    public static List readDataTypes(Element dataTypesElement, DataTypeCollector collector) {
        return readDataTypes(dataTypesElement, collector, null);
    }

    /**
     * Initialize the data types default supported by the system.
     */
    public static List readDataTypes(Element dataTypesElement, DataTypeCollector collector, BasicDataType baseDataType) {
        NodeList childNodes = dataTypesElement.getChildNodes();
        List failed = new ArrayList();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                String localName = childElement.getLocalName();
                try {
                    if (log.isDebugEnabled()) log.debug("Found child " + childElement.getTagName());
                    if ("fieldtype".equals(localName) ||  // backward compatibility   XXXX DO WE NEED BACKWARDS COMPATIBILITY??!
                        "specialization".equals(localName) ||  // backward compatibility
                        "datatype".equals(localName)) {
                        BasicDataType dataType = readDataType(childElement, baseDataType, collector).dataType;
                        collector.finish(dataType);
                        BasicDataType old = collector.addDataType(dataType);
                        if (log.isDebugEnabled()) {
                            log.debug((old == null ? "Created "  : "Configured ") + dataType + (baseDataType != null ? " based on " + baseDataType : ""));
                            if (log.isTraceEnabled()) {
                                log.trace("Now " + collector);
                            }
                        }
                        readDataTypes(childElement, collector, dataType);
                    }
                } catch (DependencyException de) {
                    de.setCollector(collector);
                    failed.add(de);
                } catch (Exception e) {
                    log.error("Error while parsing element  '" + org.mmbase.util.xml.XMLWriter.write(childElement, true, true) + "': " + e.getMessage(), e);
                }
            }
        }
        return failed;
    }

    /**
     * Reads a datatype.
     */
    public static DataTypeDefinition readDataType(Element typeElement, BasicDataType baseDataType, DataTypeCollector collector) throws DependencyException {
        DataTypeDefinition definition = collector.getDataTypeDefinition();
        definition.configure(typeElement, baseDataType);
        definition.dataType.setXml(typeElement);
        return definition;
    }

}


