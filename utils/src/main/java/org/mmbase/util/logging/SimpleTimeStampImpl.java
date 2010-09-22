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
import java.util.concurrent.CopyOnWriteArrayList;
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


    private  Map<Level, PrintStream> ps = new HashMap<Level, PrintStream>();

    {
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
    private static SimpleTimeStampImpl root = new SimpleTimeStampImpl("");
    static {
        loggers.put("", root);
    }
    private static List<Map.Entry<String, String>> configurations = new CopyOnWriteArrayList<Map.Entry<String, String>>();

    private final String name;

    public static  SimpleTimeStampImpl getLoggerInstance(String name) {
        SimpleTimeStampImpl impl = loggers.get(name);
        if (impl == null) {
            impl = new SimpleTimeStampImpl(name);
            impl.level = root.level;
            loggers.put(name, impl);
            for (Map.Entry<String, String> conf : configurations) {
                if (name.startsWith(conf.getKey())) {
                    impl.conf(conf.getValue());
                }
            }
        }
        return impl;
    }

    private SimpleTimeStampImpl(String n) {
        name = n;
    }


    protected PrintStream getStream(Level l) {
        return ps.get(l);
    }


    public static  void configure(String c) {
        for (String line : c.trim().split("\\s+")) {
            if (line.startsWith("#")) continue;
            String[] e = line.trim().split(":", 2);
            if (e.length == 2) {
                configure(e[0].trim(), e[1].trim());
            } else {
                configure("", e[0].trim());
            }
        }
    }

    private void conf(String c) {
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
                setLevel(Level.TRACE);
            }
            if (token.equals("debug")) {
                setLevel(Level.DEBUG);
            }
            if (token.equals("service")) {
                setLevel(Level.SERVICE);
            }
            if (token.equals("info")) {
                setLevel(Level.INFO);
            }
            if (token.equals("warn")) {
                setLevel(Level.WARN);
            }
            if (token.equals("error")) {
                setLevel(Level.ERROR);
            }
            if (token.equals("fatal")) {
                setLevel(Level.FATAL);
            }
        }
    }

    /**
     * The configure method of this Logger implemenation.
     *
     * @param c A string, which can contain the output (stdout or
     * stderr) and the priority (e.g. 'info')
     */

    public static  void configure(String prefix, String c) {

        if (c == null) {
            return; // everything default
        }
        configurations.add(new AbstractMap.SimpleEntry<String, String>(prefix,  c));

        for (Map.Entry<String, SimpleTimeStampImpl> entry : loggers.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                SimpleTimeStampImpl logger = entry.getValue();
                logger.conf(c);
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

    @Override
    public String toString() {
        return name + ":" + level;
    }
}
