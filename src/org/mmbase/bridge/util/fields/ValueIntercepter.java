/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.*;

import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.transformers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.XMLEntityResolver;

import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ValueIntercepter.java,v 1.8 2004-02-26 22:09:28 michiel Exp $
 * @since MMBase-1.7
 */

public class ValueIntercepter {

    public static final String PUBLIC_ID_FIELD_TYPE_DEFINITIONS_0_1 = "-//MMBase//DTD fieldtypedefinitions 0.1//EN";
    public static final String DTD_FIELD_TYPE_DEFINITIONS_0_1 = "fieldtypedefinitions_0_1.dtd";

    public static final String PUBLIC_ID_FIELD_TYPE_DEFINITIONS = PUBLIC_ID_FIELD_TYPE_DEFINITIONS_0_1;
    public static final String DTD_FIELD_TYPE_DEFINITIONS = DTD_FIELD_TYPE_DEFINITIONS_0_1;

    public static final String XML_FIELD_TYPE_DEFINITIONS = "resources/fieldtypedefinitions.xml";

    private static Logger log;

    /*
      for reference by humans only:
    private static final int NUMBER_OF_FIELDTYPES = 8;

    public final static int TYPE_STRING  = 1;
    public final static int TYPE_INTEGER = 2;
    public final static int TYPE_BYTE    = 4;
    public final static int TYPE_FLOAT   = 5;
    public final static int TYPE_DOUBLE  = 6;
    public final static int TYPE_LONG    = 7;
    public final static int TYPE_XML     = 8;
    public final static int TYPE_NODE    = 9;
    */

    private static final Processor[][] defaultSetProcessor = {
        //set  setString setInteger ... 
        {null, null, null, null, null, null, null, null, null, null}, /* 0=object */
        {null, null, null, null, null, null, null, null, null, null}, /* 1=string */
        {null, null, null, null, null, null, null, null, null, null}, /* 2=integer */
        null,                                                         /* 3 not used */
        {null, null, null, null, null, null, null, null, null, null}, /* 4=byte */
        {null, null, null, null, null, null, null, null, null, null}, /* 5=float */
        {null, null, null, null, null, null, null, null, null, null}, /* 6=double */
        {null, null, null, null, null, null, null, null, null, null}, /* 7=long */
        {null, null, null, null, null, null, null, null, null, null}, /* 8=xml */
        {null, null, null, null, null, null, null, null, null, null}  /* 9=node */
    };
    private static final Map[][] setProcessor = { // maps with guiType -> Processor
        {null, null, null, null, null, null, null, null, null, null}, /* 0=object */
        {null, null, null, null, null, null, null, null, null, null}, /* 1=string */
        {null, null, null, null, null, null, null, null, null, null}, /* 2=integer */
        null,                                                         /* 3 not used */
        {null, null, null, null, null, null, null, null, null, null}, /* 4=byte */
        {null, null, null, null, null, null, null, null, null, null}, /* 5=float */
        {null, null, null, null, null, null, null, null, null, null}, /* 6=double */
        {null, null, null, null, null, null, null, null, null, null}, /* 7=long */
        {null, null, null, null, null, null, null, null, null, null}, /* 8=xml */
        {null, null, null, null, null, null, null, null, null, null}  /* 9=node */
    };

    private static final Processor[][] defaultGetProcessor = {
        //set  setString setInteger ... 
        {null, null, null, null, null, null, null, null, null, null}, /* 0=object */
        {null, null, null, null, null, null, null, null, null, null}, /* 1=string */
        {null, null, null, null, null, null, null, null, null, null}, /* 2=integer */
        null,                                                         /* 3 not used */
        {null, null, null, null, null, null, null, null, null, null}, /* 4=byte */
        {null, null, null, null, null, null, null, null, null, null}, /* 5=float */
        {null, null, null, null, null, null, null, null, null, null}, /* 6=double */
        {null, null, null, null, null, null, null, null, null, null}, /* 7=long */
        {null, null, null, null, null, null, null, null, null, null}, /* 8=xml */
        {null, null, null, null, null, null, null, null, null, null}  /* 9=node */
    };
    private static final Map[][] getProcessor = { // maps with guiType -> Processor
        {null, null, null, null, null, null, null, null, null, null}, /* 0=object */
        {null, null, null, null, null, null, null, null, null, null}, /* 1=string */
        {null, null, null, null, null, null, null, null, null, null}, /* 2=integer */
        null,                                                         /* 3 not used */
        {null, null, null, null, null, null, null, null, null, null}, /* 4=byte */
        {null, null, null, null, null, null, null, null, null, null}, /* 5=float */
        {null, null, null, null, null, null, null, null, null, null}, /* 6=double */
        {null, null, null, null, null, null, null, null, null, null}, /* 7=long */
        {null, null, null, null, null, null, null, null, null, null}, /* 8=xml */
        {null, null, null, null, null, null, null, null, null, null}  /* 9=node */
    };

