/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.*;

import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.transformers.CharTransformer;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ValueIntercepter.java,v 1.1 2003-12-09 22:26:17 michiel Exp $
 * @since MMBase-1.7
 */

public class ValueIntercepter {

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
        null,                                                         /* 0=object (does not exist as field type, so this row is empty) */ 
        {null, null, null, null, null, null, null, null, null, null}, /* 1=string */
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null}
    };
    private static final Map[][] setProcessor = { // maps with guiType -> Processor
        null,                                                         /* 0 not used */
        {null, null, null, null, null, null, null, null, null, null}, /* 1= string */
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null}
    };

    static {
        log = Logging.getLoggerInstance(ValueIntercepter.class);
        // read the XML
        readFieldTypes();
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
                    CharTransformer charTransformer = null;
                    // todo
                    newProcessor = new CharTransformerProcessor(charTransformer);
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
    private static void readFieldTypes() {

        Class thisClass = ValueIntercepter.class;
        InputSource fieldtypes = new InputSource(thisClass.getResourceAsStream("resources/fieldtypes.xml"));
        XMLBasicReader reader  = new XMLBasicReader(fieldtypes, thisClass);

        Element fieldtypesElement = reader.getElementByPath("fieldtypes");
        Enumeration e = reader.getChildElements(fieldtypesElement, "fieldtype");
        while (e.hasMoreElements()) {
            Element typeElement = (Element) e.nextElement();
            String typeString = typeElement.getAttribute("id");
            int fieldType =  org.mmbase.module.corebuilders.FieldDefs.getDBTypeId(typeString);

            // fill the set-processor for no, or unknown, guitype
            Enumeration f = reader.getChildElements(typeElement, "setprocessor");
            while (f.hasMoreElements()) {
                Element setProcessorElement = (Element) f.nextElement();
                String setTypeString = setProcessorElement.getAttribute("type");
                if (setTypeString.equals("")) {
                    defaultSetProcessor[fieldType][0] = createProcessor(reader, setProcessorElement);
                } else {
                    int setFieldType =  org.mmbase.module.corebuilders.FieldDefs.getDBTypeId(setTypeString);
                    defaultSetProcessor[fieldType][setFieldType] = createProcessor(reader, setProcessorElement);
                }
            }

            // now for known guitypes (specializations)
            Enumeration g = reader.getChildElements(typeElement, "specialization");
            while (g.hasMoreElements()) {
                Element specializationElement = (Element) g.nextElement();
                String guiType = specializationElement.getAttribute("id");
                // fill the set-processor for this guitype
                Enumeration h = reader.getChildElements(typeElement, "setprocessor");
                while (h.hasMoreElements()) {
                    Element setProcessorElement = (Element) h.nextElement();
                    String setTypeString = setProcessorElement.getAttribute("type");
                    Map map;
                    if (setTypeString.equals("")) {
                        map = setProcessor[fieldType][0];
                    } else {
                        int setFieldType =  org.mmbase.module.corebuilders.FieldDefs.getDBTypeId(setTypeString);
                        map = setProcessor[fieldType][setFieldType];
                    }
                    map.put(guiType, createProcessor(reader, setProcessorElement));
                }
            }
        }
    }


    public static final Object processSet(final int setType, final Node node, final Field field, final  Object value) {
        int type = field.getType();

        Processor processor;
        Map map = setProcessor[type][setType];
        if (map == null) {
            processor = defaultSetProcessor[type][setType];
        } else {
            String guiType = field.getGUIType();
            processor = (Processor) map.get(guiType);
            if (processor == null) {
                processor = defaultSetProcessor[type][setType];
            }
        }
        if (processor == null) return value;
        return processor.process(node, value);
        
    }


}
