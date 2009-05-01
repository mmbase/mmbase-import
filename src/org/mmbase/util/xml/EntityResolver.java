/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.*;

import java.lang.reflect.*;

import org.mmbase.util.xml.applicationdata.ApplicationReader;
import org.mmbase.util.Casting;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.logging.*;

import org.xml.sax.InputSource;

/**
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class EntityResolver implements org.xml.sax.EntityResolver {

    public static final String DOMAIN = "http://www.mmbase.org/";
    public static final String DTD_SUBPATH = "dtd/";
    public static final String XMLNS_SUBPATH = "xmlns/";
    private static final String XSD_SUBPATH = "xsd/"; // deprecated

    private static Logger log = Logging.getLoggerInstance(EntityResolver.class);
    static {
        //log.setLevel(Level.DEBUG);
    }

    private static final String MMRESOURCES = "/org/mmbase/resources/";

    private static Map<String, Resource> publicIDtoResource = new ConcurrentHashMap<String, Resource>();
    // This maps public id's to classes which are know to be able to parse this XML's.
    // The package of these XML's will also contain the resources with the DTD.

    /**
     * XSD's have only system ID
     */
    private static Map<String, Resource> systemIDtoResource = new ConcurrentHashMap<String, Resource>();


    /**
     * Container for dtd resources information
     */
    static abstract class  Resource {
        abstract InputStream getStream();
    }
    static class StringResource extends Resource {
        private String string;
        StringResource(String s) {
            string = s;
        }
        InputStream getStream() {
            return new ByteArrayInputStream(string.getBytes());
        }
    }
    static class FileResource extends Resource {
        private final Class<?> clazz;
        private final String file;
        FileResource(Class<?> c, String f) {
            clazz = c;
            file = f;
        }

        String getResource() {
            return "resources/" + file;
        }
        String getFileName() {
            return file;
        }
        InputStream getStream() {
            InputStream stream = null;
            if (file != null) {
                stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(DTD_SUBPATH + getFileName());
                if (stream == null) {
                    stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(XMLNS_SUBPATH + getFileName());
                }
                if (stream == null) {
                    // XXX I think this was deprecated in favour in xmlns/ (all in 1.8), so perhaps this can be dropped
                    stream = ResourceLoader.getConfigurationRoot().getResourceAsStream(XSD_SUBPATH + getFileName());
                }
            }
            if (stream == null && clazz != null) {
                stream = clazz.getResourceAsStream(getResource());
            }

            return stream;
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

        //registerSystemID("http://www.oasis-open.org/docbook/xml/4.1.2/docbookx.dtd", "docbookx.dtd", null);
        //registerSystemID("http://www.oasis-open.org/docbook/xml/4.1.2/dbnotnx.mod", "dbnotnx.mod", null);
    }


    /**
     * Register a given publicID, binding it to a resource determined by a given class and resource filename
     * @param publicID the Public ID to register
     * @param dtd the name of the resourcefile
     * @param c the class indicating the location of the resource in the pacakage structure. The
     *          resource is to be found in the 'resources' package under the package of the class.
     * @since MMBase-1.7
     */
    public static void registerPublicID(String publicID, String dtd, Class<?> c) {
        publicIDtoResource.put(publicID, new FileResource(c, dtd));
        if (log.isDebugEnabled()) log.debug("publicIDtoResource: " + publicID + " " + dtd + c.getName());
    }

    /**
     * It seems that in XSD's you don't have public id's. So, this makes it possible to use system id.
     * @todo EXPERIMENTAL
     * @since MMBase-1.8
     */
    public static void registerSystemID(String systemID, String xsd, Class<?> c) {
        systemIDtoResource.put(systemID, new FileResource(c, xsd));
    }

    private final String definitionPath;

    private boolean hasDefinition; // tells whether or not a DTD/XSD is set - if not, no validition can take place

    private final boolean  validate;
    private final   Class<?>    resolveBase;



    /**
     * empty constructor
     */
    public EntityResolver() {
        this(true);
    }

    public EntityResolver(boolean v) {
        this(v, null);
    }

    public EntityResolver(boolean v, Class<?> base) {
        hasDefinition      = false;
        definitionPath     = null;
        validate    = v;
        resolveBase = base;
    }
    /**
     * @since MMBase-1.9
     */
    protected static StringBuilder camelAppend(StringBuilder sb, String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(s.substring(i));
                break;
            }
        }
        return sb;
    }
    /**
     * @since MMBase-1.9
     */
    protected static void appendEntities(StringBuilder sb, Object o, String prefix, int level, Set<Object> os) {
        os.add(o);
        org.mmbase.util.transformers.Identifier identifier = new org.mmbase.util.transformers.Identifier();
        if (o instanceof Map) {
            Set<Map.Entry<?,?>> map = ((Map) o).entrySet();
            for (Map.Entry<?,?> entry : map) {
                Object value = entry.getValue();
                if (value != null && Casting.isStringRepresentable(value.getClass()) && entry.getKey() instanceof String) {
                    sb.append("<!ENTITY ");
                    sb.append(prefix);
                    sb.append('.');
                    String k = identifier.transform((String) entry.getKey());
                    k = k.replaceAll("\\s", "");
                    sb.append(k);
                    sb.append(" \"" + org.mmbase.util.transformers.Xml.XMLAttributeEscape("" + value, '"') + "\">\n");
                }
                if (level < 3 && value != null && !os.contains(value) && ! value.getClass().getName().startsWith("java.lang")) { // recursion to acces also properties of this
                    appendEntities(sb, value, prefix + "." + entry.getKey(), level + 1, os);
                }
            }
        } else {
            for (Method m : o.getClass().getMethods()) {
                String name = m.getName();
                if (m.getParameterTypes().length == 0 &&
                    ! name.equals("getNodes")  &&
                    ! name.equals("getConnection") && // see  	 MMB-1490, we should not call
                                                      // getConnection, while we won't close it.
                    name.length() > 3 && name.startsWith("get") && Character.isUpperCase(name.charAt(3))) {
                    try {
                        Class<?> rt = m.getReturnType();
                        boolean invoked = false;
                        Object value = null;
                        if (Casting.isStringRepresentable(rt)) {
                            if (! Map.class.isAssignableFrom(rt) && ! Collection.class.isAssignableFrom(rt)) {
                                value = m.invoke(o); invoked = true;
                                sb.append("<!ENTITY ");
                                sb.append(prefix);
                                sb.append('.');
                                camelAppend(sb, name.substring(3));
                                sb.append(" \"" + org.mmbase.util.transformers.Xml.XMLAttributeEscape("" + value, '"') + "\">\n");
                            }
                        }
                        if (! rt.getName().startsWith("java.lang")) {
                            if (! invoked) value = m.invoke(o);
                            if (level < 3 && value != null && !os.contains(value)) {
                                appendEntities(sb, value, prefix + "." + camelAppend(new StringBuilder(), name.substring(3)), level + 1, os);
                            }
                        }
                    } catch (IllegalAccessException ia) {
                        log.debug(ia);
                    } catch (InvocationTargetException ite) {
                        log.debug(ite);
                    } catch (AbstractMethodError ame) {
                        log.debug(ame);
                    } catch (Exception e) {
                        log.warn("Error", e);
                    }
                }
            }
        }
    }
    protected static String ents = null;
    protected static boolean logEnts = true;
    /**
     * @since MMBase-1.9
     */
    protected static synchronized String getMMEntities() {
        if (ents == null) {
            StringBuilder sb = new StringBuilder();
            try {
                Set<Object> added = new HashSet<Object>();
                appendEntities(sb, org.mmbase.framework.Framework.getInstance(), "framework", 0, added);
                appendEntities(sb, org.mmbase.framework.ComponentRepository.getInstance(), "componentRepository", 0, added);

                org.mmbase.module.Module  mmbase = org.mmbase.module.Module.getModule("mmbaseroot", false);
                if (mmbase != null) {
                    appendEntities(sb, mmbase, "mmbase", 0, added);
                }
            } catch (Throwable ie) {
                log.warn(ie.getMessage());
                return sb.toString();
            }
            ents = sb.toString();
            if (logEnts) {
                log.debug("Using entities\n" + ents);
            }
        }
        return ents;
    }
    /**
     * @since MMBase-1.9
     */
    public static void clearMMEntities(boolean le) {
        ents = null;
        logEnts = le;
    }

    /**
     * Takes the systemId and returns the local location of the dtd/xsd
     */
    public InputSource resolveEntity(final String publicId, final String systemId) {
        if (log.isDebugEnabled()) {
            log.debug("resolving PUBLIC " + publicId + " SYSTEM " + systemId);
        }

        InputStream definitionStream = null;

        if ("http://www.mmbase.org/mmentities.ent".equals(systemId)) {
            log.debug("Reding mmbase entities for " + systemId + " " + publicId);
            //StringBuilder sb = new StringBuilder();
            //Class c = org.mmbase.framework.Framework.class;
            String ents = getMMEntities();
            if (log.isDebugEnabled()) {
                log.debug("Using entities\n" + ents);
            }
            definitionStream = new StringResource(ents).getStream();
        } else  if (publicId != null) {
            // first try with publicID or namespace
            Resource res = publicIDtoResource.get(publicId);
            log.debug("Found publicId " + publicId + " -> " + res);
            definitionStream = res == null ? null : res.getStream();
        }


        log.debug("Get definition stream by public id: " + definitionStream);

        if (definitionStream == null) {
            Resource res = systemIDtoResource.get(systemId);
            if (res != null) {
                definitionStream = res.getStream();
            }
            log.debug("Get definition stream by registered system id: " + systemId + " " + definitionStream);
        }

        if (definitionStream == null) { // not succeeded with publicid, go trying with systemId
            log.debug("No definition stream yet");
            //does systemId contain a mmbase-dtd
            if ((systemId == null) || (! systemId.startsWith(DOMAIN))) {
                // it's a systemId we can't do anything with,
                // so let the parser decide what to do

                if (validate) {
                    log.debug("Cannot resolve " + systemId + ", but needed for validation leaving to parser.");
                    log.debug("Find culpit: ", new Exception());
                    return null;
                } else if (systemId.endsWith(".dtd")) {
                    // perhaps this should not be done if it is about resolving _entities_ rather then dtd.
                    log.debug("Not validating, no need to resolve DTD (?), returning empty resource for " + systemId);
                    return new InputSource(new ByteArrayInputStream(new byte[0]));
                } else {
                    log.debug("Cannot resolve " + systemId + ", leaving to parser.");
                    return null;
                }
            } else {
                final String mmResource = systemId.substring(DOMAIN.length());
                // first, try MMBase config directory (if initialized)
                if (log.isDebugEnabled()) {
                    log.debug("mmbase resource " + ResourceLoader.getConfigurationRoot().getResource(mmResource));
                }
                definitionStream = ResourceLoader.getConfigurationRoot().getResourceAsStream(mmResource);
                if (definitionStream == null) {
                    Class<?> base = resolveBase; // if resolveBase was specified, use that.
                    Resource res = null;
                    if (base != null) {
                        if (mmResource.startsWith("xmlns/")) {
                            res = new FileResource(base, mmResource.substring(6));
                        } else {
                            res = new FileResource(base, mmResource.substring(4));  // dtd or xsd
                        }
                    }
                    if (res != null) {
                        definitionStream = res.getStream();
                        if (definitionStream == null) {
                            log.warn("Could not find " + res.toString() + " in " + base.getName() + ", falling back to " + MMRESOURCES + " while resolving " + systemId + " " + publicId);
                            base = null; // try it in org.mmbase.resources too.
                        }
                    }

                    if (base == null) {
                        String resource = MMRESOURCES + mmResource;
                        if (log.isDebugEnabled()) log.debug("Getting document definition as resource " + resource);
                        definitionStream = getClass().getResourceAsStream(resource);
                    }
                } else {
                    log.debug("Found resource in mmbase resource loader " + definitionStream);
                }
                if (definitionStream == null) {
                    if (resolveBase != null) {
                        log.error("Could not find MMBase entity '" + publicId + " " +  systemId + "' (did you make a typo?), returning null, system id will be used (needing a connection, or put in config dir) " + resolveBase + " " + mmResource);
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
     * @return whether the resolver has determined a DTD
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
