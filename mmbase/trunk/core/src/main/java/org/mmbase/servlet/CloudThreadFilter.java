/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.util.CloudUtil;

/**
 * Servlet Filter which associates a cloud of a http session to the current thread.
 * The filter removes the cloud again when the request is processed  
 */
public class CloudThreadFilter implements Filter {

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) {
        // nix
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            CloudUtil.addCloudToThread(req);

            // Call the next filter (continue request processing)
            chain.doFilter(request, response);
        } catch (Throwable ex) {
            // Let others handle it... maybe another interceptor for exceptions?
            throw new ServletException(ex);
        }
        finally {
            CloudUtil.removeCloudFromThread();
        }
    }


    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // nix
    }

}
