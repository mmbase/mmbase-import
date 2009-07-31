/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.MimeType;
import org.mmbase.applications.media.builders.MediaFragments;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;
import org.w3c.dom.Element;
import java.util.*;

/**
 * The URLComposerFactory contains the code to decide which kind of
 * URLComposer is instatiated.  This is a default implementation,
 * which can be extended for your situation (The class can be configured in
 * the mediaproviders builder xml)
 *
 * This particular implementation provides the possibility to relate
 * formats to URLComposer classes. It can also relate a
 * format/protocol combination to a URLComposer class.
 *
 * @author Michiel Meeuwissen
 * @author Rob Vermeulen (VPRO)
 */

public class URLComposerFactory  {

    private static final Logger log = Logging.getLoggerInstance(URLComposerFactory.class);

    // XML tags:
    private static final String MAIN_TAG     = "urlcomposers";
    private static final String DEFAULT_TAG  = "default";
    private static final String COMPOSER_TAG = "urlcomposer";
    private static final String FORMAT_ATT   = "format";
    private static final String PROTOCOL_ATT   = "protocol";
    private static final String MIMETYPE_ATT   = "mimetype";

    public static final  String CONFIG_FILE = "media/urlcomposers.xml";

    private static final Class<?> defaultComposerClass = URLComposer.class;

    private static URLComposerFactory instance = new URLComposerFactory();

    /**
     * Container class te represent one configuration item, which is a
     * format/protocol/URLComposer-class combination. The factory
     * maintains a List of these.
     */
    private static class ComposerConfig {
        /*
        private static Class[] constructorArgs = new Class[] {
            MMObjectNode.class, MMObjectNode.class, MMObjectNode.class, Map.class, List.class
        };*/
        private final Format format;
        private final String protocol;
        private final Class<?>  klass;
        private final MimeType mimeType;
        private final Map<String, String> properties = new HashMap<String, String>();

        ComposerConfig(Format f, Class<?> k, String p, MimeType mt) {
            this.format = f;
            this.klass = k;
            this.protocol = p == null ? "" : p;
            this.mimeType = mt;

        }
        boolean checkFormat(Format f) {
            return format == Format.ANY || format.equals(f);
        }
        boolean checkProtocol(String p) {
            return "".equals(protocol) || "".equals(p) || protocol.equals(p);
        }
        boolean checkMimeType(MimeType mt) {
            return mimeType.matches(mt);
        }

        Class<?>   getComposerClass() {
            return klass;
        }

        void setProperty(String key, String value) {
            properties.put(key, value);
        }
        URLComposer getInstance(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map<String, Object> info, Set<MMObjectNode> cacheExpireObjects) {
            try {
                log.debug("Instatiating " + klass);
                URLComposer newComposer = (URLComposer) klass.newInstance();
                for (Map.Entry<String, String> e : properties.entrySet()) {
                    org.mmbase.util.xml.Instantiator.setProperty(e.getKey(), klass, newComposer, e.getValue());
                }
                Map<String, Object> clone = new HashMap<String, Object>(); // filter may change the info map, but that should of course not influence others
                newComposer.init(provider, source, fragment, clone, cacheExpireObjects);
                return newComposer;
            }  catch (Exception g) {
                log.error("URLComposer could not be instantiated " + g.toString(), g);
            }
            return null; // could not get instance, this is an error, but go on anyway (implemtnation checks for null)
        }

        public String toString() {
            return "" + format + ":" + klass.getName() + " " + protocol + " " + mimeType + " " + properties;
        }
    }
    // this is the beforementioned list.
    private List<ComposerConfig> urlComposerClasses = new ArrayList<ComposerConfig>();

    private ComposerConfig defaultUrlComposer = new ComposerConfig(null, defaultComposerClass, null, MimeType.ANY);

    private ResourceWatcher configWatcher = new ResourceWatcher() {
        public void onChange(String file) {
            readConfiguration(file);
        }
    };


    /**
     * Construct the factory, which is a Singleton.
     */
    private URLComposerFactory() {
        configWatcher.add(CONFIG_FILE);
        configWatcher.onChange();
        configWatcher.start();

    }



