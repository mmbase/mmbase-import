/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * A very simple implementation of Logger. It writes everything to
 * standard output or standard error (the configure string can contain
 * `stderr' or `stdout' (default)).  It does not know categories (and
 * therefore is a Singleton class). It is possible to configure what
 * should be logged as well (with a level-string token in the
 * configure string).
 *
 * @author  Michiel Meeuwissen
 * @version $Id: SimpleImpl.java,v 1.10 2003-03-12 20:02:03 michiel Exp $
 * @since   MMBase-1.4
 */

public class SimpleImpl extends AbstractSimpleImpl implements Logger {

    private static SimpleImpl root = new SimpleImpl();
    private static PrintStream ps = System.out;

    private SimpleImpl() {
        // a Singleton class.
    }

    public static  SimpleImpl getLoggerInstance(String name) {
        return root;
    }

    /**
     * The configure method of this Logger implemenation.
     *
     * @param A string, which can contain the output (stdout or
     * stderr) and the priority (e.g. 'info')
     */
   
    public static  void configure(String c) {

        if (c == null) {
            return; // everything default
        }
        
        StringTokenizer t    = new StringTokenizer(c, ","); 
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            if (token.equals("stderr")) {
                ps = System.err;             
            }
            if (token.equals("stdout")) {
                ps = System.out;             
            }
            if (token.equals("trace")) {
                level = Level.TRACE_INT;
            }
            if (token.equals("debug")) {
                level = Level.DEBUG_INT;
            }
            if (token.equals("service")) {
                level = Level.SERVICE_INT;
            }
            if (token.equals("info")) {
                level = Level.INFO_INT;
            }
            if (token.equals("warn")) {
                level = Level.WARN_INT;
            }
            if (token.equals("error")) {
                level = Level.ERROR_INT;
            }
            if (token.equals("fatal")) {
                level = Level.FATAL_INT;
            }
        }
    }

    protected final void log (String s) {
        ps.println(s);
    }

}
