/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.media.*;
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
public class MediaSourceFilter extends GroupComparator {    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
        
    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    
    /**
     * Construct the MediaSourceFilter
     */
    public MediaSourceFilter() {
        super();        
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), "media" + File.separator + "mediasourcefilter.xml");
        if (! configFile.exists()) {
            log.error("Configuration file for mediasourcefilter " + configFile + " does not exist");
            return;
        }
        readConfiguration(configFile);
        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
    }
    
    /**
     * read the MediaSourceFilter configuration
     */
    private synchronized void readConfiguration(File configFile) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + configFile);
        }
        clear();

        XMLBasicReader reader = new XMLBasicReader(configFile.toString(), getClass());        
        for(Enumeration e = reader.getChildElements("mediasourcefilter.chain","filter"); e.hasMoreElements();) {
            Element chainelement=(Element)e.nextElement();
            String chainvalue = reader.getElementValue(chainelement);
            try {
                Class newclass = Class.forName(chainvalue);
                add((ResponseInfoComparator) newclass.newInstance());
            } catch (ClassNotFoundException ex) {
                log.error("Cannot load filter " + chainvalue + "\n" + ex);
            } catch (InstantiationException ex1) {
                log.error("Cannot load filter " + chainvalue + "\n" + ex1);
            } catch (IllegalAccessException ex2) {
                log.error("Cannot load filter " + chainvalue + "\n" + ex2);
            }                   
        }
    }
    
}
