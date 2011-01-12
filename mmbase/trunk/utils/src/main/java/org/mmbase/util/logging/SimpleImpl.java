/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simple implementation of Logger. It writes everything to
 * standard output or standard error (the configure string can contain
 * `stderr' or `stdout' (default)).  It does not know categories (and
 * therefore is a Singleton class). It is possible to configure what
 * should be logged as well (with a level-string token in the
 * configure string).
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.4
 */

public class SimpleImpl extends AbstractSimpleImpl implements Logger {

    private static SimpleImpl root = new SimpleImpl("");
    private static PrintStream ps = System.out;

    private static Map<String, SimpleImpl> loggers  = new ConcurrentHashMap<String, SimpleImpl>();


    private final String name;

    public static  SimpleImpl getLoggerInstance(String name) {
        SimpleImpl impl = loggers.get(name);
        if (impl == null) {
            impl = new SimpleImpl(name);
            impl.level = root.level;
            loggers.put(name, impl);
        }
        return impl;
    }

    private SimpleImpl(String n) {
        name = n;
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
            if ("stderr".equals(token)) {
                ps = System.err;
            }
            if ("stdout".equals(token)) {
                ps = System.out;
            } else if ("trace".equals(token)) {
                root.setLevel(Level.TRACE);
            } else if ("debug".equals(token)) {
                root.setLevel(Level.DEBUG);
            } else if ("service".equals(token)) {
                root.setLevel(Level.SERVICE);
            } else if ("info".equals(token)) {
                root.setLevel(Level.INFO);
            } else if ("warn".equals(token)) {
                root.setLevel(Level.WARN);
            } else if ("error".equals(token)) {
                root.setLevel(Level.ERROR);
            } else if ("fatal".equals(token)) {
                root.setLevel(Level.FATAL);
            }
        }
    }

    @Override
    protected final void log(String s) {
        ps.println(name + ":" + s);
    }

}
