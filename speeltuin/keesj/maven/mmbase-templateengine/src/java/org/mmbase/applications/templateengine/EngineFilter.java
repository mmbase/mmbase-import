/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.io.*;

import javax.servlet.*;

import org.mmbase.module.core.*;

/**
 * Engine is a Servlet that acts as a front servlet the main purpose of the filter is 
 * to choose the right template .
  * @author Kees Jongenburger
 */
public class EngineFilter implements Filter {
    Engine engine;
    public EngineFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        MMBaseContext.init(filterConfig.getServletContext());
        MMBaseContext.initHtmlRoot();
        MMBase.getMMBase();
        filterConfig.getServletContext();
        engine = new Engine();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!engine.render(request, response)) {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
        engine.destroy();
    }
}
