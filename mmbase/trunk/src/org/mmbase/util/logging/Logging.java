/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.util.Iterator;
import java.io.File;
import java.lang.reflect.Method;

import java.net.URL;
import org.mmbase.util.XMLBasicReader;
import org.xml.sax.InputSource;

/** 
 * With this class the logging is configured and it supplies the `Logger' objects.
 * <p>
 * For example:
 * <code>
 * <pre>
 * <tt>
 * <b><font color=#0000FF>import</font></b> org.mmbase.util.logging.Logging;
 * <b><font color=#0000FF>import</font></b> org.mmbase.util.logging.Logger;
 * <b><font color=#0000FF>import</font></b> org.mmbase.util.logging.Level;
 *
 * <b><font color=#0000FF>public</font></b> <b><font color=#0000FF>class</font></b> test {
 * 
 *     <b><font color=#0000FF>static</font></b> {
 *         Logging.configure(<font color=#FF0000>"log.xml"</font>);
 *     }
 * 
 *     <b><font color=#0000FF>static</font></b> Logger log = Logging.getLoggerInstance(test.<b><font color=#0000FF>class</font></b>.getName());
 *   		
 *     <b><font color=#0000FF>public</font></b> <b><font color=#0000FF>static</font></b> <font color=#009900>void</font> main(String[] args) { 		 
 *         log.debug(<font color=#FF0000>"start"</font>);
 *         log.info(<font color=#FF0000>"Entering application."</font>);
 *	   
 *         log.setPriority(Level.TRACE);
 *         <b><font color=#0000FF>if</font></b> (log.isDebugEnabled()) {
 *             log.debug(<font color=#FF0000>"debug een"</font>);
 *             log.trace(<font color=#FF0000>"trace twee"</font>);
 *         }
 *         log.info(<font color=#FF0000>"info"</font>);		
 *         log.service(<font color=#FF0000>"service"</font>);
 * 
 *
 *         Logging.shutdown();
 *     }
 * }
 * </tt>
 * </pre>
 * </code>
 * </p>
 *
 * @author Michiel Meeuwissen
 * @version $Id: Logging.java,v 1.28 2004-02-19 17:32:10 michiel Exp $
 */


public class Logging {

    private static Class  logClass          = SimpleTimeStampImpl.class; // default Logger Implementation
    private static File   configurationFile = null;             // Logging is configured with a configuration file. The path of this file can be requested later.
    private static boolean configured = false;
    private static final Logger log = getLoggerInstance(Logging.class); // logger for this class itself

    /**
     * The category for logging info about pages (like stop / start). Also if pages take the
     * initiative for logging themselves they should log below this category.
     * @since MMBase-1.7
     */
    public final static String PAGE_CATEGORY = "org.mmbase.PAGE";      

    private Logging() {
        // this class has no instances.
    }


    /**
     * Configure the logging system.
     *
     * @param configfile Path to an xml-file in which is described
     * which class must be used for logging, and how this will be
     * configured (typically the name of another configuration file).  
     *
     */
    
    public  static void configure (String configfile) {
        
        if (configfile == null) {
            log.info("No configfile given, default configuration will be used.");
            return;
        }  
        if (configured == true) {
            log.warn("Reconfiguring logging. This is not really possible (most static instances are unreachable");
        }

       // There is a problem when dtd's for the various modules are on a remote
        // machine and this machine is down. Log4j will hang without an error and if
        // SimpleImpl is used in log.xml it too will constantly try to connect to the
        // machine for the dtd's without giving an error! This line might give a hint 
        // where to search for these kinds of problems..
        
        log.info("Configuring logging with " + configfile);
        ///System.out.println("(If logging does not start then dtd validation might be a problem on your server)");



        configurationFile = new File(configfile);
        configurationFile = configurationFile.getAbsoluteFile();
        
        if (! configurationFile.exists() || 
            ! configurationFile.isFile() ||
            ! configurationFile.canRead() ) { // not a readable file, return and warn that logging cannot be configured.
            log.warn("Log configuration file is not accessible, default logging implementation will be used.");
            return;
        }
   
        // Convert the file to a system-dependant URL string for the parser to use
        try {
            URL logURL = configurationFile.toURL();
            configfile = logURL.toString();
        } catch (Exception e) {
            log.error("Cannot get URL from file " + configfile + " : " + e.toString());
            // that doesn't work, so let's try to do it ourselves
            configfile = "file:///" + configurationFile.getAbsolutePath();
        }                               


        XMLBasicReader reader = new XMLBasicReader(new InputSource(configfile), Logging.class);

        String classToUse    = "org.mmbase.util.logging.SimpleImpl"; // default
        String configuration = "stderr,debug";                        // default
        try { // to read the XML configuration file            
           String claz = reader.getElementValue("logging.class");
            if (claz != null) classToUse = claz;
            String config = reader.getElementValue("logging.configuration");
            if (config != null) configuration = config;
        } catch (Exception e) {
            log.error("Exception during parsing: " + e);
            log.error(stackTrace(e));
        }

       
        log.info("Class to use for logging " + classToUse);
        // System.out.println("(Depending on your selected logging system no more logging");
        // System.out.println("might be written to this file. See the configuration of the");
        // System.out.println("selected logging system for more hints where logging will appear)");
        Class logClassCopy = logClass; // if something's wrong, we can restore the current value.
        try { // to find the configured class
            logClass = Class.forName(classToUse);
        } catch (ClassNotFoundException e) {
            log.error("Could not find class " + classToUse);
            log.error(e.toString());
            logClass = logClassCopy;
        } catch (Throwable e) {
            log.error("Exception to find class " + classToUse + ": " +  e);
            log.info("Falling back to " + logClassCopy.getName());
            logClass = logClassCopy;
        }
        // System.out.println("logging to " + getLocations());
        configureClass(configuration);
        configured = true;
        log.service("Logging configured");
        log.debug("Replacing wrappers " + LoggerWrapper.getWrappers());
        Iterator wrappers = LoggerWrapper.getWrappers().iterator();
        while (wrappers.hasNext()) {
            LoggerWrapper wrapper = (LoggerWrapper) wrappers.next();
            wrapper.setLogger(getLoggerInstance(wrapper.getName()));
            log.debug("Replaced logger " + wrapper.getName());
        }
    }

