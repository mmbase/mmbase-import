/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.datatypes;

import java.util.Locale;
import org.w3c.dom.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.util.xml.AbstractObjectDefinition;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeDefinition.java,v 1.4 2005-07-26 14:36:31 pierre Exp $
 **/
public class DataTypeDefinition extends AbstractObjectDefinition {

    private static final Logger log = Logging.getLoggerInstance(DataTypeDefinition.class);

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

    protected void setPropertyData(DataType.Property property, Element element) {
        // set fixed
        if (hasAttribute(element, "fixed")) {
            boolean isFixed = Boolean.valueOf(getAttribute(element, "fixed")).booleanValue();
            property.setFixed(isFixed);
        }
        // set errorDescriptions
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("description".equals(childElement.getLocalName())) {
                    Locale locale = null;
                    if (hasAttribute(childElement, "xml:lang")) {
                        String language = getAttribute(childElement, "xml:lang");
                        locale = new Locale(language, null);
                    }
                    String description = getValue(childElement);
                    property.setErrorDescription(description, locale);
                }
            }
        }
    }

    /**
     * Configures the conditions of a datatype definition, using data from a DOM element
     */
    protected void configureConditions(Element dataTypeElement) {
        // add conditions
        NodeList childNodes = dataTypeElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("required".equals(childElement.getLocalName())) {
                    // not yet implemented
                    boolean value = getBooleanValue(childElement, false);
                    setPropertyData(dataType.setRequired(value), childElement);
                } else if ("unique".equals(childElement.getLocalName())) {
                    // not yet implemented
                } else if ("getprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_GET,childElement);
                } else if ("setprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_SET,childElement);
                } else if ("commitprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_COMMIT,childElement);
                } else if (dataType instanceof StringDataType) {
                    addStringCondition(childElement);
                } else if (dataType instanceof BigDataType) {
                    addBigDataCondition(childElement);
                } else if (dataType instanceof IntegerDataType) {
                    addIntegerCondition(childElement);
                } else if (dataType instanceof LongDataType) {
                    addLongCondition(childElement);
                } else if (dataType instanceof FloatDataType) {
                    addFloatCondition(childElement);
                } else if (dataType instanceof DoubleDataType) {
                    addDoubleCondition(childElement);
                } else if (dataType instanceof DateTimeDataType) {
                    // not yet implemented
                } else if (dataType instanceof ListDataType) {
                    // not yet implemented
                }
            }
        }
    }

    protected boolean getBooleanValue(Element element, boolean defaultValue) {
        if (hasAttribute(element, "value")) {
            return Boolean.valueOf(getAttribute(element, "value")).booleanValue();
        } else {
            return defaultValue;
        }
    }

    private Processor chainProcessors(Processor processor1, Processor processor2) {
        Processor processor = processor1;
        if (processor == null) {
            processor = processor2;
        } else if (processor instanceof ChainedProcessor) {
            ((ChainedProcessor) processor).add(processor2);
        } else {
            ChainedProcessor chain = new ChainedProcessor();
            chain.add(processor1);
            chain.add(processor2);
            processor = chain;
        }
        return processor;
    }

    private Processor createProcessor(Element processorElement) {
        Processor processor = null;
        NodeList childNodes = processorElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            Element classElement = (Element) childNodes.item(k);
            if ("class".equals(classElement.getLocalName())) {
                String clazString = getValue(classElement);
                try {
                    Class claz = Class.forName(clazString);
                    Processor newProcessor = null;
                    if (CharTransformer.class.isAssignableFrom(claz)) {
                        CharTransformer charTransformer = Transformers.getCharTransformer(clazString, null, " valueintercepter ", false);
                        if (charTransformer != null) {
                            newProcessor = new CharTransformerProcessor(charTransformer);
                        }
                    } else if (Processor.class.isAssignableFrom(claz)) {
                        newProcessor = (Processor)claz.newInstance();
                    } else {
                        log.error("Found class " + clazString + " is not a Processor or a CharTransformer");
                    }
                    processor = chainProcessors(processor, newProcessor);
                } catch (ClassNotFoundException cnfe) {
                    log.error("Class " + clazString + " could not be found");
                } catch (IllegalAccessException iae) {
                    log.error("Class " + clazString + " may  not be instantiated");
                } catch (InstantiationException ie) {
                    log.error("Class " + clazString + " can not be instantiated");
                }
            }
        }
        return processor;
    }

    private void addProcessor(int action, int processingType, Processor newProcessor) {
        Processor oldProcessor = dataType.getProcessor(action, processingType);
        newProcessor = chainProcessors(oldProcessor, newProcessor);
        dataType.setProcessor(action, newProcessor, processingType);
    }

    protected void addProcessor(int action, Element processorElement) {
        Processor newProcessor = createProcessor(processorElement);
        if (newProcessor != null) {
            if (action != DataType.PROCESS_COMMIT) {
                String type = processorElement.getAttribute("type");
                if (type != null && !type.equals("") && !type.equals("*")) {
                    int processingType = Field.TYPE_UNKNOWN;
                    DataType basicDataType = DataTypes.getDataType(type);
                    if (basicDataType != null) {
                        processingType = DataTypes.classToType(basicDataType.getTypeAsClass());
                    } else {
                        log.warn("Datatype " + type + " is unknown, create processor as a default processor");
                    }
                    addProcessor(action, processingType, newProcessor);
                } else {
                    // todo: iterate through all types?
                    addProcessor(action, Field.TYPE_UNKNOWN, newProcessor);
                }
            } else {
                addProcessor(action, Field.TYPE_UNKNOWN, newProcessor);
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

    protected void addBigDataCondition(Element conditionElement) {
        BigDataType bDataType = (BigDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(bDataType.setMinLength(value), conditionElement);
            // better:
        } else if ("maxLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(bDataType.setMaxLength(value), conditionElement);
        } else if ("length".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(bDataType.setMinLength(value), conditionElement);
            setPropertyData(bDataType.setMaxLength(value), conditionElement);
        }
    }

    protected void addStringCondition(Element conditionElement) {
        StringDataType sDataType = (StringDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("pattern".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            setPropertyData(sDataType.setPattern(value), conditionElement);
        } else if ("whitespace".equals(localName)) {
            // not yet implemented
        } else {
            addBigDataCondition(conditionElement);
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
            setPropertyData(iDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Integer value = getIntegerValue(conditionElement);
            setPropertyData(iDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setPropertyData(lDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Long value = getLongValue(conditionElement);
            setPropertyData(lDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setPropertyData(fDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Float value = getFloatValue(conditionElement);
            setPropertyData(fDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setPropertyData(dDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Double value = getDoubleValue(conditionElement);
            setPropertyData(dDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
        }
    }

}

