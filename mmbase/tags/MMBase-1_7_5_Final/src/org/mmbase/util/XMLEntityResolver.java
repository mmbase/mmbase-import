/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Hashtable;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.xml.BuilderReader;
import org.mmbase.util.xml.DatabaseReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @todo remove manual Transactionhandler Public ID registration
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id: XMLEntityResolver.java,v 1.37 2003-07-21 12:19:06 pierre Exp $
 */
public class XMLEntityResolver implements EntityResolver {

    private static Logger log = Logging.getLoggerInstance(XMLEntityResolver.class);

    private static final String MMRESOURCES = "/org/mmbase/resources/";

    private static Map publicIDtoResource = new Hashtable();
    // This maps public id's to classes which are know to be able to parse this XML's.
    // The package of these XML's will also contain the resources with the DTD.


    /**
     * Container for dtd resources information
     */
    static class Resource {
        private Class clazz;
        private String file;
        Resource(Class c, String f) {
            clazz = c; file = f;
        }

        String getResource() {
            return "resources/" + file;
        }
        String getFileName() {
            return file;
        }
        InputStream getAsStream() {
            if (log.isDebugEnabled()) log.debug("Getting DTD as resource " + getResource() + " of " + clazz.getName());
            return clazz.getResourceAsStream(getResource());
        }

    }

    static {
        // ask known (core) xml readers to register their public ids and dtds
        XMLBasicReader.registerPublicIDs();
        BuilderReader.registerPublicIDs();
        XMLApplicationReader.registerPublicIDs();
        DatabaseReader.registerPublicIDs();
        XMLModuleReader.registerPublicIDs();
        org.mmbase.util.xml.UtilReader.registerPublicIDs();
        org.mmbase.security.MMBaseCopConfig.registerPublicIDs();
        // Manually register transaction dtd
        // This has to be placed in registerPublicIDs() in the TransactionHandler class, but which one?
        // There are two:
        // - org.mmbase.module.TransactionHandler
        // - org.mmbase.applications.xmlimporter.TransactionHandler
        // If the latter, TransactionHandler is an application and should not be called from here.
        registerPublicID("-//MMBase//DTD builder transactions 1.0//EN", "transactions_1_0.dtd", org.mmbase.module.TransactionHandler.class);
    }

    /**
     * Register a given publicID, binding it to a resource determined by a given class and resource filename
     * @param publicID the Public ID to register
     * @param dtd the name of the resourcefile
     * @param c the class indicating the location of the resource in the pacakage structure. The
     *          resource is to be found in the 'resources' package under the package of the class.
     */
    public static void registerPublicID(String publicID, String dtd, Class c) {
        publicIDtoResource.put(publicID, new Resource(c,dtd));
        if (log.isDebugEnabled()) log.debug("publicIDtoResource: " + publicID + " " + dtd + c.getName());
    }

    private String dtdpath;

    private boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

    private boolean  validate;
    private Class    resolveBase;



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

    private InputStream getFromConfigDir(String fileName) throws IOException {
        if (MMBaseContext.isInitialized()) {
            String configpath = MMBaseContext.getConfigPath();
            if (configpath != null) {
                File  dtdFile = new File(configpath + File.separator + "dtd" + File.separator + fileName);
                if (dtdFile.canRead()) {
                    if (log.isDebugEnabled()) log.debug("dtdLocation = " + dtdFile);
                    InputStream dtdStream = new FileInputStream(dtdFile);
                    dtdpath = dtdFile.toString();
                    return dtdStream;
                }
            }
        }
        return null;
    }

    /**
     * takes the systemId and returns the local location of the dtd
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        if (log.isDebugEnabled()) {
            log.debug("resolving PUBLIC " + publicId + " SYSTEM " + systemId);
        }

        InputStream dtdStream = null;
        // first try with publicID
        if (publicId != null) {
            Resource res = (Resource) publicIDtoResource.get(publicId);
            if (res != null) {
                dtdStream = getFromConfigDir(res.getFileName());
                if (dtdStream == null) dtdStream = res.getAsStream();
            }
        }

        if (dtdStream == null) { // not succeeded with publicid, go trying with systemId
            //does systemId contain a mmbase-dtd
            if ((systemId == null) || (systemId.indexOf("http://www.mmbase.org/") == -1)) {
                if (! validate) {
                    return new InputSource(new StringReader(""));
                }
                // it's a systemId we can't do anything with,
                // so let the parser decide what to do
                return null;
            } else {
                int i = systemId.indexOf("/dtd/");
                String dtdName = systemId.substring(i + 5);
                // first, try MMBase config directory (if initialized)
                dtdStream = getFromConfigDir(dtdName);
                if (dtdStream == null) {
                    Class base = resolveBase; // if resolveBase was specified, use that.
                    Resource res = null;
                    if (base != null) {
                        res = new Resource(base, dtdName);
                    }
                    if (res != null) {
                        dtdStream = res.getAsStream();
                        if (dtdStream == null) {
                            log.warn("Could not find " + res.getResource() + " in " + base.getName() + ", falling back to " + MMRESOURCES);
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
                    log.error("Could not find MMBase dtd '" + dtdName + "' (did you make a typo?), returning null, system id will be used (needing a connection, or put in config dir)");
                    // not sure, probably should return 'null' after all, then it will be resolved with internet.
                    // but this can not happen, in fact...
                    //return new InputSource(new StringReader(""));
                    // FAILED
                    return null;
                }
            }
        }

        hasDTD=true;
        InputStreamReader dtdInputStreamReader = new InputStreamReader(dtdStream);
        InputSource dtdInputSource = new InputSource();
        dtdInputSource.setCharacterStream(dtdInputStreamReader);
        return dtdInputSource;
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
