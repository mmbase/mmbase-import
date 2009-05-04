/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.xml;

import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import org.mmbase.util.XMLEntityResolver;

/**
 * @javadoc
 * @since MMBase-1.8
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class ModuleReader extends DocumentReader {

    /** Public ID of the Module DTD version 1.0 */
    public static final String PUBLIC_ID_MODULE_1_0 = "-//MMBase//DTD module config 1.0//EN";
    private static final String PUBLIC_ID_MODULE_1_0_FAULT = "-//MMBase/DTD module config 1.0//EN";
    private static final String PUBLIC_ID_MODULE_1_0_FAULT2 = "-//MMBase/ DTD module config 1.0//EN";
    /** Public ID of the most recent Module DTD */
    public static final String PUBLIC_ID_MODULE = PUBLIC_ID_MODULE_1_0;

    /** DTD resource filename of the most recent Module DTD */
    public static final String DTD_MODULE_1_0 = "module_1_0.dtd";
    /** DTD resource filename of the most recent Module DTD */
    public static final String DTD_MODULE = DTD_MODULE_1_0;

    /**
     * Register the Public Ids for DTDs used by ModuleReader
     * This method is called by XMLEntityResolver.
     * @since MMBase-1.7
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_MODULE_1_0, DTD_MODULE_1_0, ModuleReader.class);
        // legacy public IDs (wrong, don't use these)
        XMLEntityResolver.registerPublicID(PUBLIC_ID_MODULE_1_0_FAULT, DTD_MODULE_1_0, ModuleReader.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_MODULE_1_0_FAULT2, DTD_MODULE_1_0, ModuleReader.class);
    }

    public ModuleReader(InputSource is) {
        super(is, ModuleReader.class);
    }

    /**
     * @since MMBase-1.8
     */
    public ModuleReader(Document doc) {
        super(doc);
    }

    /**
     * Get the status of this module
     */
    public String getStatus() {
        Element e = getElementByPath("module.status");        
        String s =  getElementValue(e);
        return s.equals("") ? "active" : s;
    }

    /**
     * Get the version of this module
     */
    public int getVersion() {
        Element e = getElementByPath("module");
        String version = getElementAttributeValue(e, "version");
        int n = 0;
        if (version == null) {
            return n;
        } else {
            try {
                n = Integer.parseInt(version);
            } catch (Exception f) {
                n = 0;
            }
            return n;
        }
    }

    /**
     * Get the maintainer of this module
     */
    public String getMaintainer() {
        Element e = getElementByPath("module");
        String tmp = getElementAttributeValue(e, "maintainer");
        if (tmp != null && !tmp.equals("")) {
            return tmp;
        } else {
            return "mmbase.org";
        }
    }

    /**
     * The name of the class which is implementing this Module.
     */
    public String getClassName() {
        Element e = getElementByPath("module.class");
        if (e != null) return getElementValue(e);
        // legacy fall back
        e = getElementByPath("module.classfile");
        return getElementValue(e);

    }

    /**
     * get the optional resource url for the module
     * @return the url of the resource or null if no url was defined
     **/
    public String getURLString(){
        Element e = getElementByPath("module.url");
        if (e != null){
            return getElementValue(e);
        }
        return null;
    }

    /**
     * Get the properties of this builder
     */
    public Map getProperties() {
        Map map = new LinkedHashMap();
        Element e = getElementByPath("module.properties");
        if (e != null) {
            for (Iterator iter = getChildElements(e, "property"); iter.hasNext();) {
                Element p = (Element) iter.next();
                String name = getElementAttributeValue(p, "name");
                String value = getElementValue(p);
                map.put(name, value);
            }
        }
        return map;
    }

}
