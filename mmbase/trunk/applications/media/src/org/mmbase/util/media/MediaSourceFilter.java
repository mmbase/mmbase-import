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
import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

/**
 * The MediaSourceFilter is involved in finding the appropriate MediaSource while
 * an url is requested on a MediaFragment. According to information given by an user,
 * and policies of the organisation providing the media a choice can be made.
 *
 * This class will become an abstract class, and in a later release we hope to provide
 * a more configurable class.
 *
 * This class contains code currently used by the VPRO. This is no legacy, but just an
 * example.
 *
 */
public class MediaSourceFilter {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
    
    private MediaFragment mediaFragmentBuilder = null;
    
    private static final int MINSPEED        = 16000;
    private static final int MAXSPEED        = 96000;
    private static final int MINCHANNELS     = 1;
    private static final int MAXCHANNELS     = 2;
        
    public MediaSourceFilter (MediaFragment mf) {
        mediaFragmentBuilder = mf;
    }
    
    /**
     * find the most appropriate MediaSource
     *
     * VPROs implementation:
     *   - if a g2 file is found which is encoded (status=done), set this as preffered
     *   - else find r5 file with best match with respect to wanted speed/channels
     */
    public MMObjectNode filterMediaSource(MMObjectNode mediaFragment, HttpServletRequest request, int wantedspeed, int wantedchannels) {
        
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
        
        Enumeration mediaSources = mediaFragmentBuilder.getMediaSources(mediaFragment).elements();
        if( mediaSources == null ) {
            log.warn("mediaFragment "+mediaFragment.getStringValue("title")+" does not have media sources");
        }
        
        MMObjectNode bestR5 = null;
        while(mediaSources.hasMoreElements()) {
            MMObjectNode mediaSource = (MMObjectNode) mediaSources.nextElement();
            
            /*
            // Is the MediaSource ready for use ?
            if( mediaSource.getIntValue("status") == MediaSource.DONE ) {
                
                // If G2 is available return it.
                if( mediaSource.getIntValue("format") == MediaSource.SURESTREAM_FORMAT ) {
                    return mediaSource;
                    // Take the best R5 stream.
                } else if( mediaSource.getIntValue("format") == MediaSource.RA_FORMAT ) {
                    if(mediaSource.getSpeed() <= wantedspeed && mediaSource.getChannels() <= wantedchannels) {
                        if(bestR5==null) {
                            bestR5 = mediaSource;
                        } else {
                            if(bestR5.getChannels() < mediaSource.getChannels()) {
                                bestR5 = mediaSource;
                            }
                            if(bestR5.getSpeed() < mediaSource.getSpeed && bestR5.getChannels() == mediaSource.getChannels()) {
                                bestR5 = mediaSource;
                            }
                        }
                    }
                }
            }
            */
        }
        // did we find a R5 stream ?
        if( bestR5 != null ) {
            return bestR5;
        }
        
        log.error("No appropriate MediaSource is found.");
        return null;
        
    }
}
