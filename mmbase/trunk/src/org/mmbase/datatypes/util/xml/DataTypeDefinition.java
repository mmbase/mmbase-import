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
import org.mmbase.core.util.Fields;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.XMLWriter;
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
 * @version $Id: DataTypeDefinition.java,v 1.25 2005-10-07 00:16:34 michiel Exp $
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

        // Check the class element only.
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
                                dt.inherit((BasicDataType) baseDataType);
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
        if (dt == null) { // either it had no id, or it did not exist yet.
            if (baseDataType == null) {
                log.warn("No base datatype available and no class specified for datatype '" + id + "', using 'unknown' for know.\n" + XMLWriter.write(dataTypeElement, true, true));
                baseDataType = Constants.DATATYPE_UNKNOWN;
            }
            if (id.equals("")) {
                log.debug("No id given, for the time being this datatype will be equal to its base type " + baseDataType);
                dataType = baseDataType;
            } else {
                log.debug("Id given, cloning " + baseDataType);
                dataType = (DataType) baseDataType.clone(id);
            }
        } else { // means that it existed it already
            log.debug("Existing datatype " + dt + " with base " + baseDataType);
            dataType = dt;
        }

    }

    /**
     * Configures the data type definition, using data from a DOM element
     */
    DataTypeDefinition configure(Element dataTypeElement, DataType requestBaseDataType) {

        String id = DataTypeXml.getAttribute(dataTypeElement, "id");

        if ("byte".equals(id)) {
            log.warn("Found for datatype id 'byte', supposing that 'binary' is meant");
            id = "binary"; // hmmmmm
        }

        String base = DataTypeXml.getAttribute(dataTypeElement, "base");
        if (log.isDebugEnabled()) {
            log.debug("Reading element id='" + id + "' base='" + base + "' req datatype " + requestBaseDataType);
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



    private static final java.util.regex.Pattern nonConditions   = java.util.regex.Pattern.compile("specialization|datatype|class");


    /**
     * Configures the conditions of a datatype definition, using data from a DOM element
     */
    protected void configureConditions(Element dataTypeElement) {
        log.debug("Now going to configure " + dataType);
        // add conditions
        NodeList childNodes = dataTypeElement.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if (childElement.getLocalName().equals("")) {
                    continue;
                }
                if (nonConditions.matcher(childElement.getLocalName()).matches()) {
                    continue;
                }
                if (dataType == baseDataType) {
                    log.debug("About to add/change conditions, need clone first!");
                    dataType = (DataType) baseDataType.clone(getId(""));
                }
                log.debug("Considering " + childElement.getLocalName() + " for " + dataType);
                if (!addCondition(childElement)) {
                    log.error("" + XMLWriter.write(childElement, true, true) + " defines '" + childElement.getLocalName() + "', but " + dataType + " doesn't support that");
                }
            }
        }
    }

    /**
     * Uses one subelement of a datatype xml configuration element and interpret it. Possibly this
     * method is a good candidate to override.
     * @return <code>childElement</code>
     */
    protected boolean addCondition(Element childElement) {
        boolean ret = false;
        String childTag = childElement.getLocalName();
        if ("required".equals(childTag)) {
            boolean value = DataTypeXml.getBooleanValue(childElement, false);
            setConstraintData(dataType.setRequired(value), childElement);
            ret = true;
        } else if ("unique".equals(childTag)) {
            boolean value = DataTypeXml.getBooleanValue(childElement, false);
            setConstraintData(dataType.setUnique(value), childElement);
            ret = true;
        } else if ("getprocessor".equals(childTag)) {
            addProcessor(DataType.PROCESS_GET, childElement);
            ret = true;
        } else if ("setprocessor".equals(childTag)) {
            addProcessor(DataType.PROCESS_SET, childElement);
            ret = true;
        } else if ("commitprocessor".equals(childTag)) {
            addProcessor(DataType.PROCESS_COMMIT, childElement);
            ret = true;
        } else if ("enumeration".equals(childTag)) {
            addEnumeration(childElement);
            ret = true;
        } else if ("default".equals(childTag)) {
            String value = DataTypeXml.getAttribute(childElement, "value");
            dataType.setDefaultValue(value);
            ret = true;
        } else if (addPatternCondition(childElement)) {
            ret = true;
        } else if (addLengthDataCondition(childElement)) {
            ret =  true;
        } else if (addComparableCondition(childElement)) {
            ret = true;
        }
        return ret;
    }


    private void addProcessor(int action, int processingType, Processor newProcessor) {
        Processor oldProcessor = dataType.getProcessor(action, processingType);
        newProcessor = DataTypeXml.chainProcessors(oldProcessor, newProcessor);
        dataType.setProcessor(action, newProcessor, processingType);
    }


    protected  void addProcessor(int action, Element processorElement) {
        Processor newProcessor = DataTypeXml.createProcessor(processorElement);
        if (newProcessor != null) {
            if (action != DataType.PROCESS_COMMIT) {
                String type = processorElement.getAttribute("type");
                if (!type.equals("") && !type.equals("*")) { // "" was not equal to "*" !
                    int processingType = Field.TYPE_UNKNOWN;
                    DataType basicDataType = DataTypes.getDataType(type); // this makes NO sense, processors type are assocated with bridge methods (field types) not with datatypes
                    if (basicDataType != null) {
                        processingType = Fields.classToType(basicDataType.getTypeAsClass());
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

    protected void setConstraintData(DataType.ValueConstraint property, Element element) {
        // set fixed
        if (DataTypeXml.hasAttribute(element, "fixed")) {
            boolean isFixed = Boolean.valueOf(DataTypeXml.getAttribute(element, "fixed")).booleanValue();
            property.setFixed(isFixed);
        }
        LocalizedString descriptions = property.getErrorDescription();
        property.setErrorDescription(DataTypeXml.getLocalizedDescription("description", element, descriptions));
    }

    /**
     * Used the enumeration element.
     */
    protected void addEnumeration(Element enumerationElement) {
        String value = enumerationElement.getAttribute("value");
        LocalizedEntryListFactory fact = dataType.getEnumerationFactory();
        if (!value.equals("")) {
            Locale locale = DataTypeXml.getLocale(enumerationElement);
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


    /**
     * Considers length related condition elements ('minLength', 'maxLength' , 'length')
     * @return <code>true</code> if element was used
     */
    protected boolean addLengthDataCondition(Element conditionElement) {
        if (dataType instanceof LengthDataType) {
            String localName = conditionElement.getLocalName();
            LengthDataType bDataType = (LengthDataType) dataType;
            if ("minLength".equals(localName)) {
                long value = DataTypeXml.getLongValue(conditionElement);
                setConstraintData(bDataType.setMinLength(value), conditionElement);
                return true;
            } else if ("maxLength".equals(localName)) {
                long value = DataTypeXml.getLongValue(conditionElement);
                setConstraintData(bDataType.setMaxLength(value), conditionElement);
                return true;
            } else if ("length".equals(localName)) {
                long value = DataTypeXml.getLongValue(conditionElement);
                setConstraintData(bDataType.setMinLength(value), conditionElement);
                setConstraintData(bDataType.setMaxLength(value), conditionElement);
                return true;
            }
        }
        return false;
    }


    /**
     * Considers the 'pattern' condition element.
     * @return <code>true</code> if element was used
     */

    protected boolean addPatternCondition(Element conditionElement) {
        String localName = conditionElement.getLocalName();
        if (dataType instanceof StringDataType) {
            StringDataType sDataType = (StringDataType) dataType;
            if ("pattern".equals(localName)) {
                String value = DataTypeXml.getAttribute(conditionElement, "value");
                log.debug("Setting pattern on " + sDataType);
                setConstraintData(sDataType.setPattern(java.util.regex.Pattern.compile(value)), conditionElement);
                return true;
            }
        } else if (dataType instanceof DateTimeDataType) {
            DateTimeDataType sDataType = (DateTimeDataType) dataType;
            if ("pattern".equals(localName)) {
                String value = DataTypeXml.getAttribute(conditionElement, "value");
                Locale locale = DataTypeXml.getLocale(conditionElement);
                sDataType.setPattern(value, locale);
                return true;
            }
        }
        return false;
    }

    /**
     * Considers the condition elements associated with 'Comparables' (minInclusive, maxInclusive,
     * minExclusive, maxExclusive).
     * @return <code>true</code> if element was used
     */

    protected boolean addComparableCondition(Element conditionElement) {
        if (dataType instanceof ComparableDataType) {
            String localName = conditionElement.getLocalName();
            ComparableDataType dDataType = (ComparableDataType) dataType;
            if ("minExclusive".equals(localName) || "minInclusive".equals(localName)) {
                Comparable value = (Comparable) dDataType.autoCast(DataTypeXml.getValue(conditionElement));
                setConstraintData(dDataType.setMin(value, "minInclusive".equals(localName)), conditionElement);
                return true;
            } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
                Comparable value = (Comparable) dDataType.autoCast(DataTypeXml.getValue(conditionElement));
                setConstraintData(dDataType.setMax(value, "maxInclusive".equals(localName)), conditionElement);
                return true;
            }
        }
        return false;
    }


    public String toString() {
        return "definition(" + (dataType == null ? "NONE" : dataType.toString()) + ")";
    }

}

