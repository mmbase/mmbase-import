/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.applications.media.builders.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.*;

import org.w3c.dom.Element;

import java.util.*;
import java.io.File;
import java.lang.Integer;

/**
 * The MediaSourceFilter is involved in finding the appropriate media source 
 * given a certain media fragment. The choice of the media source depends on 
 * the configuration files configured, and the information an user is passing 
 * through the info variable.
 *
 * The appropriate mediasource will be found by passing the mediasources through a
 * set of mediasource filters. These filters can be specified in the
 * mediasourcefilter configuration file.
 *
 * One standard filters is provided:
 * 1) preferredSource, this is a list of media formats. The first found format is
 * returned.
 *
 * A specific method for RealAudio is implemented. More of these can follow.
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen
 */
public class MediaSourceFilter implements MediaFilter {    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());

    public static final String MAIN_TAG          = "mainFilter";
    public static final String FILTERCONFIGS_TAG = "filterConfigs";
    public static final String FILTERCONFIG_TAG  = "config";
        
    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    
    private List filters = new ArrayList();

    /**
     * Construct the MediaSourceFilter
     */
    private MediaSourceFilter() {
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), 
                                   "media" + File.separator + "mediasourcefilter.xml");
        if (! configFile.exists()) {
            log.error("Configuration file for mediasourcefilter " + configFile + " does not exist");
            return;
        }
        readConfiguration(configFile);
        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
    }

    
    private static MediaSourceFilter filter = null;

    public static MediaSourceFilter getInstance() {
        if (filter == null) filter = new MediaSourceFilter();
        return filter;
    }


    public void configure(XMLBasicReader reader, Element e) { }
    
    /**
     * read the MediaSourceFilter configuration
     */
    private synchronized void readConfiguration(File configFile) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + configFile);
        }
        filters.clear();

        XMLBasicReader reader = new XMLBasicReader(configFile.toString(), getClass());
        Element filterConfigs = reader.getElementByPath(MAIN_TAG + "." + FILTERCONFIGS_TAG);

        ChainComparator chainComp = new ChainComparator();
        for(Enumeration e = reader.getChildElements(MAIN_TAG + ".chain","filter"); e.hasMoreElements();) {
            Element chainElement =(Element)e.nextElement();
            String  clazz        = reader.getElementValue(chainElement);
            String  elementId    = chainElement.getAttribute("id");
            try {
                Class newclass = Class.forName(clazz);
                MediaFilter filter = (MediaFilter) newclass.newInstance();
                if (filter instanceof URLComposerComparator) {
                    chainComp.add((URLComposerComparator) filter);
                } else {
                    if (chainComp.size() > 0) { 
                        filters.add(chainComp);
                        chainComp = new ChainComparator();                        
                    }
                    filters.add(filter);
                }
                log.service("Added mediasourcefilter " + clazz);
                if (elementId != null) {                    
                    // find right configuration
                    boolean found = false;
                    Enumeration f = reader.getChildElements(filterConfigs, FILTERCONFIG_TAG);
                    while (f.hasMoreElements()) {
                        Element config = (Element) f.nextElement();
                        String filterAtt = reader.getElementAttributeValue(config, "filter");
                        if (filterAtt.equals(elementId)) {
                            log.service("Configuring " + elementId +"/" + clazz);
                            filter.configure(reader, config);
                            found = true;
                            break;
                        }
                    }
                    if (! found) log.service("No configuration found for filter " + elementId);
                }
            } catch (ClassNotFoundException ex) {
                log.error("Cannot load filter " + clazz + "\n" + ex);
            } catch (InstantiationException ex1) {
                log.error("Cannot load filter " + clazz + "\n" + ex1);
            } catch (IllegalAccessException ex2) {
                log.error("Cannot load filter " + clazz + "\n" + ex2);
            }                   
        }
        if (chainComp.size() > 0) filters.add(chainComp); // make sure it is at least empty
    }

    public List filter(List urls) {
        Iterator i = filters.iterator();
        while (i.hasNext()) {
            MediaFilter filter = (MediaFilter) i.next();
            if (log.isDebugEnabled()) {
                log.debug("Using filter " + filter);
                log.debug("before: " + urls);
            }
            
            urls = filter.filter(urls);
            if (log.isDebugEnabled()) {
                log.debug("after: " + urls);
            }
        }
        return urls;
    }
    
}
