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
 * The MediaProviderFilter will find out which media provider (audio/video server)
 * is most appropriate to handle the request of the user.
 *
 * This class will become an abstract class, and in a later release we hope to provide
 * a more configurable class.
 *
 * This class contains code currently used by the VPRO. This is no legacy, but just an
 * example.
 *
 */
public class MediaProviderFilter {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
        
    private MediaSource mediasourcebuilder = null;
    
    public MediaProviderFilter (MediaSource ms) {
        mediasourcebuilder = ms;
    }
        
    /**
     * This is a test class and will return the first mediaprovider found.
     *
     */
    public MMObjectNode filterMediaProvider(MMObjectNode mediasource, HttpServletRequest request, int wantedspeed, int wantedchannels) {
        Enumeration e = mediasourcebuilder.getMediaProviders(mediasource).elements();
        
        while(e.hasMoreElements()) {
            // just return first found media provider.
            return (MMObjectNode) e.nextElement();
        }
        return null;
    }
}
