/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/


package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.builders.MediaFragments;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.*;
import org.w3c.dom.Element;
import java.util.*;
import java.io.File;
import java.lang.reflect.*;
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
 * @version $Id: URLComposerFactory.java,v 1.11 2003-02-25 23:54:31 michiel Exp $
 */

public class URLComposerFactory  {

    private static Logger log = Logging.getLoggerInstance(URLComposerFactory.class.getName());

    // XML tags:
    private static final String MAIN_TAG     = "urlcomposers";
    private static final String DEFAULT_TAG  = "default";
    private static final String COMPOSER_TAG = "urlcomposer";
    private static final String FORMAT_ATT   = "format";
    private static final String PROTOCOL_ATT   = "protocol";

    private static final Class defaultComposerClass = URLComposer.class;

 
    private static URLComposerFactory instance = new URLComposerFactory();

    /**
     * Container class te represent one configuration item, which is a
     * format/protocol/URLComposer-class combination. The factory
     * maintains a List of these.
     */

    private static class ComposerConfig {
        private static Class[] constructorArgs = new Class[] {
            MMObjectNode.class, MMObjectNode.class, MMObjectNode.class, Map.class
        };
        private Format format;
        private String protocol;
        private Class  klass;
        ComposerConfig(Format f, Class k, String p) {
            this.format = f;
            this.klass = k;            
            this.protocol = p;
            if (protocol == null) protocol = "";
            
        }
        boolean checkFormat(Format f) {     return format.equals(f); }
        boolean checkProtocol(String p) {   return "".equals(protocol) || protocol.equals(p); }

        Class   getComposerClass() { return klass; };

        URLComposer getInstance(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) { 
            try {
                Constructor c = klass.getConstructor(constructorArgs);
                return (URLComposer) c.newInstance(new Object[] {provider, source, fragment, info});            
            } catch (java.lang.NoSuchMethodException e) { 
                log.error("URLComposer implementation does not contain right constructor " + e.toString());
            } catch (java.lang.SecurityException f) {
                log.error("URLComposer implementation does not accessible constructor " + f.toString());
            }  catch (Exception g) {
               log.error("URLComposer could not be instantiated " + g.toString());
            }
            return null; // could not get instance, this is an error, but go on anyway (implemtnation checks for null)
        }
        public String toString() {
            return "" + format + ":" + klass.getName();
        }
        
        
    }
    // this is the beforementioned list.
    private List urlComposerClasses = new ArrayList();

    private ComposerConfig defaultUrlComposer;


    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    

    /**
     * Construct the factory, which is a Singleton.
     */
    private URLComposerFactory() {
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), 
                                   "media" + File.separator + "urlcomposers.xml");
        if (! configFile.exists()) {
            log.error("Configuration file for URLComposerFactory " + configFile + " does not exist");
            return;
        }
        readConfiguration(configFile);
        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
    }



    /**
     * read the factory's  configuration, which is the file 'urlcomposers.xml' in config/media/
     */
    private synchronized void readConfiguration(File configFile) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + configFile);
        }
        urlComposerClasses.clear();

        XMLBasicReader reader = new XMLBasicReader(configFile.toString(), getClass());
        try {
            defaultUrlComposer = new ComposerConfig(null, Class.forName(reader.getElementValue(MAIN_TAG + "." + DEFAULT_TAG)), null);
        } catch (java.lang.ClassNotFoundException e) {
            defaultUrlComposer = new ComposerConfig(null, defaultComposerClass, null); 
            // let it be something in any case
            log.error(e.toString());
        }
              
        for(Enumeration e = reader.getChildElements(MAIN_TAG, COMPOSER_TAG); e.hasMoreElements();) {
            Element element = (Element)e.nextElement();
            String  clazz   =  reader.getElementValue(element);
            String  f = element.getAttribute(FORMAT_ATT);
            List formats;
            if ("*".equals(f)) {
                formats = Format.getMediaFormats();
            } else {
                formats = new ArrayList();
                formats.add(Format.get(f));
            }            
            String  protocol  =  element.getAttribute(PROTOCOL_ATT);
            Iterator i = formats.iterator();
            while(i.hasNext()) {
                Format format = (Format) i.next();
                try {
                    log.service("Adding for format " + format + " urlcomposer " + clazz);
                    urlComposerClasses.add(new ComposerConfig(format, Class.forName(clazz), protocol));
                } catch (ClassNotFoundException ex) {
                    log.error("Cannot load urlcomposer " + clazz);
                } 
            }

        }
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

    protected List getTemplates(MMObjectNode fragment) {
        List templates = new ArrayList();

        if (fragment != null) {
            MediaFragments bul = (MediaFragments) fragment.parent;
            Stack stack = bul.getParentFragments(fragment);
            Iterator i = stack.iterator();
            while (i.hasNext()) {
                MMObjectNode f = (MMObjectNode) i.next();
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

    protected boolean addURLComposer(URLComposer uc, List urls) {
        if (log.isDebugEnabled()) {
            log.debug("Trying to add " + uc + " to " + urls);
        }
        if (uc == null) {
            log.debug("Could not make urlcomposer");
        } else if (urls.contains(uc)) {  // avoid duplicates
            log.debug("This URLComposer already in the list");
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

    public  List createURLComposers(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info, List urls) {
        if (urls == null) urls = new ArrayList();
        Format format   = Format.get(source.getIntValue("format"));
        String protocol = provider.getStringValue("protocol");
        if (log.isDebugEnabled()) log.debug("Creating url-composers for provider " + provider.getNumber() + "(" + format + ")");

        Iterator i = urlComposerClasses.iterator();
        boolean found = false;
        while (i.hasNext()) {
            ComposerConfig cc = (ComposerConfig) i.next();
            if (log.isDebugEnabled()) {
                log.debug("Trying " + cc + " for '" + format + "'/'" + protocol + "'");
            }
            
            if (cc.checkFormat(format) && cc.checkProtocol(protocol)) {                
                if (MarkupURLComposer.class.isAssignableFrom(cc.getComposerClass())) {
                    // markupurlcomposers need a template, and a fragment can have 0-n of those.
                    List templates = getTemplates(fragment);
                    Iterator ti = templates.iterator();
                    while (ti.hasNext()) {
                        MMObjectNode template = (MMObjectNode) ti.next();
                        Map templateInfo = new HashMap(info);
                        templateInfo.put("template", template);
                        URLComposer uc = cc.getInstance(provider, source, fragment, templateInfo);
                        addURLComposer(uc, urls);                        
                    }
                } else {
                    // normal urlcomposers are one per fragment of course
                    URLComposer uc = cc.getInstance(provider, source, fragment, info);
                    addURLComposer(uc, urls);
                }
                found = true;
            } else {
                log.debug(cc.checkFormat(format) + "/" + cc.checkProtocol(protocol));
            }
        }
        if (! found) { // use default
            URLComposer uc = defaultUrlComposer.getInstance(provider, source, fragment, info);
            if (uc != null && ! urls.contains(uc)) { // avoid duplicates
                log.debug("No urlcomposer found, adding the default");
                urls.add(uc);
            }
        }
        return urls;
    
}
}
