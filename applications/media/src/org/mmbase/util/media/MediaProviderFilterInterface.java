/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.module.core.MMObjectNode;
import java.util.*;

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
     * @param info information such as: bit rate/ httprequest/ number of channels
     * @return A new set of filtered mediaproviders.
     */
    public List filterMediaProvider(List mediaproviders, MMObjectNode mediasource, Map info);
}

