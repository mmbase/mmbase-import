/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;

import org.xml.sax.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;

/**
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $ Id: $
 */
public class XMLEntityResolver implements EntityResolver {

    private static Logger log = Logging.getLoggerInstance(XMLEntityResolver.class.getName());

    private static final String MMRESOURCES = "/org/mmbase/resources/";

    private String dtdpath;
    private boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

    private boolean  validate;  
    private Class    resolveBase;


    /**
     * This class is used by init of logging system.
     * After configuration of logging, logging must be reinitialized.
     */
    static void reinitLogger() {
        log = Logging.getLoggerInstance(XMLEntityResolver.class.getName());
    }


    /**
     * empty constructor
     */
    public XMLEntityResolver() {
        this(true);
    }

    public XMLEntityResolver(boolean v) {
        this(v, null);
    }

    public XMLEntityResolver(boolean v, Class base) {
        hasDTD      = false;
        dtdpath     = null;
        validate    = v;
        resolveBase = base;
    }

    /**
     * takes the systemId and returns the local location of the dtd
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        //does systemId contain a mmbase-dtd
        if ((systemId == null) || (systemId.indexOf("http://www.mmbase.org/") == -1)) {
            if (! validate) {
                return new InputSource(new StringReader(""));
            }
            // it's a systemId we can't do anything with,
            // so let the parser decide what to do
            return null;
        } else {
            hasDTD = true ;
            int i = systemId.indexOf("/dtd/");
            String dtdName = systemId.substring(i + 5);
            InputStream dtdStream = null;          

            // first, try MMBase config directory (if initialized)

            if (MMBaseContext.isInitialized()) {
                String configpath = MMBaseContext.getConfigPath();
                if (configpath != null) {                    
                    File  dtdFile = new File(configpath + File.separator + "dtd" + File.separator + dtdName);
                    if (dtdFile.canRead()) {
                        if (log.isDebugEnabled()) log.debug("dtdLocation = " + dtdFile);
                        dtdStream = new FileInputStream(dtdFile);
                        dtdpath = dtdFile.toString();
                    }
                }
            }
            
            if (dtdStream == null) {
                Class base = resolveBase;
                if (base != null) {
                    String resource = "resources/" + dtdName;
                    if (log.isDebugEnabled()) log.debug("Getting DTD as resource " + resource + " of " + resolveBase.getClass().getName());
                    dtdStream = resolveBase.getResourceAsStream(resource);
                    if (dtdStream == null) {
                        log.warn("Could not find " + resource + " in " + resolveBase.getClass().getName() + ", falling back to " + MMRESOURCES);
                        base = null; // try it in org.mmbase.resources too.
                    }
                }
               
                if (base == null) {
                    String resource = MMRESOURCES + "dtd/" + dtdName;
                    if (log.isDebugEnabled()) log.debug("Getting DTD as resource " + resource);
                    dtdStream = getClass().getResourceAsStream(resource);
                } 
            }
            if (dtdStream == null) {
                log.error("Could not find MMBase dtd '" + dtdName + "' (did you make a typo?), returning an 'empty' DTD.");
                return new InputSource(new StringReader(""));
                //return null;
            }


            InputStreamReader dtdInputStreamReader = new InputStreamReader(dtdStream);
            InputSource dtdInputSource = new InputSource();
            dtdInputSource.setCharacterStream(dtdInputStreamReader);      
            return dtdInputSource;
        }
    }

    /**
     * @return whether the resolver has determiend a DTD
     */
    public boolean hasDTD() {
        return hasDTD;
    }

    /**
     * @return The actually used path to the DTD
     */
    public String getDTDPath() {
        return this.dtdpath;
    }
}
