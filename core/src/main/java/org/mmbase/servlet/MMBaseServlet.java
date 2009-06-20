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
import org.xml.sax.InputSource;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.xml.DocumentReader;


/**
 * MMBaseServlet is a base class for other MMBase servlets (like ImageServlet). Its main goal is to
 * store a MMBase instance for all its descendants, but it can also be used as a serlvet itself, to
 * show MMBase version information.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class MMBaseServlet extends  HttpServlet implements MMBaseStarter {

    private static final Logger log = Logging.getLoggerInstance(MMBaseServlet.class);
    /**
     * MMBase reference. While null, servlet does not accept request.
     */
    protected MMBase mmbase = null;
    // private   static String context;


    // ----------------
    // members needed for refcount functionality.

    /**
     * To keep track of the currently running servlets, you can use this logger.
     *
     */
    private static final Logger servletsLog =  Logging.getLoggerInstance("org.mmbase.SERVLETS");

    private static int servletCount; // Number of running servlets
    /**
     *  Lock to sync add and remove of threads
     */
    private static final Object servletCountLock = new Object();
    /**
     * Map containing currently running servlets
     */
    private static Map<MMBaseServlet, ServletReferenceCount> runningServlets = new HashMap<MMBaseServlet, ServletReferenceCount>();
    /**
     * Toggle to print running servlets to log.
     * @javadoc Not clear, I don't understand it.
     */
    private static int printCount;

    private static int servletInstanceCount = 0;
    // servletname -> servletmapping
    // obtained from web.xml
    private static Map<String, List<String>> servletMappings    = new HashMap<String, List<String>>();
    // topic -> servletname
    // set by isntantiated servlets
    private static Map<String, ServletEntry> associatedServlets = new HashMap<String, ServletEntry>();
    // topic -> servletmapping
    // set by instantiated servlets
    private static Map<String, ServletEntry> associatedServletMappings = new HashMap<String, ServletEntry>();
    // mapping to servlet instance
    private static Map<String, HttpServlet> mapToServlet = new HashMap<String, HttpServlet>();

    private long start = System.currentTimeMillis();

    /**
     * Boolean indicating whether MMBase has been started. Used by {@link #checkInited}, set to true {@link #by setMMBase}.
     * @since MMBase-1.7
     */
    private static boolean mmbaseInited = false;

    /**
     * If MMBase has not been started, a 503 is given, with this value for the 'Retry-After' header.
     * See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">rfc 2616, section 10.5.4</a>.
     * Defaults to 60 seconds, can be configured in web.xml with the 'retry-after' propery on the servlets.
     * @since MMBase-1.7.2
     */
    protected int retryAfter = 60;


    /**
     * Thread starting MMBase
     */
    private Thread initThread;


    /**
     * On default, servlets are not associated with any function.
     *
     * This function is called in the init method.
     *
     * @return A map of Strings (function) -> Integer (priority). Never null.
     */

    protected Map<String, Integer> getAssociations() {
        return new HashMap<String, Integer>();
    }

    /**
     * Used in association map
     */
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


    /**
     * Returns the MMBase instance.
     * @since MMBase-1.7
     */
    public  MMBase getMMBase() {
        return mmbase;
    }

    /**
     * Sets the mmbase member. Can be overriden to implement extra initalization for the servlet which needs a running MMBase.
     * @since MMBase-1.7
     */
    public void setMMBase(MMBase mmb) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new java.util.Date(System.currentTimeMillis() - start));
        if (! mmbaseInited) {
            log.info("MMBase servlets are ready to receive requests, started in " +
                     cal.get(Calendar.MINUTE) + " min " + cal.get(Calendar.SECOND) +" sec.");
        }

        mmbase = mmb;
        mmbaseInited = true;
        initThread = null;
    }



    /**
     * Used in checkInited.
     */
    private static ServletException initException = null;

    /**
     * Called by MMBaseStartThread, if something went wrong during
     * initialization of MMBase. It will be thrown by checkInited
     * then.
     * @since MMBase-1.7
     */
    public void setInitException(ServletException e) {
        initException = e;
    }

    /**
     * The init of an MMBaseServlet checks if MMBase is running. It not then it is started.
     */
    public void init() throws ServletException {

        ServletContext servletContext = getServletConfig().getServletContext();

        String retryAfterParameter = servletContext.getInitParameter("retry-after");
        if (retryAfterParameter == null) {
            // default: one minute
            retryAfter = 60;
        } else {
            retryAfter = Integer.valueOf(retryAfterParameter);
        }

        if (! MMBaseContext.isInitialized()) {
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
                java.net.URL url;
                try {
                    url = getServletConfig().getServletContext().getResource("/WEB-INF/web.xml");
                } catch (NoSuchMethodError nsme) {
                    // for old app-servers.
                    log.error(nsme);
                    url = (new java.io.File(getServletConfig().getServletContext().getRealPath("/WEB-INF/web.xml"))).toURL();
                }
                if (url == null) {
                    log.warn("No web.xml found");
                } else {
                    InputSource path = new InputSource(url.openStream());
                    log.service("Reading servlet mappings from " + url);
                    DocumentReader webDotXml = new DocumentReader(path, false);

                    for (Element mapping: webDotXml.getChildElements("web-app", "servlet-mapping")) {
                        Element servName = webDotXml.getElementByPath(mapping, "servlet-mapping.servlet-name");
                        String name = webDotXml.getElementValue(servName);
                        if (!(name.equals(""))) {
                            Element urlPattern=webDotXml.getElementByPath(mapping, "servlet-mapping.url-pattern");
                            String pattern=webDotXml.getElementValue(urlPattern);
                            if (!(pattern.equals(""))) {
                                List<String> ls = servletMappings.get(name);
                                if (ls == null) {
                                    ls = new ArrayList<String>();
                                    servletMappings.put(name, ls);
                                }
                                ls.add(pattern);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.debug("Loaded servlet mappings");
        }
        log.debug("Associating this servlet with functions");
        for (Map.Entry<String, Integer> e : getAssociations().entrySet()) {
            associate(e.getKey(), getServletName(), e.getValue());
        }
        log.debug("Associating this servlet with mappings");
        for (String mapping :  getServletMappings(getServletConfig().getServletName())) {
            mapToServlet.put(mapping, this);
        }

        String hold = servletContext.getInitParameter("stall-server");
        if ("yes".equals(hold) || "true".equals(hold)) {
            log.service(getServletName() + ": Waiting until MMBase is started");
            Runnable starter = new MMBaseStartThread.Job(this);
            starter.run();
            log.service(getServletName() + ": Ready to receive requests.");
        } else {
            // stuff that can take indefinite amount of time (database down and so on) is done in separate thread
            initThread = new MMBaseStartThread(this);
            initThread.start();
        }
    }

    /**
     * Gets the servlet that belongs to the given mapping
     *
     * @param mapping the mapping used to access the servlet
     * @return the Servlet that handles the mapping
     */
    public static HttpServlet getServletByMapping(String mapping) {
        return mapToServlet.get(mapping);
    }

    /**
     * Gets all the mappings for a given servlet. So, this is a method to obtain info from web.xml.
     *
     * @param servletName the name of the servlet
     * @return an unmodifiable list of servlet mappings for this servlet
     */
    public static List<String> getServletMappings(String servletName) {
        List<String> ls = servletMappings.get(servletName);
        if (ls == null) {
            return Collections.emptyList();
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
    public static List<String> getServletMappingsByAssociation(String function) {
        // check if any mappings were explicitly set for this function
        // if so, return that list.
        ServletEntry mapping = associatedServletMappings.get(function);
        if (mapping != null) {
            List<String> mappings = new ArrayList<String>();
            mappings.add(mapping.name);
            return mappings;
        }
        // otherwise, get the associated servet
        String name = getServletByAssociation(function);
        if (name != null) {
            return getServletMappings(name);
        } else {
            return Collections.emptyList();
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
        ServletEntry e = associatedServlets.get(function);
        if (e != null) {
            return e.name;
        } else {
            return null;
        }
    }
    /**
     * @since MMBase-1.8.5
     */
    public static String getBasePath(String function) {
        List<String> ls = MMBaseServlet.getServletMappingsByAssociation(function);
        if (ls.size() == 0) return null;
        String baseUrl = ls.get(0);
        int pos = baseUrl.lastIndexOf("*");
        if (pos > 0) {
            baseUrl = baseUrl.substring(0, pos);
        }
        pos = baseUrl.indexOf("*");
        if (pos == 0) {
            baseUrl = baseUrl.substring(pos + 1);
        }
        return baseUrl;
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
        if (priority == null) priority = 0;
        ServletEntry m = associatedServletMappings.get(function);
        if (m != null && (priority.intValue() < m.priority)) return;
        ServletEntry e = associatedServlets.get(function);
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
        if (priority == null) priority = 0;
        ServletEntry m = associatedServletMappings.get(function);
        if (m != null && (priority.intValue() < m.priority)) return;
        ServletEntry e = associatedServlets.get(function);
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
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter pw = res.getWriter();
        pw.print(org.mmbase.Version.get());
        String q = req.getQueryString();
        if ("starttime".equals(q)) {
            res.setHeader("Cache-Control", "no-cache");
            pw.print("\nUp since " + new Date((long) MMBase.startTime * 1000));
        } else if ("uptime".equals(q)) {
            res.setHeader("Cache-Control", "no-cache");
            int seconds = (int) (System.currentTimeMillis() / 1000) - MMBase.startTime;
            int days = seconds / (60 * 60 * 24);
            seconds %=  60 * 60 * 24;
            int hours = seconds / (60 * 60);
            seconds %= 60 * 60;
            int minutes = seconds / 60;
            seconds %=  60;
            pw.print("\nUptime: " + (days == 1 ? "1 day" : ( days > 1 ? "" + days + " days" : "")) +
                     (hours > 0 || days > 0 ? " " + (hours == 1 ? "1 hour" : "" + hours + " hours") : "")  +
                     (minutes > 0 || hours > 0 ? " " + (minutes == 1 ? "1 minute" : "" + minutes + " minutes") : "") +
                     (seconds > 0 || minutes > 0 ? " " + (seconds == 1 ? "1 second" : "" + seconds + " seconds") : ""));

        } else if ("server".equals(q)) {
            String appserver = System.getProperty("catalina.base"); // to do: similar arrangment for
                                                                    // other ap-servers.
            String root = "" + getServletContext().getResource("/");
            String rootRealPath = "" + getServletContext().getRealPath("/");
            pw.print("\n" + getServletContext().getServerInfo() + " " + System.getProperty("java.version") +
                     " (" + System.getProperty("java.vendor") + ") " +
                     (appserver == null ? "" : appserver) +
                     "@" + java.net.InetAddress.getLocalHost().getCanonicalHostName() + " " +
                     System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch")  +
                     "\n" + root + (rootRealPath != null ? ("\n" + rootRealPath) : ""));

        }
        pw.close();
    }


    /**
     * This methods can be (and is) called in the beginning of
     * service. It sends an UNAVAILABLE error if MMBase has not bee
     * started, or throws an exeption if that was unsuccessful.
     * @return A boolean. If false, then service must return immediately (because mmbase has not been inited yet).
     * @since MMBase-1.7.2
     */
    protected  boolean checkInited(HttpServletResponse res) throws ServletException, IOException  {
        if (initException != null) {
            throw initException;
        }

        if (! mmbaseInited) {
            res.setHeader("Retry-After", "" + retryAfter);
            res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "MMBase not yet, or not successfully initialized (check mmbase log)");
        }
        return mmbaseInited;
    }




    /**
     * The service method is extended with calls for the refCount
     * functionality (for performance related debugging).  So you can
     * simply override doGet in extension classes, and this stays
     * working, without having to think about it.
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        if (!checkInited(res)) {
            return;
        }
        incRefCount(req);
        try {
            res.addHeader("X-Powered-By", org.mmbase.Version.get());
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
        if (queryString != null) result += "?" + queryString;
        return result;
    }


    /**
     * Decrease the reference count of the servlet
     * @param req The HttpServletRequest.
     */

    protected void decRefCount(HttpServletRequest req) {
        if (servletsLog.isDebugEnabled()) {
            String url = getRequestURL(req) + " " + req.getMethod();
            synchronized (servletCountLock) {
                servletCount--;
                ServletReferenceCount s = runningServlets.get(this);
                if (s != null) {
                    if (s.refCount == 0) {
                        runningServlets.remove(this);
                    } else {
                        s.refCount--;
                        int i = s.uris.indexOf(url);
                        if (i >= 0) s.uris.remove(i);
                    }

                }// s!=null
            }//sync
        }// if (logServlets)
    }

    /**
     * Increase the reference count of the servlet (for debugging)
     * and send running servlets to log once every 32 requests
     * @param req The HttpServletRequest.
     * @bad-constant  31 should be configurable.
     */

    protected void incRefCount(HttpServletRequest req) {
        if (servletsLog.isDebugEnabled()) {
            String url = getRequestURL(req) + " " + req.getMethod();
            int curCount;
            synchronized (servletCountLock) {
                servletCount++;
                curCount = servletCount;
                printCount++;
                ServletReferenceCount s = runningServlets.get(this);
                if (s == null) {
                    runningServlets.put(this, new ServletReferenceCount(url, 0));
                } else {
                    s.refCount++;
                    s.uris.add(url);
                }
            }// sync

            if ((printCount & 31) == 0) { // Why not (printCount % <configurable number>) == 0?
                if (curCount > 0) {
                    synchronized(servletCountLock) {
                        servletsLog.debug("Running servlets: " + curCount);
                        for (Object element : runningServlets.values())
                            servletsLog.debug(element);
                    }

                }// curCount>0
            }
        }
    }

    @Override
    public void destroy() {
        log.info("Servlet " + getServletName() + " is taken out of service");
        if (initThread != null) {
            initThread.interrupt();
        } else {
            log.debug(" " + getServletName() + " was not initialized");
        }
        log.debug("Disassociating this servlet with mappings");
        for (String mapping :  getServletMappings(getServletConfig().getServletName())) {
            mapToServlet.remove(mapping);
        }
        super.destroy();
         // for retrieving servletmappings, determine status
        synchronized (servletMappings) {

           servletInstanceCount--;
            if (servletInstanceCount == 0) {
                try {
                    log.info("Unloaded servlet mappings");
                    associatedServlets.clear();
                    servletMappings.clear();
                    log.info("No MMBase servlets left; modules can be shut down");
                    MMBase.getMMBase().shutdown();
                    Module.shutdownModules();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
                try {
                    ThreadGroup threads = MMBaseContext.getThreadGroup();
                    log.service("Send interrupt to " + threads.activeCount() + " threads in " +
                                threads + " of " + threads.getParent());
                    threads.interrupt();
                    Thread.yield();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
                try {
                    org.mmbase.util.FileWatcher.shutdown();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
                try {
                    org.mmbase.cache.CacheManager.shutdown();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
                try {
                    Logging.shutdown();
                } catch (Throwable t) {
                    System.err.println(t.getMessage());
                }
            }
        }
   }

    /**
     * This class maintains current state information for a running servlet.
     * It contains a reference count, as well as a list of URI's being handled by the servlet.
     */
    private class ServletReferenceCount {
        /**
         * List of URIs that call the servlet
         */
        final List<String> uris = new ArrayList<String>();
        /**
         * Nr. of references
         */
        int refCount;

        /**
         * Create a new ReferenceCountServlet using the jamesServlet
         */
        ServletReferenceCount(String uri, int refCount) {
            uris.add(uri);
            this.refCount = refCount;
        }

        /**
         * Return a description containing servlet info and URI's
         */
        public String toString() {
            return "servlet(" + MMBaseServlet.this + "), refcount(" + (refCount + 1) + "), uri's(" + uris + ")";
        }
    }


}
