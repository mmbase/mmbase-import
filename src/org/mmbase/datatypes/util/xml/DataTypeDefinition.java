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
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;

/**
 * This utility class contains methods to instantiate the right DataType instance. It is used by DataTypeReader.
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeDefinition.java,v 1.19 2005-09-14 11:01:26 michiel Exp $
 * @since MMBase-1.8
 **/
public class DataTypeDefinition {

    private static final Logger log = Logging.getLoggerInstance(DataTypeDefinition.class);

    /**
     * the data type
     */
    public DataType dataType = null;

    /**
     * The data type collector that contains the data datatype which this definition.
     */
    protected final DataTypeCollector collector;

    /**
     * Constructor.
     */
    public DataTypeDefinition(DataTypeCollector collector) {
        this.collector = collector;
    }

    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected boolean hasAttribute(Element element, String localName) {
        return DocumentReader.hasAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }

    /**
     * Returns the textual value (content) of a certain element
     */
    protected String getValue(Element element) {
        return DocumentReader.getNodeTextValue(element);
    }

    private  DataType getImplementation(Element dataTypeElement, String id, DataType baseDataType) {
        DataType dt = collector.getDataType(id);
        NodeList childNodes = dataTypeElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                if (childElement.getLocalName().equals("class")) {
                    if (dt != null) {
                        log.error("Already defained " + id);                        
                    } else {
                        try {
                            String className = childElement.getAttribute("name");
                            Class claz = Class.forName(className);
                            java.lang.reflect.Constructor constructor = claz.getConstructor(new Class[] { String.class});
                            dt = (DataType) constructor.newInstance(new Object[] { id });
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                    break;
                } else {
                    continue;
                }              
            }
        }
        if (dt == null) {
            if (baseDataType == null) {
                log.warn("No base datatype available and no class specified for datatype '" + id + "', using 'unknown' for know.\n" + org.mmbase.util.xml.XMLWriter.write(dataTypeElement, true));
                baseDataType = Constants.DATATYPE_UNKNOWN;
            }
            dt = (DataType)baseDataType.clone(id);
        } else {
            //
            // XXX: add check on base datatype if given!
            //
            collector.rewrite(dt);
            dt.clear(); // clears datatype.
        }
        return dt;

        
    }

    private static int anonymousSequence = 1;
    /**
     * Configures the data type definition, using data from a DOM element
     */
    DataTypeDefinition configure(Element dataTypeElement, DataType baseDataType) {

        String typeString = getAttribute(dataTypeElement, "id");
        if (typeString.equals("")) {
            if (baseDataType == null) {
                typeString = "ANONYMOUS" + anonymousSequence++;
            } else {
                typeString = baseDataType.getName() + "_" + anonymousSequence++;
            }
        }
        if ("byte".equals(typeString)) {
            log.warn("Found for datatype id 'byte', supposing that 'binary' is meant");
            typeString = "binary"; // hmmmmm
        }

        String baseString = getAttribute(dataTypeElement, "base");
        if (log.isDebugEnabled()) {
            log.debug("Reading element " + typeString + " " + baseString);
        }
        if (! baseString.equals("")) {
            DataType definedBaseDataType = collector.getDataType(baseString, true);
            if (baseDataType != null) {
                if (baseDataType != definedBaseDataType) {
                    log.warn("Attribute 'base' ('" + baseString+ "') not allowed with datatype '" + typeString + "', because it has already an baseDataType '" + baseDataType + "'");
                }                
            }
            if (definedBaseDataType == null) {
                log.warn("Attribute 'base' ('" + baseString + "') of datatype '" + typeString + "' is an unknown datatype.");
            } else {
                baseDataType = definedBaseDataType;
            }
        }

        dataType = getImplementation(dataTypeElement, typeString, baseDataType);

        configureConditions(dataTypeElement);
        return this;
    }


