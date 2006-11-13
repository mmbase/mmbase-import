package org.mmbase.applications.friendlylink;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Converts ugly 'index.jsp?nr345' links to pages in 'userfriendly' hierarchical links 
 * (like '/news/NiceWeather.html' ending f.e.) based on the title of a page and visa versa.
 * This is a site specific utility class, the real work is done in UrlConverter and UrlFilter 
 * using UrlCache to cache the generated and real URL's.
 * UrlCache depends on the MMBase OSCache application.
 * 
 * @author    André van Toly
 * @version   $Id: PagesUtil.java,v 1.1 2006-11-13 22:16:06 andre Exp $
 */
public class PagesUtil {

    private static final Logger log = Logging.getLoggerInstance(PagesUtil.class);
    
	public static String SEPARATOR = "/";
	public final static String PAGE_EXTENSION = ".html";	// page ext that should be appended
	public static String PAGE_PARAM = "nr";
  	
//  	private Cloud cloud;
//  	private HttpServletRequest request;
  	private String pagenr;
  	private boolean convert;
/*  	
  	public void setCloud(Cloud c) {
  	    cloud = c;
  	}
  	
  	public void setHttpservletrequest(HttpServletRequest req) {
  	    request = req;
  	}
*/  	
  	public void setPagenr(String nr) {
  	    pagenr = nr;
  	}
  	
  	public void setConvert(boolean c) {
  	    convert = c;
  	}
  	  	
    /**
      * Creates an url to print in a page.
      * The url consists of:
      * 1. contextpath
      * 2. pages.title's of previous nodes 
      * 3. pages.title of current page node
      * 
      * Returns an url with 'pages.template' or related template
      * and the page with the page parameter when 'convert' is false.
      *
      * @param cloud    the MMBase cloud
      * @param request  HttpServletRequest that is needed
      * @param pagenr	the MMBase nodenumber of a page
      * @param convert  if we want to convert the url or not
      * @return url		link to a template or a 'userfriendly' link
      */
    public String createPageUrl(Cloud cloud, HttpServletRequest request, String pagenr, Boolean convert) {
        StringBuffer url = new StringBuffer();
        //String contextpath = request.getContextPath();
        
        // creates the jspUrl like: /template.jsp?nr=234 
        url.append(SEPARATOR);
        url.append( getPageTemplate(cloud, pagenr) );
        url.append("?").append(PAGE_PARAM).append("=").append(pagenr);
        
        if (convert) {	// boolean to check if we really want to convert urls
            url = new StringBuffer();   // overwrite
            url.append( createFriendlyLink(cloud, request, pagenr) );
        }
        
        if (log.isDebugEnabled()) log.debug("returning link: " + url.toString());
        return url.toString();
    }
    
    /**
     * Creates the 'userfriendly' link
     *
     * @param cloud     MMBase cloud
     * @param pagenr    Nodenumber of a page
     * @return the link
     */
    public String createFriendlyLink(Cloud cloud, HttpServletRequest request, String pagenr) {
        UrlCache cache = UrlConverter.getCache();
        StringBuffer url = new StringBuffer();
        String contextpath = request.getContextPath();
        
        url.append(contextpath).append(SEPARATOR);
        url.append(getPageTemplate(cloud, pagenr));
        url.append("?").append(PAGE_PARAM).append("=").append(pagenr);
    
        String jspUrl = url.toString();
        String userUrl = "";
        if (cache.hasURLEntry(jspUrl)) {
            userUrl = cache.getURLEntry(jspUrl);    // Get from cache
            if (log.isDebugEnabled()) log.debug("Processed from url cache: " + userUrl);
             
            url = new StringBuffer(userUrl);    // overwrite
        } else {    // create the userUrl
            userUrl = pagesPathToRoot(cloud, request, pagenr);
            
            // remove homepage (= ROOT) from url
            if (userUrl.indexOf(SEPARATOR) > 0) {
                if (log.isDebugEnabled()) log.debug("Remove home from userUrl");
                userUrl = userUrl.substring(userUrl.indexOf(SEPARATOR) + 1, userUrl.length());
            }
            
            url = new StringBuffer();           // overwrite
            url.append(contextpath).append(SEPARATOR).append(userUrl);
            url.append(PAGE_EXTENSION);    // .html
            cache.putURLEntry(jspUrl, url.toString());
            cache.putJSPEntry(url.toString(), jspUrl);
        }
        if (log.isDebugEnabled()) log.debug("Created 'userfriendly' link: " + url.toString());
        return url.toString();
    }
   
