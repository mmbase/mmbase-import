/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.util.*;

/**
 * Base class for simple Logger implementations (no patterns and so
 * on).
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 */

abstract public class AbstractSimpleImpl  implements Logger {

    private static ThreadLocal<Map<String, Object>> MDC_VALUES = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            Map<String, Object> o = new HashMap<String, Object>();
            return o;
        }

    };

    /**
     * @since MMBase-2.0
     */
    public static MDC getMDC() {
        return new MDC() {
            @Override
            public void put(String key, Object value) {
                if (value != null) {
                    MDC_VALUES.get().put(key, value);
                } else {
                    MDC_VALUES.get().remove(key);
                }
            }

            @Override
            public Object get(String key) {
                return MDC_VALUES.get().get(key);
            }
        };
    }

    /**
     * @since MMBase-1.8
     */
    protected int level = Level.INFO_INT;

    @Override
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

    @Override
    public void trace (Object m) {
        if (level <= Level.TRACE_INT) {
            log(m,  Level.TRACE);
        }
    }

    @Override
    public void trace (Object m, Throwable t) {
        if (level <= Level.TRACE_INT) {
            log(m,  Level.TRACE, t);
        }
    }

    @Override
    public void debug (Object m) {
        if (level <= Level.DEBUG_INT) {
            log(m, Level.DEBUG);
        }
    }
    @Override
    public void debug (Object m, Throwable t) {
        if (level <= Level.DEBUG_INT) {
            log(m, Level.DEBUG, t);
        }
    }

    @Override
    public void service (Object m) {
        if (level <= Level.SERVICE_INT) {
            log(m, Level.SERVICE);
        }
    }

    @Override
    public void service (Object m, Throwable t) {
        if (level <= Level.SERVICE_INT) {
            log(m, Level.SERVICE, t);
        }
    }

    @Override
    public void info    (Object m) {
        if (level <= Level.INFO_INT) {
            log(m, Level.INFO);
        }
    }

    @Override
    public void info    (Object m, Throwable t) {
        if (level <= Level.INFO_INT) {
            log(m, Level.INFO, t);
        }
    }

    @Override
    public void warn    (Object m) {
        if (level <= Level.WARN_INT) {
            log(m, Level.WARN);
        }
    }

    @Override
    public void warn    (Object m, Throwable t) {
        if (level <= Level.WARN_INT) {
            log(m, Level.WARN, t);
        }
    }

    @Override
    public void error   (Object m) {
        if (level <= Level.ERROR_INT) {
            log(m, Level.ERROR);
        }
    }

    @Override
    public void error   (Object m, Throwable t) {
        if (level <= Level.ERROR_INT) {
            log(m, Level.ERROR, t);
        }
    }

    @Override
    public void fatal   (Object m) {
        if (level <= Level.FATAL_INT) {
            log(m, Level.FATAL);
        }
    }

    @Override
    public void fatal   (Object m, Throwable t) {
        if (level <= Level.FATAL_INT) {
            log(m, Level.FATAL, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return level <= Level.TRACE_INT;
    }

    @Override
    public boolean isDebugEnabled() {
        return level <= Level.DEBUG_INT;
    }

    @Override
    public boolean isServiceEnabled() {
        return level <= Level.SERVICE_INT;
    }

    @Override
    public boolean isEnabledFor(Level l) {
        return level <= l.toInt();
    }

}
