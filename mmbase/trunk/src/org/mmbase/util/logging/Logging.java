/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

    private static Class   logclass   = SimpleImpl.class; // default Logger Implementation
    private static String  configuration = null;      // stores configuration string (a filename, 'stdout' or 'stderr' for SimpleImpl.)

    private static File    configuration_file = null;     // Loging is configured with a configuration file. The path of this file can be requested later.

    private Logging() {
        // this class has no instances.
    }

    /**
     * Configure the logging system.
     *
     * @param configfile Path to an xml-file in which is described which class must be used for logging, and how this will be configured (typically the name of another configuration file).
     */

    public  static void configure (String configfile) {

  
        if (configfile != null) {
            System.out.println("Configuring logging with " + configfile);

            configuration_file = new File(configfile);
            configuration_file = configuration_file.getAbsoluteFile();

            if (! configuration_file.exists() || 
                ! configuration_file.isFile() ||
                ! configuration_file.canRead() ) { // not a readable file, return and warn that logging cannot be configured.
                System.out.println("Log configuration file is not accessible, default logging implementation will be used.");
                return;
            }
   
            configfile = configuration_file.getAbsolutePath();
            //configfile = configfile.replace('/',(System.getProperty("file.separator")).charAt(0));
            //configfile = configfile.replace('\\',(System.getProperty("file.separator")).charAt(0));   

            try {
                DOMParser parser = new DOMParser();
      
                parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion",   true);
                parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
                configfile="file://" + configfile;

                // System.out.println("configfile:" + configfile);
                parser.parse(configfile);
                Document document = parser.getDocument();

                String classtouse = "org.mmbase.util.logging.SimpleImpl";
          
                Node n1=document.getFirstChild();

                while (n1 != null) {

                    if (n1.getNodeName().equals("logging")) {
                        Node n2=n1.getFirstChild();
                        while (n2 != null) {
                            if (n2.getNodeName().equals("class")) {       
                                classtouse=n2.getFirstChild().getNodeValue();
                                //System.err.println("found class" + classtouse);
                            }
                            if (n2.getNodeName().equals("configuration")) {
                                configuration=n2.getFirstChild().getNodeValue();
                                // System.err.println("found conf" + configuration);
                            }
                            n2=n2.getNextSibling();
       
                        }
                    } 
                    n1=n1.getNextSibling();
                }    
                System.out.println("Class to use for logging " + classtouse);
                Class classname = logclass;
                try {
                    //System.out.println("classloader1: " + ClassLoader.getSystemClassLoader().getClass().getName());
                    //System.out.println("classloader2: " + Logging.class.getClassLoader().getClass().getName());

                    //logclass = Logging.class.getClassLoader().loadClass(classtouse);
                    //logclass = Class.forName(classtouse, true, ClassLoader.getSystemClassLoader());  
                    //logclass = Class.forName(classtouse);
                    logclass = Thread.currentThread().getContextClassLoader().loadClass(classtouse);

                    // It's a little tricky to find the right classloader, but as it is now, it works for me.

                } catch (ClassNotFoundException e) {
                    System.err.println("Could not find class " + classtouse);
                    System.err.println(e.toString());
                    logclass = classname;    
                } 
            } catch (Exception e) {
                System.err.println("Exception during parsing: " + e);
            }
   
            try {
                // System.out.println("Found class " + logclass.getName());
                java.lang.reflect.Method conf = 
                    logclass.getMethod("configure", new Class[] { String.class } ); 
                conf.invoke(logclass, new String[] { configuration } );    
            } catch (NoSuchMethodException e) {
                // okay, simply don't configure
            } catch (java.lang.reflect.InvocationTargetException e) {
                System.err.println("!!! Invocation Exception !!! " + e.getMessage());
                e.printStackTrace(System.err);
            } catch (Exception e) {
                System.err.println(e);
            } 

        }
    }

    /**
     * Logging is configured with a log file. This method returns the File which was used.
     */
    public static File getConfigurationFile() {
        return configuration_file;
    }
    /**
     * After configuring the logging system, you can get Logger instances to log with. 
     *
     * @param s A string describing the `category' of the Logger. This is a log4j concept.
     */

    public  static Logger getLoggerInstance (String s) { 
  
        // call the getLoggerInstance static method of the logclass:
        try {
            java.lang.reflect.Method getIns = logclass.getMethod("getLoggerInstance", new Class[] { String.class } );
            return  (Logger) getIns.invoke(logclass, new String[] {s}); 
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
            java.lang.reflect.Method shutdown = 
                logclass.getMethod("shutdown", new Class[] {} ); 
            shutdown.invoke(logclass, new String[] {} );    
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
     * @param e the Throwable from which the stack trace must be stringified.
     *
     **/
    public static String stackTrace(Throwable e) {
        java.io.ByteArrayOutputStream stream =  new java.io.ByteArrayOutputStream();
        e.printStackTrace(new java.io.PrintStream(stream));
        return stream.toString();
    } 

}
