/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.*;

import org.w3c.dom.*;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


/**
 * MMBaseServlet is a base class for other MMBase servlets (like ImageServlet). Its main goal is to
 * store a MMBase instance for all its descendants, but it can also be used as a serlvet itself, to
 * show MMBase version information.
 *
 * @version $Id: MMBaseServlet.java,v 1.28 2004-02-24 11:53:19 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class MMBaseServlet extends  HttpServlet implements MMBaseStarter {

    private   static final Logger log = Logging.getLoggerInstance(MMBaseServlet.class);
    protected static MMBase mmbase;
    // private   static String context;


    // ----------------
    // members needed for refcount functionality.

    /**
     * To keep track of the currently running servlets
     * switch the following boolean to true.
     *
     * @bad-constant
     */
    private static final boolean logServlets = true;
    private static int servletCount; // Number of running servlets
    /**
     *  Lock to sync add and remove of threads
     */
    private static Object servletCountLock = new Object();
    /**
     * Hashtable containing currently running servlets
     */
    private static Hashtable runningServlets = new Hashtable();
    /**
     * Toggle to print running servlets to log.
     * @javadoc Not clear, I don't understand it.
     */
    private static int printCount;

    private static int servletInstanceCount = 0;
    // servletname -> servletmapping
    // obtained from web.xml
    private static Map servletMappings    = new Hashtable();
    // topic -> servletname
    // set by isntantiated servlets
    private static Map associatedServlets = new Hashtable();
    // topic -> servletmapping
    // set by instantiated servlets
    private static Map associatedServletMappings = new Hashtable();
    // mapping to servlet instance
    private static Map mapToServlet = new Hashtable();

    /** 
     * @since MMBase-1.7
     */
    private static ServletException initException = new ServletException("MMBase not yet, or not successfully initialized (check mmbase log)");


    private Thread initThread;


    /**
     * On default, servlets are not associated with any function.
     *
     * This function is called in the init method.
     *
     * @return A map of Strings (function) -> Integer (priority). Never null.
     */

    protected Map getAssociations() {
        return new Hashtable();
    }

    private static class ServletEntry {
        ServletEntry(String n) {
            this(n, null);
        }
        ServletEntry(String n, Integer p) {
            name = n;
            if (p == null) {
                priority = 0;
            } else {
                priority = p.intValue();
            }
        }
        String name;
        int    priority;
    }


    public MMBase getMMBase() {
        return mmbase;
    }
    public void setMMBase(MMBase mmb) {
        mmbase = mmb;
    }

    public void setInitException(ServletException e) {
        initException = e;
    }
    /**
     * The init of an MMBaseServlet checks if MMBase is running. It not then it is started.
     */

    public void init() throws ServletException {

        if (! MMBaseContext.isInitialized()) {
            ServletContext servletContext = getServletConfig().getServletContext();
            MMBaseContext.init(servletContext);
            MMBaseContext.initHtmlRoot();
        }

        log.info("Init of servlet " + getServletName() + ".");
        boolean initialize = false;
        // for retrieving servletmappings, determine status
        synchronized (servletMappings) {
            initialize = (servletInstanceCount == 0);
            servletInstanceCount++;
        }
        if (initialize) {
            // used to determine the accurate way to access a servlet
            try {
               
                MMBaseContext.initHtmlRoot();
                // get config and do stuff.
                String path = MMBaseContext.getHtmlRoot() + "/WEB-INF/web.xml";
                log.service("Reading servlet mappings from " + path);
                XMLBasicReader webDotXml = new XMLBasicReader(path, false);
                Enumeration mappings = webDotXml.getChildElements("web-app", "servlet-mapping");
                while (mappings.hasMoreElements()) {
                    Element mapping = (Element) mappings.nextElement();
                    Element servName = webDotXml.getElementByPath(mapping, "servlet-mapping.servlet-name");
                    String name = webDotXml.getElementValue(servName);
                    if (!(name.equals(""))) {
                        Element urlPattern=webDotXml.getElementByPath(mapping, "servlet-mapping.url-pattern");
                        String pattern=webDotXml.getElementValue(urlPattern);
                        if (!(pattern.equals(""))) {
                            List ls = (List) servletMappings.get(name);
                            if (ls == null) {
                                ls = new ArrayList(); 
                                servletMappings.put(name, ls);
                            }
                            ls.add(pattern);
                        }
                    }
                }
                webDotXml = null;
            } catch (Exception e) {
                log.error(e.getMessage() + Logging.stackTrace(e));
            }
            log.debug("Loaded servlet mappings");
        }
        log.debug("Associating this servlet with functions");
        Iterator i = getAssociations().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            associate((String) e.getKey(), getServletName(), (Integer) e.getValue());
        }
        log.debug("Associating this servlet with mappings");
        i = getServletMappings(getServletConfig().getServletName()).iterator();
        while (i.hasNext()) {
            String mapping=(String)i.next();
            mapToServlet.put(mapping,this);
        }

        // stuff that can take indefinite amount of time (database down and so on) is done in separate thread
        initThread = new MMBaseStartThread(this);
        initThread.start();

    }

    /**
     * Gets the servlet that belongs to the given mapping
     *
     * @param mapping the mapping used to access the servlet
     * @return the Servlet that handles the mapping
     */
    public static HttpServlet getServletByMapping(String mapping) {
        return (HttpServlet)mapToServlet.get(mapping);
    }

    /**
     * Gets all the mappings for a given servlet. So, this is a method to obtain info from web.xml.
     *
     * @param servletName the name of the servlet
     * @return an unmodifiable list of servlet mappings for this servlet
     */
    public static List getServletMappings(String servletName) {
        List ls = (List) servletMappings.get(servletName);
        if (ls==null) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableList(ls);
        }
    }

    /**
     * Gets all the mappings for a given association.
     *
     * Use this to find out how to call a servlet to handle a certain
     * type of operation or data (i.e 'images', 'attachments').
     *
     *
     * @param function the function that identifies the type of association
     * @return an unmodifiable list of servlet mappings associated with the function
     */
    public static List getServletMappingsByAssociation(String function) {
        // check if any mappings were explicitly set for this function
        // if so, return that list.
        ServletEntry mapping = (ServletEntry)associatedServletMappings.get(function);
        if (mapping != null) {
            List mappings = new ArrayList();
            mappings.add(mapping.name);
            return mappings;
        }
        // otherwise, get the associated servet
        String name = getServletByAssociation(function);
        if (name != null) {
            return getServletMappings(name);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Gets the name of the servlet that performs actions associated with the
     * the given function.
     *
     * Use this to find a servlet to handle a certain type of
     * operation or data (i.e 'imageservlet', 'myimageservlet',
     * 'images');
     *
     * @param function the function that identifies the type of association
     * @return the name of the servlet associated with the function, or null if there is none
     */
    public static String getServletByAssociation(String function) {
        ServletEntry e = ((ServletEntry) associatedServlets.get(function));
        if (e != null) {
            return e.name;
        } else {
            return null;
        }
    }


    /**
     * Associate a given servlet with the given function.
     * Use this to set a servlet to handle a certain type of operation or data (i.e 'image-processing');
     * For now, only one servlet can be registered.
     * @param function the function that deidentifies the type of association
     * @param servletname name of the servlet to associate with the function
     * @param priority priority of this association, the association only occurs if no servlet or servletmapping
     *                    with higher priority for the same function is present already
     */
    private static synchronized void associate(String function, String servletName, Integer priority) {
        if (priority == null) priority = new Integer(0);
        ServletEntry m = (ServletEntry) associatedServletMappings.get(function);
        if (m != null && (priority.intValue() < m.priority)) return;
        ServletEntry e = (ServletEntry) associatedServlets.get(function);
        if (e != null && (priority.intValue() < e.priority)) return;
        log.service("Associating function '" + function + "' with servlet name " + servletName + 
           (e == null ? ""  : " (previous assocation was with " + e.name +")")+
           (m == null ? ""  : " (previous assocation was with " + m.name +")"));
        associatedServlets.put(function, new ServletEntry(servletName, priority));
        if (m != null) {
            associatedServletMappings.remove(function);
        }
    }

    /**
     * Associate a given servletmapping with the given function.
     * Use this to set a servletmapping to call for a certain type of operation or data (i.e 'image-processing');
     * For now, only one servletmapping can be registered.
     * @param function the function that identifies the type of association
     * @param servletMapping mapping of the servlet to associate with the function
     * @param priority    priority of this association, the association only occurs if no servlet or servletmapping
     *                    with higher priority for the same function is present already
     */
    protected static synchronized void associateMapping(String function, String servletMapping, Integer priority) {
        if (priority == null) priority = new Integer(0);
        ServletEntry m = (ServletEntry) associatedServletMappings.get(function);
        if (m != null && (priority.intValue() < m.priority)) return;
        ServletEntry e = (ServletEntry) associatedServlets.get(function);
        if (e != null && (priority.intValue() < e.priority)) return;
        log.service("Associating function '" + function + "' with servlet mapping " + servletMapping +
           (e == null ? ""  : " (previous assocation was with " + e.name +")")+
           (m == null ? ""  : " (previous assocation was with " + m.name +")"));
        associatedServletMappings.put(function, new ServletEntry(servletMapping, priority));
        if (e != null) {
            associatedServlets.remove(function);
        }
    }

    /**
     * Serves MMBase version information. This doesn't do much usefull
     * yet, but one could image lots of cool stuff here. Any other
     * MMBase servlet will probably override this method.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter pw = res.getWriter();
        pw.print(org.mmbase.Version.get());
        pw.close();
    }

    /**
     * The service method is extended with calls for the refCount
     * functionality (for performance related debugging).  So you can
     * simply override doGet in extension classes, and this stays
     * working, without having to think about it.
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        if (initException != null) {
            throw initException;
        }
        incRefCount(req);
        try {
            super.service(req, res);
        } finally { // whatever happens, decrease the refcount:
            decRefCount(req);
        }
    }

    /**
     * Returns information about this servlet. Don't forget to override it.
     */

    public String getServletInfo()  {
        return "Serves MMBase version information";
    }



    // ----------------------
    // functions needed for refcount functionality.

    /**
     * Return URI with QueryString appended
     * @param req The HttpServletRequest.
     */
    protected static String getRequestURL(HttpServletRequest req) {
        String result = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString!=null) result += "?" + queryString;
        return result;
    }


    /**
     * Decrease the reference count of the servlet
     * @param req The HttpServletRequest.
     */

    protected void decRefCount(HttpServletRequest req) {
        if (logServlets) {
            String url = getRequestURL(req) + " " + req.getMethod();
            synchronized (servletCountLock) {
                servletCount--;
                ReferenceCountServlet s = (ReferenceCountServlet) runningServlets.get(this);
                if (s!=null) {
                    if (s.refCount == 0) {
                        runningServlets.remove(this);
                    } else {
                        s.refCount--;
                        int i = s.uris.indexOf(url);
                        if (i >= 0) s.uris.removeElementAt(i);
                    }
                }// s!=null
            }//sync
        }// if (logServlets)
    }

    /**
     * Increase the reference count of the servlet (for debugging)
     * and send running servlets to log once every 32 requests
     * @param req The HttpServletRequest.
     * @scope private
     * @bad-constant  31 should be configurable.
     */

    protected void incRefCount(HttpServletRequest req) {
        if (logServlets) {
            String url = getRequestURL(req) + " " + req.getMethod();
            int curCount;
            synchronized (servletCountLock) {
                servletCount++; 
                curCount = servletCount; 
                printCount++;
                ReferenceCountServlet s = (ReferenceCountServlet) runningServlets.get(this);
                if (s==null) {
                    runningServlets.put(this, new ReferenceCountServlet(this, url, 0));
                } else { 
                    s.refCount++; 
                    s.uris.addElement(url); 
                }
            }// sync

            if ((printCount & 31) == 0) { // Why not (printCount % <configurable number>) == 0?
                if (curCount > 0) {
                    log.info("Running servlets: " + curCount);
                    for(Enumeration e=runningServlets.elements(); e.hasMoreElements();)
                        log.info(e.nextElement());
                }// curCount>0
            }
        }
    }

    public void destroy() {
        log.info("Servlet " + getServletName() + " is taken out of service");
        initThread.interrupt();
        log.debug("Disassociating this servlet with mappings");
        Iterator i = getServletMappings(getServletConfig().getServletName()).iterator();
        while (i.hasNext()) {
            String mapping=(String)i.next();
            mapToServlet.remove(mapping);
        }
        super.destroy();
         // for retrieving servletmappings, determine status
        synchronized (servletMappings) {

           servletInstanceCount--;
            if (servletInstanceCount == 0) {
                log.info("Unloaded servlet mappings");
                associatedServlets.clear();
                servletMappings.clear();
                log.info("No MMBase servlets left; modules can be shut down");
                Module.shutdownModules();
                Logging.shutdown();
                mmbase = null;
            }
        }
   }
    
    /**
     * This class maintains current state information for a running servlet.
     * It contains a reference count, as well as a list of URI's being handled by the servlet.
     */
    private class ReferenceCountServlet {
        /**
         * The servlet do debug
         * @scope private
         */
        MMBaseServlet servlet;
        /**
         * List of URIs that call the servlet
         * @scope private
         */
        Vector uris = new Vector();
        /**
         * Nr. of references
         * @scope private
         */
        int refCount;

        /**
         * Create a new ReferenceCountServlet using the jamesServlet
         */
        ReferenceCountServlet(MMBaseServlet servlet, String uri, int refCount) {
            this.servlet = servlet;
            uris.add(uri);
            this.refCount = refCount;
        }

        /**
         * Return a description containing servlet info and URI's
         */
        public String toString() {
            return "servlet("+servlet+"), refcount("+(refCount+1)+"), uri's("+uris+")";
        }
    }


}
