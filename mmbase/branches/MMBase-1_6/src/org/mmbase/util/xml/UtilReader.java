/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util.xml;

import org.w3c.dom.Element;
import java.util.*;
import java.io.File;
import org.mmbase.module.core.MMBaseContext;

/**
 * This class reads configuration files for utilities, that are 
 * placed in /config/utils/.
 *
 */
public class UtilReader extends org.mmbase.util.XMLBasicReader {

    public static final String CONFIG_UTILS = "utils";
    /**
     * @param filename The name of the property file (e.g. httppost.xml).
     */
    public UtilReader(String filename) {
        super(MMBaseContext.getConfigPath() + File.separator + CONFIG_UTILS + File.separator + filename, 
              UtilReader.class);
    }

    /**
     * Get the properties of this utility.
     */
    public Map getProperties() {
        Map map = new HashMap();
        Element e = getElementByPath("util.properties");
	if (e != null) {
	    Enumeration enum = getChildElements(e, "property");
	    Element p;
	    String name, value;
	    while (enum.hasMoreElements()) {
		p = (Element)enum.nextElement();
		name = getElementAttributeValue(p, "name");
		value = getElementValue(p);
		map.put(name,value);
	    }
	}
        return Collections.unmodifiableMap(map);
    }
}
