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
 * @version $Id: UtilReader.java,v 1.9 2004-07-13 12:05:51 michiel Exp $
 */
public class UtilReader {

    private static final Logger log = Logging.getLoggerInstance(UtilReader.class);

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
        private WrappedFileWatcher wrappedFileWatcher;
        public UtilFileWatcher(WrappedFileWatcher f) {
            super(true); // true: keep reading.
            wrappedFileWatcher = f;
        }

        public void onChange(File file) {
            readProperties(file);
            if (wrappedFileWatcher != null) {
                wrappedFileWatcher.onChange(file);
            }
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
        watcher = new UtilFileWatcher(null);
        watcher.add(file);
        watcher.start();
    }
    /**
     * @since MMBase-1.8
     * @param w A unstarted WrappedFileWatcher without files. (It will be only be called from the filewatcher in this reader).
     */
    public UtilReader(String filename, WrappedFileWatcher w) {
        File file = new File(MMBaseContext.getConfigPath() + File.separator + CONFIG_UTILS + File.separator + filename);
        readProperties(file);
        watcher = new UtilFileWatcher(w);
        watcher.add(file);
        watcher.start();

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
                while (enumeration.hasMoreElements()) {
                    Element p = (Element)enumeration.nextElement();
                    String name = reader.getElementAttributeValue(p, "name");
                    String type = reader.getElementAttributeValue(p, "type");
                    if (type.equals("map")) {
                        Enumeration entries = reader.getChildElements(p, "entry");
                        Map map = new LinkedHashMap();
                        while(entries.hasMoreElements()) {
                            Element entry = (Element) entries.nextElement();
                            Enumeration en = reader.getChildElements(entry, "*");
                            String key = null;
                            String value = null;
                            while(en.hasMoreElements()) {
                                Element keyorvalue = (Element) en.nextElement();
                                if (keyorvalue.getTagName().equals("key")) {
                                    key = reader.getElementValue(keyorvalue);
                                } else {
                                    value = reader.getElementValue(keyorvalue);
                                }
                            }
                            if (key != null && value != null) {
                                map.put(key, value);                            
                            }
                        }
                        properties.put(name, map);
                    } else {
                        String value = reader.getElementValue(p);
                        properties.put(name, value);
                    }
                }
            }
        } else {
            log.debug("File " + f + " does not exist");
        }
    }

}
