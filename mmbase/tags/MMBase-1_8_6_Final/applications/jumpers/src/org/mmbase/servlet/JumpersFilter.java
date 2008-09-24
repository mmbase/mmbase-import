/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.mmbase.jumpers.Jumpers;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Redirects request based on information supplied by the jumpers builder.
 *
 * @application Tools, Jumpers
 * @author Jaco de Groot
 * @version $Id: JumpersFilter.java,v 1.3 2007-09-11 12:39:04 michiel Exp $
 */
public class JumpersFilter implements Filter, MMBaseStarter {
    private static final Logger log = Logging.getLoggerInstance(JumpersFilter.class);
    private static MMBase mmbase;
    private static Jumpers jumpers;
    private static String name;

    private Thread initThread;

    /**
     * Supported for use with older versions of the servlet api, such as used by Orion 1.5.2
     * This method simply thows an exception when called.
     * @deprecated will be dropped in future versions
     */
    public void setFilterConfig(FilterConfig fc) {
        log.info("Setting filter-config");
        throw new UnsupportedOperationException("This method is not part of the Servlet api 2.3");
    }

    /**
     * Supported for use with older versions of the servlet api, such as used by Orion 1.5.2
     * This method simply thows an exception when called.
     * @deprecated will be dropped in future versions
     */
    public FilterConfig getFilterConfig() {
        log.info("Getting filter-config");
        throw new UnsupportedOperationException("This method is not part of the Servlet api 2.3");
    }

    public MMBase getMMBase() {
        return mmbase;
    }

    public void setMMBase(MMBase mmb) {
        mmbase = mmb;
    }

    public void setInitException(ServletException se) {
        // never mind, simply, ignore
    }

    /**
     * Initializes the filter
     */
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        log.info("Starting JumpersFilter with " + filterConfig);
        name = filterConfig.getFilterName();
        MMBaseContext.init(filterConfig.getServletContext());
        MMBaseContext.initHtmlRoot();
        // stuff that can take indefinite amount of time (database down and so on) is done in separate thread
        initThread = new MMBaseStartThread(this);
        initThread.start();
    }

    /**
     * Filters the request: tries to find a jumper and redirects to this url when found, otherwise the
     * request will be handled somewhere else in the filterchain.
     * @param servletRequest The ServletRequest.
     * @param servletResponse The ServletResponse.
     * @param filterChain The FilterChain.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws java.io.IOException, ServletException {
        if (mmbase == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (jumpers == null) {
            if (mmbase != null) {
                jumpers = (Jumpers)mmbase.getBuilder("jumpers");
            }
            if (jumpers == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return; // nothing to be done
            }
        }
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        /*
         * getContextPath()
         * Returns the portion of the request URI that indicates the context of the request.
         * The context path always comes first in a request URI.
         * The path starts with a "/" character but does not end with a "/" character.
         * For servlets in the default (root) context, this method returns "". The container does not decode this string.
         */
        String context = "";
        context = req.getContextPath();
        int contextPart = context.length();
        String reqURI = req.getRequestURI();
        String key = "";
        if (contextPart < reqURI.length()) {
            // also remove the leading "/", unless it's an empty string.
            key = req.getRequestURI().substring(contextPart+1);
        }

        if (log.isDebugEnabled()) {
            log.debug("contextpath is: " + context);
            log.debug("key is: " + key);
            log.debug("uri is: " + reqURI);
        }

        //ignore keys with extensions
        if (key.indexOf('.') == -1 ) {
            // because Tomcat version > 5.0.5 always adds a trailing slash if
            // there's a directory with the same name, the trailing slash must be removed
            if (key.endsWith("/")) {
                key = key.substring(0,key.length()-1);
                if (log.isDebugEnabled()){
                    log.debug("after removing trailing slash key becomes: " + key);
                }
            }
            //get jumper from Jumpers builder
            String url = jumpers.getJump(key);
            if (url != null) {
                /*
                 * Sends a temporary redirect response to the client using the specified redirect location URL.
                 * This method can accept relative URLs; the servlet container must convert the relative URL
                 * to an absolute URL before sending the response to the client. If the location is relative without a leading '/' the
                 * container interprets it as relative to the current request URI. If the location is relative with a leading '/' the container
                 *  interprets it as relative to the servlet container root.
                 * If the response has already been committed, this method throws an IllegalStateException.
                 *  After using this method, the response should be considered to be committed and should not be written to.
                 */
                // res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + url));

                // there can be different types of jumper uris which all must behandled
                // differently:
                // - absolute with a scheme (http://www.servername.nl/context/bla/bla123), muste be redirected
                // - absolute without a scheme (/bla/bla123), must be redirected
                // - relative (bla/bla123), must be made absolute and then be redirected
                URI redirURI = null;
                try {
                    redirURI = new URI(url);
                } catch (URISyntaxException ex) {
                    log.error ("jumper URI syntax is not valid");
                }
                if (redirURI != null) {
                    if (redirURI.isAbsolute()) {
                        res.sendRedirect(url);
                    } else if (url.startsWith("/")) {
                        res.sendRedirect(url);
                    } else {
                        res.sendRedirect(context +"/" + url);
                    }
                }
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * destroys the filter
     */
    public void destroy() {
        log.info("Filter " + name + " destroyed.");
        initThread.interrupt();
    }


}
