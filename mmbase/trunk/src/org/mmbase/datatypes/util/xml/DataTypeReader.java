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
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeReader.java,v 1.4 2005-08-04 14:14:27 pierre Exp $
 * @since MMBase-1.8
 **/
public class DataTypeReader {

    private static final Logger log = Logging.getLoggerInstance(DataTypeReader.class);

    /** XSD resource filename of the datatypes XSD version 1.0 */
    public static final String XSD_DATATYPES_1_0 = "datatypes.xsd";
    /** XSD namespace of the datatypes XSD version 1.0 */
    public static final String NAMESPACE_DATATYPES_1_0 = "http://www.mmbase.org/xmlns/datatypes";
    /** XSD resource filename of the enumeration query (expansion of standard searchquery) XSD version 1.0 */
    public static final String XSD_ENUMERATIONQUERY_1_0 = "datatypes.xsd";
    /** XSD namespace of the enumeration query XSD version 1.0 */
    public static final String NAMESPACE_ENUMERATIONQUERY_1_0 = "http://www.mmbase.org/xmlns/datatypes";

    /** XSD namespace of the datatypes XSD, most recent version */
    public static final String NAMESPACE_DATATYPES = NAMESPACE_DATATYPES_1_0;

    /** XSD namespace of the enumeration query XSD, most recent version */
    public static final String NAMESPACE_ENUMERATIONQUERY = NAMESPACE_ENUMERATIONQUERY_1_0;

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(NAMESPACE_DATATYPES_1_0, XSD_DATATYPES_1_0, DataTypeConfigurer.class);
        XMLEntityResolver.registerPublicID(NAMESPACE_ENUMERATIONQUERY_1_0, XSD_ENUMERATIONQUERY_1_0, DataTypeConfigurer.class);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * searchquery namespace
     */
    static public String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element,NAMESPACE_DATATYPES_1_0,localName);
    }

    /**
     * Initialize the data types default supported by the system.
     */
    public static void readDataTypes(Element dataTypesElement, DataTypeCollector collector) {
        readDataTypes(dataTypesElement, new DataTypeConfigurer(collector), null);
    }

    /**
     * Initialize the data types default supported by the system.
     */
    public static void readDataTypes(Element dataTypesElement, DataTypeConfigurer configurer) {
        readDataTypes(dataTypesElement, configurer, null);
    }

    /**
     * Initialize the data types default supported by the system.
     */
    public static void readDataTypes(Element dataTypesElement, DataTypeConfigurer configurer, DataType baseDataType) {
        NodeList childNodes = dataTypesElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                String localName = childElement.getLocalName();
                if (log.isDebugEnabled()) log.debug("Found child " + childElement.getTagName());
                if ("fieldtype".equals(localName) ||  // backward compatibility
                    "specialization".equals(localName) ||  // backward compatibility
                    "datatype".equals(localName)) {
                    DataType dataType = readDataType(childElement, baseDataType, configurer).dataType;
                    configurer.addDataType(dataType);
                    readDataTypes(childElement, configurer, dataType);
                }
            }
        }
    }

    /**
     * Read a datatype
     */
    public static DataTypeDefinition readDataType(Element typeElement, DataType baseDataType, DataTypeConfigurer configurer) {
        DataTypeDefinition definition = new DataTypeDefinition(configurer);
        definition.configure(typeElement, baseDataType);
        configurer.finish(definition.dataType);
        if (log.isDebugEnabled()) log.debug("Created " + definition);
        return definition;
    }

}


