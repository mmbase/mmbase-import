/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;
import java.lang.reflect.Method;
import java.io.*;
import java.util.Set;
import java.util.HashSet;
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
 */


public class Logging {

    private static Class  logClass          = SimpleImpl.class; // default Logger Implementation
    private static File   configurationFile = null;             // Logging is configured with a configuration file. The path of this file can be requested later.

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
            System.out.println("No configfile given, default configuration will be used.");
            return;
        }  

       // There is a problem when dtd's for the various modules are on a remote
        // machine and this machine is down. Log4j will hang without an error and if
        // SimpleImpl is used in log.xml it too will constantly try to connect to the
        // machine for the dtd's without giving an error! This line might give a hint 
        // where to search for these kinds of problems..
        
        System.out.println("Configuring logging with " + configfile);
        System.out.println("(If logging does not start then dtd validation might be a problem on your server)");



        configurationFile = new File(configfile);
        configurationFile = configurationFile.getAbsoluteFile();
        
        if (! configurationFile.exists() || 
            ! configurationFile.isFile() ||
            ! configurationFile.canRead() ) { // not a readable file, return and warn that logging cannot be configured.
            System.out.println("Log configuration file is not accessible, default logging implementation will be used.");
            return;
        }
   
        // Convert the file to a system-dependant URL string for the parser to use
        try {
            URL logURL = configurationFile.toURL();
            configfile = logURL.toString();
        }
        catch (Exception e)
        {
            System.out.println("Cannot get URL from file " + 
                               configfile + 
                               " : " +
                               e.toString());
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
            System.err.println("Exception during parsing: " + e);
            e.printStackTrace(System.err);
        }

       
        System.out.println("Class to use for logging " + classToUse);
        System.out.println("(Depending on your selected logging system no more logging");
        System.out.println("might be written to this file. See the configuration of the");
        System.out.println("selected logging system for more hints where logging will appear)");
        Class logClassCopy = logClass; // if something's wrong, we can restore the current value.
        try { // to find the configured class
            //System.out.println("classloader1: " + ClassLoader.getSystemClassLoader().getClass().getName());
            //System.out.println("classloader2: " + Logging.class.getClassLoader().getClass().getName());            
            //logclass = Logging.class.getClassLoader().loadClass(classToUse);
            //logclass = Class.forName(classToUse, true, ClassLoader.getSystemClassLoader());  
            logClass = Class.forName(classToUse);
            // logclass = Thread.currentThread().getContextClassLoader().loadClass(classToUse);                
            // It's a little tricky to find the right classloader, but as it is now, it works for me.
            
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class " + classToUse);
            System.err.println(e.toString());
            logClass = logClassCopy;
        } catch (Throwable e) {
            System.err.println("Exception to find class " + classToUse + ": " +  e);
            logClass = logClassCopy;
        }
        // System.out.println("logging to " + getLocations());
        configureClass(configuration);
    }

    /** 
     * Calls the 'configure' static method of the used logging class,
     * or does nothing if it doesn't exist. You could call this method
     * if you want to avoid using 'configure', which parses an XML file.
     *
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
            return  (Logger) getIns.invoke(null, new String[] {s}); 
        } catch (Exception e) {
            System.err.println(e);
        }

        return null; // should not come here.

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
            Method shutdown = 
                logClass.getMethod("shutdown", new Class[] {} ); 
            shutdown.invoke(null, new String[] {} );    
        } catch (NoSuchMethodException e) {
            // System.err.println("No such method"); // okay, nothing to shutdown.
        } catch (Exception e) {
            System.err.println(e);
        }

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
        java.io.ByteArrayOutputStream stream =  new java.io.ByteArrayOutputStream();
        e.printStackTrace(new java.io.PrintStream(stream));
        return stream.toString();
    } 

}
