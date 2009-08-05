package org.mmbase.applications.friendlylink;

import java.util.*;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.XMLWriter;

/**
 * Converts ugly technical links to friendlier links and converts them back to the original
 * technical url.
 *
 * @author Andr&eacute; van Toly &lt;andre@toly.nl&gt;
 * @version $Id: Pages.java,v 1.9 2007-04-30 20:53:12 andre Exp $
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
/*    public static String SEPARATOR = "/";
    public static String PAGE_PARAM = "nr";
    public static String TEMPLATE = "index.jsp";    // default template
*/            
    /**
     * Configure method parses a DOM element passed by UrlFilter with the configuration
     * that is specific for this type of friendlylink. 
     * Note: At present this works only for converting friendlylinks back to technical urls,
     *   the methods creating friendlylinks are configured in a functionset.
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
                		log.error("Parameter '" + name + "' is defined more than once in " + XMLWriter.write(element, true));
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
     * @param   page    Page node
     * @param   convert To convert or not
     * @return  a 'userfriendly' link
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, Node page, Boolean convert) {
        String url = getUrl(cloud, page);
        
        String pagenr = page.getStringValue("number");
        String template = parameters.getString("template");
        String page_param = parameters.getString("parameter");
        
        log.debug("cloud: " + cloud + " req: " + request + " pagenr: " + pagenr);
        
        log.debug("url: " + url);
        
        if (convert == true) {
            url = makeFriendlyLink(cloud, request, page);
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
    public String makeFriendlyLink(Cloud cloud, HttpServletRequest request, Node page) {
    	UrlCache cache = UrlConverter.getCache();
    	Identifier transformer = new Identifier();
        StringBuffer flink = new StringBuffer();
        String jsp = getUrl(cloud, page);
        
        String pagenr = page.getStringValue("number");
        String separator = parameters.getString("separator");
        String page_param = parameters.getString("parameter");
        
        if (cache.hasJSPEntry(jsp)) {
            String friendlyUrl = cache.getURLEntry(jsp);    // Get from cache
            flink.append(friendlyUrl);
            if (log.isDebugEnabled()) log.debug("Processed from url cache: " + friendlyUrl);
        } else {    // create the friendlyUrl
           
            String path = getRootPath(cloud, page);
            String title = page.getStringValue("title");
            title = transformer.transform(title);
            title = title.toLowerCase();
            
            if (!"".equals(path)) flink.append(separator).append(path);
            flink.append(separator).append(title);
            
            // TODO: check if this friendlylink already exists in cache and change it?
            if (cache.hasURLEntry(flink.toString())) {
                log.warn("flink '" + flink.toString() + "' already in cache, appending nodenr '/" + pagenr + "'");
                
            	flink.append(separator).append(pagenr);
            	//if (!"".equals(".html")) flink.append(".html");       // appending .html at the end
            }
            
            //flink.insert(0, "/");   // should append / at beginning !
            cache.putEntries(pagenr, jsp, flink.toString());
            
            if (log.isDebugEnabled()) log.debug("Created 'userfriendly' link: " + flink.toString());
        }
        
        return flink.toString();
    }
    
    /**
     * Path to homepage in sitemap based on field 'title', starting at pagenr
     *
     * @param  cloud MMBase cloud
     * @param  page a pages Node
     * @return a path f.e. like /nieuws/artikelen/
     */
    public String getRootPath(Cloud cloud, Node page) {
        StringBuffer path = new StringBuffer();
        Identifier transformer = new Identifier();
        String separator = parameters.getString("separator");
        
        List pagesList = PagesFunctions.listPagesToRoot(page);
        for (Iterator i = pagesList.iterator(); i.hasNext();) {
            Node pn = (Node) i.next();
            
            String pagetitle = pn.getStringValue("title");
            pagetitle = transformer.transform(pagetitle);
            pagetitle = pagetitle.toLowerCase();
            
            // don't put home in the url
            if (!pagetitle.equals("home") && !pagetitle.equals("homepage")) {
                path.append(pagetitle);
                if (i.hasNext()) path.append(separator);
            }
            
        }
        
        if (log.isDebugEnabled()) log.debug("Path to /: " + path.toString());
        return path.toString();
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
        String separator = parameters.getString("separator");
        String page_param = parameters.getString("parameter");
        
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
        NodeList nl = nm.getList("LOWER(title) = '" + title + 
                                                    "' OR LOWER(title) = '" + alttitle + "'", null, null);
        if (nl.size() > 0) {
            Node n = nl.getNode(0);
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
            
            jspurl.append(separator).append(PagesFunctions.getTemplate(cloud, n));
            jspurl.append("?").append(page_param).append("=").append(number);
            
            // cache.putURLEntry(jspurl.toString(), flink);
            // cache.putJSPEntry(flink, jspurl.toString());
            // cache.putNodeEntry(number, jspurl.toString());
            cache.putEntries(number, jspurl.toString(), flink);

            if (params != null) jspurl.append("&").append(params);
            return jspurl.toString();

        } else {
            if (log.isDebugEnabled()) log.debug("No match found.");
            return "";
        }
    }

    /**
     * Creates the technical (jsp) url. Urls to nodes need to be cached in just one proper way?
     *
     * @param  cloud    MMBase cloud
     * @param  page		page node
     * @return url to a template that can display the article node
     */
    private String getUrl(Cloud cloud, Node page) {
        StringBuilder url = new StringBuilder();
        
        String separator = parameters.getString("separator");
        String page_param = parameters.getString("parameter");
        String template = PagesFunctions.getTemplate(cloud, page);
        String connector = (template.indexOf("?") == -1 ? "?" : "&");
        
        url.append(separator).append(template).append(connector);
        url.append(page_param).append("=").append(page.getStringValue("number"));
        
        return url.toString();
    }

}
