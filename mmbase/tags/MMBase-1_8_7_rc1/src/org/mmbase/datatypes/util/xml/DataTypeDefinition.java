/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import java.util.*;
import org.w3c.dom.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.mmbase.bridge.Field;
import org.mmbase.datatypes.processors.*;
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
 * @version $Id$
 * @since MMBase-1.8
 **/
public class DataTypeDefinition {

    private static final Logger log = Logging.getLoggerInstance(DataTypeDefinition.class);

    /**
     * The data type which will be produced
     */
    public BasicDataType dataType = null;

    /**
     * The base data type on which it was based, or <code>null</code>
     */
    private BasicDataType baseDataType = null;

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
     * If id was empty string, then this can still be equal to baseDataType, and nothing changed. Never <code>null</code>
     * @param   dataTypeElement piece of XML used to configure. Only the 'class' subelements are explored in this method.
     * @param   id              the new id or empty string (which means that is still identical to baseDataType)
     *
     */
    private  void getImplementation(Element dataTypeElement, String id) {
        BasicDataType dt = id.equals("") ? null : collector.getDataType(id);
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
                            log.debug("Instantiating " + claz + " for " + dataType);
                            java.lang.reflect.Constructor constructor = claz.getConstructor(new Class[] { String.class});
                            dt = (BasicDataType) constructor.newInstance(new Object[] { id });
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
            dataType = (BasicDataType) baseDataType.clone(id);
        } else { // means that it existed it already
            log.debug("Existing datatype " + dt + " with base " + baseDataType);
            dataType = dt;
        }

    }

    /**
     * Configures the data type definition, using data from a DOM element
     */
    DataTypeDefinition configure(Element dataTypeElement, BasicDataType requestBaseDataType) throws DependencyException {

        String id = DataTypeXml.getAttribute(dataTypeElement, "id");

        String base = DataTypeXml.getAttribute(dataTypeElement, "base");

        if (log.isDebugEnabled()) {
            log.debug("Reading element id='" + id + "' base='" + base + "' req datatype " + requestBaseDataType);
        }
        if (! base.equals("")) { // also specified, let's see if it is correct

            BasicDataType definedBaseDataType = collector.getDataType(base, true);
            if (requestBaseDataType != null) {
                if (requestBaseDataType != definedBaseDataType) {
                    if ("".equals(id)) {
                        // in builder you often 'anonymously' override or define datatype.
                        // don't pollute log with warning if e.g. using datetime datatype on integer. That is supported. Though some features may perish.
                        log.debug("Inheriting a " + definedBaseDataType + " from " + requestBaseDataType + ", functionality may get lost");
                    } else {
                        log.warn("Attribute 'base' ('" + base+ "') not allowed with datatype '" + id + "', because it has already an baseDataType '" + definedBaseDataType + "' in " + XMLWriter.write(dataTypeElement, true, true) + " of " + XMLWriter.write(dataTypeElement.getParentNode(), true, true));
                    }
                    definedBaseDataType = requestBaseDataType; // requestedBaseDataType takes precedence!
                }
            }

            if (definedBaseDataType == null) {
                log.debug("Attribute 'base' ('" + base + "') of datatype '" + id + "' is an unknown datatype (in " + dataTypeElement.getOwnerDocument() + ").");
                throw new DependencyException(dataTypeElement, requestBaseDataType, this);
            } else {
                requestBaseDataType = definedBaseDataType;
            }
        }

        baseDataType = requestBaseDataType;
        getImplementation(dataTypeElement, id);
        LocalizedString description = dataType.getLocalizedDescription();
        DataTypeXml.getLocalizedDescription("description", dataTypeElement, description, dataType.getName());
        LocalizedString name = dataType.getLocalizedGUIName();
        DataTypeXml.getLocalizedDescription("name", dataTypeElement, name, dataType.getName());

        configureConditions(dataTypeElement, id);

        return this;
    }

    private static final java.util.regex.Pattern nonConditions   = java.util.regex.Pattern.compile("specialization|datatype|class|name|description");

    /**
     * Configures the conditions of a datatype definition, using data from a DOM element
     */
    protected void configureConditions(Element dataTypeElement, String id) {
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
                log.debug("Considering " + childElement.getLocalName() + " for " + dataType);
                if (!addCondition(childElement)) {
                    log.error("" + XMLWriter.write(childElement, true, true) + " defines '" + childElement.getLocalName() + "', but " + dataType + " doesn't support that in (" + dataTypeElement.getOwnerDocument() + ")");
                }
            }
        }
    }

    /**
     * Uses one subelement of a datatype xml configuration element and interpret it. Possibly this
     * method is a good candidate to override.
     * @return whether successfully read the element.
     */
    protected boolean addCondition(Element childElement) {
        boolean ret = false;
        String childTag = childElement.getLocalName();
        if ("property".equals(childTag)) {
            ret = setProperty(childElement);
        } else if ("required".equals(childTag)) {
            boolean value = DataTypeXml.getBooleanValue(childElement, false);
            dataType.setRequired(value);
            setRestrictionData(dataType.getRequiredRestriction(), childElement);
            ret = true;
        } else if ("unique".equals(childTag)) {
            boolean value = DataTypeXml.getBooleanValue(childElement, false);
            dataType.setUnique(value);
            setRestrictionData(dataType.getUniqueRestriction(), childElement);
            ret = true;
        } else if ("getprocessor".equals(childTag)) {
            addProcessor(DataType.PROCESS_GET, childElement);
            ret = true;
        } else if ("setprocessor".equals(childTag)) {
            addProcessor(DataType.PROCESS_SET, childElement);
            ret = true;
        } else if ("commitprocessor".equals(childTag)) {
            addCommitProcessor(childElement);
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
        } else if (addPasswordProperty(childElement)) {
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
        log.debug(dataType + " Found processor " + oldProcessor + "--> " + newProcessor);
        dataType.setProcessor(action, newProcessor, processingType);
    }


    protected  void addProcessor(int action, Element processorElement) {
        Processor newProcessor = DataTypeXml.createProcessor(processorElement);
        if (newProcessor != null) {
            String type = processorElement.getAttribute("type");
            if (type.equals("")) {
                addProcessor(action, Field.TYPE_UNKNOWN, newProcessor);
            } else if (type.equals("*")) {
                for (int i = Fields.TYPE_MINVALUE; i <= Fields.TYPE_MAXVALUE; i++) {
                    BasicDataType basicDataType = DataTypes.getDataType(i);
                    int processingType = Fields.classToType(basicDataType.getTypeAsClass());
                    addProcessor(action, processingType, newProcessor);
                }
            } else {
                int processingType = Field.TYPE_UNKNOWN;
                BasicDataType basicDataType = DataTypes.getDataType(type);
                // this makes NO sense, processors type are assocated with bridge methods (field types) not with datatypes
                if (basicDataType != null) {
                    processingType = Fields.classToType(basicDataType.getTypeAsClass());
                } else {
                    log.warn("Datatype " + type + " is unknown, create processor as a default processor");
                }
                addProcessor(action, processingType, newProcessor);
            }
        }
    }

    protected void addCommitProcessor(Element processorElement) {
        CommitProcessor newProcessor = DataTypeXml.createCommitProcessor(processorElement);
        CommitProcessor oldProcessor = dataType.getCommitProcessor();
        newProcessor = DataTypeXml.chainProcessors(oldProcessor, newProcessor);
        dataType.setCommitProcessor(newProcessor);
    }

    protected void setRestrictionData(DataType.Restriction restriction, Element element) {
        if (DataTypeXml.hasAttribute(element, "fixed")) {
            boolean isFixed = Boolean.valueOf(DataTypeXml.getAttribute(element, "fixed")).booleanValue();
            restriction.setFixed(isFixed);
        }
        String enforce = DataTypeXml.getAttribute(element, "enforce").toLowerCase();
        if (enforce.equals("absolute")) {
            restriction.setEnforceStrength(DataType.ENFORCE_ABSOLUTE);
        } else if (enforce.equals("always") || enforce.equals("")) {
            restriction.setEnforceStrength(DataType.ENFORCE_ALWAYS);
        } else if (enforce.equals("onchange")) {
            restriction.setEnforceStrength(DataType.ENFORCE_ONCHANGE);
        } else if (enforce.equals("oncreate")) {
            restriction.setEnforceStrength(DataType.ENFORCE_ONCREATE);
        } else if (enforce.equals("onvalidate")) {
            restriction.setEnforceStrength(DataType.ENFORCE_ONVALIDATE);
        } else if (enforce.equals("never")) {
            restriction.setEnforceStrength(DataType.ENFORCE_NEVER);
        } else {
            log.warn("Unrecognised value for 'enforce' attribute '" + enforce + "' in " + XMLWriter.write(element, true, true));
        }
        LocalizedString descriptions = restriction.getErrorDescription();
        restriction.setErrorDescription(DataTypeXml.getLocalizedDescription("description", element, descriptions, null));
    }

    /**
     * Used the enumeration element.
     */
    protected void addEnumeration(Element enumerationElement) {
        LocalizedEntryListFactory fact = dataType.getEnumerationFactory();
        setRestrictionData(dataType.getEnumerationRestriction(), enumerationElement);
        fact.clear();
        fact.fillFromXml(enumerationElement, dataType.getTypeAsClass());
    }

    protected boolean setProperty(Element element) {
        try {
            String name = DataTypeXml.getAttribute(element, "name");
            String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            String value = DataTypeXml.getAttribute(element, "value");
            Class claz = dataType.getClass();
            Method method = claz.getMethod(methodName, new Class[] {String.class});
            method.invoke(dataType, new Object[] { value });
        } catch (NoSuchMethodException nsme) {
            log.warn(nsme);
            return false;
        } catch (SecurityException se) {
            log.warn(se);
            return false;
        } catch (IllegalAccessException iae) {
            log.warn(iae);
            return false;
        } catch (InvocationTargetException ite) {
            log.warn(ite);
            return false;
        }
        return true;
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
                bDataType.setMinLength(value);
                setRestrictionData(bDataType.getMinLengthRestriction(), conditionElement);
                return true;
            } else if ("maxLength".equals(localName)) {
                long value = DataTypeXml.getLongValue(conditionElement);
                bDataType.setMaxLength(value);
                setRestrictionData(bDataType.getMaxLengthRestriction(), conditionElement);
                return true;
            } else if ("length".equals(localName)) {
                long value = DataTypeXml.getLongValue(conditionElement);
                bDataType.setMinLength(value);
                setRestrictionData(bDataType.getMinLengthRestriction(), conditionElement);
                bDataType.setMaxLength(value);
                setRestrictionData(bDataType.getMaxLengthRestriction(), conditionElement);
                return true;
            }
        }
        return false;
    }



    protected boolean addPasswordProperty(Element propertyElement) {
        String localName = propertyElement.getLocalName();
        if ("password".equals(localName) && (dataType instanceof StringDataType)) {
            StringDataType stringDataType = (StringDataType) dataType;
            boolean value = Casting.toBoolean(DataTypeXml.getAttribute(propertyElement, "value"));
            stringDataType.setPassword(value);
            return true;
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
                sDataType.setPattern(java.util.regex.Pattern.compile(value));
                    setRestrictionData(sDataType.getPatternRestriction(), conditionElement);
                return true;
            }
        } else if (dataType instanceof DateTimeDataType) {
            DateTimeDataType sDataType = (DateTimeDataType) dataType;
            if ("pattern".equals(localName)) {
                String value = DataTypeXml.getAttribute(conditionElement, "value");
                Locale locale = LocalizedString.getLocale(conditionElement);
                sDataType.setPattern(value, locale);
                return true;
            }
        } else if (dataType instanceof BinaryDataType) { // not really a condition yet.
            BinaryDataType sDataType = (BinaryDataType) dataType;
            if ("pattern".equals(localName)) {
                String value = DataTypeXml.getAttribute(conditionElement, "value");
                sDataType.setValidMimeTypes(java.util.regex.Pattern.compile(value));
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
                Comparable value = (Comparable) dDataType.cast(DataTypeXml.getValue(conditionElement), null, null);
                dDataType.setMin(value, "minInclusive".equals(localName));
                setRestrictionData(dDataType.getMinRestriction(), conditionElement);
                return true;
            } else if ("maxExclusive".equals(localName) || "maxInclusive".equals(localName)) {
                Comparable value = (Comparable) dDataType.cast(DataTypeXml.getValue(conditionElement), null, null);
                dDataType.setMax(value, "maxInclusive".equals(localName));
                setRestrictionData(dDataType.getMaxRestriction(), conditionElement);
                return true;
            }
        }
        return false;
    }


    public String toString() {
        return "definition(" + (dataType == null ? "NONE" : dataType.toString()) + ")";
    }

}
