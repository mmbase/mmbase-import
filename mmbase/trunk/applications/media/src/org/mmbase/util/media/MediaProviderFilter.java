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
import org.mmbase.util.XMLBasicReader;

import java.util.*;
import java.io.File;

import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

/**
 * The MediaProviderFilter will find out which MediaProvider (audio/video server)
 * is most appropriate to handle the request of the user.
 *
 * The appropriate mediaprovider will be found by passing the mediaproviders of the
 * mediasource through a set of mediaprovider filters. These filters can be specified
 * in the mediaproviderfilter configuration file.
 *
 * Two standard filters are provided:
 * 1) hostFilter, will determine providers according to the hostname of the user.
 * 2) preferedFilter, will sort the providers from most prefered to least prefered.
 *
 * @author Rob Vermeulen (VPRO)
 *
 */
public class MediaProviderFilter {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
    
    // A reference to the MediaSource class.
    private MediaSource mediasourcebuilder = null;
    
    // Contains information about which host will result in which provider
    private static Hashtable hostFilter = new Hashtable();
    
    // Contains a list of prefered providers (from most to least prefered)
    private Vector preferFilter = new Vector();
    
    // This chain contains the filters for the mediaproviders
    private static Vector filterChain = new Vector();
    
    // contains the external filters
    private Hashtable externFilters = new Hashtable();
    
    
    /**
     * construct the MediaProviderFilter
     */
    public MediaProviderFilter(MediaSource ms) {
        mediasourcebuilder = ms;
        readConfiguration();
    }
    
    /**
     * read the MediaProviderFilter configuration
     */
    private void readConfiguration() {
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), "mediaproviderfilter.xml");
        if (! configFile.exists()) {
            log.warn("Configuration file for mediaproviderfilter " + configFile + " does not exist");
            return;
        }
        
        XMLBasicReader reader = new XMLBasicReader(configFile.toString());
        
        // reading filterchain information
        for(Enumeration e = reader.getChildElements("mediaproviderfilter.chain","filter");e.hasMoreElements();) {
            Element chainelement=(Element)e.nextElement();
            String chainvalue = reader.getElementValue(chainelement);
            log.debug("chain = "+chainvalue);
            if(!chainvalue.equals("sortProviders") && !chainvalue.equals("filterOnHost")) {
                
                try {
                    Class newclass=Class.forName(chainvalue);
                    externFilters.put(chainvalue,(MediaProviderFilterInterface)newclass.newInstance());
                } catch (Exception exception) {
                    log.error("Cannot load MediaProviderFilter "+chainvalue+"\n"+exception);
                }
                
                log.error("read extern chain - "+chainvalue);
            } else {
                log.debug("read standard chain - "+chainvalue);
            }
            filterChain.addElement(chainvalue);
        }
        
        // reading hostFilter information
        for( Enumeration e = reader.getChildElements("mediaproviderfilter.filterOnHost","hostfilter");e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String key = reader.getElementAttributeValue(n3,"user");
            String value = reader.getElementAttributeValue(n3,"provider");
            log.debug("read host filter element "+key+" -> "+value);
            hostFilter.put(key,value);
        }
        
        // reading preferFilter information
        for( Enumeration e = reader.getChildElements("mediaproviderfilter.sortProviders","provider");e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String host = reader.getElementAttributeValue(n3,"host");
            log.error("read prefered host "+host);
            preferFilter.addElement(host);
            
        }
    }
    
    /**
     * filter the most appropriate mediaprovider. This method is invoked from MediaSource. 
     * The mediaprovider will be found by passing a list of mediaproviders through a chain
     * of mediaprovider filters.
     */
    public MMObjectNode filterMediaProvider(MMObjectNode mediasource, HttpServletRequest request, int wantedspeed, int wantedchannels) {
        Vector mediaproviders = mediasourcebuilder.getMediaProviders(mediasource);
        
        // passing the mediaproviders through al the filters
        for (Enumeration e = filterChain.elements();e.hasMoreElements();) {
            String filter = (String)e.nextElement();
            if(filter.equals("sortProviders")) {
                mediaproviders = sortMediaProviders(mediaproviders);
            } else if(filter.equals("filterOnHost")) {
                mediaproviders = filterHostOnDomain("userinfo",mediaproviders);
            } else {
                MediaProviderFilterInterface mpfi = (MediaProviderFilterInterface)externFilters.get(filter);
                mediaproviders = mpfi.filterMediaProvider(mediasource, mediaproviders, request, wantedspeed, wantedchannels);
            }
        }
        
        return  takeOneMediaProvider(mediaproviders);
    }
    
    
    /**
     * take one mediaprovider. This method is used to just take one mediaprovider
     * of a list with appropriate media providers.
     * @param mediaproviders list of appropriate media providers
     * @return The mediaprovider that is going to handle the request
     */
    private MMObjectNode takeOneMediaProvider(Vector mediaproviders) {
        
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
        
        Vector sortedProviders = new Vector();
        
        Enumeration pp = preferFilter.elements();
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
        
        String result = null;
        
        while(!hostFilter.containsKey(userhost)) {
            int point = userhost.indexOf(".");
            if(point==-1) {
                if(hostFilter.containsKey("")) {
                    result = (String)hostFilter.get("");
                    break;
                } else {
                    log.error("Please specify a default provider.");
                    return null;
                }
            }
            userhost = userhost.substring(point+1);
        }
        result = (String)hostFilter.get(userhost);
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
