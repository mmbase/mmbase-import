/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * A very simple implementation of Logger. It ignores everything below
 * warn (or what else if configured), and throws an exception for
 * everything higher. In junit tests this generates test-case failures
 * if a warn or error is issued (we don't want that in normal
 * situations).
 *
 * Logging can be set up like this in the setup of your test:
   <pre>
  Logging.configure(System.getProperty("mmbase.config") + File.separator + "log" + File.separator + "log.xml");
   </pre>
 *
 * @author Michiel Meeuwissen 
 * @since  MMBase-1.7
 */

public class ExceptionImpl extends AbstractSimpleImpl implements Logger {

    private static ExceptionImpl root = new ExceptionImpl();

    private ExceptionImpl() {
        // a Singleton class.
    }

    public static  ExceptionImpl getLoggerInstance(String name) {
        return root;
    }

    /**
     * The configure method of this Logger implemenation.
     *
     * @param A string, which can contain the output (stdout or
     * stderr) and the priority (e.g. 'info')
     */
   
    public static  void configure(String c) {
        if (c == null || c.equals("")) {
            return; // everything default
        } else {
            level = Level.toLevel(c).toInt();
        }
    }

    public final void log (String s) {
        throw new LoggingException(s);
    }


}
