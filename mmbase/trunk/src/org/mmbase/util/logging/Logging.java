/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;
import java.lang.reflect.Method;
import java.io.File;

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

        System.out.println("Configuring logging with " + configfile);

        configurationFile = new File(configfile);
        configurationFile = configurationFile.getAbsoluteFile();
        
        if (! configurationFile.exists() || 
            ! configurationFile.isFile() ||
            ! configurationFile.canRead() ) { // not a readable file, return and warn that logging cannot be configured.
            System.out.println("Log configuration file is not accessible, default logging implementation will be used.");
            return;
        }
   
        configfile = configurationFile.getAbsolutePath();
        //configfile = configfile.replace('/',(System.getProperty("file.separator")).charAt(0));
        //configfile = configfile.replace('\\',(System.getProperty("file.separator")).charAt(0));   
        

        // configfile is in XML, which we are going to parse with Xerces, 
        // but with reflection, to makes us independent of Xerces.
        Class domParserClass = null;
        Class documentClass  = null;
        Class nodeClass      = null;
        
        try { 
            domParserClass = Class.forName("org.apache.xerces.parsers.DOMParser");
            documentClass  = Class.forName("org.w3c.dom.Document");
            nodeClass      = Class.forName("org.w3c.dom.Node");
        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
            System.err.println("Could not find xerces classes, logging cannot be configured, using defaults.");            
            return;
        } 

        String classToUse    = "org.mmbase.util.logging.SimpleImpl"; // default
        String configuration = "stderr,info";                        // default
        try { // to read the XML configuration file
            
            Object parser = domParserClass.newInstance();
            Method setFeature =  domParserClass.getMethod("setFeature",  new Class [] { String.class, Boolean.TYPE});
            Method parse       = domParserClass.getMethod("parse",       new Class [] { String.class });
            Method getDocument = domParserClass.getMethod("getDocument", new Class [] {});
            
            setFeature.invoke(parser, new Object[] { "http://apache.org/xml/features/dom/defer-node-expansion",        new Boolean(true)});
            // setFeature.invoke(parser, new Object[] { "http://apache.org/xml/features/continue-after-fatal-error",  new Boolean(true)});
            
            configfile="file://" + configfile;
            
            // System.out.println("configfile:" + configfile);
            parse.invoke(parser, new Object[] {configfile});            
            
            Object [] no = new Object[] {}; // shorthand...
            Object document = getDocument.invoke(parser, no);
            Method getFirstChild = nodeClass.getMethod("getFirstChild", new Class [] {});
            Method getNodeName   = nodeClass.getMethod("getNodeName",   new Class [] {});
            Method getNextSibling= nodeClass.getMethod("getNextSibling",new Class [] {});
            Method getNodeValue  = nodeClass.getMethod("getNodeValue",  new Class [] {});
                                   
            Object n1 = getFirstChild.invoke(document, new Object[] {});
            
            while (n1 != null) {              
                if (getNodeName.invoke(n1, no).equals("logging")) {
                    Object n2 = getFirstChild.invoke(n1, no);
                    while (n2 != null) {
                        if (getNodeName.invoke(n2, no).equals("class")) {       
                            classToUse = (String)getNodeValue.invoke(getFirstChild.invoke(n2, no), no);
                            //System.err.println("found class" + classToUse);
                        }
                        if (getNodeName.invoke(n2, no).equals("configuration")) {
                            configuration = (String)getNodeValue.invoke(getFirstChild.invoke(n2, no), no);
                            // System.err.println("found conf" + configuration);
                        }
                        n2 = getNextSibling.invoke(n2, no);
                        
                    }
                } 
                n1 = getNextSibling.invoke(n1, no);
            }
        } catch (Exception e) {
            System.err.println("Exception during parsing: " + e);
            e.printStackTrace(System.err);
        }

       
        System.out.println("Class to use for logging " + classToUse);
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
        } 
                 
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
            // System.out.println("Found class " + logclass.getName());
            Method conf = logClass.getMethod("configure", new Class[] { String.class } ); 
            conf.invoke(null, new String[] { configuration } );    
        } catch (NoSuchMethodException e) {
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
