/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.io.File;
import java.util.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.Element;
/**
 * This class reads configuration files for utilities, that are
 * placed in /config/utils/.
 * @since MMBase-1.6.4
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 * @version $Id: UtilReader.java,v 1.7 2004-02-09 13:50:36 pierre Exp $
 */
public class UtilReader {

    private static Logger log = Logging.getLoggerInstance(UtilReader.class);

    public static final String CONFIG_UTILS = "utils";

    /** Public ID of the Utilities config DTD version 1.0 */
    public static final String PUBLIC_ID_UTIL_1_0 = "-//MMBase//DTD util config 1.0//EN";
    /** DTD resource filename of the Utilities config DTD version 1.0 */
    public static final String DTD_UTIL_1_0 = "util_1_0.dtd";

    /** Public ID of the most recent Utilities config DTD */
    public static final String PUBLIC_ID_UTIL = PUBLIC_ID_UTIL_1_0;
    /** DTD respource filename of the most recent Utilities config DTD */
    public static final String DTD_UTIL = DTD_UTIL_1_0;

    /**
     * Register the Public Ids for DTDs used by UtilReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_UTIL_1_0, DTD_UTIL_1_0, UtilReader.class);
    }

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
        } else {
            log.warn(file.getName() + " does not exist:" + Logging.stackTrace());
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
                Enumeration enumeration = reader.getChildElements(e, "property");
                Element p;
                String name, value;
                while (enumeration.hasMoreElements()) {
                    p = (Element)enumeration.nextElement();
                    name = reader.getElementAttributeValue(p, "name");
                    value = reader.getElementValue(p);
                    properties.put(name, value);
                }
            }
        } else {
            log.warn("File " + f + " does not exist");
        }
    }

}