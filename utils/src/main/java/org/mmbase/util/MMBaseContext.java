/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import java.text.DateFormat;

import org.mmbase.core.event.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Using MMBaseContext class you can retrieve the servletContext from anywhere
 * using the get method. (This class used to be {@link org.mmbase.module.core.MMBaseContext} before MMBase 2.0).
 *
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @author Jaco de Groot
 * @version $Id$
 */
public class MMBaseContext implements ServletContextListener {
    private static final Logger LOG     = Logging.getLoggerInstance(MMBaseContext.class);
    public  static final Logger INITLOG = Logging.getLoggerInstance("org.mmbase.INIT");
    private static boolean initialized = false;
    private static boolean shutdown = false;
    private static boolean htmlRootInitialized = false;
    private static ServletContext sx;
    private static String userDir;

    private static String htmlRoot;
    private static String htmlRootUrlPath = "/";
    private static boolean htmlRootUrlPathInitialized = false;
    private static String outputFile;

    private static String encoding = null;
    private static String host = null;
    private static String dataDirString;
    private static boolean shownDataDir = false;


    public static final int startTime = (int) (System.currentTimeMillis() / 1000);

    /**
     * Name of the machine used in the mmbase cluster.
     * it is used for the mmservers objects. Make sure that this is different
     * for each node in your cluster. This is not the machines dns name
     * (as defined by host as name or ip number).
     */
    private static String machineName = null;

