/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.builders.media;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.media.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.servlet.http.*;

/**
 * The MediaFragment object specifies a piece of media. This can be audio, or video.
 * A mediafragment contains a title, description of the media and also information about
 * the source. A mediapart will have relations with mediasources which are the actual
 * files in different formats (mp3, real, etc.)
 *
 * INFO:
 * The classification stuff is added for backwards compatibility for the VPRO. This is
 * already deprecated and will be removed in new versions.
 *
 * Caching will be handled in caching package, and will be implemented in the end.
 *
 * Add functionality for mediapart -> mediapart -> raws. This is done in the VPRO
 * audio/video builder by making a source EXCERPT, we will solve this with a relation
 * from one audiopart to another one.
 *
 * This builder is work in progress,
 * Please if you have more comments add them here.
 *
 * @author Rob Vermeulen (VPRO)
 */

public class MediaFragments extends MMObjectBuilder {
    
    // logging
    private static Logger log = Logging.getLoggerInstance(MediaFragments.class.getName());
    
    // This filter is able to find the best mediasource by a mediafragment.
    private MediaSourceFilter mediaSourceFilter;
    
    // The media source builder
    private MediaSources      mediaSourceBuilder;
    
    // Is the mediafragment builders already inited ?
    // this class is used for several builders (mediafragments and descendants)

    private boolean           initDone           = false;
    
  
    public boolean init() {
        if(initDone) return super.init();
        log.service("Init of media-fragments");
       
        initDone = true;       
        boolean result = super.init();
              
        // Retrieve a reference to the MediaSource builder
        mediaSourceBuilder = (MediaSources) mmb.getMMObject("mediasources");

        
        if(mediaSourceBuilder == null) {
            log.error("Builder mediasources is not loaded.");
        } else {
            log.debug("The builder mediasources is retrieved.");
        }
        
        mediaSourceFilter = new MediaSourceFilter(this, mediaSourceBuilder);        

        // deprecated:
        retrieveClassificationInfo();

        return result;
    }
        
    /**
     * 
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + args + ") on" + node);
        }
        if (function.equals("info")) {
            List empty = new Vector();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put("showurl", "(<format>) ");
            info.put("longurl", "(<format>) ");
            info.put("urlresult", "(<??>) ");
            info.put("gui", "(state|channels|codec|format|..) Gui representation of this object.");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }            
        } else if (args != null && args.size() > 0) {
            if (function.equals("showurl")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
                return getURL(node, new Hashtable());
            } else if (function.equals("longurl")) {
                // hashtable can be filled with speed/channel/ or other info to evalute the url.
                return getLongURL(node, new Hashtable());
            } else if (function.equals("contenttype")) {
                // hashtable can be filled with speed/channel/ or other info to evalute the url.
                return getContentType(node, new Hashtable());
            } else if (function.equals("showlength")) {
                return ""+calculateLength(node);
            }
        }
        log.debug("Function not matched in mediafragments");
        return super.executeFunction(node, function, args);
    }

    
    /**
     * calculate the length of a mediafragment
     * @param node the mediafragment
     * @return length in milliseconds
     */
    private long calculateLength(MMObjectNode node) {
        long start  = node.getLongValue("start");
        long stop   = node.getLongValue("stop");
        long length = node.getLongValue("length");
        
        if(stop != 0) {
            return stop - start;
        } else if (length != 0) {
            return length - start;
        }
        log.debug("length cannot be evaluated, no stoptime and no length");
        return 0;
    }
    
    /**
     * Will show the title in the editors
     * @param node the mediapart node
     * @return the title of the mediapart
     */
    public String getGUIIndicator(MMObjectNode node) {
        String url = node.getStringValue("showurl");
        if (! "".equals(url)) {
            return "<a href=\"" + url + "\" alt=\"\" >" + node.getStringValue("title") + "</a>";
        } else {
            return "[" + node.getStringValue("title") + "]";
        }        
    }
    
