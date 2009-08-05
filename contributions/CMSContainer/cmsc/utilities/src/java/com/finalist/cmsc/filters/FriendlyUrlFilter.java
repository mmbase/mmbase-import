/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.filters;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The controller filter filters out all navigation requests and dispatches them
 * through to the portal servlet.
 * 
 * @author R.W. van 't Veer
 * @author Wouter Heijke
 */
public abstract class FriendlyUrlFilter implements Filter {
	/**
	 * A logger instance.
	 */
	private static final Log log = LogFactory.getLog(FriendlyUrlFilter.class);

	/**
	 * The servlet container context this filter lives in.
	 */
	protected ServletContext ctx = null;

    private Pattern excludePattern;
    
	/**
	 * Initialize this filter.
	 * 
	 * @param config filter configuration
	 */
	public void init(FilterConfig config) {
		ctx = config.getServletContext();
        
        String excludes = config.getInitParameter("excludes");
        if (excludes != null && excludes.length() > 0) {
            excludePattern = Pattern.compile(excludes);
        }
	}

	/**
	 * Filter request.
	 * 
	 * @param request servlet request object
	 * @param response servlet response object
	 * @param chain filter chain
	 * @throws IOException when forward or chained filter yields it
	 * @throws ServletException when forward or chained filter yields it
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

        String path = getPath(req);
        if (path != null) {
            try {
                if (excludePattern != null && excludePattern.matcher(path).find()) {
                    log.debug("Exclude pattern matched path " + path );
                    chain.doFilter(request, response);
                    return;
                }
            } catch(IOException ioe) {
                if (ioe.getMessage() != null && ioe.getMessage().indexOf("Broken pipe") > -1) {
                    log.info("IO error " + ioe.getMessage());
                }
                else {
                    log.error("IO error " + ioe);
                }
                return;
            } catch (Exception ex) {
                log.fatal("can't process exclude pattern", ex);
                throw new ServletException(ex);
            }
        }
        
		// see if it matches a navigation URL
		if (isFriendlyUrl(req, resp)) {
			RequestDispatcher rd = ctx.getNamedDispatcher(getServlet());
			rd.forward(request, response);
		} else {
            log.debug("isNOTnavigation path " + path );
			chain.doFilter(request, response);
		}
	}

    /**
     * Get path part of request.
     * @param request servlet request
     * @return the path part of request or <tt>null</tt> when it cannot
     * be determined
     */
    private static String getPath (HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null ? path : request.getPathInfo();
    }
    
	/**
	 * Release resources we (don't) have.
	 */
	public void destroy() {
		// nix
	}

    protected abstract String getServlet();

    protected abstract boolean isFriendlyUrl(HttpServletRequest req, HttpServletResponse resp);
    
}
