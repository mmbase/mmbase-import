/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.builders.media;

import java.util.*;
import java.net.URL;
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
 * @author Michiel Meeuwissen
 * @version $Id: MediaFragments.java,v 1.25 2003-01-22 11:13:13 michiel Exp $
 * @since MMBase-1.7
 */

public class MediaFragments extends MMObjectBuilder {
    
    // logging
    private static Logger log = Logging.getLoggerInstance(MediaFragments.class.getName());

    // let the compiler check for typo's:
    public static final String FUNCTION_URLS        = "urls";
    public static final String FUNCTION_SORTEDURLS  = "sortedurls";
    public static final String FUNCTION_URL         = "url";
    public static final String FUNCTION_PARENT      = "parent";    
    public static final String FUNCTION_SUBFRAGMENT = "subfragment";
    public static final String FUNCTION_AVAILABLE   = "available";
    public static final String FUNCTION_FORMAT      = "format";

    
    // This filter is able to find the best mediasource by a mediafragment.
    // private  static MediaSourceFilter mediaSourceFilter = null;
    
   // Is the mediafragment builders already inited ?
    // this class is used for several builders (mediafragments and descendants)

    private static boolean           initDone           = false;
    
 
    public boolean init() {
        if(initDone) return super.init();
        log.service("Init of media-fragments");
        initDone = true;  // because of inheritance we do init-protections
        
        boolean result = super.init();       
        // deprecated:
        retrieveClassificationInfo();

        return result;
    }

    /**
     * Would something like this be feasible to translate a List to a Map?
     *
     */
    static protected Map translateURLArguments(List arguments, Map info) {
        if (info == null) info = new HashMap();
        if (arguments != null) {
            info.put("format", arguments);
        }
        return info;
    }
        