    /** 
     * Calls the 'configure' static method of the used logging class,
     * or does nothing if it doesn't exist. You could call this method
     * if you want to avoid using 'configure', which parses an XML file.
     **/

    public static void configureClass(String configuration) {
        try { // to configure
            // System.out.println("Found class " + logClass.getName());
            Method conf = logClass.getMethod("configure", new Class[] { String.class } ); 
            conf.invoke(null, new String[] { configuration } );    
        } catch (NoSuchMethodException e) {
            //System.err.println("Could not find configure method in " + logClass.getName());
            // okay, simply don't configure
        } catch (java.lang.reflect.InvocationTargetException e) {
            System.err.println("Invocation Exception while configuration class. " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e);
        } 
    }

    /**
     * Logging is configured with a log file. This method returns the File which was used.
     */
    public static File getConfigurationFile() {
        return configurationFile;
    }
    /**
     * After configuring the logging system, you can get Logger instances to log with. 
     *
     * @param s A string describing the `category' of the Logger. This is a log4j concept.
     */

    public  static Logger getLoggerInstance (String s) {   
        // call the getLoggerInstance static method of the logclass:
        try {
            Method getIns = logClass.getMethod("getLoggerInstance", new Class[] { String.class } );
            Logger logger =  (Logger) getIns.invoke(null, new String[] {s}); 
            if (configured) {
                return logger;
            } else {
                return new LoggerWrapper(logger, s);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return null; // should not come here.
    }

    /**
     * Most Logger categories in MMBase are based on class name.
     * @since MMBase-1.6.4
     */
    public static Logger getLoggerInstance(Class cl) {
        return getLoggerInstance(cl.getName());
    }

    /**
     * Returns a Set of String which indicates where your logging can
     * be (If this is implemented in the class).
     */ 
    /*
    public  static Set getLocations() {   
        // call the getLoggerInstance static method of the logclass:
        try {
            Method getIns = logClass.getMethod("getLocations", new Class[] {} );
            return  (Set) getIns.invoke(null, new Object[] {}); 
        } catch (Exception e) {
            HashSet result = new HashSet();
            result.add("<could not be determined>");
            return result;
        }
    }
    */

    /** 
     * If the configured Logger implements a shutdown static method,
     * it will be called. (the log4j Category does).
     *
     */
    public static void shutdown() {
        try {
            Method shutdown = logClass.getMethod("shutdown", new Class[] {} ); 
            shutdown.invoke(null, new String[] {} );    
        } catch (NoSuchMethodException e) {
            // System.err.println("No such method"); // okay, nothing to shutdown.
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    /**
     * Returns the stacktrace of the current call. This can be used to get a stacktrace
     * when no exception was thrown and my help determine the root cause of an error message
     * (what class called the method that gave the error message.
     *
     **/
    public static String stackTrace() {
        return stackTrace(-1);
    } 

    /**
     * @since MMBase-1.7
     */
    public static String stackTrace(int max) {
        Exception e = new Exception("logging.stacktrace");
        /*
        StackTraceElement[] stack = e.getStackTrace();
        java.util.List stackList = new java.util.ArrayList(java.util.Arrays.asList(stack));
        stackList.remove(0); // is Logging.stackTrace, which is hardly interesting
        e.setStackTrace((StackTraceElement[])stackList.toArray());
        */
        return stackTrace(e, max);
    } 

    /**
     * Returns the stacktrace of an exception as a string, which can
     * be logged handy.  Doing simply e.printStackTrace() would dump
     * the stack trace to standard error, which with the log4j
     * implementation will appear in the log file too, but this is a
     * little nicer. 
     * 
     * It is also possible to call 'error' or 'fatal' with an extra argument. 
     *
     * @param e the Throwable from which the stack trace must be stringified.
     *
     **/
    public static String stackTrace(Throwable e) {
        return stackTrace(e, -1);
    } 

    /**
     * Also returns a stringified stack trace to log, but no deeper than given max.
     * @since MMBase-1.7
     */
    public static String stackTrace(Throwable e, int max) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        String message = e.getMessage();
        StringBuffer buf = new StringBuffer(e.getClass().getName() + ": ");
        if (message == null) {

        }  else {
            buf.append(message);
        }
        for (int i = 0; i < stackTrace.length; i++) {
            if (i == max) break;
            buf.append("\n        at ").append(stackTrace[i]);
        }
        Throwable t = e.getCause();
        if (t != null) {
            buf.append(stackTrace(t, max));
        }
        return buf.toString();
    } 

}