    /**
     * Creates a path to the root with all titles of pages
     *
     * @param   cloud   The MMBase cloud
     * @param   request HttpServletRequest
     * @param   pagenr  Contains the nodes pagenr
     * @return  a 'userfriendly' path
     */
    public String pagesPathToRoot(Cloud cloud, HttpServletRequest request, String pagenr) {
        StringBuffer path = new StringBuffer();
        Identifier transformer = new Identifier();
        
        Node pageNode = cloud.getNode(pagenr);
        List pathList = listPagesToRoot(pageNode);
        for (Iterator i = pathList.iterator(); i.hasNext();) {
            Node pn = (Node)i.next();
            
            String pagetitle = pn.getStringValue("title");
            pagetitle = transformer.transform(pagetitle);   // transform
            pagetitle = pagetitle.toLowerCase();
            path.append(pagetitle);
            
            if (i.hasNext()) path.append(SEPARATOR);
        }
        
        if (log.isDebugEnabled()) log.debug("path to root is: " + path.toString());
        return path.toString();
    }
    
    /**
     * Finds a pages' parentpages in a site, presumes posrel relation between pages.
     * Walks from the current page to the root page.
     *
     * @param   page    An MMBase node to get parentpage from
     * @return  A list with pages nodes
     */
    public List listPagesToRoot(Node page) {
        List pageList = new ArrayList();
        NodeList parentList = page.getRelatedNodes("pages", "posrel", "SOURCE");
        while (parentList.size() != 0) {
            Node parent = (Node) parentList.get(0);
            pageList.add(parent);
            parentList = parent.getRelatedNodes("pages", "posrel", "SOURCE");
        }
        pageList.add(0, page);  // add the page we started with at the beginning
        Collections.reverse(pageList);
        return pageList;
    }

   /**
     * Returns the template related to a page or when the page contains a field
     * named 'template' it returns that one.
     *
     * @param cloud		current cloud
     * @param pagenr	Nodenumber of a page
     * @return String	template url pointing to a JSP page
     */
    public static String getPageTemplate(Cloud cloud, String pagenr) {
        if (pagenr != null && !pagenr.equals("") && !pagenr.equals("-1")) {
        	NodeManager pnm = cloud.getNodeManager("pages");
        	Node pageNode = cloud.getNode(pagenr);
        	if (pnm.hasField("template")) {
        		if (pageNode.getStringValue("template") != null 
        		  && !pageNode.getStringValue("template").equals("")) {
        			return pageNode.getStringValue("template");
        		} else {
        			log.error("Field 'template' is empty");
        			return("/");	// send to homepage
        		}
        	} else {
	        	NodeList templateList = pageNode.getRelatedNodes("templates", "related", "DESTINATION");
    	     	if (templateList.size() == 1) {
        	    	return templateList.getNode(0).getStringValue("url");
         		}
         	}
      	}
      	return "nix? error?";
    }
    
    /**
     * Converts nicelink to a jsp with parameters, if not found in cache and if possible
     *
     * @param   nicelink the 'friendlylink' we try to find a page for
     * @param   params Request parameters
     * @return  a technical (jsp)url
     */
    public static String friendlyLinkToJsp(String contextpath, String nicelink, String params) {
        StringBuffer jspurl = new StringBuffer();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        UrlCache cache = UrlConverter.getCache();
        
        String title = "";
        title = nicelink.substring(nicelink.lastIndexOf("/"), nicelink.length() - 5);
        if (log.isDebugEnabled()) log.debug("title: " + title);
        
        NodeManager nm = cloud.getNodeManager("pages");
        NodeList nl = nm.getList("title = '" + title + "'", null, null);
        if (nl.size() > 0) {
            Node n = nl.getNode(0);
            String number = n.getStringValue("number");
            if (log.isDebugEnabled()) {
                log.debug("Found a node with number '" + number + "' having this friendlylink as a 'title'." );
            }
            jspurl = new StringBuffer();
            jspurl.append(contextpath).append(SEPARATOR);
            jspurl.append(getPageTemplate(cloud, number));
            jspurl.append("?").append(PAGE_PARAM).append("=").append(number);
            
            // cache.putURLEntry(jspUrl, url.toString());
            // cache.putJSPEntry(url.toString(), jspUrl);
            cache.putURLEntry(jspurl.toString(), nicelink);
            cache.putJSPEntry(nicelink, jspurl.toString());
            
        } else {
            if (log.isDebugEnabled()) log.debug("No match found.");
        }
        
        if (params != null) jspurl.append("&").append(params);
        return jspurl.toString();
    }
    
    /**
     * Lists all cache entries, not just the cache entries of this nodetype
     * but all entries in UrlCache, by calling the UrlCache#toString().
     *
     * @return  all cache entries
     */
    public String showCache() {
        UrlCache cache = UrlConverter.getCache();
        return cache.toString();
    }
    
    public String strFunction() {
        return "He, hello!";
    }    
    
    public String stringFunction() {
        return "He, hello!" + pagenr;
    }    
}
