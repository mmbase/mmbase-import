/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.datatypes;

import org.w3c.dom.*;

import org.mmbase.datatypes.*;
import org.mmbase.bridge.util.xml.AbstractObjectDefinition;
import org.mmbase.util.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeDefinition.java,v 1.3 2005-07-22 12:35:46 pierre Exp $
 **/
public class DataTypeDefinition extends AbstractObjectDefinition {

    /**
     * The id of the data type
     */
    public String name = null;

    /**
     * the base data type
     */
    public DataType baseDataType = null;

    /**
     * the data type
     */
    public DataType dataType = null;

    /**
     * The type set definition belonging to this field
     */
    protected TypeSetDefinition typeSetDefinition = null;

    /**
     * The data type configurer that instantiated this definition
     */
    protected DataTypeConfigurer configurer = null;

    /**
     * Constructor.
     */
    public DataTypeDefinition(TypeSetDefinition typeSetDefinition, DataTypeConfigurer configurer) {
        super(DataTypeConfigurer.NAMESPACE_DATATYPES);
        this.typeSetDefinition = typeSetDefinition;
        this.configurer = configurer;
    }

    /**
     * Configures the data type definition, using data from a DOM element
     */
    public DataTypeDefinition configure(Element dataTypeElement) {
        if (hasAttribute(dataTypeElement,"name")) {
            name = getAttribute(dataTypeElement,"name");
        }
        String baseType = "string";
        if (hasAttribute(dataTypeElement,"base")) {
            baseType = getAttribute(dataTypeElement,"base");
        }
        baseDataType = configurer.getDataType(baseType);
        dataType = (DataType)baseDataType.clone(name);
        configureConditions(dataTypeElement);
        return this;
    }

    /**
     * Configures the datatypes of a type set definition, using data from a DOM element
     */
    protected void configureConditions(Element dataTypeElement) {
        // add conditions
        NodeList childNodes = dataTypeElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("required".equals(childElement.getLocalName())) {
                    // not yet implemented
                } else if ("unique".equals(childElement.getLocalName())) {
                    // not yet implemented
                } else if (dataType instanceof StringDataType) {
                    addStringCondition(childElement);
                } else if (dataType instanceof IntegerDataType) {
                    addIntegerCondition(childElement);
                } else if (dataType instanceof LongDataType) {
                    addLongCondition(childElement);
                } else if (dataType instanceof FloatDataType) {
                    addFloatCondition(childElement);
                } else if (dataType instanceof DoubleDataType) {
                    addDoubleCondition(childElement);
                }
            }
        }
    }

    protected int getIntValue(Element element) {
        return getIntValue(element, true);
    }

    protected int getIntValue(Element element, boolean required) {
        if (hasAttribute(element, "value")) {
            return Integer.parseInt(getAttribute(element, "value"));
        } else {
            if (required) {
                throw new IllegalArgumentException("no 'value' argument");
            } else {
                return -1;
            }
        }
    }

    protected void addStringCondition(Element conditionElement) {
        StringDataType sDataType = (StringDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            sDataType.setMinLength(value);
            // better:
            // sDataType.setMinLength(getIntValue(conditionElement),
            //                        getDescriptions(conditionElement),
            //                        getFixed(conditionElement));
        } else if ("maxLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            sDataType.setMaxLength(value);
        } else if ("length".equals(localName)) {
            int value = getIntValue(conditionElement);
            sDataType.setMinLength(value);
            sDataType.setMaxLength(value);
        } else if ("pattern".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            sDataType.setPattern(value);
        } else if ("whitespace".equals(localName)) {
            // not yet implemented
        }
    }

    protected Integer getIntegerValue(Element element) {
        if (hasAttribute(element, "value")) {
            return new Integer(getAttribute(element, "value"));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }

    protected void addIntegerCondition(Element conditionElement) {
        IntegerDataType iDataType = (IntegerDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Integer value = getIntegerValue(conditionElement);
            iDataType.setMin(value, "minInclusive".equals(localName));
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Integer value = getIntegerValue(conditionElement);
            iDataType.setMax(value, "maxInclusive".equals(localName));
        }
    }

    protected Long getLongValue(Element element) {
        if (hasAttribute(element, "value")) {
            return new Long(getAttribute(element, "value"));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }

    protected void addLongCondition(Element conditionElement) {
        LongDataType lDataType = (LongDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Long value = getLongValue(conditionElement);
            lDataType.setMin(value, "minInclusive".equals(localName));
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Long value = getLongValue(conditionElement);
            lDataType.setMax(value, "maxInclusive".equals(localName));
        }
    }

    protected Float getFloatValue(Element element) {
        if (hasAttribute(element, "value")) {
            return new Float(getAttribute(element, "value"));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }

    protected void addFloatCondition(Element conditionElement) {
        FloatDataType fDataType = (FloatDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Float value = getFloatValue(conditionElement);
            fDataType.setMin(value, "minInclusive".equals(localName));
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Float value = getFloatValue(conditionElement);
            fDataType.setMax(value, "maxInclusive".equals(localName));
        }
    }

    protected Double getDoubleValue(Element element) {
        if (hasAttribute(element, "value")) {
            return new Double(getAttribute(element, "value"));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }

    protected void addDoubleCondition(Element conditionElement) {
        DoubleDataType dDataType = (DoubleDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Double value = getDoubleValue(conditionElement);
            dDataType.setMin(value, "minInclusive".equals(localName));
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Double value = getDoubleValue(conditionElement);
            dDataType.setMax(value, "maxInclusive".equals(localName));
        }
    }

}

