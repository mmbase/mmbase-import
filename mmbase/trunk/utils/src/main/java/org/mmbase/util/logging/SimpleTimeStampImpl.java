/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    private static SimpleTimeStampImpl root = new SimpleTimeStampImpl("");
    private static Map<Level, PrintStream> ps = new HashMap<Level, PrintStream>();

    static {
        for (Level l : Level.getLevels()) {
            if (l.toInt() >= Level.WARN.toInt()) {
                ps.put(l, System.err);
            } else {
                ps.put(l, System.out);
            }
        }
    }

    private static int stacktraceLevel  = Level.FATAL_INT;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");
    private static Map<String, SimpleTimeStampImpl> loggers  = new ConcurrentHashMap<String, SimpleTimeStampImpl>();

    private final String name;

    public static  SimpleTimeStampImpl getLoggerInstance(String name) {
        SimpleTimeStampImpl impl = loggers.get(name);
        if (impl == null) {
            impl = new SimpleTimeStampImpl(name);
            impl.level = root.level;
            loggers.put(name, impl);
        }
        return impl;
    }

    private SimpleTimeStampImpl(String n) {
        name = n;
    }


    protected PrintStream getStream(Level l) {
        return ps.get(l);
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
                for (Level l : Level.getLevels()) {
                    ps.put(l, System.err);
                }
            }
            if (token.equals("stdout")) {
                for (Level l : Level.getLevels()) {
                    ps.put(l, System.out);
                }
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

    @Override
    protected final void log (String s, Level l) {
        PrintStream stream = getStream(l);
        stream.println(l.toString() + " " + dateFormat.format(new java.util.Date()) + s);
        if (l.toInt() >= stacktraceLevel) {
            Throwable t = new Throwable();
            stream.println(Logging.stackTrace(t));
        }
    }

}
