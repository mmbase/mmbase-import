package org.mmbase.applications.friendlylink;

import java.util.*;
import org.w3c.dom.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;

/**
 * Converts ugly technical links to friendlier links and converts them back to the original
 * technical url.
 *
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Id: Pages.java,v 1.8 2007-03-16 23:18:00 andre Exp $
 */
public class Pages extends FriendlyLink {

    private static final Logger log = Logging.getLoggerInstance(Pages.class);
    
    /* Configurable parameters in '/utils/friendlylinks.xml' */
    public final static Parameter[] PARAMS = new Parameter[] {
        new Parameter("separator", String.class, "/"),
        new Parameter("template", String.class, "anders.jsp"),
        new Parameter("parameter", String.class, "nrs"),
        new Parameter("extension", String.class)
    };

    protected Parameter[] getParameterDefinition() {
        return PARAMS;
    }

    /* 
      Statics for creating links 
      (no way to configure this? except by function parameters maybe?)
    */
    public static String SEPARATOR = "/";
    public static String PAGE_PARAM = "nr";
    public static String TEMPLATE = "index.jsp";    // default template
            
    /**
     * Configure method parses a DOM element passed by UrlFilter with the configuration
     * that is specific for this type of friendlylink
     *
     * @param  element  DOM element friendlylink from 'friendlylinks.xml' 
     */
    protected void configure(Element element) {
        log.service("Configuring " + this);
        
        Element descrElement = (Element) element.getElementsByTagName("description").item(0);
        String description = DocumentReader.getNodeTextValue(descrElement);
        log.debug("Found the description: " + description);
        
        Map<String, String> params = new HashMap<String, String>(); /* String -> String */
        org.w3c.dom.NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                if (childElement.getLocalName().equals("parameter")) {
                	String name = childElement.getAttribute("name");
                	String value = DocumentReader.getNodeTextValue(childElement);
                	log.debug("Found parameter name '" + name + "' and value '" + value + "'");
                	if (params.put(name, value) != null) {
                		log.error("Parameter '" + name + "' is defined more than once in " + org.mmbase.util.xml.XMLWriter.write(element, true));
                	}
                }
            }
        }
        Parameters flinkParams = getParameters();
        flinkParams.setAll(params);
        // log.debug("parameters.getString() hier: " + parameters.getString("template"));
    }
    
    /**
     * Creates the url to print in a page
     *
     * @param   cloud   An MMBase cloud
     * @param   request HTTP servlet request
     * @param   page  Nodenumber of the page
     * @param   convert To convert or not
     * @return  a 'userfriendly' link
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, String page, Boolean convert) {
        org.mmbase.bridge.Node pageNode = cloud.getNode(page);    // get real nodenr for alias
        page = pageNode.getStringValue("number");
        
        StringBuffer url = new StringBuffer();
        url.append(getPageTemplate(cloud, TEMPLATE, page)).append("?").append(PAGE_PARAM).append("=").append(page);
        
        if (convert.booleanValue() == true) {
            url = new StringBuffer();   // overwrite
            url.append( makeFriendlyLink(cloud, request, page) );
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
        
        flink.append(SEPARATOR).append(getPageTemplate(cloud, TEMPLATE, pagenr )).append("?");
        flink.append(PAGE_PARAM).append("=").append(pagenr);
        
        String jspUrl = flink.toString();
        String friendlyUrl = "";
        if (cache.hasJSPEntry(jspUrl)) {
            friendlyUrl = cache.getURLEntry(jspUrl);    // Get from cache
            flink = new StringBuffer(friendlyUrl);
            if (log.isDebugEnabled()) log.debug("Processed from url cache: " + friendlyUrl);
        } else {    // create the friendlyUrl
            friendlyUrl = getRootPath(cloud, pagenr);
            
            flink = new StringBuffer();         // overwrite
            flink.append(SEPARATOR).append(friendlyUrl);
            
            // TODO: check if this friendlylink already exists in cache and change it?
            if (cache.hasURLEntry(flink.toString())) {
                log.warn("flink '" + flink.toString() + "' already in cache, appending nodenr '_" + pagenr + "'");
                
                flink = new StringBuffer();			// overwrite again
            	flink.append(friendlyUrl).append("_").append(pagenr);
            	//if (!"".equals(".html")) flink.append(".html");       // appending .html at the end
            }
            
            //flink.insert(0, "/");   // should append / at beginning !
            cache.putURLEntry(jspUrl, flink.toString());
            cache.putJSPEntry(flink.toString(), jspUrl);
            
            if (log.isDebugEnabled()) log.debug("Created 'userfriendly' link: " + flink.toString());
        }
        
        return flink.toString();
    }
    
    /**
     * Path to homepage in sitemap based on field 'title', starting at pagenr
     *
     * @param  cloud MMBase cloud
     * @param  pagenr
     * @return a path f.e. like /nieuws/artikelen/
     */
    public String getRootPath(Cloud cloud, String pagenr) {
        StringBuffer path = new StringBuffer();
        Identifier transformer = new Identifier();
        org.mmbase.bridge.Node pageNode = cloud.getNode(pagenr);
        
        List pagesList = listPagesToRoot(pageNode);
        for (Iterator i = pagesList.iterator(); i.hasNext();) {
            org.mmbase.bridge.Node pn = (org.mmbase.bridge.Node) i.next();
            
            String pagetitle = pn.getStringValue("title");
            pagetitle = transformer.transform(pagetitle);
            pagetitle = pagetitle.toLowerCase();
            
            // don't put home in the url
            if (!pagetitle.equals("home") && !pagetitle.equals("homepage")) {
                path.append(pagetitle);
                if (i.hasNext()) path.append(SEPARATOR);
            }
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
        pageList.add(0, page);  // add our page at the beginning
        Collections.reverse(pageList);
        return pageList;
    }
    
    /**
     * Returns the (jsp) template, in this case the field 'template' of the page node or
     * a related node of type 'templates' in which it looks for the 'url' field. 
     * Visitors get send to the default template if none is found.
     *
     * @param   cloud       MMBase cloud
     * @param   template    default template if none is found
     * @param   pagenr      nodenumber
     * @return  template url
     */
    public String getPageTemplate(Cloud cloud, String template, String pagenr) {
        if (pagenr != null && !pagenr.equals("") && !pagenr.equals("-1")) {
            NodeManager pnm = cloud.getNodeManager("pages");
            org.mmbase.bridge.Node pageNode = cloud.getNode(pagenr);
            if (pnm.hasField("template")) {
                if (pageNode.getStringValue("template") != null && !pageNode.getStringValue("template").equals("")) {
                    return pageNode.getStringValue("template");
                } else {
                    log.error("Field 'template' is empty");
                    return template;
                }
            } else {
                org.mmbase.bridge.NodeList templateList = pageNode.getRelatedNodes("templates", "related", "DESTINATION");
                if (templateList.size() == 1) {
                    return templateList.getNode(0).getStringValue("url");
                } else {
            		return template;
                }
            }
        } else {
            return template;
        }
    }
    
    /**
     * Creates the url to a JSP page (or any other technical url) from a friendlylink.
     * This one presumes the path consists of pagetitles.
     * Returns an empty string when no match is found.
     *
     * @param   flink   the friendlylink to convert
     * @param   params  parameters in request
     * @return  technical link
     */
    public String convertToJsp(String flink, String params) {
        String template = parameters.getString("template");
        if (log.isDebugEnabled()) log.debug("Trying to find a technical url for '" + flink + "'");

        StringBuffer jspurl = new StringBuffer();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        UrlCache cache = UrlConverter.getCache();
        
        StringTokenizer st = new StringTokenizer(flink, "/");
        String title = "";
        int tokens = st.countTokens();
        while (st.hasMoreTokens()) {
            title = st.nextToken();
            // log.debug("token '" + title + "' of " + tokens + " tokens");
        }
        log.debug("trying the last token '" + title + "'");
        
        if (title.indexOf(".html") > -1) title = title.substring(0, title.indexOf(".html"));
    
        // make a title in which _ are replaced (back) with spaces
        String alttitle = title.replace("_", " ");
        
        NodeManager nm = cloud.getNodeManager("pages");
        org.mmbase.bridge.NodeList nl = nm.getList("LOWER(title) = '" + title + 
                                                    "' OR LOWER(title) = '" + alttitle + "'", null, null);
        if (nl.size() > 0) {
            org.mmbase.bridge.Node n = nl.getNode(0);
            String number = n.getStringValue("number");
            
            if (log.isDebugEnabled()) {
                log.debug("Found a node with number '" + number + "' having this friendlylink as a title." );
            }
            
            // TODO: checken met getRootPath(cloud, pagenr)
            /*
            String rootpath = getRootPath(cloud, number);
            log.debug("rootpath '" + rootpath + "'");
            if (rootpath.indexOf(title) > -1) {
                log.warn("!! rootpath '" + rootpath + "'");
            }
            */
            jspurl = new StringBuffer();
            
            jspurl.append(SEPARATOR).append(getPageTemplate(cloud, template, number));
            jspurl.append("?").append(PAGE_PARAM).append("=").append(number);
            
            cache.putURLEntry(jspurl.toString(), flink);
            cache.putJSPEntry(flink, jspurl.toString());

            if (params != null) jspurl.append("&").append(params);
            return jspurl.toString();

        } else {
            if (log.isDebugEnabled()) log.debug("No match found.");
            return "";
        }
    }
    
    
    /**
     * Lists all cache entries, not just the cache entries of this nodetype
     * but all entries in UrlCache, by calling UrlCache#toString().
     *
     * @return  all cache entries
     */
    public String showCache() {
        UrlCache cache = UrlConverter.getCache();
        return cache.toString();
    }
    
}
