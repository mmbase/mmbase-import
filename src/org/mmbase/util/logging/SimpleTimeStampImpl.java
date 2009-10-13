/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.PrintStream;
import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Like SimpleImpl, but also adds timestamps.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.7
 */

public class SimpleTimeStampImpl extends AbstractSimpleImpl implements Logger {

    private static SimpleTimeStampImpl root = new SimpleTimeStampImpl();
    private static PrintStream ps = System.out;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");


    private SimpleTimeStampImpl() {
        // a Singleton class.
    }

    public static  SimpleTimeStampImpl getLoggerInstance(String name) {
        return root;
    }

    /**
     * The configure method of this Logger implemenation.
     *
     * @param c A string, which can contain the output (stdout or
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
                root.setLevel(Level.TRACE);
            }
            if (token.equals("debug")) {
                root.setLevel(Level.DEBUG);
            }
            if (token.equals("service")) {
                root.setLevel(Level.SERVICE);
            }
            if (token.equals("info")) {
                root.setLevel(Level.INFO);
            }
            if (token.equals("warn")) {
                root.setLevel(Level.WARN);
            }
            if (token.equals("error")) {
                root.setLevel(Level.ERROR);
            }
            if (token.equals("fatal")) {
                root.setLevel(Level.FATAL);
            }
        }
    }

    protected final void log (String s) {
        ps.println(dateFormat.format(new java.util.Date()) + s);
    }

}
