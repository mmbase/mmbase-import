/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * Base class for simple Logger implementations (no patterns and so
 * on).
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 */

abstract public class AbstractSimpleImpl  implements Logger {

    /**
     * @since MMBase-1.8
     */
    protected int level = Level.INFO_INT;

    public void setLevel(Level p) {
        level = p.toInt();
    }

    // override one of these two
    /**
     * How to write one string. Only to be called by {@link #log(String, Level)}. Default this does
     * nothing, you must override this, or {@link #log(String, Level)}.
     */
    protected void log (String s) {
    }

    /**
     * Logs a message for a certain string. Default calls {@link #log(String)} where the string is
     * prefixed with the level. Override this one if you want it differently.
     */
    protected void log(String s, Level level) {
        log(level.toString() + " " + s);
    }


    /**
     * Override to implement different stringification of objects to log. (default "" + s)
     * @since MMBase-1.8
     */
    protected void log(Object s, Level level) {
        log("" + s, level);
    }

    /**
     * @since MMBase-1.8
     */
    protected void log(Object s, Level level, Throwable t) {
        log(s + "\n" + Logging.stackTrace(t), level);
    }

    public void trace (Object m) {
        if (level <= Level.TRACE_INT) {
            log(m,  Level.TRACE);
        }
    }

    public void trace (Object m, Throwable t) {
        if (level <= Level.TRACE_INT) {
            log(m,  Level.TRACE, t);
        }
    }

    public void debug (Object m) {
        if (level <= Level.DEBUG_INT) {
            log(m, Level.DEBUG);
        }
    }
    public void debug (Object m, Throwable t) {
        if (level <= Level.DEBUG_INT) {
            log(m, Level.DEBUG, t);
        }
    }

    public void service (Object m) {
        if (level <= Level.SERVICE_INT) {
            log(m, Level.SERVICE);
        }
    }

    public void service (Object m, Throwable t) {
        if (level <= Level.SERVICE_INT) {
            log(m, Level.SERVICE, t);
        }
    }

    public void info    (Object m) {
        if (level <= Level.INFO_INT) {
            log(m, Level.INFO);
        }
    }

    public void info    (Object m, Throwable t) {
        if (level <= Level.INFO_INT) {
            log(m, Level.INFO, t);
        }
    }

    public void warn    (Object m) {
        if (level <= Level.WARN_INT) {
            log(m, Level.WARN);
        }
    }

    public void warn    (Object m, Throwable t) {
        if (level <= Level.WARN_INT) {
            log(m, Level.WARN, t);
        }
    }

    public void error   (Object m) {
        if (level <= Level.ERROR_INT) {
            log(m, Level.ERROR);
        }
    }

    public void error   (Object m, Throwable t) {
        if (level <= Level.ERROR_INT) {
            log(m, Level.ERROR, t);
        }
    }

    public void fatal   (Object m) {
        if (level <= Level.FATAL_INT) {
            log(m, Level.FATAL);
        }
    }

    public void fatal   (Object m, Throwable t) {
        if (level <= Level.FATAL_INT) {
            log(m, Level.FATAL, t);
        }
    }

    public boolean isTraceEnabled() {
        return level <= Level.TRACE_INT;
    }

    public boolean isDebugEnabled() {
        return level <= Level.DEBUG_INT;
    }

    public boolean isServiceEnabled() {
        return level <= Level.SERVICE_INT;
    }

}
