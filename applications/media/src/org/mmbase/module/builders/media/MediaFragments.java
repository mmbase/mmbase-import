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
 * already depricated and will be removed in new versions.
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
    private MediaSourceFilter mediaSourceFilter = null;
    
    // The media source builder
    private MediaSources mediaSourceBuilder = null;
    
    // Is the mediafragment builders already init ?
    private static boolean initDone=false;
    
    // depricated, for downwards compatability
    private static Hashtable classification = null;
    
    public MediaFragments() {
    }
    
    public boolean init() {
        if(initDone) return super.init();
        initDone=true;
        
        boolean result = super.init();
               
        // Retrieve a reference to the MediaSource builder
        mediaSourceBuilder = (MediaSources) mmb.getMMObject("mediasources");
        if(mediaSourceBuilder==null) {
            log.error("Builder mediasources is not loaded.");
        } else {
            log.debug("The builder mediasources is retrieved.");
        }
        
        mediaSourceFilter = new MediaSourceFilter(this, mediaSourceBuilder);
        
        // depricated for downwards compatibility
        //retrieveClassificationInfo();
        
        return result;
    }
    
    /**
     * For downloads compatibility reasons, the first version of the mediafragment builder
     * will contain the classification field. This field will contain numbers that are 
     * resolved using the lookup builder. This construction, using classification in 
     * mediafragment, was used for speeding up listings. 
     * @depricated
     */
    private void retrieveClassificationInfo() {
        
        MMObjectBuilder lookup = (MMObjectBuilder) mmb.getMMObject("lookup");
        if(lookup==null) {
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
            log.debug("classification uses: "+index+" -> "+value);
            classification.put(index,value);
        }
        return;
    }
    
    /**
     * create some virtual extra fields.
     * @param node the mediapart
     * @param field the virtual field
     * @return the information of the virtual field
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals("showurl")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
            return getUrl(node, new Hashtable());
        } else if (field.equals("showurl")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
            return getLongUrl(node, new Hashtable());
        } else if (field.equals("contenttype")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
            return getContentType(node, new Hashtable());
        } else if (field.equals("showlength")) {
            return ""+calculateLength(node);
        } else {
            return super.getValue( node, field );
        }
    }
    
    /**
     * calculate the length of a mediafragment
     * @param node the mediafragment
     * @return length in milliseconds
     */
    private long calculateLength(MMObjectNode node) {
        long start = node.getLongValue("start");
        long stop = node.getLongValue("stop");
        long length = node.getLongValue("length");
        
        if(stop!=0) {
            return stop-start;
        } else if(length!=0) {
            return length-start;
        }
        log.debug("length cannot be evaluated, no stoptime and no length");
        return 0;
    }
    
    /**
     * will show the title in the editors
     * @param node the mediapart node
     * @return the title of the mediapart
     */
    public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("title");
        return(str);
    }
    
    /**
     * retrieves the url with URI information of the mediasource that matches best.
     * (e.g. pnm://www.mmbase.org/test/test.ra?start+10:10.2&title=Ikeol
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the url of the audio file
     */
    private String getLongUrl(MMObjectNode mediaFragment, Hashtable info) {
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
    private String getUrl(MMObjectNode mediaFragment, Hashtable info) {
        log.debug("Getting url");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);
        if(mediaSource==null) {
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
    private MMObjectNode filterMediaSource(MMObjectNode mediaFragment, Hashtable info) {
        
        MMObjectNode mediaSource = mediaSourceFilter.filterMediaSource(mediaFragment, info);
        if(mediaSource==null) {
            log.error("No matching media source found by media fragment ("+mediaFragment.getIntValue("number")+")");
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
    public boolean isSubFragment(MMObjectNode mediafragment) {
        int mediacount = mediafragment.getRelationCount("mediasources");
        
        return (mediacount==0 && mediafragment.getRelationCount("mediafragments")>0);
    }
    
    /**
     * find the mediafragment of which the given mediafragment is a part.
     * @param mediafragment sub media fragment
     * @return the parent media fragment
     */
    public MMObjectNode getParentFragment(MMObjectNode mediafragment) {
        Enumeration e = mediafragment.getRelatedNodes("mediafragments").elements();
        
        if(!e.hasMoreElements()) {
            log.error("Cannot find parent media fragment");
        } else {
            return (MMObjectNode)e.nextElement();
        }
        return null;
    }
    
    /**
     * get all mediasources belonging to this mediafragment
     * @param mediafragment the mediafragment
     * @return All mediasources related to given mediafragment
     */
    public Vector getMediaSources(MMObjectNode mediafragment) {
        log.debug("Get mediasources mediafragment "+mediafragment.getNumber());
        while(isSubFragment(mediafragment)) {
            log.debug("mediafragment "+mediafragment.getNumber()+ " is a subfragment");
            mediafragment = getParentFragment(mediafragment);
        }
        Vector mediasources = mediafragment.getRelatedNodes("mediasources");
        log.debug("Mediafragment contains "+mediasources.size()+" mediasources");
        
        return mediasources;
    }
    
    /**
     * used by the editors
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        return node.getStringValue(field);
    }
    
    /**
     * Removes related media sources.
     * @param number objectnumber of the media fragment.
     * @return true if remove was succesful, false otherwise.
     */
    public boolean removeMediaSources(int number) {
         return false;
    }
    
    /**
     * Replace all for frontend code
     * Replace commands available are GETURL (gets mediafile url for an objectnumber),
     * from cache or not depending on builder property.
     * @param sp the scanpage
     * @param sp the stringtokenizer reference with the replace command.
     * @return the result value of the replace command or null.
     */
    public String replace(scanpage sp,StringTokenizer command) {
        /*
        if (command.hasMoreTokens()) {
            String token=command.nextToken();
            // debug("replace: The nextToken = "+token);
            if (token.equals("GETURL")) {
                int number=0;
                int userSpeed=getMinSpeed();
                int userChannels=getMinChannels();
                if (command.hasMoreTokens()) number=getNumberParam(command.nextToken());
                if (command.hasMoreTokens()) userSpeed=getSpeedParam(command.nextToken());
                if (command.hasMoreTokens()) userChannels=getChannelsParam(command.nextToken());
                if (number!=-1) {
                    String url = null;
                    if (urlCaching)
                        url = getUrlFromCache(sp,number,userSpeed,userChannels);
                    else
                        url = getUrl(sp,number,userSpeed,userChannels);
                    if (log.isDebugEnabled()) {
                        log.debug("replace: GETURL returns: " + url);
                    }
                    return url;
                } else {
                    log.error("getUrl: No objectnumber defined.");
                    return null;
                }
            } else if (token.equals("GETURLNOCACHE")) {
                if (log.isDebugEnabled()) {
                    log.debug("replace: Command is GETURLNOCACHE getting url directly.");
                }
                int number=0;
                int userSpeed=getMinSpeed();
                int userChannels=getMinChannels();
                if (command.hasMoreTokens()) number=getNumberParam(command.nextToken());
                if (command.hasMoreTokens()) userSpeed=getSpeedParam(command.nextToken());
                if (command.hasMoreTokens()) userChannels=getChannelsParam(command.nextToken());
                if (number!=-1) {
                    String url = null;
                    url = getUrl(sp,number,userSpeed,userChannels);
                    if(log.isDebugEnabled()) {
                        log.debug("replace: GETURLNOCACHE returns: " + url);
                    }
                    return url;
                } else {
                    log.error("getUrl: No objectnumber defined.");
                    return null;
                }
            } else {
                log.error("replace: Unknown command: "+token);
                return "ERROR: Unknown command: "+token;
            }
        }
        log.info("replace: No command defined.");
         */
        return "No command defined";
    }
    /**
     * Called when a node was changed on a local server.
     * @param machine Name of the node that was changed.
     * @param number the object number of the node that was changed.
     * @param builder the buildername of the object that was changed
     * @param ctype the node changed type
     * @return true, always
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        if (log.isDebugEnabled()) {
            log.debug("nodeLocalChanged("+machine+","+number + "," + builder + "," + ctype + ") ctype:" + ctype);
        }
        return true;
    }
    
    /**
     * Called when a node was changed by a remote server.
     * @param machine Name of the node that was changed.
     * @param number the object number of the node that was changed.
     * @param builder the buildername of the object that was changed
     * @param ctype the node changed type
     * @return true, always
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        if (log.isDebugEnabled()) {
            log.debug("nodeRemoteChanged("+machine+","+number + "," + builder + "," + ctype + ") ctype:" + ctype);
        }
        return true;
    }
}
