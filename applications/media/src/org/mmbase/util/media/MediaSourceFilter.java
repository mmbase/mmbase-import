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
 * 1) preferedSource, this is a list of media formats. The first found format is
 * returned.
 *
 * A specific method for RealAudio is implemented. More of these can follow.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class MediaSourceFilter {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
    
    private MediaFragments mediaFragmentBuilder = null;
    private MediaSources mediaSourceBuilder = null;
        
    private static int MINSPEED        = 0;
    private static int MAXSPEED        = 0;
    private static int MINCHANNELS     = 0;
    private static int MAXCHANNELS     = 0;
    
    // PreferedSource information
    private List preferedSources = null;
    
    // contains the external filters
    private Map externFilters = null;
    
    // This chain contains the filters for the mediaproviders
    private static List filterChain = null;
    
    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    
    /**
     * construct the MediaSourceFilter
     */
    public MediaSourceFilter(MediaFragments mf, MediaSources ms) {
        mediaFragmentBuilder = mf;
        mediaSourceBuilder = ms;
        
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
        
        XMLBasicReader reader = new XMLBasicReader(configFile.toString());
        
        // reading filterchain information
        externFilters = new Hashtable();
        filterChain   = new Vector();
        for(Enumeration e = reader.getChildElements("mediasourcefilter.chain","filter");e.hasMoreElements();) {
            Element chainelement=(Element)e.nextElement();
            String chainvalue = reader.getElementValue(chainelement);
            if(!chainvalue.equals("preferedSource")) {
                
                try {
                    Class newclass=Class.forName(chainvalue);
                    externFilters.put(chainvalue,(MediaSourceFilterInterface)newclass.newInstance());
                    filterChain.add(chainvalue);
                } catch (Exception exception) {
                    log.error("Cannot load MediaSourceFilter "+chainvalue+"\n"+exception);
                }
                
                log.debug("Read extern chain: "+chainvalue);
                
            } else {
                log.debug("Read standard chain: "+chainvalue);
                filterChain.add(chainvalue);
            }
        }
        // reading preferedSource information
        preferedSources = new Vector();
        for( Enumeration e = reader.getChildElements("mediasourcefilter.preferedSource","source");e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(n3,"format");
            preferedSources.add(format.toLowerCase());
            log.debug("Adding preferedSource format: "+format);
        }
        
        try {
            MINSPEED = Integer.parseInt(reader.getElementValue("mediasourcefilter.realaudio.minspeed"));
            MAXSPEED = Integer.parseInt(reader.getElementValue("mediasourcefilter.realaudio.maxspeed"));
            MINCHANNELS = Integer.parseInt(reader.getElementValue("mediasourcefilter.realaudio.maxchannels"));
            MAXCHANNELS = Integer.parseInt(reader.getElementValue("mediasourcefilter.realaudio.minchannels"));
        } catch (Exception e) {
            log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information");
        }
        if(log.isDebugEnabled()) {
            log.debug("Minspeed="+MINSPEED);
            log.debug("Maxspeed="+MAXSPEED);
            log.debug("Minchannels="+MINCHANNELS);
            log.debug("Maxchannels="+MAXCHANNELS);
        }
    }
    
    
    /**
     * filter the most appropriate mediasource. This method is invoked from MediaFragment.
     * The mediaSource will be found by passing a list of mediaSources through a chain
     * of mediaSources filters.
     * @param mediaFragment the given media fragment
     * @param info Additional information given by an user
     * @return the most appropriate media source
     */
    public MMObjectNode filterMediaSource(MMObjectNode mediaFragment, Map info) {
        
        List mediaSources = mediaFragmentBuilder.getMediaSources(mediaFragment);
        if( mediaSources == null ) {
            log.warn("mediaFragment "+mediaFragment.getStringValue("title")+" does not have media sources");
        }
        
        // passing the mediasources through al the filters
        for (Iterator i = filterChain.iterator(); i.hasNext();) {
            String filter = (String) i.next();
            log.debug("Using filter " + filter);
            if(filter.equals("preferedSource")) {
                mediaSources = preferedSource(mediaSources, info);
            } else {
                MediaSourceFilterInterface mpfi = (MediaSourceFilterInterface)externFilters.get(filter);
                mediaSources = mpfi.filterMediaSource(mediaSources, mediaFragment, info);
            }
        }
        
        return  takeOneMediaSource(mediaSources);
    }
    
    /**
     * Take one mediasource. This method is used to just take one mediasource
     * of a list with appropriate media sources.
     * @param mediasources list of appropriate media sources
     * @return The mediasource that is going to handle the request
     */
    private MMObjectNode takeOneMediaSource(List mediasources) {

        if(mediasources == null) return null;
        
        Iterator i = mediasources.iterator();
        while(i.hasNext()) {
            // just return first found media source.
            return (MMObjectNode) i.next();
        }
        return null;
    }
    
    /**
     * Find a media source with a format specified in the preferedSource list in the
     * mediasourcefilter configuration file.
     * @param mediasources The list with appropriate mediasources
     * @param info Additional information
     * @return The most appropriate media source
     */
    private List preferedSource(List mediasources, Map info) {
        MMObjectNode node = null;
        
        for (Iterator i=preferedSources.iterator(); i.hasNext();) {
            String format = (String) i.next();
            log.debug("checking format "+format);
            if(format.equals("ra")) {
                node = getRealAudio(mediasources, info);
            } else {
                node = getFormat(mediasources, MediaSources.convertFormatToNumber(format));
            } 
            if (node!=null) {
                log.debug("found mediasource format "+format);
                List mediasource = new Vector();
                mediasource.add(node);
                return mediasource;
            }
        }
        log.error("No appropriate mediasource found.");
        return null;
    }
    
    /**
     * select the mediasource that is of the approriate format
     * @param mediaSources the list of appropriate mediasources
     * @param format the wanted format
     * @return a mediasource of wanted format
     */
    private MMObjectNode getFormat(List mediaSources, int format) {
        log.debug("Getting format "+format);
        for(Iterator i=mediaSources.iterator(); i.hasNext();) {
            MMObjectNode mediaSource = (MMObjectNode) i.next();
            
            // Is the MediaSource ready for use && is it of format surestream
            if(mediaSource.getIntValue("format") == format ) {
                if(mediaSource.getIntValue("state") == MediaSources.DONE) {
                    log.debug("Media source found ("+mediaSource.getStringValue("number")+")");
                    return mediaSource;
                } else {
                    log.debug("Media source ("+mediaSource.getStringValue("number")+")does not have status DONE");
                }
            }
        }
        return null;
    }
    
    /**
     * select the best realaudio mediasource if available
     * @param mediaSources the list of appropriate mediasources
     * @return the best realaudio mediasource
     */
    private MMObjectNode getRealAudio(List mediaSources, Map info) {
        int wantedspeed=0;
        int wantedchannels=0;
        if(info.containsKey("wantedspeed")) {
            wantedspeed=Integer.parseInt(""+info.get("wantedspeed"));
        }
        if(info.containsKey("wantedchannels")) {
            wantedchannels=Integer.parseInt(""+info.get("wantedchannels"));
        }
        
        if( wantedspeed < MINSPEED ) {
            log.error("wantedspeed("+wantedspeed+") less than minspeed("+MINSPEED+")");
            wantedspeed = MINSPEED;
        }
        if( wantedspeed > MAXSPEED ) {
            log.error("wantedspeed("+wantedspeed+") greater than maxspeed("+MAXSPEED+")");
            wantedspeed = MAXSPEED;
        }
        if( wantedchannels < MINCHANNELS ) {
            log.error("wantedchannels("+wantedchannels+") less than minchannels("+MINCHANNELS+")");
            wantedchannels = MINCHANNELS;
        }
        if( wantedchannels > MAXCHANNELS ) {
            log.error("wantedchannels("+wantedchannels+") greater than maxchannels("+MAXCHANNELS+")");
            wantedchannels = MAXCHANNELS;
        }
        
        MMObjectNode bestR5 = null;
        for(Iterator i=mediaSources.iterator(); i.hasNext();) {
            MMObjectNode mediaSource = (MMObjectNode) i.next();
            
            // Is the MediaSource ready for use && is format realaudio
            if( mediaSource.getIntValue("status") == MediaSources.DONE  &&
            mediaSource.getIntValue("format") == MediaSources.RA_FORMAT ) {
                if(mediaSourceBuilder.getSpeed(mediaSource) <= wantedspeed && mediaSourceBuilder.getChannels(mediaSource) <= wantedchannels) {
                    if(bestR5==null) {
                        bestR5 = mediaSource;
                    } else {
                        if(mediaSourceBuilder.getChannels(bestR5) < mediaSourceBuilder.getChannels(mediaSource)) {
                            bestR5 = mediaSource;
                        }
                        if(mediaSourceBuilder.getSpeed(bestR5) < mediaSourceBuilder.getSpeed(mediaSource) && mediaSourceBuilder.getChannels(bestR5) == mediaSourceBuilder.getChannels(mediaSource)) {
                            bestR5 = mediaSource;
                        }
                    }
                }
            }
        }
        // did we find a R5 stream ?
        if( bestR5 != null ) {
            log.debug("R5 stream found "+bestR5.getStringValue("number"));
            return bestR5;
        }
        
        return null;
    }
}
