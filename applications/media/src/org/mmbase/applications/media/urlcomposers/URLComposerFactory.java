/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/


package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.Format;
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
 * This particular implementation provides the possiblility to relate
 * formats to URLComposer classes.
 *
 * @author Michiel Meeuwissen
 * @version $Id: URLComposerFactory.java,v 1.6 2003-02-05 11:41:25 michiel Exp $
 */

public class URLComposerFactory  {

    private static Logger log = Logging.getLoggerInstance(URLComposerFactory.class.getName());
    private static final String MAIN_TAG     = "urlcomposers";
    private static final String DEFAULT_TAG  = "default";
    private static final String COMPOSER_TAG = "urlcomposer";
    private static final String FORMAT_ATT   = "format";

    private static final Class defaultComposerClass = URLComposer.class;

  
    private static URLComposerFactory instance = new URLComposerFactory();

    private static class ComposerConfig {
        private static Class[] constructorArgs = new Class[] {
            MMObjectNode.class, MMObjectNode.class, MMObjectNode.class, Map.class
        };
        private Format format;
        private Class  klass;
        ComposerConfig(Format f, Class k) {
            this.format = f;
            this.klass = k;            
        }
        Format getFormat() { return format; }
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

    private List urlComposerClasses = new ArrayList();
    private ComposerConfig defaultUrlComposer;

    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    

    /**
     * Construct the MainFilter
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
     * read the MainFilter configuration
     */
    private synchronized void readConfiguration(File configFile) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + configFile);
        }
        urlComposerClasses.clear();

        XMLBasicReader reader = new XMLBasicReader(configFile.toString(), getClass());
        try {
            defaultUrlComposer = new ComposerConfig(null, Class.forName(reader.getElementValue(MAIN_TAG + "." + DEFAULT_TAG)));
        } catch (java.lang.ClassNotFoundException e) {
            defaultUrlComposer = new ComposerConfig(null, defaultComposerClass); 
            // let it be something in any case
            log.error(e.toString());
        }
              
        for(Enumeration e = reader.getChildElements(MAIN_TAG, COMPOSER_TAG); e.hasMoreElements();) {
            Element element = (Element)e.nextElement();
            String  clazz   =  reader.getElementValue(element);
            Format  format  =  Format.get(element.getAttribute(FORMAT_ATT));
            try {
                log.service("Adding for format " + format + " urlcomposer " + clazz);
                urlComposerClasses.add(new ComposerConfig(format, Class.forName(clazz)));
            } catch (ClassNotFoundException ex) {
                log.error("Cannot load urlcomposer " + clazz);
            } 

        }
    }



    public  static URLComposerFactory getInstance() {
        return instance;
    }

    public  List createURLComposers(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info, List urls) {
        if (urls == null) urls = new ArrayList();
        Format format = Format.get(source.getIntValue("format"));
        if (log.isDebugEnabled()) log.debug("Creating url-composers for provider " + provider.getNumber() + "(" + format + ")");

        Iterator i = urlComposerClasses.iterator();
        boolean found = false;
        while (i.hasNext()) {
            ComposerConfig cc = (ComposerConfig) i.next();
            log.debug("Trying " + cc + " for " + format);
            if (format.equals(cc.getFormat())) {
                URLComposer uc = cc.getInstance(provider, source, fragment, info);
                log.debug("Trying to add " + uc + " to " + urls);
                if (uc != null && ! urls.contains(uc)) { // avoid duplicates
                    log.debug("Adding a " + uc.getClass().getName());
                    urls.add(uc);
                } 
                found = true;
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
