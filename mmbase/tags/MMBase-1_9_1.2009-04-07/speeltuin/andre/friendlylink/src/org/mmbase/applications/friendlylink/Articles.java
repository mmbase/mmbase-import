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
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Id: Articles.java,v 1.1 2007-04-30 20:53:50 andre Exp $
 */
public class Articles extends FriendlyLink {

    private static final Logger log = Logging.getLoggerInstance(Articles.class);
    
    /* Configurable parameters in '/utils/friendlylinks.xml' */
    public final static Parameter[] PARAMS = new Parameter[] {
        new Parameter("separator", String.class, "/"),
        new Parameter("template", String.class, "article.jsp"),
        new Parameter("parameter", String.class, "art"),
        new Parameter("extension", String.class)    // could be something like '.html' or '.asp'
    };

    protected Parameter[] getParameterDefinition() {
        return PARAMS;
    }
    
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
     * @param   page  Nodenumber of the page
     * @param   convert To convert or not
     * @return  a 'userfriendly' link
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, Node article, Boolean convert) {
        String artnr = article.getStringValue("number");
        log.debug("article: " + artnr);
        
        String url = getUrl(cloud, article);
        log.debug("url: " + url);
        if (convert == true) {
            UrlCache cache = UrlConverter.getCache();
            if (cache.hasJSPEntry(url)) {
                url = cache.getURLEntry(url);    // Get from cache
                if (log.isDebugEnabled()) log.debug("url from cache: " + url);
            } else {
                url = makeFriendlyLink(cloud, request, article);
                log.debug("url: " + url);
            }
        }
        
        if (log.isDebugEnabled()) log.debug("returning link: " + url);
        return url;
    }
    
    /**
     * Makes the actual friendlylink
     *
     * @param   cloud   An MMBase cloud
     * @param   request HTTP servlet request
     * @param   article  articles node
     * @param   convert To convert or not
     * @return  a friendlylink
     */
    public String makeFriendlyLink(Cloud cloud, HttpServletRequest request, Node article) {
    	UrlCache cache = UrlConverter.getCache();
    	Identifier transformer = new Identifier();
        StringBuilder flink = new StringBuilder();
        String jsp = getUrl(cloud, article);

        String artnr = article.getStringValue("number");
        String art_param = parameters.getString("parameter");
        String separator = parameters.getString("separator");
        
        String path = getRootPath(cloud, article);

        String title = article.getStringValue("title");
        title = transformer.transform(title);
        title = title.toLowerCase();
        
        if (!"".equals(path)) flink.append(separator).append(path);
        flink.append(separator).append(title);
        
        // TODO: check if this friendlylink already exists in cache and change it?
        if (cache.hasURLEntry(flink.toString())) {
            log.warn("flink '" + flink.toString() + "' already in cache, appending nodenr '/" + artnr + "'");
            
        	flink.append(separator).append(artnr);
        	//if (!"".equals(".html")) flink.append(".html");       // appending .html at the end
        }
        
        //flink.insert(0, "/");   // should append / at beginning !
        //cache.putURLEntry(jspUrl, flink.toString());
        //cache.putJSPEntry(flink.toString(), jspUrl);
        cache.putEntries(artnr, jsp, flink.toString());
        
        if (log.isDebugEnabled()) log.debug("Created 'userfriendly' link: " + flink.toString());
        
        return flink.toString();
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
        if (log.isDebugEnabled()) log.debug("Trying to find a technical url for '" + flink + "'");
        
        StringBuilder jspurl = new StringBuilder();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        UrlCache cache = UrlConverter.getCache();
        
        String separator = parameters.getString("separator");
        String art_param = parameters.getString("parameter");
        
        StringTokenizer st = new StringTokenizer(flink, "/");
        String title = "";
        int tokens = st.countTokens();
        while (st.hasMoreTokens()) {
            title = st.nextToken();
            log.debug("token '" + title + "' of " + tokens + " tokens");
        }
        log.debug("trying the last token '" + title + "'");
        
        if (title.indexOf(".html") > -1) title = title.substring(0, title.indexOf(".html"));
    
        // make a title in which _ are replaced (back) with spaces
        String alttitle = title.replace("_", " ");
        
        NodeManager nm = cloud.getNodeManager("articles");
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
            jspurl = new StringBuilder();
            
            jspurl.append(separator).append(ArticlesFunctions.getTemplate(cloud, n));
            jspurl.append("?").append(art_param).append("=").append(number);
            
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
     * Path to homepage in sitemap based on field 'title', starting at pagenr
     *
     * @param  cloud MMBase cloud
     * @param  article  an article node
     * @return a path f.e. like /nieuws/artikelen/
     */
    public String getRootPath(Cloud cloud, Node article) {
        StringBuffer path = new StringBuffer();
        Identifier transformer = new Identifier();
        String separator = parameters.getString("separator");
        
        List pageList = PagesFunctions.listPagesToRoot(article);
        for (Iterator i = pageList.iterator(); i.hasNext();) {
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
     * Creates the technical (jsp) url. Urls to nodes need to be cached in just one proper way?
     *
     * @param  cloud    MMBase cloud
     * @param  article  article node
     * @return url to a template that can display the article node
     */
    private String getUrl(Cloud cloud, Node article) {
        StringBuilder url = new StringBuilder();
        
        String separator = parameters.getString("separator");
        String art_param = parameters.getString("parameter");
        String template = ArticlesFunctions.getTemplate(cloud, article);
        String connector = (template.indexOf("?") == -1 ? "?" : "&");
        url.append(separator).append(template).append(connector).append(art_param).append("=").append(article.getStringValue("number"));
        
        return url.toString();
    }
    
    
}
