package org.mmbase.applications.friendlylink;

import java.util.*;
import org.w3c.dom.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Converts ugly links
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public class Pages extends FriendlyLink {

    private static final Logger log = Logging.getLoggerInstance(Pages.class);
    
    private final LocalizedString description = null;

    public static String SEPARATOR = "/";
    public final static String PAGE_EXTENSION = ".html";    // page ext that should be appended
    public static String PAGE_PARAM = "nr";
    
    /**
     * Configure method parses a DOM element passed by UrlFilter with the configuration
     * that is specific for this type of friendlylink
     *
     * @param  element  A DOM element from 'friendlylinks.xml' 
     *
     */
    protected void configure(Element element) {
        log.service("Configuring " + this);
        
        Element descrElement = (Element) element.getElementsByTagName("description").item(0);
        String description = org.mmbase.util.xml.DocumentReader.getNodeTextValue(descrElement);
        
        log.debug("Found the description: " + description);
    }

    /**
     * Creates the url to print in a page
     *
     * @param   cloud   An MMBase cloud
     * @param   request HTTP servlet request
     * @param   pagenr  Nodenumber of the page
     * @param   convert To convert or not
     * @return  a 'userfriendly' link
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, String pagenr, Boolean convert) {
        StringBuffer url = new StringBuffer();
        url.append(SEPARATOR);
        url.append(getPageTemplate(cloud, pagenr)).append("?").append(PAGE_PARAM).append("=").append(pagenr);
        
        if (convert) {  // boolean to check if we really want to convert urls
            url = new StringBuffer();   // overwrite
            url.append( makeFriendlyLink(cloud, request, pagenr) );
        }
        
        if (log.isDebugEnabled()) log.debug("returning link: " + url.toString());
        return url.toString();
    }
    
    /**
     * Makes the actual friendlylink
     *
     * @param   cloud   An MMBase cloud
     * @param   request HTTP servlet request
     * @param   pagenr  Nodenumber of the page
     * @param   convert To convert or not
     * @return  a friendlylink
     */
    public String makeFriendlyLink(Cloud cloud, HttpServletRequest request, String pagenr) {
        UrlCache cache = UrlConverter.getCache();
        StringBuffer flink = new StringBuffer();
        // String contextpath = request.getContextPath();
        
        flink.append(getPageTemplate(cloud, pagenr)).append("?").append(PAGE_PARAM).append("=").append(pagenr);
        
        
        String jspUrl = flink.toString();
        String friendlyUrl = "";
        if (cache.hasURLEntry(jspUrl)) {
            friendlyUrl = cache.getURLEntry(jspUrl);    // Get from cache
            if (log.isDebugEnabled()) log.debug("Processed from url cache: " + friendlyUrl);
            
            flink = new StringBuffer(friendlyUrl);    // overwrite
        } else {    // create the friendlyUrl
            friendlyUrl = getRootPath(cloud, pagenr);
            
            // remove homepage (= ROOT) from url
            if (friendlyUrl.indexOf(SEPARATOR) > 0) {
                friendlyUrl = friendlyUrl.substring(friendlyUrl.indexOf(SEPARATOR) + 1, friendlyUrl.length());
                if (log.isDebugEnabled()) log.debug("Removed '/home' (rootpage) from: " + friendlyUrl);
            }
            
            flink = new StringBuffer();         // overwrite
            // flink.append(contextpath).append(SEPARATOR).append(friendlyUrl);
            flink.append(SEPARATOR).append(friendlyUrl);                        // NO CONTEXT !!
            flink.append(PAGE_EXTENSION);       // appending .html at the end
            
            // TODO: check if this friendlylink already exists in cache and change it?
            if (cache.hasURLEntry(flink.toString())) {
                log.warn("flink '" + flink.toString() + "' already in cache, appending nodenr '_" + pagenr + "'");
                
                flink = new StringBuffer();			// overwrite again
            	flink.append(SEPARATOR).append(friendlyUrl).append("_").append(pagenr);
            	flink.append(PAGE_EXTENSION);       // appending .html at the end
            }
            
            cache.putURLEntry(jspUrl, flink.toString());
            cache.putJSPEntry(flink.toString(), jspUrl);
            
            if (log.isDebugEnabled()) log.debug("Created 'userfriendly' link: " + flink.toString());
        }
        
        if (log.isDebugEnabled()) log.debug("Returning flink: " + flink.toString());
        return flink.toString();
    }
    
    /**
     * Returns the (jsp) template, in this case the field 'template' of the page node or
     * a related node of type 'templates' in which it looks for the 'url' field. 
     * Visitors get send back to the homepage (index.jsp) if no template is found.
     *
     * @param   cloud   MMBase cloud
     * @param   pagenr  nodenumber
     * @return  template url
     */
    public String getPageTemplate(Cloud cloud, String pagenr) {
        if (pagenr != null && !pagenr.equals("") && !pagenr.equals("-1")) {
            
            NodeManager pnm = cloud.getNodeManager("pages");
            org.mmbase.bridge.Node pageNode = cloud.getNode(pagenr);
            if (pnm.hasField("template")) {
                if (pageNode.getStringValue("template") != null 
                  && !pageNode.getStringValue("template").equals("")) {
                    return pageNode.getStringValue("template");
                } else {
                    log.error("Field 'template' is empty");
                    return("index.jsp");    // send to homepage
                }
            } else {
                org.mmbase.bridge.NodeList templateList = pageNode.getRelatedNodes("templates", "related", "DESTINATION");
                if (templateList.size() == 1) {
                    return templateList.getNode(0).getStringValue("url");
                } else {
            		return "index.jsp";
                }
            }
        } else {
            return "index.jsp";
        }
    }

    /**
     * path to root in sitemap based on field 'title', starting at pagenr
     *
     * @param  cloud MMBase cloud
     * @param  request
     * @param  pagenr
     * @return a path f.e. like /nieuws/artikelen/
     */
    public String getRootPath(Cloud cloud, String pagenr) {
        StringBuffer path = new StringBuffer();
        Identifier transformer = new Identifier();
        org.mmbase.bridge.Node pageNode = cloud.getNode(pagenr);
        List pathList = listPagesToRoot(pageNode);
        for (Iterator i = pathList.iterator(); i.hasNext();) {
            org.mmbase.bridge.Node pn = (org.mmbase.bridge.Node)i.next();
            
            String pagetitle = pn.getStringValue("title");
            pagetitle = transformer.transform(pagetitle);   // transform
            pagetitle = pagetitle.toLowerCase();
            path.append(pagetitle);
            
            if (i.hasNext()) path.append(SEPARATOR);
        }
        
        if (log.isDebugEnabled()) log.debug("Path to /: " + path.toString());
        return path.toString();
    }

    
    /**
     * Finds a pages' parentpages in a site, presumes posrel relation between pages.
     * Walks from the current page to the root page.
     *
     * @param   page    An MMBase node to get parentpage from
     * @return  A list with pages nodes
     */
    public List listPagesToRoot(org.mmbase.bridge.Node page) {
        List pageList = new ArrayList();
        org.mmbase.bridge.NodeList parentList = page.getRelatedNodes("pages", "posrel", "SOURCE");
        while (parentList.size() != 0) {
            org.mmbase.bridge.Node parent = (org.mmbase.bridge.Node) parentList.get(0);
            pageList.add(parent);
            parentList = parent.getRelatedNodes("pages", "posrel", "SOURCE");
        }
        pageList.add(0, page);  // add the page we started with at the beginning
        Collections.reverse(pageList);
        return pageList;
    }
    
    
    /**
     * Creates the url to a JSP page (or any other technical url) from a friendlylink.
     * This one presumes the path consists of pagetitles
     *
     * @param   flink   the friendlylink to convert
     * @param   params  parameters in request
     * @return  technical link
     */
    public String convertToJsp(String flink, String params) {
        StringBuffer jspurl = new StringBuffer();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        UrlCache cache = UrlConverter.getCache();
        
        String title = "";
        title = flink.substring(flink.lastIndexOf("/") + 1, flink.length() - 5);
        title = title.toLowerCase();
        if (log.isDebugEnabled()) log.debug("title: " + title);
        
        NodeManager nm = cloud.getNodeManager("pages");
        org.mmbase.bridge.NodeList nl = nm.getList("LOWER(title) = '" + title + "'", null, null);
        if (nl.size() > 0) {
            org.mmbase.bridge.Node n = nl.getNode(0);
            String number = n.getStringValue("number");
            if (log.isDebugEnabled()) {
                log.debug("Found a node with number '" + number + "' having this friendlylink as a 'title'." );
            }
            jspurl = new StringBuffer();
            
            //jspurl.append(contextpath).append(SEPARATOR);
            jspurl.append(getPageTemplate(cloud, number));
            jspurl.append("?").append(PAGE_PARAM).append("=").append(number);
            
            cache.putURLEntry(jspurl.toString(), flink);
            cache.putJSPEntry(flink, jspurl.toString());
            
        } else {
            if (log.isDebugEnabled()) log.debug("No match found.");
        }
        
        if (params != null) jspurl.append("?").append(params);
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
    
}
