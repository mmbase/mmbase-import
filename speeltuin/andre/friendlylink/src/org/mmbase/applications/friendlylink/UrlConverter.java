package org.mmbase.applications.friendlylink;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMBaseContext;

/**
 * Converts urls using the utility classes provided per nodetype and the caches
 *
 * TODO: Create a method to be called at initialization of webapp to fill the cache with links
 * 
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public class UrlConverter {

    private static final Logger log = Logging.getLoggerInstance(UrlConverter.class);
	
	public static String URL_CACHE = "url_cache";
	
	/**
	 * Gets and initiaties the cache to store nicelinks, it uses MMBaseContext to get
	 * the ServletContext
	 *
	 * @return UrlCache
	 */
	protected static UrlCache getCache() {
	    MMBaseContext mc = new MMBaseContext();
	    ServletContext app = mc.getServletContext();
	    UrlCache cache = (UrlCache) app.getAttribute(URL_CACHE);
	    if (cache == null) {
	        cache = new UrlCache();
	        app.setAttribute(URL_CACHE, cache);
	    }
	    return cache;
	}
    
    protected Map<String, FriendlyLink> getFlinks() {
        Map<String, FriendlyLink> flinks = new HashMap<String, FriendlyLink>();
        flinks = UrlFilter.getFriendlylinks();
        return flinks;
    }
    
    /**
     * Called by UrlFilter passing the HttpServletRequest with the 
     * 'userfriendly' URL to be converted to a technical URL.
     *
     * @param  HttpServletRequest
     * @return the converted technical URL
     */
    public static String convertUrl(HttpServletRequest request) {
        String contextpath = request.getContextPath();
        String url = getPath(request);
        String params = request.getQueryString();
        return convertUrl(contextpath, url, params);
    }

    /**
    * Converts a 'userfriendly' URL into technical URL.
    *
    * @param contextpath the webapp context
    * @param nicelink    contains the URL to be converted
    * @param params      contains the requestparameters of the URL
    * @return            the converted URL or the original URL if it could not be converted or found.
    */
	public static String convertUrl(String contextpath, String nicelink, String params) {
	    // params is to remove the found page parameter from the string
	    if (log.isDebugEnabled()) log.debug("Converting friendlylink '" + nicelink + "' with params '" + params + "' and context: " + contextpath);
	    
	    StringBuffer jspurl = new StringBuffer();
	    // jspurl.append(contextpath).append(nicelink);
	    jspurl.append(nicelink);
        if (params != null) jspurl.append("&amp;").append(params);
	    
	    UrlCache cache = getCache();
	    String link = nicelink;
	    if (log.isDebugEnabled()) log.debug("Checking if nicelink '" + link +"' lives in the cache.");
	    if ( cache.hasURLEntry(link) ) {
	        jspurl = new StringBuffer( cache.getJSPEntry(link) );  // get its jspUrl
	        if (params != null) jspurl.append("&").append(params);
	        
	        if (log.isDebugEnabled()) log.debug("URL was cached");
    	    return jspurl.toString();
	    } else {    
	        // TODO: find the correct jsp for 'friendlylink', delegate to ...Util or other depending on nodetype
	        if (log.isDebugEnabled()) {
	            log.debug("nicelink '" + link + "' not in cache, try searching for friendlylink in ...");
	        }
	        //String url = PagesUtil.friendlyLinkToJsp(contextpath, link, params);
	        
	        String url = "404.jsp";
	        
	        Map<String, FriendlyLink> flinks = UrlFilter.getFriendlylinks();
	        Set<String> keys = flinks.keySet();           // Get the set of keys held by the Map
            Iterator<String> i = keys.iterator();
            while(i.hasNext()) {
                String name = i.next();
    	        log.debug("FriendlyLink: " + name);
    	        
    	        FriendlyLink flink = flinks.get(name);
    	        if (url.equals("404.jsp")) {
    	            url = flink.convertToJsp(link, params);
    	        }
            }
	        if (log.isDebugEnabled()) log.debug("Returning '" + url + "'");

	        return url;
	    }
	    
	}
	
	/**
	 * Gets the path from a request
	 *
	 * @param  request The request
	 * @return path of the request or info left about path or null when nothing is found
	 */
	protected static String getPath(HttpServletRequest request) {
	   String path = request.getServletPath();
	   return path != null ? path : request.getPathInfo();
	}


   /** 
	 * To test the methods
	 *
	 * @param arg1    the first parameter
	 * @param arg2    the second parameter
	 * @return        <code>true</code> if something,  
	 *                <code>false</code> otherwise.
	 */
   public static void main(String args[]) {
      String testurl = "/start/geschiedenis.html";
      String context = "";
      if (!args[0].equals("")) testurl = args[0];
      if (!args[1].equals("")) context = args[1];
      System.out.println("Testing this URL :" + context + testurl);
      System.out.println("... converted into :" + convertUrl(context, testurl, ""));
   }

}
