/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package speeltuin.media.org.mmbase.util.media;

import org.mmbase.module.builders.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
    private static final int MINSPEED        = 16000;
    private static final int MAXSPEED        = 96000;
    private static final int MINCHANNELS     = 1;
    private static final int MAXCHANNELS     = 2;
    
    
    /**
     * find the most appropriate MediaSource
     *
     * VPROs implementation:
     *   - if a g2 file is found which is encoded (status=done), set this as preffered
     *   - else find r5 file with best match with respect to wanted speed/channels
     */
    public MediaSource filterMediaSource(MediaFragment mediafragment, int wantedspeed, int wantedchannels) {
        
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
        
        Enumeration mediasources = mediafragment.getMediaSources();
        if( mediasources == null ) {
            log.warn("mediafragment "+mediaframent.getStringValue("title")+" does not have media sources");
        }
        
        MediaSource bestR5 = null;
        while(mediasources.hasMoreElements()) {
            mediasource = (MediaSource) mediasources.nextElement();
            
            // Is the MediaSource ready for use ?
            if( mediasource.getIntValue("status") == MediaSource.DONE ) {
                
                // If G2 is available return it.
                if( mediasource.getIntValue("format") == MediaSource.G2 ) {
                    return mediasource;
                    // Take the best R5 stream.
                } else if( mediasource.getIntValue("format") == MediaSource.R5 ) {
                    if(mediasource.getSpeed() <= wantedspeed && mediasource.getChannels() <= wantedchannels) {
                        if(bestR5==null) {
                            bestR5 = mediasource;
                        } else {
                            if(bestR5.getChannels() < mediasource.getChannels()) {
                                bestR5 = mediasource;
                            }
                            if(bestR5.getSpeed() < mediasource.getSpeed && bestR5.getChannels() == mediasource.getChannels()) {
                                bestR5 = mediasource;
                            }
                        }
                    }
                }
            }
        }
        // did we find a R5 stream ?
        if( bestR5 != null ) {
            return bestR5;
        }
        
        log.error("No appropriate MediaSource is found.");
        return null;
        
    }
}