    static {
        log = Logging.getLoggerInstance(ValueIntercepter.class);
        // read the XML
        try {
            readFieldTypeDefinitions();
        } catch (Throwable t) {
            log.error(t.getClass().getName() + ": " + Logging.stackTrace(t));
        }
    }

    protected static int getType(final String s) {
        if (s.equals("object")) return 0;
        return org.mmbase.module.corebuilders.FieldDefs.getDBTypeId(s);
    }

    private static Processor createProcessor(XMLBasicReader reader, Element processorElement) {
        Processor processor = null;
        Enumeration classes = reader.getChildElements(processorElement, "class");
        while(classes.hasMoreElements()) {
            Element clazElement = (Element) classes.nextElement();
            String clazString = reader.getElementValue(reader.getElementByPath(clazElement, "class"));
            try {
                Class claz = Class.forName(clazString);
                Processor newProcessor = null;
                if (CharTransformer.class.isAssignableFrom(claz)) {
                    CharTransformer charTransformer = Transformers.getCharTransformer(clazString, null, " valueinterceper ", false);
                    if (charTransformer != null) {
                        newProcessor = new CharTransformerProcessor(charTransformer);
                    }
                } else if (Processor.class.isAssignableFrom(claz)) {
                    newProcessor = (Processor) claz.newInstance();
                } else {
                    log.error("Found class " + clazString + " is not a Processor or a CharTransformer");
                }

                if (newProcessor != null) {
                    if (processor == null) {
                        processor = newProcessor;
                    } else if (processor instanceof ChainedProcessor) {
                        ((ChainedProcessor) processor).add(newProcessor);
                    } else {
                        ChainedProcessor chain = new ChainedProcessor();
                        chain.add(processor);
                        chain.add(newProcessor);
                        processor = chain;
                    }
                }
            } catch (ClassNotFoundException ex) {
                log.error("Class " + clazString + " could not be found");
            } catch (IllegalAccessException iae) {
                log.error("Class " + clazString + " may  not be instantiated");
            } catch (InstantiationException ie) {
                log.error("Class " + clazString + " can not be instantiated");
            }
        }
        return processor;
    }

    /**
     * Initialize the type handlers default supported by the system.
     */
    private static void readFieldTypeDefinitions() {
        Class thisClass = ValueIntercepter.class;
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FIELD_TYPE_DEFINITIONS, DTD_FIELD_TYPE_DEFINITIONS, thisClass);
        log.service("Reading fieldtype-definitions");
        InputSource fieldTypes = new InputSource(thisClass.getResourceAsStream(XML_FIELD_TYPE_DEFINITIONS));
        if (fieldTypes.getSystemId() == null) {
            fieldTypes.setSystemId("resource:" + thisClass.getPackage().getName() + "/" + XML_FIELD_TYPE_DEFINITIONS); // I've honousley no idea what it should be, but this is at least fit for humans (in case of errors)
        }
        XMLBasicReader reader  = new XMLBasicReader(fieldTypes, thisClass);

