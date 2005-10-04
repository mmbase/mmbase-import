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
 * XXX MM: This class does the actual datatype reading, so more correctly this one would be called
 * DatatypeReader?
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: DataTypeDefinition.java,v 1.22 2005-10-04 17:18:37 michiel Exp $
 * @since MMBase-1.8
 **/
public class DataTypeDefinition {

    private static final Logger log = Logging.getLoggerInstance(DataTypeDefinition.class);

    /**
     * The data type which will be produced
     */
    public DataType dataType = null;

    /**
     * The base data type on which it was based, or <code>null</code>
     */
    private DataType baseDataType = null;

    /**
     * The data type collector that contains the data datatype with this definition.
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
    protected static boolean hasAttribute(Element element, String localName) {
        return DocumentReader.hasAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected static String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }

    private static int anonymousSequence = 1;

    private String getId(String id) {
        if (id.equals("")) {
            if (baseDataType == null) {
                return "ANONYMOUS" + anonymousSequence++;
            } else {
                return  baseDataType.getName() + anonymousSequence++;
            }
        } else {
            return id;
        }
    }
    /**
     * If id was empty string, then this can still be equal to baseDataType, and nothing changed. Never <code>null</code>
     * @param   dataTypeElement piece of XML used to configure. Only the 'class' subelements are explored in this method.
     * @param   id              the new id or empty string (which means that is still identical to baseDataType)
     *
     */
    private  void getImplementation(Element dataTypeElement, String id) {
        DataType dt = id.equals("") ? null : collector.getDataType(id);
        if (dt != null) {
            collector.rewrite(dt);
        }

        NodeList childNodes = dataTypeElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                if (childElement.getLocalName().equals("class")) {
                    String className = childElement.getAttribute("name");
                    if (dt != null) {
                        log.debug("Already defined " + id);
                        if (! className.equals(dt.getClass().getName())) {
                            log.error("Cannot change class for '" + id + "' from " + dt.getClass().getName() + " to '" + className + "'");
                        }
                    } else {
                        try {
                            Class claz = Class.forName(className);
                            log.info("Instantiating " + claz + " for " + dataType);
                            java.lang.reflect.Constructor constructor = claz.getConstructor(new Class[] { String.class});
                            dt = (DataType) constructor.newInstance(new Object[] { getId(id) });
                            if (baseDataType != null) {
                                // should check class here, perhaps
                                dt.inherit(baseDataType);
                            }
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
            if (id.equals("")) {
                dataType = baseDataType;
            } else {
                dataType = (DataType) baseDataType.clone(id);
                dataType.clear(); // clears datatype.
            }
        } else {
            dataType = dt;
        }

    }

    /**
     * Configures the data type definition, using data from a DOM element
     */
    DataTypeDefinition configure(Element dataTypeElement, DataType requestBaseDataType) {

        String id = getAttribute(dataTypeElement, "id");

        if ("byte".equals(id)) {
            log.warn("Found for datatype id 'byte', supposing that 'binary' is meant");
            id = "binary"; // hmmmmm
        }

        String base = getAttribute(dataTypeElement, "base");
        if (log.isDebugEnabled()) {
            log.debug("Reading element id='" + id + "' base='" + base + "'");
        }
        if (! base.equals("")) { // also specified, let's see if it is correct

            DataType definedBaseDataType = collector.getDataType(base, true);
            if (requestBaseDataType != null) {
                if (requestBaseDataType != definedBaseDataType) {
                    log.warn("Attribute 'base' ('" + base+ "') not allowed with datatype '" + id + "', because it has already an baseDataType '" + baseDataType + "'");
                }
            }
            if (definedBaseDataType == null) {
                log.warn("Attribute 'base' ('" + base + "') of datatype '" + id + "' is an unknown datatype.");
            } else {
                requestBaseDataType = definedBaseDataType;
            }
        }

        baseDataType = requestBaseDataType;

        getImplementation(dataTypeElement, id);
        configureConditions(dataTypeElement);

        return this;
    }


    /**
     * This utility takes care of reading the xml:lang attribute from an element
     */
    protected static Locale getLocale(Element element) {
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


    protected static LocalizedString getLocalizedDescription(String tagName, Element element, LocalizedString descriptions) {
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if (tagName.equals(childElement.getLocalName())) {
                    Locale locale = getLocale(childElement);
                    String description = DocumentReader.getNodeTextValue(childElement);
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

    private static final java.util.regex.Pattern nonConditions   = java.util.regex.Pattern.compile("specialization|datatype|class");

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
                if (dataType == baseDataType) {
                    dataType = (DataType) baseDataType.clone(getId(""));
                }
                String childTag = childElement.getLocalName();

                if ("required".equals(childTag)) {
                    boolean value = getBooleanValue(childElement, false);
                    setConstraintData(dataType.setRequired(value), childElement);
                } else if ("unique".equals(childTag)) {
                    boolean value = getBooleanValue(childElement, false);
                    setConstraintData(dataType.setUnique(value), childElement);
                } else if ("getprocessor".equals(childTag)) {
                    addProcessor(DataType.PROCESS_GET, childElement);
                } else if ("setprocessor".equals(childTag)) {
                    addProcessor(DataType.PROCESS_SET, childElement);
                } else if ("commitprocessor".equals(childTag)) {
                    addProcessor(DataType.PROCESS_COMMIT, childElement);
                } else if ("enumeration".equals(childTag)) {
                    addEnumeration(childElement);
                } else if ("default".equals(childTag)) {
                    String value = getAttribute(childElement, "value");
                    dataType.setDefaultValue(value);
                } else if (nonConditions.matcher(childTag).matches()) {
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


    private Object getParameterValue(Element param) {
        String stringValue = DocumentReader.getNodeTextValue(param);
        NodeList childNodes = param.getChildNodes();
        Collection subParams = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (! (child instanceof Element)) continue;
            if (child.getLocalName().equals("param")) {
                Element subParam = (Element) child;
                if (subParams == null) subParams = new ArrayList();
                String name = subParam.getAttribute("name");
                subParams.add(new Entry(name, getParameterValue(subParam)));
            }
        }
        if (subParams != null) {
            if (! stringValue.equals("")) {
                log.warn("" + param + " has both a text value and sub parameters, ignoring the text value '" + stringValue + "'");
            }
            return subParams;
        } else {
            return stringValue;
        }
    }
    private void fillParameters(Element paramContainer, Parameters params) {
        NodeList childNodes = paramContainer.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element paramElement = (Element) childNodes.item(i);
                if ("param".equals(paramElement.getLocalName())) {
                    String name = paramElement.getAttribute("name");
                    Object value = getParameterValue(paramElement);
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
                        clazString = DocumentReader.getNodeTextValue(classElement);
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
                if (!type.equals("") && !type.equals("*")) { // "" was not equal to "*" !
                    int processingType = Field.TYPE_UNKNOWN;
                    DataType basicDataType = DataTypes.getDataType(type); // this makes NO sense, processors type are assocated with bridge methods (field types) not with datatypes
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
                try {
                    fact.addBundle(resource, getClass().getClassLoader(), constantsClass,
                                   wrapper, comparator);
                } catch (MissingResourceException mre) {
                    log.error(mre);
                }
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

    protected void addDateTimeCondition(Element conditionElement) {
        DateTimeDataType dtDataType = (DateTimeDataType) dataType;
        String localName = conditionElement.getLocalName();
        if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
            Date value = getDateTimeValue(conditionElement);
            setConstraintData(dtDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
        } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
            Date value = getDateTimeValue(conditionElement);
            setConstraintData(dtDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
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