    /**
     * Initialize MMBase using a <code>ServletContext</code>. This method will
     * check the servlet configuration for context parameters mmbase.outputfile
     * and mmbase.config. If not found it will look for system
     * properties.
     *
     * @throws ServletException  if mmbase.config is not set or is not a
     *                           directory or doesn't contain the expected
     *                           config files.
     *
     */
    public synchronized static void init(ServletContext servletContext) {
        if (!initialized ||
            (initialized && sx == null)) { // initialized, but with init(configPath)

            if (servletContext == null) {
                throw new IllegalArgumentException();
            }
            if (initialized) {
                LOG.info("Reinitializing, this time with ServletContext");
            }

            // store the current context
            sx = servletContext;
            LOG.service("Found servletContext " + sx);
            EventManager.getInstance().propagateEvent(new SystemEvent.ServletContext(sx), true);

            // Get the user directory using the user.dir property.
            // default set to the startdir of the appserver
            userDir = sx.getInitParameter("user.dir");
            if (userDir == null) {
                try {
                    userDir = System.getProperty("user.dir");
                } catch (SecurityException se) {
                    LOG.service(se.getMessage());
                }
            }
            // take into account userdir can start at webrootdir
            if (userDir != null && userDir.indexOf("$WEBROOT") == 0) {
                userDir = servletContext.getRealPath(userDir.substring(8));
            }
            // Init outputfile.
            String mmbaseOutputFile = sx.getInitParameter("mmbase.outputfile");
            if (mmbaseOutputFile == null) {
                try {
                    mmbaseOutputFile = System.getProperty("mmbase.outputfile");
                } catch (SecurityException se) {
                    LOG.debug(se.getMessage());
                }
            }
            // take into account that configpath can start at webrootdir
            if (mmbaseOutputFile != null && mmbaseOutputFile.indexOf("$WEBROOT") == 0) {
                mmbaseOutputFile = servletContext.getRealPath(mmbaseOutputFile.substring(8));
            }
            initOutputfile(mmbaseOutputFile);

            // Init logging.
            String initLoggingParam = sx.getInitParameter("mmbase.initlogging");
            if (initLoggingParam == null || "true".equals(initLoggingParam)) {
                // resource loader is initialized asynchrously, logging can only be intialized when it is ready.
                EventManager.getInstance().addEventListener(new SystemEventListener() {
                    @Override
                    public void notify(SystemEvent se) {
                        if (se instanceof SystemEvent.ResourceLoaderChange) {
                            initLogging();
                        }
                    }
                    @Override
                    public int getWeight() {
                        return 10000;
                    }
                });

            }
            initialized = true;
            try {
                initHtmlRoot();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

        }
    }

    /**
     * Initialize MMBase using a config path. Useful when testing
     * MMBase classes with a main. You can also configure to init
     * logging or not.
     *
     * @throws Exception  if mmbase.config is not set or is not a
     *                    directory or doesn't contain the expected
     *                    config files.
     *
     */
    public synchronized static void init(String configPath, boolean initLogging) throws Exception {
        if (!initialized) {
            LOG.service("Initializing with " + configPath);
            // Get the current directory using the user.dir property.
	    try {
		userDir = System.getProperty("user.dir");
	    } catch (SecurityException se) {
		LOG.info(se.getMessage());
	    }

            // Init outputfile. // use of mmbase.outputfile  is deprecated!
	    try {
		initOutputfile(System.getProperty("mmbase.outputfile"));
	    } catch (SecurityException se) {
		LOG.info(se.getMessage());
	    }

            // Init logging.
            if (initLogging) {
                initLogging();
            }
            initialized = true;
       }
    }

    /**
     * Initialize MMBase using system properties only. This may be useful in
     * cases where MMBase is used without a servlet. For example when running
     * JUnit tests.
     *
     * @throws Exception  if mmbase.config is not set or is not a
     *                    directory or doesn't contain the expected
     *                    config files.
     */
    public synchronized static void init() throws Exception {
        init(System.getProperty("mmbase.config"), true);
    }

    /**
     * Returns the MMBase thread group.
     * @since MMBase-1.8
     */
    public static ThreadGroup getThreadGroup() {
        return org.mmbase.util.ThreadPools.threadGroup;
    }

    /**
     * Starts a daemon thread using the MMBase thread group.
     * @param task the task to run as a thread
     * @param name the thread's name
     * @since MMBase-1.8
     */
    public static Thread startThread(Runnable task, String name) {
        Thread kicker = new Thread(getThreadGroup(), task, getMachineName() + ":" + name);
        kicker.setDaemon(true);
        kicker.start();
        return kicker;
    }
    private static void initOutputfile(String o) {
        outputFile = o;
        if (outputFile != null) {
            if (!new File(outputFile).isAbsolute()) {
                outputFile = userDir + File.separator + outputFile;
            }
            try {
                 PrintStream stream = new PrintStream(new FileOutputStream(outputFile, true));
                 System.setOut(stream);
                 System.setErr(stream);
            } catch (IOException e) {
                 outputFile = null;
                 LOG.error("Failed to set mmbase.outputfile to '" + outputFile + "'.");
                 LOG.error(Logging.stackTrace(e));
            }
        }
    }

    private static void initLogging() {
        // Starting the logger
        Logging.configure(ResourceLoader.getConfigurationRoot().getChildResourceLoader("log"), "log.xml");
        INITLOG.info("===========================");
        INITLOG.info("MMBase logging initialized.");
        INITLOG.info("===========================");
        try {
            INITLOG.info("java.version       : " +  System.getProperty("java.version"));
        } catch (SecurityException se) {
            INITLOG.info("java.version       : " +  se.getMessage());
        }

        INITLOG.info("user.dir          : " + userDir);
        String configPath = ResourceLoader.getConfigurationRoot().toString();
        INITLOG.info("configuration     : " + configPath);
        INITLOG.info("webroot           : " + ResourceLoader.getWebRoot());
        String version = org.mmbase.Version.get();
        INITLOG.info("version           : " + version);
        Runtime rt = Runtime.getRuntime();
        INITLOG.info("total memory      : " + rt.totalMemory() / (1024 * 1024) + " MiB");
        INITLOG.info("free memory       : " + rt.freeMemory() / (1024 * 1024) + " MiB");
        INITLOG.service("system locale     : " + Locale.getDefault());
        {
            boolean assertsEnabled = false;
            assert assertsEnabled = true; // Intentional side effect!!!
            if (assertsEnabled) {
                INITLOG.info("Assertions are enabled");
            }
        }

        INITLOG.info("start time        : " + DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(1000 * (long) startTime)));
    }

