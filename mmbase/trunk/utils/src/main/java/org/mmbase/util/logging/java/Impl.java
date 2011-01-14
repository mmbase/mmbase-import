/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.java;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Level;
import org.mmbase.util.logging.Logging;


import java.io.*;

import org.mmbase.util.ResourceWatcher;
import org.mmbase.util.ResourceLoader;

/**
 * Since java 1.4 there is a Logger implemented in java itself; this MMBase Logger implementation
 * delegates all logging to this java framework. The java framework is comparable to log4j, so you could use it as an alternative.
 *
 <table>
 <tr><th>Level in MMBase's {@link org.mmbase.util.logging.Level}</th><th>Level in Java's {@link java.util.logging.Level}</th></tr>
 <tr><td>TRACE</td><td>{@link java.util.logging.Level#FINEST}</td></tr>
 <tr><td>TRACE</td><td>{@link java.util.logging.Level#FINER}</td></tr>
 <tr><td>DEBUG</td><td>{@link java.util.logging.Level#FINE}</td></tr>
 <tr><td>SERVICE</td><td>{@link java.util.logging.Level#CONFIG}</td></tr>
 <tr><td>INFO</td><td>{@link java.util.logging.Level#INFO}</td></tr>
 <tr><td>WARN</td><td>{@link java.util.logging.Level#WARNING}</td></tr>
 <tr><td>ERROR</td><td>{@link java.util.logging.Level#SEVERE}</td></tr>
 <tr><td>FATAL</td><td>{@link java.util.logging.Level#SEVERE}</td></tr>
 </table>
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @see   org.mmbase.util.logging.java.MMBaseLogger
 */


public final class Impl implements Logger {

    private static final Logger log = Logging.getLoggerInstance(Impl.class);
    private static ResourceWatcher configWatcher;

    private final java.util.logging.Logger logger;


    /**
     * Constructor. This calls java's {@link java.util.logging.Logger#getLogger(String)}.
     */

    protected Impl(String name) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    /**
     * @since MMBase-2.0
     */
    public Impl(java.util.logging.Logger l) {
        logger = l;
    }


    /**
     * Return a MMBase logger object, which wrappes a java.util.logging.Logger object.
     */
    public static Impl getLoggerInstance(String name) {
        return new Impl(name);
    }


    /**
     * Calls LogManager#readConfiguration, and feeds it with the configured InputStream. So you can
     * configure java-logging. The file is watched, so you can add and change it later.
     *
     * There need not be a configuration file for java logging.
     **/

    public static void configure(String s) {
        if ("".equals(s)) {
            System.out.println("Using default java logging configuration");
        } else {
            try {
                log.info("logging configurationfile : " + s);

                ResourceLoader rl = Logging.getResourceLoader();

                log.info("using " + rl + " for resolving " + s);
                configWatcher = new ResourceWatcher (rl) {
                    @Override
                        public void onChange(String s) {
                            try {
                                log.info("Reading configuration file : " + s);
                                java.util.logging.LogManager.getLogManager().readConfiguration(resourceLoader.getResourceAsStream(s));
                            } catch (IOException ioe) {
                                log.error(ioe);
                            }
                        }
                    };

                configWatcher.add(s);
                configWatcher.start();

                java.util.logging.LogManager.getLogManager().readConfiguration(rl.getResourceAsStream(s));
            } catch (IOException ioe) {
                log.error(ioe);
            }
        }
    }

    protected java.util.logging.Level getJavaLevel(Level p) {
        switch (p.toInt()) {
        case Level.TRACE_INT:   return java.util.logging.Level.FINER;
        case Level.DEBUG_INT:   return java.util.logging.Level.FINE;
        case Level.SERVICE_INT: return java.util.logging.Level.CONFIG;
        case Level.INFO_INT:   return java.util.logging.Level.INFO;
        case Level.WARN_INT:   return java.util.logging.Level.WARNING;
        case Level.ERROR_INT:   return java.util.logging.Level.SEVERE;
        case Level.FATAL_INT:   return java.util.logging.Level.SEVERE;
        default: return java.util.logging.Level.FINE;
        }
    }

    @Override
    public void setLevel(Level p) {
        logger.setLevel(getJavaLevel(p));
    }


    @Override
    public void trace (Object m) {
        logger.log(java.util.logging.Level.FINER, "{0}", m);
    }
    @Override
    public void trace (Object m, Throwable t) {
        logger.log(java.util.logging.Level.FINER, "" + m, t);
    }
    @Override
    public void debug (Object m) {
        logger.log(java.util.logging.Level.FINE, "{0}", m);
    }
    @Override
    public void debug (Object m, Throwable t) {
        logger.log(java.util.logging.Level.FINE, "" + m, t);
    }

    @Override
    public void service (Object m) {
        logger.log(java.util.logging.Level.CONFIG, "{0}", m);
    }
    @Override
    public void service (Object m, Throwable t) {
        logger.log(java.util.logging.Level.CONFIG, "" + m, t);
    }
    @Override
    public void info    (Object m) {
        logger.log(java.util.logging.Level.INFO, "{0}", m);
    }
    @Override
    public void info    (Object m, Throwable t) {
        logger.log(java.util.logging.Level.INFO, "" + m, t);
    }
    @Override
    public void warn    (Object m) {
        logger.log(java.util.logging.Level.WARNING, "{0}", m);
    }
    @Override
    public void warn    (Object m, Throwable t) {
        logger.log(java.util.logging.Level.WARNING, "" + m, t);
    }
    @Override
    public void error   (Object m) {
        logger.log(java.util.logging.Level.SEVERE, "{0}", m);
    }
    @Override
    public void error   (Object m, Throwable t) {
        logger.log(java.util.logging.Level.SEVERE, "" + m, t);
    }
    @Override
    public void fatal   (Object m) {
        logger.log(java.util.logging.Level.SEVERE, "{0}", m);
    }
    @Override
    public void fatal   (Object m, Throwable t) {
        logger.log(java.util.logging.Level.SEVERE, "" + m, t);
    }

    private java.util.logging.Level getLevel() {
        java.util.logging.Level level = null;
        java.util.logging.Logger l = logger;
        while (level == null) {
            level = l.getLevel();
            l = l.getParent();
        }
        return level;
    }

    @Override
    public boolean isTraceEnabled() {
        return (getLevel().intValue() <= java.util.logging.Level.FINER.intValue());
    }

    @Override
    public boolean isDebugEnabled() {
        return (getLevel().intValue() <= java.util.logging.Level.FINE.intValue());
    }

    @Override
    public boolean isServiceEnabled() {
        return (getLevel().intValue() <= java.util.logging.Level.CONFIG.intValue());
    }

    @Override
    public boolean isEnabledFor(Level l) {
        return (getLevel().intValue() <= getJavaLevel(l).intValue());
    }

}

