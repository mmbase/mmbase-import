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
import org.mmbase.util.FileWatcher;
import org.mmbase.util.XMLBasicReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * This class reads configuration files for utilities, that are 
 * placed in /config/utils/.
 *
 */
public class UtilReader {

    private static Logger log = Logging.getLoggerInstance(UtilReader.class.getName());
    public static final String CONFIG_UTILS = "utils";

    private class UtilFileWatcher extends FileWatcher {

        public UtilFileWatcher() {
            super(true); // true: keep reading.
        }
        
        public void onChange(File file) {
            readProperties(file);
        }
    }


    private Map properties;
    private FileWatcher watcher;

    /**
     * @param filename The name of the property file (e.g. httppost.xml).
     */
    public UtilReader(String filename) {        
        File file = new File(MMBaseContext.getConfigPath() + File.separator + CONFIG_UTILS + File.separator + filename);
        readProperties(file);
        if (file.exists()) {
            watcher = new UtilFileWatcher();
            watcher.add(file);
            watcher.start();
        } 
    }

    /**
     * Get the properties of this utility.
     */
    public Map getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    protected void readProperties(File f) {
        if (properties == null) {
            properties = new HashMap();
        } else {
            properties.clear();
        }
        if (f.exists()) {
            XMLBasicReader reader = new XMLBasicReader(f.toString(), UtilReader.class);
            Element e = reader.getElementByPath("util.properties");
            if (e != null) {
                Enumeration enum = reader.getChildElements(e, "property");
                Element p;
                String name, value;
                while (enum.hasMoreElements()) {
                    p = (Element)enum.nextElement();
                    name = reader.getElementAttributeValue(p, "name");
                    value = reader.getElementValue(p);
                    properties.put(name,value);
                }
            }
        } else {
            log.error("File " + f + " does not exist");
        }
    }
}
