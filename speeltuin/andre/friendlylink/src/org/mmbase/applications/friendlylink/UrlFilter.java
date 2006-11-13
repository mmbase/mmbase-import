package org.mmbase.applications.friendlylink;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.*;

import javax.servlet.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.mmbase.util.*;
import org.mmbase.servlet.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Copied and adapted from LeoCMS and MMCommons
 * 
 * Requestfilter that filters out all virtual 'userfriendly' URL's that have a 
 * corresponding page within the website. Regular expressions that define URL's to
 * be excluded from filtering should be listed in the 'excludes' parameter in web.xml.
 * 
 * The filtering and conversion to a URL pointing to an existing JSP template is 
 * done by UrlConverter.
 *
 * @author Finalist IT Group / peter
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */

public class UrlFilter implements Filter, MMBaseStarter  {
	
	private static final Logger log = Logging.getLoggerInstance(UrlFilter.class);
    
    /*
     * The context this servlet lives in
     */
    protected ServletContext ctx = null;

    /*
     * MMBase needs to be started first to be able to load config
     */
	private static MMBase mmbase;
	private Thread initThread;
    
	/*
	 * The pattern being used to determine to exlude an URL
	 */
    private static Pattern excludePattern;
    
    /*
     * The DOM element that will contain the FriendlyLinks configuration
     */
    public static Element configElement;    // TODO: pass this element to the FriendlyLinks class
    
    private static Map<String, FriendlyLink> flinks = new HashMap<String, FriendlyLink>();
    
    protected static Map getFriendlylinks() {
        return flinks;
    }
    
    /*
     * Methods that need to be overriden form MMBaseStarter
     */
    public MMBase getMMBase() {
        return mmbase;
    }

    public void setMMBase(MMBase mm) {
        mmbase = mm;
    }

    public void setInitException(ServletException se) {
        // never mind, simply, ignore
    }

	/**
	 * Initialize the filter, called on webapp startup
	 *
	 * @param config object containing init parameters specified
	 * @throws ServletException thrown when an exception occurs in the web.xml
	 */
	public void init(FilterConfig config) throws ServletException {
	    log.info("Starting UrlFilter for FriendlyLinks with " + config);
	    ctx = config.getServletContext();
	    String excludes = config.getInitParameter("excludes");
	    if (excludes != null && excludes.length() > 0) {
			excludePattern = Pattern.compile(excludes);
		}
		
		/* initialize MMBase if its not started yet */
		MMBaseContext.init(ctx);
		MMBaseContext.initHtmlRoot();
        initThread = new MMBaseStartThread(this);
        initThread.start();
        
	    log.info("UrlFilter initialized");
	}
 
	/**
	 * Destroy method
	 */
	public void destroy() {
		// nothing needed here
	}
    
    /**
     * Reads the configuration from utils/friendlylinks.xml
     *
     */
    public void readConfiguration() {
        log.service("Start readConfiguration() of FriendlyLinks");
        try { 
            ResourceLoader loader =  ResourceLoader.getConfigurationRoot();
            Document doc = loader.getDocument("utils/friendlylinks.xml", true, UrlFilter.class);
            configElement = doc.getDocumentElement();
            NodeList frElements = configElement.getElementsByTagName("friendlylink");
            
            for (int i = 0; i < frElements.getLength(); i++) {
                Element element = (Element) frElements.item(i);
                String name = element.getAttribute("name");

                Element classElement = (Element) element.getElementsByTagName("class").item(0);
                String claz = org.mmbase.util.xml.DocumentReader.getNodeTextValue(classElement);
                
                log.info("Friendlylink '" + name + "' class '" + claz + "'");
                
                FriendlyLink flink = getFlInstance(name, element);
                flinks.put(name, flink);
                
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
    
     /**
      * Initiates friendlylinks
      *
      * @param name     Name of FriendlyLink
      * @param element  Piece of xml describing it
      * @throws ClassNotFoundException
      */
    private FriendlyLink getFlInstance(String name, Element element) throws ClassNotFoundException {
        FriendlyLink flink = null;
        
        Element classElement = (Element) element.getElementsByTagName("class").item(0);
        String className = org.mmbase.util.xml.DocumentReader.getNodeTextValue(classElement);
        Class clazz = Class.forName(className);
        
        try {
            flink = (FriendlyLink) clazz.newInstance();
        } catch (InstantiationException ie) {
            log.error("Unable to instantiate class '" + clazz + "': " + ie);
        } catch (IllegalAccessException iae) {
            log.error("IllegalAccessException instantiating class " + clazz + "': " + iae);
        }
        
        flink.configure(element);
        return flink;
    }
	
	/**
	 * Filters a request 
	 * URL conversion is only done when the URI does not match one of the excludes in web.xml.
	 * The conversion work is delegated to UrlConverter.
	 *
	 * @param request	incoming request
	 * @param response	outgoing response
	 * @param chain		a chain object, provided for by the servlet container
	 * @throws ServletException thrown when an exception occurs
	 * @throws IOException thrown when an exception occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
 		
 		if (mmbase == null) {
 		    if (log.isDebugEnabled()) log.debug("Still waiting for MMBase (not initialized)");
            chain.doFilter(request, response);
            return;
        }
        if (configElement == null) {
    		readConfiguration();            
        } 
        
 		if (request instanceof HttpServletRequest) {
     		HttpServletRequest req = (HttpServletRequest) request;
    		String path = UrlConverter.getPath(req);
    		if (log.isDebugEnabled()) log.debug("Processing path: " + path);
    		if (path != null) {
    			try {
    				if (excludePattern != null && excludePattern.matcher(path).find()) {
    				    //if (log.isDebugEnabled()) log.debug("Excluding path: " + path);
    				    chain.doFilter(request, response);	// url is excluded from further actions
    				    return;
    				}
    			} catch (Exception e) {
    				log.fatal("Could not process exclude pattern: " + e);
    			}
    		}
		
		
    		// URL is not excluded, pass it to UrlConverter to process and forward the request
    		String forwardUrl = "/" + UrlConverter.convertUrl(req);
    		if (forwardUrl != null && !forwardUrl.equals("")) {
    		    
    		    // Ergo: this removes contextpath from the url
    		    /*
    		    String contextpath = req.getContextPath();
    		    if (!contextpath.equals("")) {
    		        forwardUrl = forwardUrl.substring(contextpath.length());
		        }
		        */
    			if (log.isDebugEnabled()) log.debug("Recieved an url from UrlConverter, forwarding: " + forwardUrl);
    		    
    		    /* 
    		     * RequestDispatcher: If the path begins with a "/" it is interpreted
    		     * as relative to the current context root.
    		     */
    			RequestDispatcher rd = request.getRequestDispatcher(forwardUrl);
    			rd.forward(request, response);
    		} else {
    			if (log.isDebugEnabled()) log.debug("No matching technical URL, just forwarding: " + path);
    			chain.doFilter(request, response);
    		}
    	} else {
    	    if (log.isDebugEnabled()) log.debug("Request not an instance of HttpServletRequest, therefore no url forwarding");
    	    chain.doFilter(request, response);
    	}
	}
	
}