    /**
     * retrieves the url with URI information of the mediasource that matches best.
     * (e.g. pnm://www.mmbase.org/test/test.ra?start+10:10.2&title=Ikeol
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the url of the audio file
     */
    private String getLongURL(MMObjectNode mediaFragment, Map info) {
        log.debug("Getting longurl");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);
        if(mediaSource==null) {
            log.error("Cannot determine longurl");
            return "";
        }
        return mediaSourceBuilder.getLongUrl(mediaFragment, mediaSource, info);
    }
      
    /**
     * retrieves the url (e.g. pnm://www.mmbase.org/music/test.ra) of the 
     * mediasource that matches best.
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the url of the audio file
     */
    private String getURL(MMObjectNode mediaFragment, Map info) {
        log.debug("Getting url");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);
        if(mediaSource == null) {
            log.error("Cannot determine url");
            return "";
        }
        return mediaSourceBuilder.getUrl(mediaSource, info);
    }
    
    /**
     * Find the most appropriate media source
     * @param mediafragment a media fragment
     * @param info additional information provider by a user
     * @return the most appropriate media source
     */
    private MMObjectNode filterMediaSource(MMObjectNode mediaFragment, Map info) {
        if (log.isDebugEnabled()) {
            log.debug("mediasourcefilter " + mediaSourceFilter + " info " + info);
        }
        MMObjectNode mediaSource = mediaSourceFilter.filterMediaSource(mediaFragment, info);
        if(mediaSource == null) {
            log.error("No matching media source found by media fragment (" + mediaFragment.getIntValue("number") + ")");
        }
        return mediaSource;
    }
    
    /**
     * returns the content type of the mediasource that matches best.
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the content type
     */
    private String getContentType(MMObjectNode mediaFragment, Hashtable info) {
        log.debug("Getting content type");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);        
        if(mediaSource==null) {
            log.error("Cannot determine content type");
            return "";
        }
        return mediaSourceBuilder.getContentType(mediaSource);
    }
    
    /**
     * if a mediafragment is coupled to another mediafragment instead of being directly
     * coupled to mediasources, the mediafragment is a subfragment.
     * @return true if the mediafragment is coupled to another fragment, false otherwise.
     */
    protected boolean isSubFragment(MMObjectNode mediafragment) {
        int mediacount = mediafragment.getRelationCount("mediasources");        
        return (mediacount == 0 && mediafragment.getRelationCount("mediafragments") > 0);
    }
    
    /**
     * find the mediafragment of which the given mediafragment is a part.
     * @param mediafragment sub media fragment
     * @return the parent media fragment
     */
    protected MMObjectNode getParentFragment(MMObjectNode mediafragment) {
        Enumeration e = mediafragment.getRelatedNodes("mediafragments").elements();
        
        if(!e.hasMoreElements()) {
            log.error("Cannot find parent media fragment");
        } else {
            return (MMObjectNode)e.nextElement();
        }
        return null;
    }
    
    /**
     * Get all mediasources belonging to this mediafragment
     * @param mediafragment the mediafragment
     * @return All mediasources related to given mediafragment
     */
    protected List getMediaSources(MMObjectNode mediafragment) {
        if (log.isDebugEnabled()) log.debug("Get mediasources mediafragment "+mediafragment.getNumber());
        while(isSubFragment(mediafragment)) {
            if (log.isDebugEnabled()) log.debug("mediafragment "+mediafragment.getNumber()+ " is a subfragment");
            mediafragment = getParentFragment(mediafragment);
        }
        Vector mediasources = mediafragment.getRelatedNodes("mediasources");
        if (log.isDebugEnabled()) log.debug("Mediafragment contains "+mediasources.size()+" mediasources");
        
        return mediasources;
    }
    
    
    /**
     * Removes related media sources. This can be used by automatic recording VWMS's.
     * 
     * @param mediaFragment The MMObjectNode
     */
    public  void removeMediaSources(MMObjectNode mediafragment) {
        List ms = getMediaSources(mediafragment);
        for (Iterator mediaSources = ms.iterator() ;mediaSources.hasNext();) {
            MMObjectNode mediaSourceNode = (MMObjectNode) mediaSources.next();
            mediaSourceBuilder.removeRelations(mediaSourceNode);
            mediaSourceBuilder.removeNode(mediaSourceNode);
        }
    }


    // --------------------------------------------------------------------------------
    // 

        
    // deprecated, for downwards compatability
    private Map               classification     = null;

     /**
      * For backwards compatibility reasons, the first version of the mediafragment builder
      * will contain the classification field. This field will contain numbers that are 
      * resolved using the lookup builder. This construction, using classification in 
      * mediafragment, was used for speeding up listings. 
      * @deprecated
      */
     private void retrieveClassificationInfo() {

         MMObjectBuilder lookup = (MMObjectBuilder) mmb.getMMObject("lookup");
         if(lookup == null) {
             log.debug("Downwards compatible classification code not used.");
             return;
         }
         log.debug("Using downwards compatible classification code.");
         classification =  new Hashtable();
         MMObjectNode fn = getNode(mmb.getTypeDef().getIntValue("mediafragments"));
         Vector nodes = fn.getRelatedNodes("lookup");
         for (Enumeration e = nodes.elements();e.hasMoreElements();) {
             MMObjectNode node = (MMObjectNode)e.nextElement();
             String index = node.getStringValue("index");
             String value = node.getStringValue("value");
             log.debug("classification uses: " + index + " -> " + value);
             classification.put(index,value);
         }
         return;
     }
     



}
