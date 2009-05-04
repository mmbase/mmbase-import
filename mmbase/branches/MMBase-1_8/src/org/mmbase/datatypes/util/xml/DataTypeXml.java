/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;
import org.mmbase.datatypes.processors.*;
import java.util.*;
import org.w3c.dom.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.*;
import org.mmbase.core.util.Fields;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.BeanFunction;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;

/**
 * Static methods used for parsing of datatypes.xml
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 **/
public abstract class DataTypeXml {

    private static final Logger log = Logging.getLoggerInstance(DataTypeXml.class);

    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    public static boolean hasAttribute(Element element, String localName) {
        return DocumentReader.hasAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * default namespace
     */
    protected static String getAttribute(Element element, String localName) {
        return DocumentReader.getAttribute(element, DataTypeReader.NAMESPACE_DATATYPES, localName);
    }


    /**
     * Reads a number of tags with 'xml:lang' attributes.
     *
     * @param tagName Wich tags to read. The bodies are the values.
     * @param element From which element this tags must be childs.
     * @param descriptions Existing LocalizedString instance or <code>null</code> if a new one must be created.
     * @param defaultKey   If the localized string was created with some silly automatic key, it can be provided here, in
     *                     which case it will be changed if a tag withouth xml:lang is found, or with xml:lang equals the current default.
     *                     It can also be <code>null</code>
     * @return A new LocalizedString or the updated 'descriptions' parameter if that was not <code>null</code>
     */

    public static LocalizedString getLocalizedDescription(final String tagName, final Element element, LocalizedString descriptions, final String defaultKey) {
        if (descriptions == null) descriptions = new LocalizedString(null);
        descriptions.fillFromXml(tagName, element);
        if (defaultKey != null &&  descriptions.getKey().equals(defaultKey)) {
            descriptions.setKey(descriptions.get(LocalizedString.getDefault()));
        }
        return descriptions;
    }



    public static boolean getBooleanValue(Element element, boolean defaultValue) {
        if (hasAttribute(element, "value")) {
            return Boolean.valueOf(getAttribute(element, "value")).booleanValue();
        } else {
            return defaultValue;
        }
    }

    public static Processor chainProcessors(Processor processor1, Processor processor2) {
        Processor processor = processor1;
        if (processor == null || processor instanceof CopyProcessor) {
            processor = processor2;
        } else if (processor2 instanceof CopyProcessor) {
            processor = processor1;
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

    public static CommitProcessor chainProcessors(CommitProcessor processor1, CommitProcessor processor2) {
        CommitProcessor processor = processor1;
        if (processor == null || processor instanceof EmptyCommitProcessor) {
            processor = processor2;
        } else if (processor2 instanceof EmptyCommitProcessor) {
            processor = processor1;
        } else if (processor instanceof ChainedCommitProcessor) {
            ((ChainedCommitProcessor) processor).add(processor2);
        } else {
            ChainedCommitProcessor chain = new ChainedCommitProcessor();
            chain.add(processor1);
            chain.add(processor2);
            processor = chain;
        }
        return processor;
    }


    private static Object getParameterValue(Element param) {
        String stringValue = param.getAttribute("value");
        if (stringValue == null || "".equals(stringValue)) {
            stringValue = DocumentReader.getNodeTextValue(param, false);
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
                if (stringValue != null && ! stringValue.trim().equals("")) {
                    log.warn("" + param + " has both a text value and sub parameters, ignoring the text value '" + stringValue + "'");
                }
                return subParams;
            } else {
                return stringValue;
            }
        } else {
            NodeList childNodes = param.getChildNodes();
            if (childNodes.getLength() > 0) {
                log.warn("Using value attribute together with child nodes on " + param);
            }
            return stringValue;
        }
    }

    private static void fillParameters(Element paramContainer, Parameters params) {
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

    /**
     * @since MMBase-1.8.5
     */
    private static String fillBeanParameters(Element paramContainer, Object bean) {
        try {
            Parameters params = null;
            BeanFunction function = null;
            NodeList childNodes = paramContainer.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    Element paramElement = (Element) childNodes.item(i);
                    if ("param".equals(paramElement.getLocalName())) {
                        String name = paramElement.getAttribute("name");
                        Object value = getParameterValue(paramElement);
                        if (params == null) {
                            function = new BeanFunction(bean, "toString"); // any object has 'toString'.
                            params = function.createParameters();
                            params.setAutoCasting(true);
                        }
                        params.set(name, value);
                    }
                }
            }
            if (params != null) {
                Object res = function.getFunctionValue(params); // calling the function actually calls setters
                return "" + res;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Processor createProcessor(Element processorElement) {
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
                        Processor newProcessor;
                        if (CharTransformer.class.isAssignableFrom(claz)) {
                            CharTransformer charTransformer = Transformers.getCharTransformer(clazString, null, " valueintercepter ", false);
                            if (charTransformer != null) {
                                newProcessor = new CharTransformerProcessor(charTransformer);
                                fillBeanParameters(classElement, newProcessor);
                            } else {
                                continue;
                            }
                        } else if (Processor.class.isAssignableFrom(claz)) {
                            newProcessor = (Processor)claz.newInstance();
                            fillBeanParameters(classElement, newProcessor);
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
                            continue;
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
        return processor == null ? CopyProcessor.getInstance() : processor;
    }
    public static CommitProcessor createCommitProcessor(Element processorElement) {
        CommitProcessor processor = null;
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
                        CommitProcessor newProcessor;
                        if (CommitProcessor.class.isAssignableFrom(claz)) {
                            newProcessor = (CommitProcessor)claz.newInstance();
                            fillBeanParameters(classElement, newProcessor);
                        } else if (ParameterizedCommitProcessorFactory.class.isAssignableFrom(claz)) {
                            ParameterizedCommitProcessorFactory factory = (ParameterizedCommitProcessorFactory) claz.newInstance();
                            Parameters params = factory.createParameters();
                            fillParameters(classElement, params);
                            newProcessor = factory.createProcessor(params);
                        } else {
                            log.error("Found class " + clazString + " is not a CommitProcessor or a factory for that.");
                            continue;
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
        return processor == null ? EmptyCommitProcessor.getInstance() : processor;
    }


    public static String getValue(Element element) {
        if (hasAttribute(element, "value")) {
            return getAttribute(element, "value");
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }

    public static long getLongValue(Element element) {
        if (hasAttribute(element, "value")) {
            return Long.parseLong(getAttribute(element, "value"));
        } else {
            throw new IllegalArgumentException("no 'value' argument");
        }
    }
}

