/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Hashtable;

import org.mmbase.util.xml.ApplicationReader;
import org.mmbase.util.xml.BuilderReader;
import org.mmbase.util.xml.ModuleReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @move org.mmbase.util.xml
 * @rename EntityResolver
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class XMLEntityResolver implements EntityResolver {

    public static final String DOMAIN = "http://www.mmbase.org/";
    public static final String DTD_SUBPATH = "dtd/";
    public static final String XMLNS_SUBPATH = "xmlns/";
    private static final String XSD_SUBPATH = "xsd/"; // deprecated

    private static Logger log = Logging.getLoggerInstance(XMLEntityResolver.class);

    private static final String MMRESOURCES = "/org/mmbase/resources/";

    private static Map publicIDtoResource = new Hashtable();
    // This maps public id's to classes which are know to be able to parse this XML's.
    // The package of these XML's will also contain the resources with the DTD.

    /**
     * XSD's have only system ID
     */
    private static Map systemIDtoResource = new Hashtable();

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
            if (log.isDebugEnabled()) log.debug("Getting document definition as resource " + getResource() + " of " + clazz.getName());
            return clazz != null ? clazz.getResourceAsStream(getResource()) : null;
        }
        public String toString() {
            return file + ": " + clazz;
        }

    }

    static {
        // ask known (core) xml readers to register their public ids and dtds
        // the advantage of doing it this soon, is that the 1DTD are know as early as possible.
        org.mmbase.util.xml.DocumentReader.registerPublicIDs();
        BuilderReader.registerPublicIDs();
        BuilderReader.registerSystemIDs();
        ApplicationReader.registerPublicIDs();
        ModuleReader.registerPublicIDs();
        org.mmbase.util.xml.UtilReader.registerPublicIDs();
        org.mmbase.bridge.util.xml.query.QueryReader.registerSystemIDs();

        registerSystemID("http://www.w3.org/2001/03/xml.xsd",       "xml.xsd", null);
        registerSystemID("http://www.w3.org/2001/03/XMLSchema.dtd", "XMLSchema.dtd", null);
        registerSystemID("http://www.w3.org/2001/03/datatypes.dtd", "datatypes.dtd", null);
    }


    /**
     * Register a given publicID, binding it to a resource determined by a given class and resource filename
     * @param publicID the Public ID to register
     * @param dtd the name of the resourcefile
     * @param c the class indicating the location of the resource in the pacakage structure. The
     *          resource is to be found in the 'resources' package under the package of the class.
     * @since MMBase-1.7
     */
    public static void registerPublicID(String publicID, String dtd, Class c) {
        publicIDtoResource.put(publicID, new Resource(c, dtd));
        if (log.isDebugEnabled()) log.debug("publicIDtoResource: " + publicID + " " + dtd + c.getName());
    }

    /**
     * It seems that in XSD's you don't have public id's. So, this makes it possible to use system id.
     * @todo EXPERIMENTAL
     * @since MMBase-1.8
     */
    public static void registerSystemID(String systemID, String xsd, Class c) {
        systemIDtoResource.put(systemID, new Resource(c, xsd));
    }

    private String definitionPath;

    private boolean hasDefinition; // tells whether or not a DTD/XSD is set - if not, no validition can take place

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
        hasDefinition      = false;
        definitionPath     = null;
        validate    = v;
        resolveBase = base;
    }


    private InputStream getStream(Resource res) {
        InputStream stream = null;
       if (res != null) {
           stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(DTD_SUBPATH + res.getFileName());
           if (stream == null) {
               stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(XMLNS_SUBPATH + res.getFileName());
           }
           if (stream == null) {
               // XXX I think this was deprecated in favour in xmlns/ (all in 1.8), so perhaps this can be dropped
               stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(XSD_SUBPATH + res.getFileName());
           }
           if (stream == null) {
               stream = res.getAsStream();
           }
       }
       return stream;
    }

    /**
     * Takes the systemId and returns the local location of the dtd/xsd
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        if (log.isDebugEnabled()) {
            log.debug("resolving PUBLIC " + publicId + " SYSTEM " + systemId);
        }
        if (! validate && systemId.endsWith(".dtd")) {
            log.debug("Not validating, not need to resolve DTD,  returning empty resource");
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }

        InputStream definitionStream = null;

        // first try with publicID or namespace
        if (publicId != null) {
            Resource res = (Resource) publicIDtoResource.get(publicId);
            definitionStream = getStream(res);
        }
        log.debug("Get definition stream by public id: " + definitionStream);

        if (definitionStream == null) {
            Resource res = (Resource) systemIDtoResource.get(systemId);
            if (res != null) {
                definitionStream = getStream(res);
            }
        }

        if (definitionStream == null) { // not succeeded with publicid, go trying with systemId

            //does systemId contain a mmbase-dtd
            if ((systemId == null) || (! systemId.startsWith(DOMAIN))) {
                // it's a systemId we can't do anything with,
                // so let the parser decide what to do

                if (log.isDebugEnabled()) {
                    log.debug("Cannot resolve " + systemId + ", but needed for validation leaving to parser.");
                    log.debug("Find culprit: " + Logging.stackTrace(new Exception()));
                }
                return null;
            } else {
                log.debug("mmbase resource");
                String mmResource = systemId.substring(22);
                // first, try MMBase config directory (if initialized)
                definitionStream = ResourceLoader.getConfigurationRoot().getResourceAsStream(mmResource);
                if (definitionStream == null) {
                    Class base = resolveBase; // if resolveBase was specified, use that.
                    Resource res = null;
                    if (base != null) {
                        if (mmResource.startsWith("xmlns/")) {
                            res = new Resource(base, mmResource.substring(6));
                        } else {
                            res = new Resource(base, mmResource.substring(4));  // dtd or xsd
                        }
                    }
                    if (res != null) {
                        definitionStream = res.getAsStream();
                        if (definitionStream == null) {
                            log.warn("Could not find " + res.getResource() + " in " + base.getName() + ", falling back to " + MMRESOURCES);
                            base = null; // try it in org.mmbase.resources too.
                        }
                    }

                    if (base == null) {
                        String resource = MMRESOURCES + mmResource;
                        if (log.isDebugEnabled()) log.debug("Getting document definition as resource " + resource);
                        definitionStream = getClass().getResourceAsStream(resource);
                    }
                }
                if (definitionStream == null) {

                    if (resolveBase != null) {
                        log.error("Could not find MMBase entity '" + publicId + " " +  systemId + "' (did you make a typo?), returning null, system id will be used (needing a connection, or put in config dir)");
                    } else {
                        log.service("Could not find MMBase entity '" + publicId + " " +  systemId + "' (did you make a typo?), returning null, system id will be used (needing a connection, or put in config dir)");

                    }
                    // not sure, probably should return 'null' after all, then it will be resolved with internet.
                    // but this can not happen, in fact...
                    //return new InputSource(new StringReader(""));
                    // FAILED
                    return null;
                }
            }
        }
        hasDefinition = true;

        InputStreamReader definitionInputStreamReader = new InputStreamReader(definitionStream);
        InputSource definitionInputSource = new InputSource();
        if (systemId != null) {
            definitionInputSource.setSystemId(systemId);
        }
        if (publicId != null) {
            definitionInputSource.setPublicId(publicId);
        }
        definitionInputSource.setCharacterStream(definitionInputStreamReader);
        return definitionInputSource;
    }

    /**
     * @return whether the resolver has determiend a DTD
     */
    public boolean hasDTD() {
        return hasDefinition;
    }

    /**
     * @return The actually used path to the DTD
     */
    public String getDTDPath() {
        return definitionPath;
    }
}