        Element fieldtypesElement = reader.getElementByPath("fieldtypedefinitions");
        Enumeration e = reader.getChildElements(fieldtypesElement, "fieldtype");
        while (e.hasMoreElements()) {
            Element typeElement = (Element) e.nextElement();
            String typeString = typeElement.getAttribute("id");
            int fieldType =  getType(typeString);

            // fill the set-processor for no, or unknown, guitype
            Enumeration f = reader.getChildElements(typeElement, "setprocessor");
            while (f.hasMoreElements()) {
                Element setProcessorElement = (Element) f.nextElement();
                String setTypeString = setProcessorElement.getAttribute("type");
                Processor newProcessor = createProcessor(reader, setProcessorElement);
                if (setTypeString.equals("")) {
                    defaultSetProcessor[fieldType][0] = newProcessor;
                } else {
                    int setFieldType =  getType(setTypeString);
                    defaultSetProcessor[fieldType][setFieldType] = newProcessor;
                }
                log.service("Defined for field type " + typeString + "/DEFAULT setprocessor (" + setTypeString + ")" + newProcessor);
            }

            f = reader.getChildElements(typeElement, "getprocessor");
            while (f.hasMoreElements()) {
                Element getProcessorElement = (Element) f.nextElement();
                String getTypeString = getProcessorElement.getAttribute("type");
                Processor newProcessor = createProcessor(reader, getProcessorElement);
                if (getTypeString.equals("")) {
                    defaultGetProcessor[fieldType][0] = newProcessor;
                } else {
                    int getFieldType = getType(getTypeString);
                    defaultGetProcessor[fieldType][getFieldType] = newProcessor;
                }
                log.service("Defined for field type " + typeString + "/DEFAULT getprocessor (" + getTypeString + ")" + newProcessor);
            }

            // now for known guitypes (specializations)
            Enumeration g = reader.getChildElements(typeElement, "specialization");
            while (g.hasMoreElements()) {
                Element specializationElement = (Element) g.nextElement();
                String guiType = specializationElement.getAttribute("id");
                // fill the set-processor for this guitype
                Enumeration h = reader.getChildElements(specializationElement, "setprocessor");
                while (h.hasMoreElements()) {
                    Element setProcessorElement = (Element) h.nextElement();
                    String setTypeString = setProcessorElement.getAttribute("type");
                    Map map;
                    if (setTypeString.equals("")) {
                        map = setProcessor[fieldType][0];
                    } else {
                        int setFieldType = getType(setTypeString);
                        map = setProcessor[fieldType][setFieldType];
                    }

                    if (map == null) {
                        map = new HashMap();
                        if (setTypeString.equals("")) {
                            setProcessor[fieldType][0] = map;
                        } else {
                            int setFieldType =  getType(setTypeString);
                            setProcessor[fieldType][setFieldType] = map;
                        }
                    }
                    Processor newProcessor = createProcessor(reader, setProcessorElement);
                    map.put(guiType, newProcessor);
                    log.service("Defined for field type " + typeString + "/" + guiType + " setprocessor(" + ("".equals(setTypeString) ? "ALL TYPES" : setTypeString) + ") " + newProcessor);
                }

                h = reader.getChildElements(specializationElement, "getprocessor");
                while (h.hasMoreElements()) {
                    Element getProcessorElement = (Element) h.nextElement();
                    String getTypeString = getProcessorElement.getAttribute("type");
                    Map map;
                    if (getTypeString.equals("")) {
                        map = getProcessor[fieldType][0];
                    } else {
                        int getFieldType =  getType(getTypeString);
                        map = getProcessor[fieldType][getFieldType];
                    }

                    if (map == null) {
                        map = new HashMap();
                        if (getTypeString.equals("")) {
                            getProcessor[fieldType][0] = map;
                        } else {
                            int getFieldType =  getType(getTypeString);
                            getProcessor[fieldType][getFieldType] = map;
                        }
                    }
                    Processor newProcessor = createProcessor(reader, getProcessorElement);
                    map.put(guiType, newProcessor);
                    log.service("Defined for field type " + typeString + "/" + guiType + " getprocessor(" + ("".equals(getTypeString) ? "ALL TYPES" : getTypeString)  + ") " + newProcessor);
                }
            }
        }
    }


    protected static final Processor getDefaultSetProcessor(final int setType, final int type) {
        Processor processor = defaultSetProcessor[type][setType];
        if (processor == null) {
            processor = defaultSetProcessor[type][0];
        }
        return processor;
        //log.info("default " + processor);
    }
    protected static final Processor getDefaultGetProcessor(final int setType, final int type) {
        Processor processor = defaultGetProcessor[type][setType];
        if (processor == null) {
            processor = defaultGetProcessor[type][0];
        }
        return processor;
        //log.info("default " + processor);
    }

    public static final Object processSet(final int setType, final Node node, final Field field, final  Object value) {

        int type = field.getType();

        if (type == Field.TYPE_UNKNOWN) {
            log.warn("TYPE UNKNOWN processSet " + setType + "/" + type + " " + field.getName() + " " + field.getGUIType() + " for node " + node.getNumber()); 
            return value;
        }

        Processor processor;
        Map map = setProcessor[type][setType];

                                                
        if (map == null) { // not configured
            map = setProcessor[type][0];
        }
        if (map == null) { // if that too not configured
            processor = getDefaultSetProcessor(setType, type);
        } else {
            String guiType = field.getGUIType();
            processor = (Processor) map.get(guiType);
            if (processor == null) {
                processor = getDefaultSetProcessor(setType, type);
            } else {
            }
        }
        if (processor == null) return value;
        return processor.process(node, field, value);
        
    }

    public static final Object processGet(final int getType, final Node node, final Field field, Object value) {

        int type = field.getType();

        if (type == Field.TYPE_UNKNOWN) {
            log.warn("TYPE UNKNOWN processGet " + getType + "/" + type + " " + field.getName() + " " + field.getGUIType() + " for node " + node.getNumber()); 
            return value;
        }

        Processor processor;
        Map map = getProcessor[type][getType];

                                                
        if (map == null) {  // not configured
            map = getProcessor[type][0];
        }
        if (map == null) { // if that too not configured
            processor = getDefaultGetProcessor(getType, type);
        } else {
            String guiType = field.getGUIType();
            processor = (Processor) map.get(guiType);
            if (processor == null) {
                processor = getDefaultGetProcessor(getType, type);
            } else {
            }
        }
        if (processor == null) return value;
        return processor.process(node, field, value);
        
    }


}
