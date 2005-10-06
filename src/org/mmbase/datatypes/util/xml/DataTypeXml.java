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
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;

/**
 * Static methods used for parsing of datatypes.xml
 *
 * @author Michiel Meeuwissen
 * @version $Id: DataTypeXml.java,v 1.1 2005-10-06 23:02:03 michiel Exp $
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
     * This utility takes care of reading the xml:lang attribute from an element
     */
    public static Locale getLocale(Element element) {
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


    public static LocalizedString getLocalizedDescription(String tagName, Element element, LocalizedString descriptions) {
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



    public static boolean getBooleanValue(Element element, boolean defaultValue) {
        if (hasAttribute(element, "value")) {
            return Boolean.valueOf(getAttribute(element, "value")).booleanValue();
        } else {
            return defaultValue;
        }
    }

    public static Processor chainProcessors(Processor processor1, Processor processor2) {
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


    private static Object getParameterValue(Element param) {
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

