/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import java.util.*;
import org.w3c.dom.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeDefinition.java,v 1.8 2005-08-29 14:33:02 michiel Exp $
 * @since MMBase-1.8
 **/
public class DataTypeDefinition {

    private static final Logger log = Logging.getLoggerInstance(DataTypeDefinition.class);

    /**
     * the data type
     */
    public DataType dataType = null;

    /**
     * The data type configurer that instantiated this definition
     */
    protected DataTypeConfigurer configurer = null;

    /**
     * Constructor.
     */
    public DataTypeDefinition(DataTypeConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected boolean hasAttribute(Element element, String localName) {
        return DocumentReader.hasAttribute(element,DataTypeReader.NAMESPACE_DATATYPES,localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element,DataTypeReader.NAMESPACE_DATATYPES,localName);
    }

    /**
     * Returns the textual value (content) of a certain element
     */
    protected String getValue(Element element) {
        return DocumentReader.getNodeTextValue(element);
    }

    /**
     * Configures the data type definition, using data from a DOM element
     */
    public DataTypeDefinition configure(Element dataTypeElement, DataType baseDataType) {
        String typeString = getAttribute(dataTypeElement,"id");
        if ("byte".equals(typeString)) typeString = "binary";
        String baseString = getAttribute(dataTypeElement,"base");
        if (log.isDebugEnabled()) log.debug("Reading element " + typeString + " " + baseString);
        if (baseString != null && !baseString.equals("")) {
            if (baseDataType != null) {
                log.warn("Attribute 'base' ('" + baseDataType + "') not allowed with datatype " + typeString + ".");
            } else {
                baseDataType = configurer.getDataType(baseString);
                if (baseDataType == null) {
                    log.warn("Attribute 'base' of datatype '" + typeString + "' is an unknown datatype.");
                }
            }
        }
        dataType = configurer.getDataType(typeString);
        if (dataType == null) {
            if (baseDataType == null) {
                log.warn("No base datatype available for datatype " + typeString + ", use 'unknown' for know.");
                baseDataType = Constants.DATATYPE_UNKNOWN;
            }
            dataType = (DataType)baseDataType.clone(typeString);
        } else {
            //
            // XXX: add check on base datatype if given!
            //
            configurer.rewrite(dataType);
            dataType.clear(); // clears datatype.
        }
        configureConditions(dataTypeElement);
        return this;
    }

    protected LocalizedString getLocalizedDescriptions(String tagName, Element element, LocalizedString descriptions) {
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if (tagName.equals(childElement.getLocalName())) {
                    Locale locale = null;
                    if (hasAttribute(childElement, "xml:lang")) {
                        String language = getAttribute(childElement, "xml:lang");
                        locale = new Locale(language, null);
                    }
                    String description = getValue(childElement);
                    if (descriptions ==  null) {
                        descriptions = new LocalizedString(description);
                    }
                    descriptions.set(description, locale);
                }
            }
        }
        return descriptions;
    }

    protected void setPropertyData(DataType.Property property, Element element) {
        // set fixed
        if (hasAttribute(element, "fixed")) {
            boolean isFixed = Boolean.valueOf(getAttribute(element, "fixed")).booleanValue();
            property.setFixed(isFixed);
        }
        LocalizedString descriptions = property.getLocalizedErrorDescription();
        property.setLocalizedErrorDescription(getLocalizedDescriptions("description", element, descriptions));
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
                if (childElement.getLocalName().equals("")) {
                    continue;
                }
                if ("required".equals(childElement.getLocalName())) {
                    boolean value = getBooleanValue(childElement, false);
                    setPropertyData(dataType.setRequired(value), childElement);
                } else if ("unique".equals(childElement.getLocalName())) {
                    boolean value = getBooleanValue(childElement, false);
                    setPropertyData(dataType.setUnique(value), childElement);
                } else if ("getprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_GET, childElement);
                } else if ("setprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_SET, childElement);
                } else if ("commitprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_COMMIT, childElement);
                } else if ("enumeration".equals(childElement.getLocalName())) {
                    addEnumeration(childElement);
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
                    addDateTimeCondition(childElement);
                } else if (dataType instanceof ListDataType) {
                    addListCondition(childElement);
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
            if (childNodes.item(k) instanceof Element) {
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

    protected void addEnumeration(Element enumerationElement) {
        String value = enumerationElement.getAttribute("value");
        if (value != null && !value.equals("")) {
            DataType.EnumerationValue enumerationValue = dataType.addEnumerationValue(value);
            // set display
            LocalizedString descriptions = enumerationValue.getLocalizedDescription();
            enumerationValue.setLocalizedDescription(getLocalizedDescriptions("display", enumerationElement, descriptions));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
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
        } else if ("maxLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(bDataType.setMaxLength(value), conditionElement);
        } else if ("length".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(bDataType.setMinLength(value), conditionElement);
            setPropertyData(bDataType.setMaxLength(value), conditionElement);
        } else {
            log.error("Unsupported tag '" + localName + "' for bigdata.");
        }
    }

    protected void addStringCondition(Element conditionElement) {
        StringDataType sDataType = (StringDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("pattern".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            setPropertyData(sDataType.setPattern(java.util.regex.Pattern.compile(value)), conditionElement);
        } else if ("whiteSpace".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            Integer whiteSpaceValue = null;
            if (value == null || value.equals("preserve")) {
                whiteSpaceValue = StringDataType.WHITESPACE_PRESERVE;
            } else if (value.equals("replace")) {
                whiteSpaceValue = StringDataType.WHITESPACE_REPLACE;
            } else if (value.equals("collapse")) {
                whiteSpaceValue = StringDataType.WHITESPACE_COLLAPSE;
            }
            setPropertyData(sDataType.setWhiteSpace(whiteSpaceValue), conditionElement);
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
        } else {
            log.error("Unsupported tag '" + localName + "' for integer.");
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
        } else {
            log.error("Unsupported tag '" + localName + "' for long.");
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
        } else {
            log.error("Unsupported tag '" + localName + "' for float.");
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
        } else {
            log.error("Unsupported tag '" + localName + "' for double.");
        }
    }

    protected Date getDateTimeValue(Element element) {
        if (hasAttribute(element, "value")) {
            Date date = Casting.toDate(getAttribute(element, "value"));
            return date;
        } else {
            throw new IllegalArgumentException("no 'value' attribute");
        }
    }

    protected int getDateTimePartValue(Element element) {
        if (hasAttribute(element, "part")) {
            String value = getAttribute(element, "value").toLowerCase();
            return Queries.getDateTimePart(value);
        } else {
            return Calendar.MILLISECOND;
        }
    }

    protected void addDateTimeCondition(Element conditionElement) {
        DateTimeDataType dtDataType = (DateTimeDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Date value = getDateTimeValue(conditionElement);
            int precision = getDateTimePartValue(conditionElement);
            setPropertyData(dtDataType.setMin(value, precision, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Date value = getDateTimeValue(conditionElement);
            log.info("Found " + value + " for max");
            int precision = getDateTimePartValue(conditionElement);
            setPropertyData(dtDataType.setMax(value, precision, "maxInclusive".equals(localName)), conditionElement);
        } else {
            log.error("Unsupported tag '" + localName + "' for datetime.");
        }
    }

    protected void addListCondition(Element conditionElement) {
        ListDataType lDataType = (ListDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minSize".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(lDataType.setMinSize(value), conditionElement);
        } else if ("maxSize".equals(localName)) {
            int value = getIntValue(conditionElement);
            setPropertyData(lDataType.setMaxSize(value), conditionElement);
        } else if ("itemDataType".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            setPropertyData(lDataType.setItemDataType(configurer.getDataType(value)), conditionElement);
        } else {
            log.error("Unsupported tag '" + localName + "' for list.");
        }
    }

    public String toString() {
        return dataType == null ? "NONE" : dataType.toString();
    }

}

