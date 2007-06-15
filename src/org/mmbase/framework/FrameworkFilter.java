package org.mmbase.framework;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.*;

import javax.servlet.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.servlet.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Requestfilter that filters out all URL's looking for virtual 'userfriendly' links that have a 
 * corresponding page (technical URL) within the website. When the recieved URL is not a
 * recognized by the framework as an 'userfriendly' one it just gets forwarded in its original 
 * form.
 * Regular expressions that define URL's to be excluded from filtering should be listed in the
 * 'excludes' parameter in web.xml.
 * The filtering and conversion to a URL pointing to an existing JSP template is 
 * done by UrlConverter. Based upon code from LeoCMS and CMSC.
 *
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Id: FrameworkFilter.java,v 1.2 2007-06-15 10:18:05 andre Exp $
 */

public class FrameworkFilter implements Filter, MMBaseStarter  {
	
    private static final Logger log = Logging.getLoggerInstance(FrameworkFilter.class);
    
    /**
     * The context this servlet lives in
     */
    protected ServletContext ctx = null;

    /**
     * MMBase needs to be started first to be able to load config
     */
    private static MMBase mmbase;
    private Thread initThread;
    
    /**
     * The pattern being used to determine to exlude an URL
     */
    private static Pattern excludePattern;
        
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
        // never mind, simply ignore
    }

    /**
     * Initialize the filter, called on webapp startup
     *
     * @param config object containing init parameters specified
     * @throws ServletException thrown when an exception occurs in the web.xml
     */
    public void init(FilterConfig config) throws ServletException {
        log.info("Starting UrlFilter for YAFramework");
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
    

    protected static String getPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null ? path : request.getPathInfo();
    }
    /**
     * Filters a request. 
     * URL conversion is only done when the URI does not match one of the excludes in web.xml.
     * The conversion work is delegated to UrlConverter. Waits for MMBase to be up.
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
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String path = getPath(req);
            if (log.isDebugEnabled()) log.debug("Processing path: " + path);
            if (path != null) {
                try {
                    if (excludePattern != null && excludePattern.matcher(path).find()) {
                        chain.doFilter(request, response);	// url is excluded from further actions
                        return;
                    }
                } catch (Exception e) {
                    log.fatal("Could not process exclude pattern: " + e);
                }
            }
            
            // URL is not excluded, pass it to UrlConverter to process and forward the request
            String forwardUrl = MMBase.getMMBase().getFramework().convertUrl(req);
            if (log.isDebugEnabled()) log.debug("Received '" + forwardUrl + "' from UrlConverter, forwarding.");
            if (forwardUrl != null && !forwardUrl.equals("")) {
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

