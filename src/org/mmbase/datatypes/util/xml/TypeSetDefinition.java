/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import java.util.*;
import org.w3c.dom.*;

import org.mmbase.datatypes.DataType;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: TypeSetDefinition.java,v 1.2 2005-07-29 17:15:35 michiel Exp $
 * @since MMBase-1.8
 **/
public class TypeSetDefinition {

    /**
     * The id of the typeset set
     */
    public String id = null;

    /**
     * the map of data types
     */
    public Map dataTypes;

    /**
     * The data type configurer that instantiated this definition
     */
    protected DataTypeConfigurer configurer = null;

    /**
     * Constructor.
     */
    public TypeSetDefinition(DataTypeConfigurer configurer) {
        this.configurer = configurer;
        dataTypes = new HashMap();
    }

    /**
     * Configures the type set definition, using data from a DOM element
     */
    public TypeSetDefinition configure(Element typeSetElement) {
        if (DocumentReader.hasAttribute(typeSetElement,DataTypeReader.NAMESPACE_DATATYPES,"id")) {
            id = DocumentReader.getAttribute(typeSetElement,DataTypeReader.NAMESPACE_DATATYPES,"id");
        }
        configureDataTypes(typeSetElement);
        return this;
    }

    /**
     * Configures the datatypes of a type set definition, using data from a DOM element
     */
    protected void configureDataTypes(Element typeSetElement) {
        NodeList childNodes = typeSetElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("datatype".equals(childElement.getLocalName())) {
                    DataTypeDefinition dataType = configurer.getDataTypeDefinition(this);
                    dataType.configure(childElement);
                    if (dataType.name == null) {
                        dataType.name = "dt_"+System.currentTimeMillis()+"_"+k;
                    }
                    dataTypes.put(dataType.name,dataType);
                }
            }
        }
    }
}

