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
import javax.servlet.http.HttpServletRequest;


/**
 * The MediaFragment object specifies a piece of media. This can be audio, or video.
 * A mediafragment contains a title, description of the media and also information about
 * the source. A mediapart will have relations with mediasources which are the actual
 * files in different formats (mp3, real, etc.)
 *
 * INFO:
 * classification stuff is removed. This was available for audioparts but not
 * for videoparts (that is using a classification relation). Probabaly it is better
 * to use an extra classification object. In the way you use a category object.
 *
 * Caching will be handled in caching package, and will be implemented in the end.
 *
 * There is some extra functionality in the old Videopart builder that is not
 * integrated yet in this mediapart builder.
 *
 * Add functionality for mediapart -> mediapart -> raws. This is done in the VPRO
 * audio/video builder by making a source EXCERPT, we will solve this with a relation
 * from one audiopart to another one.
 *
 * This builder is work in progress,
 * Please if you have more comments add them here.
 */

public class MediaFragments extends MMObjectBuilder {
    
    // logging
    private static Logger log = Logging.getLoggerInstance(MediaFragments.class.getName());
      
    // This filter is able to find the best mediasource by a mediafragment.
    private MediaSourceFilter mediaSourceFilter = null;
    
    // The media source builder
    private MediaSources mediaSourceBuilder = null;
    
    public MediaFragments() {
    }
    
    public boolean init() {
        boolean result = super.init();
        
        
        // Retrieve a reference to the MediaSource builder
               mediaSourceBuilder = (MediaSources) mmb.getMMObject("mediasources");
        if(mediaSourceBuilder==null) {
            log.error("Builder mediasources is not loaded.");
        } else {
            log.debug("The builder mediasources is retrieved.");
        }
        
        mediaSourceFilter = new MediaSourceFilter(this, mediaSourceBuilder);
        
        return result;
    }
    
    /**
     * create some virtual extra fields.
     * @param node the mediapart
     * @param field the virtual field
     * @return the information of the virtual field
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals("showlength")) {
            return ""+calculateLength(node);
        } else if (field.equals("urlresult")) {
            return getUrl(node.getNumber(), null, 0, 0);
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
     * get an url for the requested media.
     *
     * @param mediafragment the number of the media fragment wanted
     * @param request the HttpRequest of the user
     * @param wantedspeed the requested speed of the user
     * @param wantedchannels the request channels of the user
     * @return the Url for the requested media
     */
    public String getUrl(int mediafragmentnr, HttpServletRequest request, int wantedspeed, int wantedchannels) {
        log.debug("Getting url for mediafragment "+mediafragmentnr);
        // Which MediaSource is the best one to use ?
        MMObjectNode mediaFragment = getNode(mediafragmentnr);
        MMObjectNode mediaSource = mediaSourceFilter.filterMediaSource(mediaFragment, request, wantedspeed, wantedchannels);
        log.debug("Selected mediasource = "+mediaSource.getNumber());
        return mediaSourceBuilder.getUrl(mediaFragment, mediaSource, request, wantedspeed, wantedchannels);
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
        return "not implemented (11)";
    }
    
   
           
    /**
     * add rawmedia object
     */
    /*
    public void addRawMedia(RawAudios bul,int id, int status, int format, int speed, int channels) {
        MMObjectNode node=bul.getNewNode("system");
        node.setValue("id",id);
        node.setValue("status",status);
        node.setValue("format",format);
        node.setValue("speed",speed);
        node.setValue("channels",channels);
        bul.insert("system",node);
    }
     */
        
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
