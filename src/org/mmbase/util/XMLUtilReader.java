/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import org.w3c.dom.Element;
import java.util.Hashtable;
import java.util.Enumeration;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import java.io.File;

/**
 * This class reads configuration files for utilities, that are 
 * placed in /config/utils/.
 *
 */
public class XMLUtilReader extends XMLBasicReader {

    /**
     * @param filename The name of the property file (e.g. httppost.xml).
     */
    public XMLUtilReader(String filename) {
        super(MMBaseContext.getConfigPath()+File.separator+"utils"+File.separator+filename, XMLUtilReader.class);
    }

    /**
     * Get the properties of this builder
     */
    public Hashtable getProperties() {
        Hashtable hash=new Hashtable();
        Element e = getElementByPath("util.properties");
	if (e!=null) {
	    Enumeration enum = getChildElements(e,"property");
	    Element p;
	    String name, value;
	    while (enum.hasMoreElements()) {
		p = (Element)enum.nextElement();
		name = getElementAttributeValue(p,"name");
		value = getElementValue(p);
		hash.put(name,value);
	    }
	}
        return(hash);
    }
}
