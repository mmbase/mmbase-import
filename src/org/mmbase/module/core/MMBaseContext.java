/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMBaseContext.java,v 1.16 2001-09-25 15:47:26 eduard Exp $

$Log: not supported by cvs2svn $
Revision 1.15  2001/08/16 14:59:19  pierre
pierre: fixed the check for configuration security files

Revision 1.14  2001/07/24 11:04:38  jaco
jaco: Throw a RunTimeException if a get method is called before the init method.

Revision 1.13  2001/07/16 10:08:08  jaco
jaco: Moved all configuration stuff to MMBaseContext.
If needed params not found or incorrect a ServletException with a description isthrown.
It's now again possible to not redirect System.out and System.err to a file.
Parameters are searched in the webapp (using context-param parameters) when started using a servlet.
If htmlroot is not specified MMBaseContext will try to set it to the webapp root directory.

Revision 1.12  2001/06/26 08:39:19  eduard
eduard : i want to use logging, so now the value will be set to "mmbase.log" incase there is nothing known of the log file

Revision 1.11  2001/06/26 07:52:13  pierre
pierre: removed (commented out) recursive call to getLogging() in getOutputFile(), which caused MMBase to crash on startup.
I suspect this is the correct way to fix this bug, but someone else might need to verify this.

Revision 1.10  2001/06/23 18:07:27  daniel
oops forgot something

Revision 1.9  2001/06/23 16:13:46  daniel
added support for servlet params

Revision 1.8  2001/04/10 17:32:05  michiel
michiel: new logging system

Revision 1.7  2000/12/24 23:26:25  daniel
removed modules.xml warning

Revision 1.6  2000/10/15 22:50:25  gerard
gerard: added some checks
submitted by Eduard Witteveen

Revision 1.5  2000/03/30 13:11:39  wwwtech
Rico: added license

Revision 1.4  2000/03/29 10:48:19  wwwtech
Rob: Licenses changed

Revision 1.3  2000/02/24 14:40:44  wwwtech
Davzev added CVS again

Revision 1.2  2000/02/24 13:57:38  wwwtech
Davzev added CVS comment.

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.*;
import org.mmbase.module.database.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Using MMBaseContext class you can retrieve the servletContext from anywhere
 * using the get method.
 *
 * @version 23 December 1999
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @author Jaco de Groot
 * @$Revision: 1.16 $ $Date: 2001-09-25 15:47:26 $
 */
public class MMBaseContext {
    private static Logger log;
    static boolean initialized = false;
    static boolean htmlRootInitialized = false;
    static ServletContext sx;
    static String userdir;
    static String configpath;
    static String htmlroot;
    static String outputfile;

    /**
     * Initialize MMBase using a <code>SevletContext</code>. This method will
     * check the servlet configuration for context parameters mmbase.outputfile
     * and mmbase.config. If not found it will look for system properties.
     *
     * @throws ServletException  if mmbase.configpath is not set or is not a
     *                           directory or doesn't contain the expected
     *                           config files.
     *
     */
    public synchronized static void init(ServletContext servletContext) throws ServletException {
        if (!initialized) {
            sx = servletContext;
            // Get the current directory using the user.dir property.
            userdir = System.getProperty("user.dir");
            // Init outputfile.
            outputfile = sx.getInitParameter("mmbase.outputfile");
            if (outputfile == null) {
                outputfile = System.getProperty("mmbase.outputfile");
            }
            initOutputfile();
            // Init configpath.
            configpath = sx.getInitParameter("mmbase.config");
            if (configpath == null) {
                configpath = System.getProperty("mmbase.config");
            }
            if (configpath == null) {
	    	// desperate looking for a location.. (say we are a war file..)
		// keeping the value 'null' will always give a failure..
                configpath =  servletContext.getRealPath("WEB-INF/config");
            }
            try {
                initConfigpath();
            } catch(Exception e) {
                throw new ServletException(e.getMessage());
            }
            // Init logging.
            initLogging();
            initialized = true;
        }
    }