    /**
     * Initialize mmbase.htmlroot parameter.
     * If the mmbase.htmlroot parameter is not found in the servlet
     * context or system prooperties this method will try to set it to the
     * root directory of the webapp.
     *
     * @throws ServletException  if mmbase.htmlroot is not set or is not a
     *                           directory
     *
     */
    public synchronized static void initHtmlRoot() throws ServletException {
        if (!initialized) {
            throw new RuntimeException("The init(ServletContext) method should be called first. (Not initalized)");
        }
        if (null == sx) {
            throw new RuntimeException("The init(ServletContext) method should be called first. (No servlet context was given)");
        }
        if (!htmlRootInitialized) {
            // Init htmlroot.
            htmlRoot = sx.getInitParameter("mmbase.htmlroot");
            if (htmlRoot == null) {
                try {
                    htmlRoot = System.getProperty("mmbase.htmlroot");
                } catch (SecurityException se) {
                    LOG.debug(se);
                }
            }
            if (htmlRoot == null) {
                htmlRoot = sx.getRealPath("");
            }
            if (htmlRoot == null){
                LOG.service("Parameter mmbase.htmlroot not set.");
            } else {
                if (userDir != null && !new File(htmlRoot).isAbsolute()) {
                    htmlRoot = userDir + File.separator + htmlRoot;
                }
                if (!new File(htmlRoot).isDirectory()) {
                    userDir = null;
                    htmlRoot = null;
                    throw new ServletException("Parameter mmbase.htmlroot is not pointing to a directory.");
                } else {
                    if (htmlRoot.endsWith(File.separator)) {
                        htmlRoot = htmlRoot.substring(0, htmlRoot.length() - 1);
                    }
                }
            }
            htmlRootInitialized = true;
            INITLOG.info("mmbase.htmlroot   : " + htmlRoot);
            INITLOG.info("context           : " + getHtmlRootUrlPath());
        }
    }

    /**
     * Returns the <code>ServletContext</code> used to initialize MMBase.
     * Before calling this method the init method should be called.
     *
     * @return  the <code>ServletContext</code> used to initialize MMBase or
     *          <code>null</code> if MMBase was initialized without
     *          <code>ServletContext</code>
     */
    public static ServletContext getServletContext() {
        if (!initialized) {
            throw new RuntimeException("The init method should be called first.");
        }
        return sx;
    }

    /**
     * Returns a string representing the mmbase.config parameter without a
     * final <code>File.separator</code>. Before calling this method the
     * init method should be called to make sure this parameter is set.
     *
     * @return  the mmbase.config parameter or WEB-INF/config
     * @deprecated use {@link org.mmbase.util.ResourceLoader#getConfigurationRoot} with relative path
     */
    public   static String getConfigPath() {
        List<File> files =  ResourceLoader.getConfigurationRoot().getFiles("");
        if (files.isEmpty()) {
            return null;
        } else {
            return files.get(0).getAbsolutePath();
        }
    }

    /**
     * Returns a string representing the mmbase.htmlroot parameter without a
     * final <code>File.separator</code>. Before calling this method the
     * initHtmlRoot method should be called to make sure this parameter is set.
     *
     * @return  the mmbase.htmlroot parameter or <code>null</code> if not
     *          initialized
     */
    public static String getHtmlRoot() {
        if (!htmlRootInitialized) {
            throw new RuntimeException("The initHtmlRoot method should be called first.");
        }
       return htmlRoot;
    }

    /**
     * @since MMBase-2.0
     */
    public static boolean isHtmlRootInitialized() {
        return htmlRootInitialized;
    }