    /**
     * read the factory's  configuration, which is the file 'urlcomposers.xml' in config/media/
     */
    private synchronized void readConfiguration(String configFile) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + configFile);
        }
        urlComposerClasses.clear();
        org.w3c.dom.Document doc;
        try {
            doc = ResourceLoader.getConfigurationRoot().getDocument(configFile, true, getClass());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
        if (doc == null) {
            log.error("Configuration file for URLComposerFactory " + configFile + " does not exist");
        } else {
            DocumentReader reader = new DocumentReader(doc);
            try {
                defaultUrlComposer = new ComposerConfig(null, Class.forName(reader.getElementValue(MAIN_TAG + "." + DEFAULT_TAG)), null, MimeType.ANY);
            } catch (java.lang.ClassNotFoundException e) {
                defaultUrlComposer = new ComposerConfig(null, defaultComposerClass, null, MimeType.ANY);
                // let it be something in any case
                log.error(e.toString());
            }

            for(Element element:reader.getChildElements(MAIN_TAG, COMPOSER_TAG)) {
                String  clazz   =  element.getAttribute("class");
                if (clazz.length() == 0) {
                    clazz = reader.getElementValue(element);
                }
                String  f = element.getAttribute(FORMAT_ATT);
                Format format;
                if ("*".equals(f) || f.length() == 0) {
                    format = Format.ANY;
                } else {
                    format = Format.get(f);
                }
                String  protocol  =  element.getAttribute(PROTOCOL_ATT);
                MimeType mimeType = new MimeType(element.getAttribute(MIMETYPE_ATT));
                try {
                    log.debug("Adding for format " + format + " urlcomposer " + clazz);
                    ComposerConfig config = new ComposerConfig(format, Class.forName(clazz), protocol, mimeType);
                    for(Element e : reader.getChildElements(element, "param")) {
                        config.setProperty(e.getAttribute("name"), reader.getElementValue(e));
                    }
                    urlComposerClasses.add(config);
                } catch (ClassNotFoundException ex) {
                    log.error("Cannot load urlcomposer " + clazz + " because " + ex.getMessage());
                }
            }
            log.info("Read url composers " + urlComposerClasses);
        }
        org.mmbase.cache.Cache<String, String> cache =  org.mmbase.applications.media.cache.URLCache.getCache();
        if (cache.size() > 0) {
            log.service("Clearing Media URL-cache");
        }
        cache.clear();
    }


    /**
     * Returns the one instance.
     */

    public  static URLComposerFactory getInstance() {
        return instance;
    }


    /**
     * You can relate template objects to media fragments. They can be
     * processed by 'MarkupURLComposers'. For every template a
     * MarkupURLComposers will be created (if, at least,
     * MarkupURLComposers are configured in urlcomposers.xml).
     */

    protected List<MMObjectNode> getTemplates(MMObjectNode fragment) {
        List<MMObjectNode> templates = new ArrayList<MMObjectNode>();

        if (fragment != null) {
            MediaFragments bul = (MediaFragments) fragment.getBuilder();
            Stack<MMObjectNode> stack = bul.getParentFragments(fragment);
            Iterator<MMObjectNode> i = stack.iterator();
            while (i.hasNext()) {
                MMObjectNode f = i.next();
                templates.addAll(f.getRelatedNodes("templates"));
            }
        }
        return templates;
    }


    /**
     * Add urlcomposer to list of urlcomposers if that is possible.
     *
     * @return true if added, false if not.
     */
    protected boolean addURLComposer(URLComposer uc, List<URLComposer> urls) {
        if (log.isDebugEnabled()) {
            log.debug("Trying to add " + uc + " to " + urls);
        }
        if (uc == null) {
            log.debug("Could not make urlcomposer");
        } else if (urls.contains(uc)) {  // avoid duplicates
            log.debug("This URLComposer " + uc + " already in the list " + urls);
        } else if (!uc.canCompose()) {
            log.debug("This URLComposer cannot compose");
        } else {
            log.debug("Adding a " + uc.getClass().getName());
            urls.add(uc);
            return true;
        }
        return false;
    }

    /**
     * When the provider/source/fragment combo is determined they can
     * be fed into this function of the urlcomposerfactory, which will
     * then produce zero or more urlcomposers. They are added to the
     * provided list, or a new list will be made if the argument List
     * is 'null'.
     *
     * @param provider MMObjectNode
     * @param source   MMObjectNode
     * @param info     A Map with additional options
     * @param urls     A List with URLComposer to which the new ones must be added, or null.
     *
     * @return The (new) list with urlcomposers.
     */
    public  List<URLComposer> createURLComposers(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map<String, Object> info, List<URLComposer> urls, Set<MMObjectNode> cacheExpireObjects) {
        if (urls == null) {
            urls = new ArrayList<URLComposer>();
        }

        final Format format   = Format.get(source.getIntValue("format"));
        final String protocol = provider.getStringValue("protocol");
        MimeType mt = format.getMimeType();
        if (source.getBuilder().hasField("mimetype")) {
            String v = source.getStringValue("mimetype");
            if (v != null && v.length() > 0) {
                mt = new MimeType(source.getStringValue("mimetype"));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating url-composers for provider " + provider.getNumber() + " (format: " + format + ", protocol: " + protocol + ")");
        }

        boolean found = false;
        for (ComposerConfig cc : urlComposerClasses) {
            if (log.isDebugEnabled()) {
                log.debug("Trying " + cc + " for '" + format + "'/'" + protocol + "' " + mt);
            }

            if (cc.checkFormat(format) && cc.checkProtocol(protocol) && cc.checkMimeType(mt)) {
                if (MarkupURLComposer.class.isAssignableFrom(cc.getComposerClass())) {
                    // markupurlcomposers need a template, and a fragment can have 0-n of those.
                    List<MMObjectNode> templates = getTemplates(fragment);
                    Iterator<MMObjectNode> ti = templates.iterator();
                    while (ti.hasNext()) {
                        MMObjectNode template = ti.next();
                        Map<String, Object> templateInfo = new HashMap<String, Object>(info);
                        templateInfo.put("template", template);
                        URLComposer uc = cc.getInstance(provider, source, fragment, templateInfo, cacheExpireObjects);
                        addURLComposer(uc, urls);
                    }
                } else {
                    // normal urlcomposers are one per fragment of course
                    URLComposer uc = cc.getInstance(provider, source, fragment, info, cacheExpireObjects);
                    addURLComposer(uc, urls);
                }
                log.debug("Can be used!");
                found = true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(cc + " Not usable. For format '" + format + "': " + cc.checkFormat(format) + ", for protocol '" + protocol + "': " + cc.checkProtocol(protocol));
                }
            }
        }

        if (! found) { // use default
            URLComposer uc = defaultUrlComposer.getInstance(provider, source, fragment, info, cacheExpireObjects);
            log.trace("nothing found");
            if (uc != null && ! urls.contains(uc)) { // avoid duplicates
                log.debug("No urlcomposer found, adding the default");
                urls.add(uc);
            }
        }
        log.debug("returning " + urls);
        return urls;
    }
}
