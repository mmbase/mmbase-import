/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.module.core.MMObjectNode;
import java.util.Vector;

/**
 * Though this interface the MediaProviderFilter is able to access filters written
 * by external parties. In the mediaproviderfilter configuration file the external
 * filters can be specified.
 *
 * @author  Rob Vermeulen (VPRO)
 */
public interface MediaProviderFilterInterface {
    
    /**
     * this method will filter mediaproviders. A set with mediaproviders will be
     * passed as a parameter, and the method will return the filtered set of 
     * mediaproviders.
     * @param mediasource The requested mediasource
     * @param mediaproviders The set of already filtered mediaproviders
     * @param request The servlet request of the user
     * @param wantedspeed
     * @param wantedchannels
     * @return A new set of filtered mediaproviders.
     */
    public Vector filterMediaProvider(MMObjectNode mediasource, Vector mediaproviders, HttpServletRequest request, int wantedspeed, int wantedchannels);
}