    /**
     * Initialize MMBase using system properties only. This may be useful in
     * cases where MMBase is used without a servlet. For example when running
     * JUnit tests.
     *
     * @throws Exception  if mmbase.configpath is not set or is not a
     *                    directory or doesn't contain the expected
     *                    config files.
     *
     */
    public synchronized static void init() throws Exception {
        if (!initialized) {
            // Get the current directory using the user.dir property.
            userdir = System.getProperty("user.dir");
            // Init outputfile.
            outputfile = System.getProperty("mmbase.outputfile");
            initOutputfile();
            // Init configpath.
            configpath = System.getProperty("mmbase.config");
            initConfigpath();
            // Init logging.
            initLogging();
            initialized = true;
        }
    }

    private static void initOutputfile() {
        if (outputfile != null) {
            if (!new File(outputfile).isAbsolute()) {
                outputfile = userdir + File.separator + outputfile;
            }
            try {
                 FileOutputStream fos;
                 fos = new FileOutputStream(outputfile, true);
                 PrintStream mystream = new PrintStream(fos);
                 System.setOut(mystream);
                 System.setErr(mystream);
            } catch (IOException e) {
                 outputfile = null;
                 System.err.println("Failed to set mmbase.outputfile to '"
                                    + outputfile + "'.");
                 e.printStackTrace();
            }
        }
    }