    /**
     * This utility takes care of reading the xml:lang attribute from an element
     */
    protected Locale getLocale(Element element) {
        Locale loc = null;
        String xmlLang = getAttribute(element, "xml:lang");
        if (! xmlLang.equals("")) {
            String[] split = xmlLang.split("-");
            if (split.length == 1) {
                loc = new Locale(split[0]);
            } else {
                loc = new Locale(split[0], split[1]);
            }
        }
        return loc;
    }


    protected LocalizedString getLocalizedDescription(String tagName, Element element, LocalizedString descriptions) {
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if (tagName.equals(childElement.getLocalName())) {
                    Locale locale = getLocale(childElement);
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

    protected void setConstraintData(DataType.ValueConstraint property, Element element) {
        // set fixed
        if (hasAttribute(element, "fixed")) {
            boolean isFixed = Boolean.valueOf(getAttribute(element, "fixed")).booleanValue();
            property.setFixed(isFixed);
        }
        LocalizedString descriptions = property.getErrorDescription();
        property.setErrorDescription(getLocalizedDescription("description", element, descriptions));
    }

    private static final java.util.regex.Pattern nonConditions   = java.util.regex.Pattern.compile("specialization|datatype");

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
                    setConstraintData(dataType.setRequired(value), childElement);
                } else if ("unique".equals(childElement.getLocalName())) {
                    boolean value = getBooleanValue(childElement, false);
                    setConstraintData(dataType.setUnique(value), childElement);
                } else if ("getprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_GET, childElement);
                } else if ("setprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_SET, childElement);
                } else if ("commitprocessor".equals(childElement.getLocalName())) {
                    addProcessor(DataType.PROCESS_COMMIT, childElement);
                } else if ("enumeration".equals(childElement.getLocalName())) {
                    addEnumeration(childElement);
                } else if ("default".equals(childElement.getLocalName())) {
                    String value = getAttribute(childElement, "value");
                    dataType.setDefaultValue(value);
                } else if (nonConditions.matcher(childElement.getLocalName()).matches()) {
                    // ignore
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

    private void fillParameters(Element paramContainer, Parameters params) {
        NodeList childNodes = paramContainer.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element paramElement = (Element) childNodes.item(i);
                if ("param".equals(paramElement.getLocalName())) {
                    String name = paramElement.getAttribute("name");
                    String value = getValue(paramElement);
                    params.set(name, value);                    
                }
            }
        }     
    }

