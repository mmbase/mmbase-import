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

import java.util.*;

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
    
    private static Hashtable filterHosts = new Hashtable();
    
    public MediaProviderFilter(MediaSource ms) {
        mediasourcebuilder = ms;
    }
        
    public MMObjectNode filterMediaProvider(MMObjectNode mediasource, HttpServletRequest request, int wantedspeed, int wantedchannels) {
        Vector mediaproviders = mediasourcebuilder.getMediaProviders(mediasource);
        mediaproviders = sortMediaProviders(mediaproviders);
        mediaproviders = filterHostOnDomain("userinfo",mediaproviders);
        // Here we have to put other mediaproviderfilters in the chain...
        // mediaproviders = filterMediaProvider(mediaproviders, mediasource, request, wantedspeed, wantedchannels);
        return  takeBestMediaProvider(mediaproviders);
    }
    
    
    /**
     */
    protected MMObjectNode takeBestMediaProvider(Vector mediaproviders) {
        
        Enumeration e = mediaproviders.elements();
        while(e.hasMoreElements()) {
            // just return first found media provider.
            return (MMObjectNode) e.nextElement();
        }
        return null;
    }
    
    
    /**
     * sort the mediaproviders with the most prefered providers first.
     */
    protected Vector sortMediaProviders(Vector mediaproviders) {
        
        // from configuration
        Vector prefferedProviders = new Vector();
        prefferedProviders.addElement("streams.omroep.nl"); // first place
        prefferedProviders.addElement("streams.vpro.nl"); // second place
        Vector sortedProviders = new Vector();
        
        Enumeration pp = prefferedProviders.elements();
        while (pp.hasMoreElements()) {
            
            String prefname = (String)pp.nextElement();
            
            MMObjectNode node = null;
            Enumeration e = mediaproviders.elements();
            while(e.hasMoreElements()) {
                node = (MMObjectNode) e.nextElement();
                if(prefname.equals(node.getStringValue("rooturl"))) {
                    sortedProviders.addElement(node);
                }
            }
        }
        return sortedProviders;
    }
    
    
    /**
     * filter the MediaProvider according to the host address of the user.
     * filters can be set like vpro.nl -> streams.vpro.nl,speedy.vpro.nl, this means
     * that if the user is locoted in the domain *.vpro.nl the provider will be chosen
     * from streams.vpro.nl or speedy.vpro.nl
     *
     * @return vector of provider names. Not the real providers because this will cause to
     * many database calls.
     */
    protected Vector filterHostOnDomain(String userhost, Vector mediaproviders) {
        // From configuration
        filterHosts.put("","streams.omroep.nl"); //default
        filterHosts.put("vpro.nl","streams.vpro.nl");
        filterHosts.put("nl","streams.streams.nl");
        
        String result = null;
        
        while(!filterHosts.containsKey(userhost)) {
            int point = userhost.indexOf(".");
            if(point==-1) {
                if(filterHosts.containsKey("")) {
                    result = (String)filterHosts.get("");
                    break;
                } else {
                    log.error("Please specify a default provider.");
                    return null;
                }
            }
            userhost = userhost.substring(point+1);
        }
        result = (String)filterHosts.get(userhost);
        StringTokenizer st = new StringTokenizer(result,",");
        Vector providers = new Vector();
        
        while(st.hasMoreTokens()) {
            String hostname = (String)st.nextElement();
            
            MMObjectNode node = null;
            Enumeration e = mediaproviders.elements();
            while(e.hasMoreElements()) {
                node = (MMObjectNode) e.nextElement();
                if(hostname.equals(node.getStringValue("rooturl"))) {
                    providers.addElement(node);
                }
                
            }
        }
        return providers;
    }
    
}
