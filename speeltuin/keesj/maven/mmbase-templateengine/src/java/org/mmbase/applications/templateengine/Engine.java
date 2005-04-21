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

import org.mmbase.util.logging.*;

/**
 * Engine is a Servlet that acts as a front servlet the main purpose of the filter is 
 * to choose the right template .
  * @author Kees Jongenburger
 */
public class Engine {

    protected static String engineName = "";
    //every time a new EngineFacade is created a the counter is increased 
    protected static long creationCounter = 0;

    //	every time the EngineFacade is destroy is called a the counter is increased
    protected static long destroyCounter = 0;

    //request counter shared over the different engines
    protected static int requestCounter = 0;

    //the configuration of the filter
    //private ServletConfig servletConfig = null;

    //we have a separate logger for the different instances
    private static Logger log = Logging.getLoggerInstance(EngineFilter.class);

    //we have only one facade
    private static Facade _facade;

    public Engine() {
        creationCounter++;
    }

    public static synchronized Facade getFacade() {
        if (_facade == null) {
            _facade = new FacadeImpl();
            //_facade.setEngineURL("/" + engineName + "/");
			_facade.setEngineURL("/");
            log.info("setting engine url to " + _facade.getEngineURL());
        }
        return _facade;
    }

    public void destroy() {
        destroyCounter++;
    }

    public boolean render(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {

            requestCounter++;

            resp.setContentType("text/html;charset=" + Facade.encoding);
            //resp.setContentType("text/html");

            //code to determin the "engine url"
            Facade facade = getFacade();

            //create your shared object
            WhiteBoard wb = new WhiteBoard(req, resp);
            wb.setFacade(facade);
            //and store the whiteboard on it
            req.setAttribute("wb", wb);

            if (req.getRequestURI().length() == facade.getEngineURL().length()) {
                return false;
            }

            //extract the servlet context name and the eninge name
            //this is the starting point
            String path = req.getRequestURI().substring(facade.getEngineURL().length());

            //remove leading slashes
            while (path.charAt(path.length() - 1) == '/') {
                path = path.substring(0, path.length() - 1);
            }

            if (log.isDebugEnabled()) {
                //log.debug("request[" + requestCounter + "] servlet path info " + req.getPathInfo());
                //log.debug("request[" + requestCounter + "] " + req.getRequestURI());
                //log.debug("request[" + requestCounter + "] servlet request url " + req.getRequestURL());
                //log.debug("request[" + requestCounter + "] servlet context path " + req.getContextPath());
                //log.debug("request[" + requestCounter + "] servlet  path " + req.getServletPath());
                //log.debug("request[" + requestCounter + "] guesed filter path " + facade.getEngineURL());
                log.debug("request[" + requestCounter + "] requested " + path);
            }

            NavigationControl navigationComponent = facade.getNavigationControl();
            //resolve the current navigation
            Navigation nav = navigationComponent.resolveNavigation(new Path(path));
            if (nav != null) {
                //if the navigation was found put it in the whiteboard
                wb.setCurrentNavigation(nav);
                String navpath = navigationComponent.getURLString(nav);
                log.debug("request[" + requestCounter + "] found " + navpath);

                try {
                    Template t = navigationComponent.getTemplate(nav);
                    log.debug("using template: " + t.getName() + " " + t.getDescription());
                    if (!path.equals(navpath)) {
                        log.debug("navpath " + navpath);
                        log.debug("path " + path);
                        String remainingPath = path.substring(navpath.length() + NavigationControl.PATH_SEPARATOR.length());
                        t.renderRelative(remainingPath, wb);
                    } else {
                        t.render(wb, resp.getWriter());
                    }
                } catch (IOException e) {
                    log.warn(Logging.stackTrace(e));
                } catch (Exception e) {
                    log.warn(Logging.stackTrace(e));
                }
            } else {
            	log.debug(req.getRequestURI() + " is not a TemplateEningeSite");
                return false;
            }
        } catch (Throwable t) {
            log.fatal(t.getMessage() + " " + Logging.stackTrace(t));
        }
        return true;
    }
}
