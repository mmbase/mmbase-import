/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.util.*;
import java.lang.reflect.Method;

import org.mmbase.util.ResourceWatcher;
import org.mmbase.util.ApplicationContextReader;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.DocumentReader;

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
 * @version $Id$
 */


public class Logging {

    private static Class  logClass    = SimpleTimeStampImpl.class; // default Logger Implementation
    private static boolean configured = false;
    private static final Logger log   = getLoggerInstance(Logging.class); // logger for this class itself

   /**
    * The category for logging info about pages (like stop / start). Also if pages take the
     * initiative for logging themselves they should log below this category.
     * @since MMBase-1.7
     */
    public final static String PAGE_CATEGORY = "org.mmbase.PAGE";

    private static ResourceLoader resourceLoader;

    /**
     * @since MMBase-1.8
     */
    private static ResourceWatcher configWatcher;

    private static String machineName = "localhost";


    private Logging() {
        // this class has no instances.
    }


    /**
     * @since MMBase-1.8
     */
    public static String getMachineName() {
        return machineName;
    }
    /**
     * @since MMBase-1.8
     */
    public static void setMachineName(String mn) {
        machineName = mn;
    }

    /**
     * @since MMBase-1.8.5
     */
    public static Map getInitParameters() {
        try {
            Map contextMap = ApplicationContextReader.getProperties("mmbase-logging");
            return contextMap;
        } catch (javax.naming.NamingException ne) {
            log.debug("Can't obtain properties from application context: " + ne.getMessage());
            return new HashMap();
        }
    }


    /**
     * Configure the logging system.
     *
     * @param configFile Path to an xml-file in which is described
     * which class must be used for logging, and how this will be
     * configured (typically the name of another configuration file).
     *
     */

    public  static void configure (ResourceLoader rl, String configFile) {
        resourceLoader = rl;
        configWatcher = new ResourceWatcher(rl) {
            public void onChange(String s) {
                configure(resourceLoader, s);
            }
        };

        if (configFile == null) {
            log.info("No configfile given, default configuration will be used.");
            return;
        }

       // There is a problem when dtd's for the various modules are on a remote
        // machine and this machine is down. Log4j will hang without an error and if
        // SimpleImpl is used in log.xml it too will constantly try to connect to the
        // machine for the dtd's without giving an error! This line might give a hint
        // where to search for these kinds of problems..

        log.info("Configuring logging with " + configFile);
        ///System.out.println("(If logging does not start then dtd validation might be a problem on your server)");

        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();

        DocumentReader reader;
        try {
            reader = new DocumentReader(resourceLoader.getInputSource(configFile), Logging.class);
        } catch (Exception e) {
            log.error("Could not open " + configFile + " " + e, e);
            return;
        }
        if (reader == null) {
            log.error("No " + configFile);
            return;
        }

        String classToUse    = SimpleImpl.class.getName(); // default
        String configuration = "stderr,info";              // default

        Map overrides = getInitParameters();
        try { // to read the XML configuration file
            String claz = overrides.containsKey("class") ? (String) overrides.get("class") : reader.getElementValue("logging.class");
            if (claz != null) {
                classToUse = claz;
            }
            String config = overrides.containsKey("configuration") ? (String) overrides.get("configuration") : reader.getElementValue("logging.configuration");
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
            if (configured) {
                if (! logClassCopy.equals(logClass)) {
                    log.warn("Tried to change logging implementation from " + logClassCopy + " to " + logClass + ". This is not really possible (most static instances are unreachable). Trying anyway as requested, if this gives strange results, you might need to restart.");
                }
            }

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
        log.debug("Now watching " + configWatcher.getResources());
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
            conf.invoke(null, new Object[] { configuration } );
        } catch (NoSuchMethodException e) {
            log.debug("Could not find configure method in " + logClass.getName());
            // okay, simply don't configure
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error("Invocation Exception while configuration class. " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * Logging is configured with a log file. This method returns the File which was used.
     */
    public static ResourceLoader getResourceLoader() {
        return resourceLoader;
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
            Logger logger =  (Logger) getIns.invoke(null, new Object[] {s});
            if (configured) {
                return logger;
            } else {
                return new LoggerWrapper(logger, s);
            }
        } catch (Exception e) {
            log.warn(e);
            return  SimpleImpl.getLoggerInstance(s);
        }
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
            if (logClass != null) {
                Method shutdown = logClass.getMethod("shutdown", new Class[] {} );
                shutdown.invoke(null, new Object[] {} );
            }
        } catch (NoSuchMethodException e) {
            // System.err.println("No such method"); // okay, nothing to shutdown.
        } catch (Exception e) {
            System.err.println(e + stackTrace(e));
        }

    }

    /**
     * Returns the stacktrace of the current call. This can be used to get a stacktrace
     * when no exception was thrown and my help determine the root cause of an error message
     * (what class called the method that gave the error message.
     * @since MMBase-1.7
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

    /**
     * @since MMBase-1.8
     */
    public static String applicationStacktrace() {
        Exception e = new Exception("logging.showApplicationStacktrace");
        return applicationStacktrace(e);
    }

    /**
     * @since MMBase-1.8
     */
    public static String applicationStacktrace(Throwable e) {
        StringBuffer buf = new StringBuffer("Application stacktrace");

        // Get the stack trace
        StackTraceElement stackTrace[] = e.getStackTrace();
        // stackTrace[0] contains the method that created the exception.
        // stackTrace[stackTrace.length-1] contains the oldest method call.
        // Enumerate each stack element.

        boolean mmbaseClassesFound = false;
        int appended = 0;
        for (int i = 0; i < stackTrace.length; i++) {
           String className = stackTrace[i].getClassName();

           if (className.indexOf("org.mmbase") > -1) {
               mmbaseClassesFound = true;
               // show mmbase taglib
               if (className.indexOf("bridge.jsp.taglib") > -1) {
                   buf.append("\n        at ").append(stackTrace[i]);
                   appended++;
               }
           } else {
               if (mmbaseClassesFound) {
                   // show none mmbase method which invoked an mmbase method.
                   buf.append("\n        at ").append(stackTrace[i]);
                   appended++;
                   break;
               }
               // show compiled jsp lines
               if (className.indexOf("_jsp") > -1) {
                   buf.append("\n        at ").append(stackTrace[i]);
                   appended++;
               }
           }
        }
        if (appended == 0) {
            for (int i = 2; i < stackTrace.length; i++) {
                buf.append("\n        at ").append(stackTrace[i]);
            }
        }
        return buf.toString();
    }

}
