
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.servlet.*;

/**
 * ServletEngine is a Servlet that acts as a front servlet the main purpose of the filter is 
 * to choose the right template .
  * @author Kees Jongenburger
 */
public class EngineServlet extends BridgeServlet {
    Engine engine;
    /**
     *
     */
    public void init() throws ServletException {
        super.init();
        engine = new Engine();
    }

    public void destroy() {
        engine.destroy();
    }
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        if (!engine.render(servletRequest, servletResponse)) {
        	return;
            //req.getRequestDispatcher("/programma/" + path).forward(servletRequest, servletResponse);
        }
    }

    public void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doGet(arg0, arg1);
    }
}
