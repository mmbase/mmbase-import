/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.applications.media.builders;
import org.mmbase.applications.media.filters.MainFilter;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.*;
import java.net.URL;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.media.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.servlet.http.*;

/**
 * The MediaFragment builder describes a piece of media. This can be audio, or video.
 * A media fragment contains a title, description, and more information about the media fragment.
 * A media fragment will have relations with mediasources which are the actual
 * files in different formats (mp3, real, etc.)
 *
 * The classification, and replace methods are added for backwards compatibility.
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen (NOS)
 * @version $Id: MediaFragments.java,v 1.9 2003-02-17 18:06:16 michiel Exp $
 * @since MMBase-1.7
 */

public class MediaFragments extends MMObjectBuilder {
    
    // logging
    private static Logger log = Logging.getLoggerInstance(MediaFragments.class.getName());

    // let the compiler check for typo's:
    public static final String FUNCTION_URLS        = "urls";
    public static final String FUNCTION_FILTEREDURLS  = "filteredurls";
    public static final String FUNCTION_URL         = "url";
    public static final String FUNCTION_PARENT      = "parent";    
    public static final String FUNCTION_PARENTS     = "parents";    
    public static final String FUNCTION_ROOT        = "root";    
    public static final String FUNCTION_SUBFRAGMENT = "issubfragment";
    public static final String FUNCTION_SUBFRAGMENTS = "subfragments";
    public static final String FUNCTION_AVAILABLE   = "available";
    public static final String FUNCTION_FORMAT      = "format";

    
    // This filter is able to find the best mediasource by a mediafragment.
    // private  static MainFilter mediaSourceFilter = null;
    
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
            info.put("format",  arguments);
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
            info.put(FUNCTION_URLS, "(info) A list of all possible URLs to this fragment (Really URLComposer.URLComposer's)");
            info.put(FUNCTION_PARENT, "() Returns the 'parent' MMObjectNode's number of the parent or null");
            info.put(FUNCTION_SUBFRAGMENT, "() Wether this fragment is a subfragment (returns a Boolean)");
            info.put(FUNCTION_SUBFRAGMENTS, "() Returns a stack of parents  (Stack of MMObjectNode)");
            info.put(FUNCTION_AVAILABLE, "() Wether this fragment is 'available'. A fragment can be unaivable when there is a related publishtimes which defines it 'unpublished'");
            // info.put("urlresult", "(<??>) ");
            info.put("gui", "(state|channels|codec|format|..) Gui representation of this object.");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }            
        } else if (FUNCTION_URLS.equals(function)) {
            return getURLs(node, translateURLArguments(args, null), null);
        } else if (FUNCTION_FILTEREDURLS.equals(function)) {
            return getFilteredURLs(node, translateURLArguments(args, null));
        } else if (FUNCTION_SUBFRAGMENT.equals(function)) {
            return new Boolean(isSubFragment(node));
        } else if (FUNCTION_PARENT.equals(function)) {
            MMObjectNode parent = getParentFragment(node);
            return parent;
        } else if (FUNCTION_PARENTS.equals(function)) {
            return getParentFragments(node);
        } else if (FUNCTION_ROOT.equals(function)) {
            return "" + getRootFragment(node).getNumber();
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
            if (url.startsWith("/")) {
                url = MMBaseContext.getHtmlRootUrlPath() + url.substring(1);
            }
            return "<a href=\"" + url + "\" alt=\"\" >" + title + "</a>";
        } else {
            return "[" + title + "]";
        }        
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if ("start".equals(field) || "stop".equals(field)) {
            StringBuffer buf = new StringBuffer();
            org.mmbase.applications.media.urlcomposers.RealURLComposer.appendTime(node.getIntValue(field), buf);
            return buf.toString();
        }
        return super.getGUIIndicator(field, node);
    }


    /** 
     * Returns a List of all possible (unfiltered) URLComposer's for this Fragment.
     * A list of arguments can be supplied, which is currently unused (but should not be null).
     * It could contain a Map with preferences, or other information about the client.
     *
     * @author mm
     */
    protected List getURLs(MMObjectNode fragment, Map info, List urls) {
        if (urls == null) urls = new ArrayList();

        Iterator i = getSources(fragment).iterator();
        while (i.hasNext()) {
            MMObjectNode source = (MMObjectNode) i.next();
            MediaSources bul    = (MediaSources) source.parent; // cast everytime, because it can be extended
            bul.getURLs(source, fragment, info, urls);
        }
        return urls;        
    }   

    protected List getFilteredURLs(MMObjectNode fragment, Map info) {
        log.debug("getfilteredurls");
        List urls =  getURLs(fragment, info, null);
        return MainFilter.getInstance().filter(urls);
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
        List urls = getFilteredURLs(fragment, info);
        if (urls.size() > 0) {
            return ((URLComposer) urls.get(0)).getURL();
        } else {
            return ""; //no sources 
        }
    }

    protected  String getFormat(MMObjectNode fragment, Map info)   {
        log.debug("Getting format of a fragment.");        
        List urls = getFilteredURLs(fragment, info);
        if (urls.size() > 0) {
            return ((URLComposer) urls.get(0)).getFormat().toString();
        } else {
            return ""; //no sources 
        }
    }
        
    /**
     * If a mediafragment is coupled to another mediafragment instead of being directly
     * coupled to mediasources, the mediafragment is a subfragment.
     * @return true if the mediafragment is coupled to another fragment, false otherwise.
     */
    public boolean isSubFragment(MMObjectNode mediafragment) {
        int mediacount = mediafragment.getRelationCount("mediasources");        
        return (mediacount == 0 && mediafragment.getRelationCount("mediafragments") > 0);
    }
    
    public Stack getParentFragments(MMObjectNode fragment) {
        Stack result = new Stack();
        result.push(fragment);
        int thisNumber = fragment.getNumber();
        log.debug("Finding parent of node " + thisNumber);
        int role = mmb.getRelDef().getNumberByName("posrel");
        org.mmbase.module.corebuilders.InsRel insrel =  mmb.getRelDef().getBuilder(role);
        Enumeration e = insrel.getRelations(thisNumber, mmb.getBuilder("mediafragments").getObjectType(), role);
        while (e.hasMoreElements()) {
            MMObjectNode relation = (MMObjectNode) e.nextElement();
            log.debug("Checking relation " + relation);
            if (relation.getIntValue("dnumber") == thisNumber) { // yes, found a parent node
                log.debug("Yes, found parent of " + thisNumber + " " + relation.getIntValue("snumber"));
                result.push(getNode(relation.getIntValue("snumber")));
            }            
        }
        
        role = mmb.getRelDef().getNumberByName("parent");
        insrel =  mmb.getRelDef().getBuilder(role);
        e = insrel.getRelations(thisNumber, mmb.getBuilder("mediafragments").getObjectType(), role);
        while (e.hasMoreElements()) {
            MMObjectNode relation = (MMObjectNode) e.nextElement();
            log.debug("Checking relation " + relation);
            if (relation.getIntValue("snumber") == thisNumber) { // yes, found a parent node
                log.debug("Yes, found base=parent of " + thisNumber + " " + relation.getIntValue("dnumber"));
                result.push(getNode(relation.getIntValue("dnumber")));
            }            
        }
        return result;
    }


    /**
     * Find the mediafragment of which the given mediafragment is a part.
     * @param mediafragment sub media fragment
     * @return The parent media fragment or null if it has not.
     */
    protected MMObjectNode getParentFragment(MMObjectNode fragment) {
        Stack s = getParentFragments(fragment);
        return (MMObjectNode) s.peek();
    }

    /**
     * Returns the fragment which has no parent fragments. This fragment should have the mediasources related to it.
     *
     * @return An MMObjectNode.
     */

    public MMObjectNode getRootFragment(MMObjectNode fragment) {
        MMObjectNode temp;
        log.debug("Finding root of " + fragment.getNumber());
        List path = new ArrayList(); // to avoid circular references (in case of wrong links)
        do {
            temp = fragment;
            path.add(temp);
            fragment = getParentFragment(fragment);
        } while (fragment != null && (!path.contains(fragment)));
        return temp;
    }
    
    /**
     * Get all mediasources belonging to this mediafragment
     * @param mediafragment the mediafragment
     * @return All mediasources related to given mediafragment
     * @scope  should be protected
     */
    public List getSources(MMObjectNode fragment) {
        if (log.isDebugEnabled()) log.debug("Get mediasources mediafragment " + fragment.getNumber());
        MMObjectNode root  = getRootFragment(fragment);
        List mediasources =  root.getRelatedNodes("mediasources");
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
    // These methods are added to be backwards compatible. 

        
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
     
    /**
     * Replace all for frontend code
     * Replace commands available are GETURL (gets mediafile url for an objectnumber),
     * @param sp the scanpage
     * @param command the stringtokenizer reference with the replace command.
     * @return the result value of the replace command or null.
     */
    public String replace(scanpage sp,StringTokenizer command) {
        if (command.hasMoreTokens()) {
            String token=command.nextToken();

	    log.debug("scan - "+token);
            if (token.equals("GETURL")) {
                Integer number=null, userSpeed=null, userChannels=null;
                if (command.hasMoreTokens()) number=new Integer(command.nextToken());
                if (command.hasMoreTokens()) userSpeed=new Integer(command.nextToken());
                if (command.hasMoreTokens()) userChannels=new Integer(command.nextToken());
                if (number!=null) {
			MMObjectNode media = getNode(number.intValue());
			Map info = new HashMap();
			if(userSpeed!=null) {
				info.put("speed",""+userSpeed);
			}
			if(userChannels!=null) {
				info.put("channels",""+userChannels);
			}
                    String url = getURL(media, info);
		    log.debug("resolving url = "+url);
                    return url;
                } else {
			log.error("No mediafragment specified"); 
                    return null;
                }
            } 
	    log.error("only command GETURL is supported");
	    return "only command GETURL is supported";
        }
        log.error("No commands defined.");
        return "No commands defined.";
    }
}