    /**
     * @author mm
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + args + ") on " + node);
        }
        if (function.equals("info")) {
            List empty = new Vector();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put(FUNCTION_URL, "(<format>)  Returns the 'best' url for this fragment. Hashtable can be filled with speed/channel/ or other info to evalute the url.");
            info.put("longurl", "(<format>) ");
            info.put(FUNCTION_URLS, "(info) A list of all possible URLs to this fragment (Really MediaURLComposer.ResponseInfo's)");
            info.put(FUNCTION_PARENT, "() Returns the 'parent' MMObjectNode of the parent or null");
            info.put(FUNCTION_SUBFRAGMENT, "() Wether this fragment is a subfragment (returns a Boolean)");
            info.put(FUNCTION_AVAILABLE, "() Wether this fragment is 'available'. A fragment can be unaivable when there is a related publishtimes which defines it 'unpublished'");
            // info.put("urlresult", "(<??>) ");
            info.put("gui", "(state|channels|codec|format|..) Gui representation of this object.");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }            
        } else if (FUNCTION_URLS.equals(function)) {
            return getURLs(node, translateURLArguments(args, null));
        } else if (FUNCTION_SORTEDURLS.equals(function)) {
            return getSortedURLs(node, translateURLArguments(args, null));
        } else if (FUNCTION_SUBFRAGMENT.equals(function)) {
            return new Boolean(isSubFragment(node));
        } else if (FUNCTION_PARENT.equals(function)) {
            return getParentFragment(node);
        } else if (FUNCTION_AVAILABLE.equals(function)) {
            List pt  = node.getRelatedNodes("publishtimes");
            if (pt.size() == 0) { 
                return Boolean.TRUE;
            } else {
                MMObjectNode publishtime = (MMObjectNode) pt.get(0);
                int now   = (int) (System.currentTimeMillis() / 1000);
                int begin = publishtime.getIntValue("begin");
                int end   = publishtime.getIntValue("end");
                Boolean available = Boolean.TRUE;
                if (begin > 0 && now < begin) available = Boolean.FALSE;
                if (end   > 0 && now > end)   available = Boolean.FALSE;
                return available;
            }
        } else if (FUNCTION_URL.equals(function)) {
            return getURL(node, translateURLArguments(args, null));
        } else if (FUNCTION_FORMAT.equals(function)) {
            return getFormat(node, translateURLArguments(args, null));
        } else if (function.equals("longurl")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
            // return getLongURL(node, new Hashtable());
        } else if (function.equals("contenttype")) {
            // hashtable can be filled with speed/channel/ or other info to evalute the url.
            //            return getContentType(node, new Hashtable());
        } else if (function.equals("showlength")) {
            return ""+calculateLength(node);
        }
        log.debug("Function not matched in mediafragments");
        return super.executeFunction(node, function, args);
    }

    
    /**
     * calculate the length of a mediafragment
     * @param node the mediafragment
     * @return length in milliseconds
     */
    protected long calculateLength(MMObjectNode node) {
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
     * Will show the title (clickable if possible)
     * @param node the mediapart node
     * @return the title of the mediapart
     */
    public String getGUIIndicator(MMObjectNode node) {
        String url = node.getFunctionValue(FUNCTION_URL, null).toString();
        String title = node.getStringValue("title");
        if ("".equals(title)) title = "***";
        if (! "".equals(url)) {
            return "<a href=\"" + url + "\" alt=\"\" >" + title + "</a>";
        } else {
            return "[" + title + "]";
        }        
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if ("start".equals(field) || "stop".equals(field)) {
            StringBuffer buf = new StringBuffer();
            FragmentResponseInfo.appendTime(node.getIntValue(field), buf);
            return buf.toString();
        }
        return super.getGUIIndicator(field, node);
    }

    
    /**
     * Retrieves the url with URI information of the mediasource that matches best.
     * (e.g. pnm://www.mmbase.org/test/test.ra?start+10:10.2&title=Ikeol
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the url of the audio file
     */
    /*
    protected String getLongURL(MMObjectNode mediaFragment, Map info) {
        log.debug("Getting longurl");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);
        if(mediaSource==null) {
            log.error("Cannot determine longurl");
            return "";
        }
        return mediaSourceBuilder.getLongUrl(mediaFragment, mediaSource, info);
    }
    */

    /** 
     * Returns a List of all possible (unfiltered) ResponseInfo's for this Fragment.
     * A list of arguments can be supplied, which is currently unused (but should not be null).
     * It could contain a Map with preferences, or other information about the client.
     *
     * @author mm
     */
    protected List getURLs(MMObjectNode fragment, Map info) {
        List result = new ArrayList();

        Iterator i = getSources(fragment).iterator();
        while (i.hasNext()) {
            MMObjectNode source = (MMObjectNode) i.next();
            MediaSources bul    = (MediaSources) source.parent; // cast everytime, because it can be extended
            List sourcesurls = bul.getURLs(source, fragment, info);
            if (sourcesurls.removeAll(result)) {
                log.debug("removed duplicates");
            }
            result.addAll(sourcesurls);
        }
        return result;        
    }   

    protected List getSortedURLs(MMObjectNode fragment, Map info) {
        log.debug("getsortedurls");
        List urls =  getURLs(fragment, info);
        return MediaSourceFilter.getInstance().filter(urls);
    }

      
    /** 
     * Retrieves the url (e.g. pnm://www.mmbase.org/music/test.ra) of the 
     * mediasource that matches best.
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, preferred format)
     * @return the url of the audio file
     */
    protected  String getURL(MMObjectNode fragment, Map info)   {
        log.debug("Getting url of a fragment.");        
        List urls = getSortedURLs(fragment, info);
        if (urls.size() > 0) {
            return ((ResponseInfo) urls.get(0)).getURL();
        } else {
            return ""; //no sources 
        }
    }

    protected  String getFormat(MMObjectNode fragment, Map info)   {
        log.debug("Getting format of a fragment.");        
        List urls = getSortedURLs(fragment, info);
        if (urls.size() > 0) {
            return ((ResponseInfo) urls.get(0)).getFormat().toString();
        } else {
            return ""; //no sources 
        }
    }
    


    /**
     * Find the most appropriate media source
     * @param mediafragment a media fragment
     * @param info additional information provider by a user
     * @return the most appropriate media source
     */
    /*
    private MMObjectNode filterMediaSource(MMObjectNode fragment, Map info) {
        List urls = getSortedURLs(fragment, info); 
        if(urls.size() == 0) {
            log.error("No matching media source found by media fragment (" + fragment.getIntValue("number") + ")");
            return null;
        }
        MMObjectNode mediaSource = (MMObjectNode) urls.get(0);
        return mediaSource;
    }   
    */
    /**
     * returns the content type of the mediasource that matches best.
     *
     * @param mediaFragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, etc.)
     * @return the content type
     */
    /*
    private String getContentType(MMObjectNode mediaFragment, Hashtable info) {
        log.debug("Getting content type");
        MMObjectNode mediaSource = filterMediaSource(mediaFragment, info);        
        if(mediaSource==null) {
            log.error("Cannot determine content type");
            return "";
        }
        return mediaSourceBuilder.getMimeType(mediaSource);
    }
    */
    
    /**
     * If a mediafragment is coupled to another mediafragment instead of being directly
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
     * @scope  should be protected
     */
    public List getSources(MMObjectNode mediafragment) {
        if (log.isDebugEnabled()) log.debug("Get mediasources mediafragment "+mediafragment.getNumber());
        // just to bu sure not to enter an infinite loop:
        Set parents = new HashSet();
        while((! parents.contains(mediafragment.getStringValue("number")))  && isSubFragment(mediafragment)) {
            parents.add(mediafragment.getStringValue("number"));
            if (log.isDebugEnabled()) log.debug("mediafragment "+mediafragment.getNumber()+ " is a subfragment");
            mediafragment = getParentFragment(mediafragment);
        }
        List mediasources = mediafragment.getRelatedNodes("mediasources");
        if (mediasources == null) {
            log.warn("Could not get related nodes of type mediasources");
        }
        if (log.isDebugEnabled()) log.debug("Mediafragment contains "+mediasources.size()+" mediasources");
        
        return mediasources;
    }
    
    
    /**
     * Removes related media sources. This can be used by automatic recording VWMS's.
     * 
     * @param mediaFragment The MMObjectNode
     */
    public  void removeSources(MMObjectNode mediafragment) {
        List ms = getSources(mediafragment);
        for (Iterator mediaSources = ms.iterator() ;mediaSources.hasNext();) {
            MMObjectNode source = (MMObjectNode) mediaSources.next();
            source.parent.removeRelations(source);
            source.parent.removeNode(source);
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