    private Processor createProcessor(Element processorElement) {
        Processor processor = null;
        NodeList childNodes = processorElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element classElement = (Element) childNodes.item(k);
                if ("class".equals(classElement.getLocalName())) {
                    String clazString = classElement.getAttribute("name");
                    if (clazString.equals("")) {
                        log.warn("No 'name' attribute on " + org.mmbase.util.xml.XMLWriter.write(classElement, true) + ", trying body");
                        clazString = getValue(classElement);
                    }
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
                        } else if (ParameterizedTransformerFactory.class.isAssignableFrom(claz)) {
                            ParameterizedTransformerFactory factory = (ParameterizedTransformerFactory) claz.newInstance();
                            Parameters params = factory.createParameters();
                            fillParameters(classElement, params);
                            Transformer transformer = factory.createTransformer(params);
                            newProcessor = new CharTransformerProcessor((CharTransformer) transformer);
                        } else if (ParameterizedProcessorFactory.class.isAssignableFrom(claz)) {
                            ParameterizedProcessorFactory factory = (ParameterizedProcessorFactory) claz.newInstance();
                            Parameters params = factory.createParameters();
                            fillParameters(classElement, params);
                            newProcessor = factory.createProcessor(params);
                        } else {
                            log.error("Found class " + clazString + " is not a Processor or a CharTransformer, nor a factory for those.");
                        }
                        processor = chainProcessors(processor, newProcessor);
                    } catch (ClassNotFoundException cnfe) {
                        log.error("Class '" + clazString + "' could not be found");
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
        LocalizedEntryListFactory fact = dataType.getEnumerationFactory();
        if (!value.equals("")) {
            Locale locale = getLocale(enumerationElement);
            String display = enumerationElement.getAttribute("display");
            if (display.equals("")) display = value;
            fact.add(locale, value, display);
        } else {
            String resource = enumerationElement.getAttribute("resource");
            if (! resource.equals("")) {
                Comparator comparator = null;
                Class wrapper    = dataType.getTypeAsClass();
                {
                    String sorterClass = enumerationElement.getAttribute("sorterclass");
                    if (!sorterClass.equals("")) {
                        try {
                            Class sorter = Class.forName(sorterClass);
                            if (Comparator.class.isAssignableFrom(sorter)) {
                                comparator = (Comparator) sorter.newInstance();
                            } else {
                                wrapper = sorter;
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
                Class constantsClass = null;
                {
                    String javaConstants = enumerationElement.getAttribute("constantsclass");
                    if (!javaConstants.equals("")) {
                        try {
                            constantsClass = Class.forName(javaConstants);
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
                fact.addBundle(resource, getClass().getClassLoader(), constantsClass,
                               wrapper, comparator);
            } else {
                throw new IllegalArgumentException("no 'value' or 'resource' attribute on enumeration element");
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
            setConstraintData(bDataType.setMinLength(value), conditionElement);
        } else if ("maxLength".equals(localName)) {
            int value = getIntValue(conditionElement);
            setConstraintData(bDataType.setMaxLength(value), conditionElement);
        } else if ("length".equals(localName)) {
            int value = getIntValue(conditionElement);
            setConstraintData(bDataType.setMinLength(value), conditionElement);
            setConstraintData(bDataType.setMaxLength(value), conditionElement);
        } else {
            log.error("Unsupported tag '" + localName + "' for bigdata.");
        }
    }

    protected void addStringCondition(Element conditionElement) {
        StringDataType sDataType = (StringDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("pattern".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            setConstraintData(sDataType.setPattern(java.util.regex.Pattern.compile(value)), conditionElement);
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
            setConstraintData(sDataType.setWhiteSpace(whiteSpaceValue), conditionElement);
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
            setConstraintData(iDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Integer value = getIntegerValue(conditionElement);
            setConstraintData(iDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setConstraintData(lDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Long value = getLongValue(conditionElement);
            setConstraintData(lDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setConstraintData(fDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Float value = getFloatValue(conditionElement);
            setConstraintData(fDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setConstraintData(dDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Double value = getDoubleValue(conditionElement);
            setConstraintData(dDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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
            setConstraintData(dtDataType.setMin(value, precision, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Date value = getDateTimeValue(conditionElement);
            int precision = getDateTimePartValue(conditionElement);
            setConstraintData(dtDataType.setMax(value, precision, "maxInclusive".equals(localName)), conditionElement);
        } else if ("pattern".equals(localName)) {
            String pattern = getAttribute(conditionElement, "value");
            Locale locale = getLocale(conditionElement);
            dtDataType.setPattern(pattern, locale);
        } else {
            log.error("Unsupported tag '" + localName + "' for datetime.");
        }
    }

    protected void addListCondition(Element conditionElement) {
        ListDataType lDataType = (ListDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minSize".equals(localName)) {
            int value = getIntValue(conditionElement);
            setConstraintData(lDataType.setMinSize(value), conditionElement);
        } else if ("maxSize".equals(localName)) {
            int value = getIntValue(conditionElement);
            setConstraintData(lDataType.setMaxSize(value), conditionElement);
        } else if ("itemDataType".equals(localName)) {
            String value = getAttribute(conditionElement, "value");
            setConstraintData(lDataType.setItemDataType(collector.getDataType(value)), conditionElement);
        } else {
            log.error("Unsupported tag '" + localName + "' for list.");
        }
    }

    public String toString() {
        return "definition(" + (dataType == null ? "NONE" : dataType.toString()) + ")";
    }

}