    private static void initConfigpath() throws Exception {
        if (configpath == null) {
            userdir = null;
            configpath = null;
            String message = "Parameter mmbase.config not set.";
            System.err.println(message);
            throw new Exception(message);
        }
        File fileConfigpath = new File(configpath);
        if (userdir != null && !fileConfigpath.isAbsolute()) {
            configpath = userdir + File.separator + configpath;
            fileConfigpath = new File(configpath);
        } 
        // Make it absolute. Needed for servscan and servdb to
        // to startup properly.
        configpath = fileConfigpath.getAbsolutePath();
        if (!fileConfigpath.isDirectory()) {
            userdir = null;
            configpath = null;
            String message = "Parameter mmbase.config is not pointing to "
                             + "a directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if(!new File(configpath + "/security/security.xml").isFile()) {
            userdir = null;
            configpath = null;
            String message = "File 'security/security.xml' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
    /*
        if(!new File(configpath + "/accounts.properties").isFile()) {
            userdir = null;
            configpath = null;
            String message = "File 'accounts.properties' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
    */
        if(!new File(configpath + "/builders").isDirectory()) {
            userdir = null;
            configpath = null;
            String message = "Directory 'builders' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if(!new File(configpath + "/modules").isDirectory()) {
            userdir = null;
            configpath = null;
            String message = "Directory 'modules' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if(!new File(configpath + "/modules/mmbaseroot.xml").isFile()) {
            userdir = null;
            configpath = null;
            String message = "File 'modules/mmbaseroot.xml' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if(!new File(configpath + "/modules/jdbc.xml").isFile()) {
            userdir = null;
            configpath = null;
            String message = "File 'modules/jdbc.xml' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if(!new File(configpath + "/log/log.xml").isFile()) {
            userdir = null;
            configpath = null;
            String message = "File 'log/log.xml' missing in "
                             + "mmbase.config directory("+fileConfigpath.getAbsolutePath()+").";
            System.err.println(message);
            throw new Exception(message);
        }
        if (configpath.endsWith(File.separator)) {
            configpath = configpath.substring(0, configpath.length() - 1);
        }
    }

    private static void initLogging() {
        Logging.configure(configpath + "/log/log.xml");
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(MMBaseContext.class.getName());
        log.info("===========================");
        log.info("MMBase logging initialized.");
        log.info("===========================");
        log.info("user.dir          : " + userdir);
        log.info("mmbase.config     : " + configpath);
        log.info("mmbase.outputfile : " + outputfile);
    }

    /**
     * Initialize mmbase.htmlroot parameter. This method is only needed for
     * SCAN related servlets and should be called after the init(ServletContext)
     * method. If the mmbase.htmlroot parameter is not found in the servlet
     * context or system properties this method will try to set it to the
     * root directory of the webapp.
     *
     * @throws ServletException  if mmbase.htmlroot is not set or is not a
     *                           directory
     *
     */
    public synchronized static void initHtmlRoot() throws ServletException {
        if (!initialized || sx == null) {
            String message = "The init(ServletContext) method should be called"
                                       + " first.";
            System.err.println(message);
            throw new RuntimeException(message);
        }
        if (!htmlRootInitialized) {
            // Init htmlroot.
            htmlroot = sx.getInitParameter("mmbase.htmlroot");
            if (htmlroot == null) {
                htmlroot = System.getProperty("mmbase.htmlroot");
            }
            if (htmlroot == null) {
                htmlroot = sx.getRealPath("");
            }
            if (htmlroot == null) {
                String message = "Parameter mmbase.htmlroot not set.";
                System.err.println(message);
                throw new ServletException(message);
            } else {
                if (userdir != null && !new File(htmlroot).isAbsolute()) {
                    htmlroot = userdir + File.separator + htmlroot;
                }
                if (!new File(htmlroot).isDirectory()) {
                    userdir = null;
                    configpath = null;
                    htmlroot = null;
                    String message = "Parameter mmbase.htmlroot is not pointing "
                                     + "to a directory.";
                    System.err.println(message);
                    throw new ServletException(message);
                } else {
                    if (htmlroot.endsWith(File.separator)) {
                        htmlroot = htmlroot.substring(0, htmlroot.length() - 1);
                    }
                    htmlRootInitialized = true;
                    log.info("mmbase.htmlroot   : " + htmlroot);
                }
            }
        }
    }

    /**
     * Returns the <code>ServeltContext</code> used to initialize MMBase.
     * Before calling this method the init method should be called.
     *
     * @return  the <code>ServeltContext</code> used to initialize MMBase or
     *          <code>null</code> if MMBase was initilized without
     *          <code>ServletContext</code>
     */
    public synchronized static ServletContext getServletContext() {
        if (!initialized) {
            String message = "The init method should be called first.";
            System.err.println(message);
            throw new RuntimeException(message);
        }
        return sx;
    }

    /**
     * Returns a string representing the mmbase.config parameter without a
     * final <code>File.separator</code>. Before calling this method the
     * init method should be called to make sure this parameter is set.
     *
     * @return  the mmbase.config parameter
     */
    public synchronized static String getConfigPath() {
        if (!initialized) {
            String message = "The init method should be called first.";
            System.err.println(message);
            throw new RuntimeException(message);
        }
        return configpath;
    }

    /**
     * Returns a string representing the mmbase.htmlroot parameter without a
     * final <code>File.separator</code>. Before calling this method the
     * initHtmlRoot method should be called to make sure this parameter is set.
     *
     * @return  the mmbase.htmlroot parameter or <code>null</code> if not
     *          initialized
     */
    public synchronized static String getHtmlRoot() {
        if (!htmlRootInitialized) {
            String message = "The initHtmlRoot method should be called first.";
            System.err.println(message);
            throw new RuntimeException();
        }
       return htmlroot;
    }

    /**
     * Returns a string representing the mmbase.outputfile parameter. If set,
     * this is the file to wich all <code>System.out</code> and
     * <code>System.err</code> output is redirected. Before calling this method
     * the init method should be called.
     *
     * @return  the mmbase.htmlroot parameter or <code>null</code> if not set
     */
    public synchronized static String getOutputFile() {
        if (!initialized) {
            String message = "The init method should be called first.";
            System.err.println(message);
            throw new RuntimeException(message);
        }
        return outputfile;
    }

}
