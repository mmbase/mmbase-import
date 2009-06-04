/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.media.builders;

import org.mmbase.applications.media.filters.MainFilter;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.cache.URLCache;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The MediaFragment builder describes a piece of media. This can be audio, or video.
 * A media fragment contains a title, description, and more information about the media fragment.
 * A media fragment will have relations with mediasources which are the actual
 * files in different formats (mp3, real, etc.)
 *
 * The classification, and replace methods are added for backwards compatibility.
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class MediaFragments extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(MediaFragments.class);

    // let the compiler check for typo's:
    public static final String FUNCTION_URLS        = "urls";
    public static final String FUNCTION_FILTEREDURLS  = "filteredurls";
    public static final String FUNCTION_URL         = "url";
    public static final String FUNCTION_NUDEURL     = "nudeurl";
    public static final String FUNCTION_PARENTS     = "parents";
    public static final String FUNCTION_ROOT        = "root";
    public static final String FUNCTION_SUBFRAGMENT = "issubfragment";
    public static final String FUNCTION_SUBFRAGMENTS = "subfragments";
    public static final String FUNCTION_AVAILABLE   = "available";
    public static final String FUNCTION_FORMAT      = "format";
    public static final String FUNCTION_DURATION    = "duration";

    // parameter definitions (making use of reflection utitility for functions)
    public final static Parameter[] URLS_PARAMETERS          = { new Parameter("format",  List.class), new Parameter("bitrate", String.class), Parameter.REQUEST };
    public final static Parameter[] FILTEREDURLS_PARAMETERS  = URLS_PARAMETERS;
    public final static Parameter[] URL_PARAMETERS           = URLS_PARAMETERS;
    public final static Parameter[] NUDEURL_PARAMETERS       = URLS_PARAMETERS;
    public final static Parameter[] PARENTS_PARAMETERS       = {};
    public final static Parameter[] ROOT_PARAMETERS          = {};
    public final static Parameter[] ISSUBFRAGMENT_PARAMETERS = {};
    public final static Parameter[] SUBFRAGMENT_SPARAMETERS  = {};
    public final static Parameter[] AVAILABLE_PARAMETERS     = URLS_PARAMETERS;
    public final static Parameter[] FORMAT_PARAMETERS        = URLS_PARAMETERS;
    public final static Parameter[] DURATION_PARAMETERS      = {};


    // This filter is able to find the best mediasource by a mediafragment.
    // private  static MainFilter mediaSourceFilter = null;

    // Is the mediafragment builder already initialised?
    // this class is used for several builders (mediafragments and descendants)
    private static boolean           initDone           = false;

    private URLCache cache = URLCache.getCache();

    public boolean init() {
        if(initDone) {
        return super.init();
    }
        log.service("Init of Media Fragments builder");
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
    static protected Map<String, Object> translateURLArguments(List<?> arguments, Map<String, Object> info) {
        if (info == null) info = new HashMap<String, Object>();
        if (arguments != null) {
            if (arguments instanceof Parameters) {
                info.putAll(((Parameters) arguments).toMap());
            } else {
                info.put("format",  arguments);
            }
        }
        return info;
    }

    /**
     * {@inheritDoc}
     */
    protected Object executeFunction(MMObjectNode node, String function, List<?> args) {
        if (log.isDebugEnabled()) {
            log.debug("executeFunction  " + function + "(" + args + ") on " + node);
        }
        if (function.equals("info")) {
            List<Object> empty = new Vector<Object>();
            java.util.Map<String, String> info = (java.util.Map<String, String>) super.executeFunction(node, function, empty);
            info.put(FUNCTION_URL, "(<format>)  Returns the 'best' url for this fragment. Hashtable can be filled with speed/channel/ or other info to evalute the url.");
            info.put(FUNCTION_URLS, "(info) A list of all possible URLs to this fragment (Really URLComposer.URLComposer's)");
            info.put(FUNCTION_ROOT, "() Returns the 'parent' MMObjectNode's number of the parent or null");
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
            return getURLs(node, translateURLArguments(args, null), null,null);
        } else if (FUNCTION_FILTEREDURLS.equals(function)) {
            return getFilteredURLs(node, translateURLArguments(args, null),null);
        } else if (FUNCTION_SUBFRAGMENT.equals(function)) {
            return Boolean.valueOf(isSubFragment(node));
        } else if (FUNCTION_ROOT.equals(function)) {
            MMObjectNode parent = getRootFragment(node);
            return parent;
        } else if (FUNCTION_PARENTS.equals(function)) {
            return getParentFragments(node);
        } else if (FUNCTION_ROOT.equals(function)) {
            return "" + getRootFragment(node).getNumber();
        } else if (FUNCTION_AVAILABLE.equals(function)) {
            if (mmb.getBuilder("publishtimes") != null) {
                List<MMObjectNode> pt  = node.getRelatedNodes("publishtimes");
                if (pt.size() == 0) {
                    return Boolean.TRUE;
                } else {
                    MMObjectNode publishtime = pt.get(0);
                    int now   = (int) (System.currentTimeMillis() / 1000);
                    int begin = publishtime.getIntValue("begin");
                    int end   = publishtime.getIntValue("end");
                    Boolean available = Boolean.TRUE;
                    if (begin > 0 && now < begin) available = Boolean.FALSE;
                    if (end   > 0 && now > end)   available = Boolean.FALSE;
                    return available;
                }
            } else {
                return Boolean.TRUE;
            }
        } else if (FUNCTION_URL.equals(function)) {
            return getURL(node, translateURLArguments(args, null));
        } else if (FUNCTION_NUDEURL.equals(function)) {
            Map<String, Object> info = translateURLArguments(args, null);
            info.put("nude", "true");
            return getURL(getRootFragment(node), info);
        } else if (FUNCTION_FORMAT.equals(function)) {
            return getFormat(node, translateURLArguments(args, null));
        } else if (FUNCTION_DURATION.equals(function)) {
            StringBuffer buf = new StringBuffer();
            org.mmbase.applications.media.urlcomposers.RealURLComposer.appendTime(calculateLength(node), buf);
            return buf.toString();
        }
        log.debug("Function not matched in mediafragments");
        return super.executeFunction(node, function, args);
    }


    /**
     * Calculate the length of a mediafragment
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
     * @param node the mediafragment node
     * @return the title of the mediafragment
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
        if (getField(field).getGUIType().equals("relativetime")) { // must be delegated to a field-type implementation
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
     */
    protected List<URLComposer> getURLs(MMObjectNode fragment, Map<String, Object> info, List<URLComposer> urls, Set<MMObjectNode> cacheExpireObjects) {
        if (urls == null) urls = new ArrayList<URLComposer>();

        Iterator<MMObjectNode> i = getSources(fragment).iterator();
        while (i.hasNext()) {
            MMObjectNode source = i.next();
            MediaSources bul    = (MediaSources) source.getBuilder(); // cast everytime, because it can be extended
            bul.getURLs(source, fragment, info, urls, cacheExpireObjects);
        }
        return urls;
    }

    protected List<URLComposer> getFilteredURLs(MMObjectNode fragment, Map<String, Object> info, Set<MMObjectNode> cacheExpireObjects) {
        log.debug("getfilteredurls");
        List<URLComposer> urls =  getURLs(fragment, info, null,cacheExpireObjects);
        return MainFilter.getInstance().filter(urls);
    }


    /**
     * Retrieves the url of the mediasource that matches best.
     * (e.g. pnm://www.mmbase.org/music/test.ra)
     *
     * @param fragment the media fragment
     * @param info extra information (i.e. request, wanted bitrate, preferred format)
     * @return the url of the audio file
     */
    protected  String getURL(MMObjectNode fragment, Map<String, Object> info) {
        log.debug("Getting url of a fragment.");
        String key = URLCache.toKey(fragment, info);
        if(cache.containsKey(key)) {
            String url = cache.get(key);
            if (log.isDebugEnabled()) {
                log.debug("Cache hit, key = " + key);
                log.debug("Resolved url = " + url);
            }
            return url;
        } else {
            log.debug("No cache hit, key = " + key);
        }

        Set<MMObjectNode> cacheExpireObjects = new HashSet<MMObjectNode>();
        List<URLComposer> urls = getFilteredURLs(fragment, info, cacheExpireObjects);
        String result = "";
        if (urls.size() > 0) {
            result = urls.get(0).getURL();
        }
        if (log.isDebugEnabled()) {
            log.debug("Add to cache, key = " + key);
            log.debug("Resolved url = " + result);
        }
        // put result in cache
        cache.put(key, result, cacheExpireObjects);
        return result;
    }

    protected  String getFormat(MMObjectNode fragment, Map<String, Object> info)   {
        log.debug("Getting format of a fragment.");
        // XXX also cache this ?
        // XXX can be done in the same cache if we extend the key...
        List<URLComposer> urls = getFilteredURLs(fragment, info, null);
        if (urls.size() > 0) {
            return urls.get(0).getFormat().toString();
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

    /**
     * Adds a parent fragment to the Stack and returns true, or returns false.
     */
    protected boolean addParentFragment(Stack<MMObjectNode> fragments) {
        MMObjectNode fragment = fragments.peek();
        int role = mmb.getRelDef().getNumberByName("posrel");
        InsRel insrel =  mmb.getRelDef().getBuilder(role);
        Enumeration<MMObjectNode> e = insrel.getRelations(fragment.getNumber(), mmb.getBuilder("mediafragments").getObjectType(), role);
        while (e.hasMoreElements()) {
            MMObjectNode relation = e.nextElement();
            if (relation.getIntValue("dnumber") == fragment.getNumber()) { // yes, found a parent node
                if (log.isDebugEnabled()) {
                    log.debug("Yes, found parent of " + fragment.getNumber() + " " + relation.getIntValue("snumber"));
                }
                MMObjectNode parent = getNode(relation.getIntValue("snumber"));
                if (fragments.contains(parent)) {
                    log.warn("Circular fragment nesting detected " + fragments + " breaking infinite loop");
                    return false;
                }
                fragments.push(parent);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Stack with all parent fragments. Starts stacking from
     * this, so on top is the mediafragment with the sources, and on
     * the bottom is the fragment itself.
     */
    public Stack<MMObjectNode> getParentFragments(MMObjectNode fragment) {
        Stack<MMObjectNode> result = new Stack<MMObjectNode>();
        result.push(fragment);
        if (log.isDebugEnabled()) {
            log.debug("Finding parents of node " + fragment.getNumber());
        }
        while (addParentFragment(result));
        return result;
    }


    /**
     * Find the mediafragment of which the given mediafragment is a
     * part. This fragment is not a subfragment itself, and should be
     * linked to the actual sources.
     *
     * @param fragment sub media fragment
     * @return The parent media fragment or null if it has not.
     */
    public MMObjectNode getRootFragment(MMObjectNode fragment) {
        Stack<MMObjectNode> s = getParentFragments(fragment);
        return s.peek();
    }

    /**
     * Get all mediasources belonging to this mediafragment
     * (scope  should be protected)
     * @param fragment the mediafragment
     * @return All mediasources related to given mediafragment
     */
    public List<MMObjectNode> getSources(MMObjectNode fragment) {
        if (log.isDebugEnabled()) log.debug("Get mediasources mediafragment " + fragment.getNumber());
        MMObjectNode root  = getRootFragment(fragment);
        List<MMObjectNode> mediasources =  root.getRelatedNodes("mediasources");
        if (mediasources == null) {
            log.warn("Could not get related nodes of type mediasources");
        }
        if (log.isDebugEnabled()) log.debug("Mediafragment contains "+mediasources.size()+" mediasources");

        return mediasources;
    }


    /**
     * Removes related media sources. This can be used by automatic recording VWMS's.
     *
     * @param fragment The MMObjectNode
     */
    public  void removeSources(MMObjectNode fragment) {
        List<MMObjectNode> ms = getSources(fragment);
        for (MMObjectNode source : ms) {
            MMObjectBuilder parent = source.getBuilder();
            parent.removeRelations(source);
            parent.removeNode(source);
        }
    }

    // --------------------------------------------------------------------------------
    // These methods are added to be backwards compatible.

    private Map<String, String>               classification     = null;

     /**
      * For backwards compatibility reasons, the first version of the mediafragment builder
      * will contain the classification field. This field will contain numbers that are
      * resolved using the lookup builder. This construction, using classification in
      * mediafragment, was used for speeding up listings.
      * @deprecated
      */
     private void retrieveClassificationInfo() {

         MMObjectBuilder lookup = mmb.getMMObject("lookup");
         if(lookup == null) {
             log.debug("Downwards compatible classification code not used.");
             return;
         }
         log.debug("Using downwards compatible classification code.");
         classification =  new Hashtable<String, String>();
         MMObjectNode fn = getNode(mmb.getTypeDef().getIntValue("mediafragments"));
         Vector<MMObjectNode> nodes = fn.getRelatedNodes("lookup");
         for (MMObjectNode node : nodes) {
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
     * @param sp the PageInfo
     * @param command the stringtokenizer reference with the replace command.
     * @return the result value of the replace command or null.
     */
    public String replace(PageInfo sp,StringTokenizer command) {
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
                    if(!media.getBuilder().isExtensionOf(mmb.getBuilder("mediafragments"))) {
                        log.error("Number "+number+" is not a media/audio/video fragment "+media);
                        return "Number "+number+" is not a media/audio/video fragment "+media;
                    }
                    Map<String, Object> info = new HashMap<String, Object>();
                    if(userSpeed!=null) {
                        info.put("speed",""+userSpeed);
                    }
                    if(userChannels!=null) {
                        info.put("channels",""+userChannels);
                    }
                    return getURL(media, info);
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

    public Object getObjectValue(MMObjectNode node, String field) {
        if (field.equals("lengthsec")) {
            long val=node.getLongValue("length");
            return ""+val/1000;
        }
        return super.getObjectValue(node,field);
    }

    public boolean setValue(MMObjectNode node,String fieldname) {
        if (fieldname.equals("lengthsec")) {
            long val=node.getLongValue("lengthsec");
            log.info("store value in seconds: "+val);
            node.setValue("length",new Long(val*1000));
            node.storeValue("lengthsec",null);
            return false;
        }
        return super.setValue(node,fieldname);
    }

    /**
     * {@inheritDoc}
     *
     * Stack.contains is used, so make sure equal node are equal.
     */

    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        int n1 = o1.getNumber();
        int n2 = o2.getNumber();
        if (n1 > 0 && n2 > 0) { // both 'real' nodes.
            return n1 == n2;
        } else {
            String t1 = o1.getStringValue("_number");
            String t2 = o2.getStringValue("_number");
            if (t1 == null) {
                return n1 == n2 && t2 == null;
            } else {
                return n1 == n2 && t1.equals(t2);
            }
        }

    }

}