    /**
     * Returns a string representing the mmbase.outputfile parameter. If set,
     * this is the file to wich all <code>System.out</code> and
     * <code>System.err</code> output is redirected. Before calling this method
     * the init method should be called.
     *
     * @return  the mmbase.outputFile parameter or <code>null</code> if not set
     * @deprecated use logging system
     */
    public static String getOutputFile() {
        if (!initialized) {
            throw new RuntimeException("The init method should be called first.");
        }
        return outputFile;
    }

    /**
     * Returns a string representing the HtmlRootUrlPath, this is the path under
     * the webserver, what is the root for this instance.
     * this will return '/' or something like '/mmbase/' or so...
     *
     * This information should be requested from the ServletRequest, but if for some reason you
     * don't have one handy, this method can be used.
     * When using Servlet 2.5, this is the same as {@link #getServletContext()}.getContextPath() +
     * "/", so this <em>also ends in a /</em>
     * @return  the HtmlRootUrlPath
     */
    public  static String getHtmlRootUrlPath() {
        if (! htmlRootUrlPathInitialized) {
            LOG.debug("Finding root url");
            if (! initialized) {
                throw new RuntimeException("The init method should be called first.");
            }
            if (sx == null) { // no serlvetContext -> no htmlRootUrlPath
                htmlRootUrlPathInitialized = true;
                return htmlRootUrlPath;
            }
            String initPath = sx.getInitParameter("mmbase.htmlrooturlpath");
            if (initPath != null) {
                LOG.debug("Found mmbase.htmlrooturlpath  explicitely configured");
                htmlRootUrlPath = initPath;
            } else {
                // init the htmlRootUrlPath
                try {
                    LOG.debug("Autodetecting htmlrooturlpath ");
                    // fetch resource path for the root servletcontext root...
                    // check wether this is root
                    if (sx.equals(sx.getContext("/"))) {
                        htmlRootUrlPath = "/";
                    } else if (sx.getMajorVersion() > 2 || (sx.getMajorVersion() == 2 && sx.getMinorVersion() >= 5)) {
                        try {
                            htmlRootUrlPath = sx.getClass().getMethod("getContextPath").invoke(sx) + "/";
                        } catch(Exception e) {
                            LOG.error(e);
                        }
                    } else {
                        String url = sx.getResource("/").toString();
                        // MM: simply hope that it is the last part of that URL.
                        // I do not think it is garantueed. Used mmbase.htmlrooturlpath in web.xml if it doesn't work.
                        int length = url.length();
                        int lastSlash = url.substring(0, length - 1).lastIndexOf('/');
                        if (lastSlash > 0) {
                            htmlRootUrlPath = url.substring(lastSlash);
                            LOG.info("Found " + htmlRootUrlPath + " from " + url);
                        } else {
                            LOG.warn("Could not determine htmlRootUrlPath. Using default " + htmlRootUrlPath + "(contextUrl     :" + url + ")");
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e);
                }
                try {
                    ServletContext refound = sx.getContext(htmlRootUrlPath);
                    if (refound != null && !sx.equals(refound)) {
                        LOG.warn("Probably did not succeed in determining htmlRootUrlPath ('" + htmlRootUrlPath + "', because " + sx + "!= " + refound + "). Consider using the mmbase.htmlrooturlpath  context-param in web.xml");
                    }
                } catch (Exception e2) {
                    LOG.error(e2);
                }
            }
            htmlRootUrlPathInitialized = true;
        }
        return htmlRootUrlPath;
    }


    /**
     * Returns whether this class has been initialized.
     * This can be used to determine whether MMBase specific configuration data is accessible.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the unique identifier for the MMBase instance. Used for clustering.
     * @since MMBase-1.8.7
     * @return 'machine name' to identify this web app or <code>null</code> if not yet determined.
     */
    public static String getMachineName() {
        return machineName;
    }

    /**
     * @since MMBase-2.0
     */
    public static void setMachineName(String nm) {
        if (machineName != null && ! machineName.equals(nm)) throw new IllegalStateException();
        machineName = nm;
    }
    /**
     * @since MMBase-2.0
     */
    public static void shutdown() {
        shutdown = true;
        org.mmbase.core.event.EventManager.getInstance().propagateEvent(new SystemEvent.Shutdown());
    }

    /**
     * @since MMBase-2.0
     */
    public static boolean isShutdown() {
        return shutdown;
    }

    /**
     * @since MMBase-2.0
     */
    public static String getEncoding() {
        return encoding == null ? "ISO-8859-1" : encoding;
    }

    /**
     * @since MMBase-2.0
     */
    public static void setEncoding(String e) {
        if (encoding != null && ! encoding.equals(e)) throw new IllegalStateException();
        encoding = e;
    }
    /**
     * The host or ip number of the machine this module is
     * running on. Its important that this name is set correctly because it is
     * used for communication between mmbase nodes and external devices
     * @since MMBase-2.0
     */
    public static String getHost() {
        return host == null ? "unknown": host;
    }
    /**
     * @since MMBase-2.0
     */
    public static void setHost(String h) {
        if (host != null && ! host.equals(h)) throw new IllegalStateException();
        host = h;
    }

    /**
     * @since MMBase-2.0
     */
    public static File getDataDir() {

        javax.servlet.ServletContext sc = MMBaseContext.getServletContext();
        if (dataDirString == null) {
            if (sc == null) {
                dataDirString = "data";
            } else {
                dataDirString = "WEB-INF/data";
            }
        }
        File dataDir = new File(dataDirString);

        if (! dataDir.isAbsolute()) {
            if (sc != null && sc.getRealPath("/" + dataDirString) != null) {
                LOG.debug(" "  + sc.getRealPath("/" + dataDirString));
                dataDir = new File(sc.getRealPath("/" + dataDirString));
            } else {
                dataDir = new File(System.getProperty("user.dir"), dataDirString);
            }
        }

        if (! dataDir.exists()) {
            try {
                if (dataDir.mkdirs()) {
                    LOG.info("Created " + dataDir);
                }
            } catch (SecurityException  se) {
                LOG.warn(se);
            }
        }

        if (! dataDir.isDirectory()) {
            LOG.warn("Datadir " + dataDir + " is not a directory");
        }
        if (! dataDir.canRead()) {
            LOG.warn("Datadir " + dataDir + " is not readable");
        }
        {
            boolean  canWrite = false;
            try {
                canWrite = dataDir.canWrite();
            } catch (SecurityException se) {
            }
            if (! canWrite) {
                try {
                    File proposal = sc != null ? (File) sc.getAttribute("javax.servlet.context.tempdir") : new File(System.getProperty("java.io.tmpdir"));
                    if (proposal.canWrite()) {
                        LOG.warn("Datadir " + dataDir + " is not writable. Falling back to " + proposal);
                        dataDir = proposal;
                    } else {
                        LOG.warn("Datadir " + dataDir + " is not writable.");
                    }
                } catch (SecurityException se) {
                    LOG.warn(se.getMessage(), se);
                }

            }
        }
        if (shownDataDir) {
            LOG.debug("MMBase data dir: " + dataDir);
        } else {
            LOG.info("MMBase data dir: " + dataDir);
            shownDataDir = true;
        }
        return dataDir;

    }
    /**
     * @since MMBase-2.0
     */
    public static void setDataDir(String setting) {
        if (dataDirString != null && ! dataDirString.equals(setting)) throw new IllegalStateException();
        if ("".equals(setting)) setting = null;
        dataDirString = setting;
        if (! new File(dataDirString).canWrite()) {
            LOG.warn("The data dir " + dataDirString + " is not writable");
        }
    }


    /**
     * @since MMBase-2.0
     */
    @Override
    public void	contextDestroyed(ServletContextEvent sce) {
        shutdown();
    }

    /**
     * @since MMBase-2.0
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.debug("Received " + sce);
        init(sce.getServletContext());
    }

}
